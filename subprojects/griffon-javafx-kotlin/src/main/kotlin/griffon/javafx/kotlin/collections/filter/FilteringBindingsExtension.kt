/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.javafx.kotlin.collections.filter

import griffon.javafx.beans.binding.FilteringBindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.FloatBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.LongBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

/**
 * @author Andres Almiray
 * @since 2.13.0
 */

fun ObservableList<String>.filterThenFindFirstString(defaultValue: String, filter: Predicate<in String>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, defaultValue, filter)
}

fun ObservableSet<Boolean>.filterThenFindFirstBoolean(defaultValue: Boolean, filter: Predicate<in Boolean>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, defaultValue, filter)
}

fun ObservableSet<Boolean>.filterThenFindFirstBoolean(supplier: Supplier<Boolean>, filter: Predicate<in Boolean>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, supplier, filter)
}

fun <T> ObservableList<T>.filterThenFindFirst(defaultValue: T, filter: Predicate<in T>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, defaultValue, filter)
}

fun <T> ObservableList<T>.filterThenFindFirst(supplier: Supplier<T>, filter: Predicate<in T>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, supplier, filter)
}

fun <T> ObservableList<T>.filterThenFindFirst(defaultValue: T, filter: ObservableValue<Predicate<in T>>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, defaultValue, filter)
}

fun <T> ObservableList<T>.filterThenFindFirst(supplier: Supplier<T>, filter: ObservableValue<Predicate<in T>>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, supplier, filter)
}

fun ObservableList<Boolean>.filterThenFindFirstBoolean(defaultValue: Boolean, filter: Predicate<in Boolean>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, defaultValue, filter)
}

fun ObservableList<Boolean>.filterThenFindFirstBoolean(supplier: Supplier<Boolean>, filter: Predicate<in Boolean>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, supplier, filter)
}

fun ObservableList<Boolean>.filterThenFindFirstBoolean(defaultValue: Boolean, filter: ObservableValue<Predicate<in Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, defaultValue, filter)
}

fun ObservableList<Boolean>.filterThenFindFirstBoolean(supplier: Supplier<Boolean>, filter: ObservableValue<Predicate<in Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, supplier, filter)
}

fun ObservableList<Int>.filterThenFindFirstInteger(defaultValue: Int, filter: Predicate<in Int>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, defaultValue, filter)
}

fun ObservableList<Int>.filterThenFindFirstInteger(supplier: Supplier<Int>, filter: Predicate<in Int>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, supplier, filter)
}

fun ObservableList<Int>.filterThenFindFirstInteger(defaultValue: Int, filter: ObservableValue<Predicate<in Int>>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, defaultValue, filter)
}

fun ObservableList<Int>.filterThenFindFirstInteger(supplier: Supplier<Int>, filter: ObservableValue<Predicate<in Int>>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, supplier, filter)
}

fun ObservableList<Long>.filterThenFindFirstLong(defaultValue: Long, filter: Predicate<in Long>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, defaultValue, filter)
}

fun ObservableList<Long>.filterThenFindFirstLong(supplier: Supplier<Long>, filter: Predicate<in Long>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, supplier, filter)
}

fun ObservableList<Long>.filterThenFindFirstLong(defaultValue: Long, filter: ObservableValue<Predicate<in Long>>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, defaultValue, filter)
}

fun ObservableList<Long>.filterThenFindFirstLong(supplier: Supplier<Long>, filter: ObservableValue<Predicate<in Long>>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, supplier, filter)
}

fun ObservableList<Float>.filterThenFindFirstFloat(defaultValue: Float, filter: Predicate<in Float>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, defaultValue, filter)
}

fun ObservableList<Float>.filterThenFindFirstFloat(supplier: Supplier<Float>, filter: Predicate<in Float>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, supplier, filter)
}

fun ObservableList<Float>.filterThenFindFirstFloat(defaultValue: Float, filter: ObservableValue<Predicate<in Float>>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, defaultValue, filter)
}

fun ObservableList<Float>.filterThenFindFirstFloat(supplier: Supplier<Float>, filter: ObservableValue<Predicate<in Float>>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, supplier, filter)
}

fun ObservableList<Double>.filterThenFindFirstDouble(defaultValue: Double, filter: Predicate<in Double>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, defaultValue, filter)
}

fun ObservableList<Double>.filterThenFindFirstDouble(supplier: Supplier<Double>, filter: Predicate<in Double>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, supplier, filter)
}

fun ObservableList<Double>.filterThenFindFirstDouble(defaultValue: Double, filter: ObservableValue<Predicate<in Double>>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, defaultValue, filter)
}

fun ObservableList<Double>.filterThenFindFirstDouble(supplier: Supplier<Double>, filter: ObservableValue<Predicate<in Double>>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, supplier, filter)
}

fun ObservableList<String>.filterThenFindFirstString(supplier: Supplier<String>, filter: Predicate<in String>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, supplier, filter)
}

fun ObservableList<String>.filterThenFindFirstString(defaultValue: String, filter: ObservableValue<Predicate<in String>>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, defaultValue, filter)
}

fun ObservableList<String>.filterThenFindFirstString(supplier: Supplier<String>, filter: ObservableValue<Predicate<in String>>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, supplier, filter)
}

fun <T> ObservableSet<T>.filterThenFindFirst(defaultValue: T, filter: Predicate<in T>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, defaultValue, filter)
}

fun <T> ObservableSet<T>.filterThenFindFirst(supplier: Supplier<T>, filter: Predicate<in T>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, supplier, filter)
}

fun <T> ObservableSet<T>.filterThenFindFirst(defaultValue: T, filter: ObservableValue<Predicate<in T>>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, defaultValue, filter)
}

fun <T> ObservableSet<T>.filterThenFindFirst(supplier: Supplier<T>, filter: ObservableValue<Predicate<in T>>): ObjectBinding<T> {
    return FilteringBindings.filterThenFindFirst(this, supplier, filter)
}

fun ObservableSet<Boolean>.filterThenFindFirstBoolean(defaultValue: Boolean, filter: ObservableValue<Predicate<in Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, defaultValue, filter)
}

fun ObservableSet<Boolean>.filterThenFindFirstBoolean(supplier: Supplier<Boolean>, filter: ObservableValue<Predicate<in Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, supplier, filter)
}

fun ObservableSet<Int>.filterThenFindFirstInteger(defaultValue: Int, filter: Predicate<in Int>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, defaultValue, filter)
}

fun ObservableSet<Int>.filterThenFindFirstInteger(supplier: Supplier<Int>, filter: Predicate<in Int>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, supplier, filter)
}

fun ObservableSet<Int>.filterThenFindFirstInteger(defaultValue: Int, filter: ObservableValue<Predicate<in Int>>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, defaultValue, filter)
}

fun ObservableSet<Int>.filterThenFindFirstInteger(supplier: Supplier<Int>, filter: ObservableValue<Predicate<in Int>>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, supplier, filter)
}

fun ObservableSet<Long>.filterThenFindFirstLong(defaultValue: Long, filter: Predicate<in Long>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, defaultValue, filter)
}

fun ObservableSet<Long>.filterThenFindFirstLong(supplier: Supplier<Long>, filter: Predicate<in Long>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, supplier, filter)
}

fun ObservableSet<Long>.filterThenFindFirstLong(defaultValue: Long, filter: ObservableValue<Predicate<in Long>>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, defaultValue, filter)
}

fun ObservableSet<Long>.filterThenFindFirstLong(supplier: Supplier<Long>, filter: ObservableValue<Predicate<in Long>>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, supplier, filter)
}

fun ObservableSet<Float>.filterThenFindFirstFloat(defaultValue: Float, filter: Predicate<in Float>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, defaultValue, filter)
}

fun ObservableSet<Float>.filterThenFindFirstFloat(supplier: Supplier<Float>, filter: Predicate<in Float>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, supplier, filter)
}

fun ObservableSet<Float>.filterThenFindFirstFloat(defaultValue: Float, filter: ObservableValue<Predicate<in Float>>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, defaultValue, filter)
}

fun ObservableSet<Float>.filterThenFindFirstFloat(supplier: Supplier<Float>, filter: ObservableValue<Predicate<in Float>>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, supplier, filter)
}

fun ObservableSet<Double>.filterThenFindFirstDouble(defaultValue: Double, filter: Predicate<in Double>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, defaultValue, filter)
}

fun ObservableSet<Double>.filterThenFindFirstDouble(supplier: Supplier<Double>, filter: Predicate<in Double>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, supplier, filter)
}

fun ObservableSet<Double>.filterThenFindFirstDouble(defaultValue: Double, filter: ObservableValue<Predicate<in Double>>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, defaultValue, filter)
}

fun ObservableSet<Double>.filterThenFindFirstDouble(supplier: Supplier<Double>, filter: ObservableValue<Predicate<in Double>>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, supplier, filter)
}

fun ObservableSet<String>.filterThenFindFirstString(defaultValue: String, filter: Predicate<in String>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, defaultValue, filter)
}

fun ObservableSet<String>.filterThenFindFirstString(supplier: Supplier<String>, filter: Predicate<in String>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, supplier, filter)
}

fun ObservableSet<String>.filterThenFindFirstString(defaultValue: String, filter: ObservableValue<Predicate<in String>>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, defaultValue, filter)
}

fun ObservableSet<String>.filterThenFindFirstString(supplier: Supplier<String>, filter: ObservableValue<Predicate<in String>>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, supplier, filter)
}

fun <K, V> ObservableMap<K, V>.filterThenFindFirst(defaultValue: V, filter: Predicate<in V>): ObjectBinding<V> {
    return FilteringBindings.filterThenFindFirst(this, defaultValue, filter)
}

fun <K, V> ObservableMap<K, V>.filterThenFindFirst(supplier: Supplier<V>, filter: Predicate<in V>): ObjectBinding<V> {
    return FilteringBindings.filterThenFindFirst(this, supplier, filter)
}

fun <K, V> ObservableMap<K, V>.filterThenFindFirst(defaultValue: V, filter: ObservableValue<Predicate<in V>>): ObjectBinding<V> {
    return FilteringBindings.filterThenFindFirst(this, defaultValue, filter)
}

fun <K, V> ObservableMap<K, V>.filterThenFindFirst(supplier: Supplier<V>, filter: ObservableValue<Predicate<in V>>): ObjectBinding<V> {
    return FilteringBindings.filterThenFindFirst(this, supplier, filter)
}

fun <K> ObservableMap<K, Boolean>.filterThenFindFirstBoolean(defaultValue: Boolean, filter: Predicate<in Boolean>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Boolean>.filterThenFindFirstBoolean(supplier: Supplier<Boolean>, filter: Predicate<in Boolean>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, supplier, filter)
}

fun <K> ObservableMap<K, Boolean>.filterThenFindFirstBoolean(defaultValue: Boolean, filter: ObservableValue<Predicate<in Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Boolean>.filterThenFindFirstBoolean(supplier: Supplier<Boolean>, filter: ObservableValue<Predicate<in Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenFindFirstBoolean(this, supplier, filter)
}

fun <K> ObservableMap<K, Int>.filterThenFindFirstInteger(defaultValue: Int, filter: Predicate<in Int>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Int>.filterThenFindFirstInteger(supplier: Supplier<Int>, filter: Predicate<in Int>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, supplier, filter)
}

fun <K> ObservableMap<K, Int>.filterThenFindFirstInteger(defaultValue: Int, filter: ObservableValue<Predicate<in Int>>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Int>.filterThenFindFirstInteger(supplier: Supplier<Int>, filter: ObservableValue<Predicate<in Int>>): IntegerBinding {
    return FilteringBindings.filterThenFindFirstInteger(this, supplier, filter)
}

fun <K> ObservableMap<K, Long>.filterThenFindFirstLong(defaultValue: Long, filter: Predicate<in Long>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Long>.filterThenFindFirstLong(supplier: Supplier<Long>, filter: Predicate<in Long>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, supplier, filter)
}

fun <K> ObservableMap<K, Long>.filterThenFindFirstLong(defaultValue: Long, filter: ObservableValue<Predicate<in Long>>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Long>.filterThenFindFirstLong(supplier: Supplier<Long>, filter: ObservableValue<Predicate<in Long>>): LongBinding {
    return FilteringBindings.filterThenFindFirstLong(this, supplier, filter)
}

fun <K> ObservableMap<K, Float>.filterThenFindFirstFloat(defaultValue: Float, filter: Predicate<in Float>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Float>.filterThenFindFirstFloat(supplier: Supplier<Float>, filter: Predicate<in Float>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, supplier, filter)
}

fun <K> ObservableMap<K, Float>.filterThenFindFirstFloat(defaultValue: Float, filter: ObservableValue<Predicate<in Float>>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Float>.filterThenFindFirstFloat(supplier: Supplier<Float>, filter: ObservableValue<Predicate<in Float>>): FloatBinding {
    return FilteringBindings.filterThenFindFirstFloat(this, supplier, filter)
}

fun <K> ObservableMap<K, Double>.filterThenFindFirstDouble(defaultValue: Double, filter: Predicate<in Double>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Double>.filterThenFindFirstDouble(supplier: Supplier<Double>, filter: Predicate<in Double>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, supplier, filter)
}

fun <K> ObservableMap<K, Double>.filterThenFindFirstDouble(defaultValue: Double, filter: ObservableValue<Predicate<in Double>>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, defaultValue, filter)
}

fun <K> ObservableMap<K, Double>.filterThenFindFirstDouble(supplier: Supplier<Double>, filter: ObservableValue<Predicate<in Double>>): DoubleBinding {
    return FilteringBindings.filterThenFindFirstDouble(this, supplier, filter)
}

fun <K> ObservableMap<K, String>.filterThenFindFirstString(defaultValue: String, filter: Predicate<in String>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, defaultValue, filter)
}

fun <K> ObservableMap<K, String>.filterThenFindFirstString(supplier: Supplier<String>, filter: Predicate<in String>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, supplier, filter)
}

fun <K> ObservableMap<K, String>.filterThenFindFirstString(defaultValue: String, filter: ObservableValue<Predicate<in String>>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, defaultValue, filter)
}

fun <K> ObservableMap<K, String>.filterThenFindFirstString(supplier: Supplier<String>, filter: ObservableValue<Predicate<in String>>): StringBinding {
    return FilteringBindings.filterThenFindFirstString(this, supplier, filter)
}

fun <T, R> ObservableList<T>.mapThenFilterThenFindFirst(defaultValue: R, mapper: Function<in T, R>, filter: Predicate<in R>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T, R> ObservableList<T>.mapThenFilterThenFindFirst(supplier: Supplier<R>, mapper: Function<in T, R>, filter: Predicate<in R>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T, R> ObservableList<T>.mapThenFilterThenFindFirst(defaultValue: R, mapper: ObservableValue<Function<in T, R>>, filter: ObservableValue<Predicate<in R>>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T, R> ObservableList<T>.mapThenFilterThenFindFirst(supplier: Supplier<R>, mapper: ObservableValue<Function<in T, R>>, filter: ObservableValue<Predicate<in R>>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToBooleanThenFilterThenFindFirst(defaultValue: Boolean, mapper: Function<in T, Boolean>, filter: Predicate<Boolean>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToBooleanThenFilterThenFindFirst(supplier: Supplier<Boolean>, mapper: Function<in T, Boolean>, filter: Predicate<Boolean>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToBooleanThenFilterThenFindFirst(defaultValue: Boolean, mapper: ObservableValue<Function<in T, Boolean>>, filter: ObservableValue<Predicate<Boolean>>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToBooleanThenFilterThenFindFirst(supplier: Supplier<Boolean>, mapper: ObservableValue<Function<in T, Boolean>>, filter: ObservableValue<Predicate<Boolean>>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToIntegerThenFilterThenFindFirst(defaultValue: Int, mapper: Function<in T, Int>, filter: Predicate<Int>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToIntegerThenFilterThenFindFirst(supplier: Supplier<Int>, mapper: Function<in T, Int>, filter: Predicate<Int>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToIntegerThenFilterThenFindFirst(defaultValue: Int, mapper: ObservableValue<Function<in T, Int>>, filter: ObservableValue<Predicate<Int>>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToIntegerThenFilterThenFindFirst(supplier: Supplier<Int>, mapper: ObservableValue<Function<in T, Int>>, filter: ObservableValue<Predicate<Int>>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToLongThenFilterThenFindFirst(defaultValue: Long, mapper: Function<in T, Long>, filter: Predicate<Long>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToLongThenFilterThenFindFirst(supplier: Supplier<Long>, mapper: Function<in T, Long>, filter: Predicate<Long>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToLongThenFilterThenFindFirst(defaultValue: Long, mapper: ObservableValue<Function<in T, Long>>, filter: ObservableValue<Predicate<Long>>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToLongThenFilterThenFindFirst(supplier: Supplier<Long>, mapper: ObservableValue<Function<in T, Long>>, filter: ObservableValue<Predicate<Long>>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToFloatThenFilterThenFindFirst(defaultValue: Float, mapper: Function<in T, Float>, filter: Predicate<Float>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToFloatThenFilterThenFindFirst(supplier: Supplier<Float>, mapper: Function<in T, Float>, filter: Predicate<Float>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToFloatThenFilterThenFindFirst(defaultValue: Float, mapper: ObservableValue<Function<in T, Float>>, filter: ObservableValue<Predicate<Float>>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToFloatThenFilterThenFindFirst(supplier: Supplier<Float>, mapper: ObservableValue<Function<in T, Float>>, filter: ObservableValue<Predicate<Float>>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToDoubleThenFilterThenFindFirst(defaultValue: Double, mapper: Function<in T, Double>, filter: Predicate<Double>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToDoubleThenFilterThenFindFirst(supplier: Supplier<Double>, mapper: Function<in T, Double>, filter: Predicate<Double>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToDoubleThenFilterThenFindFirst(defaultValue: Double, mapper: ObservableValue<Function<in T, Double>>, filter: ObservableValue<Predicate<Double>>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToDoubleThenFilterThenFindFirst(supplier: Supplier<Double>, mapper: ObservableValue<Function<in T, Double>>, filter: ObservableValue<Predicate<Double>>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToStringThenFilterThenFindFirst(defaultValue: String, mapper: Function<in T, String>, filter: Predicate<String>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToStringThenFilterThenFindFirst(supplier: Supplier<String>, mapper: Function<in T, String>, filter: Predicate<String>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableList<T>.mapToStringThenFilterThenFindFirst(defaultValue: String, mapper: ObservableValue<Function<in T, String>>, filter: ObservableValue<Predicate<String>>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableList<T>.mapToStringThenFilterThenFindFirst(supplier: Supplier<String>, mapper: ObservableValue<Function<in T, String>>, filter: ObservableValue<Predicate<String>>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T, R> ObservableSet<T>.mapThenFilterThenFindFirst(defaultValue: R, mapper: Function<in T, R>, filter: Predicate<in R>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T, R> ObservableSet<T>.mapThenFilterThenFindFirst(supplier: Supplier<R>, mapper: Function<in T, R>, filter: Predicate<in R>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T, R> ObservableSet<T>.mapThenFilterThenFindFirst(defaultValue: R, mapper: ObservableValue<Function<in T, R>>, filter: ObservableValue<Predicate<in R>>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T, R> ObservableSet<T>.mapThenFilterThenFindFirst(supplier: Supplier<R>, mapper: ObservableValue<Function<in T, R>>, filter: ObservableValue<Predicate<in R>>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToBooleanThenFilterThenFindFirst(defaultValue: Boolean, mapper: Function<in T, Boolean>, filter: Predicate<Boolean>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToBooleanThenFilterThenFindFirst(supplier: Supplier<Boolean>, mapper: Function<in T, Boolean>, filter: Predicate<Boolean>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToBooleanThenFilterThenFindFirst(defaultValue: Boolean, mapper: ObservableValue<Function<in T, Boolean>>, filter: ObservableValue<Predicate<Boolean>>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToBooleanThenFilterThenFindFirst(supplier: Supplier<Boolean>, mapper: ObservableValue<Function<in T, Boolean>>, filter: ObservableValue<Predicate<Boolean>>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToIntegerThenFilterThenFindFirst(defaultValue: Int, mapper: Function<in T, Int>, filter: Predicate<Int>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToIntegerThenFilterThenFindFirst(supplier: Supplier<Int>, mapper: Function<in T, Int>, filter: Predicate<Int>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToIntegerThenFilterThenFindFirst(defaultValue: Int, mapper: ObservableValue<Function<in T, Int>>, filter: ObservableValue<Predicate<Int>>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToIntegerThenFilterThenFindFirst(supplier: Supplier<Int>, mapper: ObservableValue<Function<in T, Int>>, filter: ObservableValue<Predicate<Int>>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToLongThenFilterThenFindFirst(defaultValue: Long, mapper: Function<in T, Long>, filter: Predicate<Long>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToLongThenFilterThenFindFirst(supplier: Supplier<Long>, mapper: Function<in T, Long>, filter: Predicate<Long>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToLongThenFilterThenFindFirst(defaultValue: Long, mapper: ObservableValue<Function<in T, Long>>, filter: ObservableValue<Predicate<Long>>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToLongThenFilterThenFindFirst(supplier: Supplier<Long>, mapper: ObservableValue<Function<in T, Long>>, filter: ObservableValue<Predicate<Long>>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToFloatThenFilterThenFindFirst(defaultValue: Float, mapper: Function<in T, Float>, filter: Predicate<Float>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToFloatThenFilterThenFindFirst(supplier: Supplier<Float>, mapper: Function<in T, Float>, filter: Predicate<Float>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToFloatThenFilterThenFindFirst(defaultValue: Float, mapper: ObservableValue<Function<in T, Float>>, filter: ObservableValue<Predicate<Float>>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToFloatThenFilterThenFindFirst(supplier: Supplier<Float>, mapper: ObservableValue<Function<in T, Float>>, filter: ObservableValue<Predicate<Float>>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToDoubleThenFilterThenFindFirst(defaultValue: Double, mapper: Function<in T, Double>, filter: Predicate<Double>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToDoubleThenFilterThenFindFirst(supplier: Supplier<Double>, mapper: Function<in T, Double>, filter: Predicate<Double>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToDoubleThenFilterThenFindFirst(defaultValue: Double, mapper: ObservableValue<Function<in T, Double>>, filter: ObservableValue<Predicate<Double>>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToDoubleThenFilterThenFindFirst(supplier: Supplier<Double>, mapper: ObservableValue<Function<in T, Double>>, filter: ObservableValue<Predicate<Double>>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToStringThenFilterThenFindFirst(defaultValue: String, mapper: Function<in T, String>, filter: Predicate<String>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToStringThenFilterThenFindFirst(supplier: Supplier<String>, mapper: Function<in T, String>, filter: Predicate<String>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T> ObservableSet<T>.mapToStringThenFilterThenFindFirst(defaultValue: String, mapper: ObservableValue<Function<in T, String>>, filter: ObservableValue<Predicate<String>>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <T> ObservableSet<T>.mapToStringThenFilterThenFindFirst(supplier: Supplier<String>, mapper: ObservableValue<Function<in T, String>>, filter: ObservableValue<Predicate<String>>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V, R> ObservableMap<K, V>.mapThenFilterThenFindFirst(defaultValue: R, mapper: Function<in V, R>, filter: Predicate<in R>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V, R> ObservableMap<K, V>.mapThenFilterThenFindFirst(supplier: Supplier<R>, mapper: Function<in V, R>, filter: Predicate<in R>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V, R> ObservableMap<K, V>.mapThenFilterThenFindFirst(defaultValue: R, mapper: ObservableValue<Function<in V, R>>, filter: ObservableValue<Predicate<in R>>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V, R> ObservableMap<K, V>.mapThenFilterThenFindFirst(supplier: Supplier<R>, mapper: ObservableValue<Function<in V, R>>, filter: ObservableValue<Predicate<in R>>): ObjectBinding<R> {
    return FilteringBindings.mapThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenFilterThenFindFirst(defaultValue: Boolean, mapper: Function<in V, Boolean>, filter: Predicate<Boolean>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenFilterThenFindFirst(supplier: Supplier<Boolean>, mapper: Function<in V, Boolean>, filter: Predicate<Boolean>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenFilterThenFindFirst(defaultValue: Boolean, mapper: ObservableValue<Function<in V, Boolean>>, filter: ObservableValue<Predicate<Boolean>>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenFilterThenFindFirst(supplier: Supplier<Boolean>, mapper: ObservableValue<Function<in V, Boolean>>, filter: ObservableValue<Predicate<Boolean>>): BooleanBinding {
    return FilteringBindings.mapToBooleanThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenFilterThenFindFirst(defaultValue: Int, mapper: Function<in V, Int>, filter: Predicate<Int>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenFilterThenFindFirst(supplier: Supplier<Int>, mapper: Function<in V, Int>, filter: Predicate<Int>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenFilterThenFindFirst(defaultValue: Int, mapper: ObservableValue<Function<in V, Int>>, filter: ObservableValue<Predicate<Int>>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenFilterThenFindFirst(supplier: Supplier<Int>, mapper: ObservableValue<Function<in V, Int>>, filter: ObservableValue<Predicate<Int>>): IntegerBinding {
    return FilteringBindings.mapToIntegerThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenFilterThenFindFirst(defaultValue: Long, mapper: Function<in V, Long>, filter: Predicate<Long>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenFilterThenFindFirst(supplier: Supplier<Long>, mapper: Function<in V, Long>, filter: Predicate<Long>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenFilterThenFindFirst(defaultValue: Long, mapper: ObservableValue<Function<in V, Long>>, filter: ObservableValue<Predicate<Long>>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenFilterThenFindFirst(supplier: Supplier<Long>, mapper: ObservableValue<Function<in V, Long>>, filter: ObservableValue<Predicate<Long>>): LongBinding {
    return FilteringBindings.mapToLongThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenFilterThenFindFirst(defaultValue: Float, mapper: Function<in V, Float>, filter: Predicate<Float>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenFilterThenFindFirst(supplier: Supplier<Float>, mapper: Function<in V, Float>, filter: Predicate<Float>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenFilterThenFindFirst(defaultValue: Float, mapper: ObservableValue<Function<in V, Float>>, filter: ObservableValue<Predicate<Float>>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenFilterThenFindFirst(supplier: Supplier<Float>, mapper: ObservableValue<Function<in V, Float>>, filter: ObservableValue<Predicate<Float>>): FloatBinding {
    return FilteringBindings.mapToFloatThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenFilterThenFindFirst(defaultValue: Double, mapper: Function<in V, Double>, filter: Predicate<Double>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenFilterThenFindFirst(supplier: Supplier<Double>, mapper: Function<in V, Double>, filter: Predicate<Double>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenFilterThenFindFirst(defaultValue: Double, mapper: ObservableValue<Function<in V, Double>>, filter: ObservableValue<Predicate<Double>>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenFilterThenFindFirst(supplier: Supplier<Double>, mapper: ObservableValue<Function<in V, Double>>, filter: ObservableValue<Predicate<Double>>): DoubleBinding {
    return FilteringBindings.mapToDoubleThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenFilterThenFindFirst(defaultValue: String, mapper: Function<in V, String>, filter: Predicate<String>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenFilterThenFindFirst(supplier: Supplier<String>, mapper: Function<in V, String>, filter: Predicate<String>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenFilterThenFindFirst(defaultValue: String, mapper: ObservableValue<Function<in V, String>>, filter: ObservableValue<Predicate<String>>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, defaultValue, mapper, filter)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenFilterThenFindFirst(supplier: Supplier<String>, mapper: ObservableValue<Function<in V, String>>, filter: ObservableValue<Predicate<String>>): StringBinding {
    return FilteringBindings.mapToStringThenFilterThenFindFirst(this, supplier, mapper, filter)
}

fun <T, R> ObservableList<T>.filterThenMapThenFindFirst(defaultValue: R, filter: Predicate<in T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T, R> ObservableList<T>.filterThenMapThenFindFirst(supplier: Supplier<R>, filter: Predicate<in T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, supplier, filter, mapper)
}

fun <T, R> ObservableList<T>.filterThenMapThenFindFirst(defaultValue: R, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T, R> ObservableList<T>.filterThenMapThenFindFirst(supplier: Supplier<R>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToBooleanThenFindFirst(defaultValue: Boolean, filter: Predicate<in T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToBooleanThenFindFirst(supplier: Supplier<Boolean>, filter: Predicate<in T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToBooleanThenFindFirst(defaultValue: Boolean, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToBooleanThenFindFirst(supplier: Supplier<Boolean>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToIntegerThenFindFirst(defaultValue: Int, filter: Predicate<in T>, mapper: Function<in T, Int>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToIntegerThenFindFirst(supplier: Supplier<Int>, filter: Predicate<in T>, mapper: Function<in T, Int>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToIntegerThenFindFirst(defaultValue: Int, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToIntegerThenFindFirst(supplier: Supplier<Int>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToLongThenFindFirst(defaultValue: Long, filter: Predicate<in T>, mapper: Function<in T, Long>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToLongThenFindFirst(supplier: Supplier<Long>, filter: Predicate<in T>, mapper: Function<in T, Long>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToLongThenFindFirst(defaultValue: Long, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToLongThenFindFirst(supplier: Supplier<Long>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToFloatThenFindFirst(defaultValue: Float, filter: Predicate<in T>, mapper: Function<in T, Float>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToFloatThenFindFirst(supplier: Supplier<Float>, filter: Predicate<in T>, mapper: Function<in T, Float>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToFloatThenFindFirst(defaultValue: Float, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToFloatThenFindFirst(supplier: Supplier<Float>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToDoubleThenFindFirst(defaultValue: Double, filter: Predicate<in T>, mapper: Function<in T, Double>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToDoubleThenFindFirst(supplier: Supplier<Double>, filter: Predicate<in T>, mapper: Function<in T, Double>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToDoubleThenFindFirst(defaultValue: Double, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToDoubleThenFindFirst(supplier: Supplier<Double>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToStringThenFindFirst(defaultValue: String, filter: Predicate<in T>, mapper: Function<in T, String>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToStringThenFindFirst(supplier: Supplier<String>, filter: Predicate<in T>, mapper: Function<in T, String>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToStringThenFindFirst(defaultValue: String, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableList<T>.filterThenMapToStringThenFindFirst(supplier: Supplier<String>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, supplier, filter, mapper)
}

fun <T, R> ObservableSet<T>.filterThenMapThenFindFirst(defaultValue: R, filter: Predicate<in T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T, R> ObservableSet<T>.filterThenMapThenFindFirst(supplier: Supplier<R>, filter: Predicate<in T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, supplier, filter, mapper)
}

fun <T, R> ObservableSet<T>.filterThenMapThenFindFirst(defaultValue: R, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T, R> ObservableSet<T>.filterThenMapThenFindFirst(supplier: Supplier<R>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToBooleanThenFindFirst(defaultValue: Boolean, filter: Predicate<in T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToBooleanThenFindFirst(supplier: Supplier<Boolean>, filter: Predicate<in T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToBooleanThenFindFirst(defaultValue: Boolean, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToBooleanThenFindFirst(supplier: Supplier<Boolean>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToIntegerThenFindFirst(defaultValue: Int, filter: Predicate<in T>, mapper: Function<in T, Int>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToIntegerThenFindFirst(supplier: Supplier<Int>, filter: Predicate<in T>, mapper: Function<in T, Int>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToIntegerThenFindFirst(defaultValue: Int, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToIntegerThenFindFirst(supplier: Supplier<Int>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToLongThenFindFirst(defaultValue: Long, filter: Predicate<in T>, mapper: Function<in T, Long>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToLongThenFindFirst(supplier: Supplier<Long>, filter: Predicate<in T>, mapper: Function<in T, Long>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToLongThenFindFirst(defaultValue: Long, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToLongThenFindFirst(supplier: Supplier<Long>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToFloatThenFindFirst(defaultValue: Float, filter: Predicate<in T>, mapper: Function<in T, Float>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToFloatThenFindFirst(supplier: Supplier<Float>, filter: Predicate<in T>, mapper: Function<in T, Float>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToFloatThenFindFirst(defaultValue: Float, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToFloatThenFindFirst(supplier: Supplier<Float>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToDoubleThenFindFirst(defaultValue: Double, filter: Predicate<in T>, mapper: Function<in T, Double>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToDoubleThenFindFirst(supplier: Supplier<Double>, filter: Predicate<in T>, mapper: Function<in T, Double>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToDoubleThenFindFirst(defaultValue: Double, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToDoubleThenFindFirst(supplier: Supplier<Double>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToStringThenFindFirst(defaultValue: String, filter: Predicate<in T>, mapper: Function<in T, String>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToStringThenFindFirst(supplier: Supplier<String>, filter: Predicate<in T>, mapper: Function<in T, String>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, supplier, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToStringThenFindFirst(defaultValue: String, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, defaultValue, filter, mapper)
}

fun <T> ObservableSet<T>.filterThenMapToStringThenFindFirst(supplier: Supplier<String>, filter: ObservableValue<Predicate<in T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V, R> ObservableMap<K, V>.filterThenMapThenFindFirst(defaultValue: R, filter: Predicate<in V>, mapper: Function<in V, R>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V, R> ObservableMap<K, V>.filterThenMapThenFindFirst(supplier: Supplier<R>, filter: Predicate<in V>, mapper: Function<in V, R>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V, R> ObservableMap<K, V>.filterThenMapThenFindFirst(defaultValue: R, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, R>>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V, R> ObservableMap<K, V>.filterThenMapThenFindFirst(supplier: Supplier<R>, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, R>>): ObjectBinding<R> {
    return FilteringBindings.filterThenMapThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToBooleanThenFindFirst(defaultValue: Boolean, filter: Predicate<in V>, mapper: Function<in V, Boolean>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToBooleanThenFindFirst(supplier: Supplier<Boolean>, filter: Predicate<in V>, mapper: Function<in V, Boolean>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToBooleanThenFindFirst(defaultValue: Boolean, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToBooleanThenFindFirst(supplier: Supplier<Boolean>, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Boolean>>): BooleanBinding {
    return FilteringBindings.filterThenMapToBooleanThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToIntegerThenFindFirst(defaultValue: Int, filter: Predicate<in V>, mapper: Function<in V, Int>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToIntegerThenFindFirst(supplier: Supplier<Int>, filter: Predicate<in V>, mapper: Function<in V, Int>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToIntegerThenFindFirst(defaultValue: Int, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Int>>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToIntegerThenFindFirst(supplier: Supplier<Int>, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Int>>): IntegerBinding {
    return FilteringBindings.filterThenMapToIntegerThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToLongThenFindFirst(defaultValue: Long, filter: Predicate<in V>, mapper: Function<in V, Long>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToLongThenFindFirst(supplier: Supplier<Long>, filter: Predicate<in V>, mapper: Function<in V, Long>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToLongThenFindFirst(defaultValue: Long, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Long>>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToLongThenFindFirst(supplier: Supplier<Long>, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Long>>): LongBinding {
    return FilteringBindings.filterThenMapToLongThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToFloatThenFindFirst(defaultValue: Float, filter: Predicate<in V>, mapper: Function<in V, Float>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToFloatThenFindFirst(supplier: Supplier<Float>, filter: Predicate<in V>, mapper: Function<in V, Float>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToFloatThenFindFirst(defaultValue: Float, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Float>>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToFloatThenFindFirst(supplier: Supplier<Float>, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Float>>): FloatBinding {
    return FilteringBindings.filterThenMapToFloatThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToDoubleThenFindFirst(defaultValue: Double, filter: Predicate<in V>, mapper: Function<in V, Double>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToDoubleThenFindFirst(supplier: Supplier<Double>, filter: Predicate<in V>, mapper: Function<in V, Double>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToDoubleThenFindFirst(defaultValue: Double, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Double>>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToDoubleThenFindFirst(supplier: Supplier<Double>, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, Double>>): DoubleBinding {
    return FilteringBindings.filterThenMapToDoubleThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToStringThenFindFirst(defaultValue: String, filter: Predicate<in V>, mapper: Function<in V, String>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToStringThenFindFirst(supplier: Supplier<String>, filter: Predicate<in V>, mapper: Function<in V, String>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, supplier, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToStringThenFindFirst(defaultValue: String, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, String>>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, defaultValue, filter, mapper)
}

fun <K, V> ObservableMap<K, V>.filterThenMapToStringThenFindFirst(supplier: Supplier<String>, filter: ObservableValue<Predicate<in V>>, mapper: ObservableValue<Function<in V, String>>): StringBinding {
    return FilteringBindings.filterThenMapToStringThenFindFirst(this, supplier, filter, mapper)
}
