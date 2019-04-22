/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.ExceptionHandler;
import griffon.core.ExecutorServiceManager;
import griffon.core.event.EventRouter;
import griffon.util.MethodDescriptor;
import griffon.util.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static griffon.util.GriffonClassUtils.isEventHandler;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractEventRouter implements EventRouter {
    protected static final Object[] LOCK = new Object[0];
    private static final String ERROR_MODE_BLANK = "Argument 'mode' must not be blank";
    private static final String ERROR_HANDLER_NULL = "Argument 'handler' must not be null";
    private static final String ERROR_EVENT_NULL = "Argument 'event' must not be null";
    private static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";
    private static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEventRouter.class);
    protected final Map<String, List<Object>> instanceListeners = new ConcurrentHashMap<>();
    private final MethodCache methodCache = new MethodCache();
    private boolean enabled = true;

    protected static final AtomicInteger EVENT_ROUTER_ID = new AtomicInteger(1);

    protected ExecutorServiceManager executorServiceManager;
    protected final ExecutorService executorService;
    protected final int eventRouterId;

    @Inject
    private ExceptionHandler exceptionHandler;

    public AbstractEventRouter() {
        eventRouterId = EVENT_ROUTER_ID.getAndIncrement();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory(eventRouterId));
    }

    @Inject
    public void setExecutorServiceManager(@Nonnull ExecutorServiceManager executorServiceManager) {
        requireNonNull(executorServiceManager, "Argument 'executorServiceManager' must not be null");
        if (this.executorServiceManager != null) {
            this.executorServiceManager.remove(executorService);
        }
        this.executorServiceManager = executorServiceManager;
        this.executorServiceManager.add(executorService);
    }

    protected void runInsideExecutorService(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        executorService.submit(() -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                exceptionHandler.uncaughtException(Thread.currentThread(), throwable);
            }
        });
    }

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
    public void subscribe(Object handler) {
        requireNonNull(handler, ERROR_HANDLER_NULL);
        if (!methodCache.isEventListener(handler.getClass())) {
            return;
        }

        boolean added = false;
        for (String eventType : methodCache.fetchMethodMetadata(handler.getClass()).keySet()) {
            List<Object> instances = instanceListeners.computeIfAbsent(eventType, (k) -> new ArrayList<>());
            if (!instances.contains(handler)) {
                added = true;
                instances.add(handler);
            }
        }

        if (added) {
            try {
                LOG.debug("Adding listener {}", handler);
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Adding listener {}", handler.getClass().getName());
            }
        }
    }

    @Override
    public void unsubscribe(Object handler) {
        requireNonNull(handler, ERROR_HANDLER_NULL);
        if (!methodCache.isEventListener(handler.getClass())) {
            return;
        }

        boolean removed = false;
        for (String eventType : methodCache.fetchMethodMetadata(handler.getClass()).keySet()) {
            List<Object> instances = instanceListeners.get(eventType);
            if (instances != null && instances.contains(handler)) {
                instances.remove(handler);
                removed = true;
                if (instances.isEmpty()) {
                    instanceListeners.remove(eventType);
                }
            }
        }

        if (removed) {
            try {
                LOG.debug("Removing listener {}", handler);
            } catch (UnsupportedOperationException uoe) {
                LOG.debug("Removing listener {}", handler.getClass().getName());
            }
        }
    }

    @Override
    public <E> void publishEvent(E event) {
        if (!isEventPublishingEnabled()) { return; }
        requireNonNull(event, ERROR_EVENT_NULL);
        buildPublisher(event, "synchronously").run();
    }

    @Override
    public <E> void publishEventAsync(E event) {
        if (!isEventPublishingEnabled()) { return; }
        requireNonNull(event, ERROR_EVENT_NULL);
        Runnable publisher = buildPublisher(event, "asynchronously");
        doPublishAsync(publisher);
    }

    @Override
    public <E> void publishEventOutsideUI(E event) {
        if (!isEventPublishingEnabled()) { return; }
        requireNonNull(event, ERROR_EVENT_NULL);
        Runnable publisher = buildPublisher(event, "outside UI");
        doPublishOutsideUI(publisher);
    }

    protected abstract void doPublishOutsideUI(@Nonnull Runnable publisher);

    protected abstract void doPublishAsync(@Nonnull Runnable publisher);

    protected <E> void fireEvent(@Nonnull Object instance, @Nonnull E event) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);
        requireNonNull(event, ERROR_EVENT_NULL);

        Method method = methodCache.findMatchingMethodFor(instance.getClass(), event.getClass());

        if (method != null) {
            MethodUtils.invokeUnwrapping(method, instance, new Object[]{event});
        }
    }

    protected <E> Runnable buildPublisher(@Nonnull final E event, @Nonnull final String mode) {
        requireNonNull(event, ERROR_EVENT_NULL);
        requireNonBlank(mode, ERROR_MODE_BLANK);
        return () -> {
            String eventType = event.getClass().getName();
            LOG.debug("Triggering event '{}' {}", event.getClass().getSimpleName(), mode);
            // defensive copying to avoid CME during event dispatching
            List<Object> listenersCopy = new ArrayList<>();
            List<Object> instances = instanceListeners.get(eventType);
            if (instances != null) {
                listenersCopy.addAll(instances);
            }

            for (Object listener : listenersCopy) {
                fireEvent(listener, event);
            }
        };
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
        public Method findMatchingMethodFor(@Nonnull Class<?> klass, @Nonnull Class<?> eventType) {
            Map<String, List<MethodInfo>> methodMetadata = methodMap.get(klass);

            Class[] otherParamTypes = {eventType};
            for (List<MethodInfo> descriptors : methodMetadata.values()) {
                for (MethodInfo info : descriptors) {
                    if (info.descriptor.matches(otherParamTypes)) {
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
                if (isEventHandler(descriptor)) {
                    String eventType = method.getParameterTypes()[0].getName();
                    List<MethodInfo> descriptors = methodMetadata.computeIfAbsent(eventType, k -> new ArrayList<>());
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

    private static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private DefaultThreadFactory(int eventRouterId) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
            namePrefix = "event-router-" + eventRouterId + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) { t.setDaemon(false); }
            if (t.getPriority() != Thread.NORM_PRIORITY) { t.setPriority(Thread.NORM_PRIORITY); }
            return t;
        }
    }
}
