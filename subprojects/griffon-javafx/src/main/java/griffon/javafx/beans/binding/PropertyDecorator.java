/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.javafx.beans.binding;

import griffon.annotations.core.Nonnull;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class PropertyDecorator<T> implements Property<T> {
    private final Property<T> delegate;

    public PropertyDecorator(@Nonnull Property<T> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final Property<T> getDelegate() {
        return delegate;
    }

    @Override
    public void bind(ObservableValue<? extends T> observable) {
        delegate.bind(observable);
    }

    @Override
    public void unbind() {
        delegate.unbind();
    }

    @Override
    public boolean isBound() {
        return delegate.isBound();
    }

    @Override
    public void bindBidirectional(Property<T> other) {
        delegate.bindBidirectional(other);
    }

    @Override
    public void unbindBidirectional(Property<T> other) {
        delegate.unbindBidirectional(other);
    }

    @Override
    public Object getBean() {
        return delegate.getBean();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        delegate.removeListener(listener);
    }

    @Override
    public T getValue() {
        return delegate.getValue();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        delegate.removeListener(listener);
    }

    @Override
    public void setValue(T value) {
        delegate.setValue(value);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" + delegate.toString();
    }
}
