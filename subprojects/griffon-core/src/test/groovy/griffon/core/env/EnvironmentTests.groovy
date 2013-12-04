package griffon.core.env

public class EnvironmentTests extends GroovyTestCase {

    protected void tearDown() {
        System.setProperty(Environment.KEY, '')

        Metadata.getCurrent().clear()
    }

    void testGetCurrent() {
        System.setProperty('griffon.env', 'prod')

        assert Environment.PRODUCTION == Environment.getCurrent()

        System.setProperty('griffon.env', 'dev')

        assert Environment.DEVELOPMENT == Environment.getCurrent()

        System.setProperty('griffon.env', 'soe')

        assert Environment.CUSTOM == Environment.getCurrent()

    }

    void testGetEnvironment() {
        assert Environment.DEVELOPMENT == Environment.getEnvironment('dev')
        assert Environment.TEST == Environment.getEnvironment('test')
        assert Environment.PRODUCTION == Environment.getEnvironment('prod')
        assert !Environment.getEnvironment('doesntexist')
    }

    void testSystemPropertyOverridesMetadata() {
        Metadata.getInstance(new ByteArrayInputStream('griffon.env=production'.bytes))

        assert Environment.PRODUCTION == Environment.getCurrent()

        System.setProperty('griffon.env', 'dev')

        assert Environment.DEVELOPMENT == Environment.getCurrent()

        System.setProperty('griffon.env', '')

        assert Environment.PRODUCTION == Environment.getCurrent()

        Metadata.getInstance(new ByteArrayInputStream(''.bytes))

        assert Environment.DEVELOPMENT == Environment.getCurrent()
    }
}
