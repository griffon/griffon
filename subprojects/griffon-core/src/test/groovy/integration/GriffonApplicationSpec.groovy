/*
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package integration

import griffon.core.ApplicationBootstrapper
import griffon.core.GriffonApplication
import griffon.core.LifecycleHandler
import griffon.core.ShutdownHandler
import griffon.core.addon.GriffonAddon
import griffon.core.artifact.*
import griffon.core.controller.ActionInterceptor
import griffon.core.env.ApplicationPhase
import griffon.core.env.Lifecycle
import griffon.core.mvc.MVCCallable
import griffon.core.mvc.MVCGroup
import org.codehaus.griffon.runtime.core.DefaultApplicationBootstrapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.annotation.Nonnull
import javax.annotation.Nullable

import static griffon.util.AnnotationUtils.named
import static java.util.Arrays.asList

@Stepwise
class GriffonApplicationSpec extends Specification {
    @Shared
    private static GriffonApplication application

    @Shared
    private static List<Invokable> invokables = []

    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    def setupSpec() {
        application = new TestGriffonApplication(['foo', 'bar'] as String[])
        application.addShutdownHandler(new TestShutdownHandler())
    }

    def cleanupSpec() {
        //when:
        assert application.shutdown()

        // then:
        assert ApplicationPhase.SHUTDOWN == application.phase

        for (Invokable invokable : invokables) {
            assert invokable.invoked
        }
    }

    def "Bootstrap application"() {
        given:
        ApplicationBootstrapper bootstrapper = new DefaultApplicationBootstrapper(application)

        when:
        bootstrapper.bootstrap()
        List<Invokable> invokables = new ArrayList<>()
        for (Lifecycle lifecycle : asList(Lifecycle.INITIALIZE, Lifecycle.STARTUP, Lifecycle.READY, Lifecycle.SHUTDOWN)) {
            invokables << application.injector.getInstance(LifecycleHandler, named(lifecycle.name))
        }
        for (GriffonAddon addon : application.injector.getInstances(GriffonAddon)) {
            invokables << addon
        }
        bootstrapper.run()

        then:
        ApplicationPhase.MAIN == application.phase
    }

    def 'Check artifact controller'() {
        given:
        GriffonControllerClass controllerClass = application.artifactManager.findGriffonClass(IntegrationController)
        controllerClass.resetCaches()

        expect:
        controllerClass.eventNames == []
        controllerClass.actionNames == ['handleException', 'sayHello', 'throwException']
    }

    def 'Check artifact model'() {
        given:
        GriffonModelClass modelClass = application.artifactManager.findGriffonClass(IntegrationModel)
        GriffonModel model = application.mvcGroupManager.findGroup('integration').model
        modelClass.resetCaches()

        when:
        modelClass.setModelPropertyValue(model, 'input', 'INPUT')

        then:
        modelClass.eventNames == []
        modelClass.propertyNames == ['input', 'output']
        modelClass.getModelPropertyValue(model, 'input') == 'INPUT'
    }

    def 'Check artifact service'() {
        given:
        GriffonServiceClass serviceClass = application.artifactManager.findGriffonClass(IntegrationService)
        serviceClass.resetCaches()

        expect:
        serviceClass.eventNames == []
        serviceClass.serviceNames == ['sayHello']
    }

    def 'Check artifact view'() {
        given:
        GriffonViewClass viewClass = application.artifactManager.findGriffonClass(IntegrationView)
        viewClass.resetCaches()

        expect:
        viewClass.eventNames == ['RandomEvent']
    }

    def "Check groups"() {
        // given:
        InvokeActionInterceptor interceptor = application.injector.getInstance(ActionInterceptor)
        assert interceptor.configure
        assert ['foo', 'bar'] == application.startupArgs

        when:
        MVCGroup group = application.mvcGroupManager.findGroup('integration')

        then:
        group
        !interceptor.before
        !interceptor.after
        !interceptor.exception
    }

    def 'Invoke sayHello Action'() {
        given:
        InvokeActionInterceptor interceptor = application.injector.getInstance(ActionInterceptor)
        MVCGroup group = application.mvcGroupManager.findGroup('integration')
        invokables << group.view

        when:
        group.controller.invokeAction('sayHello')

        then:
        interceptor.before
        interceptor.after
        !interceptor.exception
    }

    def 'Invoke handleException Action'() {
        given:
        InvokeActionInterceptor interceptor = application.injector.getInstance(ActionInterceptor)
        MVCGroup group = application.mvcGroupManager.findGroup('integration')

        when:
        group.controller.invokeAction('handleException')

        then:
        interceptor.before
        interceptor.after
        interceptor.exception
    }

    def 'Invoke throwException Action'() {
        given:
        InvokeActionInterceptor interceptor = application.injector.getInstance(ActionInterceptor)
        MVCGroup group = application.mvcGroupManager.findGroup('integration')

        when:
        group.controller.invokeAction('throwException')

        then:
        thrown(RuntimeException)
        interceptor.before
        interceptor.after
        interceptor.exception
    }

    def 'Verify ActionManager'() {
        given:
        GriffonController controller = application.mvcGroupManager.findGroup('integration').controller

        expect:
        application.actionManager.actionsFor(controller).keySet() == (['handleException', 'sayHello', 'throwException'] as Set)
        application.actionManager.normalizeName('fooAction') == 'foo'
        application.actionManager.normalizeName('foo') == 'foo'
        application.actionManager.actionFor(controller, 'sayHello')
        !application.actionManager.actionFor(controller, 'unknown')
    }

    def 'Verify AddonManager'() {
        expect:
        application.addonManager.addons.size() == 1
        application.addonManager.findAddon('integration')
        application.addonManager.findAddon('IntegrationGriffonAddon')
        application.addonManager.addons.containsKey('integration')
    }

    def 'Verify MVCGroupManager'() {
        expect:
        application.mvcGroupManager.configurations.size() == 2
        application.mvcGroupManager.findConfiguration('integration')
        application.mvcGroupManager.findConfiguration('simple')
        application.mvcGroupManager.configurations.containsKey('integration')
        application.mvcGroupManager.configurations.containsKey('simple')

        application.mvcGroupManager.models.containsKey('integration')
        application.mvcGroupManager.controllers.containsKey('integration')
        application.mvcGroupManager.views.containsKey('integration')
    }

    def 'Verify ArtifactManager'() {
        given:
        GriffonModel model = application.mvcGroupManager.findGroup('integration').model

        expect:
        application.artifactManager.findGriffonClass('integration.SimpleModel')
        application.artifactManager.findGriffonClass('integration.SimpleModel', 'model')
        application.artifactManager.findGriffonClass(SimpleModel)
        application.artifactManager.findGriffonClass(SimpleModel, 'model')
        application.artifactManager.findGriffonClass(model)

        !application.artifactManager.findGriffonClass('integration.SampleModel')
        !application.artifactManager.findGriffonClass('integration.SampleModel', 'model')
        !application.artifactManager.findGriffonClass('integration.SampleModel', 'domain')
        !application.artifactManager.findGriffonClass(SimpleModel, 'domain')
        !application.artifactManager.findGriffonClass(SimpleModel, 'controller')

        application.artifactManager.getClassesOfType('model').clazz == [IntegrationModel, SimpleModel]
        !application.artifactManager.getClassesOfType('domain')

        application.artifactManager.allClasses*.clazz.sort() == [IntegrationModel, IntegrationView, IntegrationController, IntegrationService, SimpleModel, SimpleView, SimpleController].sort()
    }

    def 'Verify withMvcGroup(type , handler)'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('simple', new MVCCallable() {
            @Override
            void call(
                @Nullable GriffonModel model,
                @Nullable GriffonView view,
                @Nullable GriffonController controller) {
                checks << (model instanceof SimpleModel)
                checks << (view instanceof SimpleView)
                checks << (controller instanceof SimpleController)
                checks << (controller.mvcId == 'simple')
                checks << (controller.key == null)
            }
        })

        then:
        checks.every { it == true }
    }

    def 'Verify withMvcGroup(type, id , handler)'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('simple', 'simple-1', new MVCCallable() {
            @Override
            void call(
                @Nullable GriffonModel model,
                @Nullable GriffonView view,
                @Nullable GriffonController controller) {
                checks << (model instanceof SimpleModel)
                checks << (view instanceof SimpleView)
                checks << (controller instanceof SimpleController)
                checks << (controller.mvcId == 'simple-1')
                checks << (controller.key == null)
            }
        })

        then:
        checks.every { it == true }
    }

    def 'Verify withMvcGroup(type, id, map , handler)'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('simple', 'simple-2', [key: 'griffon'], new MVCCallable() {
            @Override
            void call(
                @Nullable GriffonModel model,
                @Nullable GriffonView view,
                @Nullable GriffonController controller) {
                checks << (model instanceof SimpleModel)
                checks << (view instanceof SimpleView)
                checks << (controller instanceof SimpleController)
                checks << (controller.mvcId == 'simple-2')
                checks << (controller.key == 'griffon')
            }
        })

        then:
        checks.every { it == true }
    }

    def 'Verify withMvcGroup(type, map , handler)'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('simple', [key: 'griffon'], new MVCCallable() {
            @Override
            void call(
                @Nullable GriffonModel model,
                @Nullable GriffonView view,
                @Nullable GriffonController controller) {
                checks << (model instanceof SimpleModel)
                checks << (view instanceof SimpleView)
                checks << (controller instanceof SimpleController)
                checks << (controller.mvcId == 'simple')
                checks << (controller.key == 'griffon')
            }
        })

        then:
        checks.every { it == true }
    }

    def 'Verify withMvcGroup(map, type, id , handler)'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('simple', 'simple-2', key: 'griffon', new MVCCallable() {
            @Override
            void call(
                @Nullable GriffonModel model,
                @Nullable GriffonView view,
                @Nullable GriffonController controller) {
                checks << (model instanceof SimpleModel)
                checks << (view instanceof SimpleView)
                checks << (controller instanceof SimpleController)
                checks << (controller.mvcId == 'simple-2')
                checks << (controller.key == 'griffon')
            }
        })

        then:
        checks.every { it == true }
    }

    def 'Verify withMvcGroup(map, type , handler)'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('simple', key: 'griffon', new MVCCallable() {
            @Override
            void call(
                @Nullable GriffonModel model,
                @Nullable GriffonView view,
                @Nullable GriffonController controller) {
                checks << (model instanceof SimpleModel)
                checks << (view instanceof SimpleView)
                checks << (controller instanceof SimpleController)
                checks << (controller.mvcId == 'simple')
                checks << (controller.key == 'griffon')
            }
        })

        then:
        checks.every { it == true }
    }

    def 'Verify buildMVCGroup(type)'() {
        given:
        MVCGroup group = application.mvcGroupManager.buildMVCGroup('simple')

        expect:
        group.model instanceof SimpleModel
        group.view instanceof SimpleView
        group.controller instanceof SimpleController
        group.controller.mvcId == 'simple'
        group.controller.key == null

        cleanup:
        group.destroy()
    }

    def 'Verify buildMVCGroup(type, id)'() {
        given:
        MVCGroup group = application.mvcGroupManager.buildMVCGroup('simple', 'simple-1')

        expect:
        group.model instanceof SimpleModel
        group.view instanceof SimpleView
        group.controller instanceof SimpleController
        group.controller.mvcId == 'simple-1'
        group.controller.key == null

        cleanup:
        group.destroy()
    }

    def 'Verify buildMVCGroup(type, map)'() {
        given:
        MVCGroup group = application.mvcGroupManager.buildMVCGroup('simple', [key: 'griffon'])

        expect:
        group.model instanceof SimpleModel
        group.view instanceof SimpleView
        group.controller instanceof SimpleController
        group.controller.mvcId == 'simple'
        group.controller.key == 'griffon'

        cleanup:
        group.destroy()
    }

    def 'Verify buildMVCGroup(map, type)'() {
        given:
        MVCGroup group = application.mvcGroupManager.buildMVCGroup('simple', key: 'griffon')

        expect:
        group.model instanceof SimpleModel
        group.view instanceof SimpleView
        group.controller instanceof SimpleController
        group.controller.mvcId == 'simple'
        group.controller.key == 'griffon'

        cleanup:
        group.destroy()
    }

    def 'Verify buildMVCGroup(type, id, map)'() {
        given:
        MVCGroup group = application.mvcGroupManager.buildMVCGroup('simple', 'simple-2', [key: 'griffon'])

        expect:
        group.model instanceof SimpleModel
        group.view instanceof SimpleView
        group.controller instanceof SimpleController
        group.controller.mvcId == 'simple-2'
        group.controller.key == 'griffon'

        cleanup:
        group.destroy()
    }

    def 'Verify buildMVCGroup(map, type, id)'() {
        given:
        MVCGroup group = application.mvcGroupManager.buildMVCGroup('simple', 'simple-2', key: 'griffon')

        expect:
        group.model instanceof SimpleModel
        group.view instanceof SimpleView
        group.controller instanceof SimpleController
        group.controller.mvcId == 'simple-2'
        group.controller.key == 'griffon'

        cleanup:
        group.destroy()
    }

    def 'Verify createMVCGroup(type)'() {
        given:
        List members = application.mvcGroupManager.createMVCGroup('simple')

        expect:
        members[0] instanceof SimpleModel
        members[1] instanceof SimpleView
        members[2] instanceof SimpleController
        members[2].mvcId == 'simple'
        members[2].key == null

        cleanup:
        members[2].mvcGroup.destroy()
    }

    def 'Verify createMVCGroup(type, id)'() {
        given:
        List members = application.mvcGroupManager.createMVCGroup('simple', 'simple-1')

        expect:
        members[0] instanceof SimpleModel
        members[1] instanceof SimpleView
        members[2] instanceof SimpleController
        members[2].mvcId == 'simple-1'
        members[2].key == null

        cleanup:
        members[2].mvcGroup.destroy()
    }

    def 'Verify createMVCGroup(type, map)'() {
        given:
        List members = application.mvcGroupManager.createMVCGroup('simple', [key: 'griffon'])

        expect:
        members[0] instanceof SimpleModel
        members[1] instanceof SimpleView
        members[2] instanceof SimpleController
        members[2].mvcId == 'simple'
        members[2].key == 'griffon'

        cleanup:
        members[2].mvcGroup.destroy()
    }

    def 'Verify createMVCGroup(map, type)'() {
        given:
        List members = application.mvcGroupManager.createMVCGroup('simple', key: 'griffon')

        expect:
        members[0] instanceof SimpleModel
        members[1] instanceof SimpleView
        members[2] instanceof SimpleController
        members[2].mvcId == 'simple'
        members[2].key == 'griffon'

        cleanup:
        members[2].mvcGroup.destroy()
    }

    def 'Verify createMVCGroup(type, id, map)'() {
        given:
        List members = application.mvcGroupManager.createMVCGroup('simple', 'simple-2', [key: 'griffon'])

        expect:
        members[0] instanceof SimpleModel
        members[1] instanceof SimpleView
        members[2] instanceof SimpleController
        members[2].mvcId == 'simple-2'
        members[2].key == 'griffon'

        cleanup:
        members[2].mvcGroup.destroy()
    }

    def 'Verify createMVCGroup(map, type, id)'() {
        given:
        List members = application.mvcGroupManager.createMVCGroup('simple', 'simple-2', key: 'griffon')

        expect:
        members[0] instanceof SimpleModel
        members[1] instanceof SimpleView
        members[2] instanceof SimpleController
        members[2].mvcId == 'simple-2'
        members[2].key == 'griffon'

        cleanup:
        members[2].mvcGroup.destroy()
    }

    private
    static class TestShutdownHandler implements ShutdownHandler, Invokable {
        private boolean invoked

        @Override
        boolean canShutdown(@Nonnull GriffonApplication application) {
            return true
        }

        @Override
        void onShutdown(@Nonnull GriffonApplication application) {
            invoked = true
        }

        boolean isInvoked() {
            return invoked
        }
    }
}
