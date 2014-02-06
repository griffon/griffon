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
import griffon.core.mvc.MVCGroup
import org.codehaus.griffon.runtime.core.DefaultApplicationBootstrapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.annotation.Nonnull

import static griffon.util.AnnotationUtils.named
import static java.util.Arrays.asList

@Stepwise
class GriffonApplicationSpec extends Specification {
    @Shared
    private static GriffonApplication application

    @Shared
    private static List<Invokable> invokables = []

    def setupSpec() {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
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
        controllerClass.actionNames == ['sayHello', 'throwException']
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

    def 'Invoke throwException Action'() {
        given:
        InvokeActionInterceptor interceptor = application.injector.getInstance(ActionInterceptor)
        MVCGroup group = application.mvcGroupManager.findGroup('integration')

        when:
        group.controller.invokeAction('throwException')

        then:
        interceptor.before
        interceptor.after
        interceptor.exception
    }

    def 'Verify ActionManager'() {
        given:
        GriffonController controller = application.mvcGroupManager.findGroup('integration').controller

        expect:
        application.actionManager.actionsFor(controller).keySet() == (['sayHello','throwException'] as Set)
        application.actionManager.normalizeName('fooAction') == 'foo'
        application.actionManager.normalizeName('foo') == 'foo'
        application.actionManager.actionFor(controller, 'sayHello')
        !application.actionManager.actionFor(controller, 'unknown')
    }

    private static class TestShutdownHandler implements ShutdownHandler, Invokable {
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
