/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core.controller;

import griffon.core.CallableWithArgs;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionManager;
import griffon.core.threading.UIThreadManager;
import griffon.swing.SwingAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class SwingGriffonControllerAction extends AbstractAction {
    private static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    private static final String KEY_LONG_DESCRIPTION = "longDescription";
    private static final String KEY_SMALL_ICON = "smallIcon";
    private static final String KEY_LARGE_ICON = "largeIcon";
    private static final String KEY_SELECTED = "selected";
    private static final String KEY_ACCELERATOR = "accelerator";
    private static final String KEY_MNEMONIC = "mnemonic";

    private String shortDescription;
    private String longDescription;
    private String smallIcon;
    private String largeIcon;
    private String accelerator;
    private String mnemonic;
    private boolean selected;
    private final SwingAction toolkitAction;

    public SwingGriffonControllerAction(final @Nonnull UIThreadManager uiThreadManager, @Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final String actionName) {
        super(actionManager, controller, actionName);
        requireNonNull(uiThreadManager, "Argument 'uiThreadManager' cannot be null");

        toolkitAction = new SwingAction(new CallableWithArgs<Void>() {
            public Void call(@Nonnull Object... args) {
                actionManager.invokeAction(controller, actionName, args);
                return null;
            }
        });

        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                uiThreadManager.runInsideUIAsync(new Runnable() {
                    public void run() {
                        if (KEY_NAME.equals(evt.getPropertyName())) {
                            toolkitAction.putValue(Action.NAME, evt.getNewValue());
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
                            if (!isBlank(mnemonic)) {
                                toolkitAction.putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(mnemonic).getKeyCode());
                            }
                        } else if (KEY_ACCELERATOR.equals(evt.getPropertyName())) {
                            String accelerator = (String) evt.getNewValue();
                            if (!isBlank(accelerator)) {
                                toolkitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
                            }
                        } else if (KEY_SMALL_ICON.equals(evt.getPropertyName())) {
                            String smallIcon = (String) evt.getNewValue();
                            if (!isBlank(smallIcon)) {
                                toolkitAction.putValue(Action.SMALL_ICON, new ImageIcon(smallIcon));
                            }
                        } else if (KEY_LARGE_ICON.equals(evt.getPropertyName())) {
                            String largeIcon = (String) evt.getNewValue();
                            if (!isBlank(largeIcon)) {
                                toolkitAction.putValue(Action.LARGE_ICON_KEY, new ImageIcon(largeIcon));
                            }
                        }
                    }
                });
            }
        });
    }

    protected void doInitialize() {
        toolkitAction.putValue(Action.NAME, getName());
        toolkitAction.putValue(Action.SHORT_DESCRIPTION, getShortDescription());
        toolkitAction.putValue(Action.LONG_DESCRIPTION, getLongDescription());
        toolkitAction.setEnabled(isEnabled());
        toolkitAction.putValue(Action.SELECTED_KEY, isSelected());
        String mnemonic = getMnemonic();
        if (!isBlank(mnemonic)) {
            toolkitAction.putValue(Action.MNEMONIC_KEY, KeyStroke.getKeyStroke(mnemonic).getKeyCode());
        }
        String accelerator = getAccelerator();
        if (!isBlank(accelerator)) {
            toolkitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
        }
        String smallIcon = getSmallIcon();
        if (!isBlank(smallIcon)) {
            toolkitAction.putValue(Action.SMALL_ICON, new ImageIcon(smallIcon));
        }
        String largeIcon = getLargeIcon();
        if (!isBlank(largeIcon)) {
            toolkitAction.putValue(Action.LARGE_ICON_KEY, new ImageIcon(largeIcon));
        }
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

    @Nonnull
    public Object getToolkitAction() {
        return toolkitAction;
    }

    protected void doExecute(Object... args) {
        ActionEvent event = null;
        if (args != null && args.length == 1 && args[0] instanceof ActionEvent) {
            event = (ActionEvent) args[0];
        }
        toolkitAction.actionPerformed(event);
    }
}
