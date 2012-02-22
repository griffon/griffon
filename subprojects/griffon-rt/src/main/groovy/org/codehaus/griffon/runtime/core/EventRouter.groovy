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
package org.codehaus.griffon.runtime.core

import griffon.core.UIThreadManager
import griffon.util.RunnableWithArgs
import java.util.concurrent.LinkedBlockingQueue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static griffon.util.GriffonNameUtils.capitalize
import static org.codehaus.groovy.runtime.MetaClassHelper.convertToTypeArray

/**
 * An event handling helper.<p>
 * Listeners may be of type<ul>
 * <li>a <tt>Script</tt></li>
 * <li>a <tt>Map</tt></li>
 * <li>a <tt>Closure</tt></li>
 * <li>a <tt>RunnableWithArgs</tt></li>
 * <li>a <tt>Object</tt> (a Java bean)</li>
 * </ul>
 *
 * With the exception of Maps and Closures, the naming convention for an eventHandler is
 * "on" + eventName, Maps and Closures require handlers to be named as eventName only.<p>
 * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
 * Event names must follow the camelCase naming convention.<p>
 *
 * @author Andres Almiray
 */
class EventRouter {
    private boolean enabled = true
    private List listeners = Collections.synchronizedList([])
    private Map<Script, Binding> scriptBindings = [:]
    private Map<String, List> closureListeners = Collections.synchronizedMap([:])
    private static final Logger LOG = LoggerFactory.getLogger(EventRouter)
    private final Queue<Closure> deferredEvents = new LinkedBlockingQueue<Closure>()

    private static final Object LOCK = new Object()
    private static int count = 1

    private static int identifier() {
        synchronized (LOCK) {
            count++
        }
    }

    EventRouter() {
        new Thread({
            while (true) {
                deferredEvents.take()()
            }
        }, "EventRouter-${identifier()}").start()
    }

    /**
     * Returns the current enabled state.
     * @return true if the router is enabled; false otherwise.
     */
    boolean isEnabled() {
        synchronized (LOCK) {
            this.@enabled
        }
    }

    /**
     * Sets the enabled state of this router.</p>
     * A disabled router will simply discard all events that are sent to it, in other
     * words listeners will never be notified. Discarded events cannot be recovered, even
     * if the router is enabled at a later point in time.
     *
     * @param enabled the value for the enabled state
     */
    void setEnabled(boolean enabled) {
        synchronized (LOCK) {
            this.@enabled = enabled
        }
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners will be notified in the same thread
     * that originated the event.
     *
     * @param eventName the name of the event
     * @param params the event's arguments
     *
     */
    void publish(String eventName, List params = []) {
        if (!isEnabled()) return
        if (!eventName) return
        buildPublisher(eventName, params)('synchronously')
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * outside of the UI thread always.
     *
     * @param eventName the name of the event
     * @param params the event's arguments
     * @deprecated use #eventOutsideUI() instead
     */
    @Deprecated
    void publishOutside(String eventName, List params = []) {
        publishOutsideUI(eventName, params)
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * outside of the UI thread always.
     *
     * @param eventName the name of the event
     * @param params the event's arguments
     *
     */
    void publishOutsideUI(String eventName, List params = []) {
        if (!isEnabled()) return
        if (!eventName) return
        UIThreadManager.instance.executeOutside(buildPublisher(eventName, params).curry('outside UI'))
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * in a different thread than the publisher's, always.
     *
     * @param eventName the name of the event
     * @param params the event's arguments
     *
     */
    void publishAsync(String eventName, List params = []) {
        if (!isEnabled()) return
        if (!eventName) return
        deferredEvents.offer(buildPublisher(eventName, params).curry('asynchronously'))
    }

    private Closure buildPublisher(String eventName, List params) {
        return { mode ->
            if (LOG.traceEnabled) LOG.trace("Triggering event '$eventName' $mode")
            eventName = capitalize(eventName)
            def eventHandler = 'on' + eventName

            // defensive copying to avoid CME during event dispatching
            // GRIFFON-224
            List listenersCopy = []
            synchronized (listeners) {
                listenersCopy.addAll(listeners)
            }
            synchronized (closureListeners) {
                for (listener in closureListeners[eventName]) {
                    listenersCopy << listener
                }
            }

            for (listener in listenersCopy) {
                fireEvent(listener, eventHandler, params)
            }
        }
    }

    private void fireEvent(Script script, String eventHandler, List params) {
        def binding = scriptBindings[script]
        if (!binding) {
            binding = new Binding()
            script.binding = binding
            script.run()
            scriptBindings[script] = binding
        }

        for (variable in script.binding.variables) {
            def m = variable.key =~ /$eventHandler/
            if (m.matches()) {
                variable.value(* params)
                return
            }
        }
    }

    private void fireEvent(Map map, String eventHandler, List params) {
        eventHandler = eventHandler[2..-1]
        def handler = map[eventHandler]
        if (handler instanceof Closure) {
            handler(* params)
        } else if (handler instanceof RunnableWithArgs) {
            handler.run(params.toArray(new Object[params.size()]))
        }
    }

    private void fireEvent(Closure closure, String eventHandler, List params) {
        closure(* params)
    }

    private void fireEvent(RunnableWithArgs runnable, String eventHandler, List params) {
        runnable.run(params.toArray(new Object[params.size()]))
    }

    private void fireEvent(Object instance, String eventHandler, List params) {
        def mp = instance.metaClass.getMetaProperty(eventHandler)
        if (mp && mp.getProperty(instance)) {
            mp.getProperty(instance)(* params)
            return
        }

        Class[] argTypes = convertToTypeArray(params as Object[])
        def mm = instance.metaClass.pickMethod(eventHandler, argTypes)
        if (mm) {
            mm.invoke(instance, * params)
        }
    }

    /**
     * Adds an event listener.<p>
     *
     * A listener may be a<ul>
     * <li>a <tt>Script</tt></li>
     * <li>a <tt>Map</tt></li>
     * <li>a <tt>Object</tt> (a Java bean)</li>
     * </ul>
     *
     * With the exception of Maps, the naming convention for an eventHandler is
     * "on" + eventName, Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void addEventListener(listener) {
        if (!listener || listener instanceof Closure || listener instanceof RunnableWithArgs) return
        if (listener instanceof Map) {
            addEventListener((Map) listener)
            return
        }
        synchronized (listeners) {
            if (listeners.find { it == listener }) return
            try {
                LOG.debug("Adding listener $listener")
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Adding listener ${listener.class.name}")
            }
            listeners.add(listener)
        }
    }

    /**
     * Adds a Map containing event listeners.<p>
     *
     * An event listener may be a<ul>
     * <li>a <tt>Closure</tt></li>
     * <li>a <tt>RunnableWithArgs</tt></li>
     * </ul>
     *
     * Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: StartupStart, MyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void addEventListener(Map listener) {
        if (!listener) return
        for (entry in listener) {
            addEventListener(entry.key, entry.value)
        }
    }

    /**
     * Removes an event listener.<p>
     *
     * A listener may be a<ul>
     * <li>a <tt>Script</tt></li>
     * <li>a <tt>Map</tt></li>
     * <li>a <tt>Object</tt> (a Java bean)</li>
     * </ul>
     *
     * With the exception of Maps, the naming convention for an eventHandler is
     * "on" + eventName, Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: onStartupStart, onMyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void removeEventListener(listener) {
        if (!listener || listener instanceof Closure || listener instanceof RunnableWithArgs) return
        if (listener instanceof Map) {
            removeEventListener((Map) listener)
            return
        }
        synchronized (listeners) {
            if (LOG.debugEnabled) {
                try {
                    LOG.debug("Removing listener $listener")
                } catch (UnsupportedOperationException uoe) {
                    LOG.debug("Removing listener ${listener.class.name}")
                }
            }
            listeners.remove(listener)
            removeNestedListeners(listener)
        }
    }

    /**
     * Removes a Map containing event listeners.<p>
     *
     * An event listener may be a<ul>
     * <li>a <tt>Closure</tt></li>
     * <li>a <tt>RunnableWithArgs</tt></li>
     * </ul>
     *
     * Maps require handlers to be named as eventName only.<p>
     * Some examples of eventHandler names are: StartupStart, MyCoolEvent.
     * Event names must follow the camelCase naming convention.<p>
     *
     * @param listener an event listener of type Script, Map or Object
     */
    void removeEventListener(Map listener) {
        if (!listener) return
        for (entry in listener) {
            removeEventListener(entry.key, entry.value)
        }
    }

    /**
     * Adds a Closure as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener the event listener
     */
    void addEventListener(String eventName, Closure listener) {
        if (!eventName || !listener) return
        synchronized (closureListeners) {
            def list = closureListeners.get(capitalize(eventName), [])
            if (list.find { it == listener }) return
            if (LOG.debugEnabled) {
                LOG.debug("Adding listener ${listener.class.name} on $eventName")
            }
            list.add(listener)
        }
    }

    /**
     * Adds a Runnable as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener the event listener
     */
    void addEventListener(String eventName, RunnableWithArgs listener) {
        if (!eventName || !listener) return
        synchronized (closureListeners) {
            def list = closureListeners.get(capitalize(eventName), [])
            if (list.find { it == listener }) return
            if (LOG.debugEnabled) {
                LOG.debug("Adding listener ${listener.class.name} on $eventName")
            }
            list.add(listener)
        }
    }

    /**
     * Removes a Closure as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener the event listener
     */
    void removeEventListener(String eventName, Closure listener) {
        if (!eventName || !listener) return
        synchronized (closureListeners) {
            def list = closureListeners[capitalize(eventName)]
            if (list) {
                if (LOG.debugEnabled) {
                    LOG.debug("Removing listener ${listener.class.name} on $eventName")
                }
                list.remove(listener)
            }
        }
    }

    /**
     * Removes a Runnable as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener the event listener
     */
    void removeEventListener(String eventName, RunnableWithArgs listener) {
        if (!eventName || !listener) return
        synchronized (closureListeners) {
            def list = closureListeners[capitalize(eventName)]
            if (list) {
                if (LOG.debugEnabled) {
                    LOG.debug("Removing listener ${listener.class.name} on $eventName")
                }
                list.remove(listener)
            }
        }
    }

    private void removeNestedListeners(Object subject) {
        synchronized (closureListeners) {
            for (event in closureListeners.entrySet()) {
                String eventName = event.key
                List listenerList = event.value
                List toRemove = []
                for (listener in listenerList) {
                    if (isNestedListener(listener, subject)) {
                        toRemove << listener
                    }
                }
                for (listener in toRemove) {
                    if (LOG.debugEnabled) {
                        LOG.debug("Removing listener ${listener.class.name} on $eventName")
                    }
                    listenerList.remove(listener)
                }
            }
        }
    }

    private boolean isNestedListener(listener, subject) {
        if (listener instanceof Closure) {
            return listener.owner == subject
        } else if (listener instanceof RunnableWithArgs) {
            Class listenerClass = listener.class
            if (listenerClass.memberClass && listenerClass.enclosingClass == subject.class) {
                return listener['this$0'] == subject
            }
        }
        false
    }
}
