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
package griffon.core.mvc;

import griffon.core.Context;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Defines an MVC group and its contents
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface MVCGroup extends MVCHandler {
    /**
     * Returns the configuration of this group.
     *
     * @return the configuration used to instantiate this group.
     */
    @Nonnull
    MVCGroupConfiguration getConfiguration();

    /**
     * Returns the type of this group as set in the application's configuration.
     *
     * @return the type of the group
     */
    @Nonnull
    String getMvcType();

    /**
     * Returns the id of the group.
     * Ids are used to uniquely identify a group instance.
     *
     * @return the id of this group.
     */
    @Nonnull
    String getMvcId();

    /**
     * Returns the Model portion of this group.
     *
     * @return a GriffonModel instance if the group has a model member, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    @Nullable
    GriffonModel getModel();

    /**
     * Returns the View portion of this group.
     *
     * @return a GriffonView instance if the group has a view member, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    @Nullable
    GriffonView getView();

    /**
     * Returns the Controller portion of this group.
     *
     * @return a GriffonController instance if the group has a controller member, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    @Nullable
    GriffonController getController();

    /**
     * Returns the specified member type.
     *
     * @param name the type of member to retrieve
     * @return the selected MVC member if a match is found, null otherwise
     * @throws IllegalStateException if the group has been destroyed already
     */
    @Nullable
    Object getMember(@Nonnull String name);

    /**
     * Returns a read-only view of all instance members.
     *
     * @return an unmodifiable Map view of all members.
     * @throws IllegalStateException if the group has been destroyed already
     */
    @Nonnull
    Map<String, Object> getMembers();

    /**
     * Destroys the current group.
     * <strong>Should only be called once.</strong>
     *
     * @throws IllegalStateException if the group has been destroyed already
     */
    void destroy();

    /**
     * Returns whether this group has been destroyed or not.
     *
     * @return true if the group has not been destroyed yet, false otherwise
     */
    boolean isAlive();

    /**
     * Returns the context belonging to this group.
     *
     * @since 2.2.0
     */
    @Nonnull
    Context getContext();

    /**
     * Returns the parent {@code MVCGroup} of this group.
     * <p>The parent may be null if this is a top level MVC group.</p>
     *
     * @since 2.2.0
     */
    @Nullable
    MVCGroup getParentGroup();

    /**
     * Returns an immutable view of all children groups for which this group is a parent
     *
     * @return an immutable view of children groups, keyed by mvcId.
     * @since 2.4.0
     */
    @Nonnull
    Map<String, MVCGroup> getChildrenGroups();

    /**
     * Notifies the group when another MVC group has been destroyed, potentially
     * a child group.
     *
     * @param mvcId the id of the group that has been destroyed
     * @since 2.4.0
     */
    void notifyMVCGroupDestroyed(@Nonnull String mvcId);
}
