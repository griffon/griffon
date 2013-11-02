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

package org.codehaus.griffon.runtime.core.addon;

import griffon.core.addon.GriffonAddon;
import griffon.core.addon.GriffonAddonDescriptor;

import javax.annotation.Nonnull;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the GriffonAddonDescriptor interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractAddonDescriptor implements GriffonAddonDescriptor {
    protected static final String ERROR_PREFIX_BLANK = "Argument 'prefix' cannot be blank";
    protected static final String ERROR_NAME_BLANK = "Argument 'name' cannot be blank";
    protected static final String ERROR_PLUGIN_NAME_BLANK = "Argument 'pluginName' cannot be blank";
    protected static final String ERROR_VERSION_BLANK = "Argument 'version' cannot be blank";
    protected static final String ERROR_ADDON_NULL = "Argument 'addon' cannot be null";

    protected final String name;
    protected final String pluginName;
    protected final String version;
    protected final String prefix;
    protected final GriffonAddon addon;

    public AbstractAddonDescriptor(@Nonnull String prefix, @Nonnull String name, @Nonnull String pluginName, @Nonnull String version, @Nonnull GriffonAddon addon) {
        this.prefix = requireNonBlank(prefix, ERROR_PREFIX_BLANK);
        this.name = requireNonBlank(name, ERROR_NAME_BLANK);
        this.pluginName = requireNonBlank(pluginName, ERROR_PLUGIN_NAME_BLANK);
        this.version = requireNonBlank(version, ERROR_VERSION_BLANK);
        this.addon = requireNonNull(addon, ERROR_ADDON_NULL);
    }

    public String toString() {
        return name + " " + version;
    }

    @Nonnull
    @Override
    public String getPrefix() {
        return prefix;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getPluginName() {
        return pluginName;
    }

    @Nonnull
    @Override
    public String getVersion() {
        return version;
    }

    @Nonnull
    @Override
    public GriffonAddon getAddon() {
        return addon;
    }
}
