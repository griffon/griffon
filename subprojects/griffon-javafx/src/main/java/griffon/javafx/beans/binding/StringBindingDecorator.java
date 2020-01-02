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
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.ObservableList;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
public class StringBindingDecorator extends StringBinding {
    private final StringBinding delegate;

    public StringBindingDecorator(@Nonnull StringBinding delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final StringBinding getDelegate() {
        return delegate;
    }

    @Override
    protected String computeValue() {
        return delegate.get();
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
    public void addListener(ChangeListener<? super String> listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super String> listener) {
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
    public String getValue() {
        return getDelegate().getValue();
    }

    @Override
    public StringExpression concat(Object other) {
        return getDelegate().concat(other);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableStringValue other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(String other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableStringValue other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(String other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualToIgnoreCase(ObservableStringValue other) {
        return getDelegate().isEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding isEqualToIgnoreCase(String other) {
        return getDelegate().isEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding isNotEqualToIgnoreCase(ObservableStringValue other) {
        return getDelegate().isNotEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding isNotEqualToIgnoreCase(String other) {
        return getDelegate().isNotEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding greaterThan(ObservableStringValue other) {
        return getDelegate().greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(String other) {
        return getDelegate().greaterThan(other);
    }

    @Override
    public BooleanBinding lessThan(ObservableStringValue other) {
        return getDelegate().lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(String other) {
        return getDelegate().lessThan(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(ObservableStringValue other) {
        return getDelegate().greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(String other) {
        return getDelegate().greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(ObservableStringValue other) {
        return getDelegate().lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(String other) {
        return getDelegate().lessThanOrEqualTo(other);
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
    public IntegerBinding length() {
        return getDelegate().length();
    }

    @Override
    public BooleanBinding isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public BooleanBinding isNotEmpty() {
        return getDelegate().isNotEmpty();
    }
}