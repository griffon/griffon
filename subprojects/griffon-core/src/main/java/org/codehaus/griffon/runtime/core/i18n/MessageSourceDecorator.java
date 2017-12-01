/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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

import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class MessageSourceDecorator implements MessageSource {
    private final MessageSource delegate;

    public MessageSourceDecorator(@Nonnull MessageSource delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected MessageSource getDelegate() {
        return delegate;
    }

    @Nonnull
    public String getMessage(@Nonnull String key) throws NoSuchMessageException {
        return getDelegate().getMessage(key);
    }

    @Nonnull
    public String formatMessage(@Nonnull String message, @Nonnull Object[] args) {
        return getDelegate().formatMessage(message, args);
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, args, locale, defaultMessage);
    }

    @Nonnull
    public String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale) throws NoSuchMessageException {
        return getDelegate().getMessage(key, args, locale);
    }

    @Nonnull
    public String getMessage(@Nonnull String key, @Nonnull List<?> args) throws NoSuchMessageException {
        return getDelegate().getMessage(key, args);
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, args, defaultMessage);
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, args, locale, defaultMessage);
    }

    @Nonnull
    public String formatMessage(@Nonnull String message, @Nonnull List<?> args) {
        return getDelegate().formatMessage(message, args);
    }

    @Nonnull
    public String formatMessage(@Nonnull String message, @Nonnull Map<String, Object> args) {
        return getDelegate().formatMessage(message, args);
    }

    @Nonnull
    public String getMessage(@Nonnull String key, @Nonnull Object[] args) throws NoSuchMessageException {
        return getDelegate().getMessage(key, args);
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, defaultMessage);
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, args, defaultMessage);
    }

    @Nonnull
    public String getMessage(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException {
        return getDelegate().getMessage(key, locale);
    }

    @Nonnull
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchMessageException {
        return getDelegate().getMessage(key, args);
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nonnull Locale locale, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, locale, defaultMessage);
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nonnull Object[] args, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, args, defaultMessage);
    }

    @Nonnull
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale) throws NoSuchMessageException {
        return getDelegate().getMessage(key, args, locale);
    }

    @Nonnull
    public ResourceBundle asResourceBundle() {
        return getDelegate().asResourceBundle();
    }

    @Nullable
    public String getMessage(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable String defaultMessage) {
        return getDelegate().getMessage(key, args, locale, defaultMessage);
    }

    @Nonnull
    public String getMessage(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale) throws NoSuchMessageException {
        return getDelegate().getMessage(key, args, locale);
    }

    @Nonnull
    public Object resolveMessageValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException {
        return getDelegate().resolveMessageValue(key, locale);
    }
}
