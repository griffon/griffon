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
import griffon.javafx.beans.binding.UIThreadAwareBindings;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import griffon.annotations.core.Nonnull;

@ArtifactProviderFor(GriffonView.class)
public class Tab2View extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull private SampleController controller;
    @MVCMember @Nonnull private SampleModel model;
    @MVCMember @Nonnull private AppView parentView;

    @FXML private TextField input;
    @FXML private Label output;

    private StringProperty uiInput;
    private StringProperty uiOutput;

    @Override
    public void initUI() {
        Node node = loadFromFXML();
        uiInput = UIThreadAwareBindings.uiThreadAwareStringProperty(model.inputProperty());
        uiOutput = UIThreadAwareBindings.uiThreadAwareStringProperty(model.outputProperty());
        input.textProperty().bindBidirectional(uiInput);
        output.textProperty().bind(uiOutput);
        connectActions(node, controller);

        Tab tab = new Tab("FXML");
        tab.setGraphic(new FontIcon(FontAwesome.COG));
        tab.setContent(node);
        tab.setClosable(false);

        parentView.getTabPane().getTabs().add(tab);
    }
}