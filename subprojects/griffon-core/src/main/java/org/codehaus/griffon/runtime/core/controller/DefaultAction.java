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
package org.codehaus.griffon.runtime.core.controller;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadata;
import griffon.core.threading.UIThreadManager;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultAction extends AbstractAction {
    private final DefaultToolkitAction toolkitAction;

    public DefaultAction(@Nonnull final UIThreadManager uiThreadManager, @Nonnull final ActionManager actionManager, @Nonnull final GriffonController controller, @Nonnull final ActionMetadata actionMetadata) {
        super(actionManager, controller, actionMetadata);
        requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be null");

        toolkitAction = new DefaultToolkitAction(() -> actionManager.invokeAction(controller, actionMetadata.getActionName()));
        addPropertyChangeListener(evt -> uiThreadManager.runInsideUIAsync(() -> toolkitAction.setName(String.valueOf(evt.getNewValue()))));
    }

    @Nonnull
    @Override
    public Object getToolkitAction() {
        return toolkitAction;
    }

    @Override
    protected void doExecute(Object... args) {
        toolkitAction.execute();
    }

    @Override
    protected void doInitialize() {
        toolkitAction.setName(getName());
    }
}
