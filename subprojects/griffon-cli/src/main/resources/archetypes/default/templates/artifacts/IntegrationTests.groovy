@artifact.package@import griffon.core.GriffonApplication
import griffon.test.*

class @artifact.name@ extends @artifact.superclass@ {
    GriffonApplication app

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
        fail('Not implemented!')
    }
}
