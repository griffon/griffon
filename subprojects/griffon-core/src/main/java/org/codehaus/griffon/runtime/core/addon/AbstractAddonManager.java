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

import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.addon.AddonManager;
import griffon.core.addon.GriffonAddon;
import griffon.core.mvc.MVCGroupConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;
import java.util.*;

import static griffon.util.CollectionUtils.reverse;
import static griffon.util.GriffonClassUtils.getDependsOn;
import static griffon.util.GriffonClassUtils.mapInstancesByName;
import static griffon.util.GriffonNameUtils.getPropertyName;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code AddonManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public abstract class AbstractAddonManager implements AddonManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAddonManager.class);

    protected static final String GRIFFON_ADDON_SUFFIX = "GriffonAddon";
    private static final String ERROR_NAME_BLANK = "Argument 'name' cannot be blank";
    private final Map<String, GriffonAddon> addons = new LinkedHashMap<>();
    private final Object lock = new Object[0];
    @GuardedBy("lock")
    private boolean initialized;

    private final GriffonApplication application;

    @Inject
    public AbstractAddonManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
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
        if (name.endsWith(GRIFFON_ADDON_SUFFIX)) {
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
        LOG.info("Loading addons [START]");

        Map<String, GriffonAddon> addons = preloadAddons();
        event(ApplicationEvent.LOAD_ADDONS_START);

        for (Map.Entry<String, GriffonAddon> entry : addons.entrySet()) {
            String name = entry.getKey();
            GriffonAddon addon = entry.getValue();
            LOG.info("Loading addon {} with class {}", name, addon.getClass().getName());
            event(ApplicationEvent.LOAD_ADDON_START, asList(getApplication(), name, addon));

            getApplication().getEventRouter().addEventListener(addon);
            addMVCGroups(addon);
            addon.init(getApplication());

            event(ApplicationEvent.LOAD_ADDON_END, asList(getApplication(), name, addon));
            LOG.info("Loaded addon {}", name);
        }

        for (GriffonAddon addon : reverse(addons.values())) {
            getApplication().addShutdownHandler(addon);
        }

        LOG.info("Loading addons [END]");
        event(ApplicationEvent.LOAD_ADDONS_END);
    }

    @Nonnull
    protected Map<String, GriffonAddon> preloadAddons() {
        Collection<GriffonAddon> addonInstances = getApplication().getInjector().getInstances(GriffonAddon.class);
        Map<String, GriffonAddon> addons = mapInstancesByName(addonInstances, GRIFFON_ADDON_SUFFIX);

        Map<String, GriffonAddon> map = new LinkedHashMap<>();
        map.putAll(addons);

        List<GriffonAddon> sortedAddons = new ArrayList<>();
        Set<String> addedDeps = new LinkedHashSet<>();

        while (!map.isEmpty()) {
            int processed = 0;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Current addon order is " + addons.keySet());
            }

            for (Iterator<Map.Entry<String, GriffonAddon>> iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, GriffonAddon> entry = iter.next();
                String addonName = entry.getKey();
                String[] dependsOn = getDependsOn(entry.getValue());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Processing addon '" + addonName + "'");
                    LOG.debug("    depends on '" + Arrays.toString(dependsOn) + "'");
                }

                if (dependsOn.length != 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("  Checking addon '" + addonName + "' dependencies (" + dependsOn.length + ")");
                    }

                    boolean failedDep = false;
                    for (String dep : dependsOn) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("  Checking addon '" + addonName + "' dependencies: " + dep);
                        }
                        if (!addedDeps.contains(dep)) {
                            // dep not in the list yet, we need to skip adding this to the list for now
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("  Skipped addon '" + addonName + "', since dependency '" + dep + "' not yet added");
                            }
                            failedDep = true;
                            break;
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("  Addon '" + addonName + "' dependency '" + dep + "' already added");
                            }
                        }
                    }

                    if (failedDep) {
                        // move on to next dependency
                        continue;
                    }
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("  Adding addon '" + addonName + "', since all dependencies have been added");
                }
                sortedAddons.add(entry.getValue());
                addedDeps.add(addonName);
                iter.remove();
                processed++;
            }

            if (processed == 0) {
                // we have a cyclical dependency, warn the user and load in the order they appeared originally
                if (LOG.isWarnEnabled()) {
                    LOG.warn("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    LOG.warn("::   Unresolved addon dependencies detected         ::");
                    LOG.warn("::   Continuing with original addon order           ::");
                    LOG.warn("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                }
                for (Map.Entry<String, GriffonAddon> entry : map.entrySet()) {
                    String addonName = entry.getKey();
                    String[] dependsOn = getDependsOn(entry.getValue());

                    // display this as a cyclical dep
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("::   Addon " + addonName);
                    }
                    if (dependsOn.length != 0) {
                        for (String dep : dependsOn) {
                            if (LOG.isWarnEnabled()) {
                                LOG.warn("::     depends on " + dep);
                            }
                        }
                    } else {
                        // we should only have items left in the list with deps, so this should never happen
                        // but a wise man once said...check for true, false and otherwise...just in case
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("::   Problem while resolving dependencies.");
                            LOG.warn("::   Unable to resolve dependency hierarchy.");
                        }
                    }
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    }
                }
                break;
                // if we have processed all the addons, we are done
            } else if (sortedAddons.size() == addons.size()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Addon dependency ordering complete");
                }
                break;
            }
        }

        addons = mapInstancesByName(sortedAddons, GRIFFON_ADDON_SUFFIX);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Computed addon order is " + addons.keySet());
        }

        return addons;
    }


    @SuppressWarnings("unchecked")
    protected void addMVCGroups(@Nonnull GriffonAddon addon) {
        for (Map.Entry<String, Map<String, Object>> groupEntry : addon.getMvcGroups().entrySet()) {
            String type = groupEntry.getKey();
            LOG.debug("Adding MVC group {}", type);
            Map<String, Object> members = groupEntry.getValue();
            Map<String, Object> configMap = new LinkedHashMap<>();
            Map<String, String> membersCopy = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry: members.entrySet()) {
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

    protected void event(@Nonnull ApplicationEvent evt) {
        event(evt, asList(getApplication()));
    }

    protected void event(@Nonnull ApplicationEvent evt, @Nonnull List<?> args) {
        getApplication().getEventRouter().publish(evt.getName(), args);
    }
}
