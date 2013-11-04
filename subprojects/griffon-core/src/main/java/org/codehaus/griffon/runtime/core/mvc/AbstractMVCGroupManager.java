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

package org.codehaus.griffon.runtime.core.mvc;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCCallable;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.mvc.MVCGroupManager;
import griffon.exceptions.MVCGroupConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code MVCGroupManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractMVCGroupManager implements MVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMVCGroupManager.class);
    protected static final String ERROR_MVCTYPE_BLANK = "Argument 'mvcType' cannot be blank";
    protected static final String ERROR_MVCID_BLANK = "Argument 'mvcId' cannot be blank";
    protected static final String ERROR_CONFIGURATION_NULL = "Argument 'configuration' cannot be null";
    protected static final String ERROR_GROUP_NULL = "Argument 'group' cannot be null";
    protected static final String ERROR_CONFIG_NULL = "Argument 'config' cannot be null";
    protected static final String ERROR_ARGS_NULL = "Argument 'args' cannot be null";

    private final GriffonApplication application;

    private final Map<String, MVCGroupConfiguration> configurations = new LinkedHashMap<>();
    private final Map<String, MVCGroup> groups = new LinkedHashMap<>();
    private final Object lock = new Object[0];
    private boolean initialized;

    @Inject
    public AbstractMVCGroupManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
    }

    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    public Map<String, MVCGroupConfiguration> getConfigurations() {
        synchronized (lock) {
            return unmodifiableMap(configurations);
        }
    }

    @Nonnull
    public Map<String, MVCGroup> getGroups() {
        synchronized (lock) {
            return unmodifiableMap(groups);
        }
    }

    @Nonnull
    public MVCGroupConfiguration findConfiguration(@Nonnull String mvcType) {
        requireNonBlank(mvcType, ERROR_MVCTYPE_BLANK);
        MVCGroupConfiguration configuration = null;
        synchronized (lock) {
            configuration = configurations.get(mvcType);
        }

        if (configuration == null) {
            throw new MVCGroupConfigurationException("Unknown MVC type '" + mvcType + "'. Known types are " + configurations.keySet(), mvcType);
        }
        return configuration;
    }

    @Nullable
    public MVCGroup findGroup(@Nonnull String mvcId) {
        requireNonBlank(mvcId, ERROR_MVCID_BLANK);
        synchronized (lock) {
            LOG.debug("Searching group {}", mvcId);
            return groups.get(mvcId);
        }
    }

    @Nullable
    public MVCGroup getAt(@Nonnull String mvcId) {
        return findGroup(mvcId);
    }

    public final void initialize(@Nonnull Map<String, MVCGroupConfiguration> configurations) {
        requireNonNull(configurations, "Argument 'configurations' cannot be null");
        if (configurations.isEmpty()) return;
        synchronized (lock) {
            if (!initialized) {
                doInitialize(configurations);
                initialized = true;
            }
        }
    }

    public void addConfiguration(@Nonnull MVCGroupConfiguration configuration) {
        requireNonNull(configuration, ERROR_CONFIGURATION_NULL);
        synchronized (lock) {
            if (initialized && configurations.get(configuration.getMvcType()) != null) {
                return;
            }
            configurations.put(configuration.getMvcType(), configuration);
        }
    }

    public void removeConfiguration(@Nonnull MVCGroupConfiguration configuration) {
        requireNonNull(configuration, ERROR_CONFIGURATION_NULL);
        removeConfiguration(configuration.getMvcType());
    }

    public void removeConfiguration(@Nonnull String name) {
        requireNonBlank(name, "Argument 'name' cannot be blank");
        if (!isBlank(name)) {
            synchronized (lock) {
                configurations.remove(name);
            }
        }
    }

    protected void addGroup(@Nonnull MVCGroup group) {
        requireNonNull(group, ERROR_GROUP_NULL);
        synchronized (lock) {
            LOG.debug("Adding group {}:{}", group.getMvcId(), group);
            groups.put(group.getMvcId(), group);
        }
    }

    protected void removeGroup(@Nonnull MVCGroup group) {
        requireNonNull(group, ERROR_GROUP_NULL);
        synchronized (lock) {
            LOG.debug("Removing group {}:{}", group.getMvcId(), group);
            groups.remove(group.getMvcId());
        }
    }

    @Nonnull
    public final Map<String, ? extends GriffonModel> getModels() {
        Map<String, GriffonModel> models = new LinkedHashMap<>();
        synchronized (lock) {
            for (MVCGroup group : groups.values()) {
                GriffonModel model = group.getModel();
                if (model != null) {
                    models.put(group.getMvcId(), model);
                }
            }
        }
        return unmodifiableMap(models);
    }

    @Nonnull
    public final Map<String, ? extends GriffonView> getViews() {
        Map<String, GriffonView> views = new LinkedHashMap<>();
        synchronized (lock) {
            for (MVCGroup group : groups.values()) {
                GriffonView view = group.getView();
                if (view != null) {
                    views.put(group.getMvcId(), view);
                }
            }
        }
        return unmodifiableMap(views);
    }

    @Nonnull
    public final Map<String, ? extends GriffonController> getControllers() {
        Map<String, GriffonController> controllers = new LinkedHashMap<>();
        synchronized (lock) {
            for (MVCGroup group : groups.values()) {
                GriffonController controller = group.getController();
                if (controller != null) {
                    controllers.put(group.getMvcId(), controller);
                }
            }
        }
        return unmodifiableMap(controllers);
    }

    @Nonnull
    @Override
    public MVCGroupConfiguration cloneMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, Object> config) {
        requireNonBlank(mvcType, ERROR_MVCTYPE_BLANK);
        requireNonNull(config, ERROR_CONFIG_NULL);
        MVCGroupConfiguration configuration = findConfiguration(mvcType);
        Map<String, Object> configCopy = new LinkedHashMap<>();
        configCopy.putAll(configuration.getConfig());
        configCopy.putAll(config);
        return newMVCGroupConfiguration(mvcType, configuration.getMembers(), configCopy);
    }

    @Nonnull
    protected List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args) {
        MVCGroup group = buildMVCGroup(findConfiguration(configuration.getMvcType()), mvcId, args);
        return asList(group.getModel(), group.getView(), group.getController());
    }

    protected <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCCallable<M, V, C> handler) {
        MVCGroup group = null;
        try {
            group = buildMVCGroup(configuration, mvcId, args);
            handler.call((M) group.getModel(), (V) group.getView(), (C) group.getController());
        } finally {
            try {
                if (group != null) {
                    destroyMVCGroup(group.getMvcId());
                }
            } catch (Exception x) {
                LOG.warn("Could not destroy group [{}] of type {}", mvcId, configuration.getMvcType(), sanitize(x));

            }
        }
    }

    @Nonnull
    protected abstract MVCGroup buildMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args);

    protected abstract void doInitialize(@Nonnull Map<String, MVCGroupConfiguration> configurations);

    @Nonnull
    @Override
    public MVCGroup buildMVCGroup(@Nonnull String mvcType) {
        return buildMVCGroup(findConfiguration(mvcType), null, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public MVCGroup buildMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return buildMVCGroup(findConfiguration(mvcType), mvcId, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public MVCGroup buildMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return buildMVCGroup(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public MVCGroup buildMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return buildMVCGroup(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public MVCGroup buildMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return buildMVCGroup(findConfiguration(mvcType), mvcId, args);
    }

    @Nonnull
    @Override
    public MVCGroup buildMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return buildMVCGroup(findConfiguration(mvcType), mvcId, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType) {
        return createMVCGroup(findConfiguration(mvcType), null, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return createMVCGroup(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return createMVCGroup(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return createMVCGroup(findConfiguration(mvcType), mvcId, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return createMVCGroup(findConfiguration(mvcType), mvcId, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return createMVCGroup(findConfiguration(mvcType), mvcId, args);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCCallable<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), null, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCCallable<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCCallable<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCCallable<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCCallable<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), null, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCCallable<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), null, args, handler);
    }
}
