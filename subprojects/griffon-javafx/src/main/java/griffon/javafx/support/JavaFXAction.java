/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.javafx.support;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;

/**
 * @author Andres Almiray
 */
public class JavaFXAction {
    // -- onAction

    private ObjectProperty<EventHandler<ActionEvent>> onAction;

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        if (onAction == null) {
            onAction = new SimpleObjectProperty<>(this, "onAction");
        }
        return onAction;
    }

    public void setOnAction(EventHandler<ActionEvent> value) {
        onActionProperty().set(value);
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onAction == null ? null : onActionProperty().get();
    }

    // -- name

    private StringProperty name;

    public final StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    public String getName() {
        return name == null ? null : nameProperty().get();
    }

    // -- description

    private StringProperty description;

    public final StringProperty descriptionProperty() {
        if (description == null) {
            description = new SimpleStringProperty(this, "description");
        }
        return description;
    }

    public void setDescription(String description) {
        descriptionProperty().set(description);
    }

    public String getDescription() {
        return description == null ? null : descriptionProperty().get();
    }

    // -- enabled

    private BooleanProperty enabled;

    public final BooleanProperty enabledProperty() {
        if (enabled == null) {
            enabled = new SimpleBooleanProperty(this, "enabled", true);
        }
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        enabledProperty().set(enabled);
    }

    public boolean getEnabled() {
        return enabled != null && enabledProperty().get();
    }

    public boolean isEnabled() {
        return enabled != null && enabledProperty().get();
    }

    // -- accelerator

    private ObjectProperty<KeyCombination> accelerator;

    public void setAccelerator(String accelerator) {
        setAccelerator(KeyCombination.keyCombination(accelerator));
    }

    public final void setAccelerator(KeyCombination value) {
        acceleratorProperty().set(value);
    }

    public final KeyCombination getAccelerator() {
        return accelerator == null ? null : accelerator.get();
    }

    public final ObjectProperty<KeyCombination> acceleratorProperty() {
        if (accelerator == null) {
            accelerator = new SimpleObjectProperty<>(this, "accelerator");
        }
        return accelerator;
    }

    // -- icon

    private StringProperty icon;

    public final StringProperty iconProperty() {
        if (icon == null) {
            icon = new SimpleStringProperty(this, "icon");
        }
        return icon;
    }

    public void setIcon(String icon) {
        iconProperty().set(icon);
    }

    public String getIcon() {
        return icon == null ? null : iconProperty().get();
    }

    // -- image

    private ObjectProperty<Image> image;

    public final ObjectProperty<Image> imageProperty() {
        if (image == null) {
            image = new SimpleObjectProperty<>(this, "image");
        }
        return image;
    }

    public void setImage(Image image) {
        imageProperty().set(image);
    }

    public Image getImage() {
        return image == null ? null : imageProperty().get();
    }

    // -- graphic

    private ObjectProperty<Node> graphic;

    public final ObjectProperty<Node> graphicProperty() {
        if (graphic == null) {
            graphic = new SimpleObjectProperty<>(this, "graphic");
        }
        return graphic;
    }

    public void setGraphic(Node graphic) {
        graphicProperty().set(graphic);
    }

    public Node getGraphic() {
        return graphic == null ? null : graphicProperty().get();
    }

    // -- selected

    private BooleanProperty selected;

    public final BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(this, "selected");
        }
        return selected;
    }

    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    public boolean getSelected() {
        return selected != null && selectedProperty().get();
    }

    public boolean isSelected() {
        return selected != null && selectedProperty().get();
    }

    // -- visible

    private BooleanProperty visible;

    public final BooleanProperty visibleProperty() {
        if (visible == null) {
            visible = new SimpleBooleanProperty(this, "visible", true);
        }
        return visible;
    }

    public void setVisible(boolean visible) {
        visibleProperty().set(visible);
    }

    public boolean getVisible() {
        return visible != null && visibleProperty().get();
    }

    public boolean isVisible() {
        return visible != null && visibleProperty().get();
    }

    // -- styleClass

    private StringProperty styleClass;

    public final StringProperty styleClassProperty() {
        if (styleClass == null) {
            styleClass = new SimpleStringProperty(this, "styleClass");
        }
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        styleClassProperty().set(styleClass);
    }

    public String getStyleClass() {
        return styleClass == null ? null : styleClassProperty().get();
    }

    // -- style

    private StringProperty style;

    public final StringProperty styleProperty() {
        if (style == null) {
            style = new SimpleStringProperty(this, "style");
        }
        return style;
    }

    public void setStyle(String style) {
        styleProperty().set(style);
    }

    public String getStyle() {
        return style == null ? null : styleProperty().get();
    }

    // -- graphicStyleClass

    private StringProperty graphicStyleClass;

    public final StringProperty graphicStyleClassProperty() {
        if (graphicStyleClass == null) {
            graphicStyleClass = new SimpleStringProperty(this, "graphicStyleClass");
        }
        return graphicStyleClass;
    }

    public void setGraphicStyleClass(String graphicStyleClass) {
        graphicStyleClassProperty().set(graphicStyleClass);
    }

    public String getGraphicStyleClass() {
        return graphicStyleClass == null ? null : graphicStyleClassProperty().get();
    }

    // -- graphicStyle

    private StringProperty graphicStyle;

    public final StringProperty graphicStyleProperty() {
        if (graphicStyle == null) {
            graphicStyle = new SimpleStringProperty(this, "graphicStyle");
        }
        return graphicStyle;
    }

    public void setGraphicStyle(String graphicStyle) {
        graphicStyleProperty().set(graphicStyle);
    }

    public String getGraphicStyle() {
        return graphicStyle == null ? null : graphicStyleProperty().get();
    }
}
