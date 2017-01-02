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
    private static final String ERROR_PUBLISHER_NULL = "Argument 'publisher' must not be null";
    private UIThreadManager uiThreadManager;

    @Inject
    public void setUIThreadManager(@Nonnull UIThreadManager uiThreadManager) {
        this.uiThreadManager = requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be null");
    }

    protected void doPublishOutsideUI(@Nonnull Runnable publisher) {
        requireNonNull(publisher, ERROR_PUBLISHER_NULL);
        if (!uiThreadManager.isUIThread()) {
            publisher.run();
        } else {
            runInsideExecutorService(publisher);
        }
    }

    protected void doPublishAsync(@Nonnull Runnable publisher) {
        executorService.submit(publisher);
    }
}
