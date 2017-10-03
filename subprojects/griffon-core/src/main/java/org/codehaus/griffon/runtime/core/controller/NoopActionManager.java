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

import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionHandler;
import griffon.core.controller.ActionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static griffon.util.GriffonNameUtils.uncapitalize;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class NoopActionManager implements ActionManager {
    @Nonnull
    @Override
    public Map<String, Action> actionsFor(@Nonnull GriffonController controller) {
        return Collections.emptyMap();
    }

    @Nullable
    @Override
    public Action actionFor(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return null;
    }

    @Override
    public void createActions(@Nonnull GriffonController controller) {

    }

    @Nonnull
    @Override
    public String normalizeName(@Nonnull String actionName) {
        requireNonBlank(actionName, "Argument 'actionName' must not be blank");
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }
        return uncapitalize(actionName);
    }

    @Override
    public void updateActions() {

    }

    @Override
    public void updateActions(@Nonnull GriffonController controller) {

    }

    @Override
    public void updateAction(@Nonnull Action action) {

    }

    @Override
    public void updateAction(@Nonnull GriffonController controller, @Nonnull String actionName) {

    }

    @Override
    public void invokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, Object... args) {

    }

    @Override
    public void invokeAction(@Nonnull Action action, @Nonnull Object... args) {

    }

    @Override
    public void addActionHandler(@Nonnull ActionHandler actionHandler) {

    }
}
