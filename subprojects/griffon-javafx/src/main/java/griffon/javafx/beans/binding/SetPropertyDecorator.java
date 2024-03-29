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
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.Collection;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class SetPropertyDecorator<E> extends SetProperty<E> {
    private final SetProperty<E> delegate;

    public SetPropertyDecorator(@Nonnull SetProperty<E> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final SetProperty<E> getDelegate() {
        return delegate;
    }

    @Override
    public void setValue(ObservableSet<E> v) {
        delegate.setValue(v);
    }

    @Override
    public void bindBidirectional(Property<ObservableSet<E>> other) {
        delegate.bindBidirectional(other);
    }

    @Override
    public void unbindBidirectional(Property<ObservableSet<E>> other) {
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
    public void bindContentBidirectional(ObservableSet<E> set) {
        delegate.bindContentBidirectional(set);
    }

    @Override
    public void unbindContentBidirectional(Object object) {
        delegate.unbindContentBidirectional(object);
    }

    @Override
    public void bindContent(ObservableSet<E> set) {
        delegate.bindContent(set);
    }

    @Override
    public void unbindContent(Object object) {
        delegate.unbindContent(object);
    }

    @Override
    public ObservableSet<E> getValue() {
        return delegate.getValue();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public ReadOnlyIntegerProperty sizeProperty() {
        return delegate.sizeProperty();
    }

    @Override
    public ReadOnlyBooleanProperty emptyProperty() {
        return delegate.emptyProperty();
    }

    @Override
    public BooleanBinding isEqualTo(ObservableSet<?> other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableSet<?> other) {
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
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object obj) {
        return delegate.contains(obj);
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return delegate.toArray(array);
    }

    @Override
    public boolean add(E element) {
        return delegate.add(element);
    }

    @Override
    public boolean remove(Object obj) {
        return delegate.remove(obj);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return delegate.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        return delegate.addAll(elements);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return delegate.removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return delegate.retainAll(objects);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public ObservableSet<E> get() {
        return delegate.get();
    }

    @Override
    public void addListener(ChangeListener<? super ObservableSet<E>> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super ObservableSet<E>> listener) {
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
    public void addListener(SetChangeListener<? super E> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(SetChangeListener<? super E> listener) {
        delegate.removeListener(listener);
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
    public void bind(ObservableValue<? extends ObservableSet<E>> observable) {
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
    public void set(ObservableSet<E> value) {
        delegate.set(value);
    }
}
