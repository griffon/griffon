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
package griffon.test.core;

import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.env.Environment;
import org.codehaus.griffon.test.core.DefaultGriffonApplication;
import org.codehaus.griffon.test.core.TestApplicationBootstrapper;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class GriffonUnitExtension implements TestInstancePostProcessor, BeforeEachCallback, AfterEachCallback {
    private final static Namespace GRIFFON = create("griffon");
    private final static String TEST_INSTANCE = "testInstance";
    private final static String APPLICATION = "application";

    public static class Builder {
        private String[] startupArgs = DefaultGriffonApplication.EMPTY_ARGS;
        private Class<? extends GriffonApplication> applicationClass = DefaultGriffonApplication.class;
        private Class<? extends ApplicationBootstrapper> applicationBootstrapper = TestApplicationBootstrapper.class;

        public Builder startupArgs(String[] args) {
            if (args != null) {
                startupArgs = args;
            }
            return this;
        }

        public Builder applicationClass(Class<? extends GriffonApplication> clazz) {
            if (clazz != null) {
                applicationClass = clazz;
            }
            return this;
        }

        public Builder applicationBootstrapper(Class<? extends ApplicationBootstrapper> bootstrapper) {
            if (bootstrapper != null) {
                applicationBootstrapper = bootstrapper;
            }
            return this;
        }

        public GriffonUnitExtension build() {
            return new GriffonUnitExtension(startupArgs, applicationClass, applicationBootstrapper);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String[] startupArgs;
    private final Class<? extends GriffonApplication> applicationClass;
    private final Class<? extends ApplicationBootstrapper> applicationBootstrapper;

    public GriffonUnitExtension() {
        this(DefaultGriffonApplication.EMPTY_ARGS, DefaultGriffonApplication.class, TestApplicationBootstrapper.class);
    }

    private GriffonUnitExtension(String[] startupArgs, Class<? extends GriffonApplication> applicationClass, Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        this.startupArgs = startupArgs;
        this.applicationClass = applicationClass;
        this.applicationBootstrapper = applicationBootstrapper;
        if (!Environment.isSystemSet()) {
            System.setProperty(Environment.KEY, Environment.TEST.getName());
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        context.getStore(GRIFFON).put(TEST_INSTANCE, testInstance);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Object target = context.getStore(GRIFFON).get(TEST_INSTANCE);

        GriffonApplication application = instantiateApplication();
        context.getStore(GRIFFON).put(APPLICATION, application);

        ApplicationBootstrapper bootstrapper = instantiateApplicationBootstrapper(application, target);

        bootstrapper.bootstrap();
        application.initialize();
        application.getInjector().injectMembers(target);
        handleTestForAnnotation(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        GriffonApplication application = (GriffonApplication) context.getStore(GRIFFON).get(APPLICATION);
        if (application != null) {
            application.shutdown();
        }
    }

    private GriffonApplication instantiateApplication() throws Exception {
        String[] array = new String[0];
        Constructor<? extends GriffonApplication> ctor = applicationClass.getDeclaredConstructor(array.getClass());
        return ctor.newInstance(new Object[]{startupArgs});
    }

    private ApplicationBootstrapper instantiateApplicationBootstrapper(GriffonApplication application, Object testCase) throws Exception {
        Constructor<? extends ApplicationBootstrapper> constructor = applicationBootstrapper.getDeclaredConstructor(GriffonApplication.class);
        ApplicationBootstrapper bootstrapper = constructor.newInstance(application);
        if (bootstrapper instanceof TestCaseAware) {
            ((TestCaseAware) bootstrapper).setTestCase(testCase);
        }
        return bootstrapper;
    }

    private void handleTestForAnnotation(ExtensionContext context) throws Exception {
        Object target = context.getStore(GRIFFON).get(TEST_INSTANCE);
        GriffonApplication application = (GriffonApplication) context.getStore(GRIFFON).get(APPLICATION);

        retrieveAnnotationFromTestClasses(context)
            .ifPresent(testFor -> {
                try {
                    Class<? extends GriffonArtifact> artifactClass = testFor.value();
                    GriffonArtifact artifact = application.getArtifactManager().newInstance(artifactClass);
                    GriffonClass griffonClass = artifact.getGriffonClass();
                    Field artifactField = target.getClass().getDeclaredField(griffonClass.getArtifactType());
                    artifactField.setAccessible(true);
                    artifactField.set(target, artifact);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            });
    }

    private Optional<TestFor> retrieveAnnotationFromTestClasses(final ExtensionContext context) {
        ExtensionContext currentContext = context;
        Optional<TestFor> annotation;

        do {
            annotation = findAnnotation(currentContext.getElement(), TestFor.class);

            if (!currentContext.getParent().isPresent()) {
                break;
            }

            currentContext = currentContext.getParent().get();
        } while (!annotation.isPresent() && currentContext != context.getRoot());

        return annotation;
    }
}
