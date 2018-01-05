/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.javafx.support.JavaFXAction;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.example.api.Repository;
import org.reactfx.EventStreams;

import javax.annotation.Nonnull;
import java.util.Collections;

import static griffon.javafx.support.JavaFXUtils.createJavaFXThreadProxyList;
import static griffon.util.GriffonNameUtils.isBlank;
import static org.example.State.DISABLED;
import static org.example.State.READY;

@ArtifactProviderFor(GriffonView.class)
public class ReactiveView extends AbstractJavaFXGriffonView {
    private ReactiveController controller;
    private ReactiveModel model;

    @FXML private TextField organization;
    @FXML private TextField limit;
    @FXML private Label total;
    @FXML private ProgressBar progress;
    @FXML private ListView<Repository> repositories;

    private BooleanProperty enabled = new SimpleBooleanProperty(this, "enabled", true);
    private BooleanProperty running = new SimpleBooleanProperty(this, "running", false);

    @MVCMember
    public void setController(@Nonnull ReactiveController controller) {
        this.controller = controller;
    }

    @MVCMember
    public void setModel(@Nonnull ReactiveModel model) {
        this.model = model;
    }

    @Override
    public void initUI() {
        Stage stage = (Stage) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        stage.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        stage.setScene(init());
        stage.sizeToScene();
        stage.setResizable(false);
        getApplication().getWindowManager().attach("mainWindow", stage);
    }

    // build the UI
    private Scene init() {
        Scene scene = new Scene(new Group());
        scene.setFill(Color.WHITE);
        scene.getStylesheets().add("bootstrapfx.css");

        Node node = loadFromFXML();
        connectActions(node, controller);
        if (node instanceof Parent) {
            scene.setRoot((Parent) node);
        } else {
            ((Group) scene.getRoot()).getChildren().addAll(node);
        }

        ObservableList<Repository> items = createJavaFXThreadProxyList(model.getRepositories().sorted());
        repositories.setItems(items);
        EventStreams.sizeOf(items).subscribe(v -> total.setText(String.valueOf(v)));

        organization.textProperty().addListener((observable, oldValue, newValue) -> {
            model.setState(isBlank(newValue) ? DISABLED : READY);
        });

        model.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case DISABLED:
                    enabled.setValue(false);
                    running.setValue(false);
                    break;
                case READY:
                    enabled.setValue(true);
                    running.setValue(false);
                    break;
                case RUNNING:
                    enabled.setValue(false);
                    running.setValue(true);
                    break;
            }
        });

        JavaFXAction loadAction = toolkitActionFor(controller, "load");
        JavaFXAction cancelAction = toolkitActionFor(controller, "cancel");

        loadAction.enabledProperty().bind(enabled);
        cancelAction.enabledProperty().bind(running);

        progress.visibleProperty().bind(cancelAction.enabledProperty());

        organization.textProperty().bindBidirectional(model.organizationProperty());
        limit.textProperty().bindBidirectional(model.limitProperty(), new NumberStringConverter());

        return scene;
    }
}