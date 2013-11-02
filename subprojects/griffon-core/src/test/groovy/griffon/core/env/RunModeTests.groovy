package griffon.core.env

import griffon.core.env.RunMode

class RunModeTests extends GroovyTestCase {
    protected void setUp() {
        System.clearProperty(RunMode.KEY)
    }

    protected void tearDown() {
        System.clearProperty(RunMode.KEY)
    }

    void testIsSystemSet() {
        assert !RunMode.isSystemSet()
        System.setProperty(RunMode.KEY, 'STANDALONE')
        assert RunMode.isSystemSet()
    }

    void testGetCurrent() {
        assert RunMode.current == RunMode.STANDALONE

        System.setProperty(RunMode.KEY, 'STANDALONE')
        assert RunMode.current == RunMode.STANDALONE

        System.setProperty(RunMode.KEY, 'WEBSTART')
        assert RunMode.current == RunMode.WEBSTART

        System.setProperty(RunMode.KEY, 'APPLET')
        assert RunMode.current == RunMode.APPLET

        System.setProperty(RunMode.KEY, 'TEST RUN MODE')
        assert RunMode.current == RunMode.CUSTOM
        assert RunMode.current.name == 'TEST RUN MODE'
    }

    void testGetRunMode() {
        assert RunMode.STANDALONE == RunMode.getRunMode('standalone')
        assert RunMode.WEBSTART == RunMode.getRunMode('webstart')
        assert RunMode.APPLET == RunMode.getRunMode('applet')
        assert !RunMode.getRunMode('custom')
    }
}
