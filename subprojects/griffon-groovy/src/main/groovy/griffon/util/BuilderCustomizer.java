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
package griffon.util;

import groovy.lang.Closure;
import groovy.util.Factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public interface BuilderCustomizer {
    @Nonnull
    Map<String, Object> getVariables();

    @Nonnull
    Map<String, Factory> getFactories();

    @Nonnull
    Map<String, Closure> getMethods();

    @Nonnull
    Map<String, Closure[]> getProps();

    @Nonnull
    List<Closure> getAttributeDelegates();

    @Nonnull
    List<Closure> getPreInstantiateDelegates();

    @Nonnull
    List<Closure> getPostInstantiateDelegates();

    @Nonnull
    List<Closure> getPostNodeCompletionDelegates();

    @Nonnull
    List<Closure> getDisposalClosures();

    @Nullable
    Closure getMethodMissingDelegate();

    @Nullable
    Closure getPropertyMissingDelegate();
}
