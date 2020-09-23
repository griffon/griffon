/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.annotations.core.Nonnull;
import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonService;
import griffon.core.artifact.GriffonView;
import griffon.core.env.GriffonEnvironment;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.InjectorFactory;
import griffon.core.injection.Key;
import griffon.core.injection.Module;
import griffon.util.ConverterRegistryHolder;
import griffon.util.GriffonClassUtils;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.util.TypeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.application.converter.ConverterRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.AnnotationUtils.sortByDependencies;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;
import static org.kordamp.jipsy.util.TypeLoader.load;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractApplicationBootstrapper implements ApplicationBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractApplicationBootstrapper.class);
    private static final String INJECTOR = "injector";
    private static final String SERVICES_PATH = "META-INF/services";
    protected final GriffonApplication application;

    public AbstractApplicationBootstrapper(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
    }

    @Override
    public void bootstrap() throws Exception {
        // 1 initialize environment settings
        LOG.info("Griffon {}", GriffonEnvironment.getGriffonVersion());
        LOG.info("Build: {}", GriffonEnvironment.getBuildDateTime());
        LOG.info("Revision: {}", GriffonEnvironment.getBuildRevision());
        LOG.info("JVM: {}", GriffonEnvironment.getJvmVersion());
        LOG.info("OS: {}", GriffonEnvironment.getOsVersion());

        // 2 create bindings
        LOG.debug("Creating module bindings");
        Iterable<Binding<?>> bindings = createBindings();

        if (LOG.isTraceEnabled()) {
            for (Binding<?> binding : bindings) {
                LOG.trace(binding.toString());
            }
        }

        // 3 create injector
        LOG.debug("Creating application injector");
        createInjector(bindings);

        ConverterRegistryHolder.setConverterRegistry(application.getInjector().getInstance(ConverterRegistry.class));
    }

    @Override
    public void run() {
        application.initialize();
        application.startup();
        application.ready();
    }

    @Nonnull
    protected Iterable<Binding<?>> createBindings() {
        Map<Key, Binding<?>> map = new LinkedHashMap<>();

        List<Module> modules = new ArrayList<>();
        createApplicationModule(modules);
        createArtifactsModule(modules);
        collectModuleBindings(modules);

        for (Module module : modules) {
            for (Binding<?> binding : module.getBindings()) {
                map.put(Key.of(binding), binding);
            }
        }

        return unmodifiableCollection(map.values());
    }

    protected void createArtifactsModule(@Nonnull List<Module> modules) {
        modules.add(new AbstractModule() {
            @Override
            protected void doConfigure() {
                TypeLoader.LineProcessor lineProcessor = (cl, type, line) -> {
                    line = line.trim();
                    try {
                        bind(cl.loadClass(line));
                    } catch (ClassNotFoundException e) {
                        LOG.error("'" + line + "' could not be resolved as a subtype of " + type.getName());
                        throw new IllegalStateException(e);
                    }
                };
                ClassLoader classLoader = getClass().getClassLoader();

                load(classLoader, SERVICES_PATH, GriffonModel.class, lineProcessor);
                load(classLoader, SERVICES_PATH, GriffonController.class, lineProcessor);
                load(classLoader, SERVICES_PATH, GriffonView.class, lineProcessor);
                load(classLoader, SERVICES_PATH, GriffonService.class, (cl, type, line) -> {
                    line = line.trim();
                    try {
                        bind(cl.loadClass(line)).asSingleton();
                    } catch (ClassNotFoundException e) {
                        LOG.error("'" + line + "' could not be resolved as a subtype of " + type.getName());
                        throw new IllegalStateException(e);
                    }
                });
            }
        });
    }

    protected void createApplicationModule(@Nonnull List<Module> modules) {
        modules.add(new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(GriffonApplication.class)
                    .toInstance(application);
            }
        });
    }

    protected void collectModuleBindings(@Nonnull Collection<Module> modules) {
        List<Module> moduleInstances = loadModules();
        moduleInstances.add(0, new DefaultApplicationModule());
        Map<String, Module> sortedModules = sortModules(moduleInstances);
        for (Map.Entry<String, Module> entry : sortedModules.entrySet()) {
            LOG.debug("Loading module bindings from {}:{}", entry.getKey(), entry.getValue());
            modules.add(entry.getValue());
        }
    }

    @Nonnull
    protected Map<String, Module> sortModules(@Nonnull List<Module> moduleInstances) {
        return sortByDependencies(moduleInstances, "Module", "module");
    }

    @Nonnull
    protected abstract List<Module> loadModules();

    private void createInjector(@Nonnull Iterable<Binding<?>> bindings) throws Exception {
        ServiceLoader<InjectorFactory> serviceLoader = ServiceLoader.load(InjectorFactory.class);
        try {
            Iterator<InjectorFactory> iterator = serviceLoader.iterator();
            InjectorFactory injectorFactory = iterator.next();
            LOG.debug("Injector will be created by {}", injectorFactory);
            Injector<?> injector = injectorFactory.createInjector(application, bindings);
            GriffonClassUtils.setProperty(application, INJECTOR, injector);
        } catch (Exception e) {
            LOG.error("An error occurred while initializing the injector", sanitize(e));
            throw e;
        }
    }
}
