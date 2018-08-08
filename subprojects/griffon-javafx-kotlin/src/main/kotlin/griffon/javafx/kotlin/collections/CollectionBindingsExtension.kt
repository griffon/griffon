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
package griffon.javafx.kotlin.collections

import griffon.javafx.beans.binding.CollectionBindings
import javafx.beans.binding.NumberBinding
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import java.util.function.Function
import java.util.function.Supplier
import java.util.function.ToDoubleFunction

/**
 * @author Andres Almiray
 * @since 2.13.0
 */

fun ObservableList<*>.join(delimiter: String?): StringBinding {
    return CollectionBindings.joinList(this, delimiter)
}

fun <T> ObservableList<T>.join(delimiter: String?, mapper: Function<in T, String>): StringBinding {
    return CollectionBindings.joinList(this, delimiter, mapper)
}

fun ObservableList<*>.join(delimiter: ObservableValue<String>): StringBinding {
    return CollectionBindings.joinList(this, delimiter)
}

fun <T> ObservableList<T>.join(delimiter: ObservableValue<String>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return CollectionBindings.joinList(this, delimiter, mapper)
}

fun ObservableSet<*>.join(delimiter: String?): StringBinding {
    return CollectionBindings.joinSet(this, delimiter)
}

fun <T> ObservableSet<T>.join(delimiter: String?, mapper: Function<in T, String>): StringBinding {
    return CollectionBindings.joinSet(this, delimiter, mapper)
}

fun ObservableSet<*>.join(delimiter: ObservableValue<String>): StringBinding {
    return CollectionBindings.joinSet(this, delimiter)
}

fun <T> ObservableSet<T>.join(delimiter: ObservableValue<String>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return CollectionBindings.joinSet(this, delimiter, mapper)
}

fun <K, V> ObservableMap<K, V>.join(delimiter: String?): StringBinding {
    return CollectionBindings.joinMap(this, delimiter)
}

fun <K, V> ObservableMap<K, V>.join(delimiter: String?, mapper: Function<Map.Entry<K, V>, String>): StringBinding {
    return CollectionBindings.joinMap(this, delimiter, mapper)
}

fun <K, V> ObservableMap<K, V>.join(delimiter: ObservableValue<String>): StringBinding {
    return CollectionBindings.joinMap(this, delimiter)
}

fun <K, V> ObservableMap<K, V>.join(delimiter: ObservableValue<String>, mapper: ObservableValue<Function<Map.Entry<K, V>, String>>): StringBinding {
    return CollectionBindings.joinMap(this, delimiter, mapper)
}

fun ObservableList<out Number>.min(defaultValue: Number): NumberBinding {
    return CollectionBindings.minInList(this, defaultValue)
}

fun ObservableList<out Number>.min(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.minInList(this, supplier)
}

fun ObservableList<out Number>.max(defaultValue: Number): NumberBinding {
    return CollectionBindings.maxInList(this, defaultValue)
}

fun ObservableList<out Number>.max(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.maxInList(this, supplier)
}

fun ObservableList<out Number>.average(defaultValue: Number): NumberBinding {
    return CollectionBindings.averageInList(this, defaultValue)
}

fun ObservableList<out Number>.average(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.averageInList(this, supplier)
}

fun ObservableList<out Number>.sum(): NumberBinding {
    return CollectionBindings.sumOfList(this)
}

fun <T> ObservableList<T>.min(defaultValue: Number, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.minInList(this, defaultValue, mapper)
}

fun <T> ObservableList<T>.min(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.minInList(this, supplier, mapper)
}

fun <T> ObservableList<T>.max(defaultValue: Number, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.maxInList(this, defaultValue, mapper)
}

fun <T> ObservableList<T>.max(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.maxInList(this, supplier, mapper)
}

fun <T> ObservableList<T>.average(defaultValue: Number, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.averageInList(this, defaultValue, mapper)
}

fun <T> ObservableList<T>.average(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.averageInList(this, supplier, mapper)
}

fun <T> ObservableList<T>.sum(mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.sumOfList(this, mapper)
}

fun <T> ObservableList<T>.min(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.minInList(this, defaultValue, mapper)
}

fun <T> ObservableList<T>.min(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.minInList(this, supplier, mapper)
}

fun <T> ObservableList<T>.max(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.maxInList(this, defaultValue, mapper)
}

fun <T> ObservableList<T>.max(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.maxInList(this, supplier, mapper)
}

fun <T> ObservableList<T>.average(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.averageInList(this, defaultValue, mapper)
}

fun <T> ObservableList<T>.average(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.averageInList(this, supplier, mapper)
}

fun <T> ObservableList<T>.sum(mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.sumOfList(this, mapper)
}

fun ObservableSet<out Number>.min(defaultValue: Number): NumberBinding {
    return CollectionBindings.minInSet(this, defaultValue)
}

fun ObservableSet<out Number>.min(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.minInSet(this, supplier)
}

fun ObservableSet<out Number>.max(defaultValue: Number): NumberBinding {
    return CollectionBindings.maxInSet(this, defaultValue)
}

fun ObservableSet<out Number>.max(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.maxInSet(this, supplier)
}

fun ObservableSet<out Number>.average(defaultValue: Number): NumberBinding {
    return CollectionBindings.averageInSet(this, defaultValue)
}

fun ObservableSet<out Number>.average(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.averageInSet(this, supplier)
}

fun ObservableSet<out Number>.sum(): NumberBinding {
    return CollectionBindings.sumOfSet(this)
}

fun <T> ObservableSet<T>.min(defaultValue: Number, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.minInSet(this, defaultValue, mapper)
}

fun <T> ObservableSet<T>.min(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.minInSet(this, supplier, mapper)
}

fun <T> ObservableSet<T>.max(defaultValue: Number, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.maxInSet(this, defaultValue, mapper)
}

fun <T> ObservableSet<T>.max(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.maxInSet(this, supplier, mapper)
}

fun <T> ObservableSet<T>.average(defaultValue: Number, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.averageInSet(this, defaultValue, mapper)
}

fun <T> ObservableSet<T>.average(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.averageInSet(this, supplier, mapper)
}

fun <T> ObservableSet<T>.sum(mapper: ToDoubleFunction<in T>): NumberBinding {
    return CollectionBindings.sumOfSet(this, mapper)
}

fun <T> ObservableSet<T>.min(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.minInSet(this, defaultValue, mapper)
}

fun <T> ObservableSet<T>.min(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.minInSet(this, supplier, mapper)
}

fun <T> ObservableSet<T>.max(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.maxInSet(this, defaultValue, mapper)
}

fun <T> ObservableSet<T>.max(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.maxInSet(this, supplier, mapper)
}

fun <T> ObservableSet<T>.average(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.averageInSet(this, defaultValue, mapper)
}

fun <T> ObservableSet<T>.average(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.averageInSet(this, supplier, mapper)
}

fun <T> ObservableSet<T>.sum(mapper: ObservableValue<ToDoubleFunction<in T>>): NumberBinding {
    return CollectionBindings.sumOfSet(this, mapper)
}

fun <K> ObservableMap<K, Number>.min(defaultValue: Number): NumberBinding {
    return CollectionBindings.minInMap(this, defaultValue)
}

fun <K> ObservableMap<K, Number>.min(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.minInMap(this, supplier)
}

fun <K> ObservableMap<K, Number>.max(defaultValue: Number): NumberBinding {
    return CollectionBindings.maxInMap(this, defaultValue)
}

fun <K> ObservableMap<K, Number>.max(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.maxInMap(this, supplier)
}

fun <K> ObservableMap<K, Number>.average(defaultValue: Number): NumberBinding {
    return CollectionBindings.averageInMap(this, defaultValue)
}

fun <K> ObservableMap<K, Number>.average(supplier: Supplier<out Number>): NumberBinding {
    return CollectionBindings.averageInMap(this, supplier)
}

fun <K> ObservableMap<K, Number>.sum(): NumberBinding {
    return CollectionBindings.sumOfMap(this)
}

fun <K, V> ObservableMap<K, V>.min(defaultValue: Number, mapper: ToDoubleFunction<in V>): NumberBinding {
    return CollectionBindings.minInMap(this, defaultValue, mapper)
}

fun <K, V> ObservableMap<K, V>.min(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in V>): NumberBinding {
    return CollectionBindings.minInMap(this, supplier, mapper)
}

fun <K, V> ObservableMap<K, V>.max(defaultValue: Number, mapper: ToDoubleFunction<in V>): NumberBinding {
    return CollectionBindings.maxInMap(this, defaultValue, mapper)
}

fun <K, V> ObservableMap<K, V>.max(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in V>): NumberBinding {
    return CollectionBindings.maxInMap(this, supplier, mapper)
}

fun <K, V> ObservableMap<K, V>.average(defaultValue: Number, mapper: ToDoubleFunction<in V>): NumberBinding {
    return CollectionBindings.averageInMap(this, defaultValue, mapper)
}

fun <K, V> ObservableMap<K, V>.average(supplier: Supplier<out Number>, mapper: ToDoubleFunction<in V>): NumberBinding {
    return CollectionBindings.averageInMap(this, supplier, mapper)
}

fun <K, V> ObservableMap<K, V>.sum(mapper: ToDoubleFunction<in V>): NumberBinding {
    return CollectionBindings.sumOfMap(this, mapper)
}

fun <K, V> ObservableMap<K, V>.min(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in V>>): NumberBinding {
    return CollectionBindings.minInMap(this, defaultValue, mapper)
}

fun <K, V> ObservableMap<K, V>.min(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in V>>): NumberBinding {
    return CollectionBindings.minInMap(this, supplier, mapper)
}

fun <K, V> ObservableMap<K, V>.max(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in V>>): NumberBinding {
    return CollectionBindings.maxInMap(this, defaultValue, mapper)
}

fun <K, V> ObservableMap<K, V>.max(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in V>>): NumberBinding {
    return CollectionBindings.maxInMap(this, supplier, mapper)
}

fun <K, V> ObservableMap<K, V>.average(defaultValue: Number, mapper: ObservableValue<ToDoubleFunction<in V>>): NumberBinding {
    return CollectionBindings.averageInMap(this, defaultValue, mapper)
}

fun <K, V> ObservableMap<K, V>.average(supplier: Supplier<out Number>, mapper: ObservableValue<ToDoubleFunction<in V>>): NumberBinding {
    return CollectionBindings.averageInMap(this, supplier, mapper)
}

fun <K, V> ObservableMap<K, V>.sum(mapper: ObservableValue<ToDoubleFunction<in V>>): NumberBinding {
    return CollectionBindings.sumOfMap(this, mapper)
}
