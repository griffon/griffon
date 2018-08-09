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
import javax.application.converter.ConverterRegistry;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface Configuration extends javax.application.configuration.Configuration {
    @Nonnull
    ConverterRegistry getConverterRegistry();

    @Nonnull
    Map<String, Object> asFlatMap();

    @Nonnull
    ResourceBundle asResourceBundle();

    @Nonnull
    Properties asProperties();

    /**
     * Returns the value associated with the given key.
     * Convenience method to use in Groovy aware environments.
     *
     * @param key the key to search
     *
     * @return the value associated with the key or <tt>null<</tt> if not found.
     */
    @Nullable
    <T> T getAt(@Nonnull String key);

    /**
     * Returns the value associated with the given key.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key was not found
     * @param <T>          the type of the value
     *
     * @return returns the value associated with the key, <tt>defaultValue</tt> if the key was not found
     */
    @Nullable
    <T> T getAt(@Nonnull String key, @Nullable T defaultValue);
}
