/*
 * Copyright 2009-2011 the original author or authors.
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

import griffon.core.*;
import griffon.exceptions.MVCGroupConfigurationException;
import griffon.util.GriffonExceptionHandler;
import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import java.util.*;

/**
 * Base implementation of the {@code MVCGroupManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractMVCGroupManager implements MVCGroupManager {
    private final GriffonApplication app;

    private final Map<String, MVCGroupConfiguration> configurations = new LinkedHashMap<String, MVCGroupConfiguration>();
    private final Map<String, MVCGroup> groups = new LinkedHashMap<String, MVCGroup>();
    private final Object lock = new Object();
    private boolean initialized;

    public AbstractMVCGroupManager(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    public Map<String, MVCGroupConfiguration> getConfigurations() {
        synchronized (lock) {
            return Collections.unmodifiableMap(configurations);
        }
    }

    public Map<String, MVCGroup> getGroups() {
        synchronized (lock) {
            return Collections.unmodifiableMap(groups);
        }
    }

    public MVCGroupConfiguration findConfiguration(String mvcType) {
        MVCGroupConfiguration configuration = null;
        synchronized (lock) {
            configuration = configurations.get(mvcType);
        }

        if (configuration == null) {
            throw new MVCGroupConfigurationException("Unknown MVC type '" + mvcType + "'. Known types are " + configurations.keySet(), mvcType);
        }
        return configuration;
    }

    public MVCGroup findGroup(String mvcId) {
        synchronized (lock) {
            return groups.get(mvcId);
        }
    }

    public MVCGroup getAt(String mvcId) {
        return findGroup(mvcId);
    }

    public final void initialize(Map<String, MVCGroupConfiguration> configurations) {
        synchronized (lock) {
            if (!initialized) {
                doInitialize(configurations);
                initialized = true;
            }
        }
    }

    protected void addConfiguration(MVCGroupConfiguration configuration) {
        synchronized (lock) {
            configurations.put(configuration.getMvcType(), configuration);
        }
    }

    protected void addGroup(MVCGroup group) {
        synchronized (lock) {
            groups.put(group.getMvcId(), group);
        }
    }

    protected void removeGroup(MVCGroup group) {
        synchronized (lock) {
            groups.remove(group.getMvcId());
        }
    }

    protected abstract void doInitialize(Map<String, MVCGroupConfiguration> configurations);

    public MVCGroup buildMVCGroup(String mvcType) {
        return buildMVCGroup(findConfiguration(mvcType), mvcType, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName) {
        return buildMVCGroup(findConfiguration(mvcType), mvcName, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType) {
        return buildMVCGroup(findConfiguration(mvcType), mvcType, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, Map<String, Object> args) {
        return buildMVCGroup(findConfiguration(mvcType), mvcType, args);
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return buildMVCGroup(findConfiguration(mvcType), mvcName, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return buildMVCGroup(findConfiguration(mvcType), mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType) {
        return createMVCGroup(findConfiguration(mvcType), mvcType, Collections.<String, Object>emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType) {
        return createMVCGroup(findConfiguration(mvcType), mvcType, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, Map<String, Object> args) {
        return createMVCGroup(findConfiguration(mvcType), mvcType, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName) {
        return createMVCGroup(findConfiguration(mvcType), mvcName, Collections.<String, Object>emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return createMVCGroup(findConfiguration(mvcType), mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return createMVCGroup(findConfiguration(mvcType), mvcName, args);
    }

    public void withMVCGroup(String mvcType, Closure handler) {
        withMVCGroup(findConfiguration(mvcType), mvcType, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Closure handler) {
        withMVCGroup(findConfiguration(mvcType), mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, Map<String, Object> args, Closure handler) {
        withMVCGroup(findConfiguration(mvcType), mvcType, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, Closure handler) {
        withMVCGroup(findConfiguration(mvcType), mvcType, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, Closure handler) {
        withMVCGroup(findConfiguration(mvcType), mvcName, args, handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, Closure handler) {
        withMVCGroup(findConfiguration(mvcType), mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, MVCClosure<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcType, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcType, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, MVCClosure<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcType, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        withMVCGroup(findConfiguration(mvcType), mvcName, args, handler);
    }

    protected List<? extends GriffonMvcArtifact> createMVCGroup(MVCGroupConfiguration configuration, String mvcName, Map<String, Object> args) {
        MVCGroup group = buildMVCGroup(findConfiguration(configuration.getMvcType()), mvcName, args);
        return Arrays.asList(group.getModel(), group.getView(), group.getController());
    }

    protected void withMVCGroup(MVCGroupConfiguration configuration, String mvcName, Map<String, Object> args, Closure handler) {
        try {
            List<? extends GriffonMvcArtifact> group = createMVCGroup(configuration, mvcName, args);
            handler.call(group.get(0), group.get(1), group.get(2));
        } finally {
            try {
                destroyMVCGroup(mvcName);
            } catch (Exception x) {
                if (app.getLog().isWarnEnabled()) {
                    app.getLog().warn("Could not destroy group [" + mvcName + "] of type " + configuration.getMvcType() + ".", GriffonExceptionHandler.sanitize(x));
                }
            }
        }
    }

    protected <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(MVCGroupConfiguration configuration, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        try {
            List<? extends GriffonMvcArtifact> group = createMVCGroup(configuration, mvcName, args);
            handler.call((M) group.get(0), (V) group.get(1), (C) group.get(2));
        } finally {
            try {
                destroyMVCGroup(mvcName);
            } catch (Exception x) {
                if (app.getLog().isWarnEnabled()) {
                    app.getLog().warn("Could not destroy group [" + mvcName + "] of type " + configuration.getMvcType() + ".", GriffonExceptionHandler.sanitize(x));
                }
            }
        }
    }

    protected abstract MVCGroup buildMVCGroup(MVCGroupConfiguration configuration, String mvcName, Map<String, Object> args);

    public final Map<String, ? extends FactoryBuilderSupport> getBuilders() {
        Map<String, FactoryBuilderSupport> builders = new LinkedHashMap<String, FactoryBuilderSupport>();
        synchronized (lock) {
            for (MVCGroup group : groups.values()) {
                FactoryBuilderSupport builder = group.getBuilder();
                if (builder != null) {
                    builders.put(group.getMvcId(), builder);
                }
            }
        }
        return Collections.unmodifiableMap(builders);
    }

    public final Map<String, ? extends GriffonModel> getModels() {
        Map<String, GriffonModel> models = new LinkedHashMap<String, GriffonModel>();
        synchronized (lock) {
            for (MVCGroup group : groups.values()) {
                GriffonModel model = group.getModel();
                if (model != null) {
                    models.put(group.getMvcId(), model);
                }
            }
        }
        return Collections.unmodifiableMap(models);
    }

    public final Map<String, ? extends GriffonView> getViews() {
        Map<String, GriffonView> views = new LinkedHashMap<String, GriffonView>();
        synchronized (lock) {
            for (MVCGroup group : groups.values()) {
                GriffonView view = group.getView();
                if (view != null) {
                    views.put(group.getMvcId(), view);
                }
            }
        }
        return Collections.unmodifiableMap(views);
    }

    public final Map<String, ? extends GriffonController> getControllers() {
        Map<String, GriffonController> controllers = new LinkedHashMap<String, GriffonController>();
        synchronized (lock) {
            for (MVCGroup group : groups.values()) {
                GriffonController controller = group.getController();
                if (controller != null) {
                    controllers.put(group.getMvcId(), controller);
                }
            }
        }
        return Collections.unmodifiableMap(controllers);
    }
}
