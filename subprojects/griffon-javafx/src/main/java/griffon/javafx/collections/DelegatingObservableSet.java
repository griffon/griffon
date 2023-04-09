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
package griffon.javafx.collections;

import griffon.annotations.core.Nonnull;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.WeakSetChangeListener;

import java.util.Collection;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public abstract class DelegatingObservableSet<E> extends ObservableSetBase<E> implements ObservableSet<E> {
    private final ObservableSet<E> delegate;
    private SetChangeListener<E> sourceListener;

    public DelegatingObservableSet(@Nonnull ObservableSet<E> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
        this.delegate.addListener(new WeakSetChangeListener<>(getListener()));
    }

    @Nonnull
    protected ObservableSet<E> getDelegate() {
        return delegate;
    }

    private SetChangeListener<E> getListener() {
        if (sourceListener == null) {
            sourceListener = DelegatingObservableSet.this::sourceChanged;
        }
        return sourceListener;
    }

    protected abstract void sourceChanged(@Nonnull SetChangeListener.Change<? extends E> c);

    // --== Delegate methods ==--

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getDelegate().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return getDelegate().iterator();
    }

    @Override
    public Object[] toArray() {
        return getDelegate().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getDelegate().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return getDelegate().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return getDelegate().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getDelegate().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return getDelegate().addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getDelegate().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getDelegate().removeAll(c);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public boolean equals(Object o) {
        return getDelegate().equals(o);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }
}
