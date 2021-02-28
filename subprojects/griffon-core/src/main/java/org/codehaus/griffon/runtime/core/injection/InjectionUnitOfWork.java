/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.core.injection;

import griffon.annotations.core.Nonnull;

import java.util.ArrayList;
import java.util.List;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.6.0
 */
public class InjectionUnitOfWork {
    private static final ThreadLocal<List<Object>> CONTEXT = new ThreadLocal<>();
    private static final String ERROR_NO_UNITOFWORK_IN_PROCESS = "There is no InjectionUnitOfWork in process!";

    private InjectionUnitOfWork() {
        // prevent instantiation
    }

    public static void start() {
        requireState(CONTEXT.get() == null, "There is already an existing InjectionUnitOfWork in process!");
        CONTEXT.set(new ArrayList<>());
    }

    @Nonnull
    public static List<Object> finish() {
        List<Object> instances = CONTEXT.get();
        requireState(instances != null, ERROR_NO_UNITOFWORK_IN_PROCESS);
        CONTEXT.set(null);
        return instances;
    }

    public static void track(@Nonnull Object instance) {
        requireNonNull(instance, "Argument 'instance' must not be null");
        List<Object> instances = CONTEXT.get();
        requireState(instances != null, ERROR_NO_UNITOFWORK_IN_PROCESS);
        instances.add(instance);
    }
}