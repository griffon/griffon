package org.codehaus.griffon.runtime.core

import groovy.beans.Bindable
import griffon.core.GriffonClass
import griffon.core.GriffonModelClass

class DefaultGriffonModelClassTests extends AbstractGriffonClassTestCase {
    private GriffonModelClass modelClass

    void setUp() {
        super.setUp()
        modelClass = new DefaultGriffonModelClass(app, SampleModel)
    }

    protected GriffonClass getGriffonClassUnderTest() {
        modelClass
    }

    void testGetPropertyNames() {
        assert ['property1'] == modelClass.propertyNames
        modelClass.metaClass.property5 = 'property5'
        modelClass.resetCaches()
        assert ['property1', 'property5'] == modelClass.propertyNames
    }

    void testGetEventNames() {
        assert ['Event1', 'Event2'] == modelClass.eventNames
        modelClass.metaClass.onEvent5 = {}
        modelClass.resetCaches()
        assert ['Event1', 'Event2', 'Event5'] == modelClass.eventNames
    }
}

class SampleModel extends AbstractGriffonModel {
    @Bindable String property1
    def property2 = {}
    private String property3
    private property4 = {}

    void mvcGroupInit(Map args) {}

    def onEvent1 = {-> }
    void onEvent2() {}
    private def onEvent3 = {-> }
    private void onEvent4() {}
}
