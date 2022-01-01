/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package org.codehaus.griffon.test.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.BindTo;
import griffon.core.GriffonApplication;
import griffon.core.injection.Module;
import griffon.test.core.TestCaseAware;
import griffon.test.core.TestModuleAware;
import griffon.test.core.TestModuleOverrides;
import griffon.test.core.TestModules;
import griffon.test.core.injection.TestingModule;
import griffon.util.AnnotationUtils;
import org.codehaus.griffon.runtime.core.DefaultApplicationBootstrapper;
import org.codehaus.griffon.runtime.core.injection.AnnotatedBindingBuilder;
import org.codehaus.griffon.runtime.core.injection.LinkedBindingBuilder;
import org.codehaus.griffon.runtime.core.injection.SingletonBindingBuilder;
import org.codehaus.griffon.test.core.injection.AbstractTestingModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.util.AnnotationUtils.named;
import static griffon.util.StringUtils.getPropertyName;
import static griffon.util.StringUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TestApplicationBootstrapper extends DefaultApplicationBootstrapper implements TestCaseAware {
    private static final Logger LOG = LoggerFactory.getLogger(TestApplicationBootstrapper.class);

    private static final String METHOD_MODULES = "modules";
    private static final String METHOD_MODULE_OVERRIDES = "moduleOverrides";
    private Object testCase;

    public TestApplicationBootstrapper(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setTestCase(@Nonnull Object testCase) {
        this.testCase = testCase;
    }

    @Nonnull
    @Override
    protected List<Module> loadModules() {
        List<Module> modules = doCollectModulesFromMethod();
        if (!modules.isEmpty()) {
            return modules;
        }
        modules = super.loadModules();
        doCollectOverridingModules(modules);
        doCollectModulesFromInnerClasses(modules);
        doCollectModulesFromFields(modules);
        return modules;
    }

    @Nonnull
    @Override
    protected Map<String, Module> sortModules(@Nonnull List<Module> moduleInstances) {
        Map<String, Module> sortedModules = super.sortModules(moduleInstances);
        // move all `TestingModules` at the end
        // turns out the map is of type LinkedHashMap so insertion order is retained
        Map<String, Module> testingModules = new LinkedHashMap<>();
        for (Map.Entry<String, Module> e : sortedModules.entrySet()) {
            if (e.getValue() instanceof TestingModule) {
                testingModules.put(e.getKey(), e.getValue());
            }
        }
        for (String key : testingModules.keySet()) {
            sortedModules.remove(key);
        }
        sortedModules.putAll(testingModules);

        LOG.debug("computed {} order is {}", "Module", sortedModules.keySet());

        return sortedModules;
    }

    @SuppressWarnings("unchecked")
    private List<Module> doCollectModulesFromMethod() {
        if (testCase == null) {
            return Collections.emptyList();
        }

        if (testCase instanceof TestModuleAware) {
            return ((TestModuleAware) testCase).modules();
        } else {
            Class<?> clazz = testCase.getClass();
            List<Class<?>> classes = new ArrayList<>();
            while (clazz != null) {
                classes.add(clazz);
                clazz = clazz.getSuperclass();
            }

            Collections.reverse(classes);
            for (Class<?> c : classes) {
                List<Module> ms = harvestModulesFromMethod(c, METHOD_MODULES, TestModules.class);
                if (!ms.isEmpty()) { return ms; }
            }
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private void doCollectOverridingModules(@Nonnull final Collection<Module> modules) {
        if (testCase == null) {
            return;
        }

        if (testCase instanceof TestModuleAware) {
            List<Module> overrides = ((TestModuleAware) testCase).moduleOverrides();
            modules.addAll(overrides);
        } else {
            Class<?> clazz = testCase.getClass();
            List<Class<?>> classes = new ArrayList<>();
            while (clazz != null) {
                classes.add(clazz);
                clazz = clazz.getSuperclass();
            }

            Collections.reverse(classes);
            for (Class<?> c : classes) {
                List<Module> overrides = harvestModulesFromMethod(c, METHOD_MODULE_OVERRIDES, TestModuleOverrides.class);
                if (!overrides.isEmpty()) {
                    modules.addAll(overrides);
                    return;
                }
            }
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private List<Module> harvestModulesFromMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nonnull Class<? extends Annotation> annotationClass) {
        Method annotatedMethod = null;

        // check annotation first
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getAnnotation(annotationClass) != null) {
                annotatedMethod = m;
                break;
            }
        }

        // check using naming convention
        Method namedMethod = null;
        try {
            namedMethod = clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            if (annotatedMethod == null) {
                return Collections.emptyList();
            }
        }

        if (namedMethod != null && annotatedMethod != namedMethod) {
            System.err.println("Usage of method named '" + clazz.getName() + "." + methodName + "()' is discouraged. Rename the method and annotate it with @" + annotationClass.getSimpleName());
        }

        Method method = annotatedMethod != null ? annotatedMethod : namedMethod;
        try {
            method.setAccessible(true);
            return (List<Module>) method.invoke(testCase);
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occurred while initializing modules from " + clazz.getName() + "." + method.getName(), e);
        }
    }

    private void doCollectModulesFromInnerClasses(@Nonnull final Collection<Module> modules) {
        if (testCase != null) {
            modules.add(new InnerClassesModule());
            modules.addAll(harvestInnerModules());
        }
    }

    @Nonnull
    private Collection<? extends Module> harvestInnerModules() {
        Class<?> clazz = testCase.getClass();
        List<Class<?>> classes = new ArrayList<>();
        while (clazz != null) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }

        Collections.reverse(classes);

        List<Module> modules = new ArrayList<>();
        for (Class<?> c : classes) {
            modules.addAll(doHarvestInnerModules(c));
        }

        return modules;
    }

    @Nonnull
    private Collection<? extends Module> doHarvestInnerModules(@Nonnull Class<?> rootClass) {
        List<Module> modules = new ArrayList<>();
        for (Class<?> clazz : rootClass.getDeclaredClasses()) {
            if (!Module.class.isAssignableFrom(clazz) || !Modifier.isPublic(clazz.getModifiers())) {
                continue;
            }

            try {
                modules.add((Module) clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error("Can't instantiate module " + clazz.getName() + " . Make sure it's marked as public and provides a no-args constructor.");
            }
        }
        return modules;
    }

    private void doCollectModulesFromFields(@Nonnull final Collection<Module> modules) {
        if (testCase != null) {
            modules.add(new FieldsModule());
        }
    }

    @Nonnull
    protected List<Annotation> harvestQualifiers(@Nonnull Class<?> clazz) {
        List<Annotation> list = new ArrayList<>();
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (AnnotationUtils.isAnnotatedWith(annotation, Qualifier.class)) {
                if (BindTo.class.isAssignableFrom(annotation.getClass())) {
                    continue;
                }

                // special case for @Named
                if (Named.class.isAssignableFrom(annotation.getClass())) {
                    Named named = (Named) annotation;
                    if (isBlank(named.value())) {
                        list.add(named(getPropertyName(clazz)));
                        continue;
                    }
                }
                list.add(annotation);
            }
        }
        return list;
    }

    @Nonnull
    protected List<Annotation> harvestQualifiers(@Nonnull Field field) {
        List<Annotation> list = new ArrayList<>();
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (AnnotationUtils.isAnnotatedWith(annotation, Qualifier.class)) {
                if (BindTo.class.isAssignableFrom(annotation.getClass())) {
                    continue;
                }

                // special case for @Named
                if (Named.class.isAssignableFrom(annotation.getClass())) {
                    Named named = (Named) annotation;
                    if (isBlank(named.value())) {
                        list.add(named(getPropertyName(field.getName())));
                        continue;
                    }
                }
                list.add(annotation);
            }
        }
        return list;
    }

    private class InnerClassesModule extends AbstractTestingModule {
        @Override
        @SuppressWarnings("unchecked")
        protected void doConfigure() {
            Class<?> clazz = testCase.getClass();
            List<Class<?>> classes = new ArrayList<>();
            while (clazz != null) {
                classes.add(clazz);
                clazz = clazz.getSuperclass();
            }

            Collections.reverse(classes);
            for (Class<?> c : classes) {
                harvestBindings(c);
            }
        }

        @SuppressWarnings("unchecked")
        protected void harvestBindings(@Nonnull Class<?> rootClass) {
            for (Class<?> clazz : rootClass.getDeclaredClasses()) {
                BindTo bindTo = clazz.getAnnotation(BindTo.class);
                if (bindTo == null) { continue; }
                List<Annotation> qualifiers = harvestQualifiers(clazz);
                Annotation classifier = qualifiers.isEmpty() ? null : qualifiers.get(0);
                boolean isSingleton = clazz.getAnnotation(Singleton.class) != null;

                AnnotatedBindingBuilder<?> abuilder = bind(bindTo.value());
                if (classifier != null) {
                    LinkedBindingBuilder<?> lbuilder = abuilder.withClassifier(classifier);
                    if (Provider.class.isAssignableFrom(clazz)) {
                        SingletonBindingBuilder<?> sbuilder = lbuilder.toProvider((Class) clazz);
                        if (isSingleton) { sbuilder.asSingleton(); }
                    } else {
                        SingletonBindingBuilder<?> sbuilder = lbuilder.to((Class) clazz);
                        if (isSingleton) { sbuilder.asSingleton(); }
                    }
                } else {
                    if (Provider.class.isAssignableFrom(clazz)) {
                        SingletonBindingBuilder<?> sbuilder = abuilder.toProvider((Class) clazz);
                        if (isSingleton) { sbuilder.asSingleton(); }
                    } else {
                        SingletonBindingBuilder<?> sbuilder = abuilder.to((Class) clazz);
                        if (isSingleton) { sbuilder.asSingleton(); }
                    }
                }
            }
        }
    }

    private class FieldsModule extends AbstractTestingModule {
        @Override
        @SuppressWarnings("unchecked")
        protected void doConfigure() {
            Class<?> clazz = testCase.getClass();
            List<Class<?>> classes = new ArrayList<>();
            while (clazz != null) {
                classes.add(clazz);
                clazz = clazz.getSuperclass();
            }

            Collections.reverse(classes);
            for (Class<?> c : classes) {
                harvestBindings(c);
            }
        }

        @SuppressWarnings("unchecked")
        protected void harvestBindings(@Nonnull Class<?> rootClass) {
            for (Field field : rootClass.getDeclaredFields()) {
                BindTo bindTo = field.getAnnotation(BindTo.class);
                if (bindTo == null) { continue; }
                List<Annotation> qualifiers = harvestQualifiers(field);
                Annotation classifier = qualifiers.isEmpty() ? null : qualifiers.get(0);
                boolean isSingleton = field.getAnnotation(Singleton.class) != null;

                field.setAccessible(true);
                Object instance = null;
                try {
                    instance = field.get(testCase);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }

                if (instance != null) {
                    AnnotatedBindingBuilder<Object> abuilder = (AnnotatedBindingBuilder<Object>) bind(bindTo.value());
                    if (classifier != null) {
                        if (Provider.class.isAssignableFrom(instance.getClass())) {
                            SingletonBindingBuilder<?> sbuilder = abuilder
                                .withClassifier(classifier)
                                .toProvider((Provider<Object>) instance);
                            if (isSingleton) { sbuilder.asSingleton(); }
                        } else {
                            abuilder.withClassifier(classifier).toInstance(instance);
                        }
                    } else if (Provider.class.isAssignableFrom(instance.getClass())) {
                        SingletonBindingBuilder<?> sbuilder = abuilder.toProvider((Provider<Object>) instance);
                        if (isSingleton) { sbuilder.asSingleton(); }
                    } else {
                        abuilder.toInstance(instance);
                    }
                } else {
                    AnnotatedBindingBuilder<?> abuilder = bind(bindTo.value());
                    if (classifier != null) {
                        LinkedBindingBuilder<?> lbuilder = abuilder.withClassifier(classifier);
                        if (Provider.class.isAssignableFrom(field.getType())) {
                            SingletonBindingBuilder<?> sbuilder = lbuilder.toProvider((Class) field.getType());
                            if (isSingleton) { sbuilder.asSingleton(); }
                        } else {
                            SingletonBindingBuilder<?> sbuilder = lbuilder.to((Class) field.getType());
                            if (isSingleton) { sbuilder.asSingleton(); }
                        }
                    } else {
                        if (Provider.class.isAssignableFrom(field.getType())) {
                            SingletonBindingBuilder<?> sbuilder = abuilder.toProvider((Class) field.getType());
                            if (isSingleton) { sbuilder.asSingleton(); }
                        } else {
                            SingletonBindingBuilder<?> sbuilder = abuilder.to((Class) field.getType());
                            if (isSingleton) { sbuilder.asSingleton(); }
                        }
                    }
                }
            }
        }
    }
}
