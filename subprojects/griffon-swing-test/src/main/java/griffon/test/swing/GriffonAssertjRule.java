/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
import griffon.test.core.GriffonUnitRule;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.FrameFixture;
import org.codehaus.griffon.test.core.TestApplicationBootstrapper;
import org.codehaus.griffon.test.swing.AssertjAwareSwingGriffonApplication;

import java.awt.*;
import java.lang.reflect.Field;

import static org.assertj.swing.core.BasicRobot.robotWithNewAwtHierarchy;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonAssertjRule extends GriffonUnitRule {
    protected FrameFixture window;

    public GriffonAssertjRule() {
        this(AssertjAwareSwingGriffonApplication.EMPTY_ARGS, TestApplicationBootstrapper.class);
    }

    public GriffonAssertjRule(@Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        this(AssertjAwareSwingGriffonApplication.EMPTY_ARGS, applicationBootstrapper);
    }

    public GriffonAssertjRule(@Nonnull String[] startupArgs) {
        this(startupArgs, TestApplicationBootstrapper.class);
    }

    public GriffonAssertjRule(@Nonnull String[] startupArgs, @Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        super(startupArgs, AssertjAwareSwingGriffonApplication.class, applicationBootstrapper);
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

        try {
            Field windowField = target.getClass().getDeclaredField("window");
            windowField.setAccessible(true);
            windowField.set(target, window);
        } catch (Exception e) {
            after(application, target);
            throw new IllegalStateException("Class " + target.getClass().getName() +
                " does not define a field named 'window' of type " + FrameFixture.class.getName(), e);
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
