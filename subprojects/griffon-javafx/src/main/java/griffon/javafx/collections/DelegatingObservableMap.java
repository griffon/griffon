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
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.WeakMapChangeListener;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public abstract class DelegatingObservableMap<K, V> extends ObservableMapBase<K, V> implements ObservableMap<K, V> {
    private final ObservableMap<K, V> delegate;
    private MapChangeListener<K, V> sourceListener;

    public DelegatingObservableMap(@Nonnull ObservableMap<K, V> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
        this.delegate.addListener(new WeakMapChangeListener<>(getListener()));
    }

    @Nonnull
    protected ObservableMap<K, V> getDelegate() {
        return delegate;
    }

    private MapChangeListener<K, V> getListener() {
        if (sourceListener == null) {
            sourceListener = DelegatingObservableMap.this::sourceChanged;
        }
        return sourceListener;
    }

    protected abstract void sourceChanged(@Nonnull MapChangeListener.Change<? extends K, ? extends V> c);

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
    public boolean containsValue(Object value) {
        return getDelegate().containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return getDelegate().containsKey(key);
    }

    @Override
    public V get(Object key) {
        return getDelegate().get(key);
    }

    @Override
    public V put(K key, V value) {
        return getDelegate().put(key, value);
    }

    @Override
    public V remove(Object key) {
        return getDelegate().remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        getDelegate().putAll(m);
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

    @Override
    public Set<K> keySet() {
        return getDelegate().keySet();
    }

    @Override
    public Collection<V> values() {
        return getDelegate().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getDelegate().entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return getDelegate().equals(o);
    }

    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }

    @Override
    public String toString() {
        return getDelegate().toString();
    }
}
