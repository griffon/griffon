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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class ReadOnlyListPropertyDecorator<E> extends ReadOnlyListProperty<E> {
    private final ReadOnlyListProperty<E> delegate;

    public ReadOnlyListPropertyDecorator(@Nonnull ReadOnlyListProperty<E> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final ReadOnlyListProperty<E> getDelegate() {
        return delegate;
    }

    @Override
    public void bindContentBidirectional(ObservableList<E> list) {
        delegate.bindContentBidirectional(list);
    }

    @Override
    public void unbindContentBidirectional(Object object) {
        delegate.unbindContentBidirectional(object);
    }

    @Override
    public void bindContent(ObservableList<E> list) {
        delegate.bindContent(list);
    }

    @Override
    public void unbindContent(Object object) {
        delegate.unbindContent(object);
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
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
    public ObservableList<E> getValue() {
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
    public ObjectBinding<E> valueAt(int index) {
        return delegate.valueAt(index);
    }

    @Override
    public ObjectBinding<E> valueAt(ObservableIntegerValue index) {
        return delegate.valueAt(index);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableList<?> other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableList<?> other) {
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
    public boolean addAll(int i, Collection<? extends E> elements) {
        return delegate.addAll(i, elements);
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
    public E get(int i) {
        return delegate.get(i);
    }

    @Override
    public E set(int i, E element) {
        return delegate.set(i, element);
    }

    @Override
    public void add(int i, E element) {
        delegate.add(i, element);
    }

    @Override
    public E remove(int i) {
        return delegate.remove(i);
    }

    @Override
    public int indexOf(Object obj) {
        return delegate.indexOf(obj);
    }

    @Override
    public int lastIndexOf(Object obj) {
        return delegate.lastIndexOf(obj);
    }

    @Override
    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return delegate.listIterator(i);
    }

    @Override
    public List<E> subList(int from, int to) {
        return delegate.subList(from, to);
    }

    @Override
    public boolean addAll(E... elements) {
        return delegate.addAll(elements);
    }

    @Override
    public boolean setAll(E... elements) {
        return delegate.setAll(elements);
    }

    @Override
    public boolean setAll(Collection<? extends E> elements) {
        return delegate.setAll(elements);
    }

    @Override
    public boolean removeAll(E... elements) {
        return delegate.removeAll(elements);
    }

    @Override
    public boolean retainAll(E... elements) {
        return delegate.retainAll(elements);
    }

    @Override
    public void remove(int from, int to) {
        delegate.remove(from, to);
    }

    @Override
    public ObservableList<E> get() {
        return delegate.get();
    }

    @Override
    public void addListener(ChangeListener<? super ObservableList<E>> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super ObservableList<E>> listener) {
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
    public void addListener(ListChangeListener<? super E> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ListChangeListener<? super E> listener) {
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
}



