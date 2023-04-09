/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConverterRegistry;

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
     * @param key the key to search. Must not be {@code null}.
     * @return {@code true} if the context (or its parent) contains the given key, {@code false} otherwise.
     */
    boolean containsKey(@Nonnull String key);

    /**
     * /**
     * Finds a value associated with the given key. The value is
     * blindly cast to type {@code T} if found.
     *
     * @param key the key to search. Must not be {@code null}.
     * @return the value associated with {@code key}, {@code null} otherwise.
     * @throws ClassCastException if the value is not of the expected type.
     */
    @Nullable
    <T> T get(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * blindly cast to type {@code T} if found. If not found then the
     * supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found. May be {@code null}.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws ClassCastException if the value is not of the expected type.
     */
    @Nullable
    <T> T get(@Nonnull String key, @Nullable T defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code boolean} if found.
     *
     * @param key the key to search. Must not be {@code null}.
     * @return the value associated with {@code key}, or {@code false} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code boolean}.
     */
    boolean getAsBoolean(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code boolean} if found. If not found then the
     * supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code boolean}.
     */
    boolean getAsBoolean(@Nonnull String key, boolean defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to an {@code int} if found.
     *
     * @param key the key to search. Must not be {@code null}.
     * @return the value associated with {@code key}, or {@code 0} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to an {@code int}.
     */
    int getAsInt(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to an {@code int} if found. If not found then the
     * supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to an {@code int}.
     */
    int getAsInt(@Nonnull String key, int defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code long} if found.
     *
     * @param key the key to search. Must not be {@code null}.
     * @return the value associated with {@code key}, or {@code 0L} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code long}.
     */
    long getAsLong(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code long} if found. If not found then the
     * supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code long}.
     */
    long getAsLong(@Nonnull String key, long defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code float} if found.
     *
     * @param key the key to search. Must not be {@code null}.
     * @return the value associated with {@code key}, or {@code 0.0f} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code float}.
     */
    float getAsFloat(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code float} if found. If not found then the
     * supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code float}.
     */
    float getAsFloat(@Nonnull String key, float defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code double} if found.
     *
     * @param key the key to search. Must not be {@code null}.
     * @return the value associated with {@code key}, or {@code 0.0d} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code double}.
     */
    double getAsDouble(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code double} if found. If not found then the
     * supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to a {@code double}.
     */
    double getAsDouble(@Nonnull String key, double defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code String} if found.
     *
     * @param key the key to search. Must not be {@code null}.
     * @return the literal value associated with {@code key}, or {@code null} if it was not found.
     */
    @Nullable
    String getAsString(@Nonnull String key);

    /**
     * Finds a value associated with the given key. The value is
     * converted to a {@code String} if found. If not found then the
     * supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found. May be {@code null}.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     */
    @Nullable
    String getAsString(@Nonnull String key, @Nullable String defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type {@code T} if found using a {@code Converter}.
     *
     * @param key  the key to search. Must not be {@code null}.
     * @param type the type to be returned. Must not be {@code null}.
     * @return the value associated with {@code key}, {@code null} otherwise.
     * @throws griffon.converter.ConversionException if the resource could not be converted to the target type {@code T}.
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type {@code T}if found using a {@code Converter}.
     *
     * @param key    the key to search. Must not be {@code null}.
     * @param type   the type to be returned. Must not be {@code null}.
     * @param format format used to convert the value. Must not be {@code null}.
     * @return the value associated with {@code key}, {@code null} otherwise.
     * @throws griffon.converter.ConversionException if the resource could not be converted to the target type {@code T}.
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nonnull String format);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type {@code T} if found using a {@code Converter}.
     * If not found then the supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param type         the type to be returned. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found. May be {@code null}.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to the target type {@code T}.
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nullable T defaultValue);

    /**
     * Finds a value associated with the given key. The value is
     * converted to type {@code T} if found using a {@code Converter}.
     * If not found then the supplied {@code defaultValue} will be returned.
     *
     * @param key          the key to search. Must not be {@code null}.
     * @param type         the type to be returned. Must not be {@code null}.
     * @param format       format used to convert the value. Must not be {@code null}.
     * @param defaultValue the value to be returned if the key is not found. May be {@code null}.
     * @return the value associated with {@code key}, or {@code defaultValue} if it was not found.
     * @throws griffon.converter.ConversionException if the resource could not be converted to the target type {@code T}.
     */
    @Nullable
    <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nonnull String format, @Nullable T defaultValue);

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
     * @return returns the value associated with the key, <tt>defaultValue</tt> if the key was not found
     */
    @Nullable
    <T> T getAt(@Nonnull String key, @Nullable T defaultValue);
}
