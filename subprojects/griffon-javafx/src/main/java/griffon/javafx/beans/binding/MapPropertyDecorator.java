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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class MapPropertyDecorator<K, V> extends MapProperty<K, V> {
    private final MapProperty<K, V> delegate;

    public MapPropertyDecorator(@Nonnull MapProperty<K, V> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final MapProperty<K, V> getDelegate() {
        return delegate;
    }

    @Override
    public void setValue(ObservableMap<K, V> v) {
        delegate.setValue(v);
    }

    @Override
    public void bindBidirectional(Property<ObservableMap<K, V>> other) {
        delegate.bindBidirectional(other);
    }

    @Override
    public void unbindBidirectional(Property<ObservableMap<K, V>> other) {
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
    public void bindContentBidirectional(ObservableMap<K, V> map) {
        delegate.bindContentBidirectional(map);
    }

    @Override
    public void unbindContentBidirectional(Object object) {
        delegate.unbindContentBidirectional(object);
    }

    @Override
    public void bindContent(ObservableMap<K, V> map) {
        delegate.bindContent(map);
    }

    @Override
    public void unbindContent(Object object) {
        delegate.unbindContent(object);
    }

    @Override
    public ObservableMap<K, V> getValue() {
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
    public ObjectBinding<V> valueAt(K key) {
        return delegate.valueAt(key);
    }

    @Override
    public ObjectBinding<V> valueAt(ObservableValue<K> key) {
        return delegate.valueAt(key);
    }

    @Override
    public BooleanBinding isEqualTo(ObservableMap<?, ?> other) {
        return delegate.isEqualTo(other);
    }

    @Override
    public BooleanBinding isNotEqualTo(ObservableMap<?, ?> other) {
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
    public boolean containsKey(Object obj) {
        return delegate.containsKey(obj);
    }

    @Override
    public boolean containsValue(Object obj) {
        return delegate.containsValue(obj);
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object obj) {
        return delegate.remove(obj);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> elements) {
        delegate.putAll(elements);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public ObservableMap<K, V> get() {
        return delegate.get();
    }

    @Override
    public void addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
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
    public void addListener(MapChangeListener<? super K, ? super V> listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(MapChangeListener<? super K, ? super V> listener) {
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
    public void bind(ObservableValue<? extends ObservableMap<K, V>> observable) {
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
    public void set(ObservableMap<K, V> value) {
        delegate.set(value);
    }
}
