/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.core.controller;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionManager;
import griffon.core.threading.UIThreadManager;
import griffon.javafx.support.JavaFXAction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.TypeUtils.castToBoolean;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class JavaFXGriffonControllerAction extends AbstractAction {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ICON = "icon";
    public static final String KEY_SELECTED = "selected";
    public static final String KEY_ACCELERATOR = "accelerator";

    private String description;
    private String icon;
    private String accelerator;
    private boolean selected;

    private final JavaFXAction toolkitAction;

    public JavaFXGriffonControllerAction(final @Nonnull UIThreadManager uiThreadManager, @Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final String actionName) {
        super(actionManager, controller, actionName);
        requireNonNull(uiThreadManager, "Argument 'uiThreadManager' cannot be null");

        toolkitAction = new JavaFXAction();
        toolkitAction.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                actionManager.invokeAction(controller, actionName, actionEvent);
            }
        });
        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                uiThreadManager.runInsideUIAsync(new Runnable() {
                    public void run() {
                        if (KEY_NAME.equals(evt.getPropertyName())) {
                            toolkitAction.setName(String.valueOf(evt.getNewValue()));
                        } else if (KEY_DESCRIPTION.equals(evt.getPropertyName())) {
                            toolkitAction.setDescription(String.valueOf(evt.getNewValue()));
                        } else if (KEY_ENABLED.equals(evt.getPropertyName())) {
                            toolkitAction.setEnabled(castToBoolean(evt.getNewValue()));
                        } else if (KEY_SELECTED.equals(evt.getPropertyName())) {
                            toolkitAction.setSelected(castToBoolean(evt.getNewValue()));
                        } else if (KEY_ACCELERATOR.equals(evt.getPropertyName())) {
                            String accelerator = (String) evt.getNewValue();
                            if (!isBlank(accelerator))
                                toolkitAction.setAccelerator(accelerator);
                        } else if (KEY_ICON.equals(evt.getPropertyName())) {
                            final String icon = (String) evt.getNewValue();
                            if (!isBlank(icon)) toolkitAction.setIcon(icon);
                        }
                    }
                });
            }
        });
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

    @Nonnull
    public Object getToolkitAction() {
        return toolkitAction;
    }

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
        String accelerator = getAccelerator();
        if (!isBlank(accelerator)) toolkitAction.setAccelerator(accelerator);
        String icon = getIcon();
        if (!isBlank(icon)) toolkitAction.setIcon(icon);
    }
}
