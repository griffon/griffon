package org.example;

import griffon.core.artifact.GriffonView;
import griffon.javafx.support.fontawesome.FontAwesomeIcon;
import griffon.metadata.ArtifactProviderFor;
import griffon.plugins.fontawesome.FontAwesome;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;

@ArtifactProviderFor(GriffonView.class)
public class Tab2View extends AbstractJavaFXGriffonView {
    private SampleController controller;
    private SampleModel model;
    private AppView parentView;

    @FXML
    private TextField input;

    @FXML
    private Label output;

    @Override
    public void initUI() {
        Node node = loadFromFXML();
        model.inputProperty().bindBidirectional(input.textProperty());
        model.outputProperty().bindBidirectional(output.textProperty());
        connectActions(node, controller);

        Tab tab = new Tab("FXML");
        tab.setGraphic(new FontAwesomeIcon(FontAwesome.FA_COG));
        tab.setContent(node);
        tab.setClosable(false);

        parentView.getTabPane().getTabs().add(tab);
    }
}