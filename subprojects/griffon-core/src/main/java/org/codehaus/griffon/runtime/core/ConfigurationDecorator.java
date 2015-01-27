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
package org.codehaus.griffon.runtime.core;

import griffon.core.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public abstract class ConfigurationDecorator implements Configuration {
    protected final Configuration delegate;

    public ConfigurationDecorator(@Nonnull Configuration delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null.");
    }

    @Override
    public boolean containsKey(@Nonnull String key) {
        return delegate.containsKey(key);
    }

    @Override
    @Nonnull
    public Map<String, Object> asFlatMap() {
        return delegate.asFlatMap();
    }

    @Override
    @Nonnull
    public ResourceBundle asResourceBundle() {
        return delegate.asResourceBundle();
    }

    @Override
    @Nonnull
    public Properties asProperties() {
        return delegate.asProperties();
    }

    @Override
    @Nullable
    public Object get(@Nonnull String key) {
        return delegate.get(key);
    }

    @Override
    @Nullable
    public <T> T get(@Nonnull String key, @Nullable T defaultValue) {
        return delegate.get(key, defaultValue);
    }

    @Override
    @Nullable
    public Object getAt(@Nonnull String key) {
        return delegate.getAt(key);
    }

    @Override
    @Nullable
    public <T> T getAt(@Nonnull String key, @Nullable T defaultValue) {
        return delegate.getAt(key, defaultValue);
    }

    @Override
    public boolean getAsBoolean(@Nonnull String key) {
        return delegate.getAsBoolean(key);
    }

    @Override
    public boolean getAsBoolean(@Nonnull String key, boolean defaultValue) {
        return delegate.getAsBoolean(key, defaultValue);
    }

    @Override
    public int getAsInt(@Nonnull String key) {
        return delegate.getAsInt(key);
    }

    @Override
    public int getAsInt(@Nonnull String key, int defaultValue) {
        return delegate.getAsInt(key, defaultValue);
    }

    @Override
    public long getAsLong(@Nonnull String key) {
        return delegate.getAsLong(key);
    }

    @Override
    public long getAsLong(@Nonnull String key, long defaultValue) {
        return delegate.getAsLong(key, defaultValue);
    }

    @Override
    public float getAsFloat(@Nonnull String key) {
        return delegate.getAsFloat(key);
    }

    @Override
    public float getAsFloat(@Nonnull String key, float defaultValue) {
        return delegate.getAsFloat(key, defaultValue);
    }

    @Override
    public double getAsDouble(@Nonnull String key) {
        return delegate.getAsDouble(key);
    }

    @Override
    public double getAsDouble(@Nonnull String key, double defaultValue) {
        return delegate.getAsDouble(key, defaultValue);
    }

    @Override
    @Nullable
    public String getAsString(@Nonnull String key) {
        return delegate.getAsString(key);
    }

    @Override
    @Nullable
    public String getAsString(@Nonnull String key, @Nullable String defaultValue) {
        return delegate.getAsString(key, defaultValue);
    }
}
