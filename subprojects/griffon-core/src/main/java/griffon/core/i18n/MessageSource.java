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
package griffon.core.i18n;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Interface for resolving messages, with support for the parameterization and internationalization of such messages.
 *
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 2.0.0
 */
public interface MessageSource extends javax.application.i18n.MessageSource {
    /**
     * "@["
     */
    String REF_KEY_START = "@[";

    /**
     * "]"
     */
    String REF_KEY_END = "]";

    @Nonnull
    String getMessage(@Nonnull String key) throws NoSuchMessageException;

    @Nonnull
    String getMessage(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException;

    @Nonnull
    String getMessage(@Nonnull String key, @Nonnull Object[] args) throws NoSuchMessageException;

    @Nonnull
    String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{0}" within a
     *             message, but this might differ between implementations), or null if none.
     *
     * @return The resolved message at the given key for the default locale
     *
     * @throws NoSuchMessageException if no message is found
     */
    @Nonnull
    String getMessage(@Nonnull String key, @Nonnull List<?> args) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key    Key to lookup, such as 'log4j.appenders.console'
     * @param args   Arguments that will be filled in for params within the message (params look like "{0}" within a
     *               message, but this might differ between implementations), or null if none.
     * @param locale Locale in which to lookup
     *
     * @return The resolved message at the given key for the given locale
     *
     * @throws NoSuchMessageException if no message is found
     */
    @Nonnull
    String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale) throws NoSuchMessageException;

    @Nullable
    String getMessage(@Nonnull String key, @Nullable String defaultMessage);

    @Nullable
    String getMessage(@Nonnull String key, @Nonnull Locale locale, @Nullable String defaultMessage);

    @Nullable
    String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nullable String defaultMessage);

    @Nullable
    String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable String defaultMessage);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{0}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     *
     * @return The resolved message at the given key for the default locale
     */
    @Nullable
    String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nullable String defaultMessage);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{0}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param locale         Locale in which to lookup
     * @param defaultMessage Message to return if the lookup fails
     *
     * @return The resolved message at the given key for the given locale
     */
    @Nullable
    String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable String defaultMessage);

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{:key}"
     *             within a message, but this might differ between implementations), or null if none.
     *
     * @return The resolved message at the given key for the default locale
     *
     * @throws NoSuchMessageException if no message is found
     */
    @Nonnull
    String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key    Key to lookup, such as 'log4j.appenders.console'
     * @param args   Arguments that will be filled in for params within the message (params look like "{:key}"
     *               within a message, but this might differ between implementations), or null if none.
     * @param locale Locale in which to lookup
     *
     * @return The resolved message at the given key for the given locale
     *
     * @throws NoSuchMessageException if no message is found
     */
    @Nonnull
    String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale) throws NoSuchMessageException;

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{:key}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     *
     * @return The resolved message at the given key for the default locale
     */
    @Nullable
    String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable String defaultMessage);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{:key}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param locale         Locale in which to lookup
     * @param defaultMessage Message to return if the lookup fails
     *
     * @return The resolved message at the given key for the given locale
     */
    @Nullable
    String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable String defaultMessage);

    /**
     * <p>Resolve a message given a key and a Locale.</p>
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
     *    assert resolveMessageValue('some.key', Locale.default) == 'Hello {0}'
     *    assert resolveMessageValue('other.key', Locale.default) == 'Hello {0}'
     * </pre>
     * <p/>
     * </p>
     *
     * @param key    Key to lookup, such as 'log4j.appenders.console'
     * @param locale Locale in which to lookup
     *
     * @return the resolved message value at the given key for the given locale
     *
     * @throws NoSuchMessageException if no message is found
     */
    @Nonnull
    Object resolveMessageValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException;

    /**
     * Formats the given message using supplied args to substitute placeholders.
     *
     * @param message The message following a predefined format.
     * @param args    Arguments that will be filled in for params within the message (params look like "{0}"
     *                within a message, but this might differ between implementations), or null if none.
     *
     * @return the formatted message with all matching placeholders with their substituted values.
     */
    @Nonnull
    String formatMessage(@Nonnull String message, @Nonnull List<?> args);

    @Nonnull
    String formatMessage(@Nonnull String message, @Nonnull Object[] args);

    /**
     * Formats the given message using supplied args to substitute placeholders.
     *
     * @param message The message following a predefined format.
     * @param args    Arguments that will be filled in for params within the message (params look like "{:key}"
     *                within a message, but this might differ between implementations), or null if none.
     *
     * @return the formatted message with all matching placeholders with their substituted values.
     */
    @Nonnull
    String formatMessage(@Nonnull String message, @Nonnull Map<String, Object> args);

    @Nonnull
    ResourceBundle asResourceBundle();
}
