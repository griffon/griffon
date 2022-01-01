/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.core.event.EventPublisher;
import griffon.core.event.EventRouter;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractEventPublisher implements EventPublisher {
    private static final String ERROR_HANDLER_NULL = "Argument 'hanlder' must not be null";
    private static final String ERROR_EVENT_NULL = "Argument 'event' must not be null";

    private EventRouter eventRouter;

    @Inject
    public void setEventRouter(@Nonnull EventRouter eventRouter) {
        this.eventRouter = requireNonNull(eventRouter, "Argument 'eventRouter' must not be null");
    }

    @Override
    public boolean isEventPublishingEnabled() {
        return eventRouter.isEventPublishingEnabled();
    }

    @Override
    public void setEventPublishingEnabled(boolean enabled) {
        eventRouter.setEventPublishingEnabled(enabled);
    }

    @Override
    public void subscribe(Object handler) {
        requireNonNull(handler, ERROR_HANDLER_NULL);
        eventRouter.subscribe(handler);
    }

    @Override
    public void unsubscribe(Object handler) {
        requireNonNull(handler, ERROR_HANDLER_NULL);
        eventRouter.unsubscribe(handler);
    }

    @Override
    public <E> void publishEvent(E event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        eventRouter.publishEvent(event);
    }

    @Override
    public <E> void publishEventAsync(E event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        eventRouter.publishEventAsync(event);
    }

    @Override
    public <E> void publishEventOutsideUI(E event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        eventRouter.publishEventOutsideUI(event);
    }
}
