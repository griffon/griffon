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
package org.codehaus.griffon.runtime.injection;

import com.google.inject.Binding;
import com.google.inject.Injector;
import org.codehaus.griffon.runtime.core.injection.InjectionUnitOfWork;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.inject.Scopes.isSingleton;
import static griffon.util.GriffonClassUtils.hasMethodAnnotatedwith;
import static griffon.util.GriffonClassUtils.invokeAnnotatedMethod;
import static java.util.Collections.synchronizedMap;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.6.0
 */
class InstanceTracker {
    private static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    private static final String ERROR_BINDING_NULL = "Argument 'binding' must not be null";

    private final Map<Object, Binding<?>> instanceToKeyMap = synchronizedMap(new LinkedHashMap<Object, Binding<?>>());

    private com.google.inject.Injector injector;

    public void setInjector(@Nonnull Injector injector) {
        this.injector = injector;
    }

    @Nonnull
    public Injector getInjector() {
        return injector;
    }

    @Nonnull
    public <T> T track(@Nonnull Binding<?> binding, @Nonnull final T instance) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        if (hasMethodAnnotatedwith(instance, PreDestroy.class)) {
            if (isSingleton(binding)) {
                instanceToKeyMap.put(instance, binding);
            } else {
                try {
                    InjectionUnitOfWork.track(instance);
                } catch (IllegalStateException ise) {
                    instanceToKeyMap.put(instance, binding);
                }
            }
        }
        return instance;
    }

    public <T> void release(@Nonnull T instance) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        invokeAnnotatedMethod(instance, PreDestroy.class);

        Binding<?> binding = instanceToKeyMap.get(instance);
        if (binding != null) {
            instanceToKeyMap.remove(instance);
        }
    }

    public void releaseAll() {
        List<Object> instances = new ArrayList<>();

        instances.addAll(instanceToKeyMap.keySet());
        instanceToKeyMap.clear();

        Collections.reverse(instances);

        for (Object instance : instances) {
            invokeAnnotatedMethod(instance, PreDestroy.class);
        }

        instances.clear();
    }
}