/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package griffon.javafx.mapping

import griffon.javafx.beans.binding.MappingBindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.FloatBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.LongBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableFloatValue
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableLongValue
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author Andres Almiray
 * @since 2.13.0
 */

fun <T, R> ObservableValue<T>.mapAsObject(mapper: Function<T, R>): ObjectBinding<R> {
    return MappingBindings.mapAsObject(this, mapper)
}

fun <T, R> ObservableValue<T>.mapAsObject(mapper: ObservableValue<Function<T, R>>): ObjectBinding<R> {
    return MappingBindings.mapAsObject(this, mapper)
}

fun <T> ObservableValue<T>.mapAsBoolean(mapper: Function<T, Boolean>): BooleanBinding {
    return MappingBindings.mapAsBoolean(this, mapper)
}

fun <T> ObservableValue<T>.mapAsBoolean(mapper: ObservableValue<Function<T, Boolean>>): BooleanBinding {
    return MappingBindings.mapAsBoolean(this, mapper)
}

fun <T> ObservableValue<T>.mapAsInteger(mapper: Function<T, Int>): IntegerBinding {
    return MappingBindings.mapAsInteger(this, mapper)
}

fun <T> ObservableValue<T>.mapAsInteger(mapper: ObservableValue<Function<T, Int>>): IntegerBinding {
    return MappingBindings.mapAsInteger(this, mapper)
}

fun <T> ObservableValue<T>.mapAsLong(mapper: Function<T, Long>): LongBinding {
    return MappingBindings.mapAsLong(this, mapper)
}

fun <T> ObservableValue<T>.mapAsLong(mapper: ObservableValue<Function<T, Long>>): LongBinding {
    return MappingBindings.mapAsLong(this, mapper)
}

fun <T> ObservableValue<T>.mapAsFloat(mapper: Function<T, Float>): FloatBinding {
    return MappingBindings.mapAsFloat(this, mapper)
}

fun <T> ObservableValue<T>.mapAsFloat(mapper: ObservableValue<Function<T, Float>>): FloatBinding {
    return MappingBindings.mapAsFloat(this, mapper)
}

fun <T> ObservableValue<T>.mapAsDouble(mapper: Function<T, Double>): DoubleBinding {
    return MappingBindings.mapAsDouble(this, mapper)
}

fun <T> ObservableValue<T>.mapAsDouble(mapper: ObservableValue<Function<T, Double>>): DoubleBinding {
    return MappingBindings.mapAsDouble(this, mapper)
}

fun <T> ObservableValue<T>.mapAsString(mapper: Function<T, String>): StringBinding {
    return MappingBindings.mapAsString(this, mapper)
}

fun <T> ObservableValue<T>.mapAsString(mapper: ObservableValue<Function<T, String>>): StringBinding {
    return MappingBindings.mapAsString(this, mapper)
}

fun ObservableStringValue.mapToObject(): ObjectBinding<String> {
    return MappingBindings.mapToObject(this)
}

fun ObservableBooleanValue.mapToObject(): ObjectBinding<Boolean> {
    return MappingBindings.mapToObject(this)
}

fun ObservableIntegerValue.mapToObject(): ObjectBinding<Int> {
    return MappingBindings.mapToObject(this)
}

fun ObservableLongValue.mapToObject(): ObjectBinding<Long> {
    return MappingBindings.mapToObject(this)
}

fun ObservableFloatValue.mapToObject(): ObjectBinding<Float> {
    return MappingBindings.mapToObject(this)
}

fun ObservableDoubleValue.mapToObject(): ObjectBinding<Double> {
    return MappingBindings.mapToObject(this)
}

fun ObservableObjectValue<Boolean>.mapToBoolean(): BooleanBinding {
    return MappingBindings.mapToBoolean(this)
}

fun ObservableObjectValue<Int>.mapToInteger(): IntegerBinding {
    return MappingBindings.mapToInteger(this)
}

fun ObservableObjectValue<Long>.mapToLong(): LongBinding {
    return MappingBindings.mapToLong(this)
}

fun ObservableObjectValue<Float>.mapToFloat(): FloatBinding {
    return MappingBindings.mapToFloat(this)
}

fun ObservableObjectValue<Double>.mapToDouble(): DoubleBinding {
    return MappingBindings.mapToDouble(this)
}

fun ObservableObjectValue<String>.mapToString(): StringBinding {
    return MappingBindings.mapToString(this)
}

fun <S, T> ObservableList<in S>.mapList(mapper: Function<S, T>): ObservableList<T> {
    return MappingBindings.mapList(this, mapper)
}

fun <S, T> ObservableList<S>.mapList(mapper: ObservableValue<Function<S, T>>): ObservableList<T> {
    return MappingBindings.mapList(this, mapper)
}

fun <T, R> ObservableValue<T>.mapObject(mapper: Function<in T, out R>): ObjectBinding<R> {
    return MappingBindings.mapObject(this, mapper)
}

fun <T, R> ObservableValue<T>.mapObject(mapper: Function<in T, out R>, defaultValue: R?): ObjectBinding<R> {
    return MappingBindings.mapObject(this, mapper, defaultValue)
}

fun <T, R> ObservableValue<T>.mapObject(mapper: Function<in T, out R>, supplier: Supplier<R>): ObjectBinding<R> {
    return MappingBindings.mapObject(this, mapper, supplier)
}

fun <T, R> ObservableValue<T>.mapObject(mapper: ObservableValue<Function<in T, out R>>): ObjectBinding<R> {
    return MappingBindings.mapObject(this, mapper)
}

fun <T, R> ObservableValue<T>.mapObject(mapper: ObservableValue<Function<in T, out R>>, defaultValue: R?): ObjectBinding<R> {
    return MappingBindings.mapObject(this, mapper, defaultValue)
}

fun <T, R> ObservableValue<T>.mapObject(mapper: ObservableValue<Function<in T, out R>>, supplier: Supplier<R>): ObjectBinding<R> {
    return MappingBindings.mapObject(this, mapper, supplier)
}

fun ObservableValue<Boolean>.mapBoolean(mapper: Function<Boolean, Boolean>): BooleanBinding {
    return MappingBindings.mapBoolean(this, mapper)
}

fun ObservableValue<Boolean>.mapBoolean(mapper: Function<Boolean, Boolean>, defaultValue: Boolean): BooleanBinding {
    return MappingBindings.mapBoolean(this, mapper, defaultValue)
}

fun ObservableValue<Boolean>.mapBoolean(mapper: Function<Boolean, Boolean>, supplier: Supplier<Boolean>): BooleanBinding {
    return MappingBindings.mapBoolean(this, mapper, supplier)
}

fun ObservableValue<Boolean>.mapBoolean(mapper: ObservableValue<Function<Boolean, Boolean>>): BooleanBinding {
    return MappingBindings.mapBoolean(this, mapper)
}

fun ObservableValue<Boolean>.mapBoolean(mapper: ObservableValue<Function<Boolean, Boolean>>, defaultValue: Boolean): BooleanBinding {
    return MappingBindings.mapBoolean(this, mapper, defaultValue)
}

fun ObservableValue<Boolean>.mapBoolean(mapper: ObservableValue<Function<Boolean, Boolean>>, supplier: Supplier<Boolean>): BooleanBinding {
    return MappingBindings.mapBoolean(this, mapper, supplier)
}

fun ObservableValue<Int>.mapInteger(mapper: Function<Int, Int>): IntegerBinding {
    return MappingBindings.mapInteger(this, mapper)
}

fun ObservableValue<Int>.mapInteger(mapper: Function<Int, Int>, defaultValue: Int): IntegerBinding {
    return MappingBindings.mapInteger(this, mapper, defaultValue)
}

fun ObservableValue<Int>.mapInteger(mapper: Function<Int, Int>, supplier: Supplier<Int>): IntegerBinding {
    return MappingBindings.mapInteger(this, mapper, supplier)
}

fun ObservableValue<Int>.mapInteger(mapper: ObservableValue<Function<Int, Int>>): IntegerBinding {
    return MappingBindings.mapInteger(this, mapper)
}

fun ObservableValue<Int>.mapInteger(mapper: ObservableValue<Function<Int, Int>>, defaultValue: Int): IntegerBinding {
    return MappingBindings.mapInteger(this, mapper, defaultValue)
}

fun ObservableValue<Int>.mapInteger(mapper: ObservableValue<Function<Int, Int>>, supplier: Supplier<Int>): IntegerBinding {
    return MappingBindings.mapInteger(this, mapper, supplier)
}

fun ObservableValue<Long>.mapLong(mapper: Function<Long, Long>): LongBinding {
    return MappingBindings.mapLong(this, mapper)
}

fun ObservableValue<Long>.mapLong(mapper: Function<Long, Long>, defaultValue: Long): LongBinding {
    return MappingBindings.mapLong(this, mapper, defaultValue)
}

fun ObservableValue<Long>.mapLong(mapper: Function<Long, Long>, supplier: Supplier<Long>): LongBinding {
    return MappingBindings.mapLong(this, mapper, supplier)
}

fun ObservableValue<Long>.mapLong(mapper: ObservableValue<Function<Long, Long>>): LongBinding {
    return MappingBindings.mapLong(this, mapper)
}

fun ObservableValue<Long>.mapLong(mapper: ObservableValue<Function<Long, Long>>, defaultValue: Long): LongBinding {
    return MappingBindings.mapLong(this, mapper, defaultValue)
}

fun ObservableValue<Long>.mapLong(mapper: ObservableValue<Function<Long, Long>>, supplier: Supplier<Long>): LongBinding {
    return MappingBindings.mapLong(this, mapper, supplier)
}

fun ObservableValue<Float>.mapFloat(mapper: Function<Float, Float>): FloatBinding {
    return MappingBindings.mapFloat(this, mapper)
}

fun ObservableValue<Float>.mapFloat(mapper: Function<Float, Float>, defaultValue: Float): FloatBinding {
    return MappingBindings.mapFloat(this, mapper, defaultValue)
}

fun ObservableValue<Float>.mapFloat(mapper: Function<Float, Float>, supplier: Supplier<Float>): FloatBinding {
    return MappingBindings.mapFloat(this, mapper, supplier)
}

fun ObservableValue<Float>.mapFloat(mapper: ObservableValue<Function<Float, Float>>): FloatBinding {
    return MappingBindings.mapFloat(this, mapper)
}

fun ObservableValue<Float>.mapFloat(mapper: ObservableValue<Function<Float, Float>>, defaultValue: Float?): FloatBinding {
    return MappingBindings.mapFloat(this, mapper, defaultValue)
}

fun ObservableValue<Float>.mapFloat(mapper: ObservableValue<Function<Float, Float>>, supplier: Supplier<Float>): FloatBinding {
    return MappingBindings.mapFloat(this, mapper, supplier)
}

fun ObservableValue<Double>.mapDouble(mapper: Function<Double, Double>): DoubleBinding {
    return MappingBindings.mapDouble(this, mapper)
}

fun ObservableValue<Double>.mapDouble(mapper: Function<Double, Double>, defaultValue: Double?): DoubleBinding {
    return MappingBindings.mapDouble(this, mapper, defaultValue)
}

fun ObservableValue<Double>.mapDouble(mapper: Function<Double, Double>, supplier: Supplier<Double>): DoubleBinding {
    return MappingBindings.mapDouble(this, mapper, supplier)
}

fun ObservableValue<Double>.mapDouble(mapper: ObservableValue<Function<Double, Double>>): DoubleBinding {
    return MappingBindings.mapDouble(this, mapper)
}

fun ObservableValue<Double>.mapDouble(mapper: ObservableValue<Function<Double, Double>>, defaultValue: Double): DoubleBinding {
    return MappingBindings.mapDouble(this, mapper, defaultValue)
}

fun ObservableValue<Double>.mapDouble(mapper: ObservableValue<Function<Double, Double>>, supplier: Supplier<Double>): DoubleBinding {
    return MappingBindings.mapDouble(this, mapper, supplier)
}

fun ObservableValue<String>.mapString(mapper: Function<String, String>): StringBinding {
    return MappingBindings.mapString(this, mapper)
}

fun ObservableValue<String>.mapString(mapper: Function<String, String>, defaultValue: String): StringBinding {
    return MappingBindings.mapString(this, mapper, defaultValue)
}

fun ObservableValue<String>.mapString(mapper: Function<String, String>, supplier: Supplier<String>): StringBinding {
    return MappingBindings.mapString(this, mapper, supplier)
}

fun ObservableValue<String>.mapString(mapper: ObservableValue<Function<String, String>>): StringBinding {
    return MappingBindings.mapString(this, mapper)
}

fun ObservableValue<String>.mapString(mapper: ObservableValue<Function<String, String>>, defaultValue: String): StringBinding {
    return MappingBindings.mapString(this, mapper, defaultValue)
}

fun ObservableValue<String>.mapString(mapper: ObservableValue<Function<String, String>>, supplier: Supplier<String>): StringBinding {
    return MappingBindings.mapString(this, mapper, supplier)
}

fun ObservableValue<Boolean>.mapBooleans(observable: ObservableValue<Boolean>, defaultValue: Boolean, mapper: BiFunction<Boolean, Boolean, Boolean>): BooleanBinding {
    return MappingBindings.mapBooleans(this, observable, defaultValue, mapper)
}

fun ObservableValue<Boolean>.mapBooleans(observable: ObservableValue<Boolean>, supplier: Supplier<Boolean>, mapper: BiFunction<Boolean, Boolean, Boolean>): BooleanBinding {
    return MappingBindings.mapBooleans(this, observable, supplier, mapper)
}

fun ObservableValue<Boolean>.mapBooleans(observable: ObservableValue<Boolean>, defaultValue: Boolean, mapper: ObservableValue<BiFunction<Boolean, Boolean, Boolean>>): BooleanBinding {
    return MappingBindings.mapBooleans(this, observable, defaultValue, mapper)
}

fun ObservableValue<Boolean>.mapBooleans(observable: ObservableValue<Boolean>, supplier: Supplier<Boolean>, mapper: ObservableValue<BiFunction<Boolean, Boolean, Boolean>>): BooleanBinding {
    return MappingBindings.mapBooleans(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapIntegers(observable: ObservableValue<out Number>, defaultValue: Int, mapper: BiFunction<in Number, in Number, Int>): IntegerBinding {
    return MappingBindings.mapIntegers(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapIntegers(observable: ObservableValue<out Number>, supplier: Supplier<Int>, mapper: BiFunction<in Number, in Number, Int>): IntegerBinding {
    return MappingBindings.mapIntegers(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapIntegers(observable: ObservableValue<out Number>, defaultValue: Int, mapper: ObservableValue<BiFunction<in Number, in Number, Int>>): IntegerBinding {
    return MappingBindings.mapIntegers(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapIntegers(observable: ObservableValue<out Number>, supplier: Supplier<Int>, mapper: ObservableValue<BiFunction<in Number, in Number, Int>>): IntegerBinding {
    return MappingBindings.mapIntegers(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapLongs(observable: ObservableValue<out Number>, defaultValue: Long, mapper: BiFunction<in Number, in Number, Long>): LongBinding {
    return MappingBindings.mapLongs(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapLongs(observable: ObservableValue<out Number>, supplier: Supplier<Long>, mapper: BiFunction<in Number, in Number, Long>): LongBinding {
    return MappingBindings.mapLongs(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapLongs(observable: ObservableValue<out Number>, defaultValue: Long, mapper: ObservableValue<BiFunction<in Number, in Number, Long>>): LongBinding {
    return MappingBindings.mapLongs(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapLongs(observable: ObservableValue<out Number>, supplier: Supplier<Long>, mapper: ObservableValue<BiFunction<in Number, in Number, Long>>): LongBinding {
    return MappingBindings.mapLongs(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapFloats(observable: ObservableValue<out Number>, defaultValue: Float, mapper: BiFunction<in Number, in Number, Float>): FloatBinding {
    return MappingBindings.mapFloats(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapFloats(observable: ObservableValue<out Number>, supplier: Supplier<Float>, mapper: BiFunction<in Number, in Number, Float>): FloatBinding {
    return MappingBindings.mapFloats(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapFloats(observable: ObservableValue<out Number>, defaultValue: Float, mapper: ObservableValue<BiFunction<in Number, in Number, Float>>): FloatBinding {
    return MappingBindings.mapFloats(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapFloats(observable: ObservableValue<out Number>, supplier: Supplier<Float>, mapper: ObservableValue<BiFunction<in Number, in Number, Float>>): FloatBinding {
    return MappingBindings.mapFloats(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapDoubles(observable: ObservableValue<out Number>, defaultValue: Double, mapper: BiFunction<in Number, in Number, Double>): DoubleBinding {
    return MappingBindings.mapDoubles(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapDoubles(observable: ObservableValue<out Number>, supplier: Supplier<Double>, mapper: BiFunction<in Number, in Number, Double>): DoubleBinding {
    return MappingBindings.mapDoubles(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapDoubles(observable: ObservableValue<out Number>, defaultValue: Double, mapper: ObservableValue<BiFunction<in Number, in Number, Double>>): DoubleBinding {
    return MappingBindings.mapDoubles(this, observable, defaultValue, mapper)
}

fun ObservableValue<out Number>.mapDoubles(observable: ObservableValue<out Number>, supplier: Supplier<Double>, mapper: ObservableValue<BiFunction<in Number, in Number, Double>>): DoubleBinding {
    return MappingBindings.mapDoubles(this, observable, supplier, mapper)
}

fun <A, B, R> ObservableValue<A>.mapObjects(observable: ObservableValue<B>, defaultValue: R?, mapper: BiFunction<in A, in B, R>): ObjectBinding<R> {
    return MappingBindings.mapObjects(this, observable, defaultValue, mapper)
}

fun <A, B, R> ObservableValue<A>.mapObjects(observable: ObservableValue<B>, supplier: Supplier<R>, mapper: BiFunction<in A, in B, R>): ObjectBinding<R> {
    return MappingBindings.mapObjects(this, observable, supplier, mapper)
}

fun <A, B, R> ObservableValue<A>.mapObjects(observable: ObservableValue<B>, defaultValue: R?, mapper: ObservableValue<BiFunction<in A, in B, R>>): ObjectBinding<R> {
    return MappingBindings.mapObjects(this, observable, defaultValue, mapper)
}

fun <A, B, R> ObservableValue<A>.mapObjects(observable: ObservableValue<B>, supplier: Supplier<R>, mapper: ObservableValue<BiFunction<in A, in B, R>>): ObjectBinding<R> {
    return MappingBindings.mapObjects(this, observable, supplier, mapper)
}

fun ObservableValue<String>.mapStrings(observable: ObservableValue<String>, defaultValue: String?, mapper: BiFunction<String, String, String>): StringBinding {
    return MappingBindings.mapStrings(this, observable, defaultValue, mapper)
}

fun ObservableValue<String>.mapStrings(observable: ObservableValue<String>, supplier: Supplier<String>, mapper: BiFunction<String, String, String>): StringBinding {
    return MappingBindings.mapStrings(this, observable, supplier, mapper)
}

fun ObservableValue<String>.mapStrings(observable: ObservableValue<String>, defaultValue: String?, mapper: ObservableValue<BiFunction<String, String, String>>): StringBinding {
    return MappingBindings.mapStrings(this, observable, defaultValue, mapper)
}

fun ObservableValue<String>.mapStrings(observable: ObservableValue<String>, supplier: Supplier<String>, mapper: ObservableValue<BiFunction<String, String, String>>): StringBinding {
    return MappingBindings.mapStrings(this, observable, supplier, mapper)
}

fun ObservableValue<out Number>.mapInteger(): IntegerBinding {
    return MappingBindings.mapInteger(this)
}

fun ObservableValue<out Number>.mapInteger(defaultValue: Int?): IntegerBinding {
    return MappingBindings.mapInteger(this, defaultValue)
}

fun ObservableValue<out Number>.mapInteger(supplier: Supplier<Int>?): IntegerBinding {
    return MappingBindings.mapInteger(this, supplier)
}

fun ObservableValue<out Number>.mapLong(): LongBinding {
    return MappingBindings.mapLong(this)
}

fun ObservableValue<out Number>.mapLong(defaultValue: Long?): LongBinding {
    return MappingBindings.mapLong(this, defaultValue)
}

fun ObservableValue<out Number>.mapLong(supplier: Supplier<Long>?): LongBinding {
    return MappingBindings.mapLong(this, supplier)
}

fun ObservableValue<out Number>.mapFloat(): FloatBinding {
    return MappingBindings.mapFloat(this)
}

fun ObservableValue<out Number>.mapFloat(defaultValue: Float?): FloatBinding {
    return MappingBindings.mapFloat(this, defaultValue)
}

fun ObservableValue<out Number>.mapFloat(supplier: Supplier<Float>?): FloatBinding {
    return MappingBindings.mapFloat(this, supplier)
}

fun ObservableValue<out Number>.mapDouble(): DoubleBinding {
    return MappingBindings.mapDouble(this)
}

fun ObservableValue<out Number>.mapDouble(defaultValue: Double?): DoubleBinding {
    return MappingBindings.mapDouble(this, defaultValue)
}

fun ObservableValue<out Number>.mapDouble(supplier: Supplier<Double>?): DoubleBinding {
    return MappingBindings.mapDouble(this, supplier)
}
