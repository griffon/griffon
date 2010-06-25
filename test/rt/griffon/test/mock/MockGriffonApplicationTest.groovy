package griffon.test.mock

import griffon.core.ApplicationPhase
import griffon.core.GriffonApplication

class MockGriffonApplicationTest extends GroovyTestCase {
    void testCanCreateApplication() {
        GriffonApplication app = new MockGriffonApplication()
        assert app.phase == ApplicationPhase.INITIALIZE
    }

    void testCanInitialize() {
        GriffonApplication app = new MockGriffonApplication()
        app.uiThreadHandler = new MockUIThreadHandler()
        app.bindings.mocked = 0
        app.initialize()
        assert app.bindings.mocked == 0
        app.event('Mock', [app])
        assert app.bindings.mocked == 1 
        app.event('Mock', [app])
        assert app.bindings.mocked == 2 

        assert app.mvcGroups == [
            mock: [
                model: 'griffon.test.mock.MockModel',
                controller: 'griffon.test.mock.MockController',
                view: 'griffon.test.mock.MockView'
            ]
        ]
    }

    void testCanStartup() {
        GriffonApplication app = new MockGriffonApplication()
        app.uiThreadHandler = new MockUIThreadHandler()
        app.initialize()
        app.startup()
        assert app.phase == ApplicationPhase.STARTUP
        
        assert app.models.mock instanceof MockModel
        assert app.controllers.mock instanceof MockController
        assert app.views.mock instanceof MockView

        assert app.builders.mock.mock(foo: 'bar') == [foo: 'bar']
    }
}
