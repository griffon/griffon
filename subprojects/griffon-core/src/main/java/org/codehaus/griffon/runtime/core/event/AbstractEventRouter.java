/*
 * Copyright 2008-2016 the original author or authors.
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

import griffon.core.CallableWithArgs;
import griffon.core.RunnableWithArgs;
import griffon.core.event.Event;
import griffon.core.event.EventRouter;
import griffon.util.GriffonClassUtils;
import griffon.util.MethodDescriptor;
import griffon.util.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonClassUtils.convertToTypeArray;
import static griffon.util.GriffonNameUtils.capitalize;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractEventRouter implements EventRouter {
    protected static final Object[] LOCK = new Object[0];
    private static final String ERROR_EVENT_NAME_BLANK = "Argument 'eventName' must not be blank";
    private static final String ERROR_EVENT_HANDLER_BLANK = "Argument 'eventHandler' must not be blank";
    private static final String ERROR_MODE_BLANK = "Argument 'mode' must not be blank";
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' must not be null";
    private static final String ERROR_EVENT_CLASS_NULL = "Argument 'eventClass' must not be null";
    private static final String ERROR_EVENT_NULL = "Argument 'event' must not be null";
    private static final String ERROR_CALLABLE_NULL = "Argument 'callable' must not be null";
    private static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";
    private static final String ERROR_PARAMS_NULL = "Argument 'params' must not be null";
    private static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    private static final String ERROR_OWNER_NULL = "Argument 'owner' must not be null";
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEventRouter.class);
    protected final Map<String, List<Object>> instanceListeners = new ConcurrentHashMap<>();
    protected final Map<String, List<Object>> functionalListeners = new ConcurrentHashMap<>();
    private final MethodCache methodCache = new MethodCache();
    private boolean enabled = true;

    @Override
    public boolean isEventPublishingEnabled() {
        synchronized (LOCK) {
            return this.enabled;
        }
    }

    @Override
    public void setEventPublishingEnabled(boolean enabled) {
        synchronized (LOCK) {
            this.enabled = enabled;
        }
    }

    @Override
    public void publishEvent(@Nonnull String eventName) {
        publishEvent(eventName, EMPTY_LIST);
    }

    @Override
    public void publishEvent(@Nonnull String eventName, @Nullable List<?> params) {
        if (!isEventPublishingEnabled()) return;
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        if (params == null) params = EMPTY_LIST;
        buildPublisher(eventName, params, "synchronously").run();
    }

    @Override
    public void publishEventOutsideUI(@Nonnull String eventName) {
        publishEventOutsideUI(eventName, EMPTY_LIST);
    }

    @Override
    public void publishEventOutsideUI(@Nonnull String eventName, @Nullable List<?> params) {
        if (!isEventPublishingEnabled()) return;
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        if (params == null) params = EMPTY_LIST;
        final Runnable publisher = buildPublisher(eventName, params, "outside UI");
        doPublishOutsideUI(publisher);
    }

    protected abstract void doPublishOutsideUI(@Nonnull Runnable publisher);

    @Override
    public void publishEventAsync(@Nonnull String eventName) {
        publishEventAsync(eventName, EMPTY_LIST);
    }

    @Override
    public void publishEventAsync(@Nonnull String eventName, @Nullable List<?> params) {
        if (!isEventPublishingEnabled()) return;
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        if (params == null) params = EMPTY_LIST;
        final Runnable publisher = buildPublisher(eventName, params, "asynchronously");
        doPublishAsync(publisher);
    }

    protected abstract void doPublishAsync(@Nonnull Runnable publisher);

    @Override
    public void publishEvent(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        publishEvent(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void publishEventOutsideUI(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        publishEventOutsideUI(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void publishEventAsync(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        publishEventAsync(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public <E extends Event> void removeEventListener(@Nonnull Class<E> eventClass, @Nonnull CallableWithArgs<?> listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        removeEventListener(eventClass.getSimpleName(), listener);
    }

    @Override
    public <E extends Event> void removeEventListener(@Nonnull Class<E> eventClass, @Nonnull RunnableWithArgs listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        removeEventListener(eventClass.getSimpleName(), listener);
    }

    protected void fireEvent(@Nonnull RunnableWithArgs runnable, @Nonnull List<?> params) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        requireNonNull(params, ERROR_PARAMS_NULL);
        runnable.run(asArray(params));
    }

    protected void fireEvent(@Nonnull CallableWithArgs<?> callable, @Nonnull List<?> params) {
        requireNonNull(callable, ERROR_CALLABLE_NULL);
        requireNonNull(params, ERROR_PARAMS_NULL);
        callable.call(asArray(params));
    }

    protected void fireEvent(@Nonnull Object instance, @Nonnull String eventHandler, @Nonnull List<?> params) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);
        requireNonBlank(eventHandler, ERROR_EVENT_HANDLER_BLANK);
        requireNonNull(params, ERROR_PARAMS_NULL);

        Class[] argTypes = convertToTypeArray(asArray(params));
        MethodDescriptor target = new MethodDescriptor(eventHandler, argTypes);
        Method method = methodCache.findMatchingMethodFor(instance.getClass(), target);

        if (method != null) {
            MethodUtils.invokeSafe(method, instance, asArray(params));
        }
    }

    @Override
    public <E extends Event> void addEventListener(@Nonnull Class<E> eventClass, @Nonnull CallableWithArgs<?> listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        addEventListener(eventClass.getSimpleName(), listener);
    }

    @Override
    public <E extends Event> void addEventListener(@Nonnull Class<E> eventClass, @Nonnull RunnableWithArgs listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        addEventListener(eventClass.getSimpleName(), listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addEventListener(@Nonnull Object listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (listener instanceof RunnableWithArgs) {
            throw new IllegalArgumentException("Cannot add an event listener of type " + RunnableWithArgs.class.getName() +
                " because the target event name is missing. " + listener);
        }
        if (listener instanceof CallableWithArgs) {
            throw new IllegalArgumentException("Cannot add an event listener of type " + CallableWithArgs.class.getName() +
                " because the target event name is missing. " + listener);
        }

        if (listener instanceof Map) {
            addEventListener((Map) listener);
            return;
        }

        if (!methodCache.isEventListener(listener.getClass())) {
            return;
        }

        boolean added = false;
        for (String eventName : methodCache.fetchMethodMetadata(listener.getClass()).keySet()) {
            eventName = eventName.substring(2); // cut off "on" from the name
            List<Object> instances = instanceListeners.get(eventName);
            if (instances == null) {
                instances = new ArrayList<>();
                instanceListeners.put(eventName, instances);
            }
            synchronized (instances) {
                if (!instances.contains(listener)) {
                    added = true;
                    instances.add(listener);
                }
            }
        }

        if (added) {
            try {
                LOG.debug("Adding listener {}", listener);
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Adding listener {}", listener.getClass().getName());
            }
        }
    }

    @Override
    public void addEventListener(@Nonnull Map<String, Object> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        for (Map.Entry<String, Object> entry : listener.entrySet()) {
            Object eventHandler = entry.getValue();
            if (eventHandler instanceof RunnableWithArgs) {
                addEventListener(entry.getKey(), (RunnableWithArgs) eventHandler);
            } else if (eventHandler instanceof CallableWithArgs) {
                addEventListener(entry.getKey(), (CallableWithArgs) eventHandler);
            } else {
                throw new IllegalArgumentException("Unsupported functional event listener " + eventHandler);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeEventListener(@Nonnull Object listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (listener instanceof RunnableWithArgs) {
            throw new IllegalArgumentException("Cannot remove an event listener of type " + RunnableWithArgs.class.getName() +
                " because the target event name is missing. " + listener);
        }
        if (listener instanceof CallableWithArgs) {
            throw new IllegalArgumentException("Cannot remove an event listener of type " + CallableWithArgs.class.getName() +
                " because the target event name is missing. " + listener);
        }

        if (listener instanceof Map) {
            removeEventListener((Map) listener);
            return;
        }

        boolean removed = false;
        for (String eventName : methodCache.fetchMethodMetadata(listener.getClass()).keySet()) {
            eventName = eventName.substring(2); // cut off "on" from the name
            List<Object> instances = instanceListeners.get(eventName);
            if (instances != null && instances.contains(listener)) {
                instances.remove(listener);
                removed = true;
                if (instances.isEmpty()) {
                    instanceListeners.remove(eventName);
                }
            }
        }

        boolean nestedRemoved = removeNestedListeners(listener);

        if (removed || nestedRemoved) {
            try {
                LOG.debug("Removing listener {}", listener);
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Removing listener {}", listener.getClass().getName());
            }
        }
    }

    @Override
    public void removeEventListener(@Nonnull Map<String, Object> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        for (Map.Entry<String, Object> entry : listener.entrySet()) {
            Object eventHandler = entry.getValue();
            if (eventHandler instanceof RunnableWithArgs) {
                removeEventListener(entry.getKey(), (RunnableWithArgs) eventHandler);
            } else if (eventHandler instanceof CallableWithArgs) {
                removeEventListener(entry.getKey(), (CallableWithArgs) eventHandler);
            } else {
                throw new IllegalArgumentException("Unsupported functional event listener " + eventHandler);
            }
        }
    }

    @Override
    public void addEventListener(@Nonnull String eventName, @Nonnull CallableWithArgs<?> listener) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        synchronized (functionalListeners) {
            List<Object> list = functionalListeners.get(capitalize(eventName));
            if (list == null) {
                list = new ArrayList<>();
                functionalListeners.put(capitalize(eventName), list);
            }
            if (list.contains(listener)) return;
            LOG.debug("Adding listener {} on {}", listener.getClass().getName(), capitalize(eventName));
            list.add(listener);
        }
    }

    @Override
    public void addEventListener(@Nonnull String eventName, @Nonnull RunnableWithArgs listener) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        synchronized (functionalListeners) {
            List<Object> list = functionalListeners.get(capitalize(eventName));
            if (list == null) {
                list = new ArrayList<>();
                functionalListeners.put(capitalize(eventName), list);
            }
            if (list.contains(listener)) return;
            LOG.debug("Adding listener {} on {}", listener.getClass().getName(), capitalize(eventName));
            list.add(listener);
        }
    }

    @Override
    public void removeEventListener(@Nonnull String eventName, @Nonnull CallableWithArgs<?> listener) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        synchronized (functionalListeners) {
            List<Object> list = functionalListeners.get(capitalize(eventName));
            if (list != null) {
                LOG.debug("Removing listener {} on {}", listener.getClass().getName(), capitalize(eventName));
                list.remove(listener);
            }
        }
    }

    @Override
    public void removeEventListener(@Nonnull String eventName, @Nonnull RunnableWithArgs listener) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        synchronized (functionalListeners) {
            List<Object> list = functionalListeners.get(capitalize(eventName));
            if (list != null) {
                LOG.debug("Removing listener {} on {}", listener.getClass().getName(), capitalize(eventName));
                list.remove(listener);
            }
        }
    }

    @Nonnull
    @Override
    public Collection<Object> getEventListeners() {
        List<Object> listeners = new ArrayList<>();
        synchronized (instanceListeners) {
            Set<Object> instances = new HashSet<>();
            for (List<Object> objects : instanceListeners.values()) {
                instances.addAll(objects);
            }
            listeners.addAll(instances);
        }

        synchronized (functionalListeners) {
            for (List<Object> objects : functionalListeners.values()) {
                listeners.addAll(objects);
            }
        }

        return unmodifiableCollection(listeners);
    }

    @Nonnull
    @Override
    public Collection<Object> getEventListeners(@Nonnull String eventName) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        List<Object> listeners = new ArrayList<>();
        List<Object> instances = instanceListeners.get(eventName);
        if (instances != null) listeners.addAll(instances);
        instances = functionalListeners.get(eventName);
        if (instances != null) listeners.addAll(instances);
        return unmodifiableCollection(listeners);
    }

    protected Runnable buildPublisher(@Nonnull final String event, @Nonnull final List<?> params, @Nonnull final String mode) {
        requireNonNull(event, ERROR_EVENT_NULL);
        requireNonNull(params, ERROR_PARAMS_NULL);
        requireNonBlank(mode, ERROR_MODE_BLANK);
        return new Runnable() {
            public void run() {
                String eventName = capitalize(event);
                LOG.debug("Triggering event '{}' {}", eventName, mode);
                String eventHandler = "on" + eventName;
                // defensive copying to avoid CME during event dispatching
                List<Object> listenersCopy = new ArrayList<>();
                List<Object> instances = instanceListeners.get(eventName);
                if (instances != null) {
                    listenersCopy.addAll(instances);
                }
                synchronized (functionalListeners) {
                    List list = functionalListeners.get(eventName);
                    if (list != null) {
                        for (Object listener : list) {
                            listenersCopy.add(listener);
                        }
                    }
                }

                for (Object listener : listenersCopy) {
                    if (listener instanceof RunnableWithArgs) {
                        fireEvent((RunnableWithArgs) listener, params);
                    } else if (listener instanceof CallableWithArgs) {
                        fireEvent((CallableWithArgs<?>) listener, params);
                    } else {
                        fireEvent(listener, eventHandler, params);
                    }
                }
            }
        };
    }

    protected boolean removeNestedListeners(@Nonnull Object owner) {
        requireNonNull(owner, ERROR_OWNER_NULL);

        boolean removed = false;
        synchronized (functionalListeners) {
            for (Map.Entry<String, List<Object>> event : functionalListeners.entrySet()) {
                String eventName = event.getKey();
                List<Object> listenerList = event.getValue();
                List<Object> toRemove = new ArrayList<>();
                for (Object listener : listenerList) {
                    if (isNestedListener(listener, owner)) {
                        toRemove.add(listener);
                    }
                }
                removed = toRemove.size() > 0;
                for (Object listener : toRemove) {
                    LOG.debug("Removing listener {} on {}", listener.getClass().getName(), capitalize(eventName));
                    listenerList.remove(listener);
                }
            }
        }

        return removed;
    }

    protected boolean isNestedListener(@Nonnull Object listener, @Nonnull Object owner) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        requireNonNull(owner, ERROR_OWNER_NULL);
        Class<?> listenerClass = listener.getClass();
        return (listenerClass.isMemberClass() || listenerClass.isAnonymousClass() || listenerClass.isLocalClass()) &&
            owner.getClass().equals(listenerClass.getEnclosingClass()) &&
            owner.equals(GriffonClassUtils.getFieldValue(listener, "this$0"));
    }

    protected Object[] asArray(@Nonnull List<?> list) {
        return list.toArray(new Object[list.size()]);
    }

    protected static class MethodCache {
        private final Map<Class<?>, Map<String, List<MethodInfo>>> methodMap = new ConcurrentHashMap<>();

        public boolean isEventListener(@Nonnull Class<?> klass) {
            Map<String, List<MethodInfo>> methodMetadata = methodMap.get(klass);
            if (methodMetadata == null) {
                methodMetadata = fetchMethodMetadata(klass);
                if (!methodMetadata.isEmpty()) {
                    methodMap.put(klass, methodMetadata);
                } else {
                    methodMetadata = null;
                }
            }
            return methodMetadata != null;
        }

        @Nullable
        public Method findMatchingMethodFor(@Nonnull Class<?> klass, @Nonnull MethodDescriptor target) {
            Map<String, List<MethodInfo>> methodMetadata = methodMap.get(klass);

            List<MethodInfo> descriptors = methodMetadata.get(target.getName());
            if (descriptors != null) {
                for (MethodInfo info : descriptors) {
                    if (info.descriptor.matches(target)) {
                        return info.method;
                    }
                }
            }

            return null;
        }

        private Map<String, List<MethodInfo>> fetchMethodMetadata(Class<?> klass) {
            Map<String, List<MethodInfo>> methodMetadata = new LinkedHashMap<>();

            for (Method method : klass.getMethods()) {
                MethodDescriptor descriptor = MethodDescriptor.forMethod(method);
                if (GriffonClassUtils.isEventHandler(descriptor)) {
                    String methodName = method.getName();
                    List<MethodInfo> descriptors = methodMetadata.get(methodName);
                    if (descriptors == null) {
                        descriptors = new ArrayList<>();
                        methodMetadata.put(methodName, descriptors);
                    }
                    descriptors.add(new MethodInfo(descriptor, method));
                }
            }

            return methodMetadata;
        }
    }

    protected static class MethodInfo {
        private final MethodDescriptor descriptor;
        private final Method method;

        public MethodInfo(MethodDescriptor descriptor, Method method) {
            this.descriptor = descriptor;
            this.method = method;
        }

        public MethodDescriptor getDescriptor() {
            return descriptor;
        }

        public Method getMethod() {
            return method;
        }
    }
}
