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
package griffon.core.action;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface Action {
    String PROPERTY_ID = "id";
    String PROPERTY_NAME = "name";
    String PROPERTY_ENABLED = "enabled";

    /**
     * Returns the metadata associated with this action.
     *
     * @return a non-null value.
     */
    @Nonnull
    ActionMetadata getActionMetadata();

    /**
     * Returns the owner of this action, this will typically be a (@code Controller} or {@code Presenter}.
     *
     * @return a non-null value.
     */
    @Nonnull
    Object getOwner();

    /**
     * Returns the id of this action.
     * The value for this property must not be null.
     *
     * @return the id of this action.
     */
    @Nonnull
    String getId();

    /**
     * Sets the id of this action.
     *
     * @param id the id of this action. Must not be {@code null}, empty, nor blank.
     */
    void setId(@Nonnull String id);

    /**
     * Returns the name of this action.
     * The value for this property may be empty but not null.
     *
     * @return the name of this action.
     */
    @Nonnull
    String getName();

    /**
     * Sets the name of this action.
     *
     * @param name the name of this action. Must not be {@code null} but may be emtpy.
     */
    void setName(@Nonnull String name);

    /**
     * Finds ouf if this action is enabled or not.
     *
     * @return {@code true} if the action is enabled, {@code false} otherwise.
     */
    boolean isEnabled();

    /**
     * Toggles the enabled tate of this action.
     *
     * @param enabled {@code true} to enabled, {@code false} to disable.
     */
    void setEnabled(boolean enabled);

    /**
     * Executes the action with the given arguments.
     * Executing an action in this way will trigger all {@code ActionHandler}s that may be registered with the application.
     *
     * @param args a set of arguments. Mey be {@code null}.
     */
    void execute(@Nullable Object... args);
}
