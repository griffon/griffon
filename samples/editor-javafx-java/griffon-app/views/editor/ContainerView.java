/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package editor;

import griffon.core.artifact.GriffonView;
import griffon.core.controller.Action;
import griffon.metadata.ArtifactProviderFor;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;

@ArtifactProviderFor(GriffonView.class)
public class ContainerView extends AbstractJavaFXGriffonView {
    private ContainerController controller;
    private ContainerModel model;

    @FXML
    private TabPane tabGroup;

    private FileChooser fileChooser;

    public void setController(ContainerController controller) {
        this.controller = controller;
    }

    public void setModel(ContainerModel model) {
        this.model = model;
    }

    @Nonnull
    public TabPane getTabGroup() {
        return tabGroup;
    }

    @Override
    public void initUI() {
        Stage stage = (Stage) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        stage.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        stage.setWidth(480);
        stage.setHeight(320);
        stage.setScene(init());
        getApplication().getWindowManager().attach("mainWindow", stage);

        fileChooser = new FileChooser();
        fileChooser.setTitle(getApplication().getConfiguration().getAsString("application.title", "Open File"));
    }

    // build the UI
    private Scene init() {
        Scene scene = new Scene(new Group());
        scene.setFill(Color.WHITE);

        Node node = loadFromFXML();
        ((Group) scene.getRoot()).getChildren().addAll(node);
        connectActions(node, controller);

        tabGroup.getSelectionModel().selectedItemProperty().addListener(model);

        Action saveAction = actionFor(controller, "save");
        model.getDocumentModel().addPropertyChangeListener("dirty", (e) -> saveAction.setEnabled((Boolean) e.getNewValue()));

        return scene;
    }

    @Nullable
    public File selectFile() {
        Window window = (Window) getApplication().getWindowManager().getStartingWindow();
        return fileChooser.showOpenDialog(window);
    }
}