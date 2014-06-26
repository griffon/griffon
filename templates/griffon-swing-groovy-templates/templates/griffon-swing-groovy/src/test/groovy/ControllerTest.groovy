package ${project_package}

import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

@TestFor(${project_class_name}Controller)
class ${project_class_name}ControllerTest {
    private ${project_class_name}Controller controller

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Test
    void testClickAction() {
        fail('Not yet implemented!')
    }
}