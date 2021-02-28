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
package griffon.core.properties;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PropertyChangeEvent {
    private final PropertySource source;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;

    public PropertyChangeEvent(@Nonnull PropertySource source, @Nonnull String propertyName, @Nullable Object oldValue, @Nullable Object newValue) {
        this.source = requireNonNull(source, "Argument 'source' must not be null");
        this.propertyName = requireNonBlank(propertyName, "Argument 'propertyName' must not be blank");
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Nonnull
    public PropertySource getSource() {
        return source;
    }

    @Nonnull
    public String getPropertyName() {
        return propertyName;
    }

    @Nullable
    public Object getOldValue() {
        return oldValue;
    }

    @Nullable
    public Object getNewValue() {
        return newValue;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("[propertyName=").append(getPropertyName());
        sb.append("; oldValue=").append(getOldValue());
        sb.append("; newValue=").append(getNewValue());
        sb.append("; source=").append(getSource());
        return sb.append("]").toString();
    }
}
