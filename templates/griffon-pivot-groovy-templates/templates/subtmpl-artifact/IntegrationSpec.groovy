package ${project_package}

import griffon.pivot.test.GriffonPivotFuncRule
import org.junit.Rule
import spock.lang.Specification

class ${project_class_name}IntegrationSpec extends Specification {
    @Rule
    public final GriffonPivotFuncRule pivot = new GriffonPivotFuncRule()

    void "This is a smoke test" () {
        expect:
            false
    }
}