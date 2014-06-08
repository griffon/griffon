package ${project_package}

import griffon.pivot.test.GriffonPivotRule
import griffon.core.test.TestFor
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

@TestFor(${project_capitalized_name}Controller)
class ${project_capitalized_name}ControllerTest {
    private ${project_capitalized_name}Controller controller

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule()

    @Test
    void testClickAction() {
        fail('Not yet implemented!')
    }
}