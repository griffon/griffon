/*
 * Copyright 2016-2017 the original author or authors.
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
import griffon.javafx.support.JavaFXAction;
import griffon.javafx.support.JavaFXUtils;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.annotation.Nonnull;

import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

@ArtifactProviderFor(GriffonView.class)
public class Tab1View extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull private SampleController controller;
    @MVCMember @Nonnull private SampleModel model;
    @MVCMember @Nonnull private AppView parentView;

    private StringProperty uiInput;
    private StringProperty uiOutput;

    @Override
    public void initUI() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(90.0);
        anchorPane.setPrefWidth(384.0);

        Label label = new Label(getApplication().getMessageSource().getMessage("name.label"));
        TextField input = new TextField();
        input.setPrefWidth(200.0);

        Button button = new Button();
        button.setPrefWidth(200.0);
        JavaFXUtils.configure(button, (JavaFXAction) actionFor(controller, "sayHello").getToolkitAction());

        Label output = new Label();
        label.setPrefWidth(360.0);

        uiInput = UIThreadAwareBindings.uiThreadAwareStringProperty(model.inputProperty());
        uiOutput = UIThreadAwareBindings.uiThreadAwareStringProperty(model.outputProperty());
        input.textProperty().bindBidirectional(uiInput);
        output.textProperty().bind(uiOutput);

        anchorPane.getChildren().addAll(label, input, button, output);

        setLeftAnchor(label, 14.0);
        setTopAnchor(label, 14.0);
        setLeftAnchor(input, 172.0);
        setTopAnchor(input, 11.0);
        setLeftAnchor(button, 172.0);
        setTopAnchor(button, 45.0);
        setLeftAnchor(output, 14.0);
        setTopAnchor(output, 80.0);

        Tab tab = new Tab("Java");
        tab.setGraphic(new FontIcon(FontAwesome.COFFEE));
        tab.setClosable(false);
        tab.setContent(anchorPane);
        parentView.getTabPane().getTabs().add(tab);
    }
}