package ${project_package}

import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import javafx.embed.swing.JFXPanel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@TestFor(${project_class_name}::class)
class ${project_class_name}Test {
    init {
        // force initialization JavaFX Toolkit
        JFXPanel()
    }

    lateinit var ${artifact_type}: ${project_class_name}

    @Rule @JvmField
    val griffon:GriffonUnitRule = GriffonUnitRule()

    @Test
    fun smoke() {
        Assert.fail("Not yet implemented!")
    }
}