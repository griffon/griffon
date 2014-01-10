/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonApplication;
import griffon.core.env.GriffonEnvironment;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.InjectorFactory;
import griffon.core.injection.Module;
import griffon.util.GriffonClassUtils;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.*;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.AnnotationUtils.sortByDependencies;
import static java.util.Collections.unmodifiableCollection;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ApplicationBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationBootstrapper.class);
    private static final String INJECTOR = "injector";

    private final GriffonApplication application;

    public ApplicationBootstrapper(@Nonnull GriffonApplication application) {
        this.application = application;
    }

    public void bootstrap() throws Exception {
        // 1 initialize environment settings
        //Metadata metadata = Metadata.getCurrent();
        //metadata.getGriffonStartDir();
        //metadata.getGriffonWorkingDir();
        LOG.info("Griffon {}", GriffonEnvironment.getGriffonVersion());
        LOG.info("Build: {}", GriffonEnvironment.getBuildDateTime());
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
    }

    public void run() {
        application.initialize();
        application.startup();
        application.ready();
    }

    protected Iterable<Binding<?>> createBindings() {
        Map<Key, Binding<?>> map = new LinkedHashMap<>();

        List<Module> modules = new ArrayList<>();
        modules.add(new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(GriffonApplication.class)
                    .toInstance(application);
            }
        });
        collectModuleBindings(modules);

        for (Module module : modules) {
            for (Binding<?> binding : module.getBindings()) {
                map.put(Key.of(binding), binding);
            }
        }

        return unmodifiableCollection(map.values());
    }

    protected void collectModuleBindings(@Nonnull Collection<Module> modules) {
        Collection<Module> moduleInstances = loadModules();
        Map<String, Module> sortedModules = sortByDependencies(moduleInstances, "Module", "module");
        for (Map.Entry<String, Module> entry : sortedModules.entrySet()) {
            LOG.debug("Loading module bindings from {}:{}", entry.getKey(), entry.getValue());
            modules.add(entry.getValue());
        }
    }

    protected Collection<Module> loadModules() {
        Collection<Module> moduleInstances = new ArrayList<>();
        ServiceLoader<Module> serviceLoader = ServiceLoader.load(Module.class);
        moduleInstances.add(new DefaultApplicationModule());
        for (Module module : serviceLoader) {
            moduleInstances.add(module);
        }
        return moduleInstances;
    }

    private void createInjector(Iterable<Binding<?>> bindings) throws Exception {
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

    private static class Key {
        private final Class<?> source;
        private final Class<? extends Annotation> annotationType;
        private Annotation annotation;

        private Key(@Nonnull Class<?> source, @Nonnull Annotation annotation) {
            this.source = source;
            this.annotation = annotation;
            this.annotationType = annotation.getClass();
        }

        private Key(@Nonnull Class<?> source, @Nonnull Class<? extends Annotation> annotationType) {
            this.source = source;
            this.annotationType = annotationType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return source.equals(key.source) &&
                !(annotationType != null ? !annotationType.equals(key.annotationType) : key.annotationType != null) &&
                !(annotation != null ? !annotation.equals(key.annotation) : key.annotation != null);
        }

        @Override
        public int hashCode() {
            int result = source.hashCode();
            result = 31 * result + (annotationType != null ? annotationType.hashCode() : 0);
            result = 31 * result + (annotation != null ? annotation.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Key{");
            sb.append("source=").append(source);
            sb.append(", annotationType=").append(annotationType);
            sb.append(", annotation=").append(annotation);
            sb.append('}');
            return sb.toString();
        }

        @Nonnull
        @SuppressWarnings("ConstantConditions")
        public static Key of(Binding<?> binding) {
            return binding.getClassifier() != null ? new Key(binding.getSource(), binding.getClassifier()) :
                new Key(binding.getSource(), binding.getClassifierType());
        }
    }
}
