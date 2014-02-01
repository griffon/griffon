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
import griffon.core.controller.Action;
import griffon.core.controller.ActionManager;
import org.codehaus.griffon.runtime.core.AbstractObservable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractAction extends AbstractObservable implements Action {
    private String name;
    private boolean enabled = true;
    private final ActionManager actionManager;
    private final GriffonController controller;
    private final String actionName;
    private boolean initialized;
    private final Object lock = new Object[0];

    public AbstractAction(@Nonnull ActionManager actionManager, @Nonnull GriffonController controller, @Nonnull String actionName) {
        this.actionManager = requireNonNull(actionManager, "Argument 'actionManager' cannot be null");
        this.controller = requireNonNull(controller, "Argument 'controller' cannot be null");
        this.actionName = requireNonBlank(actionName, "Argument 'actionName' cannot be blank");
    }

    @Nonnull
    public ActionManager getActionManager() {
        return actionManager;
    }

    @Nonnull
    public GriffonController getController() {
        return controller;
    }

    @Nonnull
    public String getActionName() {
        return actionName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        firePropertyChange(KEY_ENABLED, this.enabled, this.enabled = enabled);
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nullable String name) {
        firePropertyChange(KEY_NAME, this.name, this.name = name);
    }

    @Override
    public final void execute(Object... args) {
        if (isEnabled()) {
            doExecute(args);
        }
    }

    protected abstract void doExecute(Object... args);

    public final void initialize() {
        synchronized (lock) {
            if (initialized) return;
            doInitialize();
            initialized = true;
        }
    }

    protected abstract void doInitialize();
}
