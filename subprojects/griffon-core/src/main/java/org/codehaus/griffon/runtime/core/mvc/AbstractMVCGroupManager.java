/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.core.mvc;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.Context;
import griffon.core.ContextFactory;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCConsumer;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.mvc.MVCGroupConfigurationFactory;
import griffon.core.mvc.MVCGroupConsumer;
import griffon.core.mvc.MVCGroupFactory;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.mvc.TypedMVCGroup;
import griffon.core.mvc.TypedMVCGroupConsumer;
import griffon.exceptions.ArtifactNotFoundException;
import griffon.exceptions.MVCGroupConfigurationException;
import griffon.exceptions.MVCGroupInstantiationException;
import griffon.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.application.converter.ConverterRegistry;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isNotBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code MVCGroupManager} interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractMVCGroupManager implements MVCGroupManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMVCGroupManager.class);

    protected static final String ERROR_MVCTYPE_BLANK = "Argument 'mvcType' must not be blank";
    protected static final String ERROR_MVCID_BLANK = "Argument 'mvcId' must not be blank";
    protected static final String ERROR_CONFIGURATION_NULL = "Argument 'configuration' must not be null";
    protected static final String ERROR_GROUP_NULL = "Argument 'group' must not be null";
    protected static final String ERROR_CONFIG_NULL = "Argument 'config' must not be null";
    protected static final String ERROR_ARGS_NULL = "Argument 'args' must not be null";
    protected static final String ERROR_NAME_BLANK = "Argument 'name' cannot be blank";
    protected static final String ERROR_TYPE_NULL = "Argument 'type' cannot be null";

    private final GriffonApplication application;
    private final Map<String, MVCGroupConfiguration> configurations = new LinkedHashMap<>();
    private final Map<String, MVCGroup> groups = new LinkedHashMap<>();
    private final Object lock = new Object[0];

    private boolean initialized;

    @Inject
    protected MVCGroupConfigurationFactory mvcGroupConfigurationFactory;

    @Inject
    protected MVCGroupFactory mvcGroupFactory;

    @Inject
    protected ContextFactory contextFactory;

    @Inject
    protected ConverterRegistry converterRegistry;

    @Inject
    public AbstractMVCGroupManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
    }

    @Override
    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    public MVCGroupConfiguration newMVCGroupConfiguration(@Nonnull String mvcType, @Nonnull Map<String, String> members, @Nonnull Map<String, Object> config) {
        return mvcGroupConfigurationFactory.create(mvcType, members, config);
    }

    @Nonnull
    public MVCGroup newMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> members, @Nullable MVCGroup parentGroup) {
        return mvcGroupFactory.create(configuration, mvcId, members, parentGroup);
    }

    @Nonnull
    @Override
    public Context newContext(@Nullable MVCGroup parentGroup) {
        Context parentContext = parentGroup != null ? parentGroup.getContext() : getApplication().getContext();
        return contextFactory.create(parentContext);
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
        MVCGroupConfiguration configuration;
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
    @Override
    public <MVC extends TypedMVCGroup> MVC findTypedGroup(@Nonnull String mvcId) {
        requireNonBlank(mvcId, ERROR_MVCID_BLANK);
        synchronized (lock) {
            LOG.debug("Searching group {}", mvcId);
            return (MVC) groups.get(mvcId);
        }
    }

    @Nullable
    public MVCGroup getAt(@Nonnull String mvcId) {
        return findGroup(mvcId);
    }

    public final void initialize(@Nonnull Map<String, MVCGroupConfiguration> configurations) {
        requireNonNull(configurations, "Argument 'configurations' must not be null");
        if (configurations.isEmpty()) { return; }
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
        requireNonBlank(name, "Argument 'name' must not be blank");
        if (isNotBlank(name)) {
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
            if (group instanceof TypedMVCGroup) {
                groups.remove(((TypedMVCGroup) group).delegate());
            }
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
    protected List<? extends GriffonMvcArtifact> createMVC(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args) {
        MVCGroup group = createMVCGroup(findConfiguration(configuration.getMvcType()), mvcId, args);
        return asList(group.getModel(), group.getView(), group.getController());
    }

    @SuppressWarnings("unchecked")
    protected <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        MVCGroup group = null;
        try {
            group = createMVCGroup(configuration, mvcId, args);
            handler.accept((M) group.getModel(), (V) group.getView(), (C) group.getController());
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

    @SuppressWarnings("unchecked")
    protected void withMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        MVCGroup group = null;
        try {
            group = createMVCGroup(configuration, mvcId, args);
            handler.accept(group);
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
    protected abstract MVCGroup createMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nullable String mvcId, @Nonnull Map<String, Object> args);

    protected abstract void doInitialize(@Nonnull Map<String, MVCGroupConfiguration> configurations);

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType) {
        return createMVCGroup(findConfiguration(mvcType), null, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId) {
        return createMVCGroup(findConfiguration(mvcType), mvcId, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return createMVCGroup(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return createMVCGroup(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return createMVCGroup(findConfiguration(mvcType), mvcId, args);
    }

    @Nonnull
    @Override
    public MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return createMVCGroup(findConfiguration(mvcType), mvcId, args);
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType) {
        return typedMvcGroup(mvcType, createMVCGroup(findConfiguration(nameOf(mvcType)), null, Collections.<String, Object>emptyMap()));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return typedMvcGroup(mvcType, createMVCGroup(findConfiguration(nameOf(mvcType)), mvcId, Collections.<String, Object>emptyMap()));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return typedMvcGroup(mvcType, createMVCGroup(findConfiguration(nameOf(mvcType)), null, args));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return typedMvcGroup(mvcType, createMVCGroup(findConfiguration(nameOf(mvcType)), null, args));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return typedMvcGroup(mvcType, createMVCGroup(findConfiguration(nameOf(mvcType)), mvcId, args));
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return typedMvcGroup(mvcType, createMVCGroup(findConfiguration(nameOf(mvcType)), mvcId, args));
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType) {
        return createMVC(findConfiguration(mvcType), null, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType) {
        return createMVC(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args) {
        return createMVC(findConfiguration(mvcType), null, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId) {
        return createMVC(findConfiguration(mvcType), mvcId, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId) {
        return createMVC(findConfiguration(mvcType), mvcId, args);
    }

    @Nonnull
    @Override
    public List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return createMVC(findConfiguration(mvcType), mvcId, args);
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType) {
        return createMVC(findConfiguration(nameOf(mvcType)), null, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType) {
        return createMVC(findConfiguration(nameOf(mvcType)), null, args);
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args) {
        return createMVC(findConfiguration(nameOf(mvcType)), null, args);
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return createMVC(findConfiguration(nameOf(mvcType)), mvcId, Collections.<String, Object>emptyMap());
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId) {
        return createMVC(findConfiguration(nameOf(mvcType)), mvcId, args);
    }

    @Nonnull
    @Override
    public <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args) {
        return createMVC(findConfiguration(nameOf(mvcType)), mvcId, args);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), null, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), null, args, handler);
    }

    @Override
    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), null, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(nameOf(mvcType)), null, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(nameOf(mvcType)), mvcId, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(nameOf(mvcType)), mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(nameOf(mvcType)), mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(nameOf(mvcType)), null, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull MVCConsumer<M, V, C> handler) {
        withMVCGroup(findConfiguration(nameOf(mvcType)), null, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        withMVCGroup(findConfiguration(mvcType), null, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupConsumer handler) {
        withMVCGroup(findConfiguration(mvcType), mvcId, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupConsumer handler) {
        withMVCGroup(findConfiguration(mvcType), null, args, handler);
    }

    @Override
    public void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupConsumer handler) {
        withMVCGroup(findConfiguration(mvcType), null, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        withMVCGroup(mvcType, null, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        withMVCGroup(mvcType, mvcId, Collections.<String, Object>emptyMap(), handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        MVC group = null;
        try {
            group = createMVCGroup(mvcType, mvcId, args);
            handler.accept(group);
        } finally {
            try {
                if (group != null) {
                    destroyMVCGroup(group.getMvcId());
                }
            } catch (Exception x) {
                LOG.warn("Could not destroy group [{}] of type {}", mvcId, nameOf(mvcType), sanitize(x));
            }
        }
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        withMVCGroup(mvcType, mvcId, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        withMVCGroup(mvcType, null, args, handler);
    }

    @Override
    public <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupConsumer<MVC> handler) {
        withMVCGroup(mvcType, null, args, handler);
    }

    @Nonnull
    @Override
    public <C extends GriffonController> C getController(@Nonnull String name, @Nonnull Class<C> type) throws ArtifactNotFoundException {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(type, ERROR_TYPE_NULL);
        GriffonController controller = getControllers().get(name);
        if (controller != null) {
            return type.cast(controller);
        }
        throw new ArtifactNotFoundException(type, name);
    }

    @Nonnull
    @Override
    public <M extends GriffonModel> M getModel(@Nonnull String name, @Nonnull Class<M> type) throws ArtifactNotFoundException {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(type, ERROR_TYPE_NULL);
        GriffonModel model = getModels().get(name);
        if (model != null) {
            return type.cast(model);
        }
        throw new ArtifactNotFoundException(type, name);
    }

    @Nonnull
    @Override
    public <V extends GriffonView> V getView(@Nonnull String name, @Nonnull Class<V> type) throws ArtifactNotFoundException {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(type, ERROR_TYPE_NULL);
        GriffonView view = getViews().get(name);
        if (view != null) {
            return type.cast(view);
        }
        throw new ArtifactNotFoundException(type, name);
    }

    @Nullable
    @Override
    public <C extends GriffonController> C findController(@Nonnull String name, @Nonnull Class<C> type) {
        try {
            return getController(name, type);
        } catch (ArtifactNotFoundException anfe) {
            return null;
        }
    }

    @Nullable
    @Override
    public <M extends GriffonModel> M findModel(@Nonnull String name, @Nonnull Class<M> type) {
        try {
            return getModel(name, type);
        } catch (ArtifactNotFoundException anfe) {
            return null;
        }
    }

    @Nullable
    @Override
    public <V extends GriffonView> V findView(@Nonnull String name, @Nonnull Class<V> type) {
        try {
            return getView(name, type);
        } catch (ArtifactNotFoundException anfe) {
            return null;
        }
    }

    @Nonnull
    protected <MVC extends TypedMVCGroup> String nameOf(@Nonnull Class<? extends MVC> mvcType) {
        return AnnotationUtils.nameFor(mvcType, true);
    }

    @Nonnull
    protected <MVC extends TypedMVCGroup> MVC typedMvcGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull MVCGroup mvcGroup) {
        try {
            Constructor<? extends MVC> constructor = mvcType.getDeclaredConstructor(MVCGroup.class);
            MVC group = constructor.newInstance(mvcGroup);
            addGroup(group);
            return group;
        } catch (Exception e) {
            throw new MVCGroupInstantiationException("Unexpected error", mvcGroup.getMvcType(), mvcGroup.getMvcId(), e);
        }
    }
}
