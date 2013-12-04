package sample;


import griffon.core.GriffonApplication;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.core.artifact.AbstractJavaFXGriffonView;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SampleView extends AbstractJavaFXGriffonView {
    private SampleController controller;
    private SampleModel model;

    @FXML
    private TextField name;

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
        stage.setTitle("JavaFX");
        stage.setWidth(400);
        stage.setHeight(300);
        stage.setScene(init());
        getApplication().getWindowManager().attach("mainWindow", stage);
    }

    public void click() {
        getApplication().getActionManager().invokeAction(controller, "click", null);
    }

    // build the UI
    private Scene init() {
        Scene scene = new Scene(new Group());
        scene.setFill(Color.WHITE);

        /*
        Text javaText = new Text();
        javaText.setText("Java");
        javaText.setFont(new Font("Sanserif", 80));
        javaText.setFill(LinearGradientBuilder.create().endX(0)
            .stops(new Stop(0, Color.ORANGE), new Stop(1, Color.CHOCOLATE)).build());

        Text fxText = new Text();
        fxText.setText("FX");
        fxText.setFont(new Font("Sanserif", 80));
        fxText.setFill(LinearGradientBuilder.create().endX(0)
            .stops(new Stop(0, Color.CYAN), new Stop(1, Color.DODGERBLUE)).build());
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.DODGERBLUE);
        dropShadow.setRadius(25);
        dropShadow.setSpread(0.25);
        fxText.setEffect(dropShadow);

        Button button = new Button();
        JavaFXAction action = (JavaFXAction) getApplication().getActionManager().actionFor(controller, "click").getToolkitAction();
        JavaFXUtils.configure(button, action);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(javaText, fxText);
        hbox.setPadding(new Insets(80, 80, 80, 80));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(hbox, button);
        hbox.setPadding(new Insets(80, 80, 80, 80));

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        */

        Node content = loadFromFXML();
        model.inputProperty().bindBidirectional(name.textProperty());
        ((Group) scene.getRoot()).getChildren().addAll(content);

        return scene;
    }
}
