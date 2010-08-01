package org.codehaus.griffon.runtime.core

import griffon.core.GriffonClass
import griffon.core.GriffonControllerClass

class DefaultGriffonControllerClassTests extends AbstractGriffonClassTestCase {
    private GriffonControllerClass controllerClass

    void setUp() {
        super.setUp()
        controllerClass = new DefaultGriffonControllerClass(app, SampleController)
    }

    protected GriffonClass getGriffonClassUnderTest() {
        controllerClass
    }

    void testGetActions() {
        assert ['action1', 'action2', 'action3'] == controllerClass.actionNames
        controllerClass.metaClass.getMetaAction = {}
        controllerClass.resetCaches()
        assert ['action1', 'action2', 'action3', 'metaAction'] == controllerClass.actionNames
    }

    void testGetEventNames() {
        assert ['Event1', 'Event2'] == controllerClass.eventNames
        controllerClass.metaClass.onEvent5 = {}
        controllerClass.resetCaches()
        assert ['Event1', 'Event2', 'Event5'] == controllerClass.eventNames
    }
}

class SampleController {
    def model
    def view

    void mvcGroupInit(Map args) {}

    def action1 = { evt = null -> }
    def action2 = {}
    void action3(evt = null) {}
    private action4 = { evt = null -> }
    private void action5(evt = null) {}

    def onEvent1 = {-> }
    void onEvent2() {}
    private def onEvent3 = {-> }
    private void onEvent4() {}
}
