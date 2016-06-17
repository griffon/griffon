/*
 * Copyright 2016 the original author or authors.
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