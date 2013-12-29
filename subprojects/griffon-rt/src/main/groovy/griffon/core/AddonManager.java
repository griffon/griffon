/*
 * Copyright 2009-2014 the original author or authors.
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

package griffon.core;

import java.util.Map;

/**
 * Helper class capable of dealing with addons.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public interface AddonManager extends ApplicationHandler {
    void initialize();

    void registerAddon(GriffonAddonDescriptor addonDescriptor);

    /**
     * Returns a read-only view of all available addons
     *
     * @return a non-null Map of addons keyed by name
     */
    Map<String, GriffonAddon> getAddons();

    /**
     * Returns a read-only view of all available addon descriptors
     *
     * @return a non-null Map of addon descriptors keyed by name
     */
    Map<String, GriffonAddonDescriptor> getAddonDescriptors();

    /**
     * Finds an addon by name.<p>
     * Example: findAddon("miglayout") will return a GriffonAddon that
     * represents the contibutions of the MigLayout plugin.
     *
     * @param name the name of the addon to search for
     * @return an addon instance if there's a match, null otherwise
     */
    GriffonAddon findAddon(String name);

    /**
     * Finds an addon descriptor by name.<p>
     * Example: findAddonDescriptor("miglayout") will return a GriffonAddonDescriptor that
     * represents the contibutions of the MigLayout plugin.
     *
     * @param name the name of the addon to search for
     * @return an addon descriptor instance if there's a match, null otherwise
     */
    GriffonAddonDescriptor findAddonDescriptor(String name);

    /**
     * Finds all addon descriptors that are registered with the specified prefix.<p>
     * Example: findAddonDescriptors("foo") will return al addon descriptors that use 'foo as a prefix.<p>
     * A null or blank prefix should return all addon descriptors registered with the 'root'
     * prefix.
     *
     * @param prefix the prefix to search for
     * @return a non-null Map of matching addon descriptors keyed by name
     */
    Map<String, GriffonAddonDescriptor> findAddonDescriptors(String prefix);
}
