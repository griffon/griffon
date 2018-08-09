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
package griffon.core.event;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.RunnableWithArgs;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Base contract for classes that can publish events using their own
 * event bus.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface EventPublisher {
    /**
     * Adds an event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an event listener
     */
    void addEventListener(@Nonnull Object listener);

    /**
     * Adds a callable as an event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener  an event listener
     *
     * @since 2.3.0
     */
    void addEventListener(@Nonnull String eventName, @Nonnull RunnableWithArgs listener);

    /**
     * Adds a Map containing event listeners.<p>
     * <p>
     * An event listener may be<ul>
     * <li>a <tt>RunnableWithArgs</tt></li>
     * </ul>
     * <p>
     * Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: StartupStart, MyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Map
     */
    void addEventListener(@Nonnull Map<String, Object> listener);

    /**
     * Adds a callable as an event listener.<p>
     *
     * @param eventClass the type of the event
     * @param listener   an event listener
     *
     * @since 2.3.0
     */
    <E extends Event> void addEventListener(@Nonnull Class<E> eventClass, @Nonnull RunnableWithArgs listener);

    /**
     * Removes an event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an event listener
     */
    void removeEventListener(@Nonnull Object listener);

    /**
     * Removes a callable as an event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener  an event listener
     *
     * @since 2.3.0
     */
    void removeEventListener(@Nonnull String eventName, @Nonnull RunnableWithArgs listener);

    /**
     * Removes a Map containing event listeners.<p>
     * <p>
     * An event listener may be<ul>
     * <li>a <tt>RunnableWithArgs</tt></li>
     * </ul>
     * <p>
     * Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: StartupStart, MyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Map
     */
    void removeEventListener(@Nonnull Map<String, Object> listener);

    /**
     * Removes a callable as an event listener.<p>
     *
     * @param eventClass the type of the event
     * @param listener   an event listener
     *
     * @since 2.3.0
     */
    <E extends Event> void removeEventListener(@Nonnull Class<E> eventClass, @Nonnull RunnableWithArgs listener);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in the same thread as the publisher.
     *
     * @param eventName the name of the event
     */
    void publishEvent(@Nonnull String eventName);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in the same thread as the publisher.
     *
     * @param eventName the name of the event
     * @param args      event arguments sent to listeners
     */
    void publishEvent(@Nonnull String eventName, @Nullable List<?> args);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in the same thread as the publisher.
     *
     * @param event the event to be published
     */
    void publishEvent(@Nonnull Event event);

    /**
     * Publishes an event.<p>
     * Listeners will be notified outside of the UI thread.
     *
     * @param eventName the name of the event
     */
    void publishEventOutsideUI(@Nonnull String eventName);

    /**
     * Publishes an event.<p>
     * Listeners will be notified outside of the UI thread.
     *
     * @param eventName the name of the event
     * @param args      event arguments sent to listeners
     */
    void publishEventOutsideUI(@Nonnull String eventName, @Nullable List<?> args);

    /**
     * Publishes an event.<p>
     * Listeners will be notified outside of the UI thread.
     *
     * @param event the event to be published
     */
    void publishEventOutsideUI(@Nonnull Event event);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in a different thread.
     *
     * @param eventName the name of the event
     */
    void publishEventAsync(@Nonnull String eventName);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in a different thread.
     *
     * @param eventName the name of the event
     * @param args      event arguments sent to listeners
     */
    void publishEventAsync(@Nonnull String eventName, @Nullable List<?> args);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in a different thread.
     *
     * @param event the event to be published
     */
    void publishEventAsync(@Nonnull Event event);

    /**
     * Returns whether events will be published by the event bus or not.
     *
     * @return true if event publishing is enabled; false otherwise.
     */
    boolean isEventPublishingEnabled();

    /**
     * Sets the enabled state for event publishing.</p>
     * Events will be automatically discarded when the enabled state is set to false.
     *
     * @param enabled the value fot the enabled state.
     */
    void setEventPublishingEnabled(boolean enabled);

    /**
     * Returns an immutable snapshot view of all event listeners registered.
     *
     * @return an immutable collection of all registered listeners.
     *
     * @since 2.6.0
     */
    @Nonnull
    Collection<Object> getEventListeners();

    /**
     * Returns an immutable snapshot view of all event listeners registered for the target event name.
     *
     * @param eventName the name of the event
     *
     * @return an immutable collection of all registered listeners for the target event name.
     *
     * @since 2.6.0
     */
    @Nonnull
    Collection<Object> getEventListeners(@Nonnull String eventName);
}
