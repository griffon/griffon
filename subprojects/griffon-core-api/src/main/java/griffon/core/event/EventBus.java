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
package griffon.core.event;

import griffon.annotations.core.Nonnull;

/**
 * Base contract for classes that can publish events.
 * <p>
 * Events can be published int two ways: synchronous (publishEvent) and asynchronous (publishEventAsync). In synchronous
 * publishing, the event emitter will wait for all matching event handlers to be called and finish handling the event. In
 * asynchronous publishing the event emitter continues its work immediately after firing the event and does not wait for
 * matching event handlers.
 * <p>
 * An event handler is a class that has at least one method annotated with {@code EventHandler} and a
 * single argument that defines the type of event hat can be handled, for example
 * <pre>
 * public class CustomEventHandler {
 *     &#064;EventHandler
 *     private void handleEvent(CustomEvent event) {
 *         ...
 *     }
 * }
 * </pre>
 * <p>
 * Event handlers may define more than one method. The method name is not important and its visibility modifier may be any
 * of the supported modifiers by the Java platform.
 *
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface EventBus {
    /**
     * Adds an event handler.<p>
     * An event handler is a class that has at least one method annotated with {@code EventHandler} and a
     * single argument that defines the type of event hat can be handled.
     *
     * @param handler an event handler. Must not be {@code null}.
     */
    void subscribe(@Nonnull Object handler);

    /**
     * Removes an event handler.<p>
     *
     * @param handler an event handler. Must not be {@code null}.
     */
    void unsubscribe(@Nonnull Object handler);

    /**
     * Publishes an event.<p>
     * Handlers will be notified in the same thread as the publisher.
     *
     * @param event the event to be published. Must not be {@code null}.
     */
    <E> void publishEvent(@Nonnull E event);

    /**
     * Publishes an event.<p>
     * Handlers will be notified in a background thread.
     *
     * @param event the event to be published. Must not be {@code null}.
     */
    <E> void publishEventAsync(@Nonnull E event);
}
