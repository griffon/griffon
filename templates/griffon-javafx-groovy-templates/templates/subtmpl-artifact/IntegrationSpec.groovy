package ${project_package}

import griffon.javafx.test.GriffonTestFXRule
import org.junit.Rule
import spock.lang.Specification

class ${project_class_name}IntegrationSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public GriffonTestFXRule testfx = new GriffonTestFXRule('mainWindow')

    void "This is a smoke test" () {
        expect:
            false
    }
}