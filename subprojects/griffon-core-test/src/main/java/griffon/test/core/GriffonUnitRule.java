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
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonUnitRule implements MethodRule {
    private final String[] startupArgs;
    private final Class<? extends GriffonApplication> applicationClass;
    private final Class<? extends ApplicationBootstrapper> applicationBootstrapper;

    public GriffonUnitRule() {
        this(DefaultGriffonApplication.EMPTY_ARGS, DefaultGriffonApplication.class, TestApplicationBootstrapper.class);
    }

    public GriffonUnitRule(@Nonnull Class<? extends GriffonApplication> applicationClass) {
        this(DefaultGriffonApplication.EMPTY_ARGS, applicationClass, TestApplicationBootstrapper.class);
    }

    public GriffonUnitRule(@Nonnull Class<? extends GriffonApplication> applicationClass, @Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        this(DefaultGriffonApplication.EMPTY_ARGS, applicationClass, applicationBootstrapper);
    }

    public GriffonUnitRule(@Nonnull String[] startupArgs) {
        this(startupArgs, DefaultGriffonApplication.class, TestApplicationBootstrapper.class);
    }

    public GriffonUnitRule(@Nonnull String[] startupArgs, @Nonnull Class<? extends GriffonApplication> applicationClass) {
        this(startupArgs, applicationClass, TestApplicationBootstrapper.class);
    }

    public GriffonUnitRule(@Nonnull String[] startupArgs, @Nonnull Class<? extends GriffonApplication> applicationClass, @Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        requireNonNull(startupArgs, "Argument 'startupArgs' must not be null");
        this.startupArgs = Arrays.copyOf(startupArgs, startupArgs.length);
        this.applicationClass = requireNonNull(applicationClass, "Argument 'applicationClass' must not be null");
        this.applicationBootstrapper = requireNonNull(applicationBootstrapper, "Argument 'applicationBootstrapper' must not be null");
        if (!Environment.isSystemSet()) {
            System.setProperty(Environment.KEY, Environment.TEST.getName());
        }
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                GriffonApplication application = instantiateApplication();
                ApplicationBootstrapper bootstrapper = instantiateApplicationBootstrapper(application, target);

                bootstrapper.bootstrap();
                application.initialize();
                application.getInjector().injectMembers(target);
                handleTestForAnnotation(application, target);

                before(application, target);
                try {
                    base.evaluate();
                } finally {
                    after(application, target);
                }
            }
        };
    }

    protected void before(@Nonnull GriffonApplication application, @Nonnull Object target) {

    }

    protected void after(@Nonnull GriffonApplication application, @Nonnull Object target) {
        application.shutdown();
    }

    @Nonnull
    private GriffonApplication instantiateApplication() throws Exception {
        String[] array = new String[0];
        Constructor<? extends GriffonApplication> ctor = applicationClass.getDeclaredConstructor(array.getClass());
        return ctor.newInstance(new Object[]{startupArgs});
    }

    @Nonnull
    private ApplicationBootstrapper instantiateApplicationBootstrapper(@Nonnull GriffonApplication application, @Nonnull Object testCase) throws Exception {
        Constructor<? extends ApplicationBootstrapper> constructor = applicationBootstrapper.getDeclaredConstructor(GriffonApplication.class);
        ApplicationBootstrapper bootstrapper = constructor.newInstance(application);
        if (bootstrapper instanceof TestCaseAware) {
            ((TestCaseAware) bootstrapper).setTestCase(testCase);
        }
        return bootstrapper;
    }

    private void handleTestForAnnotation(@Nonnull GriffonApplication application, @Nonnull Object target) throws Exception {
        TestFor testFor = target.getClass().getAnnotation(TestFor.class);
        if (testFor != null) {
            Class<? extends GriffonArtifact> artifactClass = testFor.value();
            GriffonArtifact artifact = application.getArtifactManager().newInstance(artifactClass);
            GriffonClass griffonClass = artifact.getGriffonClass();
            Field artifactField = target.getClass().getDeclaredField(griffonClass.getArtifactType());
            artifactField.setAccessible(true);
            artifactField.set(target, artifact);
        }
    }
}
