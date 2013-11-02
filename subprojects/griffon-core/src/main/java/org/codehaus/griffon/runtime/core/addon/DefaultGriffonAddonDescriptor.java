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

import javax.annotation.Nonnull;

/**
 * Base implementation of the GriffonAddonDescriptor interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultGriffonAddonDescriptor extends AbstractAddonDescriptor {
    public DefaultGriffonAddonDescriptor(@Nonnull String prefix, @Nonnull String name, @Nonnull String pluginName, @Nonnull String version, @Nonnull GriffonAddon addon) {
        super(prefix, name, pluginName, version, addon);
    }
}
