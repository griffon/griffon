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
package griffon.javafx.collections.reduce

import griffon.javafx.beans.binding.ReducingBindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.FloatBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.LongBinding
import javafx.beans.binding.NumberBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author Andres Almiray
 * @since 2.13.0
 */

fun <K, V> ObservableMap<K, V>.reduce(defaultValue: V?, reducer: BinaryOperator<V>): ObjectBinding<V> {
    return ReducingBindings.reduce(this, defaultValue, reducer)
}

fun <K, V> ObservableMap<K, V>.reduce(supplier: Supplier<V>, reducer: BinaryOperator<V>): ObjectBinding<V> {
    return ReducingBindings.reduce(this, supplier, reducer)
}

fun <K, V> ObservableMap<K, V>.reduce(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>): ObjectBinding<V> {
    return ReducingBindings.reduce(this, defaultValue, reducer)
}

fun <K, V> ObservableMap<K, V>.reduce(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>): ObjectBinding<V> {
    return ReducingBindings.reduce(this, supplier, reducer)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToBoolean(defaultValue: V?, reducer: BinaryOperator<V>, mapper: Function<in V, Boolean>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToBoolean(supplier: Supplier<V>, reducer: BinaryOperator<V>, mapper: Function<in V, Boolean>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToBoolean(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Boolean>>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToBoolean(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Boolean>>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToInteger(defaultValue: V?, reducer: BinaryOperator<V>, mapper: Function<in V, Int>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToInteger(supplier: Supplier<V>, reducer: BinaryOperator<V>, mapper: Function<in V, Int>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToInteger(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Int>>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToInteger(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Int>>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToLong(defaultValue: V?, reducer: BinaryOperator<V>, mapper: Function<in V, Long>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToLong(supplier: Supplier<V>, reducer: BinaryOperator<V>, mapper: Function<in V, Long>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToLong(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Long>>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToLong(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Long>>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToFloat(defaultValue: V?, reducer: BinaryOperator<V>, mapper: Function<in V, Float>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToFloat(supplier: Supplier<V>, reducer: BinaryOperator<V>, mapper: Function<in V, Float>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToFloat(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Float>>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToFloat(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Float>>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToDouble(defaultValue: V?, reducer: BinaryOperator<V>, mapper: Function<in V, Double>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToDouble(supplier: Supplier<V>, reducer: BinaryOperator<V>, mapper: Function<in V, Double>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToDouble(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Double>>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToDouble(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, Double>>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToString(defaultValue: V?, reducer: BinaryOperator<V>, mapper: Function<in V, String>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToString(supplier: Supplier<V>, reducer: BinaryOperator<V>, mapper: Function<in V, String>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, supplier, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToString(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, String>>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, defaultValue, reducer, mapper)
}

fun <K, V> ObservableMap<K, V>.reduceThenMapToString(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, String>>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduce(defaultValue: T?, reducer: BinaryOperator<T>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, defaultValue, reducer)
}

fun <T> ObservableList<T>.reduce(supplier: Supplier<T>, reducer: BinaryOperator<T>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, supplier, reducer)
}

fun <T> ObservableList<T>.reduce(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, defaultValue, reducer)
}

fun <T> ObservableList<T>.reduce(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, supplier, reducer)
}

fun <T> ObservableList<T>.reduceThenMapToString(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, String>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToString(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, String>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToString(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToString(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToInteger(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Int>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToInteger(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Int>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToInteger(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToInteger(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToLong(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Long>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToLong(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Long>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToLong(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToLong(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToFloat(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Float>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToFloat(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Float>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToFloat(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToFloat(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToDouble(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Double>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToDouble(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Double>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToDouble(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToDouble(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToBoolean(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToBoolean(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToBoolean(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToBoolean(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToNumber(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, out Number>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToNumber(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Number>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, supplier, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToNumber(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, out Number>>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, defaultValue, reducer, mapper)
}

fun <T> ObservableList<T>.reduceThenMapToNumber(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Number>>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduce(defaultValue: T?, reducer: BinaryOperator<T>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, defaultValue, reducer)
}

fun <T> ObservableSet<T>.reduce(supplier: Supplier<T>, reducer: BinaryOperator<T>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, supplier, reducer)
}

fun <T> ObservableSet<T>.reduce(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, defaultValue, reducer)
}

fun <T> ObservableSet<T>.reduce(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>): ObjectBinding<T> {
    return ReducingBindings.reduce(this, supplier, reducer)
}

fun <T> ObservableSet<T>.reduceThenMapToString(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, String>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToString(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, String>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToString(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToString(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, String>>): StringBinding {
    return ReducingBindings.reduceThenMapToString(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToInteger(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Int>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToInteger(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Int>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToInteger(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToInteger(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Int>>): IntegerBinding {
    return ReducingBindings.reduceThenMapToInteger(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToLong(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Long>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToLong(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Long>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToLong(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToLong(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Long>>): LongBinding {
    return ReducingBindings.reduceThenMapToLong(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToFloat(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Float>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToFloat(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Float>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToFloat(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToFloat(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Float>>): FloatBinding {
    return ReducingBindings.reduceThenMapToFloat(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToDouble(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Double>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToDouble(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Double>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToDouble(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToDouble(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Double>>): DoubleBinding {
    return ReducingBindings.reduceThenMapToDouble(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToBoolean(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToBoolean(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Boolean>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToBoolean(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToBoolean(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Boolean>>): BooleanBinding {
    return ReducingBindings.reduceThenMapToBoolean(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToNumber(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, out Number>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToNumber(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, Number>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, supplier, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToNumber(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, out Number>>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, defaultValue, reducer, mapper)
}

fun <T> ObservableSet<T>.reduceThenMapToNumber(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, Number>>): NumberBinding {
    return ReducingBindings.reduceThenMapToNumber(this, supplier, reducer, mapper)
}

fun <T, R> ObservableList<T>.reduceThenMap(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, defaultValue, reducer, mapper)
}

fun <T, R> ObservableList<T>.reduceThenMap(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, supplier, reducer, mapper)
}

fun <T, R> ObservableList<T>.reduceThenMap(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, defaultValue, reducer, mapper)
}

fun <T, R> ObservableList<T>.reduceThenMap(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, supplier, reducer, mapper)
}

fun <T, R> ObservableSet<T>.reduceThenMap(defaultValue: T?, reducer: BinaryOperator<T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, defaultValue, reducer, mapper)
}

fun <T, R> ObservableSet<T>.reduceThenMap(supplier: Supplier<T>, reducer: BinaryOperator<T>, mapper: Function<in T, R>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, supplier, reducer, mapper)
}

fun <T, R> ObservableSet<T>.reduceThenMap(defaultValue: T?, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, defaultValue, reducer, mapper)
}

fun <T, R> ObservableSet<T>.reduceThenMap(supplier: Supplier<T>, reducer: ObservableValue<BinaryOperator<T>>, mapper: ObservableValue<Function<in T, R>>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, supplier, reducer, mapper)
}

fun <K, V, R> ObservableMap<K, V>.reduceThenMap(defaultValue: V?, reducer: BinaryOperator<V>, mapper: Function<in V, R>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, defaultValue, reducer, mapper)
}

fun <K, V, R> ObservableMap<K, V>.reduceThenMap(supplier: Supplier<V>, reducer: BinaryOperator<V>, mapper: Function<in V, R>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, supplier, reducer, mapper)
}

fun <K, V, R> ObservableMap<K, V>.reduceThenMap(defaultValue: V?, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, R>>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, defaultValue, reducer, mapper)
}

fun <K, V, R> ObservableMap<K, V>.reduceThenMap(supplier: Supplier<V>, reducer: ObservableValue<BinaryOperator<V>>, mapper: ObservableValue<Function<in V, R>>): ObjectBinding<R> {
    return ReducingBindings.reduceThenMap(this, supplier, reducer, mapper)
}

fun <T, R> ObservableList<T>.mapThenReduce(defaultValue: R?, mapper: Function<in T, R>, reducer: BinaryOperator<R>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, defaultValue, mapper, reducer)
}

fun <T, R> ObservableList<T>.mapThenReduce(supplier: Supplier<R>, mapper: Function<in T, R>, reducer: BinaryOperator<R>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, supplier, mapper, reducer)
}

fun <T, R> ObservableList<T>.mapThenReduce(defaultValue: R?, mapper: ObservableValue<Function<in T, R>>, reducer: ObservableValue<BinaryOperator<R>>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, defaultValue, mapper, reducer)
}

fun <T, R> ObservableList<T>.mapThenReduce(supplier: Supplier<R>, mapper: ObservableValue<Function<in T, R>>, reducer: ObservableValue<BinaryOperator<R>>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, supplier, mapper, reducer)
}

fun <T, R> ObservableSet<T>.mapThenReduce(defaultValue: R?, mapper: Function<in T, R>, reducer: BinaryOperator<R>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, defaultValue, mapper, reducer)
}

fun <T, R> ObservableSet<T>.mapThenReduce(supplier: Supplier<R>, mapper: Function<in T, R>, reducer: BinaryOperator<R>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, supplier, mapper, reducer)
}

fun <T, R> ObservableSet<T>.mapThenReduce(defaultValue: R?, mapper: ObservableValue<Function<in T, R>>, reducer: ObservableValue<BinaryOperator<R>>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, defaultValue, mapper, reducer)
}

fun <T, R> ObservableSet<T>.mapThenReduce(supplier: Supplier<R>, mapper: ObservableValue<Function<in T, R>>, reducer: ObservableValue<BinaryOperator<R>>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, supplier, mapper, reducer)
}

fun <K, V, R> ObservableMap<K, V>.mapThenReduce(defaultValue: R?, mapper: Function<in V, R>, reducer: BinaryOperator<R>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V, R> ObservableMap<K, V>.mapThenReduce(supplier: Supplier<R>, mapper: Function<in V, R>, reducer: BinaryOperator<R>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, supplier, mapper, reducer)
}

fun <K, V, R> ObservableMap<K, V>.mapThenReduce(defaultValue: R?, mapper: ObservableValue<Function<in V, R>>, reducer: ObservableValue<BinaryOperator<R>>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V, R> ObservableMap<K, V>.mapThenReduce(supplier: Supplier<R>, mapper: ObservableValue<Function<in V, R>>, reducer: ObservableValue<BinaryOperator<R>>): ObjectBinding<R> {
    return ReducingBindings.mapThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToBooleanThenReduce(defaultValue: Boolean?, mapper: Function<in T, Boolean>, reducer: BinaryOperator<Boolean>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToBooleanThenReduce(supplier: Supplier<Boolean>, mapper: Function<in T, Boolean>, reducer: BinaryOperator<Boolean>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToBooleanThenReduce(defaultValue: Boolean?, mapper: ObservableValue<Function<in T, Boolean>>, reducer: ObservableValue<BinaryOperator<Boolean>>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToBooleanThenReduce(supplier: Supplier<Boolean>, mapper: ObservableValue<Function<in T, Boolean>>, reducer: ObservableValue<BinaryOperator<Boolean>>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToBooleanThenReduce(defaultValue: Boolean?, mapper: Function<in T, Boolean>, reducer: BinaryOperator<Boolean>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToBooleanThenReduce(supplier: Supplier<Boolean>, mapper: Function<in T, Boolean>, reducer: BinaryOperator<Boolean>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToBooleanThenReduce(defaultValue: Boolean?, mapper: ObservableValue<Function<in T, Boolean>>, reducer: ObservableValue<BinaryOperator<Boolean>>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToBooleanThenReduce(supplier: Supplier<Boolean>, mapper: ObservableValue<Function<in T, Boolean>>, reducer: ObservableValue<BinaryOperator<Boolean>>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenReduce(defaultValue: Boolean?, mapper: Function<in V, Boolean>, reducer: BinaryOperator<Boolean>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenReduce(supplier: Supplier<Boolean>, mapper: Function<in V, Boolean>, reducer: BinaryOperator<Boolean>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenReduce(defaultValue: Boolean?, mapper: ObservableValue<Function<in V, Boolean>>, reducer: ObservableValue<BinaryOperator<Boolean>>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToBooleanThenReduce(supplier: Supplier<Boolean>, mapper: ObservableValue<Function<in V, Boolean>>, reducer: ObservableValue<BinaryOperator<Boolean>>): BooleanBinding {
    return ReducingBindings.mapToBooleanThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToIntegerThenReduce(defaultValue: Int?, mapper: Function<in T, Int>, reducer: BinaryOperator<Int>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToIntegerThenReduce(supplier: Supplier<Int>, mapper: Function<in T, Int>, reducer: BinaryOperator<Int>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToIntegerThenReduce(defaultValue: Int?, mapper: ObservableValue<Function<in T, Int>>, reducer: ObservableValue<BinaryOperator<Int>>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToIntegerThenReduce(supplier: Supplier<Int>, mapper: ObservableValue<Function<in T, Int>>, reducer: ObservableValue<BinaryOperator<Int>>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToIntegerThenReduce(defaultValue: Int?, mapper: Function<in T, Int>, reducer: BinaryOperator<Int>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToIntegerThenReduce(supplier: Supplier<Int>, mapper: Function<in T, Int>, reducer: BinaryOperator<Int>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToIntegerThenReduce(defaultValue: Int?, mapper: ObservableValue<Function<in T, Int>>, reducer: ObservableValue<BinaryOperator<Int>>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToIntegerThenReduce(supplier: Supplier<Int>, mapper: ObservableValue<Function<in T, Int>>, reducer: ObservableValue<BinaryOperator<Int>>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenReduce(defaultValue: Int?, mapper: Function<in V, Int>, reducer: BinaryOperator<Int>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenReduce(supplier: Supplier<Int>, mapper: Function<in V, Int>, reducer: BinaryOperator<Int>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenReduce(defaultValue: Int?, mapper: ObservableValue<Function<in V, Int>>, reducer: ObservableValue<BinaryOperator<Int>>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToIntegerThenReduce(supplier: Supplier<Int>, mapper: ObservableValue<Function<in V, Int>>, reducer: ObservableValue<BinaryOperator<Int>>): IntegerBinding {
    return ReducingBindings.mapToIntegerThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToLongThenReduce(defaultValue: Long?, mapper: Function<in T, Long>, reducer: BinaryOperator<Long>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToLongThenReduce(supplier: Supplier<Long>, mapper: Function<in T, Long>, reducer: BinaryOperator<Long>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToLongThenReduce(defaultValue: Long?, mapper: ObservableValue<Function<in T, Long>>, reducer: ObservableValue<BinaryOperator<Long>>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToLongThenReduce(supplier: Supplier<Long>, mapper: ObservableValue<Function<in T, Long>>, reducer: ObservableValue<BinaryOperator<Long>>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToLongThenReduce(defaultValue: Long?, mapper: Function<in T, Long>, reducer: BinaryOperator<Long>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToLongThenReduce(supplier: Supplier<Long>, mapper: Function<in T, Long>, reducer: BinaryOperator<Long>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToLongThenReduce(defaultValue: Long?, mapper: ObservableValue<Function<in T, Long>>, reducer: ObservableValue<BinaryOperator<Long>>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToLongThenReduce(supplier: Supplier<Long>, mapper: ObservableValue<Function<in T, Long>>, reducer: ObservableValue<BinaryOperator<Long>>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenReduce(defaultValue: Long?, mapper: Function<in V, Long>, reducer: BinaryOperator<Long>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenReduce(supplier: Supplier<Long>, mapper: Function<in V, Long>, reducer: BinaryOperator<Long>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenReduce(defaultValue: Long?, mapper: ObservableValue<Function<in V, Long>>, reducer: ObservableValue<BinaryOperator<Long>>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToLongThenReduce(supplier: Supplier<Long>, mapper: ObservableValue<Function<in V, Long>>, reducer: ObservableValue<BinaryOperator<Long>>): LongBinding {
    return ReducingBindings.mapToLongThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToFloatThenReduce(defaultValue: Float?, mapper: Function<in T, Float>, reducer: BinaryOperator<Float>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToFloatThenReduce(supplier: Supplier<Float>, mapper: Function<in T, Float>, reducer: BinaryOperator<Float>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToFloatThenReduce(defaultValue: Float?, mapper: ObservableValue<Function<in T, Float>>, reducer: ObservableValue<BinaryOperator<Float>>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToFloatThenReduce(supplier: Supplier<Float>, mapper: ObservableValue<Function<in T, Float>>, reducer: ObservableValue<BinaryOperator<Float>>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToFloatThenReduce(defaultValue: Float?, mapper: Function<in T, Float>, reducer: BinaryOperator<Float>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToFloatThenReduce(supplier: Supplier<Float>, mapper: Function<in T, Float>, reducer: BinaryOperator<Float>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToFloatThenReduce(defaultValue: Float?, mapper: ObservableValue<Function<in T, Float>>, reducer: ObservableValue<BinaryOperator<Float>>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToFloatThenReduce(supplier: Supplier<Float>, mapper: ObservableValue<Function<in T, Float>>, reducer: ObservableValue<BinaryOperator<Float>>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenReduce(defaultValue: Float?, mapper: Function<in V, Float>, reducer: BinaryOperator<Float>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenReduce(supplier: Supplier<Float>, mapper: Function<in V, Float>, reducer: BinaryOperator<Float>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenReduce(defaultValue: Float?, mapper: ObservableValue<Function<in V, Float>>, reducer: ObservableValue<BinaryOperator<Float>>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToFloatThenReduce(supplier: Supplier<Float>, mapper: ObservableValue<Function<in V, Float>>, reducer: ObservableValue<BinaryOperator<Float>>): FloatBinding {
    return ReducingBindings.mapToFloatThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToDoubleThenReduce(defaultValue: Double?, mapper: Function<in T, Double>, reducer: BinaryOperator<Double>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToDoubleThenReduce(supplier: Supplier<Double>, mapper: Function<in T, Double>, reducer: BinaryOperator<Double>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToDoubleThenReduce(defaultValue: Double?, mapper: ObservableValue<Function<in T, Double>>, reducer: ObservableValue<BinaryOperator<Double>>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToDoubleThenReduce(supplier: Supplier<Double>, mapper: ObservableValue<Function<in T, Double>>, reducer: ObservableValue<BinaryOperator<Double>>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToDoubleThenReduce(defaultValue: Double?, mapper: Function<in T, Double>, reducer: BinaryOperator<Double>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToDoubleThenReduce(supplier: Supplier<Double>, mapper: Function<in T, Double>, reducer: BinaryOperator<Double>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToDoubleThenReduce(defaultValue: Double?, mapper: ObservableValue<Function<in T, Double>>, reducer: ObservableValue<BinaryOperator<Double>>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToDoubleThenReduce(supplier: Supplier<Double>, mapper: ObservableValue<Function<in T, Double>>, reducer: ObservableValue<BinaryOperator<Double>>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenReduce(defaultValue: Double?, mapper: Function<in V, Double>, reducer: BinaryOperator<Double>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenReduce(supplier: Supplier<Double>, mapper: Function<in V, Double>, reducer: BinaryOperator<Double>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenReduce(defaultValue: Double?, mapper: ObservableValue<Function<in V, Double>>, reducer: ObservableValue<BinaryOperator<Double>>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToDoubleThenReduce(supplier: Supplier<Double>, mapper: ObservableValue<Function<in V, Double>>, reducer: ObservableValue<BinaryOperator<Double>>): DoubleBinding {
    return ReducingBindings.mapToDoubleThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToNumberThenReduce(defaultValue: Number?, mapper: Function<in T, Number>, reducer: BinaryOperator<Number>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToNumberThenReduce(supplier: Supplier<Number>, mapper: Function<in T, Number>, reducer: BinaryOperator<Number>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToNumberThenReduce(defaultValue: Number?, mapper: ObservableValue<Function<in T, Number>>, reducer: ObservableValue<BinaryOperator<Number>>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToNumberThenReduce(supplier: Supplier<Number>, mapper: ObservableValue<Function<in T, Number>>, reducer: ObservableValue<BinaryOperator<Number>>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToNumberThenReduce(defaultValue: Number?, mapper: Function<in T, Number>, reducer: BinaryOperator<Number>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToNumberThenReduce(supplier: Supplier<Number>, mapper: Function<in T, Number>, reducer: BinaryOperator<Number>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToNumberThenReduce(defaultValue: Number?, mapper: ObservableValue<Function<in T, Number>>, reducer: ObservableValue<BinaryOperator<Number>>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToNumberThenReduce(supplier: Supplier<Number>, mapper: ObservableValue<Function<in T, Number>>, reducer: ObservableValue<BinaryOperator<Number>>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToNumberThenReduce(defaultValue: Number?, mapper: Function<in V, Number>, reducer: BinaryOperator<Number>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToNumberThenReduce(supplier: Supplier<Number>, mapper: Function<in V, Number>, reducer: BinaryOperator<Number>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToNumberThenReduce(defaultValue: Number?, mapper: ObservableValue<Function<in V, Number>>, reducer: ObservableValue<BinaryOperator<Number>>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToNumberThenReduce(supplier: Supplier<Number>, mapper: ObservableValue<Function<in V, Number>>, reducer: ObservableValue<BinaryOperator<Number>>): NumberBinding {
    return ReducingBindings.mapToNumberThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToStringThenReduce(defaultValue: String?, mapper: Function<in T, String>, reducer: BinaryOperator<String>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToStringThenReduce(supplier: Supplier<String>, mapper: Function<in T, String>, reducer: BinaryOperator<String>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableList<T>.mapToStringThenReduce(defaultValue: String?, mapper: ObservableValue<Function<in T, String>>, reducer: ObservableValue<BinaryOperator<String>>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableList<T>.mapToStringThenReduce(supplier: Supplier<String>, mapper: ObservableValue<Function<in T, String>>, reducer: ObservableValue<BinaryOperator<String>>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToStringThenReduce(defaultValue: String?, mapper: Function<in T, String>, reducer: BinaryOperator<String>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToStringThenReduce(supplier: Supplier<String>, mapper: Function<in T, String>, reducer: BinaryOperator<String>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, supplier, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToStringThenReduce(defaultValue: String?, mapper: ObservableValue<Function<in T, String>>, reducer: ObservableValue<BinaryOperator<String>>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, defaultValue, mapper, reducer)
}

fun <T> ObservableSet<T>.mapToStringThenReduce(supplier: Supplier<String>, mapper: ObservableValue<Function<in T, String>>, reducer: ObservableValue<BinaryOperator<String>>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenReduce(defaultValue: String?, mapper: Function<in V, String>, reducer: BinaryOperator<String>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenReduce(supplier: Supplier<String>, mapper: Function<in V, String>, reducer: BinaryOperator<String>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, supplier, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenReduce(defaultValue: String?, mapper: ObservableValue<Function<in V, String>>, reducer: ObservableValue<BinaryOperator<String>>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, defaultValue, mapper, reducer)
}

fun <K, V> ObservableMap<K, V>.mapToStringThenReduce(supplier: Supplier<String>, mapper: ObservableValue<Function<in V, String>>, reducer: ObservableValue<BinaryOperator<String>>): StringBinding {
    return ReducingBindings.mapToStringThenReduce(this, supplier, mapper, reducer)
}
