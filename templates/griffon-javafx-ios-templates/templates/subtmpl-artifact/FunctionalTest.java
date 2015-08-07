package ${project_package};

import griffon.javafx.test.GriffonTestFXClassRule;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ${project_class_name}FunctionalTest {
    @ClassRule
    public static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule("mainWindow");

    @Test
    public void smokeTest() {
        fail("Not yet implemented!");
    }
}