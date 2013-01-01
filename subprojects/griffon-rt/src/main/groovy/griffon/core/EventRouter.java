/*
 * Copyright 2009-2013 the original author or authors.
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
import java.util.Map;

/**
 * An event handling helper.<p>
 * Listeners may be of type<ul>
 * <li>a <tt>Script</tt></li>
 * <li>a <tt>Map</tt></li>
 * <li>a <tt>Closure</tt></li>
 * <li>a <tt>RunnableWithArgs</tt></li>
 * <li>a <tt>Object</tt> (a Java bean)</li>
 * </ul>
 * <p/>
 * With the exception of Maps and Closures, the naming convention for an eventHandler is
 * "on" + eventName, Maps and Closures require handlers to be named as eventName only.<p>
 * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
 * Event names must follow the camelCase naming convention.<p>
 *
 * @author Andres Almiray
 */
public interface EventRouter {
    /**
     * Returns the current enabled state.
     *
     * @return true if the router is enabled; false otherwise.
     */
    boolean isEnabled();

    /**
     * Sets the enabled state of this router.</p>
     * A disabled router will simply discard all events that are sent to it, in other
     * words listeners will never be notified. Discarded events cannot be recovered, even
     * if the router is enabled at a later point in time.
     *
     * @param enabled the value for the enabled state
     */
    void setEnabled(boolean enabled);

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners will be notified in the same thread
     * that originated the event.
     *
     * @param eventName the name of the event
     */
    void publish(String eventName);

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners will be notified in the same thread
     * that originated the event.
     *
     * @param eventName the name of the event
     * @param params    the event's arguments
     */
    void publish(String eventName, List params);

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * outside of the UI thread always.
     *
     * @param eventName the name of the event
     */
    void publishOutsideUI(String eventName);

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * outside of the UI thread always.
     *
     * @param eventName the name of the event
     * @param params    the event's arguments
     */
    void publishOutsideUI(String eventName, List params);

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * in a different thread than the publisher's, always.
     *
     * @param eventName the name of the event
     */
    void publishAsync(String eventName);

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * in a different thread than the publisher's, always.
     *
     * @param eventName the name of the event
     * @param params    the event's arguments
     */
    void publishAsync(String eventName, List params);

    /**
     * Adds an event listener.<p>
     * <p/>
     * A listener may be a<ul>
     * <li>a <tt>Script</tt></li>
     * <li>a <tt>Map</tt></li>
     * <li>a <tt>Object</tt> (a Java bean)</li>
     * </ul>
     * <p/>
     * With the exception of Maps, the naming convention for an eventHandler is
     * "on" + eventName, Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void addEventListener(Object listener);

    /**
     * Adds a Map containing event listeners.<p>
     * <p/>
     * An event listener may be a<ul>
     * <li>a <tt>Closure</tt></li>
     * <li>a <tt>RunnableWithArgs</tt></li>
     * </ul>
     * <p/>
     * Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: StartupStart, MyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void addEventListener(Map<String, Object> listener);

    /**
     * Removes an event listener.<p>
     * <p/>
     * A listener may be a<ul>
     * <li>a <tt>Script</tt></li>
     * <li>a <tt>Map</tt></li>
     * <li>a <tt>Object</tt> (a Java bean)</li>
     * </ul>
     * <p/>
     * With the exception of Maps, the naming convention for an eventHandler is
     * "on" + eventName, Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void removeEventListener(Object listener);

    /**
     * Removes a Map containing event listeners.<p>
     * <p/>
     * An event listener may be a<ul>
     * <li>a <tt>Closure</tt></li>
     * <li>a <tt>RunnableWithArgs</tt></li>
     * </ul>
     * <p/>
     * Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: StartupStart, MyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void removeEventListener(Map<String, Object> listener);

    /**
     * Adds a Closure as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    void addEventListener(String eventName, Closure listener);

    /**
     * Adds a Runnable as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    void addEventListener(String eventName, RunnableWithArgs listener);

    /**
     * Removes a Closure as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    void removeEventListener(String eventName, Closure listener);

    /**
     * Removes a Runnable as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    void removeEventListener(String eventName, RunnableWithArgs listener);
}
