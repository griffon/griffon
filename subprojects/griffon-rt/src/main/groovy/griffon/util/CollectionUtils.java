/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util;

import java.util.*;

/**
 * <p>Utility class that simplifies creating collections in Java.</p>
 * <p><strong>Creating Maps</strong><br/>
 * <pre>
 * Map<String, Object> m = map()
 *     .e("foo", foo)
 *     .e("bar", bar);
 * </pre></p>
 *
 * <p><strong>Creating Lists</strong><br/>
 * <pre>
 * List<String> l = list()
 *     .e("foo")
 *     .e("bar");
 * </pre></p>
 *
 * <p><strong>Creating Maps</strong><br/>
 * <pre>
 * Set<String> s = set()
 *     .e("foo")
 *     .e("bar");
 * </pre></p>
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public final class CollectionUtils {
    public static <K, V> MapBuilder<K, V> map() {
        return map(new LinkedHashMap<K, V>());
    }

    public static <K, V> MapBuilder<K, V> map(Map<K, V> delegate) {
        return new MapBuilder<K, V>(delegate);
    }

    public static <E> ListBuilder<E> list() {
        return list(new ArrayList<E>());
    }

    public static <E> ListBuilder<E> list(List<E> delegate) {
        return new ListBuilder<E>(delegate);
    }

    public static <E> SetBuilder<E> set() {
        return set(new HashSet<E>());
    }

    public static <E> SetBuilder<E> set(Set<E> delegate) {
        return new SetBuilder<E>(delegate);
    }

    public static class MapBuilder<K, V> implements Map<K, V> {
        private final Map<K, V> delegate;

        public MapBuilder(Map<K, V> delegate) {
            this.delegate = delegate;
        }

        public MapBuilder<K, V> e(K k, V v) {
            delegate.put(k, v);
            return this;
        }

        public int size() {
            return delegate.size();
        }

        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        public boolean containsKey(Object o) {
            return delegate.containsKey(o);
        }

        public boolean containsValue(Object o) {
            return delegate.containsValue(o);
        }

        public V get(Object o) {
            return delegate.get(o);
        }

        public V put(K k, V v) {
            return delegate.put(k, v);
        }

        public V remove(Object o) {
            return delegate.remove(o);
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            delegate.putAll(map);
        }

        public void clear() {
            delegate.clear();
        }

        public Set<K> keySet() {
            return delegate.keySet();
        }

        public Collection<V> values() {
            return delegate.values();
        }

        public Set<Entry<K, V>> entrySet() {
            return delegate.entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.equals(o);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }
    }

    public static class ListBuilder<E> implements List<E> {
        private final List<E> delegate;

        public ListBuilder(List<E> delegate) {
            this.delegate = delegate;
        }

        public ListBuilder<E> e(E e) {
            delegate.add(e);
            return this;
        }

        public int size() {
            return delegate.size();
        }

        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        public boolean contains(Object o) {
            return delegate.contains(o);
        }

        public Iterator<E> iterator() {
            return delegate.iterator();
        }

        public Object[] toArray() {
            return delegate.toArray();
        }

        public <T> T[] toArray(T[] ts) {
            return delegate.toArray(ts);
        }

        public boolean add(E e) {
            return delegate.add(e);
        }

        public boolean remove(Object o) {
            return delegate.remove(o);
        }

        public boolean containsAll(Collection<?> objects) {
            return delegate.containsAll(objects);
        }

        public boolean addAll(Collection<? extends E> es) {
            return delegate.addAll(es);
        }

        public boolean addAll(int i, Collection<? extends E> es) {
            return delegate.addAll(i, es);
        }

        public boolean removeAll(Collection<?> objects) {
            return delegate.removeAll(objects);
        }

        public boolean retainAll(Collection<?> objects) {
            return delegate.retainAll(objects);
        }

        public void clear() {
            delegate.clear();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.equals(o);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        public E get(int i) {
            return delegate.get(i);
        }

        public E set(int i, E e) {
            return delegate.set(i, e);
        }

        public void add(int i, E e) {
            delegate.add(i, e);
        }

        public E remove(int i) {
            return delegate.remove(i);
        }

        public int indexOf(Object o) {
            return delegate.indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return delegate.lastIndexOf(o);
        }

        public ListIterator<E> listIterator() {
            return delegate.listIterator();
        }

        public ListIterator<E> listIterator(int i) {
            return delegate.listIterator(i);
        }

        public List<E> subList(int i, int i1) {
            return delegate.subList(i, i1);
        }
    }

    public static class SetBuilder<E> implements Set<E> {
        private final Set<E> delegate;

        public SetBuilder(Set<E> delegate) {
            this.delegate = delegate;
        }

        public SetBuilder<E> e(E e) {
            delegate.add(e);
            return this;
        }

        public int size() {
            return delegate.size();
        }

        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        public boolean contains(Object o) {
            return delegate.contains(o);
        }

        public Iterator<E> iterator() {
            return delegate.iterator();
        }

        public Object[] toArray() {
            return delegate.toArray();
        }

        public <T> T[] toArray(T[] ts) {
            return delegate.toArray(ts);
        }

        public boolean add(E e) {
            return delegate.add(e);
        }

        public boolean remove(Object o) {
            return delegate.remove(o);
        }

        public boolean containsAll(Collection<?> objects) {
            return delegate.containsAll(objects);
        }

        public boolean addAll(Collection<? extends E> es) {
            return delegate.addAll(es);
        }

        public boolean retainAll(Collection<?> objects) {
            return delegate.retainAll(objects);
        }

        public boolean removeAll(Collection<?> objects) {
            return delegate.removeAll(objects);
        }

        public void clear() {
            delegate.clear();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.equals(o);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }
    }
}
