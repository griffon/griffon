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
import javafx.beans.binding.Binding;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
public class BindingDecorator<T> implements Binding<T> {
    private final Binding<T> delegate;

    public BindingDecorator(@Nonnull Binding<T> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final Binding<T> getDelegate() {
        return delegate;
    }

    @Override
    public boolean isValid() {
        return getDelegate().isValid();
    }

    @Override
    public void invalidate() {
        getDelegate().invalidate();
    }

    @Override
    public ObservableList<?> getDependencies() {
        return getDelegate().getDependencies();
    }

    @Override
    public void dispose() {
        getDelegate().dispose();
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
    public T getValue() {
        return getDelegate().getValue();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        getDelegate().removeListener(listener);
    }
}
