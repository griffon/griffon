/*
 * Copyright 2009-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.core.event;

import griffon.core.CallableWithArgs;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonClassUtils.convertToTypeArray;
import static griffon.util.GriffonNameUtils.capitalize;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.synchronizedList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractEventRouter implements EventRouter {
    private static final String ERROR_EVENT_NAME_BLANK = "Argument 'eventName' cannot be blank";
    private static final String ERROR_EVENT_HANDLER_BLANK = "Argument 'eventHandler' cannot be blank";
    private static final String ERROR_MODE_BLANK = "Argument 'mode' cannot be blank";
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' cannot be null";
    private static final String ERROR_EVENT_CLASS_NULL = "Argument 'eventClass' cannot be null";
    private static final String ERROR_EVENT_NULL = "Argument 'event' cannot be null";
    private static final String ERROR_CALLABLE_NULL = "Argument 'callable' cannot be null";
    private static final String ERROR_PARAMS_NULL = "Argument 'params' cannot be null";
    private static final String ERROR_INSTANCE_NULL = "Argument 'instance' cannot be null";
    private static final String ERROR_OWNER_NULL = "Argument 'owner' cannot be null";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEventRouter.class);
    protected static final Object[] LOCK = new Object[0];
    private boolean enabled = true;
    protected final List<Object> listeners = synchronizedList(new ArrayList<>());
    protected final Map<String, List<CallableWithArgs<?>>> callableListeners = new ConcurrentHashMap<>();
    private final MethodCache methodCache = new MethodCache();

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
    public void publish(@Nonnull String eventName) {
        publish(eventName, EMPTY_LIST);
    }

    @Override
    public void publish(@Nonnull String eventName, @Nullable List<?> params) {
        if (!isEnabled()) return;
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        if (params == null) params = EMPTY_LIST;
        buildPublisher(eventName, params, "synchronously").run();
    }

    @Override
    public void publishOutsideUI(@Nonnull String eventName) {
        publishOutsideUI(eventName, EMPTY_LIST);
    }

    @Override
    public void publishOutsideUI(@Nonnull String eventName, @Nullable List<?> params) {
        if (!isEnabled()) return;
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        if (params == null) params = EMPTY_LIST;
        final Runnable publisher = buildPublisher(eventName, params, "outside UI");
        doPublishOutsideUI(publisher);
    }

    protected abstract void doPublishOutsideUI(@Nonnull Runnable publisher);

    @Override
    public void publishAsync(@Nonnull String eventName) {
        publishAsync(eventName, EMPTY_LIST);
    }

    @Override
    public void publishAsync(@Nonnull String eventName, @Nullable List<?> params) {
        if (!isEnabled()) return;
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        if (params == null) params = EMPTY_LIST;
        final Runnable publisher = buildPublisher(eventName, params, "asynchronously");
        doPublishAsync(publisher);
    }

    protected abstract void doPublishAsync(@Nonnull Runnable publisher);

    @Override
    public void publish(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        publish(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void publishOutsideUI(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        publishOutsideUI(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void publishAsync(@Nonnull Event event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        publishAsync(event.getClass().getSimpleName(), asList(event));
    }

    @Override
    public void removeEventListener(@Nonnull Class<? extends Event> eventClass, @Nonnull CallableWithArgs<?> listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        removeEventListener(eventClass.getSimpleName(), listener);
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
    public void addEventListener(@Nonnull Class<? extends Event> eventClass, @Nonnull CallableWithArgs<?> listener) {
        requireNonNull(eventClass, ERROR_EVENT_CLASS_NULL);
        addEventListener(eventClass.getSimpleName(), listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addEventListener(@Nonnull Object listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (listener instanceof CallableWithArgs) return;
        if (listener instanceof Map) {
            addEventListener((Map) listener);
            return;
        }

        if (!methodCache.isEventListener(listener.getClass())) {
            return;
        }

        synchronized (listeners) {
            if (listeners.contains(listener)) return;
            try {
                LOG.debug("Adding listener {}", listener);
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Adding listener {}", listener.getClass().getName());
            }
            listeners.add(listener);
        }
    }

    @Override
    public void addEventListener(@Nonnull Map<String, CallableWithArgs<?>> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        for (Map.Entry<String, CallableWithArgs<?>> entry : listener.entrySet()) {
            addEventListener(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeEventListener(@Nonnull Object listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (listener instanceof CallableWithArgs) return;
        if (listener instanceof Map) {
            removeEventListener((Map) listener);
            return;
        }
        synchronized (listeners) {
            try {
                LOG.debug("Removing listener {}", listener);
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Removing listener {}", listener.getClass().getName());
            }
            listeners.remove(listener);
            removeNestedListeners(listener);
        }
    }

    @Override
    public void removeEventListener(@Nonnull Map<String, CallableWithArgs<?>> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        for (Map.Entry<String, CallableWithArgs<?>> entry : listener.entrySet()) {
            removeEventListener(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addEventListener(@Nonnull String eventName, @Nonnull CallableWithArgs<?> listener) {
        requireNonBlank(eventName, ERROR_EVENT_NAME_BLANK);
        requireNonNull(listener, ERROR_LISTENER_NULL);
        synchronized (callableListeners) {
            List<CallableWithArgs<?>> list = callableListeners.get(capitalize(eventName));
            if (list == null) {
                list = new ArrayList<>();
                callableListeners.put(capitalize(eventName), list);
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
        synchronized (callableListeners) {
            List<CallableWithArgs<?>> list = callableListeners.get(capitalize(eventName));
            if (list != null) {
                LOG.debug("Removing listener {} on {}", listener.getClass().getName(), capitalize(eventName));
                list.remove(listener);
            }
        }
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
                // GRIFFON-224
                List<Object> listenersCopy = new ArrayList<>();
                synchronized (listeners) {
                    listenersCopy.addAll(listeners);
                }
                synchronized (callableListeners) {
                    List list = callableListeners.get(eventName);
                    if (list != null) {
                        for (Object listener : list) {
                            listenersCopy.add(listener);
                        }
                    }
                }

                for (Object listener : listenersCopy) {
                    if (listener instanceof CallableWithArgs) {
                        fireEvent((CallableWithArgs<?>) listener, params);
                    } else {
                        fireEvent(listener, eventHandler, params);
                    }
                }
            }
        };
    }

    protected void removeNestedListeners(@Nonnull Object owner) {
        requireNonNull(owner, ERROR_OWNER_NULL);
        synchronized (callableListeners) {
            for (Map.Entry<String, List<CallableWithArgs<?>>> event : callableListeners.entrySet()) {
                String eventName = event.getKey();
                List<CallableWithArgs<?>> listenerList = event.getValue();
                List<CallableWithArgs<?>> toRemove = new ArrayList<>();
                for (CallableWithArgs<?> listener : listenerList) {
                    if (isNestedListener(listener, owner)) {
                        toRemove.add(listener);
                    }
                }
                for (CallableWithArgs<?> listener : toRemove) {
                    LOG.debug("Removing listener {} on {}", listener.getClass().getName(), capitalize(eventName));
                    listenerList.remove(listener);
                }
            }
        }
    }

    protected boolean isNestedListener(@Nonnull CallableWithArgs<?> listener, @Nonnull Object owner) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        requireNonNull(owner, ERROR_OWNER_NULL);
        Class<?> listenerClass = listener.getClass();
        return listenerClass.isMemberClass() &&
            listenerClass.getEnclosingClass().equals(owner.getClass()) &&
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
