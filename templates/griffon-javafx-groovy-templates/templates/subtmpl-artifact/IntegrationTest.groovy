package ${project_package}

import griffon.javafx.test.GriffonTestFXRule
import org.junit.Rule
import org.junit.Test

import static org.junit.Assert.fail

class ${project_class_name}IntegrationTest {
    @Rule
    public GriffonTestFXRule testfx = new GriffonTestFXRule('mainWindow')

    @Test
    void smokeTest() {
        fail('Not implemented yet!')
    }
}
