/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.javafx.controller;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadata;
import griffon.core.properties.PropertyChangeEvent;
import griffon.core.threading.UIThreadManager;
import griffon.javafx.support.JavaFXAction;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.image.Image;
import org.codehaus.griffon.runtime.core.controller.AbstractAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.application.converter.Converter;
import javax.application.converter.ConverterRegistry;

import static griffon.util.GriffonNameUtils.isNotBlank;
import static griffon.util.TypeUtils.castToBoolean;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class JavaFXGriffonControllerAction extends AbstractAction {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ICON = "icon";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_GRAPHIC = "graphic";
    public static final String KEY_SELECTED = "selected";
    public static final String KEY_VISIBLE = "visible";
    public static final String KEY_ACCELERATOR = "accelerator";
    public static final String KEY_STYLECLASS = "styleClass";
    public static final String KEY_STYLE = "style";
    public static final String KEY_GRAPHICSTYLECLASS = "graphicStyleClass";
    public static final String KEY_GRAPHICSTYLE = "graphicStyle";

    private final JavaFXAction toolkitAction;
    private String description;
    private String icon;
    private String image;
    private Node graphic;
    private String accelerator;
    private String styleClass;
    private String style;
    private String graphicStyleClass;
    private String graphicStyle;
    private boolean selected;
    private boolean visible = true;

    public JavaFXGriffonControllerAction(@Nonnull final UIThreadManager uiThreadManager, @Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final ActionMetadata actionMetadata) {
        super(actionManager, controller, actionMetadata);
        requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be null");

        toolkitAction = createAction(actionManager, controller, actionMetadata.getActionName());
        toolkitAction.setOnAction(actionEvent -> actionManager.invokeAction(controller, actionMetadata.getActionName(), actionEvent));

        addPropertyChangeListener(evt -> uiThreadManager.executeInsideUIAsync(() -> handlePropertyChange(evt)));
    }

    protected JavaFXAction createAction(@Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final String actionName) {
        return new JavaFXAction();
    }

    protected void handlePropertyChange(@Nonnull PropertyChangeEvent<?> evt) {
        if (KEY_NAME.equals(evt.getPropertyName())) {
            toolkitAction.setName(String.valueOf(evt.getNewValue()));
        } else if (KEY_DESCRIPTION.equals(evt.getPropertyName())) {
            toolkitAction.setDescription(String.valueOf(evt.getNewValue()));
        } else if (KEY_ENABLED.equals(evt.getPropertyName())) {
            toolkitAction.setEnabled(castToBoolean(evt.getNewValue()));
        } else if (KEY_SELECTED.equals(evt.getPropertyName())) {
            toolkitAction.setSelected(castToBoolean(evt.getNewValue()));
        } else if (KEY_VISIBLE.equals(evt.getPropertyName())) {
            toolkitAction.setVisible(castToBoolean(evt.getNewValue()));
        } else if (KEY_ACCELERATOR.equals(evt.getPropertyName())) {
            String accelerator = (String) evt.getNewValue();
            if (isNotBlank(accelerator)) { toolkitAction.setAccelerator(accelerator); }
        } else if (KEY_STYLECLASS.equals(evt.getPropertyName())) {
            String styleClass = (String) evt.getNewValue();
            if (isNotBlank(styleClass)) { toolkitAction.setStyleClass(styleClass); }
        } else if (KEY_STYLE.equals(evt.getPropertyName())) {
            String style = (String) evt.getNewValue();
            if (isNotBlank(style)) { toolkitAction.setStyle(style); }
        } else if (KEY_ICON.equals(evt.getPropertyName())) {
            String icon = (String) evt.getNewValue();
            if (isNotBlank(icon)) { toolkitAction.setIcon(icon); }
        } else if (KEY_IMAGE.equals(evt.getPropertyName())) {
            Image image = (Image) evt.getNewValue();
            if (null != image) { toolkitAction.setImage(image); }
        } else if (KEY_GRAPHIC.equals(evt.getPropertyName())) {
            Node graphic = (Node) evt.getNewValue();
            if (null != graphic) { toolkitAction.setGraphic(graphic); }
        } else if (KEY_GRAPHICSTYLECLASS.equals(evt.getPropertyName())) {
            String graphicStyleClass = (String) evt.getNewValue();
            if (isNotBlank(graphicStyleClass)) { toolkitAction.setGraphicStyleClass(graphicStyleClass); }
        } else if (KEY_GRAPHICSTYLE.equals(evt.getPropertyName())) {
            String graphicStyle = (String) evt.getNewValue();
            if (isNotBlank(graphicStyle)) { toolkitAction.setGraphicStyle(graphicStyle); }
        }
    }

    @Nullable
    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(@Nullable String styleClass) {
        firePropertyChange(KEY_STYLECLASS, this.styleClass, this.styleClass = styleClass);
    }

    @Nullable
    public String getStyle() {
        return style;
    }

    public void setStyle(@Nullable String style) {
        firePropertyChange(KEY_STYLE, this.style, this.style = style);
    }

    @Nullable
    public String getGraphicStyleClass() {
        return graphicStyleClass;
    }

    public void setGraphicStyleClass(@Nullable String graphicStyleClass) {
        firePropertyChange(KEY_GRAPHICSTYLECLASS, this.graphicStyleClass, this.graphicStyleClass = graphicStyleClass);
    }

    @Nullable
    public String getGraphicStyle() {
        return graphicStyle;
    }

    public void setGraphicStyle(@Nullable String graphicStyle) {
        firePropertyChange(KEY_GRAPHICSTYLE, this.graphicStyle, this.graphicStyle = graphicStyle);
    }

    @Nullable
    public String getAccelerator() {
        return accelerator;
    }

    public void setAccelerator(@Nullable String accelerator) {
        firePropertyChange(KEY_ACCELERATOR, this.accelerator, this.accelerator = accelerator);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        firePropertyChange(KEY_SELECTED, this.selected, this.selected = selected);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        firePropertyChange(KEY_SELECTED, this.visible, this.visible = visible);
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        firePropertyChange(KEY_DESCRIPTION, this.description, this.description = description);
    }

    @Nullable
    public String getIcon() {
        return icon;
    }

    public void setIcon(@Nullable String icon) {
        firePropertyChange(KEY_ICON, this.icon, this.icon = icon);
    }

    @Nullable
    public Image getImage() {
        ConverterRegistry converterRegistry = getController().getApplication()
            .getInjector().getInstance(ConverterRegistry.class);
        Converter<Image> converter = converterRegistry.findConverter(Image.class);
        if (converter != null) {
            return converter.fromObject(image);
        }
        return null;
    }

    public void setImage(@Nullable String image) {
        firePropertyChange(KEY_IMAGE, this.image, this.image = image);
    }

    @Nullable
    public Node getGraphic() {
        return graphic;
    }

    public void setGraphic(@Nullable Node graphic) {
        firePropertyChange(KEY_ICON, this.graphic, this.graphic = graphic);
    }

    @Nonnull
    public Object getToolkitAction() {
        return toolkitAction;
    }

    @Override
    protected void doExecute(Object... args) {
        ActionEvent event = null;
        if (args != null && args.length == 1 && args[0] instanceof ActionEvent) {
            event = (ActionEvent) args[0];
        }
        toolkitAction.onActionProperty().get().handle(event);
    }

    @Override
    protected void doInitialize() {
        toolkitAction.setName(getName());
        toolkitAction.setDescription(getDescription());
        toolkitAction.setEnabled(isEnabled());
        toolkitAction.setSelected(isSelected());
        toolkitAction.setVisible(isVisible());
        String accelerator = getAccelerator();
        if (isNotBlank(accelerator)) { toolkitAction.setAccelerator(accelerator); }
        if (isNotBlank(style)) { toolkitAction.setStyle(style); }
        if (isNotBlank(styleClass)) { toolkitAction.setStyleClass(styleClass); }
        String icon = getIcon();
        if (isNotBlank(icon)) { toolkitAction.setIcon(icon); }
        if (null != getImage()) { toolkitAction.setImage(getImage()); }
        if (null != getGraphic()) { toolkitAction.setGraphic(getGraphic()); }
        if (isNotBlank(graphicStyle)) { toolkitAction.setGraphicStyle(graphicStyle); }
        if (isNotBlank(graphicStyleClass)) { toolkitAction.setGraphicStyleClass(graphicStyleClass); }
    }
}
