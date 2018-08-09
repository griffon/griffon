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
package griffon.test.pivot;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.env.Environment;
import griffon.test.core.TestFor;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.codehaus.griffon.test.core.DefaultGriffonApplication;
import org.codehaus.griffon.test.pivot.TestDesktopPivotApplication;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import javax.swing.SwingUtilities;
import java.lang.reflect.Field;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonPivotRule implements MethodRule {
    private final String[] startupArgs;

    public GriffonPivotRule() {
        this(DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonPivotRule(@Nonnull String[] startupArgs) {
        requireNonNull(startupArgs, "Argument 'startupArgs' must not be null");
        this.startupArgs = startupArgs = Arrays.copyOf(startupArgs, startupArgs.length);
        if (!Environment.isSystemSet()) {
            System.setProperty(Environment.KEY, Environment.TEST.getName());
        }
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                TestDesktopPivotApplication.init(target);
                SwingUtilities.invokeAndWait(() -> DesktopApplicationContext.main(TestDesktopPivotApplication.class, startupArgs));
                TestDesktopPivotApplication.getLatch().await();
                GriffonApplication application = TestDesktopPivotApplication.getApplication();
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

    protected void before(@Nonnull GriffonApplication application, @Nonnull Object target) throws Throwable {

    }

    protected void after(@Nonnull GriffonApplication application, @Nonnull Object target) {
        application.shutdown();
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
