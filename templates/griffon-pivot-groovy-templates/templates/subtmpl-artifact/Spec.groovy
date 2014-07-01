package ${project_package}

import griffon.pivot.test.GriffonPivotRule
import griffon.core.test.TestFor
import org.junit.Rule
import spock.lang.Specification

@TestFor(${project_class_name})
class ${project_class_name}Spec extends Specification {
    private ${project_class_name} ${artifact_type}

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule()

    void "This is a smoke test" () {
        expect:
            false
    }
}