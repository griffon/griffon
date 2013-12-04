/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.core.mvc;

import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Manages the applicationConfiguration and instantiation of MVC groups.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public interface MVCGroupManager extends MVCHandler {
    /**
     * Creates an MVCConfiguration instance with the given arguments.
     *
     * @param mvcType the name of the MVC group
     * @param members members of the group
     * @param config  additional applicationConfiguration required by the group
     * @return a ready-to-use MVCGroupConfiguration instance
     */
    @Nonnull
    MVCGroupConfiguration newMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, String> members, @Nonnull Map<String, Object> config);

    /**
     * Clones an existing MVCGroupConfiguration, optionally overriding additional config values.
     *
     * @param mvcType the name of the applicationConfiguration to clone
     * @param config  additional config parameters to be set on the applicationConfiguration
     * @return a ready-to-use MVCGroupConfiguration instance
     * @since 0.9.5
     */
    @Nonnull
    MVCGroupConfiguration cloneMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, Object> config);

    /**
     * Creates a new MVCGroup instance.
     *
     * @param configuration the applicationConfiguration of the group
     * @param mvcId         the id to use for the group
     * @param members       the instance members of the group
     * @return a ready-to-use MVCGroup instance
     */
    @Nonnull
    MVCGroup newMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members);

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
}
