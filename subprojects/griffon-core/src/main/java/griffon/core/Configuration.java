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
package griffon.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface Configuration {
    /**
     * Searches for the key in this configuration.
     *
     * @param key the key to search
     * @return true if the context (or its parent) contains the given key, false otherwise
     */
    boolean containsKey(@Nonnull String key);

    @Nonnull
    Map<String, Object> asFlatMap();

    @Nonnull
    ResourceBundle asResourceBundle();

    @Nonnull
    Properties asProperties();

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key to search
     * @return the value associated with the key or <tt>null<</tt> if not found.
     */
    @Nullable
    Object get(@Nonnull String key);

    /**
     * Returns the value associated with the given key.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key was not found
     * @param <T>          the type of the value
     * @return returns the value associated with the key, <tt>defaultValue</tt> if the key was not found
     */
    @Nullable
    <T> T get(@Nonnull String key, @Nullable T defaultValue);

    /**
     * Returns the value associated with the given key.
     * Convenience method to use in Groovy aware environments.
     *
     * @param key the key to search
     * @return the value associated with the key or <tt>null<</tt> if not found.
     */
    @Nullable
    Object getAt(@Nonnull String key);

    /**
     * Returns the value associated with the given key.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key was not found
     * @param <T>          the type of the value
     * @return returns the value associated with the key, <tt>defaultValue</tt> if the key was not found
     */
    @Nullable
    <T> T getAt(@Nonnull String key, @Nullable T defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>boolean</tt> if found.
     *
     * @param key the key to search
     */
    boolean getAsBoolean(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>boolean</tt> if found. If not found then the
     * supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key is not found
     */
    boolean getAsBoolean(@Nonnull String key, boolean defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to an <tt>int</tt> if found.
     *
     * @param key the key to search
     */
    int getAsInt(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to an <tt>int</tt> if found. If not found then the
     * supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key is not found
     */
    int getAsInt(@Nonnull String key, int defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>long</tt> if found.
     *
     * @param key the key to search
     */
    long getAsLong(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>long</tt> if found. If not found then the
     * supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key is not found
     */
    long getAsLong(@Nonnull String key, long defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>float</tt> if found.
     *
     * @param key the key to search
     */
    float getAsFloat(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>float</tt> if found. If not found then the
     * supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key is not found
     */
    float getAsFloat(@Nonnull String key, float defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>double</tt> if found.
     *
     * @param key the key to search
     */
    double getAsDouble(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>double</tt> if found. If not found then the
     * supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key is not found
     */
    double getAsDouble(@Nonnull String key, double defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>String</tt> if found.
     *
     * @param key the key to search
     */
    @Nullable
    String getAsString(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a <tt>String</tt> if found. If not found then the
     * supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key is not found
     */
    @Nullable
    String getAsString(@Nonnull String key, @Nullable String defaultValue);

    /**
     * /**
     * Finds a value associated with the given key. The value is
     * blindly cast to type <tt>T</tt> if found.
     *
     * @param key the key to search
     * @since 2.5.0
     */
    @Nullable
    <T> T getAs(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * blindly cast to type <tt>T</tt> if found. If not found then the
     * supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param defaultValue the value to be returned if the key is not found
     * @since 2.5.0
     */
    @Nullable
    <T> T getAs(@Nonnull String key, @Nullable T defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type <tt>T</tt> if found using a {@code PropertyEditor}.
     *
     * @param key  the key to search
     * @param type the type to be returned
     * @since 2.5.0
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type <tt>T</tt> if found using a {@code PropertyEditor}.
     *
     * @param key  the key to search
     * @param type the type to be returned
     * @param format format used to convert the value
     * @since 2.11.0
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nonnull String format);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type <tt>T</tt> if found using a {@code PropertyEditor}.
     * If not found then the supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param type         the type to be returned
     * @param defaultValue the value to be returned if the key is not found
     * @since 2.5.0
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nullable T defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type <tt>T</tt> if found using a {@code PropertyEditor}.
     * If not found then the supplied <tt>defaultValue</tt> will be returned.
     *
     * @param key          the key to search
     * @param type         the type to be returned
     * @param format format used to convert the value
     * @param defaultValue the value to be returned if the key is not found
     * @since 2.11.0
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nonnull String format, @Nullable T defaultValue);
}
