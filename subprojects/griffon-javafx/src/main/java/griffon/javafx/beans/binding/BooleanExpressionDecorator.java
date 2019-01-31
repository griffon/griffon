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
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class BooleanExpressionDecorator extends BooleanExpression {
    private final BooleanExpression delegate;

    public BooleanExpressionDecorator(@Nonnull BooleanExpression delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final BooleanExpression getDelegate() {
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
    public Boolean getValue() {
        return delegate.getValue();
    }

    @Override
    public BooleanBinding and(ObservableBooleanValue other) {
        return delegate.and(other);
    }

    @Override
    public BooleanBinding or(ObservableBooleanValue other) {
        return delegate.or(other);
    }

    @Override
    public BooleanBinding not() {
        return delegate.not();
    }

    @Override
    public BooleanBinding isEqualTo(ObservableBooleanValue other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableBooleanValue other) {
        return delegate.isNotEqualTo(other);
    }

    @Override
    public StringBinding asString() {
        return delegate.asString();
    }

    @Override
    public ObjectExpression<Boolean> asObject() {
        return delegate.asObject();
    }

    @Override
    public boolean get() {
        return delegate.get();
    }

    @Override
    public void addListener(ChangeListener<? super Boolean> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Boolean> listener) {
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
