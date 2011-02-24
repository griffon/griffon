@artifact.package@import griffon.core.GriffonApplication;
import griffon.test.*;

public class @artifact.name@ extends @artifact.superclass@ {
    private GriffonApplication app;

    public void setApp(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
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
