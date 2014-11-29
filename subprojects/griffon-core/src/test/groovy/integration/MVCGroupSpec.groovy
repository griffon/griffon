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
import griffon.core.env.ApplicationPhase
import griffon.core.mvc.MVCGroup
import groovy.transform.CompileStatic
import org.codehaus.griffon.runtime.core.DefaultApplicationBootstrapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class MVCGroupSpec extends Specification {
    @Shared
    private static GriffonApplication application

    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    def setupSpec() {
        application = new TestGriffonApplication(['foo', 'bar'] as String[])
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

    def 'Creating an MVCGroup through the MVCGroupManager does not set implicit parent group'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('simple') { MVCGroup group ->
            checks << (group.model instanceof SimpleModel)
            checks << (group.view instanceof SimpleView)
            checks << (group.controller instanceof SimpleController)
            checks << (group.controller.mvcId == 'simple')
            checks << (group.controller.key == null)
            checks << (!group.controller.parentGroup)
            checks << (!group.controller.parentModel)
        }

        then:
        checks.every { it == true }
    }

    def 'Creating an MVCGroup through a group does set implicit parent group'() {
        given:
        List checks = []
        MVCGroup parentGroup = application.mvcGroupManager.createMVCGroup('simple', 'parent1')

        when:
        parentGroup.withMVCGroup('simple') { MVCGroup group ->
            checks << (group.model instanceof SimpleModel)
            checks << (group.view instanceof SimpleView)
            checks << (group.controller instanceof SimpleController)
            checks << (group.controller.mvcId == 'simple')
            checks << (group.controller.key == null)
            checks << (group.controller.parentGroup == parentGroup)
            checks << (group.controller.parentModel == parentGroup.model)
        }

        then:
        checks.every { it == true }
    }

    def 'Creating an MVCGroup through an artifact does set implicit parent group'() {
        given:
        List checks = []
        MVCGroup parentGroup = application.mvcGroupManager.createMVCGroup('simple', 'parent2')

        when:
        parentGroup.controller.withMVCGroup('simple') { MVCGroup group ->
            checks << (group.model instanceof SimpleModel)
            checks << (group.view instanceof SimpleView)
            checks << (group.controller instanceof SimpleController)
            checks << (group.controller.mvcId == 'simple')
            checks << (group.controller.key == null)
            checks << (group.controller.parentGroup == parentGroup)
            checks << (group.controller.parentModel == parentGroup.model)
        }

        then:
        checks.every { it == true }
    }
}