/*
 * Copyright 2010-2012 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonAddon;
import griffon.core.GriffonAddonDescriptor;

/**
 * Base implementation of the GriffonAddonDescriptor interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public class DefaultGriffonAddonDescriptor implements GriffonAddonDescriptor {
    private final String name;
    private final String pluginName;
    private final String version;
    private final String prefix;
    private final GriffonAddon addon;

    public DefaultGriffonAddonDescriptor(String prefix, String name, String pluginName, String version, GriffonAddon addon) {
        this.prefix = prefix;
        this.name = name;
        this.pluginName = pluginName;
        this.version = version;
        this.addon = addon;
    }

    public String toString() {
        return name + " " + version;
    }

    public String getName() {
        return name;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getVersion() {
        return version;
    }

    public String getPrefix() {
        return prefix;
    }

    public GriffonAddon getAddon() {
        return addon;
    }
}
