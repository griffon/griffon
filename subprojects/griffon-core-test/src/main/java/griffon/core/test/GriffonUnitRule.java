/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.core.test;

import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import org.codehaus.griffon.runtime.core.DefaultGriffonApplication;
import org.codehaus.griffon.runtime.core.TestApplicationBootstrapper;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonUnitRule implements MethodRule {
    private Class<? extends GriffonApplication> applicationClass;
    private Class<? extends ApplicationBootstrapper> applicationBootstrapper;

    public GriffonUnitRule() {
        this.applicationClass = DefaultGriffonApplication.class;
        this.applicationBootstrapper = TestApplicationBootstrapper.class;
    }

    public GriffonUnitRule(@Nonnull Class<? extends GriffonApplication> applicationClass) {
        this.applicationClass = requireNonNull(applicationClass, "Argument 'applicationClass' cannot be null");
    }

    public GriffonUnitRule(@Nonnull Class<? extends GriffonApplication> applicationClass, @Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        this.applicationClass = requireNonNull(applicationClass, "Argument 'applicationClass' cannot be null");
        this.applicationBootstrapper = requireNonNull(applicationBootstrapper, "Argument 'applicationBootstrapper' cannot be null");
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

                try {
                    base.evaluate();
                } finally {
                    application.shutdown();
                }
            }
        };
    }

    @Nonnull
    private GriffonApplication instantiateApplication() throws Exception {
        return applicationClass.newInstance();
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
            Class artifactClass = testFor.value();
            GriffonArtifact artifact = application.getArtifactManager().newInstance(artifactClass);
            GriffonClass griffonClass = artifact.getGriffonClass();
            Field artifactField = target.getClass().getDeclaredField(griffonClass.getArtifactType());
            artifactField.setAccessible(true);
            artifactField.set(target, artifact);
        }
    }
}
