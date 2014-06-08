package ${project_package};

import griffon.core.test.GriffonUnitRule;
import griffon.core.test.TestFor;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.fail;

@TestFor(${project_capitalized_name}Controller.class)
public class ${project_capitalized_name}ControllerTest {
    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel();
    }

    private ${project_capitalized_name}Controller controller;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Test
    public void testClickAction() {
        fail("Not yet implemented!");
    }
}