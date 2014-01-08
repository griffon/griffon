package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonView;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractJavaFXGriffonView;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonView.class)
public class SampleView extends AbstractJavaFXGriffonView {
    private SampleController controller;                                  //<1>
    private SampleModel model;                                            //<1>

    @FXML
    private TextField input;                                              //<2>
    @FXML
    private Label output;                                                 //<2>

    @Inject
    public SampleView(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setController(SampleController controller) {
        this.controller = controller;
    }

    public void setModel(SampleModel model) {
        this.model = model;
    }

    @Override
    public void initUI() {
        Stage stage = (Stage) getApplication().createApplicationContainer();
        stage.setTitle(getApplication().getApplicationConfiguration().getAsString("application.title"));
        stage.setWidth(400);
        stage.setHeight(120);
        stage.setScene(init());
        getApplication().getWindowManager().attach("mainWindow", stage);  //<3>
    }

    // build the UI
    private Scene init() {
        Scene scene = new Scene(new Group());
        scene.setFill(Color.WHITE);

        Node node = loadFromFXML();
        model.inputProperty().bindBidirectional(input.textProperty());
        model.outputProperty().bindBidirectional(output.textProperty());
        ((Group) scene.getRoot()).getChildren().addAll(node);
        connectActions(node, controller);                                 //<4>

        return scene;
    }
}