/*
 * Copyright 2009-2012 the original author or authors.
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

import griffon.core.AddonManager;
import griffon.core.GriffonAddon;
import griffon.core.GriffonAddonDescriptor;
import griffon.core.GriffonApplication;
import griffon.util.GriffonNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base implementation of the {@code AddonManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public abstract class AbstractAddonManager implements AddonManager {
    private final GriffonApplication app;

    private final Map<String, GriffonAddon> addons = new LinkedHashMap<String, GriffonAddon>();
    private final Map<String, GriffonAddonDescriptor> addonDescriptors = new LinkedHashMap<String, GriffonAddonDescriptor>();
    private final Object lock = new Object();
    private boolean initialized;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAddonManager.class);

    public AbstractAddonManager(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    public Map<String, GriffonAddon> getAddons() {
        return Collections.<String, GriffonAddon>unmodifiableMap(addons);
    }

    public Map<String, GriffonAddonDescriptor> getAddonDescriptors() {
        return Collections.<String, GriffonAddonDescriptor>unmodifiableMap(addonDescriptors);
    }

    public GriffonAddon findAddon(String name) {
        if (GriffonNameUtils.isBlank(name)) return null;
        if (name.endsWith("GriffonAddon")) name = name.substring(0, name.length() - 12);
        return addons.get(GriffonNameUtils.getPropertyName(name));
    }

    public GriffonAddonDescriptor findAddonDescriptor(String name) {
        if (GriffonNameUtils.isBlank(name)) return null;
        if (name.endsWith("GriffonAddon")) name = name.substring(0, name.length() - 12);
        return addonDescriptors.get(GriffonNameUtils.getPropertyName(name));
    }

    public Map<String, GriffonAddonDescriptor> findAddonDescriptors(String prefix) {
        if ("root".equals(prefix) || GriffonNameUtils.isBlank(prefix)) prefix = "";

        Map<String, GriffonAddonDescriptor> map = new LinkedHashMap<String, GriffonAddonDescriptor>();
        for (Map.Entry<String, GriffonAddonDescriptor> entry : addonDescriptors.entrySet()) {
            if (prefix.equals(entry.getValue().getPrefix())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    public final void registerAddon(GriffonAddonDescriptor addonDescriptor) {
        synchronized (lock) {
            if (!initialized) {
                LOG.debug("Registering addon descriptor " + addonDescriptor);
                doRegisterAddon(addonDescriptor);
                addons.put(addonDescriptor.getPluginName(), addonDescriptor.getAddon());
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

    protected abstract void doRegisterAddon(GriffonAddonDescriptor addonDescriptor);

    protected Map<String, GriffonAddonDescriptor> getAddonsInternal() {
        return addonDescriptors;
    }
}
