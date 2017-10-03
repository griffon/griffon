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
package org.codehaus.griffon.runtime.core.event;

import griffon.core.RunnableWithArgs;
import griffon.core.event.Event;
import griffon.core.event.EventPublisher;
import griffon.core.event.EventRouter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractEventPublisher implements EventPublisher {
    private static final String ERROR_EVENT_NAME_BLANK = "Argument 'eventName' must not be blank";
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' must not be null";
    private static final String ERROR_EVENT_CLASS_NULL = "Argument 'eventClass' must not be null";
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
    public <E extends Event> void removeEventListener(@Nonnull Class<E> eventClass, @Nonnull RunnableWithArgs listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.removeEventListener(eventClass, listener);
    }

    @Override
    public void addEventListener(@Nonnull Object listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.addEventListener(listener);
    }

    @Override
    public void addEventListener(@Nonnull String eventName, @Nonnull RunnableWithArgs listener) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.addEventListener(eventName, listener);
    }

    @Override
    public void publishEvent(@Nonnull String eventName) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        eventRouter.publishEvent(eventName);
    }

    @Override
    public void publishEventOutsideUI(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        eventRouter.publishEventOutsideUI(event);
    }

    @Override
    public void removeEventListener(@Nonnull Map<String, Object> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.removeEventListener(listener);
    }

    @Override
    public <E extends Event> void addEventListener(@Nonnull Class<E> eventClass, @Nonnull RunnableWithArgs listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.addEventListener(eventClass, listener);
    }

    @Override
    public void publishEventOutsideUI(@Nonnull String eventName) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        eventRouter.publishEventOutsideUI(eventName);
    }

    @Override
    public void publishEventOutsideUI(@Nonnull String eventName, @Nullable List<?> params) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        eventRouter.publishEventOutsideUI(eventName, params);
    }

    @Override
    public void publishEvent(@Nonnull String eventName, @Nullable List<?> params) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        eventRouter.publishEvent(eventName, params);
    }

    @Override
    public void publishEventAsync(@Nonnull String eventName, @Nullable List<?> params) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        eventRouter.publishEventAsync(eventName, params);
    }

    @Override
    public void removeEventListener(@Nonnull String eventName, @Nonnull RunnableWithArgs listener) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.removeEventListener(eventName, listener);
    }

    @Override
    public void removeEventListener(@Nonnull Object listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.removeEventListener(listener);
    }

    @Override
    public void addEventListener(@Nonnull Map<String, Object> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        eventRouter.addEventListener(listener);
    }

    @Override
    public void publishEvent(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        eventRouter.publishEvent(event);
    }

    @Override
    public void publishEventAsync(@Nonnull String eventName) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        eventRouter.publishEventAsync(eventName);
    }

    @Override
    public void publishEventAsync(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        eventRouter.publishEventAsync(event);
    }

    @Nonnull
    @Override
    public Collection<Object> getEventListeners() {
        return eventRouter.getEventListeners();
    }

    @Nonnull
    @Override
    public Collection<Object> getEventListeners(@Nonnull String eventName) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        return eventRouter.getEventListeners(eventName);
    }
}
