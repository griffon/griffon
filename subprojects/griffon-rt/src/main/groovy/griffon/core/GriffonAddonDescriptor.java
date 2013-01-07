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
package griffon.core;

/**
 * Describes an Addon artifact.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public interface GriffonAddonDescriptor {
    /**
     * Returns the prefix for this Addon. May be empty.
     *
     * @return the Addon's prefix.
     */
    String getPrefix();

    /**
     * Returns the name of the Addon.
     *
     * @return the Addon's name
     */
    String getName();

    /**
     * Returns the plugin name associated with this Addon
     *
     * @return the plugin's name
     */
    String getPluginName();

    /**
     * Returns the plugin version associated with this Addon
     *
     * @return the plugin's version
     */
    String getVersion();

    /**
     * Returns the Addon instance
     *
     * @return the Addon itself
     */
    GriffonAddon getAddon();
}
