/*
 * Copyright 2008-2015 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public Object resolveResource(@Nonnull String key) throws NoSuchResourceException {
        return getDelegate().resolveResource(key);
    }

    @Override
    @Nonnull
    public Object resolveResource(@Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, locale);
    }

    @Override
    @Nonnull
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args);
    }

    @Override
    @Nonnull
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args, locale);
    }

    @Override
    @Nonnull
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args);
    }

    @Override
    @Nonnull
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args, locale);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, defaultValue);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nonnull Locale locale, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, locale, defaultValue);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, args, defaultValue);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nonnull Object[] args, @Nonnull Locale locale, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, args, locale, defaultValue);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, args, defaultValue);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nonnull List<?> args, @Nonnull Locale locale, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, args, locale, defaultValue);
    }

    @Override
    @Nonnull
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args);
    }

    @Override
    @Nonnull
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale) throws NoSuchResourceException {
        return getDelegate().resolveResource(key, args, locale);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, args, defaultValue);
    }

    @Override
    @Nullable
    public Object resolveResource(@Nonnull String key, @Nonnull Map<String, Object> args, @Nonnull Locale locale, @Nullable Object defaultValue) {
        return getDelegate().resolveResource(key, args, locale, defaultValue);
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

