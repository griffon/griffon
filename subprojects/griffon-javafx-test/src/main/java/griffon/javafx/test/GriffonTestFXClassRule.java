/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.javafx.test;

import griffon.core.ApplicationEvent;
import griffon.core.RunnableWithArgs;
import griffon.core.env.Environment;
import griffon.exceptions.GriffonException;
import griffon.javafx.JavaFXGriffonApplication;
import javafx.stage.Window;
import org.awaitility.Duration;
import org.codehaus.griffon.runtime.core.DefaultGriffonApplication;
import org.codehaus.griffon.runtime.javafx.TestJavaFXGriffonApplication;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testfx.api.FxToolkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeoutException;

import static griffon.javafx.test.TestContext.getTestContext;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

/**
 * A JUnit Rule that starts the application once per test class.
 * Use it in combination with {@code FunctionalJavaFXRunner}.
 *
 * @author Andres Almiray
 * @see griffon.javafx.test.FunctionalJavaFXRunner
 * @since 2.3.0
 */
public class GriffonTestFXClassRule extends TestFX implements TestRule {
    protected final String windowName;
    protected final Duration timeout;
    protected final String[] startupArgs;
    protected final Class<? extends TestJavaFXGriffonApplication> applicationClass;
    protected JavaFXGriffonApplication application;
    private boolean failures = false;

    public GriffonTestFXClassRule(@Nonnull String windowName) {
        this(TestJavaFXGriffonApplication.class, windowName, new Duration(10, SECONDS), DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXClassRule(@Nonnull String windowName, @Nonnull Duration timeout) {
        this(TestJavaFXGriffonApplication.class, windowName, timeout, DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXClassRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName) {
        this(applicationClass, windowName, new Duration(10, SECONDS), DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXClassRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName, @Nonnull Duration timeout) {
        this(applicationClass, windowName, timeout, DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXClassRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName, @Nonnull String[] startupArgs) {
        this(applicationClass, windowName, new Duration(10, SECONDS), DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXClassRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName, @Nonnull Duration timeout, @Nonnull String[] startupArgs) {
        this.applicationClass = requireNonNull(applicationClass, "Argument 'applicationClass' must not be null");
        this.windowName = requireNonBlank(windowName, "Argument 'windowName' cannot be blank");
        this.timeout = requireNonNull(timeout, "Argument 'timeout' cannot be blank");
        requireNonNull(startupArgs, "Argument 'startupArgs' must not be null");
        this.startupArgs = new String[startupArgs.length];
        System.arraycopy(startupArgs, 0, this.startupArgs, 0, startupArgs.length);
        if (!Environment.isSystemSet()) {
            System.setProperty(Environment.KEY, Environment.TEST.getName());
        }
    }

    public void setup() {
        initialize();

        try {
            FxToolkit.registerPrimaryStage();

            application = (JavaFXGriffonApplication) FxToolkit.setupApplication(applicationClass);
            WindowShownHandler startingWindow = new WindowShownHandler(windowName);
            application.getEventRouter().addEventListener(ApplicationEvent.WINDOW_SHOWN.getName(), startingWindow);

            await().timeout(timeout).until(startingWindow::isShowing);
        } catch (TimeoutException e) {
            throw new GriffonException("An error occurred while starting up the application", e);
        }
    }

    public void cleanup() {
        if (application != null) {
            application.shutdown();
            try {
                FxToolkit.cleanupApplication(application);
            } catch (TimeoutException e) {
                throw new GriffonException("An error occurred while shutting down the application", e);
            } finally {
                application = null;
            }
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setup();
                try {
                    base.evaluate();
                } finally {
                    cleanup();
                }
            }
        };
    }

    public void injectMembers(@Nonnull Object target) {
        requireNonNull(target, "Argument 'target' must not be null");
        application.getInjector().injectMembers(target);
    }

    public boolean hasFailures() {
        return failures;
    }

    public void setFailures(boolean failures) {
        this.failures = failures;
    }

    @Nullable
    public <W extends Window> W managedWindow(@Nonnull String name) {
        return (W) application.getWindowManager().findWindow(name);
    }

    protected void initialize() {
        getTestContext().setWindowName(windowName);
    }

    private static class WindowShownHandler implements RunnableWithArgs {
        private final String windowName;
        private boolean showing;

        private WindowShownHandler(String windowName) {
            this.windowName = windowName;
        }

        public boolean isShowing() {
            return showing;
        }

        @Override
        public void run(Object... args) {
            if (args != null && args.length > 0 && args[0] instanceof CharSequence) {
                showing = windowName.equals(String.valueOf(args[0]));
            }
        }
    }
}
