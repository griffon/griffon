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
package griffon.javafx.beans.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;

import javax.annotation.Nonnull;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class IntegerPropertyDecorator extends IntegerProperty {
    private final IntegerProperty delegate;

    public IntegerPropertyDecorator(@Nonnull IntegerProperty delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final IntegerProperty getDelegate() {
        return delegate;
    }

    @Override
    public void setValue(Number v) {
        delegate.setValue(v);
    }

    @Override
    public void bindBidirectional(Property<Number> other) {
        delegate.bindBidirectional(other);
    }

    @Override
    public void unbindBidirectional(Property<Number> other) {
        delegate.unbindBidirectional(other);
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
    public ObjectProperty<Integer> asObject() {
        return delegate.asObject();
    }

    @Override
    public int intValue() {
        return delegate.intValue();
    }

    @Override
    public long longValue() {
        return delegate.longValue();
    }

    @Override
    public float floatValue() {
        return delegate.floatValue();
    }

    @Override
    public double doubleValue() {
        return delegate.doubleValue();
    }

    @Override
    public Integer getValue() {
        return delegate.getValue();
    }

    @Override
    public IntegerBinding negate() {
        return delegate.negate();
    }

    @Override
    public DoubleBinding add(double other) {
        return delegate.add(other);
    }

    @Override
    public FloatBinding add(float other) {
        return delegate.add(other);
    }

    @Override
    public LongBinding add(long other) {
        return delegate.add(other);
    }

    @Override
    public IntegerBinding add(int other) {
        return delegate.add(other);
    }

    @Override
    public DoubleBinding subtract(double other) {
        return delegate.subtract(other);
    }

    @Override
    public FloatBinding subtract(float other) {
        return delegate.subtract(other);
    }

    @Override
    public LongBinding subtract(long other) {
        return delegate.subtract(other);
    }

    @Override
    public IntegerBinding subtract(int other) {
        return delegate.subtract(other);
    }

    @Override
    public DoubleBinding multiply(double other) {
        return delegate.multiply(other);
    }

    @Override
    public FloatBinding multiply(float other) {
        return delegate.multiply(other);
    }

    @Override
    public LongBinding multiply(long other) {
        return delegate.multiply(other);
    }

    @Override
    public IntegerBinding multiply(int other) {
        return delegate.multiply(other);
    }

    @Override
    public DoubleBinding divide(double other) {
        return delegate.divide(other);
    }

    @Override
    public FloatBinding divide(float other) {
        return delegate.divide(other);
    }

    @Override
    public LongBinding divide(long other) {
        return delegate.divide(other);
    }

    @Override
    public IntegerBinding divide(int other) {
        return delegate.divide(other);
    }

    @Override
    public NumberBinding add(ObservableNumberValue other) {
        return delegate.add(other);
    }

    @Override
    public NumberBinding subtract(ObservableNumberValue other) {
        return delegate.subtract(other);
    }

    @Override
    public NumberBinding multiply(ObservableNumberValue other) {
        return delegate.multiply(other);
    }

    @Override
    public NumberBinding divide(ObservableNumberValue other) {
        return delegate.divide(other);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableNumberValue other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableNumberValue other, double epsilon) {
        return delegate.isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(double other, double epsilon) {
        return delegate.isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(float other, double epsilon) {
        return delegate.isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(long other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(long other, double epsilon) {
        return delegate.isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isEqualTo(int other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isEqualTo(int other, double epsilon) {
        return delegate.isEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableNumberValue other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableNumberValue other, double epsilon) {
        return delegate.isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(double other, double epsilon) {
        return delegate.isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(float other, double epsilon) {
        return delegate.isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(long other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(long other, double epsilon) {
        return delegate.isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding isNotEqualTo(int other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(int other, double epsilon) {
        return delegate.isNotEqualTo(other, epsilon);
    }

    @Override
    public BooleanBinding greaterThan(ObservableNumberValue other) {
        return delegate.greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(double other) {
        return delegate.greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(float other) {
        return delegate.greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(long other) {
        return delegate.greaterThan(other);
    }

    @Override
    public BooleanBinding greaterThan(int other) {
        return delegate.greaterThan(other);
    }

    @Override
    public BooleanBinding lessThan(ObservableNumberValue other) {
        return delegate.lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(double other) {
        return delegate.lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(float other) {
        return delegate.lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(long other) {
        return delegate.lessThan(other);
    }

    @Override
    public BooleanBinding lessThan(int other) {
        return delegate.lessThan(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(ObservableNumberValue other) {
        return delegate.greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(double other) {
        return delegate.greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(float other) {
        return delegate.greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(long other) {
        return delegate.greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding greaterThanOrEqualTo(int other) {
        return delegate.greaterThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(ObservableNumberValue other) {
        return delegate.lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(double other) {
        return delegate.lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(float other) {
        return delegate.lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(long other) {
        return delegate.lessThanOrEqualTo(other);
    }

    @Override
    public BooleanBinding lessThanOrEqualTo(int other) {
        return delegate.lessThanOrEqualTo(other);
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
    public void addListener(ChangeListener<? super Number> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
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

    @Override
    public int get() {
        return delegate.get();
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
    public void bind(ObservableValue<? extends Number> observable) {
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
    public void set(int value) {
        delegate.set(value);
    }
}
