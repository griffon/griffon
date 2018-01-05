/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.javafx

import griffon.javafx.beans.binding.CollectionBindings
import groovy.transform.CompileStatic
import javafx.beans.binding.NumberBinding
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.function.Function
import java.util.function.Supplier
import java.util.function.ToDoubleFunction

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
@CompileStatic
final class CollectionBindingsExtension {
    @Nonnull
    static StringBinding join(@Nonnull ObservableList<?> self, @Nullable String delimiter) {
        CollectionBindings.joinList(self, delimiter)
    }

    @Nonnull
    static <T> StringBinding join(
        @Nonnull ObservableList<T> self, @Nullable String delimiter, @Nonnull Function<? super T, String> mapper) {
        CollectionBindings.joinList(self, delimiter, mapper)
    }

    @Nonnull
    static StringBinding join(@Nonnull ObservableList<?> self, @Nonnull ObservableValue<String> delimiter) {
        CollectionBindings.joinList(self, delimiter)
    }

    @Nonnull
    static <T> StringBinding join(
        @Nonnull ObservableList<T> self,
        @Nonnull ObservableValue<String> delimiter, @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        CollectionBindings.joinList(self, delimiter, mapper)
    }

    @Nonnull
    static StringBinding join(@Nonnull ObservableSet<?> self, @Nullable String delimiter) {
        CollectionBindings.joinSet(self, delimiter)
    }

    @Nonnull
    static <T> StringBinding join(
        @Nonnull ObservableSet<T> self, @Nullable String delimiter, @Nonnull Function<? super T, String> mapper) {
        CollectionBindings.joinSet(self, delimiter, mapper)
    }

    @Nonnull
    static StringBinding join(@Nonnull ObservableSet<?> self, @Nonnull ObservableValue<String> delimiter) {
        CollectionBindings.joinSet(self, delimiter)
    }

    @Nonnull
    static <T> StringBinding join(
        @Nonnull ObservableSet<T> self,
        @Nonnull ObservableValue<String> delimiter, @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        CollectionBindings.joinSet(self, delimiter, mapper)
    }

    @Nonnull
    static <K, V> StringBinding join(@Nonnull ObservableMap<K, V> self, @Nullable String delimiter) {
        CollectionBindings.joinMap(self, delimiter)
    }

    @Nonnull
    static <K, V> StringBinding join(
        @Nonnull ObservableMap<K, V> self,
        @Nullable String delimiter, @Nonnull Function<Map.Entry<K, V>, String> mapper) {
        CollectionBindings.joinMap(self, delimiter, mapper)
    }

    @Nonnull
    static <K, V> StringBinding join(@Nonnull ObservableMap<K, V> self, @Nonnull ObservableValue<String> delimiter) {
        CollectionBindings.joinMap(self, delimiter)
    }

    @Nonnull
    static <K, V> StringBinding join(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull ObservableValue<String> delimiter,
        @Nonnull ObservableValue<Function<Map.Entry<K, V>, String>> mapper) {
        CollectionBindings.joinMap(self, delimiter, mapper)
    }

    @Nonnull
    static NumberBinding min(@Nonnull ObservableList<? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.minInList(self, defaultValue)
    }

    @Nonnull
    static NumberBinding min(
        @Nonnull ObservableList<? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.minInList(self, supplier)
    }

    @Nonnull
    static NumberBinding max(@Nonnull ObservableList<? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.maxInList(self, defaultValue)
    }

    @Nonnull
    static NumberBinding max(
        @Nonnull ObservableList<? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.maxInList(self, supplier)
    }

    @Nonnull
    static NumberBinding average(@Nonnull ObservableList<? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.averageInList(self, defaultValue)
    }

    @Nonnull
    static NumberBinding average(
        @Nonnull ObservableList<? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.averageInList(self, supplier)
    }

    @Nonnull
    static NumberBinding sum(@Nonnull ObservableList<? extends Number> self) {
        CollectionBindings.sumOfList(self)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableList<T> self, @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.minInList(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.minInList(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableList<T> self, @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.maxInList(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.maxInList(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableList<T> self, @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.averageInList(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.averageInList(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding sum(@Nonnull ObservableList<T> self, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.sumOfList(self, mapper)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableList<T> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.minInList(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.minInList(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableList<T> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.maxInList(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.maxInList(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableList<T> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.averageInList(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.averageInList(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding sum(
        @Nonnull ObservableList<T> self, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.sumOfList(self, mapper)
    }

    @Nonnull
    static NumberBinding min(@Nonnull ObservableSet<? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.minInSet(self, defaultValue)
    }

    @Nonnull
    static NumberBinding min(
        @Nonnull ObservableSet<? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.minInSet(self, supplier)
    }

    @Nonnull
    static NumberBinding max(@Nonnull ObservableSet<? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.maxInSet(self, defaultValue)
    }

    @Nonnull
    static NumberBinding max(
        @Nonnull ObservableSet<? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.maxInSet(self, supplier)
    }

    @Nonnull
    static NumberBinding average(@Nonnull ObservableSet<? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.averageInSet(self, defaultValue)
    }

    @Nonnull
    static NumberBinding average(
        @Nonnull ObservableSet<? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.averageInSet(self, supplier)
    }

    @Nonnull
    static NumberBinding sum(@Nonnull ObservableSet<? extends Number> self) {
        CollectionBindings.sumOfSet(self)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableSet<T> self, @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.minInSet(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.minInSet(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableSet<T> self, @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.maxInSet(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.maxInSet(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableSet<T> self, @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.averageInSet(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.averageInSet(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding sum(@Nonnull ObservableSet<T> self, @Nonnull ToDoubleFunction<? super T> mapper) {
        CollectionBindings.sumOfSet(self, mapper)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableSet<T> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.minInSet(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding min(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.minInSet(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableSet<T> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.maxInSet(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding max(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.maxInSet(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableSet<T> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.averageInSet(self, defaultValue, mapper)
    }

    @Nonnull
    static <T> NumberBinding average(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.averageInSet(self, supplier, mapper)
    }

    @Nonnull
    static <T> NumberBinding sum(
        @Nonnull ObservableSet<T> self, @Nonnull ObservableValue<ToDoubleFunction<? super T>> mapper) {
        CollectionBindings.sumOfSet(self, mapper)
    }

    @Nonnull
    static <K> NumberBinding min(
        @Nonnull ObservableMap<K, ? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.minInMap(self, defaultValue)
    }

    @Nonnull
    static <K> NumberBinding min(
        @Nonnull ObservableMap<K, ? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.minInMap(self, supplier)
    }

    @Nonnull
    static <K> NumberBinding max(
        @Nonnull ObservableMap<K, ? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.maxInMap(self, defaultValue)
    }

    @Nonnull
    static <K> NumberBinding max(
        @Nonnull ObservableMap<K, ? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.maxInMap(self, supplier)
    }

    @Nonnull
    static <K> NumberBinding average(
        @Nonnull ObservableMap<K, ? extends Number> self, @Nonnull Number defaultValue) {
        CollectionBindings.averageInMap(self, defaultValue)
    }

    @Nonnull
    static <K> NumberBinding average(
        @Nonnull ObservableMap<K, ? extends Number> self, @Nonnull Supplier<? extends Number> supplier) {
        CollectionBindings.averageInMap(self, supplier)
    }

    @Nonnull
    static <K> NumberBinding sum(@Nonnull ObservableMap<K, ? extends Number> self) {
        CollectionBindings.sumOfMap(self)
    }

    @Nonnull
    static <K, V> NumberBinding min(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super V> mapper) {
        CollectionBindings.minInMap(self, defaultValue, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding min(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super V> mapper) {
        CollectionBindings.minInMap(self, supplier, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding max(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super V> mapper) {
        CollectionBindings.maxInMap(self, defaultValue, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding max(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super V> mapper) {
        CollectionBindings.maxInMap(self, supplier, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding average(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Number defaultValue, @Nonnull ToDoubleFunction<? super V> mapper) {
        CollectionBindings.averageInMap(self, defaultValue, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding average(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ToDoubleFunction<? super V> mapper) {
        CollectionBindings.averageInMap(self, supplier, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding sum(
        @Nonnull ObservableMap<K, V> self, @Nonnull ToDoubleFunction<? super V> mapper) {
        CollectionBindings.sumOfMap(self, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding min(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super V>> mapper) {
        CollectionBindings.minInMap(self, defaultValue, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding min(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super V>> mapper) {
        CollectionBindings.minInMap(self, supplier, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding max(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super V>> mapper) {
        CollectionBindings.maxInMap(self, defaultValue, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding max(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super V>> mapper) {
        CollectionBindings.maxInMap(self, supplier, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding average(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Number defaultValue, @Nonnull ObservableValue<ToDoubleFunction<? super V>> mapper) {
        CollectionBindings.averageInMap(self, defaultValue, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding average(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<? extends Number> supplier, @Nonnull ObservableValue<ToDoubleFunction<? super V>> mapper) {
        CollectionBindings.averageInMap(self, supplier, mapper)
    }

    @Nonnull
    static <K, V> NumberBinding sum(
        @Nonnull ObservableMap<K, V> self, @Nonnull ObservableValue<ToDoubleFunction<? super V>> mapper) {
        CollectionBindings.sumOfMap(self, mapper)
    }
}
