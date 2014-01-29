/*
 * Copyright 2009-2014 the original author or authors.
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

package griffon.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface Configuration {
    boolean containsKey(@Nonnull String key);

    @Nonnull
    Map<String, Object> asFlatMap();

    @Nullable
    Object get(@Nonnull String key);

    @Nullable
    <T> T get(@Nonnull String key, @Nullable T defaultValue);

    @Nullable
    Object getAt(@Nonnull String key);

    @Nullable
    <T> T getAt(@Nonnull String key, @Nullable T defaultValue);

    boolean getAsBoolean(@Nonnull String key);

    boolean getAsBoolean(@Nonnull String key, boolean defaultValue);

    int getAsInt(@Nonnull String key);

    int getAsInt(@Nonnull String key, int defaultValue);

    long getAsLong(@Nonnull String key);

    long getAsLong(@Nonnull String key, long defaultValue);

    float getAsFloat(@Nonnull String key);

    float getAsFloat(@Nonnull String key, float defaultValue);

    double getAsDouble(@Nonnull String key);

    double getAsDouble(@Nonnull String key, double defaultValue);

    @Nullable
    String getAsString(@Nonnull String key);

    @Nullable
    String getAsString(@Nonnull String key, @Nullable String defaultValue);
}
