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
package griffon.test.core;

import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.env.Environment;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Optional;

import static java.util.Arrays.copyOf;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public abstract class AbstractGriffonUnitExtension implements TestInstancePostProcessor, BeforeEachCallback, AfterEachCallback {
    protected final static Namespace GRIFFON = create("griffon");
    protected final static String TEST_INSTANCE = "testInstance";
    protected final static String APPLICATION = "application";

    protected final String[] startupArgs;
    protected final Class<? extends GriffonApplication> applicationClass;
    protected final Class<? extends ApplicationBootstrapper> applicationBootstrapper;


    protected AbstractGriffonUnitExtension(String[] startupArgs, Class<? extends GriffonApplication> applicationClass, Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        requireNonNull(startupArgs, "Argument 'startupArgs' must not be null");
        this.startupArgs = copyOf(startupArgs, startupArgs.length);
        this.applicationClass = requireNonNull(applicationClass, "Argument 'applicationClass' must not be null");
        this.applicationBootstrapper = requireNonNull(applicationBootstrapper, "Argument 'applicationBootstrapper' must not be null");
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
        before(application, target);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        GriffonApplication application = (GriffonApplication) context.getStore(GRIFFON).get(APPLICATION);
        Object target = context.getStore(GRIFFON).get(TEST_INSTANCE);

        if (application != null) {
            after(application, target);
            application.shutdown();
        }
    }

    protected void before(GriffonApplication application, Object target) {
        // noop
    }

    protected void after(GriffonApplication application, Object target) {
        // noop
    }

    protected GriffonApplication instantiateApplication() throws Exception {
        String[] array = new String[0];
        Constructor<? extends GriffonApplication> ctor = applicationClass.getDeclaredConstructor(array.getClass());
        return ctor.newInstance(new Object[]{startupArgs});
    }

    protected ApplicationBootstrapper instantiateApplicationBootstrapper(GriffonApplication application, Object testCase) throws Exception {
        Constructor<? extends ApplicationBootstrapper> constructor = applicationBootstrapper.getDeclaredConstructor(GriffonApplication.class);
        ApplicationBootstrapper bootstrapper = constructor.newInstance(application);
        if (bootstrapper instanceof TestCaseAware) {
            ((TestCaseAware) bootstrapper).setTestCase(testCase);
        }
        return bootstrapper;
    }

    protected void handleTestForAnnotation(ExtensionContext context) throws Exception {
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

    protected Optional<TestFor> retrieveAnnotationFromTestClasses(final ExtensionContext context) {
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
