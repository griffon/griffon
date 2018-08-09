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
package griffon.core.mvc;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.Context;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonView;
import griffon.exceptions.ArtifactNotFoundException;

import java.util.Map;

/**
 * Manages the configuration and instantiation of MVC groups.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface MVCGroupManager extends MVCHandler {
    /**
     * Creates an MVCConfiguration instance with the given arguments.
     *
     * @param mvcType the name of the MVC group
     * @param members members of the group
     * @param config  additional configuration required by the group
     * @return a ready-to-use MVCGroupConfiguration instance
     */
    @Nonnull
    MVCGroupConfiguration newMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, String> members, @Nonnull Map<String, Object> config);

    /**
     * Clones an existing MVCGroupConfiguration, optionally overriding additional config values.
     *
     * @param mvcType the name of the configuration to clone
     * @param config  additional config parameters to be set on the configuration
     * @return a ready-to-use MVCGroupConfiguration instance
     */
    @Nonnull
    MVCGroupConfiguration cloneMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, Object> config);

    /**
     * Creates a new MVCGroup instance.
     *
     * @param configuration the configuration of the group
     * @param mvcId         the id to use for the group
     * @param members       the instance members of the group
     * @param parentGroup   the parent group (if any)
     * @return a ready-to-use MVCGroup instance
     */
    @Nonnull
    MVCGroup newMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members, @Nullable MVCGroup parentGroup);

    @Nonnull
    Context newContext(@Nullable MVCGroup parentGroup);

    /**
     * Initializes this manager with the group configurations provided by the application and addons.
     *
     * @param configurations available group configurations
     */
    void initialize(@Nonnull Map<String, MVCGroupConfiguration> configurations);

    void addConfiguration(@Nonnull MVCGroupConfiguration configuration);

    void removeConfiguration(@Nonnull MVCGroupConfiguration configuration);

    void removeConfiguration(@Nonnull String name);

    @Nonnull
    Map<String, MVCGroupConfiguration> getConfigurations();

    @Nonnull
    Map<String, MVCGroup> getGroups();

    @Nonnull
    MVCGroupConfiguration findConfiguration(@Nonnull String mvcType);

    @Nullable
    MVCGroup findGroup(@Nonnull String mvcId);

    @Nullable
    <MVC extends TypedMVCGroup> MVC findTypedGroup(@Nonnull String mvcId);

    @Nullable
    MVCGroup getAt(@Nonnull String mvcId);

    /**
     * Returns all currently available model instances, keyed by group name.<p>
     *
     * @return a Map of all currently instantiated models.
     */
    @Nonnull
    Map<String, ? extends GriffonModel> getModels();

    /**
     * Returns all currently available view instances, keyed by group name.<p>
     *
     * @return a Map of all currently instantiated views.
     */
    @Nonnull
    Map<String, ? extends GriffonView> getViews();

    /**
     * Returns all currently available controller instances, keyed by group name.<p>
     *
     * @return a Map of all currently instantiated controllers.
     */
    @Nonnull
    Map<String, ? extends GriffonController> getControllers();

    GriffonApplication getApplication();

    /**
     * Finds a named controller.
     *
     * @param name the name of the group that holds the controller
     * @param type the type of the controller
     * @return the controller instance if found
     * @throws ArtifactNotFoundException if the named controller could not be found
     * @since 2.1.0
     */
    @Nonnull
    <C extends GriffonController> C getController(@Nonnull String name, @Nonnull Class<C> type) throws ArtifactNotFoundException;

    /**
     * Finds a named model.
     *
     * @param name the name of the group that holds the model
     * @param type the type of the model
     * @return the model instance if found
     * @throws ArtifactNotFoundException if the named model could not be found
     * @since 2.1.0
     */
    @Nonnull
    <M extends GriffonModel> M getModel(@Nonnull String name, @Nonnull Class<M> type) throws ArtifactNotFoundException;

    /**
     * Finds a named view.
     *
     * @param name the name of the group that holds the view
     * @param type the type of the view
     * @return the view instance if found
     * @throws ArtifactNotFoundException if the named view could not be found
     * @since 2.1.0
     */
    @Nonnull
    <V extends GriffonView> V getView(@Nonnull String name, @Nonnull Class<V> type) throws ArtifactNotFoundException;

    /**
     * Finds a named controller.
     *
     * @param name the name of the group that holds the controller
     * @param type the type of the controller
     * @return the controller instance if found
     * @since 2.1.0
     */
    @Nullable
    <C extends GriffonController> C findController(@Nonnull String name, @Nonnull Class<C> type);

    /**
     * Finds a named model.
     *
     * @param name the name of the group that holds the model
     * @param type the type of the model
     * @return the model instance if found
     * @since 2.1.0
     */
    @Nullable
    <M extends GriffonModel> M findModel(@Nonnull String name, @Nonnull Class<M> type);

    /**
     * Finds a named view.
     *
     * @param name the name of the group that holds the view
     * @param type the type of the view
     * @return the view instance if found
     * @since 2.1.0
     */
    @Nullable
    <V extends GriffonView> V findView(@Nonnull String name, @Nonnull Class<V> type);
}
