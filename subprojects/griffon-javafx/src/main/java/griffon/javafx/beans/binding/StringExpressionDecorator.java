/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class StringExpressionDecorator extends StringExpression {
    private final StringExpression delegate;

    public StringExpressionDecorator(@Nonnull StringExpression delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final StringExpression getDelegate() {
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
    public String getValue() {
        return delegate.getValue();
    }

    @Override
    public StringExpression concat(Object other) {
        return delegate.concat(other);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableStringValue other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(String other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableStringValue other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(String other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualToIgnoreCase(ObservableStringValue other) {
        return delegate.isEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding isEqualToIgnoreCase(String other) {
        return delegate.isEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding isNotEqualToIgnoreCase(ObservableStringValue other) {
        return delegate.isNotEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding isNotEqualToIgnoreCase(String other) {
        return delegate.isNotEqualToIgnoreCase(other);
    }

    @Override
    public BooleanBinding greaterThan(ObservableStringValue other) {
        return delegate.greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(String other) {
        return delegate.greaterThan(other);
    }

    @Override
    public BooleanBinding lessThan(ObservableStringValue other) {
        return delegate.lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(String other) {
        return delegate.lessThan(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(ObservableStringValue other) {
        return delegate.greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(String other) {
        return delegate.greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(ObservableStringValue other) {
        return delegate.lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(String other) {
        return delegate.lessThanOrEqualTo(other);
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
    public IntegerBinding length() {
        return delegate.length();
    }

    @Override
    public BooleanBinding isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public BooleanBinding isNotEmpty() {
        return delegate.isNotEmpty();
    }

    @Override
    public String get() {
        return delegate.get();
    }

    @Override
    public void addListener(ChangeListener<? super String> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super String> listener) {
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
