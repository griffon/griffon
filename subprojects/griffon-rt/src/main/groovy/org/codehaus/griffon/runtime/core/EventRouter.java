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
package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonArtifact;
import griffon.core.UIThreadManager;
import griffon.util.RunnableWithArgs;
import groovy.lang.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static griffon.util.GriffonNameUtils.capitalize;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.synchronizedList;
import static org.codehaus.groovy.runtime.MetaClassHelper.convertToTypeArray;

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
public class EventRouter {
    private boolean enabled = true;
    private final List listeners = synchronizedList(new ArrayList());
    private final Map<Script, Binding> scriptBindings = new LinkedHashMap<Script, Binding>();
    private final Map<String, List> closureListeners = Collections.synchronizedMap(new LinkedHashMap<String, List>());
    private final BlockingQueue<Runnable> deferredEvents = new LinkedBlockingQueue<Runnable>();

    private static final Logger LOG = LoggerFactory.getLogger(EventRouter.class);
    private static final Object LOCK = new Object();
    private static int count = 1;

    private static int identifier() {
        synchronized (LOCK) {
            return count++;
        }
    }

    public EventRouter() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        deferredEvents.take().run();
                    } catch (InterruptedException e) {
                        // ignore ?
                    }
                }
            }
        }, "EventRouter-" + identifier()).start();
    }

    /**
     * Returns the current enabled state.
     *
     * @return true if the router is enabled; false otherwise.
     */
    public boolean isEnabled() {
        synchronized (LOCK) {
            return this.enabled;
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
    public void setEnabled(boolean enabled) {
        synchronized (LOCK) {
            this.enabled = enabled;
        }
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners will be notified in the same thread
     * that originated the event.
     *
     * @param eventName the name of the event
     */
    public void publish(String eventName) {
        publish(eventName, EMPTY_LIST);
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners will be notified in the same thread
     * that originated the event.
     *
     * @param eventName the name of the event
     * @param params    the event's arguments
     */
    public void publish(String eventName, List params) {
        if (!isEnabled()) return;
        if (isBlank(eventName)) return;
        if (params == null) params = EMPTY_LIST;
        buildPublisher(eventName, params, "synchronously").run();
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * outside of the UI thread always.
     *
     * @param eventName the name of the event
     */
    public void publishOutsideUI(String eventName) {
        publishOutsideUI(eventName, EMPTY_LIST);
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * outside of the UI thread always.
     *
     * @param eventName the name of the event
     * @param params    the event's arguments
     */
    public void publishOutsideUI(String eventName, List params) {
        if (!isEnabled()) return;
        if (isBlank(eventName)) return;
        if (params == null) params = EMPTY_LIST;
        UIThreadManager.getInstance().executeOutside(buildPublisher(eventName, params, "outside UI"));
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * in a different thread than the publisher's, always.
     *
     * @param eventName the name of the event
     */
    public void publishAsync(String eventName) {
        publishAsync(eventName, EMPTY_LIST);
    }

    /**
     * Publishes an event with optional arguments.</p>
     * Event listeners are guaranteed to be notified
     * in a different thread than the publisher's, always.
     *
     * @param eventName the name of the event
     * @param params    the event's arguments
     */
    public void publishAsync(String eventName, List params) {
        if (!isEnabled()) return;
        if (isBlank(eventName)) return;
        if (params == null) params = EMPTY_LIST;
        deferredEvents.offer(buildPublisher(eventName, params, "asynchronously"));
    }

    private Runnable buildPublisher(final String event, final List params, final String mode) {
        return new Runnable() {
            public void run() {
                String eventName = capitalize(event);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Triggering event '" + eventName + "' " + mode);
                }
                String eventHandler = "on" + eventName;
                // defensive copying to avoid CME during event dispatching
                // GRIFFON-224
                List listenersCopy = new ArrayList();
                synchronized (listeners) {
                    listenersCopy.addAll(listeners);
                }
                synchronized (closureListeners) {
                    List list = closureListeners.get(eventName);
                    if (list != null) {
                        for (Object listener : list) {
                            listenersCopy.add(listener);
                        }
                    }
                }

                for (Object listener : listenersCopy) {
                    if (listener instanceof Script) {
                        fireEvent((Script) listener, eventHandler, params);
                    } else if (listener instanceof Closure) {
                        fireEvent((Closure) listener, eventHandler, params);
                    } else if (listener instanceof RunnableWithArgs) {
                        fireEvent((RunnableWithArgs) listener, eventHandler, params);
                    } else {
                        fireEvent(listener, eventHandler, params);
                    }
                }
            }
        };
    }

    private Object[] asArray(List list) {
        return list.toArray(new Object[list.size()]);
    }

    private void invokeHandler(Object handler, List params) {
        if (handler instanceof Closure) {
            ((Closure) handler).call(asArray(params));
        } else if (handler instanceof RunnableWithArgs) {
            ((RunnableWithArgs) handler).run(asArray(params));
        }
    }

    private void fireEvent(Script script, String eventHandler, List params) {
        Binding binding = scriptBindings.get(script);
        if (binding == null) {
            binding = new Binding();
            script.setBinding(binding);
            script.run();
            scriptBindings.put(script, binding);
        }

        Object handler = binding.getVariables().get(eventHandler);
        if (handler != null) {
            invokeHandler(handler, params);
        }
    }

    private void fireEvent(Closure closure, String eventHandler, List params) {
        closure.call(asArray(params));
    }

    private void fireEvent(RunnableWithArgs runnable, String eventHandler, List params) {
        runnable.run(asArray(params));
    }

    private void fireEvent(Object instance, String eventHandler, List params) {
        MetaClass mc = metaClassOf(instance);
        MetaProperty mp = mc.getMetaProperty(eventHandler);
        if (mp != null && mp.getProperty(instance) != null) {
            invokeHandler(mp.getProperty(instance), params);
            return;
        }

        Class[] argTypes = convertToTypeArray(asArray(params));
        MetaMethod mm = mc.pickMethod(eventHandler, argTypes);
        if (mm != null) {
            mm.invoke(instance, asArray(params));
        }
    }

    private MetaClass metaClassOf(Object obj) {
        if (obj instanceof GriffonArtifact) {
            return ((GriffonArtifact) obj).getGriffonClass().getMetaClass();
        } else if (obj instanceof GroovyObject) {
            return ((GroovyObject) obj).getMetaClass();
        }
        return GroovySystem.getMetaClassRegistry().getMetaClass(obj.getClass());
    }

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
    public void addEventListener(Object listener) {
        if (listener == null || listener instanceof Closure || listener instanceof RunnableWithArgs) return;
        if (listener instanceof Map) {
            addEventListener((Map) listener);
            return;
        }
        synchronized (listeners) {
            if (listeners.contains(listener)) return;
            try {
                LOG.debug("Adding listener " + listener);
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Adding listener " + listener.getClass().getName());
            }
            listeners.add(listener);
        }
    }

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
    public void addEventListener(Map<String, Object> listener) {
        if (listener == null) return;
        for (Map.Entry<String, Object> entry : listener.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Closure) {
                addEventListener(entry.getKey(), (Closure) value);
            } else if (value instanceof RunnableWithArgs) {
                addEventListener(entry.getKey(), (RunnableWithArgs) value);
            }
        }
    }

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
    public void removeEventListener(Object listener) {
        if (listener == null || listener instanceof Closure || listener instanceof RunnableWithArgs) return;
        if (listener instanceof Map) {
            removeEventListener((Map) listener);
            return;
        }
        synchronized (listeners) {
            if (LOG.isDebugEnabled()) {
                try {
                    LOG.debug("Removing listener " + listener);
                } catch (UnsupportedOperationException uoe) {
                    LOG.debug("Removing listener " + listener.getClass().getName());
                }
            }
            listeners.remove(listener);
            removeNestedListeners(listener);
        }
    }

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
    public void removeEventListener(Map<String, Object> listener) {
        if (listener == null) return;
        for (Map.Entry<String, Object> entry : listener.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Closure) {
                removeEventListener(entry.getKey(), (Closure) value);
            } else if (value instanceof RunnableWithArgs) {
                removeEventListener(entry.getKey(), (RunnableWithArgs) value);
            }
        }
    }

    /**
     * Adds a Closure as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    public void addEventListener(String eventName, Closure listener) {
        if (isBlank(eventName) || listener == null) return;
        synchronized (closureListeners) {
            List list = closureListeners.get(capitalize(eventName));
            if (list == null) {
                list = new ArrayList();
                closureListeners.put(capitalize(eventName), list);
            }
            if (list.contains(listener)) return;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding listener " + listener.getClass().getName() + " on " + capitalize(eventName));
            }
            list.add(listener);
        }
    }

    /**
     * Adds a Runnable as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    public void addEventListener(String eventName, RunnableWithArgs listener) {
        if (isBlank(eventName) || listener == null) return;
        synchronized (closureListeners) {
            List list = closureListeners.get(capitalize(eventName));
            if (list == null) {
                list = new ArrayList();
                closureListeners.put(capitalize(eventName), list);
            }
            if (list.contains(listener)) return;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding listener " + listener.getClass().getName() + " on " + capitalize(eventName));
            }
            list.add(listener);
        }
    }

    /**
     * Removes a Closure as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    public void removeEventListener(String eventName, Closure listener) {
        if (isBlank(eventName) || listener == null) return;
        synchronized (closureListeners) {
            List list = closureListeners.get(capitalize(eventName));
            if (list != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Removing listener " + listener.getClass().getName() + " on " + capitalize(eventName));
                }
                list.remove(listener);
            }
        }
    }

    /**
     * Removes a Runnable as an event listener.<p>
     * Event names must follow the camelCase naming convention.
     *
     * @param eventName the name of the event
     * @param listener  the event listener
     */
    public void removeEventListener(String eventName, RunnableWithArgs listener) {
        if (isBlank(eventName) || listener == null) return;
        synchronized (closureListeners) {
            List list = closureListeners.get(capitalize(eventName));
            if (list != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Removing listener " + listener.getClass().getName() + " on " + capitalize(eventName));
                }
                list.remove(listener);
            }
        }
    }

    private void removeNestedListeners(Object subject) {
        synchronized (closureListeners) {
            for (Map.Entry<String, List> event : closureListeners.entrySet()) {
                String eventName = event.getKey();
                List listenerList = event.getValue();
                List toRemove = new ArrayList();
                for (Object listener : listenerList) {
                    if (isNestedListener(listener, subject)) {
                        toRemove.add(listener);
                    }
                }
                for (Object listener : toRemove) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Removing listener " + listener.getClass().getName() + " on " + capitalize(eventName));
                    }
                    listenerList.remove(listener);
                }
            }
        }
    }

    private boolean isNestedListener(Object listener, Object subject) {
        if (listener instanceof Closure) {
            return ((Closure) listener).getOwner().equals(subject);
        } else if (listener instanceof RunnableWithArgs) {
            Class listenerClass = listener.getClass();
            if (listenerClass.isMemberClass() && listenerClass.getEnclosingClass().equals(subject.getClass())) {
                return subject.equals(InvokerHelper.getProperty(listener, "this$0"));
            }
        }
        return false;
    }
}
