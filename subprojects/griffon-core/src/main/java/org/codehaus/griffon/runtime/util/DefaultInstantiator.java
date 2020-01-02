/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package org.codehaus.griffon.runtime.util;

import griffon.annotations.core.Nonnull;
import griffon.core.injection.Injector;
import griffon.exceptions.InstanceNotFoundException;
import griffon.util.Instantiator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

import static griffon.util.GriffonClassUtils.invokeAnnotatedMethod;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class DefaultInstantiator implements Instantiator {
    private final Provider<Injector> injector;

    @Inject
    public DefaultInstantiator(@Nonnull Provider<Injector> injector) {
        this.injector = requireNonNull(injector, "Argument 'injector' must not be null");
    }

    @Override
    public <T> T instantiate(@Nonnull Class<? extends T> klass) {
        Injector injector = this.injector.get();

        if (injector != null) {
            try {
                return (T) injector.getInstance(klass);
            } catch (InstanceNotFoundException e) {
                return newInstanceFromClass(klass);
            }
        } else {
            return newInstanceFromClass(klass);
        }
    }

    @Nonnull
    protected <T> T newInstanceFromClass(@Nonnull Class<? extends T> klass) {
        try {
            T instance = klass.newInstance();
            Injector injector = this.injector.get();
            if (injector != null) {
                injector.injectMembers(instance);
                invokeAnnotatedMethod(instance, PostConstruct.class);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
