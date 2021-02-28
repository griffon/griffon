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
import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class ResourceResolverDecorator implements ResourceResolver {
    private final ResourceResolver delegate;

    public ResourceResolverDecorator(@Nonnull ResourceResolver delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected ResourceResolver getDelegate() {
        return delegate;
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key) throws NoSuchResourceException {
        return getDelegate().resolveResource(key);
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, locale);
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args);
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args, locale);
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args);
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args, locale);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, defaultValue);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nonnull Locale locale, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, locale, defaultValue);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, args, defaultValue);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, args, locale, defaultValue);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, args, defaultValue);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, args, locale, defaultValue);
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args);
    }

    @Override
    @Nonnull
    public <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args, locale);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, args, defaultValue);
    }

    @Override
    @Nullable
    public <T> T resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable T defaultValue) {
        return getDelegate().resolveResource(key, args, locale, defaultValue);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, locale, type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, args, type);
    }

    @Nonnull
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, args, locale, type);
    }

    @Override
    @Nonnull
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, args, type);
    }

    @Override
    @Nonnull
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, args, locale, type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, defaultValue, type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, locale, defaultValue, type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, args, defaultValue, type);
    }

    @Nullable
    @Override
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, args, locale, defaultValue, type);
    }

    @Override
    @Nullable
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, args, defaultValue, type);
    }

    @Override
    @Nullable
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, args, locale, defaultValue, type);
    }

    @Override
    @Nonnull
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, args, type);
    }

    @Override
    @Nonnull
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nonnull Class<T> type) throws NoSuchResourceException {
        return getDelegate().resolveResourceConverted(key, args, locale, type);
    }

    @Override
    @Nullable
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, args, defaultValue, type);
    }

    @Override
    @Nullable
    public <T> T resolveResourceConverted(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable T defaultValue, @Nonnull Class<T> type) {
        return getDelegate().resolveResourceConverted(key, args, locale, defaultValue, type);
    }

    @Override
    @Nonnull
    public Object resolveResourceValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResourceValue(key, locale);
    }

    @Override
    @Nonnull
    public String formatResource(@Nonnull String resource, @Nonnull List<?> args) {
        return getDelegate().formatResource(resource, args);
    }

    @Override
    @Nonnull
    public String formatResource(@Nonnull String resource, @Nonnull Object[] args) {
        return getDelegate().formatResource(resource, args);
    }

    @Override
    @Nonnull
    public String formatResource(@Nonnull String resource, @Nonnull Map<String, Object> args) {
        return getDelegate().formatResource(resource, args);
    }
}

