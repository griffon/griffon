package ${project_package};

import griffon.test.javafx.FunctionalJavaFXRunner;
import griffon.test.javafx.GriffonTestFXClassRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(FunctionalJavaFXRunner.class)
public class ${project_class_name}FunctionalTest {
    @ClassRule
    public static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule("mainWindow");

    @Test
    public void smokeTest() {
        fail("Not yet implemented!");
    }
}