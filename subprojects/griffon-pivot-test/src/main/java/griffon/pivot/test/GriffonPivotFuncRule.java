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
package griffon.pivot.test;

import griffon.core.GriffonApplication;
import griffon.exceptions.GriffonException;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Window;
import org.codehaus.griffon.runtime.pivot.TestDesktopPivotApplication;

import javax.annotation.Nonnull;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import static griffon.pivot.support.PivotUtils.findComponentByName;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonPivotFuncRule extends GriffonPivotRule {
    private static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";

    private Window window;

    public GriffonPivotFuncRule() {
    }

    public GriffonPivotFuncRule(@Nonnull String[] startupArgs) {
        super(startupArgs);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void before(@Nonnull GriffonApplication application, @Nonnull Object target) throws Throwable {
        application.startup();
        application.ready();
        TestDesktopPivotApplication.getReadyLatch().await();

        window = (Window) application.getWindowManager().getStartingWindow();
    }

    @Nonnull
    public Window getWindow() {
        return window;
    }

    @Nonnull
    public <T> T find(@Nonnull String name, Class<T> type) {
        requireNonBlank(name, "Argument 'name' must not be blank");
        requireNonNull(type, "Argument 'type' must not be null");

        Component component = findComponentByName(name, window);
        if (component != null) {
            if (type.isAssignableFrom(component.getClass())) {
                return type.cast(component);
            }
            throw new IllegalArgumentException("Could not find a component name '"
                + name + "' with type " + type.getName()
                + "; found type " + component.getClass().getName() + " instead");
        }

        throw new IllegalArgumentException("Could not find a component name '" + name + "' with type " + type.getName());
    }

    public void runInsideUISync(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        Throwable t = null;
        final CountDownLatch latch = new CountDownLatch(1);
        final Runnable worker = new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    latch.countDown();
                }
            }
        };
        try {
            EventQueue.invokeAndWait(worker);
        } catch (InterruptedException e) {
            latch.countDown();
            t = e;
        } catch (InvocationTargetException e) {
            latch.countDown();
            t = e.getTargetException();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new GriffonException(e);
        }

        if (t != null) {
            if (t instanceof AssertionError) {
                throw (AssertionError) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new GriffonException(t);
            }
        }
    }

    public void runInsideUIAsync(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        final Throwable[] ts = new Throwable[1];
        final CountDownLatch latch = new CountDownLatch(1);
        final Runnable worker = new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    ts[0] = t;
                } finally {
                    latch.countDown();
                }
            }
        };

        EventQueue.invokeLater(worker);

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new GriffonException(e);
        }

        if (ts[0] != null) {
            if (ts[0] instanceof AssertionError) {
                throw (AssertionError) ts[0];
            } else if (ts[0] instanceof RuntimeException) {
                throw (RuntimeException) ts[0];
            } else {
                throw new GriffonException(ts[0]);
            }
        }
    }
}
