/*
 * Copyright 2008-2015 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Holds the configuration of an MVC group
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface MVCGroupConfiguration {
    /**
     * Returns the type of this group.
     *
     * @return the type of the group.
     */
    @Nonnull
    String getMvcType();

    /**
     * Returns a Map with the names of all members keyed by type.
     *
     * @return a Map of all configured members as defined by the application's configuration or and addon contribution.
     */
    @Nonnull
    Map<String, String> getMembers();

    /**
     * Returns a Map with additional configuration for this group.
     *
     * @return a Map with additional configuration for this group.
     */
    @Nonnull
    Map<String, Object> getConfig();

    /**
     * Creates a new MVCGroup instance based in this configuration.
     * The group's id will should be set to the group's type and an empty Map should be used as the additional arguments.
     *
     * @return a newly instantiated MVCGroup
     */
    @Nonnull
    MVCGroup create();

    /**
     * Creates a new MVCGroup instance based in this configuration.
     * An empty Map should be used as the additional arguments.
     *
     * @param mvcId the id to assign to this group
     * @return a newly instantiated MVCGroup
     */
    @Nonnull
    MVCGroup create(@Nullable String mvcId);

    /**
     * Creates a new MVCGroup instance based in this configuration.
     * The group's id will should be set to the group's type.
     *
     * @param args additional arguments sent to each member when initializing
     * @return a newly instantiated MVCGroup
     */
    @Nonnull
    MVCGroup create(@Nonnull Map<String, Object> args);

    /**
     * Creates a new MVCGroup instance based in this configuration.
     *
     * @param mvcId the id to assign to this group
     * @param args  additional arguments sent to each member when initializing
     * @return a newly instantiated MVCGroup
     */
    @Nonnull
    MVCGroup create(@Nullable String mvcId, @Nonnull Map<String, Object> args);
}
