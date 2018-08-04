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
package griffon.core.properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class NamedPropertyChangeListener<T> implements PropertyChangeListener<T> {
    private final String propertyName;
    private final PropertyChangeListener<T> delegate;

    public NamedPropertyChangeListener(@Nonnull String propertyName, @Nonnull PropertyChangeListener<T> delegate) {
        this.propertyName = requireNonBlank(propertyName, "Argument 'propertyName' must not be blank");
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    public String getPropertyName() {
        return propertyName;
    }

    @Nonnull
    public PropertyChangeListener<T> getDelegate() {
        return delegate;
    }

    @Override
    public void propertyChange(@Nonnull PropertyChangeEvent<T> evt) {
        delegate.propertyChange(evt);
    }

    public static <T> boolean isWrappedBy(@Nullable PropertyChangeListener<T> listener, @Nullable NamedPropertyChangeListener<T> wrapper) {
        if (listener == null || wrapper == null) {
            return false;
        }

        return wrapper.delegate == listener;
    }
}
