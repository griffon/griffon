/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.test.javafx;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.env.Environment;
import griffon.core.events.WindowShownEvent;
import griffon.exceptions.GriffonException;
import griffon.javafx.JavaFXGriffonApplication;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Window;
import org.awaitility.Duration;
import org.codehaus.griffon.test.core.DefaultGriffonApplication;
import org.codehaus.griffon.test.javafx.TestJavaFXGriffonApplication;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import javax.application.event.EventHandler;
import java.util.concurrent.TimeoutException;

import static griffon.test.javafx.TestContext.getTestContext;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

/**
 * A JUnit Rule that starts an application for each test method.
 *
 * @author Andres Almiray
 * @since 2.3.0
 */
public class GriffonTestFXRule extends TestFX implements MethodRule {
    protected final String windowName;
    protected final Duration timeout;
    protected final String[] startupArgs;
    protected final Class<? extends TestJavaFXGriffonApplication> applicationClass;
    protected JavaFXGriffonApplication application;

    public GriffonTestFXRule(@Nonnull String windowName) {
        this(TestJavaFXGriffonApplication.class, windowName, new Duration(10, SECONDS), DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXRule(@Nonnull String windowName, @Nonnull Duration timeout) {
        this(TestJavaFXGriffonApplication.class, windowName, timeout, DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName) {
        this(applicationClass, windowName, new Duration(10, SECONDS), DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName, @Nonnull Duration timeout) {
        this(applicationClass, windowName, timeout, DefaultGriffonApplication.EMPTY_ARGS);
    }

    public GriffonTestFXRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName, @Nonnull String[] startupArgs) {
        this(applicationClass, windowName, new Duration(10, SECONDS), startupArgs);
    }

    public GriffonTestFXRule(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass, @Nonnull String windowName, @Nonnull Duration timeout, @Nonnull String[] startupArgs) {
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

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        initialize(target);

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                FxToolkit.registerPrimaryStage();

                application = (JavaFXGriffonApplication) FxToolkit.setupApplication(applicationClass);
                WindowShownHandler startingWindow = new WindowShownHandler(windowName);
                application.getEventRouter().subscribe(startingWindow);
                application.getInjector().injectMembers(target);

                await().timeout(timeout).until(startingWindow::isShowing);

                before(application, target);
                try {
                    base.evaluate();
                } finally {
                    after(application, target);
                }
            }
        };
    }

    protected void initialize(Object target) {
        getTestContext().setTestCase(target);
        getTestContext().setWindowName(windowName);
    }

    protected void before(@Nonnull JavaFXGriffonApplication application, @Nonnull Object target) {

    }

    protected void after(@Nonnull JavaFXGriffonApplication application, @Nonnull Object target) {
        if (application != null) {
            application.shutdown();
            try {
                FxToolkit.cleanupApplication(application);
            } catch (TimeoutException e) {
                throw new GriffonException("An error occurred while shutting down the application", e);
            } finally {
                this.application = null;
                release(new KeyCode[0]);
                release(new MouseButton[0]);
                WaitForAsyncUtils.waitForFxEvents();
            }
        }
    }

    @Nullable
    public <W extends Window> W managedWindow(@Nonnull String name) {
        return (W) application.getWindowManager().findWindow(name);
    }

    private static class WindowShownHandler {
        private final String windowName;
        private boolean showing;

        private WindowShownHandler(String windowName) {
            this.windowName = windowName;
        }

        public boolean isShowing() {
            return showing;
        }

        @EventHandler
        public void handleWindowShownEvent(@Nonnull WindowShownEvent event) {
            showing = windowName.equals(String.valueOf(event.getName()));
        }
    }
}
