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
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import javax.application.event.EventHandler;
import java.util.concurrent.TimeoutException;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class GriffonFunctionalTestFXExtension extends TestFX
    implements TestInstancePostProcessor, BeforeAllCallback, AfterAllCallback, TestExecutionExceptionHandler, ExecutionCondition {
    private final static Namespace GRIFFON = create("griffon");
    private final static String TEST_INSTANCE = "testInstance";
    private final static String APPLICATION = "application";
    private final static String FAILURES = "failures";

    private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult.enabled("Execution was successful");
    private static final ConditionEvaluationResult DISABLED = ConditionEvaluationResult.disabled("Execution failure");

    public static class Builder {
        private String windowName = "mainWindow";
        private Duration timeout = new Duration(10, SECONDS);
        private String[] startupArgs = DefaultGriffonApplication.EMPTY_ARGS;
        private Class<? extends TestJavaFXGriffonApplication> applicationClass = TestJavaFXGriffonApplication.class;

        public Builder windowName(String windowName) {
            if (!isBlank(windowName)) {
                this.windowName = windowName;
            }
            return this;
        }

        public Builder timeout(Duration timeout) {
            if (timeout != null) {
                this.timeout = timeout;
            }
            return this;
        }

        public Builder startupArgs(String[] args) {
            if (args != null) {
                startupArgs = args;
            }
            return this;
        }

        public Builder applicationClass(Class<? extends TestJavaFXGriffonApplication> clazz) {
            if (clazz != null) {
                applicationClass = clazz;
            }
            return this;
        }

        public GriffonFunctionalTestFXExtension build() {
            return new GriffonFunctionalTestFXExtension(applicationClass, windowName, timeout, startupArgs);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final String windowName;
    private final Duration timeout;
    private final String[] startupArgs;
    private final Class<? extends TestJavaFXGriffonApplication> applicationClass;

    private JavaFXGriffonApplication application;

    private GriffonFunctionalTestFXExtension(@Nonnull Class<? extends TestJavaFXGriffonApplication> applicationClass,
                                             @Nonnull String windowName,
                                             @Nonnull Duration timeout,
                                             @Nonnull String[] startupArgs) {
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
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        context.getStore(GRIFFON).put(TEST_INSTANCE, testInstance);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        try {
            FxToolkit.registerPrimaryStage();

            application = (JavaFXGriffonApplication) FxToolkit.setupApplication(applicationClass);
            context.getStore(GRIFFON).put(APPLICATION, application);

            Object target = context.getStore(GRIFFON).get(TEST_INSTANCE);
            injectMembers(target);

            WindowShownHandler startingWindow = new WindowShownHandler(windowName);
            application.getEventRouter().subscribe(startingWindow);

            await().timeout(timeout).until(startingWindow::isShowing);
        } catch (TimeoutException e) {
            throw new GriffonException("An error occurred while starting up the application", e);
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        context.getStore(GRIFFON).put(FAILURES, true);
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Boolean failures = (Boolean) context.getStore(GRIFFON).get(FAILURES);
        return failures != null && failures ? DISABLED : ENABLED;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (application != null) {
            application.shutdown();
            try {
                FxToolkit.cleanupApplication(application);
            } catch (TimeoutException e) {
                throw new GriffonException("An error occurred while shutting down the application", e);
            } finally {
                application = null;

                release(new KeyCode[0]);
                release(new MouseButton[0]);
                WaitForAsyncUtils.waitForFxEvents();
            }
        }
    }

    public void injectMembers(@Nonnull Object target) {
        requireNonNull(target, "Argument 'target' must not be null");
        application.getInjector().injectMembers(target);
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