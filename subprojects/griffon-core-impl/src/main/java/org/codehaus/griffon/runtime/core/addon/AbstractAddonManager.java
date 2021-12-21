/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.core.addon;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.core.addon.AddonManager;
import griffon.core.addon.GriffonAddon;
import griffon.core.event.Event;
import griffon.core.events.LoadAddonEndEvent;
import griffon.core.events.LoadAddonStartEvent;
import griffon.core.events.LoadAddonsEndEvent;
import griffon.core.events.LoadAddonsStartEvent;
import griffon.core.mvc.MVCGroupConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.AnnotationUtils.sortByDependencies;
import static griffon.util.CollectionUtils.reverse;
import static griffon.util.StringUtils.getPropertyName;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code AddonManager} interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractAddonManager implements AddonManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAddonManager.class);

    private static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";
    private final Map<String, GriffonAddon> addons = new LinkedHashMap<>();
    private final Object lock = new Object[0];
    private final GriffonApplication application;
    // @GuardedBy("lock")
    private boolean initialized;

    @Inject
    public AbstractAddonManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    public Map<String, GriffonAddon> getAddons() {
        return Collections.unmodifiableMap(addons);
    }

    @Nullable
    public GriffonAddon findAddon(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        if (name.endsWith(GriffonAddon.SUFFIX)) {
            name = name.substring(0, name.length() - 12);
        }
        return addons.get(getPropertyName(name));
    }

    public final void initialize() {
        synchronized (lock) {
            if (!initialized) {
                doInitialize();
                initialized = true;
            }
        }
    }

    protected void doInitialize() {
        LOG.debug("Loading addons [START]");

        Map<String, GriffonAddon> addons = preloadAddons();
        event(LoadAddonsStartEvent.of());

        for (Map.Entry<String, GriffonAddon> entry : addons.entrySet()) {
            String name = entry.getKey();
            GriffonAddon addon = entry.getValue();
            LOG.debug("Loading addon {} with class {}", name, addon.getClass().getName());
            event(LoadAddonStartEvent.of(getApplication(), name, addon));

            getApplication().getEventRouter().subscribe(addon);
            addMVCGroups(addon);
            addon.init(getApplication());

            this.addons.put(name, addon);
            event(LoadAddonEndEvent.of(getApplication(), name, addon));
            LOG.debug("Loaded addon {}", name);
        }

        for (GriffonAddon addon : reverse(addons.values())) {
            getApplication().addShutdownHandler(addon);
        }

        LOG.debug("Loading addons [END]");
        event(LoadAddonsEndEvent.of());
    }

    @Nonnull
    protected Map<String, GriffonAddon> preloadAddons() {
        Collection<GriffonAddon> addonInstances = getApplication().getInjector().getInstances(GriffonAddon.class);
        return sortByDependencies(addonInstances, GriffonAddon.SUFFIX, "addon");
    }

    @SuppressWarnings("unchecked")
    protected void addMVCGroups(@Nonnull GriffonAddon addon) {
        for (Map.Entry<String, Map<String, Object>> groupEntry : addon.getMvcGroups().entrySet()) {
            String type = groupEntry.getKey();
            LOG.debug("Adding MVC group {}", type);
            Map<String, Object> members = groupEntry.getValue();
            Map<String, Object> configMap = new LinkedHashMap<>();
            Map<String, String> membersCopy = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : members.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if ("config".equals(key) && entry.getValue() instanceof Map) {
                    configMap = (Map<String, Object>) entry.getValue();
                } else {
                    membersCopy.put(key, String.valueOf(entry.getValue()));
                }
            }
            MVCGroupConfiguration configuration = getApplication().getMvcGroupManager().newMVCGroupConfiguration(type, membersCopy, configMap);
            getApplication().getMvcGroupManager().addConfiguration(configuration);
        }
    }

    @Nonnull
    protected Map<String, GriffonAddon> getAddonsInternal() {
        return addons;
    }

    protected <E extends Event> void event(@Nonnull E event) {
        getApplication().getEventRouter().publishEvent(event);
    }
}
