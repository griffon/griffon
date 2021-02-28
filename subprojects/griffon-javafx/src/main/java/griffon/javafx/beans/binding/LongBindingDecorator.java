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
package griffon.javafx.beans.binding;

import griffon.annotations.core.Nonnull;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.ObservableList;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
public class LongBindingDecorator extends LongBinding {
    private final LongBinding delegate;

    public LongBindingDecorator(@Nonnull LongBinding delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final LongBinding getDelegate() {
        return delegate;
    }

    @Override
    protected long computeValue() {
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
    public int intValue() {
        return getDelegate().intValue();
    }

    @Override
    public long longValue() {
        return getDelegate().longValue();
    }

    @Override
    public float floatValue() {
        return getDelegate().floatValue();
    }

    @Override
    public double doubleValue() {
        return getDelegate().doubleValue();
    }

    @Override
    public Long getValue() {
        return getDelegate().getValue();
    }

    @Override
    public LongBinding negate() {
        return getDelegate().negate();
    }

    @Override
    public DoubleBinding add(double other) {
        return getDelegate().add(other);
    }

    @Override
    public FloatBinding add(float other) {
        return getDelegate().add(other);
    }

    @Override
    public LongBinding add(long other) {
        return getDelegate().add(other);
    }

    @Override
    public LongBinding add(int other) {
        return getDelegate().add(other);
    }

    @Override
    public DoubleBinding subtract(double other) {
        return getDelegate().subtract(other);
    }

    @Override
    public FloatBinding subtract(float other) {
        return getDelegate().subtract(other);
    }

    @Override
    public LongBinding subtract(long other) {
        return getDelegate().subtract(other);
    }

    @Override
    public LongBinding subtract(int other) {
        return getDelegate().subtract(other);
    }

    @Override
    public DoubleBinding multiply(double other) {
        return getDelegate().multiply(other);
    }

    @Override
    public FloatBinding multiply(float other) {
        return getDelegate().multiply(other);
    }

    @Override
    public LongBinding multiply(long other) {
        return getDelegate().multiply(other);
    }

    @Override
    public LongBinding multiply(int other) {
        return getDelegate().multiply(other);
    }

    @Override
    public DoubleBinding divide(double other) {
        return getDelegate().divide(other);
    }

    @Override
    public FloatBinding divide(float other) {
        return getDelegate().divide(other);
    }

    @Override
    public LongBinding divide(long other) {
        return getDelegate().divide(other);
    }

    @Override
    public LongBinding divide(int other) {
        return getDelegate().divide(other);
    }

    @Override
    public ObjectExpression<Long> asObject() {
        return getDelegate().asObject();
    }

    @Override
    public NumberBinding add(ObservableNumberValue other) {
        return getDelegate().add(other);
    }

    @Override
    public NumberBinding subtract(ObservableNumberValue other) {
        return getDelegate().subtract(other);
    }

    @Override
    public NumberBinding multiply(ObservableNumberValue other) {
        return getDelegate().multiply(other);
    }

    @Override
    public NumberBinding divide(ObservableNumberValue other) {
        return getDelegate().divide(other);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableNumberValue other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableNumberValue other, double epsilon) {
        return getDelegate().isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(double other, double epsilon) {
        return getDelegate().isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(float other, double epsilon) {
        return getDelegate().isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(long other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(long other, double epsilon) {
        return getDelegate().isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(int other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(int other, double epsilon) {
        return getDelegate().isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableNumberValue other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableNumberValue other, double epsilon) {
        return getDelegate().isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(double other, double epsilon) {
        return getDelegate().isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(float other, double epsilon) {
        return getDelegate().isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(long other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(long other, double epsilon) {
        return getDelegate().isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(int other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(int other, double epsilon) {
        return getDelegate().isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding greaterThan(ObservableNumberValue other) {
        return getDelegate().greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(double other) {
        return getDelegate().greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(float other) {
        return getDelegate().greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(long other) {
        return getDelegate().greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(int other) {
        return getDelegate().greaterThan(other);
    }

    @Override
    public BooleanBinding lessThan(ObservableNumberValue other) {
        return getDelegate().lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(double other) {
        return getDelegate().lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(float other) {
        return getDelegate().lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(long other) {
        return getDelegate().lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(int other) {
        return getDelegate().lessThan(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(ObservableNumberValue other) {
        return getDelegate().greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(double other) {
        return getDelegate().greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(float other) {
        return getDelegate().greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(long other) {
        return getDelegate().greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(int other) {
        return getDelegate().greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(ObservableNumberValue other) {
        return getDelegate().lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(double other) {
        return getDelegate().lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(float other) {
        return getDelegate().lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(long other) {
        return getDelegate().lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(int other) {
        return getDelegate().lessThanOrEqualTo(other);
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
    public void addListener(ChangeListener<? super Number> listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
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