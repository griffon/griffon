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
package org.codehaus.griffon.runtime.core.resources;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.CallableWithArgs;
import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;

import java.beans.PropertyEditor;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import static griffon.core.editors.PropertyEditorResolver.findEditor;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractResourceResolver implements ResourceResolver {
    protected static final String ERROR_KEY_BLANK = "Argument 'key' must not be blank";
    protected static final String ERROR_LOCALE_NULL = "Argument 'locale' must not be null";
    protected static final String ERROR_ARGS_NULL = "Argument 'args' must not be null";
    protected static final String ERROR_RESOURCE_NULL = "Argument 'resource' must not be null";

    protected static final Object[] EMPTY_OBJECT_ARGS = new Object[0];

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key) throws NoSuchResourceException {
        return resolveResource(key, EMPTY_OBJECT_ARGS, Locale.getDefault());
    }

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
        return resolveResource(key, EMPTY_OBJECT_ARGS, locale);
    }

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args) throws NoSuchResourceException {
        return resolveResource(key, args, Locale.getDefault());
    }

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale) throws NoSuchResourceException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(args, ERROR_ARGS_NULL);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        Object resource = resolveResourceValue(key, locale);
        Object result = evalResourceWithArguments(resource, args);
        if (result != null) return result;
        throw new NoSuchResourceException(key, locale);
    }

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args) throws NoSuchResourceException {
        return resolveResource(key, toObjectArray(args), Locale.getDefault());
    }

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale) throws NoSuchResourceException {
        return resolveResource(key, toObjectArray(args), locale);
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nullable Object defaultValue) {
        return resolveResource(key, EMPTY_OBJECT_ARGS, Locale.getDefault(), defaultValue);
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Locale locale, @Nullable Object defaultValue) {
        return resolveResource(key, EMPTY_OBJECT_ARGS, locale, defaultValue);
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nullable Object defaultValue) {
        return resolveResource(key, args, Locale.getDefault(), defaultValue);
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable Object defaultValue) {
        try {
            return resolveResource(key, args, locale);
        } catch (NoSuchResourceException nsre) {
            return null == defaultValue ? key : defaultValue;
        }
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nullable Object defaultValue) {
        return resolveResource(key, toObjectArray(args), Locale.getDefault(), defaultValue);
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable Object defaultValue) {
        return resolveResource(key, toObjectArray(args), locale, defaultValue);
    }

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchResourceException {
        return resolveResource(key, args, Locale.getDefault());
    }

    @Nonnull
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale) throws NoSuchResourceException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(args, ERROR_ARGS_NULL);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        Object resource = resolveResourceValue(key, locale);
        Object result = evalResourceWithArguments(resource, args);
        if (result != null) return result;
        throw new NoSuchResourceException(key, locale);
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable Object defaultValue) {
        return resolveResource(key, args, Locale.getDefault(), defaultValue);
    }

    @Nullable
    @Override
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable Object defaultValue) {
        try {
            return resolveResource(key, args, locale);
        } catch (NoSuchResourceException nsre) {
            return null == defaultValue ? key : defaultValue;
        }
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, args, defaultValue), type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, args, locale, defaultValue), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key, args, locale), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key, args), type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, args, defaultValue), type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, args, locale, defaultValue), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key, args, locale), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key, args), type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, args, defaultValue), type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, args, locale, defaultValue), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key, args, locale), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key, args), type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, defaultValue), type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return convertValue(resolveResource(key, locale, defaultValue), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key, locale), type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Class<T> type) throws NoSuchResourceException {
        return convertValue(resolveResource(key), type);
    }

    @Nonnull
    @Override
    public Object resolveResourceValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        try {
            Object resource = doResolveResourceValue(key, locale);
            if (resource instanceof CharSequence) {
                String msg = resource.toString();
                if (msg.length() >= 4 && msg.startsWith(REF_KEY_START) && msg.endsWith(REF_KEY_END)) {
                    String refKey = msg.substring(2, msg.length() - 1);
                    resource = resolveResourceValue(refKey, locale);
                }
            }
            return resource;
        } catch (MissingResourceException mre) {
            throw new NoSuchResourceException(key, locale);
        }
    }

    @Nonnull
    @Override
    public String formatResource(@Nonnull String resource, @Nonnull List<?> args) {
        requireNonNull(resource, ERROR_RESOURCE_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);
        return formatResource(resource, args.toArray(new Object[args.size()]));
    }

    @Nonnull
    @Override
    public String formatResource(@Nonnull String resource, @Nonnull Object[] args) {
        requireNonNull(resource, ERROR_RESOURCE_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);
        if (args.length == 0) return resource;
        return MessageFormat.format(resource, args);
    }

    @Nonnull
    @Override
    public String formatResource(@Nonnull String resource, @Nonnull Map<String, Object> args) {
        requireNonNull(resource, ERROR_RESOURCE_NULL);
        requireNonNull(args, ERROR_ARGS_NULL);
        for (Map.Entry<String, Object> variable : args.entrySet()) {
            String var = variable.getKey();
            String value = variable.getValue() != null ? variable.getValue().toString() : null;
            if (value != null) {
                resource = resource.replace("{:" + var + "}", value);
            }
        }
        return resource;
    }

    @Nonnull
    protected abstract Object doResolveResourceValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException;

    @Nullable
    protected Object evalResourceWithArguments(@Nonnull Object resource, @Nonnull Object[] args) {
        if (resource instanceof CallableWithArgs) {
            CallableWithArgs<?> callable = (CallableWithArgs<?>) resource;
            return callable.call(args);
        } else if (resource instanceof CharSequence) {
            return formatResource(String.valueOf(resource), args);
        }
        return resource;
    }

    @Nullable
    protected Object evalResourceWithArguments(@Nonnull Object resource, @Nonnull Map<String, Object> args) {
        if (resource instanceof CallableWithArgs) {
            CallableWithArgs<?> callable = (CallableWithArgs<?>) resource;
            return callable.call(args);
        } else if (resource instanceof CharSequence) {
            return formatResource(String.valueOf(resource), args);
        }
        return resource;
    }

    @Nonnull
    protected Object[] toObjectArray(@Nonnull List<?> args) {
        if (args.isEmpty()) {
            return EMPTY_OBJECT_ARGS;
        }
        return args.toArray(new Object[args.size()]);
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertValue(@Nullable Object value, @Nonnull Class<T> type) {
        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                return (T) value;
            } else {
                PropertyEditor editor = findEditor(type);
                editor.setValue(value);
                return (T) editor.getValue();
            }
        }
        return null;
    }
}
