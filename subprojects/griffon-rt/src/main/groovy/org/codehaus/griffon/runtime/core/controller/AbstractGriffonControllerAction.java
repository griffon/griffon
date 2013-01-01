/*
 * Copyright 2010-2013 the original author or authors.
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

import griffon.core.GriffonController;
import griffon.core.controller.GriffonControllerAction;
import org.codehaus.griffon.runtime.core.AbstractObservable;

import java.lang.ref.WeakReference;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public abstract class AbstractGriffonControllerAction extends AbstractObservable implements GriffonControllerAction {
    private String name;
    private String shortDescription;
    private String longDescription;
    private String smallIcon;
    private String largeIcon;
    private String accelerator;
    private String mnemonic;
    private boolean enabled;
    private boolean selected;
    private WeakReference<GriffonController> controller;

    public AbstractGriffonControllerAction(GriffonController controller, String actionName) {
        this.controller = new WeakReference<GriffonController>(controller);
    }

    public GriffonController getController() {
        return controller.get();
    }

    public String getAccelerator() {
        return accelerator;
    }

    @Override
    public void setAccelerator(String accelerator) {
        firePropertyChange(KEY_ACCELERATOR, this.accelerator, this.accelerator = accelerator);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        firePropertyChange(KEY_ENABLED, this.enabled, this.enabled = enabled);
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    @Override
    public void setLargeIcon(String largeIcon) {
        firePropertyChange(KEY_LARGE_ICON, this.largeIcon, this.largeIcon = largeIcon);
    }

    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        firePropertyChange(KEY_LONG_DESCRIPTION, this.longDescription, this.longDescription = longDescription);
    }

    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public void setMnemonic(String mnemonic) {
        firePropertyChange(KEY_MNEMONIC, this.mnemonic, this.mnemonic = mnemonic);
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        firePropertyChange(KEY_NAME, this.name, this.name = name);
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        firePropertyChange(KEY_SELECTED, this.selected, this.selected = selected);
    }

    public String getShortDescription() {
        return shortDescription;
    }

    @Override
    public void setShortDescription(String shortDescription) {
        firePropertyChange(KEY_SHORT_DESCRIPTION, this.shortDescription, this.shortDescription = shortDescription);
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    @Override
    public void setSmallIcon(String smallIcon) {
        firePropertyChange(KEY_SMALL_ICON, this.smallIcon, this.smallIcon = smallIcon);
    }

    @Override
    public final void execute(Object... args) {
        if (isEnabled()) {
            doExecute(args);
        }
    }

    protected abstract void doExecute(Object[] args);
}
