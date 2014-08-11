package ${project_package};

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonView;
import griffon.metadata.ArtifactProviderFor;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class ${project_class_name}View extends AbstractJavaFXGriffonView {
    private ${project_class_name}Controller controller;
    private ${project_class_name}Model model;

    @FXML
    private Label clickLabel;

    @Inject
    public ${project_class_name}View(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setController(${project_class_name}Controller controller) {
        this.controller = controller;
    }

    public void setModel(${project_class_name}Model model) {
        this.model = model;
    }

    @Override
    public void initUI() {
        Stage stage = (Stage) getApplication()
            .createApplicationContainer(Collections.<String,Object>emptyMap());
        stage.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        stage.setScene(init());
        stage.sizeToScene();
        getApplication().getWindowManager().attach("${name}", stage);
    }

    // build the UI
    private Scene init() {
        Scene scene = new Scene(new Group());
        scene.setFill(Color.WHITE);

        Node node = loadFromFXML();
        model.clickCountProperty().bindBidirectional(clickLabel.textProperty());
        ((Group) scene.getRoot()).getChildren().addAll(node);
        connectActions(node, controller);

        return scene;
    }
}
