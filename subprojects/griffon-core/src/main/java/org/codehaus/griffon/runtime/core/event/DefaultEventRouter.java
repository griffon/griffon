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
package org.codehaus.griffon.runtime.core.event;

import griffon.core.threading.UIThreadManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultEventRouter extends AbstractEventRouter {
    private final BlockingQueue<Runnable> deferredEvents = new LinkedBlockingQueue<>();
    protected static int count = 1;
    private UIThreadManager uiThreadManager;

    public DefaultEventRouter() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        deferredEvents.take().run();
                    } catch (InterruptedException e) {
                        // ignore ?
                    }
                }
            }
        }, getClass().getSimpleName() + "-" + identifier());
        t.setDaemon(true);
        t.start();
    }

    private static int identifier() {
        synchronized (LOCK) {
            return count++;
        }
    }

    @Inject
    public void setUIThreadManager(@Nonnull UIThreadManager uiThreadManager) {
        this.uiThreadManager = requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be bull");
    }

    protected void doPublishOutsideUI(@Nonnull Runnable publisher) {
        uiThreadManager.runOutsideUI(publisher);
    }

    protected void doPublishAsync(@Nonnull Runnable publisher) {
        deferredEvents.offer(publisher);
    }
}
