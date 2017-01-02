/*
 * Copyright 2008-2017 the original author or authors.
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
import org.codehaus.griffon.runtime.core.TestApplicationBootstrapper;
import org.codehaus.griffon.runtime.swing.FestAwareSwingGriffonApplication;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.FrameFixture;

import javax.annotation.Nonnull;
import java.awt.Frame;
import java.lang.reflect.Field;

import static org.fest.swing.core.BasicRobot.robotWithNewAwtHierarchy;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonFestRule extends GriffonUnitRule {
    protected FrameFixture window;

    public GriffonFestRule() {
        this(FestAwareSwingGriffonApplication.EMPTY_ARGS, TestApplicationBootstrapper.class);
    }

    public GriffonFestRule(@Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        this(FestAwareSwingGriffonApplication.EMPTY_ARGS, applicationBootstrapper);
    }

    public GriffonFestRule(@Nonnull String[] startupArgs) {
        this(startupArgs, TestApplicationBootstrapper.class);
    }

    public GriffonFestRule(@Nonnull String[] startupArgs, @Nonnull Class<? extends ApplicationBootstrapper> applicationBootstrapper) {
        super(startupArgs, FestAwareSwingGriffonApplication.class, applicationBootstrapper);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void before(@Nonnull GriffonApplication application, @Nonnull Object target) throws Throwable {
        Robot robot = robotWithNewAwtHierarchy();

        application.startup();
        application.ready();
        Object startingWindow = application.getWindowManager().getStartingWindow();
        if (startingWindow != null) {
            if (startingWindow instanceof Frame) {
                window = new FrameFixture(robot, (Frame) startingWindow);
            }
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
