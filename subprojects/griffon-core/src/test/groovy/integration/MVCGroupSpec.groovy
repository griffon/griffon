/*
 * Copyright 2008-2017 the original author or authors.
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
import griffon.exceptions.MVCGroupInstantiationException
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
        MVCGroup parentGroup = application.mvcGroupManager.createMVCGroup('integration', 'parent1')

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
        MVCGroup parentGroup = application.mvcGroupManager.createMVCGroup('integration', 'parent2')

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

    def 'Validate MVCGroup relationships after creation and destruction (createMVCGroup)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.createMVCGroup('child', 'child1')
        root.createMVCGroup('child', 'child2')
        root.createMVCGroup('child', 'child3')

        then:
        root.childrenGroups.keySet() == ['child1', 'child2', 'child3'] as Set
        ['child1', 'child2', 'child3'].each { mvcId ->
            MVCGroup child = application.mvcGroupManager.findGroup(mvcId)
            assert child.model.parentGroup == root
            assert child.view.parentGroup == root
            assert child.controller.parentGroup == root
            assert child.model.parentModel == root.model
            assert child.view.parentView == root.view
            assert child.controller.parentController == root.controller
        }

        when:
        MVCGroup child2 = application.mvcGroupManager.findGroup('child2')
        ChildModel model = child2.model
        ChildView view = child2.view
        ChildController controller = child2.controller
        child2.destroy()

        then:
        root.childrenGroups.keySet() == ['child1', 'child3'] as Set
        !model.parentGroup
        !model.parentModel
        !view.parentGroup
        !view.parentView
        !controller.parentGroup
        !controller.parentController

        when:
        root.destroy()

        then:
        !root.alive
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('root')
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child3')
    }

    def 'Validate MVCGroup relationships after creation and destruction (createMVC)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.createMVC('child', 'child1')
        root.createMVC('child', 'child2')
        root.createMVC('child', 'child3')

        then:
        root.childrenGroups.keySet() == ['child1', 'child2', 'child3'] as Set
        ['child1', 'child2', 'child3'].each { mvcId ->
            MVCGroup child = application.mvcGroupManager.findGroup(mvcId)
            assert child.model.parentGroup == root
            assert child.view.parentGroup == root
            assert child.controller.parentGroup == root
            assert child.model.parentModel == root.model
            assert child.view.parentView == root.view
            assert child.controller.parentController == root.controller
        }

        when:
        MVCGroup child2 = application.mvcGroupManager.findGroup('child2')
        ChildModel model = child2.model
        ChildView view = child2.view
        ChildController controller = child2.controller
        child2.destroy()

        then:
        root.childrenGroups.keySet() == ['child1', 'child3'] as Set
        !model.parentGroup
        !model.parentModel
        !view.parentGroup
        !view.parentView
        !controller.parentGroup
        !controller.parentController

        when:
        root.destroy()

        then:
        !root.alive
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('root')
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child3')
    }

    def 'Validate MVCGroup relationships after creation and destruction (withMVCGroup)'() {
        given:
        List checks = []
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.withMVCGroup('child', 'child1') { MVCGroup group ->
            checks << (root.childrenGroups.containsKey('child1'))
            checks << (group.model.parentGroup == root)
            checks << (group.view.parentGroup == root)
            checks << (group.controller.parentGroup == root)
            checks << (group.model.parentModel == root.model)
            checks << (group.view.parentView == root.view)
            checks << (group.controller.parentController == root.controller)
        }
        root.withMVCGroup('child', 'child2') { MVCGroup group ->
            checks << (root.childrenGroups.containsKey('child2'))
            checks << (group.model.parentGroup == root)
            checks << (group.view.parentGroup == root)
            checks << (group.controller.parentGroup == root)
            checks << (group.model.parentModel == root.model)
            checks << (group.view.parentView == root.view)
            checks << (group.controller.parentController == root.controller)
        }
        root.withMVCGroup('child', 'child3') { MVCGroup group ->
            checks << (root.childrenGroups.containsKey('child3'))
            checks << (group.model.parentGroup == root)
            checks << (group.view.parentGroup == root)
            checks << (group.controller.parentGroup == root)
            checks << (group.model.parentModel == root.model)
            checks << (group.view.parentView == root.view)
            checks << (group.controller.parentController == root.controller)
        }

        then:
        checks.every { it == true }
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child2')
        !application.mvcGroupManager.findGroup('child3')

        cleanup:
        root.destroy()
    }

    def 'Validate MVCGroup relationships after creation and destruction (withMVC)'() {
        given:
        List checks = []
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.withMVC('child', 'child1') { model, view, controller ->
            checks << (root.childrenGroups.containsKey('child1'))
            checks << (model.parentGroup == root)
            checks << (view.parentGroup == root)
            checks << (controller.parentGroup == root)
            checks << (model.parentModel == root.model)
            checks << (view.parentView == root.view)
            checks << (controller.parentController == root.controller)
        }
        root.withMVC('child', 'child2') { model, view, controller ->
            checks << (root.childrenGroups.containsKey('child2'))
            checks << (model.parentGroup == root)
            checks << (view.parentGroup == root)
            checks << (controller.parentGroup == root)
            checks << (model.parentModel == root.model)
            checks << (view.parentView == root.view)
            checks << (controller.parentController == root.controller)
        }
        root.withMVC('child', 'child3') { model, view, controller ->
            checks << (root.childrenGroups.containsKey('child3'))
            checks << (model.parentGroup == root)
            checks << (view.parentGroup == root)
            checks << (controller.parentGroup == root)
            checks << (model.parentModel == root.model)
            checks << (view.parentView == root.view)
            checks << (controller.parentController == root.controller)
        }

        then:
        checks.every { it == true }
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child2')
        !application.mvcGroupManager.findGroup('child3')

        cleanup:
        root.destroy()
    }

    def 'Validate contextual injections'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        MVCGroup child = root.createMVCGroup('child', 'child1')

        then:
        child.controller.value == 'VALUE'
        child.controller.val == 'VALUE'

        when:
        def controller = child.controller
        root.destroy()

        then:
        !controller.value
    }

    def 'Creating an MVCGroup through a group does set implicit parent group (typed mvcgroup)'() {
        given:
        List checks = []
        MVCGroup parentGroup = application.mvcGroupManager.createMVCGroup('integration', 'parent3')

        when:
        parentGroup.withMVCGroup(SimpleMVCGroup) { MVCGroup group ->
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

    def 'Creating an MVCGroup through an artifact does set implicit parent group (typed mvcgroup)'() {
        given:
        List checks = []
        MVCGroup parentGroup = application.mvcGroupManager.createMVCGroup('integration', 'parent4')

        when:
        parentGroup.controller.withMVCGroup(SimpleMVCGroup) { MVCGroup group ->
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

    def 'Validate MVCGroup relationships after creation and destruction (createMVCGroup) (typed mvcgroup)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.createMVCGroup(ChildMVCGroup, 'child1')
        root.createMVCGroup(ChildMVCGroup, 'child2')
        root.createMVCGroup(ChildMVCGroup, 'child3')

        then:
        root.childrenGroups.keySet() == ['child1', 'child2', 'child3'] as Set
        ['child1', 'child2', 'child3'].each { mvcId ->
            MVCGroup child = application.mvcGroupManager.findGroup(mvcId)
            assert child.model.parentGroup == root
            assert child.view.parentGroup == root
            assert child.controller.parentGroup == root
            assert child.model.parentModel == root.model
            assert child.view.parentView == root.view
            assert child.controller.parentController == root.controller
        }

        when:
        MVCGroup child2 = application.mvcGroupManager.findGroup('child2')
        ChildModel model = child2.model
        ChildView view = child2.view
        ChildController controller = child2.controller
        child2.destroy()

        then:
        root.childrenGroups.keySet() == ['child1', 'child3'] as Set
        !model.parentGroup
        !model.parentModel
        !view.parentGroup
        !view.parentView
        !controller.parentGroup
        !controller.parentController

        when:
        root.destroy()

        then:
        !root.alive
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('root')
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child3')
    }

    def 'Validate MVCGroup relationships after creation and destruction (createMVC) (typed mvcgroup)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.createMVC(ChildMVCGroup, 'child1')
        root.createMVC(ChildMVCGroup, 'child2')
        root.createMVC(ChildMVCGroup, 'child3')

        then:
        root.childrenGroups.keySet() == ['child1', 'child2', 'child3'] as Set
        ['child1', 'child2', 'child3'].each { mvcId ->
            MVCGroup child = application.mvcGroupManager.findGroup(mvcId)
            assert child.model.parentGroup == root
            assert child.view.parentGroup == root
            assert child.controller.parentGroup == root
            assert child.model.parentModel == root.model
            assert child.view.parentView == root.view
            assert child.controller.parentController == root.controller
        }

        when:
        MVCGroup child2 = application.mvcGroupManager.findGroup('child2')
        ChildModel model = child2.model
        ChildView view = child2.view
        ChildController controller = child2.controller
        child2.destroy()

        then:
        root.childrenGroups.keySet() == ['child1', 'child3'] as Set
        !model.parentGroup
        !model.parentModel
        !view.parentGroup
        !view.parentView
        !controller.parentGroup
        !controller.parentController

        when:
        root.destroy()

        then:
        !root.alive
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('root')
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child3')
    }

    def 'Validate MVCGroup relationships after creation and destruction (withMVCGroup) (typed mvcgroup)'() {
        given:
        List checks = []
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.withMVCGroup(ChildMVCGroup, 'child1') { MVCGroup group ->
            checks << (root.childrenGroups.containsKey('child1'))
            checks << (group.model.parentGroup == root)
            checks << (group.view.parentGroup == root)
            checks << (group.controller.parentGroup == root)
            checks << (group.model.parentModel == root.model)
            checks << (group.view.parentView == root.view)
            checks << (group.controller.parentController == root.controller)
        }
        root.withMVCGroup(ChildMVCGroup, 'child2') { MVCGroup group ->
            checks << (root.childrenGroups.containsKey('child2'))
            checks << (group.model.parentGroup == root)
            checks << (group.view.parentGroup == root)
            checks << (group.controller.parentGroup == root)
            checks << (group.model.parentModel == root.model)
            checks << (group.view.parentView == root.view)
            checks << (group.controller.parentController == root.controller)
        }
        root.withMVCGroup(ChildMVCGroup, 'child3') { MVCGroup group ->
            checks << (root.childrenGroups.containsKey('child3'))
            checks << (group.model.parentGroup == root)
            checks << (group.view.parentGroup == root)
            checks << (group.controller.parentGroup == root)
            checks << (group.model.parentModel == root.model)
            checks << (group.view.parentView == root.view)
            checks << (group.controller.parentController == root.controller)
        }

        then:
        checks.every { it == true }
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child2')
        !application.mvcGroupManager.findGroup('child3')

        cleanup:
        root.destroy()
    }

    def 'Validate MVCGroup relationships after creation and destruction (withMVC) (typed mvcgroup)'() {
        given:
        List checks = []
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.withMVC(ChildMVCGroup, 'child1') { model, view, controller ->
            checks << (root.childrenGroups.containsKey('child1'))
            checks << (model.parentGroup == root)
            checks << (view.parentGroup == root)
            checks << (controller.parentGroup == root)
            checks << (model.parentModel == root.model)
            checks << (view.parentView == root.view)
            checks << (controller.parentController == root.controller)
        }
        root.withMVC(ChildMVCGroup, 'child2') { model, view, controller ->
            checks << (root.childrenGroups.containsKey('child2'))
            checks << (model.parentGroup == root)
            checks << (view.parentGroup == root)
            checks << (controller.parentGroup == root)
            checks << (model.parentModel == root.model)
            checks << (view.parentView == root.view)
            checks << (controller.parentController == root.controller)
        }
        root.withMVC(ChildMVCGroup, 'child3') { model, view, controller ->
            checks << (root.childrenGroups.containsKey('child3'))
            checks << (model.parentGroup == root)
            checks << (view.parentGroup == root)
            checks << (controller.parentGroup == root)
            checks << (model.parentModel == root.model)
            checks << (view.parentView == root.view)
            checks << (controller.parentController == root.controller)
        }

        then:
        checks.every { it == true }
        !root.childrenGroups
        !application.mvcGroupManager.findGroup('child1')
        !application.mvcGroupManager.findGroup('child2')
        !application.mvcGroupManager.findGroup('child3')

        cleanup:
        root.destroy()
    }

    def 'Validate contextual injections (typed mvcgroup)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        MVCGroup child = root.createMVCGroup(ChildMVCGroup, 'child1')

        then:
        child.controller.value == 'VALUE'
        child.controller.val == 'VALUE'

        when:
        def controller = child.controller
        root.destroy()

        then:
        !controller.value
    }

    def 'Validate argument injection (all args are available)'() {
        given:
        List checks = []

        when:
        application.mvcGroupManager.withMVCGroup('args', [arg1: 'value1', arg2: 'value2']) { MVCGroup group ->
            checks << (group.controller.arg1 == 'value1')
            checks << (group.controller.arg2 == 'value2')
        }

        then:
        checks.every { it == true }
    }

    def 'Validate argument injection (missing field argument)'() {
        when:
        application.mvcGroupManager.withMVCGroup('args', [arg2: 'value2']) { MVCGroup group -> }

        then:
        thrown(MVCGroupInstantiationException)
    }

    def 'Validate argument injection (missing method argument)'() {
        when:
        application.mvcGroupManager.withMVCGroup('args', [arg1: 'value1']) { MVCGroup group -> }

        then:
        thrown(MVCGroupInstantiationException)
    }

    def 'Validate argument injections with property editor (field success)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        MVCGroup child = root.createMVCGroup(ChildMVCGroup, 'child1', [list1: '1, 2, 3'])

        then:
        child.controller.list1 == ['1', '2', '3']

        and:
        root.destroy()
    }

    def 'Validate argument injections with property editor (method success)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        MVCGroup child = root.createMVCGroup(ChildMVCGroup, 'child1', [list2: '1, 2, 3'])

        then:
        child.controller.list2 == ['1', '2', '3']

        and:
        root.destroy()
    }

    def 'Validate argument injections with property editor (failure)'() {
        given:
        MVCGroup root = application.mvcGroupManager.createMVCGroup('root')

        when:
        root.createMVCGroup(ChildMVCGroup, 'child1', [list3: '1, 2, 3'])

        then:
        thrown(MVCGroupInstantiationException)

        and:
        root.destroy()
    }
}