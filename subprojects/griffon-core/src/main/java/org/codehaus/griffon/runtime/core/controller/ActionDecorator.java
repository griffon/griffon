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
package org.codehaus.griffon.runtime.core.controller;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadata;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public class ActionDecorator implements Action {
    private final Action delegate;

    public ActionDecorator(@Nonnull Action delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    @Override
    public ActionMetadata getActionMetadata() {
        return delegate.getActionMetadata();
    }

    @Nonnull
    @Override
    public String getActionName() {
        return delegate.getActionName();
    }

    @Nonnull
    @Override
    public String getFullyQualifiedName() {
        return delegate.getFullyQualifiedName();
    }

    @Nullable
    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void setName(@Nullable String name) {
        delegate.setName(name);
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        delegate.setEnabled(enabled);
    }

    @Nonnull
    @Override
    public ActionManager getActionManager() {
        return delegate.getActionManager();
    }

    @Nonnull
    @Override
    public GriffonController getController() {
        return delegate.getController();
    }

    @Nonnull
    @Override
    public Object getToolkitAction() {
        return delegate.getToolkitAction();
    }

    @Override
    public void execute(Object... args) {
        delegate.execute(args);
    }

    @Override
    public void initialize() {
        delegate.initialize();
    }
}
