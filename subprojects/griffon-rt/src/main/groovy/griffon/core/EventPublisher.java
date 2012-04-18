/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.core;

import griffon.util.RunnableWithArgs;
import groovy.lang.Closure;

import java.util.List;

/**
 * Base contract for classes that can publish events using their own
 * event bus.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public interface EventPublisher {
    /**
     * Adds an event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an event listener
     */
    void addEventListener(Object listener);

    /**
     * Adds a closure as an event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener  an event listener
     */
    void addEventListener(String eventName, Closure listener);

    /**
     * Adds a runnable as an event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener  an event listener
     */
    void addEventListener(String eventName, RunnableWithArgs listener);

    /**
     * Removes an event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an event listener
     */
    void removeEventListener(Object listener);

    /**
     * Removes a closure as an event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener  an event listener
     */
    void removeEventListener(String eventName, Closure listener);

    /**
     * Removes a runnable as an event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener  an event listener
     */
    void removeEventListener(String eventName, RunnableWithArgs listener);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in the same thread as the publisher.
     *
     * @param eventName the name of the event
     */
    void publishEvent(String eventName);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in the same thread as the publisher.
     *
     * @param eventName the name of the event
     * @param args      event arguments sent to listeners
     */
    void publishEvent(String eventName, List args);

    /**
     * Publishes an event.<p>
     * Listeners will be notified outside of the UI thread.
     *
     * @param eventName the name of the event
     */
    void publishEventOutsideUI(String eventName);

    /**
     * Publishes an event.<p>
     * Listeners will be notified outside of the UI thread.
     *
     * @param eventName the name of the event
     * @param args      event arguments sent to listeners
     */
    void publishEventOutsideUI(String eventName, List args);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in a different thread.
     *
     * @param eventName the name of the event
     */
    void publishEventAsync(String eventName);

    /**
     * Publishes an event.<p>
     * Listeners will be notified in a different thread.
     *
     * @param eventName the name of the event
     * @param args      event arguments sent to listeners
     */
    void publishEventAsync(String eventName, List args);

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
}
