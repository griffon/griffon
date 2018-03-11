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
package griffon.pivot;

import griffon.core.ApplicationBootstrapper;
import griffon.exceptions.InstanceNotFoundException;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;
import org.codehaus.griffon.runtime.core.AbstractGriffonApplication;
import org.codehaus.griffon.runtime.pivot.PivotApplicationBootstrapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonClassUtils.setPropertiesNoException;
import static java.util.Collections.singletonList;
import static org.apache.pivot.wtk.ApplicationContext.queueCallback;
import static org.apache.pivot.wtk.ApplicationContext.scheduleCallback;
import static org.apache.pivot.wtk.ApplicationContext.scheduleRecurringCallback;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractPivotGriffonApplication extends AbstractGriffonApplication implements Application {
    public AbstractPivotGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public AbstractPivotGriffonApplication(@Nonnull String[] args) {
        super(args);
    }

    @Override
    public boolean shutdown() {
        if (super.shutdown()) {
            exit();
        }
        return false;
    }

    public void exit() {
        System.exit(0);
    }

    @Nonnull
    @Override
    public Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
        Window window = new Window();
        setPropertiesNoException(window, attributes);
        return window;
    }

    @Override
    public void startup(Display display, org.apache.pivot.collections.Map<String, String> properties) throws Exception {
        ApplicationBootstrapper bootstrapper = createApplicationBootstrapper(display);
        bootstrapper.bootstrap();
        afterStartup();
    }

    @Nonnull
    protected ApplicationBootstrapper createApplicationBootstrapper(@Nonnull Display display) {
        return new PivotApplicationBootstrapper(this, display);
    }

    protected void afterStartup() {
        initialize();
        startup();
        ready();
    }

    @Override
    public boolean shutdown(boolean optional) throws Exception {
        shutdown();
        return false;
    }

    public void suspend() {
        event("AppSuspend", singletonList(this));
    }

    public void resume() {
        event("AppResume", singletonList(this));
    }

    public void schedule(long delay, Runnable callback) {
        scheduleCallback(callback, delay);
    }

    public void scheduleRecurring(long period, Runnable callback) {
        scheduleRecurringCallback(callback, period);
    }

    public void scheduleRecurring(long delay, long period, Runnable callback) {
        scheduleRecurringCallback(callback, delay, period);
    }

    public void queue(Runnable callback) {
        queue(false, callback);
    }

    public void queue(boolean wait, Runnable callback) {
        queueCallback(callback, wait);
    }

    public ApplicationContext.ResourceCacheDictionary getResourceCache() {
        return ApplicationContext.getResourceCache();
    }

    private void event(@Nonnull String eventName, @Nonnull List<?> args) {
        try {
            getEventRouter().publishEvent(eventName, args);
        } catch (InstanceNotFoundException infe) {
            // ignore
        }
    }
}
