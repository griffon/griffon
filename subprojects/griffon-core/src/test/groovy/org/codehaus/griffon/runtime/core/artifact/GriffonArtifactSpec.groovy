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
package org.codehaus.griffon.runtime.core.artifact

import griffon.core.ApplicationBootstrapper
import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonArtifact
import griffon.core.artifact.GriffonController
import griffon.core.artifact.GriffonModel
import griffon.core.artifact.GriffonView
import griffon.core.env.ApplicationPhase
import griffon.core.mvc.MVCCallable
import griffon.core.mvc.MVCGroup
import integration.SimpleController
import integration.SimpleModel
import integration.SimpleView
import integration.TestGriffonApplication
import org.codehaus.griffon.runtime.core.DefaultApplicationBootstrapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.annotation.Nullable
import java.util.concurrent.Executors
import java.util.concurrent.Future

@Stepwise
class GriffonArtifactSpec extends Specification {
    @Shared
    private static GriffonApplication application

    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    def setupSpec() {
        application = new TestGriffonApplication()
    }

    def cleanupSpec() {
        //when:
        assert application.shutdown()

        // then:
        assert ApplicationPhase.SHUTDOWN == application.phase
    }

    def "Bootstrap application"() {
        given:
        ApplicationBootstrapper bootstrapper = new DefaultApplicationBootstrapper(application)

        when:
        bootstrapper.bootstrap()
        bootstrapper.run()

        then:
        ApplicationPhase.MAIN == application.phase
    }

    def 'Verify withMvcGroup(type , handler)'() {
        given:
        List checks = []

        when:
        resolveMVCHandler().withMVC('simple', new MVCCallable() {
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
        resolveMVCHandler().withMVC('simple', 'simple-1', new MVCCallable() {
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
        resolveMVCHandler().withMVC('simple', 'simple-2', [key: 'griffon'], new MVCCallable() {
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
        resolveMVCHandler().withMVC('simple', [key: 'griffon'], new MVCCallable() {
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
        resolveMVCHandler().withMVC('simple', 'simple-2', key: 'griffon', new MVCCallable() {
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
        resolveMVCHandler().withMVC('simple', key: 'griffon', new MVCCallable() {
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
        MVCGroup group = resolveMVCHandler().buildMVCGroup('simple')

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
        MVCGroup group = resolveMVCHandler().buildMVCGroup('simple', 'simple-1')

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
        MVCGroup group = resolveMVCHandler().buildMVCGroup('simple', [key: 'griffon'])

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
        MVCGroup group = resolveMVCHandler().buildMVCGroup('simple', key: 'griffon')

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
        MVCGroup group = resolveMVCHandler().buildMVCGroup('simple', 'simple-2', [key: 'griffon'])

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
        MVCGroup group = resolveMVCHandler().buildMVCGroup('simple', 'simple-2', key: 'griffon')

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
        List members = resolveMVCHandler().createMVCGroup('simple')

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
        List members = resolveMVCHandler().createMVCGroup('simple', 'simple-1')

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
        List members = resolveMVCHandler().createMVCGroup('simple', [key: 'griffon'])

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
        List members = resolveMVCHandler().createMVCGroup('simple', key: 'griffon')

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
        List members = resolveMVCHandler().createMVCGroup('simple', 'simple-2', [key: 'griffon'])

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
        List members = resolveMVCHandler().createMVCGroup('simple', 'simple-2', key: 'griffon')

        expect:
        members[0] instanceof SimpleModel
        members[1] instanceof SimpleView
        members[2] instanceof SimpleController
        members[2].mvcId == 'simple-2'
        members[2].key == 'griffon'

        cleanup:
        members[2].mvcGroup.destroy()
    }

    def 'Verify destroyMVCGroup(id)'(){
        given:
        MVCGroup group = resolveMVCHandler().buildMVCGroup('simple')

        when:
        resolveMVCHandler().destroyMVCGroup('simple')

        then:
        !application.mvcGroupManager.findGroup('simple')
    }

    def 'Execute runnable inside UI sync'() {
        given:
        boolean invoked = false

        when:
        resolveMVCHandler().runInsideUISync {
            invoked = true
        }

        then:
        invoked
    }

    def 'Execute runnable inside UI async'() {
        given:
        boolean invoked = false

        when:
        resolveMVCHandler().runInsideUIAsync {
            invoked = true
        }

        then:
        invoked
    }

    def 'Execute runnable outside UI'() {
        given:
        boolean invoked = false

        when:
        resolveMVCHandler().runOutsideUI() {
            invoked = true
        }

        then:
        invoked
    }

    def 'Execute future'() {
        given:
        boolean invoked = false

        when:
        Future future = resolveMVCHandler().runFuture {
            invoked = true
        }
        future.get()

        then:
        invoked
    }

    def 'Execute future with ExecutorService'() {
        given:
        boolean invoked = false

        when:
        Future future = resolveMVCHandler().runFuture(Executors.newFixedThreadPool(1)) {
            invoked = true
        }
        future.get()

        then:
        invoked
    }

    protected GriffonArtifact resolveMVCHandler() {
        application.mvcGroupManager.findGroup('integration').controller
    }
}
