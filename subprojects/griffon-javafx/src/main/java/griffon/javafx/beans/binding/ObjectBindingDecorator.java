/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
public class ObjectBindingDecorator<T> extends ObjectBinding<T> {
    private final ObjectBinding<T> delegate;

    public ObjectBindingDecorator(@Nonnull ObjectBinding<T> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final ObjectBinding<T> getDelegate() {
        return delegate;
    }

    @Override
    protected T computeValue() {
        return delegate.get();
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
        return getDelegate().getValue();
    }

    @Override
    public BooleanBinding isEqualTo(ObservableObjectValue other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(Object other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableObjectValue other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(Object other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNull() {
        return getDelegate().isNull();
    }

    @Override
    public BooleanBinding isNotNull() {
        return getDelegate().isNotNull();
    }

    @Override
    public StringBinding asString() {
        return getDelegate().asString();
    }

    @Override
    public StringBinding asString(String format) {
        return getDelegate().asString(format);
    }

    @Override
    public StringBinding asString(Locale locale, String format) {
        return getDelegate().asString(locale, format);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        getDelegate().removeListener(listener);
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        getDelegate().removeListener(listener);
    }

    @Override
    public void dispose() {
        getDelegate().dispose();
    }

    @Override
    public ObservableList<?> getDependencies() {
        return getDelegate().getDependencies();
    }
}