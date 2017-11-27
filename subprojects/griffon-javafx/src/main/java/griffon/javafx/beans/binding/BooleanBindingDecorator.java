/*
 * Copyright 2008-2017 the original author or authors.
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
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
public class BooleanBindingDecorator extends BooleanBinding {
    private final BooleanBinding delegate;

    public BooleanBindingDecorator(@Nonnull BooleanBinding delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final BooleanBinding getDelegate() {
        return delegate;
    }

    @Override
    protected boolean computeValue() {
        return delegate.get();
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }

    @Override
    public Boolean getValue() {
        return getDelegate().getValue();
    }

    @Override
    public BooleanBinding and(ObservableBooleanValue other) {
        return getDelegate().and(other);
    }

    @Override
    public BooleanBinding or(ObservableBooleanValue other) {
        return getDelegate().or(other);
    }

    @Override
    public BooleanBinding not() {
        return getDelegate().not();
    }

    @Override
    public BooleanBinding isEqualTo(ObservableBooleanValue other) {
        return getDelegate().isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableBooleanValue other) {
        return getDelegate().isNotEqualTo(other);
    }

    @Override
    public StringBinding asString() {
        return getDelegate().asString();
    }

    @Override
    public ObjectExpression<Boolean> asObject() {
        return getDelegate().asObject();
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
    public void addListener(ChangeListener<? super Boolean> listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Boolean> listener) {
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