/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class ObjectExpressionDecorator<T> extends ObjectExpression<T> {
    private final ObjectExpression<T> delegate;

    public ObjectExpressionDecorator(@Nonnull ObjectExpression<T> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final ObjectExpression<T> getDelegate() {
        return delegate;
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

    @Override
    public T getValue() {
        return delegate.getValue();
    }

    @Override
    public BooleanBinding isEqualTo(ObservableObjectValue<?> other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(Object other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableObjectValue<?> other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(Object other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNull() {
        return delegate.isNull();
    }

    @Override
    public BooleanBinding isNotNull() {
        return delegate.isNotNull();
    }

    @Override
    public StringBinding asString() {
        return delegate.asString();
    }

    @Override
    public StringBinding asString(String format) {
        return delegate.asString(format);
    }

    @Override
    public StringBinding asString(Locale locale, String format) {
        return delegate.asString(locale, format);
    }

    @Override
    public T get() {
        return delegate.get();
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
    public void addListener(InvalidationListener listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        delegate.removeListener(listener);
    }
}
