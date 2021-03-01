package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.core.artifact.GriffonView
import griffon.annotations.inject.MVCMember
import org.kordamp.jipsy.annotations.ServiceProviderFor
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.Window
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import griffon.annotations.core.Nonnull

@ServiceProviderFor(GriffonView::class)
class ${project_class_name}View : AbstractJavaFXGriffonView() {
    @set:[MVCMember Nonnull]
    lateinit var model: ${project_class_name}Model
    @set:[MVCMember Nonnull]
    lateinit var controller: ${project_class_name}Controller

    lateinit private @FXML var clickLabel: Label

    override fun initUI() {
        val stage: Stage = application.createApplicationContainer(mapOf()) as Stage
        stage.title = application.configuration.getAsString("application.title")
        stage.scene = _init()
        application.getWindowManager<Window>().attach("${name}", stage)
    }

    private fun _init(): Scene {
        val scene: Scene = Scene(Group())
        scene.fill = Color.WHITE

        val node = loadFromFXML()
        model.clickCountProperty().bindBidirectional(clickLabel.textProperty())
        (scene.root as Group).children.addAll(node)
        connectActions(node, controller)
        connectMessageSource(node)
        return scene
    }
}
