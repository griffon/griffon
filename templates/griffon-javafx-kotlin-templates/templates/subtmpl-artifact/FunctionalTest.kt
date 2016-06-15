package ${project_package}

import griffon.javafx.test.FunctionalJavaFXRunner
import griffon.javafx.test.GriffonTestFXClassRule
import org.junit.Assert
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FunctionalJavaFXRunner::class)
class ${project_class_name}FunctionalTest {
    companion object {
        @ClassRule @JvmField
        val testfx: GriffonTestFXClassRule = GriffonTestFXClassRule("mainWindow")
    }

    @Test
    fun smokeTest() {
        Assert.fail("Not yet implemented!")
    }
}
