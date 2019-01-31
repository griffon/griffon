/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.lanterna.controller;

import griffon.annotations.core.Nonnull;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadata;
import griffon.core.properties.PropertyChangeEvent;
import griffon.core.threading.UIThreadManager;
import griffon.lanterna.support.LanternaAction;
import org.codehaus.griffon.runtime.core.controller.AbstractAction;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class LanternaGriffonControllerAction extends AbstractAction {
    private final LanternaAction toolkitAction;

    public LanternaGriffonControllerAction(@Nonnull final UIThreadManager uiThreadManager, @Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final ActionMetadata actionMetadata) {
        super(actionManager, controller, actionMetadata);
        requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be null");

        toolkitAction = createAction(actionManager, controller, actionMetadata.getActionName());
        addPropertyChangeListener(evt -> uiThreadManager.executeInsideUIAsync(() -> handlePropertyChange(evt)));
    }

    @Nonnull
    protected LanternaAction createAction(@Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final String actionName) {
        return new LanternaAction((Runnable) () -> actionManager.invokeAction(controller, actionName));
    }

    protected void handlePropertyChange(@Nonnull PropertyChangeEvent<?> evt) {
        toolkitAction.setName(String.valueOf(evt.getNewValue()));
    }

    @Nonnull
    public Object getToolkitAction() {
        return toolkitAction;
    }

    @Override
    protected void doExecute(Object... args) {
        toolkitAction.doAction();
    }

    @Override
    protected void doInitialize() {
        toolkitAction.setName(getName());
    }
}
