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
package org.codehaus.griffon.runtime.core.i18n;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.CallableWithArgs;
import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 2.0.0
 */
public abstract class AbstractMessageSource implements MessageSource {
    protected static final String ERROR_KEY_BLANK = "Argument 'key' must not be blank";
    protected static final String ERROR_LOCALE_NULL = "Argument 'locale' must not be null";
    protected static final String ERROR_ARGS_NULL = "Argument 'args' must not be null";
    protected static final String ERROR_MESSAGE_NULL = "Argument 'message' must not be null";

    protected static final Object[] EMPTY_OBJECT_ARGS = new Object[0];

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key) throws NoSuchMessageException {
        return getMessage(key, EMPTY_OBJECT_ARGS, Locale.getDefault());
    }

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException {
        return getMessage(key, EMPTY_OBJECT_ARGS, locale);
    }

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Object[] args) throws NoSuchMessageException {
        return getMessage(key, args, Locale.getDefault());
    }

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale) throws NoSuchMessageException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(args, ERROR_ARGS_NULL);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        Object message = resolveMessageValue(key, locale);
        Object result = evalMessageWithArguments(message, args);
        if (result != null) return result.toString();
        throw new NoSuchMessageException(key, locale);
    }

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key, @Nonnull List<?> args) throws NoSuchMessageException {
        return getMessage(key, toObjectArray(args), Locale.getDefault());
    }

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale) throws NoSuchMessageException {
        return getMessage(key, toObjectArray(args), locale);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nullable String defaultMessage) {
        return getMessage(key, EMPTY_OBJECT_ARGS, Locale.getDefault(), defaultMessage);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Locale locale, @Nullable String defaultMessage) {
        return getMessage(key, EMPTY_OBJECT_ARGS, locale, defaultMessage);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nullable String defaultMessage) {
        return getMessage(key, args, Locale.getDefault(), defaultMessage);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable String defaultMessage) {
        try {
            return getMessage(key, args, locale);
        } catch (NoSuchMessageException nsme) {
            return null == defaultMessage ? key : defaultMessage;
        }
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nullable String defaultMessage) {
        return getMessage(key, toObjectArray(args), Locale.getDefault(), defaultMessage);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable String defaultMessage) {
        return getMessage(key, toObjectArray(args), locale, defaultMessage);
    }

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchMessageException {
        return getMessage(key, args, Locale.getDefault());
    }

    @Nonnull
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale) throws NoSuchMessageException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(args, ERROR_ARGS_NULL);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        Object message = resolveMessageValue(key, locale);
        Object result = evalMessageWithArguments(message, args);
        if (result != null) return result.toString();
        throw new NoSuchMessageException(key, locale);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable String defaultMessage) {
        return getMessage(key, args, Locale.getDefault(), defaultMessage);
    }

    @Nullable
    @Override
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable String defaultMessage) {
        try {
            return getMessage(key, args, locale);
        } catch (NoSuchMessageException nsme) {
            return null == defaultMessage ? key : defaultMessage;
        }
    }

    @Nonnull
    @Override
    public Object resolveMessageValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        try {
            Object message = doResolveMessageValue(key, locale);
            if (message instanceof CharSequence) {
                String msg = message.toString();
                if (msg.length() >= 4 && msg.startsWith(REF_KEY_START) && msg.endsWith(REF_KEY_END)) {
                    String refKey = msg.substring(2, msg.length() - 1);
                    message = resolveMessageValue(refKey, locale);
                }
            }
            return message;
        } catch (MissingResourceException mre) {
            throw new NoSuchMessageException(key, locale);
        }
    }

    @Nonnull
    @Override
    public String formatMessage(@Nonnull String message, @Nonnull List<?> args) {
        requireNonNull(message, ERROR_MESSAGE_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);
        return formatMessage(message, args.toArray(new Object[args.size()]));
    }

    @Nonnull
    @Override
    public String formatMessage(@Nonnull String message, @Nonnull Object[] args) {
        requireNonNull(message, ERROR_MESSAGE_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);
        if (args.length == 0) return message;
        return MessageFormat.format(message, args);
    }

    @Nonnull
    @Override
    public String formatMessage(@Nonnull String message, @Nonnull Map<String, Object> args) {
        requireNonNull(message, ERROR_MESSAGE_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);
        for (Map.Entry<String, Object> variable : args.entrySet()) {
            String var = variable.getKey();
            String value = variable.getValue() != null ? variable.getValue().toString() : null;
            if (value != null) {
                message = message.replace("{:" + var + "}", value);
            }
        }
        return message;
    }

    @Nonnull
    protected abstract Object doResolveMessageValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException;

    @Nullable
    protected Object evalMessageWithArguments(@Nonnull Object message, @Nonnull Object[] args) {
        if (message instanceof CallableWithArgs) {
            CallableWithArgs<?> callable = (CallableWithArgs<?>) message;
            return callable.call(args);
        } else if (message instanceof CharSequence) {
            return formatMessage(String.valueOf(message), args);
        }
        return null;
    }

    @Nullable
    protected Object evalMessageWithArguments(@Nonnull Object message, @Nonnull Map<String, Object> args) {
        if (message instanceof CallableWithArgs) {
            CallableWithArgs<?> callable = (CallableWithArgs<?>) message;
            return callable.call(args);
        } else if (message instanceof CharSequence) {
            return formatMessage(String.valueOf(message), args);
        }
        return null;
    }

    @Nonnull
    protected Object[] toObjectArray(@Nonnull List<?> args) {
        if (args.isEmpty()) {
            return EMPTY_OBJECT_ARGS;
        }
        return args.toArray(new Object[args.size()]);
    }
}
