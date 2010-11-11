package org.codehaus.griffon.runtime.core

import griffon.core.GriffonClass
import griffon.core.GriffonServiceClass

class DefaultGriffonServiceClassTests extends AbstractGriffonClassTestCase {
    private GriffonServiceClass serviceClass

    void setUp() {
        super.setUp()
        serviceClass = new DefaultGriffonServiceClass(app, SampleService)
    }

    protected GriffonClass getGriffonClassUnderTest() {
        serviceClass
    }

    void testGetServices() {
        assert ['service1', 'service2'] == serviceClass.serviceNames
        serviceClass.metaClass.service5 = {}
        serviceClass.resetCaches()
        assert ['service1', 'service2', 'service5'] == serviceClass.serviceNames
    }

    void testGetEventNames() {
        assert ['Event1', 'Event2'] == serviceClass.eventNames
        serviceClass.metaClass.onEvent5 = {}
        serviceClass.resetCaches()
        assert ['Event1', 'Event2', 'Event5'] == serviceClass.eventNames
    }
}

class SampleService {
    def service1 = { }
    void service2() {}
    private service3 = { }
    private void service4() {}

    def onEvent1 = {-> }
    void onEvent2() {}
    private def onEvent3 = {-> }
    private void onEvent4() {}
}
