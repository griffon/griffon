/*
 * Copyright 2008-2015 the original author or authors.
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
import java.util.Set;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public interface Context {
    /**
     * Searches for the key in this context only
     *
     * @param key the key to search
     * @return true if the context contains the given key, false otherwise
     */
    boolean containsKey(@Nonnull String key);

    /**
     * Searches for the key in this context and its hierarchy
     *
     * @param key the key to search
     * @return true if the context (or its parent) contains the given key, false otherwise
     * @since 2.4.0
     */
    boolean hasKey(@Nonnull String key);

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

    /**
     * Returns a {@link Set} view of the keys contained in this context.
     *
     * @return a set view of the keys contained in this map
     * @since 2.4.0
     */
    @Nonnull
    Set<String> keySet();
}
