package ${project_package}

import griffon.javafx.test.FunctionalJavaFXRunner
import griffon.javafx.test.GriffonTestFXClassRule
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FunctionalJavaFXRunner)
class ${project_class_name}FunctionalTest {
    @ClassRule
    public static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule("mainWindow")

    @Test
    void smokeTest() {
        fail("Not yet implemented!")
    }
}