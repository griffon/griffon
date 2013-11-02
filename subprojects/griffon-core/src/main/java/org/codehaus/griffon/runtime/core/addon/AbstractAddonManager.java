/*
 * Copyright 2009-2013 the original author or authors.
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

import griffon.core.addon.AddonManager;
import griffon.core.addon.GriffonAddon;
import griffon.core.addon.GriffonAddonDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.getPropertyName;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code AddonManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public abstract class AbstractAddonManager implements AddonManager {
    private static final String GRIFFON_ADDON_SUFFIX = "GriffonAddon";
    private static final String ERROR_NAME_BLANK = "Argument 'name' cannot be blank";
    private static final String ERROR_DESCRIPTOR_NULL = "Argument 'descriptor' is null";
    private final Map<String, GriffonAddon> addons = new LinkedHashMap<>();
    private final Map<String, GriffonAddonDescriptor> addonDescriptors = new LinkedHashMap<>();
    private final Object lock = new Object[0];
    private boolean initialized;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAddonManager.class);

    @Nonnull
    public Map<String, GriffonAddon> getAddons() {
        return Collections.unmodifiableMap(addons);
    }

    @Nonnull
    public Map<String, GriffonAddonDescriptor> getAddonDescriptors() {
        return Collections.unmodifiableMap(addonDescriptors);
    }

    @Nullable
    public GriffonAddon findAddon(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        if (name.endsWith(GRIFFON_ADDON_SUFFIX)) {
            name = name.substring(0, name.length() - 12);
        }
        return addons.get(getPropertyName(name));
    }

    @Nullable
    public GriffonAddonDescriptor findAddonDescriptor(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        if (name.endsWith(GRIFFON_ADDON_SUFFIX)) {
            name = name.substring(0, name.length() - 12);
        }
        return addonDescriptors.get(getPropertyName(name));
    }

    @Nonnull
    public Map<String, GriffonAddonDescriptor> findAddonDescriptors(@Nonnull String prefix) {
        if ("root".equals(prefix) || isBlank(prefix))
            prefix = "";

        Map<String, GriffonAddonDescriptor> map = new LinkedHashMap<>();
        for (Map.Entry<String, GriffonAddonDescriptor> entry : addonDescriptors.entrySet()) {
            if (prefix.equals(entry.getValue().getPrefix())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public final void registerAddon(@Nonnull GriffonAddonDescriptor descriptor) {
        requireNonNull(descriptor, ERROR_DESCRIPTOR_NULL);
        synchronized (lock) {
            if (!initialized) {
                LOG.debug("Registering addon descriptor " + descriptor);
                doRegisterAddon(descriptor);
                addons.put(descriptor.getPluginName(), descriptor.getAddon());
            }
        }
    }

    public final void initialize() {
        synchronized (lock) {
            if (!initialized) {
                doInitialize();
                initialized = true;
            }
        }
    }

    protected abstract void doInitialize();

    protected abstract void doRegisterAddon(@Nonnull GriffonAddonDescriptor addonDescriptor);

    @Nonnull
    protected Map<String, GriffonAddonDescriptor> getAddonsInternal() {
        return addonDescriptors;
    }
}
