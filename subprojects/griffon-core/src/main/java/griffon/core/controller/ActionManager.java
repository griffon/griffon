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
package griffon.core.controller;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.artifact.GriffonController;

import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface ActionManager {
    String ACTION = "Action";

    @Nonnull
    Map<String, Action> actionsFor(@Nonnull GriffonController controller);

    @Nullable
    Action actionFor(@Nonnull GriffonController controller, @Nonnull String actionName);

    void createActions(@Nonnull GriffonController controller);

    @Nonnull
    String normalizeName(@Nonnull String actionName);

    /**
     * Updates all actions currently configured.
     *
     * @since 2.1.0
     */
    void updateActions();

    /**
     * Updates all actions belonging to the supplied controller.
     *
     * @param controller the controller that owns the actions to be updated.
     *
     * @since 2.1.0
     */
    void updateActions(@Nonnull GriffonController controller);

    /**
     * Update the action's properties using registered {@code ActionHandler}s.
     *
     * @param action the action to be updated
     *
     * @since 2.1.0
     */
    void updateAction(@Nonnull Action action);

    /**
     * Update the action's properties using registered {@code ActionHandler}s.
     *
     * @param controller the controller that owns the action
     * @param actionName the action's name
     *
     * @since 2.1.0
     */
    void updateAction(@Nonnull GriffonController controller, @Nonnull String actionName);

    /**
     * Execute the action using registered {@code ActionHandler}s.
     *
     * @param controller the controller that owns the action
     * @param actionName the action's name
     * @param args       additional arguments to be sent to the action
     */
    void invokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, Object... args);

    /**
     * Execute the action using registered {@code ActionHandler}s.
     *
     * @param action the action to be invoked
     * @param args   additional arguments to be sent to the action
     *
     * @since 2.1.0
     */
    void invokeAction(@Nonnull Action action, @Nonnull Object... args);

    /**
     * Register an {@code ActionHandler} with this instance.
     *
     * @param actionHandler the handler to be added to this ActionManager
     *
     * @since 2.1.0
     */
    void addActionHandler(@Nonnull ActionHandler actionHandler);
}
