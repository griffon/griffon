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
package griffon.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public interface MutableConfiguration extends Configuration {
    /**
     * Sets a key/value pair on this configuration.
     *
     * @param key   the key to be registered
     * @param value the value to save
     */
    void set(@Nonnull String key, @Nonnull Object value);

    /**
     * Removes a key from this configuration.
     *
     * @param key the key to be removed
     *
     * @return the value associated with the key or <tt>null</tt> if there wasn't any value.
     */
    @Nullable
    <T> T remove(@Nonnull String key);

    /**
     * Removes a key from this configuration. The value is
     * converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key  the key to be removed
     * @param type the type to be returned
     *
     * @return the value associated with the key or <tt>null</tt> if there wasn't any value.
     *
     * @since 2.5.0
     */
    @Nullable
    <T> T removeConverted(@Nonnull String key, @Nonnull Class<T> type);
}
