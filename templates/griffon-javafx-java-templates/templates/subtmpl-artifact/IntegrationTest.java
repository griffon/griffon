package ${project_package};

import griffon.javafx.test.GriffonTestFXRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ${project_class_name}IntegrationTest {
    @Rule
    public GriffonTestFXRule testfx = new GriffonTestFXRule("mainWindow");

    @Test
    public void smokeTest(){
        fail("Not yet implemented!");
    }
}