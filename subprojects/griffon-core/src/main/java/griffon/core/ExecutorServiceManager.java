/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package griffon.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface ExecutorServiceManager {
    /**
     * Returns an immutable view of all {@code ExecutorService}s currently managed.
     *
     * @return a collection of all {@code ExecutorService}s or empty if none.
     * @since 2.4.0
     */
    @Nonnull
    Collection<ExecutorService> getExecutorServices();

    @Nullable
    ExecutorService add(@Nullable ExecutorService executorService);

    @Nullable
    ExecutorService remove(@Nullable ExecutorService executorService);

    void shutdownAll();
}
