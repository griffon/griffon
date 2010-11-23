package griffon.util
/**
 * @author Graeme Rocher
 * @since 1.1
 */

public class BuildScopeTests extends GroovyTestCase{
    protected void tearDown() {
        System.setProperty(BuildScope.KEY, "")
    }

    void testEnableScope() {
        assertEquals BuildScope.ALL, BuildScope.getCurrent()

        BuildScope.TEST.enable()

        assertEquals BuildScope.TEST, BuildScope.getCurrent()
    }

    void testIsValid() {
        assertTrue BuildScope.isValid("run")
        assertTrue BuildScope.isValid("test")
        assertTrue BuildScope.isValid("run", "test")

        BuildScope.TEST.enable()

        assertTrue BuildScope.isValid("test")
        assertTrue BuildScope.isValid("run", "test")
        assertFalse BuildScope.isValid("run")
    }
}
