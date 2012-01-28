package sample;

import griffon.core.GriffonApplication;
import griffon.test.GriffonUnitTestCase;

public class AbstractTests extends GriffonUnitTestCase {
    private GriffonApplication app;

    public GriffonApplication getApp() {
        return app;
    }

    public void setApp(GriffonApplication app) {
        this.app = app;
    }

    protected void setUp() {
        super.setUp();
    }

    protected void tearDown() {
        super.tearDown();
    }

    public void testSomething() {

    }
}
