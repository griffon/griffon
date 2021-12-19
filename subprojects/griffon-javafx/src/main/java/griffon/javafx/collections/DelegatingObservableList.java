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
package griffon.javafx.collections;

import griffon.annotations.core.Nonnull;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.6.0
 */
public abstract class DelegatingObservableList<E> extends ObservableListBase<E> implements ObservableList<E> {
    private final ObservableList<E> delegate;
    private ListChangeListener<E> sourceListener;

    public DelegatingObservableList(@Nonnull ObservableList<E> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
        this.delegate.addListener(new WeakListChangeListener<>(getListener()));
    }

    protected ObservableList<E> getDelegate() {
        return delegate;
    }

    private ListChangeListener<E> getListener() {
        if (sourceListener == null) {
            sourceListener = DelegatingObservableList.this::sourceChanged;
        }
        return sourceListener;
    }

    protected abstract void sourceChanged(@Nonnull ListChangeListener.Change<? extends E> c);

    // --== Delegate methods ==--

    public boolean removeAll(E... elements) {
        return getDelegate().removeAll(elements);
    }

    public void remove(int from, int to) {
        getDelegate().remove(from, to);
    }

    public E remove(int index) {
        return getDelegate().remove(index);
    }

    public int size() {
        return getDelegate().size();
    }

    public int lastIndexOf(Object o) {
        return getDelegate().lastIndexOf(o);
    }

    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    public boolean addAll(E... elements) {
        return getDelegate().addAll(elements);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return getDelegate().subList(fromIndex, toIndex);
    }

    public E set(int index, E element) {
        return getDelegate().set(index, element);
    }

    public void add(int index, E element) {
        getDelegate().add(index, element);
    }

    public boolean containsAll(Collection<?> c) {
        return getDelegate().containsAll(c);
    }

    public void clear() {
        getDelegate().clear();
    }

    public Iterator<E> iterator() {
        return getDelegate().iterator();
    }

    public boolean removeAll(Collection<?> c) {
        return getDelegate().removeAll(c);
    }

    public <T> T[] toArray(T[] a) {
        return getDelegate().toArray(a);
    }

    public boolean remove(Object o) {
        return getDelegate().remove(o);
    }

    public boolean addAll(Collection<? extends E> c) {
        return getDelegate().addAll(c);
    }

    public boolean retainAll(E... elements) {
        return getDelegate().retainAll(elements);
    }

    public boolean retainAll(Collection<?> c) {
        return getDelegate().retainAll(c);
    }

    public boolean contains(Object o) {
        return getDelegate().contains(o);
    }

    public boolean setAll(Collection<? extends E> col) {
        return getDelegate().setAll(col);
    }

    public ListIterator<E> listIterator(int index) {
        return getDelegate().listIterator(index);
    }

    public boolean add(E e) {
        return getDelegate().add(e);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return getDelegate().addAll(index, c);
    }

    public Object[] toArray() {
        return getDelegate().toArray();
    }

    public ListIterator<E> listIterator() {
        return getDelegate().listIterator();
    }

    public E get(int index) {
        return getDelegate().get(index);
    }

    public boolean setAll(E... elements) {
        return getDelegate().setAll(elements);
    }

    public int indexOf(Object o) {
        return getDelegate().indexOf(o);
    }
}