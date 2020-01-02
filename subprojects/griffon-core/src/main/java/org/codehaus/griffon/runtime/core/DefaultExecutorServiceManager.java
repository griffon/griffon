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
package org.codehaus.griffon.runtime.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.ExecutorServiceManager;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.unmodifiableSet;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultExecutorServiceManager implements ExecutorServiceManager {
    private final Set<ExecutorService> executorServices = new LinkedHashSet<>();

    @Nonnull
    @Override
    public Collection<ExecutorService> getExecutorServices() {
        return unmodifiableSet(executorServices);
    }

    @Override
    @Nullable
    public ExecutorService add(@Nullable ExecutorService executorService) {
        if (executorService != null) {
            executorServices.add(executorService);
        }
        return executorService;
    }

    @Override
    @Nullable
    public ExecutorService remove(@Nullable ExecutorService executorService) {
        executorServices.remove(executorService);
        return executorService;
    }

    @Override
    public void shutdownAll() {
        for (ExecutorService executorService : executorServices) {
            if (!executorService.isShutdown()) {
                executorService.shutdown();
            }
        }
    }
}
