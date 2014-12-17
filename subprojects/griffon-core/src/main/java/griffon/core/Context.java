/*
 * Copyright 2008-2014 the original author or authors.
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

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public interface Context {
    boolean containsKey(@Nonnull String key);

    @Nullable
    Object remove(@Nonnull String key);

    void put(@Nonnull String key, @Nullable Object value);

    void putAt(@Nonnull String key, @Nullable Object value);

    @Nullable
    Object get(@Nonnull String key);

    @Nullable
    <T> T get(@Nonnull String key, @Nullable T defaultValue);

    @Nullable
    Object getAt(@Nonnull String key);

    @Nullable
    <T> T getAt(@Nonnull String key, @Nullable T defaultValue);

    void destroy();
}
