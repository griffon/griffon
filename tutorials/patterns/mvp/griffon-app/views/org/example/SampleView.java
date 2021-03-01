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
import griffon.annotations.inject.MVCMember;
import org.kordamp.jipsy.annotations.ServiceProviderFor;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;

import griffon.annotations.core.Nonnull;
import java.util.Collections;

@ServiceProviderFor(GriffonView.class)
public class SampleView extends AbstractJavaFXGriffonView {
    private SamplePresenter presenter;

    @FXML private TextField input;
    @FXML private Label output;

    @MVCMember
    public void setPresenter(@Nonnull SamplePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initUI() {
        Stage stage = (Stage) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        stage.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        stage.setWidth(400);
        stage.setHeight(120);
        stage.setScene(init());
        getApplication().getWindowManager().attach("mainWindow", stage);
    }

    // build the UI
    private Scene init() {
        Scene scene = new Scene(new Group());
        scene.setFill(Color.WHITE);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");

        Node node = loadFromFXML();
        ((Group) scene.getRoot()).getChildren().addAll(node);
        connectActions(node, presenter);                                       //<1>

        return scene;
    }

    @Nonnull
    public TextField getInput() {                                              //<2>
        return input;
    }

    @Nonnull
    public Label getOutput() {                                                 //<2>
        return output;
    }
}