package ${project_package}

import griffon.javafx.test.GriffonTestFXClassRule
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class ${project_class_name}FunctionalSpec extends Specification {
    private static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule('mainWindow')

    void setupSpec() {
        testfx.setup()
    }

    void cleanupSpec() {
        testfx.cleanup()
    }

    void "This is a smoke test" () {
        expect:
        false
    }
}