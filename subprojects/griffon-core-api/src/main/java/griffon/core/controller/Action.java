/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface Action {
    String KEY_ACTION_NAME = "actionName";
    String KEY_NAME = "name";
    String KEY_ENABLED = "enabled";

    /**
     * Returns the metadata of this action.
     *
     * @since 2.11.0
     */
    @Nonnull
    ActionMetadata getActionMetadata();

    @Nonnull
    String getActionName();

    /**
     * Returns the fully qualified name of this action.</p>
     * The value should be the controller's full class name and the action's name joined by a period.
     *
     * @return the fully qualified name of this action.
     *
     * @since 2.1.0
     */
    @Nonnull
    String getFullyQualifiedName();

    @Nullable
    String getName();

    void setName(@Nullable String name);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    @Nonnull
    ActionManager getActionManager();

    @Nonnull
    GriffonController getController();

    @Nonnull
    Object getToolkitAction();

    void execute(Object... args);

    void initialize();
}
