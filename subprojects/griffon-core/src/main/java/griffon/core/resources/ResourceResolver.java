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
package griffon.core.resources;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Interface for resolving resources, with support for the parameterization and internationalization of such resources.
 *
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 2.0.0
 */
public interface ResourceResolver extends javax.application.resources.ResourceResolver {
    /**
     * "@["
     */
    String REF_KEY_START = "@[";

    /**
     * "]"
     */
    String REF_KEY_END = "]";

    @Nonnull
    @Override
    <T> T resolveResource(@Nonnull String key) throws NoSuchResourceException;

    @Nonnull
    @Override
    <T> T resolveResource(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException;

    @Nonnull
    @Override
    <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args) throws NoSuchResourceException;

    @Nonnull
    @Override
    <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale) throws NoSuchResourceException;

    /**
     * Try to resolve the resource.
     *
     * @param key  Key to lookup, such as 'sample.SampleModel.icon'
     * @param args Arguments that will be filled in for params within the resource (params look like "{0}" within a
     *             resource, but this might differ between implementations), or {@code null} if none.
     *
     * @return The resolved resource at the given key for the default locale
     *
     * @throws NoSuchResourceException if no resource is found
     */
    @Nonnull
    <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args) throws NoSuchResourceException;

    /**
     * Try to resolve the resource.
     *
     * @param key    Key to lookup, such as 'sample.SampleModel.icon'
     * @param args   Arguments that will be filled in for params within the resource (params look like "{0}" within a
     *               resource, but this might differ between implementations), or {@code null} if none.
     * @param locale Locale in which to lookup
     *
     * @return The resolved resource at the given key for the given locale
     *
     * @throws NoSuchResourceException if no resource is found
     */
    @Nonnull
    <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale) throws NoSuchResourceException;

    @Nullable
    @Override
    <T> T resolveResource(@Nonnull String key, @Nullable T defaultValue);

    @Nullable
    @Override
    <T> T resolveResource(@Nonnull String key, @Nonnull Locale locale, @Nullable T defaultValue);

    @Nullable
    @Override
    <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nullable T defaultValue);

    @Nullable
    @Override
    <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable T defaultValue);

    /**
     * Try to resolve the resource. Return default resource if no resource was found.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{0}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param defaultValue Message to return if the lookup fails
     *
     * @return The resolved resource at the given key for the default locale
     */
    @Nullable
    <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nullable T defaultValue);

    /**
     * Try to resolve the resource. Return default resource if no resource was found.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{0}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param locale       Locale in which to lookup
     * @param defaultValue Message to return if the lookup fails
     *
     * @return The resolved resource at the given key for the given locale
     */
    @Nullable
    <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable T defaultValue);

    /**
     * Try to resolve the resource.
     *
     * @param key  Key to lookup, such as 'sample.SampleModel.icon'
     * @param args Arguments that will be filled in for params within the resource (params look like "{:key}"
     *             within a resource, but this might differ between implementations), or {@code null} if none.
     *
     * @return The resolved resource at the given key for the default locale
     *
     * @throws NoSuchResourceException if no resource is found
     */
    @Nonnull
    <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchResourceException;

    /**
     * Try to resolve the resource.
     *
     * @param key    Key to lookup, such as 'sample.SampleModel.icon'
     * @param args   Arguments that will be filled in for params within the resource (params look like "{:key}"
     *               within a resource, but this might differ between implementations), or {@code null} if none.
     * @param locale Locale in which to lookup
     *
     * @return The resolved resource at the given key for the given locale
     *
     * @throws NoSuchResourceException if no resource is found
     */
    @Nonnull
    <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale) throws NoSuchResourceException;

    /**
     * Try to resolve the resource. Return default resource if no resource was found.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{:key}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param defaultValue Message to return if the lookup fails
     *
     * @return The resolved resource at the given key for the default locale
     */
    @Nullable
    <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable T defaultValue);

    /**
     * Try to resolve the resource. Return default resource if no resource was found.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{:key}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param locale       Locale in which to lookup
     * @param defaultValue Message to return if the lookup fails
     *
     * @return The resolved resource at the given key for the given locale
     */
    @Nullable
    <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable T defaultValue);

    @Nonnull
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Class<T> type) throws NoSuchResourceException;

    @Nonnull
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException;

    @Nonnull
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Class<T> type) throws NoSuchResourceException;

    @Nonnull
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException;

    /**
     * Try to resolve the resource. The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key  Key to lookup, such as 'sample.SampleModel.icon'
     * @param args Arguments that will be filled in for params within the resource (params look like "{0}" within a
     *             resource, but this might differ between implementations), or {@code null} if none.
     * @param type the type to be returned
     *
     * @return The resolved resource at the given key for the default locale
     *
     * @throws NoSuchResourceException if no resource is found
     * @since 2.5.0
     */
    @Nonnull
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Class<T> type) throws NoSuchResourceException;

    /**
     * Try to resolve the resource. The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key    Key to lookup, such as 'sample.SampleModel.icon'
     * @param args   Arguments that will be filled in for params within the resource (params look like "{0}" within a
     *               resource, but this might differ between implementations), or {@code null} if none.
     * @param locale Locale in which to lookup
     * @param type   the type to be returned
     *
     * @return The resolved resource at the given key for the given locale
     *
     * @throws NoSuchResourceException if no resource is found
     * @since 2.5.0
     */
    @Nonnull
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException;

    @Nullable
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nullable T defaultValue, @Nonnull Class<T> type);

    @Nullable
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type);

    @Nullable
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nullable T defaultValue, @Nonnull Class<T> type);

    @Nullable
    @Override
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type);

    /**
     * Try to resolve the resource. Returns default value if no resource was found.
     * The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{0}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param defaultValue Message to return if the lookup fails
     * @param type         the type to be returned
     *
     * @return The resolved resource at the given key for the default locale
     *
     * @since 2.5.0
     */
    @Nullable
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nullable T defaultValue, @Nonnull Class<T> type);

    /**
     * Try to resolve the resource. Returns default value if no resource was found.
     * The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{0}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param locale       Locale in which to lookup
     * @param defaultValue Message to return if the lookup fails
     * @param type         the type to be returned
     *
     * @return The resolved resource at the given key for the given locale
     *
     * @since 2.5.0
     */
    @Nullable
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type);

    /**
     * Try to resolve the resource. The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key  Key to lookup, such as 'sample.SampleModel.icon'
     * @param args Arguments that will be filled in for params within the resource (params look like "{:key}"
     *             within a resource, but this might differ between implementations), or {@code null} if none.
     * @param type the type to be returned
     *
     * @return The resolved resource at the given key for the default locale
     *
     * @throws NoSuchResourceException if no resource is found
     * @since 2.5.0
     */
    @Nonnull
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Class<T> type) throws NoSuchResourceException;

    /**
     * Try to resolve the resource. The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key    Key to lookup, such as 'sample.SampleModel.icon'
     * @param args   Arguments that will be filled in for params within the resource (params look like "{:key}"
     *               within a resource, but this might differ between implementations), or {@code null} if none.
     * @param locale Locale in which to lookup
     * @param type   the type to be returned
     *
     * @return The resolved resource at the given key for the given locale
     *
     * @throws NoSuchResourceException if no resource is found
     * @since 2.5.0
     */
    @Nonnull
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException;

    /**
     * Try to resolve the resource. Returns default value if no resource was found.
     * The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{:key}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param defaultValue Message to return if the lookup fails
     * @param type         the type to be returned
     *
     * @return The resolved resource at the given key for the default locale
     *
     * @since 2.5.0
     */
    @Nullable
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable T defaultValue, @Nonnull Class<T> type);

    /**
     * Try to resolve the resource. Returns default value if no resource was found.
     * The value is converted to type <tt>T</tt> if found using a {@code Converter}.
     *
     * @param key          Key to lookup, such as 'sample.SampleModel.icon'
     * @param args         Arguments that will be filled in for params within the resource (params look like "{:key}"
     *                     within a resource, but this might differ between implementations), or {@code null} if none.
     * @param locale       Locale in which to lookup
     * @param defaultValue Message to return if the lookup fails
     * @param type         the type to be returned
     *
     * @return The resolved resource at the given key for the given locale
     *
     * @since 2.5.0
     */
    @Nullable
    <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type);

    /**
     * <p>Resolve a resource given a key and a Locale.</p>
     * <p>
     * This method should use the default Locale if the locale argument is null. The {@code key} argument may refer to
     * another key if the resolved value results in a {@code CharSequence} that begins with "@[" and ends with "]". In this
     * case the method will use the enclosed value as the next key to be resolved. For example, given the following key/value
     * definitions
     * <p/>
     * <pre>
     *     some.key = Hello {0}
     *     other.key = @[some.key]
     * </pre>
     * <p/>
     * Evaluating the keys results in
     * <p/>
     * <pre>
     *    assert resolveResourceValue('some.key', Locale.default) == 'Hello {0}'
     *    assert resolveResourceValue('other.key', Locale.default) == 'Hello {0}'
     * </pre>
     * <p/>
     * </p>
     *
     * @param key    Key to lookup, such as 'sample.SampleModel.icon'
     * @param locale Locale in which to lookup
     *
     * @return the resolved resource value at the given key for the given locale
     *
     * @throws NoSuchResourceException if no message is found
     */
    @Nonnull
    Object resolveResourceValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException;

    /**
     * Formats the given resource using supplied args to substitute placeholders.
     *
     * @param resource The resource following a predefined format.
     * @param args     Arguments that will be filled in for params within the resource (params look like "{0}"
     *                 within a resource, but this might differ between implementations), or {@code null} if none.
     *
     * @return the formatted resource with all matching placeholders with their substituted values.
     */
    @Nonnull
    String formatResource(@Nonnull String resource, @Nonnull List<?> args);

    @Nonnull
    @Override
    String formatResource(@Nonnull String resource, @Nonnull Object[] args);

    /**
     * Formats the given resource using supplied args to substitute placeholders.
     *
     * @param resource The resource following a predefined format.
     * @param args     Arguments that will be filled in for params within the resource (params look like "{:key}"
     *                 within a resource, but this might differ between implementations), or {@code null} if none.
     *
     * @return the formatted resource with all matching placeholders with their substituted values.
     */
    @Nonnull
    String formatResource(@Nonnull String resource, @Nonnull Map<String, Object> args);
}
