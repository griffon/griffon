package griffon.test.mock

import griffon.core.ApplicationPhase
import griffon.core.GriffonApplication
import griffon.core.UIThreadManager

class MockGriffonApplicationTest extends GroovyTestCase {
    static {
        UIThreadManager.instance.setUIThreadHandler(new MockUIThreadHandler())
    }

    void testCanCreateApplication() {
        GriffonApplication app = new MockGriffonApplication()
        assert app.phase == ApplicationPhase.INITIALIZE
    }

    void testCanInitialize() {
        GriffonApplication app = new MockGriffonApplication()
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
        app.initialize()
        app.startup()
        assert app.phase == ApplicationPhase.STARTUP
        
        assert app.models.mock instanceof MockModel
        assert app.controllers.mock instanceof MockController
        assert app.views.mock instanceof MockView

        assert app.builders.mock.mock(foo: 'bar') == [foo: 'bar']
    }
}
