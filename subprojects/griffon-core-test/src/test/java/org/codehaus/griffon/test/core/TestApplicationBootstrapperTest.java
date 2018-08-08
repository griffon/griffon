/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import griffon.core.ApplicationClassLoader;
import griffon.core.GriffonApplication;
import griffon.core.injection.Binding;
import griffon.core.injection.InstanceBinding;
import griffon.core.injection.Key;
import griffon.core.injection.Module;
import griffon.core.injection.ProviderBinding;
import griffon.core.injection.ProviderTypeBinding;
import griffon.core.injection.TargetBinding;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestApplicationBootstrapperTest {
    private enum BindingTypes {
        TARGET("No binding of type " + TargetBinding.class.getName() + " was found"),
        INSTANCE("No named binding of type " + InstanceBinding.class.getName() + " was found"),
        NAMED_TARGET("No binding of type " + TargetBinding.class.getName() + " was found"),
        NAMED_INSTANCE("No named binding of type " + InstanceBinding.class.getName() + " was found"),
        PROVIDER("No binding of type " + ProviderBinding.class.getName() + " was found"),
        NAMED_PROVIDER("No named binding of type " + ProviderTypeBinding.class.getName() + " was found"),
        PROVIDER_TYPE("No binding of type " + ProviderBinding.class.getName() + " was found"),
        NAMED_PROVIDER_TYPE("No named binding of type " + ProviderTypeBinding.class.getName() + " was found");

        private String label;

        BindingTypes(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @Test
    public void modulesMethodSuppliesAllBindings() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new ModulesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        assertEquals(1, modules.size());
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }

    @Test
    public void modulesMethodSuppliesAllBindings_subclass() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new ChildModulesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        assertEquals(1, modules.size());
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }

    @Test
    public void annotatedModulesMethodSuppliesAllBindings() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new AnnotatedModulesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        assertEquals(1, modules.size());
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }


    @Test
    public void annotatedModulesMethodSuppliesAllBindings_subclass() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new AnnotatedChildModulesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        assertEquals(1, modules.size());
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }

    @Test
    public void moduleOverridesMethodConfiguresCustomApplicationClassLoader() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new ModuleOverridesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }

    @Test
    public void moduleOverridesMethodConfiguresCustomApplicationClassLoader_subclass() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new ChildModuleOverridesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }

    @Test
    public void annotatedModuleOverridesMethodConfiguresCustomApplicationClassLoader() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new AnnotatedModuleOverridesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }

    @Test
    public void annotatedModuleOverridesMethodConfiguresCustomApplicationClassLoader_subclass() {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        Object testCase = new AnnotatedChildModuleOverridesMethodTestcase();
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            if (e.getValue() instanceof TargetBinding) {
                TargetBinding<?> tb = (TargetBinding) e.getValue();
                if (ApplicationClassLoader.class.isAssignableFrom(tb.getSource())) {
                    assertTrue(TestApplicationClassLoader.class.isAssignableFrom(tb.getTarget()));
                    return;
                }
            }
        }
        fail("Test case did not provide an overriding binding for " + ApplicationClassLoader.class.getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void singletonTargetsInnerClassModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.TARGET);
        bindingTypes.add(BindingTypes.NAMED_TARGET);
        assertBindingTypes(new InnerClassModuleTestcase.SingletonTargets(), bindingTypes, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void targetsInnerClassModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.TARGET);
        bindingTypes.add(BindingTypes.NAMED_TARGET);
        assertBindingTypes(new InnerClassModuleTestcase.Targets(), bindingTypes, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void singletonProviderTypesInnerClassModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.PROVIDER_TYPE);
        bindingTypes.add(BindingTypes.NAMED_PROVIDER_TYPE);
        assertBindingTypes(new InnerClassModuleTestcase.SingletonProviders(), bindingTypes, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void providerTypesInnerClassModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.PROVIDER_TYPE);
        bindingTypes.add(BindingTypes.NAMED_PROVIDER_TYPE);
        assertBindingTypes(new InnerClassModuleTestcase.Providers(), bindingTypes, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void singletonTargetsFieldsModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.TARGET);
        bindingTypes.add(BindingTypes.NAMED_TARGET);
        assertBindingTypes(new FieldsModuleTestcase.SingletonTargets(), bindingTypes, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void targetsFieldsModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.TARGET);
        bindingTypes.add(BindingTypes.NAMED_TARGET);
        assertBindingTypes(new FieldsModuleTestcase.Targets(), bindingTypes, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void singletonProviderTypesFieldsModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.PROVIDER_TYPE);
        bindingTypes.add(BindingTypes.NAMED_PROVIDER_TYPE);
        assertBindingTypes(new FieldsModuleTestcase.SingletonProviderTypes(), bindingTypes, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void providerTypesFieldsModule() throws Exception {
        List<BindingTypes> bindingTypes = new ArrayList<>();
        bindingTypes.add(BindingTypes.PROVIDER_TYPE);
        bindingTypes.add(BindingTypes.NAMED_PROVIDER_TYPE);
        assertBindingTypes(new FieldsModuleTestcase.ProviderTypes(), bindingTypes, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void instancesFieldsModule() throws Exception {
        List<BindingTypes> bindings = new ArrayList<>();
        bindings.add(BindingTypes.INSTANCE);
        bindings.add(BindingTypes.NAMED_INSTANCE);
        assertBindingTypes(new FieldsModuleTestcase.Instances(), bindings, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void singletonProvidersFieldsModule() throws Exception {
        List<BindingTypes> bindings = new ArrayList<>();
        bindings.add(BindingTypes.PROVIDER);
        bindings.add(BindingTypes.NAMED_PROVIDER);
        assertBindingTypes(new FieldsModuleTestcase.SingletonProviders(), bindings, true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void providersFieldsModule() throws Exception {
        List<BindingTypes> bindings = new ArrayList<>();
        bindings.add(BindingTypes.PROVIDER);
        bindings.add(BindingTypes.NAMED_PROVIDER);
        assertBindingTypes(new FieldsModuleTestcase.Providers(), bindings, false);
    }

    @SuppressWarnings("unchecked")
    private void assertBindingTypes(Object testCase, List<BindingTypes> bindingTypes, boolean singleton) throws Exception {
        // given:
        GriffonApplication application = new DefaultGriffonApplication();
        TestApplicationBootstrapper bootstrapper = new TestApplicationBootstrapper(application);
        bootstrapper.setTestCase(testCase);

        // when:
        List<Module> modules = bootstrapper.loadModules();

        // then:
        Map<Key<?>, Binding<?>> bindings = getBindings(modules);
        assertTrue(bindings.size() > 1);
        for (Map.Entry<Key<?>, Binding<?>> e : bindings.entrySet()) {
            Binding<?> binding = e.getValue();
            if (Atom.class.isAssignableFrom(binding.getSource())) {
                if (binding instanceof TargetBinding) {
                    assertTargetBinding((TargetBinding) binding, bindingTypes, singleton);
                } else if (binding instanceof ProviderTypeBinding) {
                    assertProviderTypeBinding((ProviderTypeBinding) binding, bindingTypes, singleton);
                } else if (binding instanceof InstanceBinding) {
                    assertInstanceBinding((InstanceBinding) binding, bindingTypes);
                } else if (binding instanceof ProviderBinding) {
                    assertProviderBinding((ProviderBinding) binding, bindingTypes, singleton);
                } else {
                    fail("Did not expect a binding of type " + binding.getClass().getName());
                }
            }
        }

        if (!bindingTypes.isEmpty()) {
            StringBuilder b = new StringBuilder();
            for (BindingTypes fb : bindingTypes) {
                b.append(fb.getLabel()).append("\n");
            }
            fail(b.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private void assertTargetBinding(TargetBinding<? extends Atom> binding, List<BindingTypes> bindingTypes, boolean singleton) throws Exception {
        if (singleton) {
            assertTrue(binding.isSingleton());
        }
        if (binding.getClassifier() == null) {
            // Hydrogen ?
            Atom atom = instantiate(binding.getTarget());
            assertEquals("Expected a binding for Hydrogen", 1, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.TARGET);
        } else {
            // Helium ?
            assertName(binding.getClassifier(), "Helium");
            Atom atom = instantiate(binding.getTarget());
            assertEquals("Expected a binding for Helium", 2, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.NAMED_TARGET);
        }
    }

    @SuppressWarnings("unchecked")
    private void assertProviderTypeBinding(ProviderTypeBinding binding, List<BindingTypes> bindingTypes, boolean singleton) throws Exception {
        if (singleton) {
            assertTrue(binding.isSingleton());
        }
        if (binding.getClassifier() == null) {
            // Litium ?
            Atom atom = instantiateProvider(binding.getProviderType());
            assertEquals("Expected a binding for Litium", 3, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.PROVIDER_TYPE);
        } else {
            // Berilium ?
            assertName(binding.getClassifier(), "Berilium");
            Atom atom = instantiateProvider(binding.getProviderType());
            assertEquals("Expected a binding for Berilium", 4, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.NAMED_PROVIDER_TYPE);
        }
    }

    @SuppressWarnings("unchecked")
    private void assertInstanceBinding(InstanceBinding<Atom> binding, List<BindingTypes> bindingTypes) {
        if (binding.getClassifier() == null) {
            // Boron ?
            Atom atom = binding.getInstance();
            assertEquals("Expected a binding for Boron", 5, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.INSTANCE);
        } else {
            // Carbon ?
            assertName(binding.getClassifier(), "Carbon");
            Atom atom = binding.getInstance();
            assertEquals("Expected a binding for Carbon", 6, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.NAMED_INSTANCE);
        }
    }

    @SuppressWarnings("unchecked")
    private void assertProviderBinding(ProviderBinding<Atom> binding, List<BindingTypes> bindingTypes, boolean singleton) {
        if (singleton) {
            assertTrue(binding.isSingleton());
        }
        if (binding.getClassifier() == null) {
            // Nitrogen ?
            Atom atom = binding.getProvider().get();
            assertEquals("Expected a binding for Nitrogen", 7, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.PROVIDER);
        } else {
            // Oxygen ?
            assertName(binding.getClassifier(), "Oxygen");
            Atom atom = binding.getProvider().get();
            assertEquals("Expected a binding for Oxygen", 8, atom.getNumElectrons());
            bindingTypes.remove(BindingTypes.NAMED_PROVIDER);
        }
    }

    private static Atom instantiate(Class<? extends Atom> atomClass) throws Exception {
        return atomClass.newInstance();
    }

    private static Atom instantiateProvider(Class<Provider<? extends Atom>> providerClass) throws Exception {
        return providerClass.newInstance().get();
    }

    private static void assertName(Annotation annotation, String name) {
        assertTrue(annotation instanceof Named);
        Named named = (Named) annotation;
        assertEquals(name, named.value());
    }

    @Nonnull
    protected Map<Key<?>, Binding<?>> getBindings(@Nonnull Collection<Module> modules) {
        Map<Key<?>, Binding<?>> map = new LinkedHashMap<>();

        for (Module module : modules) {
            for (Binding<?> binding : module.getBindings()) {
                map.put(Key.of(binding), binding);
            }
        }

        return map;
    }
}
