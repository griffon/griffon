/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.core.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Interface for resolving messages, with support for the parameterization and internationalization of such messages.
 *
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 1.1.0
 */
public interface MessageSource {
    /**
     * Try to resolve the message.
     *
     * @param key Key to lookup, such as 'log4j.appenders.console'
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key    Key to lookup, such as 'log4j.appenders.console'
     * @param locale Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key, Locale locale) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{0}" within a
     *             message, but this might differ between implementations), or null if none.
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key, Object[] args) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key    Key to lookup, such as 'log4j.appenders.console'
     * @param args   Arguments that will be filled in for params within the message (params look like "{0}" within a
     *               message, but this might differ between implementations), or null if none.
     * @param locale Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key, Object[] args, Locale locale) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{0}" within a
     *             message, but this might differ between implementations), or null if none.
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key, List args) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key    Key to lookup, such as 'log4j.appenders.console'
     * @param args   Arguments that will be filled in for params within the message (params look like "{0}" within a
     *               message, but this might differ between implementations), or null if none.
     * @param locale Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key, List args, Locale locale) throws NoSuchMessageException;

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param defaultMessage Message to return if the lookup fails
     * @return The resolved message at the given key for the default locale
     */
    String getMessage(String key, String defaultMessage);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param defaultMessage Message to return if the lookup fails
     * @param locale         Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     */
    String getMessage(String key, String defaultMessage, Locale locale);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{0}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     * @return The resolved message at the given key for the default locale
     */
    String getMessage(String key, Object[] args, String defaultMessage);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{0}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     * @param locale         Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     */
    String getMessage(String key, Object[] args, String defaultMessage, Locale locale);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{0}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     * @return The resolved message at the given key for the default locale
     */
    String getMessage(String key, List args, String defaultMessage);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{0}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     * @param locale         Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     */
    String getMessage(String key, List args, String defaultMessage, Locale locale);

    /**
     * Try to resolve the message.
     *
     * @param key  Key to lookup, such as 'log4j.appenders.console'
     * @param args Arguments that will be filled in for params within the message (params look like "{:key}"
     *             within a message, but this might differ between implementations), or null if none.
     * @return The resolved message at the given key for the default locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key, Map<String, Object> args) throws NoSuchMessageException;

    /**
     * Try to resolve the message.
     *
     * @param key    Key to lookup, such as 'log4j.appenders.console'
     * @param args   Arguments that will be filled in for params within the message (params look like "{:key}"
     *               within a message, but this might differ between implementations), or null if none.
     * @param locale Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     * @throws NoSuchMessageException if no message is found
     */
    String getMessage(String key, Map<String, Object> args, Locale locale) throws NoSuchMessageException;

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{:key}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     * @return The resolved message at the given key for the default locale
     */
    String getMessage(String key, Map<String, Object> args, String defaultMessage);

    /**
     * Try to resolve the message. Return default message if no message was found.
     *
     * @param key            Key to lookup, such as 'log4j.appenders.console'
     * @param args           Arguments that will be filled in for params within the message (params look like "{:key}"
     *                       within a message, but this might differ between implementations), or null if none.
     * @param defaultMessage Message to return if the lookup fails
     * @param locale         Locale in which to lookup
     * @return The resolved message at the given key for the given locale
     */
    String getMessage(String key, Map<String, Object> args, String defaultMessage, Locale locale);
}
