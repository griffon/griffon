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

package org.codehaus.griffon.runtime.core;

import griffon.core.Event;
import griffon.core.EventRouter;
import griffon.core.GriffonArtifact;
import griffon.util.RunnableWithArgs;
import groovy.lang.*;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static griffon.util.GriffonNameUtils.capitalize;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.synchronizedList;
import static org.codehaus.groovy.runtime.MetaClassHelper.convertToTypeArray;

/**
 * @author Andres Almiray
 */
public abstract class AbstractEventRouter implements EventRouter {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEventRouter.class);
    protected static final Object[] LOCK = new Object[0];
    private boolean enabled = true;
    protected final List listeners = synchronizedList(new ArrayList());
    private final Map<Script, Binding> scriptBindings = new LinkedHashMap<Script, Binding>();
    protected final Map<String, List> closureListeners = Collections.synchronizedMap(new LinkedHashMap<String, List>());

    @Override
    public boolean isEnabled() {
        synchronized (LOCK) {
            return this.enabled;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        synchronized (LOCK) {
            this.enabled = enabled;
        }
    }

    @Override
    public void publish(String eventName) {
        publish(eventName, EMPTY_LIST);
    }

    @Override
    public void publish(String eventName, List params) {
        if (!isEnabled()) return;
        if (isBlank(eventName)) return;
        if (params == null) params = EMPTY_LIST;
        buildPublisher(eventName, params, "synchronously").run();
    }

    @Override
    public void publishOutsideUI(String eventName) {
        publishOutsideUI(eventName, EMPTY_LIST);
    }

    @Override
    public void publishOutsideUI(String eventName, List params) {
        if (!isEnabled()) return;
        if (isBlank(eventName)) return;
        if (params == null) params = EMPTY_LIST;
        final Runnable publisher = buildPublisher(eventName, params, "outside UI");
        doPublishOutsideUI(publisher);
    }

    protected abstract void doPublishOutsideUI(Runnable publisher);

    @Override
    public void publishAsync(String eventName) {
        publishAsync(eventName, EMPTY_LIST);
    }

    @Override
    public void publishAsync(String eventName, List params) {
        if (!isEnabled()) return;
        if (isBlank(eventName)) return;
        if (params == null) params = EMPTY_LIST;
        final Runnable publisher = buildPublisher(eventName, params, "asynchronously");
        doPublishAsync(publisher);
    }

    protected abstract void doPublishAsync(Runnable publisher);

    @Override
    public void publish(Event event) {
        publish(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void publishOutsideUI(Event event) {
        publishOutsideUI(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void publishAsync(Event event) {
        publishAsync(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void addEventListener(Class<? extends Event> eventClass, Closure listener) {
        addEventListener(eventClass.getSimpleName(), listener);
    }

    @Override
    public void addEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
        addEventListener(eventClass.getSimpleName(), listener);
    }

    @Override
    public void removeEventListener(Class<? extends Event> eventClass, Closure listener) {
        removeEventListener(eventClass.getSimpleName(), listener);
    }

    @Override
    public void removeEventListener(Class<? extends Event> eventClass, RunnableWithArgs listener) {
        removeEventListener(eventClass.getSimpleName(), listener);
    }

    private void invokeHandler(Object handler, List params) {
        if (handler instanceof Closure) {
            ((Closure) handler).call(asArray(params));
        } else if (handler instanceof RunnableWithArgs) {
            ((RunnableWithArgs) handler).run(asArray(params));
        }
    }

    protected void fireEvent(Script script, String eventHandler, List params) {
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

    protected void fireEvent(Closure closure, String eventHandler, List params) {
        closure.call(asArray(params));
    }

    protected void fireEvent(RunnableWithArgs runnable, String eventHandler, List params) {
        runnable.run(asArray(params));
    }

    protected void fireEvent(Object instance, String eventHandler, List params) {
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    protected Runnable buildPublisher(final String event, final List params, final String mode) {
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

    protected void removeNestedListeners(Object subject) {
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

    protected boolean isNestedListener(Object listener, Object subject) {
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

    protected Object[] asArray(List list) {
        return list.toArray(new Object[list.size()]);
    }

    protected MetaClass metaClassOf(Object obj) {
        if (obj instanceof GriffonArtifact) {
            return ((GriffonArtifact) obj).getGriffonClass().getMetaClass();
        } else if (obj instanceof GroovyObject) {
            return ((GroovyObject) obj).getMetaClass();
        }
        return GroovySystem.getMetaClassRegistry().getMetaClass(obj.getClass());
    }
}
