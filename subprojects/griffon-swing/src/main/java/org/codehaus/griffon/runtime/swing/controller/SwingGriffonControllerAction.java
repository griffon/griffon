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
package org.codehaus.griffon.runtime.swing.controller;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadata;
import griffon.core.properties.PropertyChangeEvent;
import griffon.core.threading.UIThreadManager;
import griffon.swing.support.SwingAction;
import org.codehaus.griffon.runtime.core.controller.AbstractAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.application.converter.Converter;
import javax.application.converter.ConverterRegistry;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;

import static griffon.util.GriffonNameUtils.isNotBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SwingGriffonControllerAction extends AbstractAction {
    public static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    public static final String KEY_LONG_DESCRIPTION = "longDescription";
    public static final String KEY_SMALL_ICON = "smallIcon";
    public static final String KEY_LARGE_ICON = "largeIcon";
    public static final String KEY_SELECTED = "selected";
    public static final String KEY_ACCELERATOR = "accelerator";
    public static final String KEY_MNEMONIC = "mnemonic";
    public static final String KEY_COMMAND = "command";
    private final SwingAction toolkitAction;
    private String shortDescription;
    private String longDescription;
    private String smallIcon;
    private String largeIcon;
    private String accelerator;
    private String mnemonic;
    private String command;
    private boolean selected;

    public SwingGriffonControllerAction(@Nonnull final UIThreadManager uiThreadManager, @Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final ActionMetadata actionMetadata) {
        super(actionManager, controller, actionMetadata);
        requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be null");

        toolkitAction = createAction(actionManager, controller, actionMetadata.getActionName());

        addPropertyChangeListener(evt -> uiThreadManager.executeInsideUIAsync(() -> handlePropertyChange(evt)));
    }

    @Nonnull
    protected SwingAction createAction(@Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final String actionName) {
        return new SwingAction(args -> actionManager.invokeAction(controller, actionName, args));
    }

    protected void handlePropertyChange(@Nonnull PropertyChangeEvent<?> evt) {
        if (KEY_NAME.equals(evt.getPropertyName())) {
            toolkitAction.putValue(Action.NAME, evt.getNewValue());
        } else if (KEY_COMMAND.equals(evt.getPropertyName())) {
            toolkitAction.putValue(Action.ACTION_COMMAND_KEY, evt.getNewValue());
        } else if (KEY_SHORT_DESCRIPTION.equals(evt.getPropertyName())) {
            toolkitAction.putValue(Action.SHORT_DESCRIPTION, evt.getNewValue());
        } else if (KEY_LONG_DESCRIPTION.equals(evt.getPropertyName())) {
            toolkitAction.putValue(Action.LONG_DESCRIPTION, evt.getNewValue());
        } else if (KEY_ENABLED.equals(evt.getPropertyName())) {
            toolkitAction.setEnabled((Boolean) evt.getNewValue());
        } else if (KEY_SELECTED.equals(evt.getPropertyName())) {
            toolkitAction.putValue(Action.SELECTED_KEY, evt.getNewValue());
        } else if (KEY_MNEMONIC.equals(evt.getPropertyName())) {
            String mnemonic = (String) evt.getNewValue();
            if (isNotBlank(mnemonic)) {
                toolkitAction.putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(mnemonic).getKeyCode());
            }
        } else if (KEY_ACCELERATOR.equals(evt.getPropertyName())) {
            String accelerator = (String) evt.getNewValue();
            if (isNotBlank(accelerator)) {
                toolkitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
            }
        } else if (KEY_SMALL_ICON.equals(evt.getPropertyName())) {
            handleIcon(evt.getNewValue(), Action.SMALL_ICON);
        } else if (KEY_LARGE_ICON.equals(evt.getPropertyName())) {
            handleIcon(evt.getNewValue(), Action.LARGE_ICON_KEY);
        }
    }

    protected void handleIcon(@Nullable Object value, @Nonnull String key) {
        if (value != null) {
            Converter<Icon> converter = getController().getApplication()
                .getInjector().getInstance(ConverterRegistry.class)
                .findConverter(Icon.class);
            if (converter != null) {
                toolkitAction.putValue(key, converter.fromObject(value));
            }
        }
    }

    protected void doInitialize() {
        toolkitAction.putValue(Action.NAME, getName());
        toolkitAction.putValue(Action.ACTION_COMMAND_KEY, getCommand());
        toolkitAction.putValue(Action.SHORT_DESCRIPTION, getShortDescription());
        toolkitAction.putValue(Action.LONG_DESCRIPTION, getLongDescription());
        toolkitAction.setEnabled(isEnabled());
        toolkitAction.putValue(Action.SELECTED_KEY, isSelected());
        String mnemonic = getMnemonic();
        if (isNotBlank(mnemonic)) {
            toolkitAction.putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(mnemonic).getKeyCode());
        }
        String accelerator = getAccelerator();
        if (isNotBlank(accelerator)) {
            toolkitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
        }
        handleIcon(getSmallIcon(), Action.SMALL_ICON);
        handleIcon(getLargeIcon(), Action.LARGE_ICON_KEY);
    }

    @Nullable
    public String getAccelerator() {
        return accelerator;
    }

    public void setAccelerator(@Nullable String accelerator) {
        firePropertyChange(KEY_ACCELERATOR, this.accelerator, this.accelerator = accelerator);
    }

    @Nullable
    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(@Nullable String largeIcon) {
        firePropertyChange(KEY_LARGE_ICON, this.largeIcon, this.largeIcon = largeIcon);
    }

    @Nullable
    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(@Nullable String longDescription) {
        firePropertyChange(KEY_LONG_DESCRIPTION, this.longDescription, this.longDescription = longDescription);
    }

    @Nullable
    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(@Nullable String mnemonic) {
        firePropertyChange(KEY_MNEMONIC, this.mnemonic, this.mnemonic = mnemonic);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        firePropertyChange(KEY_SELECTED, this.selected, this.selected = selected);
    }

    @Nullable
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(@Nullable String shortDescription) {
        firePropertyChange(KEY_SHORT_DESCRIPTION, this.shortDescription, this.shortDescription = shortDescription);
    }

    @Nullable
    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(@Nullable String smallIcon) {
        firePropertyChange(KEY_SMALL_ICON, this.smallIcon, this.smallIcon = smallIcon);
    }

    @Nullable
    public String getCommand() {
        return command;
    }

    public void setCommand(@Nullable String command) {
        firePropertyChange(KEY_SMALL_ICON, this.command, this.command = command);
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
        toolkitAction.actionPerformed(event);
    }
}
