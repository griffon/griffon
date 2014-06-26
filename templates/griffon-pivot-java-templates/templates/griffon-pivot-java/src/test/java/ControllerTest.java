package ${project_package};

import griffon.pivot.test.GriffonPivotRule;
import griffon.core.test.TestFor;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.fail;

@TestFor(${project_class_name}Controller.class)
public class ${project_class_name}ControllerTest {
    private ${project_class_name}Controller controller;

    @Rule
    public final GriffonPivotRule griffon = new GriffonPivotRule();

    @Test
    public void testClickAction() {
        fail("Not yet implemented!");
    }
}