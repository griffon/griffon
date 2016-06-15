package ${project_package}

import griffon.javafx.test.GriffonTestFXRule
import org.junit.Rule
import org.junit.Test

import org.junit.Assert.fail

class ${project_class_name}IntegrationTest {
    @Rule @JvmField
    val testfx: GriffonTestFXRule = GriffonTestFXRule("mainWindow")

    @Test
    fun smokeTest() {
        Assert.fail("Not yet implemented!")
    }
}
