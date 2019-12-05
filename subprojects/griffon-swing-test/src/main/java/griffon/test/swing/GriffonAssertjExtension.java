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
package griffon.test.swing;

import griffon.annotations.core.Nonnull;
import griffon.core.ApplicationBootstrapper;
import griffon.core.GriffonApplication;
import griffon.test.core.AbstractGriffonUnitExtension;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.FrameFixture;
import org.codehaus.griffon.test.core.DefaultGriffonApplication;
import org.codehaus.griffon.test.core.TestApplicationBootstrapper;
import org.codehaus.griffon.test.swing.AssertjAwareSwingGriffonApplication;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.awt.Frame;

import static org.assertj.swing.core.BasicRobot.robotWithNewAwtHierarchy;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class GriffonAssertjExtension extends AbstractGriffonUnitExtension {
    public static class Builder {
        private String[] startupArgs = DefaultGriffonApplication.EMPTY_ARGS;
        private Class<? extends GriffonApplication> applicationClass = AssertjAwareSwingGriffonApplication.class;
        private Class<? extends ApplicationBootstrapper> applicationBootstrapper = TestApplicationBootstrapper.class;

        public Builder setStartupArgs(String[] startupArgs) {
            if (startupArgs != null) {
                this.startupArgs = startupArgs;
            }
            return this;
        }

        public Builder setApplicationClass(Class<? extends GriffonApplication> applicationClass) {
            if (applicationClass != null) {
                this.applicationClass = applicationClass;
            }
            return this;
        }

        public Builder setApplicationBootstrapper(Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
            if (applicationBootstrapper != null) {
                this.applicationBootstrapper = applicationBootstrapper;
            }
            return this;
        }

        public GriffonAssertjExtension build() {
            return new GriffonAssertjExtension(startupArgs, applicationClass, applicationBootstrapper);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private FrameFixture window;

    private GriffonAssertjExtension(@Nonnull String[] startupArgs,
                                    @Nonnull Class<? extends GriffonApplication> applicationClass,
                                    @Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        super(startupArgs, applicationClass, applicationBootstrapper);
    }

    public final FrameFixture getWindow() {
        return window;
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        context.getStore(GRIFFON).put(TEST_INSTANCE, testInstance);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void before(@Nonnull GriffonApplication application, @Nonnull Object target) {
        Robot robot = robotWithNewAwtHierarchy();

        application.startup();
        application.ready();
        Object startingWindow = application.getWindowManager().getStartingWindow();
        if (startingWindow != null && startingWindow instanceof Frame) {
            window = new FrameFixture(robot, (Frame) startingWindow);
        }

        robot.showWindow((Frame) startingWindow, null, false);
        window.moveToFront();
    }

    @Override
    protected void after(@Nonnull GriffonApplication application, @Nonnull Object target) {
        if (window != null) {
            window.cleanUp();
        }
        super.after(application, target);
    }
}