/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.javafx.groovy

import griffon.annotations.core.Nonnull
import griffon.annotations.core.Nullable
import griffon.javafx.beans.binding.MappingBindings
import groovy.transform.CompileStatic
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
@CompileStatic
final class MappingBindingsExtension {
    @Nonnull
    static <T, R> ObjectBinding<R> mapAsObject(@Nonnull ObservableValue<T> observable, @Nonnull Function<T, R> mapper) {
        MappingBindings.mapAsObject(observable, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapAsObject(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<T, R>> mapper) {
        MappingBindings.mapAsObject(observable, mapper)
    }

    @Nonnull
    static <T> BooleanBinding mapAsBoolean(
        @Nonnull ObservableValue<T> observable, @Nonnull Function<T, Boolean> mapper) {
        MappingBindings.mapAsBoolean(observable, mapper)
    }

    @Nonnull
    static <T> BooleanBinding mapAsBoolean(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<T, Boolean>> mapper) {
        MappingBindings.mapAsBoolean(observable, mapper)
    }

    @Nonnull
    static <T> IntegerBinding mapAsInteger(
        @Nonnull ObservableValue<T> observable, @Nonnull Function<T, Integer> mapper) {
        MappingBindings.mapAsInteger(observable, mapper)
    }

    @Nonnull
    static <T> IntegerBinding mapAsInteger(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<T, Integer>> mapper) {
        MappingBindings.mapAsInteger(observable, mapper)
    }

    @Nonnull
    static <T> LongBinding mapAsLong(@Nonnull ObservableValue<T> observable, @Nonnull Function<T, Long> mapper) {
        MappingBindings.mapAsLong(observable, mapper)
    }

    @Nonnull
    static <T> LongBinding mapAsLong(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<T, Long>> mapper) {
        MappingBindings.mapAsLong(observable, mapper)
    }

    @Nonnull
    static <T> FloatBinding mapAsFloat(@Nonnull ObservableValue<T> observable, @Nonnull Function<T, Float> mapper) {
        MappingBindings.mapAsFloat(observable, mapper)
    }

    @Nonnull
    static <T> FloatBinding mapAsFloat(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<T, Float>> mapper) {
        MappingBindings.mapAsFloat(observable, mapper)
    }

    @Nonnull
    static <T> DoubleBinding mapAsDouble(@Nonnull ObservableValue<T> observable, @Nonnull Function<T, Double> mapper) {
        MappingBindings.mapAsDouble(observable, mapper)
    }

    @Nonnull
    static <T> DoubleBinding mapAsDouble(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<T, Double>> mapper) {
        MappingBindings.mapAsDouble(observable, mapper)
    }

    @Nonnull
    static <T> StringBinding mapAsString(@Nonnull ObservableValue<T> observable, @Nonnull Function<T, String> mapper) {
        MappingBindings.mapAsString(observable, mapper)
    }

    @Nonnull
    static <T> StringBinding mapAsString(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<T, String>> mapper) {
        MappingBindings.mapAsString(observable, mapper)
    }

    @Nonnull
    static ObjectBinding<String> mapToObject(@Nonnull ObservableStringValue observable) {
        MappingBindings.mapToObject(observable)
    }

    @Nonnull
    static ObjectBinding<Boolean> mapToObject(@Nonnull ObservableBooleanValue observable) {
        MappingBindings.mapToObject(observable)
    }

    @Nonnull
    static ObjectBinding<Integer> mapToObject(@Nonnull ObservableIntegerValue observable) {
        MappingBindings.mapToObject(observable)
    }

    @Nonnull
    static ObjectBinding<Long> mapToObject(@Nonnull ObservableLongValue observable) {
        MappingBindings.mapToObject(observable)
    }

    @Nonnull
    static ObjectBinding<Float> mapToObject(@Nonnull ObservableFloatValue observable) {
        MappingBindings.mapToObject(observable)
    }

    @Nonnull
    static ObjectBinding<Double> mapToObject(@Nonnull ObservableDoubleValue observable) {
        MappingBindings.mapToObject(observable)
    }

    @Nonnull
    static BooleanBinding mapToBoolean(@Nonnull ObservableObjectValue<Boolean> observable) {
        MappingBindings.mapToBoolean(observable)
    }

    @Nonnull
    static IntegerBinding mapToInteger(@Nonnull ObservableObjectValue<Integer> observable) {
        MappingBindings.mapToInteger(observable)
    }

    @Nonnull
    static LongBinding mapToLong(@Nonnull ObservableObjectValue<Long> observable) {
        MappingBindings.mapToLong(observable)
    }

    @Nonnull
    static FloatBinding mapToFloat(@Nonnull ObservableObjectValue<Float> observable) {
        MappingBindings.mapToFloat(observable)
    }

    @Nonnull
    static DoubleBinding mapToDouble(@Nonnull ObservableObjectValue<Double> observable) {
        MappingBindings.mapToDouble(observable)
    }

    @Nonnull
    static StringBinding mapToString(@Nonnull ObservableObjectValue<String> observable) {
        MappingBindings.mapToString(observable)
    }

    @Nonnull
    static <S, T> ObservableList<T> mapList(@Nonnull ObservableList<? super S> source, @Nonnull Function<S, T> mapper) {
        MappingBindings.mapList(source, mapper)
    }

    @Nonnull
    static <S, T> ObservableList<T> mapList(
        @Nonnull ObservableList<S> source, @Nonnull ObservableValue<Function<S, T>> mapper) {
        MappingBindings.mapList(source, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapObject(
        @Nonnull ObservableValue<T> observable, @Nonnull Function<? super T, ? extends R> mapper) {
        MappingBindings.mapObject(observable, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapObject(
        @Nonnull ObservableValue<T> observable,
        @Nonnull Function<? super T, ? extends R> mapper, @Nullable R defaultValue) {
        MappingBindings.mapObject(observable, mapper, defaultValue)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapObject(
        @Nonnull ObservableValue<T> observable,
        @Nonnull Function<? super T, ? extends R> mapper, @Nonnull Supplier<R> supplier) {
        MappingBindings.mapObject(observable, mapper, supplier)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapObject(
        @Nonnull ObservableValue<T> observable, @Nonnull ObservableValue<Function<? super T, ? extends R>> mapper) {
        MappingBindings.mapObject(observable, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapObject(
        @Nonnull ObservableValue<T> observable,
        @Nonnull ObservableValue<Function<? super T, ? extends R>> mapper, @Nullable R defaultValue) {
        MappingBindings.mapObject(observable, mapper, defaultValue)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapObject(
        @Nonnull ObservableValue<T> observable,
        @Nonnull ObservableValue<Function<? super T, ? extends R>> mapper, @Nonnull Supplier<R> supplier) {
        MappingBindings.mapObject(observable, mapper, supplier)
    }

    @Nonnull
    static BooleanBinding mapBoolean(
        @Nonnull ObservableValue<Boolean> observable, @Nonnull Function<Boolean, Boolean> mapper) {
        MappingBindings.mapBoolean(observable, mapper)
    }

    @Nonnull
    static BooleanBinding mapBoolean(
        @Nonnull ObservableValue<Boolean> observable,
        @Nonnull Function<Boolean, Boolean> mapper, @Nonnull Boolean defaultValue) {
        MappingBindings.mapBoolean(observable, mapper, defaultValue)
    }

    @Nonnull
    static BooleanBinding mapBoolean(
        @Nonnull ObservableValue<Boolean> observable,
        @Nonnull Function<Boolean, Boolean> mapper, @Nonnull Supplier<Boolean> supplier) {
        MappingBindings.mapBoolean(observable, mapper, supplier)
    }

    @Nonnull
    static BooleanBinding mapBoolean(
        @Nonnull ObservableValue<Boolean> observable, @Nonnull ObservableValue<Function<Boolean, Boolean>> mapper) {
        MappingBindings.mapBoolean(observable, mapper)
    }

    @Nonnull
    static BooleanBinding mapBoolean(
        @Nonnull ObservableValue<Boolean> observable,
        @Nonnull ObservableValue<Function<Boolean, Boolean>> mapper, @Nonnull Boolean defaultValue) {
        MappingBindings.mapBoolean(observable, mapper, defaultValue)
    }

    @Nonnull
    static BooleanBinding mapBoolean(
        @Nonnull ObservableValue<Boolean> observable,
        @Nonnull ObservableValue<Function<Boolean, Boolean>> mapper, @Nonnull Supplier<Boolean> supplier) {
        MappingBindings.mapBoolean(observable, mapper, supplier)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<Integer> observable, @Nonnull Function<Integer, Integer> mapper) {
        MappingBindings.mapInteger(observable, mapper)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<Integer> observable,
        @Nonnull Function<Integer, Integer> mapper, @Nonnull Integer defaultValue) {
        MappingBindings.mapInteger(observable, mapper, defaultValue)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<Integer> observable,
        @Nonnull Function<Integer, Integer> mapper, @Nonnull Supplier<Integer> supplier) {
        MappingBindings.mapInteger(observable, mapper, supplier)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<Integer> observable, @Nonnull ObservableValue<Function<Integer, Integer>> mapper) {
        MappingBindings.mapInteger(observable, mapper)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<Integer> observable,
        @Nonnull ObservableValue<Function<Integer, Integer>> mapper, @Nonnull Integer defaultValue) {
        MappingBindings.mapInteger(observable, mapper, defaultValue)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<Integer> observable,
        @Nonnull ObservableValue<Function<Integer, Integer>> mapper, @Nonnull Supplier<Integer> supplier) {
        MappingBindings.mapInteger(observable, mapper, supplier)
    }

    @Nonnull
    static LongBinding mapLong(@Nonnull ObservableValue<Long> observable, @Nonnull Function<Long, Long> mapper) {
        MappingBindings.mapLong(observable, mapper)
    }

    @Nonnull
    static LongBinding mapLong(
        @Nonnull ObservableValue<Long> observable, @Nonnull Function<Long, Long> mapper, @Nonnull Long defaultValue) {
        MappingBindings.mapLong(observable, mapper, defaultValue)
    }

    @Nonnull
    static LongBinding mapLong(
        @Nonnull ObservableValue<Long> observable,
        @Nonnull Function<Long, Long> mapper, @Nonnull Supplier<Long> supplier) {
        MappingBindings.mapLong(observable, mapper, supplier)
    }

    @Nonnull
    static LongBinding mapLong(
        @Nonnull ObservableValue<Long> observable, @Nonnull ObservableValue<Function<Long, Long>> mapper) {
        MappingBindings.mapLong(observable, mapper)
    }

    @Nonnull
    static LongBinding mapLong(
        @Nonnull ObservableValue<Long> observable,
        @Nonnull ObservableValue<Function<Long, Long>> mapper, @Nonnull Long defaultValue) {
        MappingBindings.mapLong(observable, mapper, defaultValue)
    }

    @Nonnull
    static LongBinding mapLong(
        @Nonnull ObservableValue<Long> observable,
        @Nonnull ObservableValue<Function<Long, Long>> mapper, @Nonnull Supplier<Long> supplier) {
        MappingBindings.mapLong(observable, mapper, supplier)
    }

    @Nonnull
    static FloatBinding mapFloat(@Nonnull ObservableValue<Float> observable, @Nonnull Function<Float, Float> mapper) {
        MappingBindings.mapFloat(observable, mapper)
    }

    @Nonnull
    static FloatBinding mapFloat(
        @Nonnull ObservableValue<Float> observable,
        @Nonnull Function<Float, Float> mapper, @Nonnull Float defaultValue) {
        MappingBindings.mapFloat(observable, mapper, defaultValue)
    }

    @Nonnull
    static FloatBinding mapFloat(
        @Nonnull ObservableValue<Float> observable,
        @Nonnull Function<Float, Float> mapper, @Nonnull Supplier<Float> supplier) {
        MappingBindings.mapFloat(observable, mapper, supplier)
    }

    @Nonnull
    static FloatBinding mapFloat(
        @Nonnull ObservableValue<Float> observable, @Nonnull ObservableValue<Function<Float, Float>> mapper) {
        MappingBindings.mapFloat(observable, mapper)
    }

    @Nonnull
    static FloatBinding mapFloat(
        @Nonnull ObservableValue<Float> observable,
        @Nonnull ObservableValue<Function<Float, Float>> mapper, @Nullable Float defaultValue) {
        MappingBindings.mapFloat(observable, mapper, defaultValue)
    }

    @Nonnull
    static FloatBinding mapFloat(
        @Nonnull ObservableValue<Float> observable,
        @Nonnull ObservableValue<Function<Float, Float>> mapper, @Nonnull Supplier<Float> supplier) {
        MappingBindings.mapFloat(observable, mapper, supplier)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<Double> observable, @Nonnull Function<Double, Double> mapper) {
        MappingBindings.mapDouble(observable, mapper)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<Double> observable,
        @Nonnull Function<Double, Double> mapper, @Nullable Double defaultValue) {
        MappingBindings.mapDouble(observable, mapper, defaultValue)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<Double> observable,
        @Nonnull Function<Double, Double> mapper, @Nonnull Supplier<Double> supplier) {
        MappingBindings.mapDouble(observable, mapper, supplier)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<Double> observable, @Nonnull ObservableValue<Function<Double, Double>> mapper) {
        MappingBindings.mapDouble(observable, mapper)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<Double> observable,
        @Nonnull ObservableValue<Function<Double, Double>> mapper, @Nonnull Double defaultValue) {
        MappingBindings.mapDouble(observable, mapper, defaultValue)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<Double> observable,
        @Nonnull ObservableValue<Function<Double, Double>> mapper, @Nonnull Supplier<Double> supplier) {
        MappingBindings.mapDouble(observable, mapper, supplier)
    }

    @Nonnull
    static StringBinding mapString(
        @Nonnull ObservableValue<String> observable, @Nonnull Function<String, String> mapper) {
        MappingBindings.mapString(observable, mapper)
    }

    @Nonnull
    static StringBinding mapString(
        @Nonnull ObservableValue<String> observable,
        @Nonnull Function<String, String> mapper, @Nonnull String defaultValue) {
        MappingBindings.mapString(observable, mapper, defaultValue)
    }

    @Nonnull
    static StringBinding mapString(
        @Nonnull ObservableValue<String> observable,
        @Nonnull Function<String, String> mapper, @Nonnull Supplier<String> supplier) {
        MappingBindings.mapString(observable, mapper, supplier)
    }

    @Nonnull
    static StringBinding mapString(
        @Nonnull ObservableValue<String> observable, @Nonnull ObservableValue<Function<String, String>> mapper) {
        MappingBindings.mapString(observable, mapper)
    }

    @Nonnull
    static StringBinding mapString(
        @Nonnull ObservableValue<String> observable,
        @Nonnull ObservableValue<Function<String, String>> mapper, @Nonnull String defaultValue) {
        MappingBindings.mapString(observable, mapper, defaultValue)
    }

    @Nonnull
    static StringBinding mapString(
        @Nonnull ObservableValue<String> observable,
        @Nonnull ObservableValue<Function<String, String>> mapper, @Nonnull Supplier<String> supplier) {
        MappingBindings.mapString(observable, mapper, supplier)
    }

    @Nonnull
    static BooleanBinding mapBooleans(
        @Nonnull ObservableValue<Boolean> observable1,
        @Nonnull ObservableValue<Boolean> observable2,
        @Nonnull Boolean defaultValue, @Nonnull BiFunction<Boolean, Boolean, Boolean> mapper) {
        MappingBindings.mapBooleans(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static BooleanBinding mapBooleans(
        @Nonnull ObservableValue<Boolean> observable1,
        @Nonnull ObservableValue<Boolean> observable2,
        @Nonnull Supplier<Boolean> supplier, @Nonnull BiFunction<Boolean, Boolean, Boolean> mapper) {
        MappingBindings.mapBooleans(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static BooleanBinding mapBooleans(
        @Nonnull ObservableValue<Boolean> observable1,
        @Nonnull ObservableValue<Boolean> observable2,
        @Nonnull Boolean defaultValue, @Nonnull ObservableValue<BiFunction<Boolean, Boolean, Boolean>> mapper) {
        MappingBindings.mapBooleans(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static BooleanBinding mapBooleans(
        @Nonnull ObservableValue<Boolean> observable1,
        @Nonnull ObservableValue<Boolean> observable2,
        @Nonnull Supplier<Boolean> supplier, @Nonnull ObservableValue<BiFunction<Boolean, Boolean, Boolean>> mapper) {
        MappingBindings.mapBooleans(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static IntegerBinding mapIntegers(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Integer defaultValue, @Nonnull BiFunction<? super Number, ? super Number, Integer> mapper) {
        MappingBindings.mapIntegers(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static IntegerBinding mapIntegers(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Integer> supplier, @Nonnull BiFunction<? super Number, ? super Number, Integer> mapper) {
        MappingBindings.mapIntegers(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static IntegerBinding mapIntegers(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Integer defaultValue,
        @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Integer>> mapper) {
        MappingBindings.mapIntegers(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static IntegerBinding mapIntegers(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Integer>> mapper) {
        MappingBindings.mapIntegers(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static LongBinding mapLongs(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Long defaultValue, @Nonnull BiFunction<? super Number, ? super Number, Long> mapper) {
        MappingBindings.mapLongs(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static LongBinding mapLongs(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Long> supplier, @Nonnull BiFunction<? super Number, ? super Number, Long> mapper) {
        MappingBindings.mapLongs(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static LongBinding mapLongs(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Long defaultValue, @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Long>> mapper) {
        MappingBindings.mapLongs(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static LongBinding mapLongs(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Long>> mapper) {
        MappingBindings.mapLongs(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static FloatBinding mapFloats(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Float defaultValue, @Nonnull BiFunction<? super Number, ? super Number, Float> mapper) {
        MappingBindings.mapFloats(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static FloatBinding mapFloats(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Float> supplier, @Nonnull BiFunction<? super Number, ? super Number, Float> mapper) {
        MappingBindings.mapFloats(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static FloatBinding mapFloats(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Float defaultValue,
        @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Float>> mapper) {
        MappingBindings.mapFloats(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static FloatBinding mapFloats(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Float>> mapper) {
        MappingBindings.mapFloats(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static DoubleBinding mapDoubles(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Double defaultValue, @Nonnull BiFunction<? super Number, ? super Number, Double> mapper) {
        MappingBindings.mapDoubles(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static DoubleBinding mapDoubles(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Double> supplier, @Nonnull BiFunction<? super Number, ? super Number, Double> mapper) {
        MappingBindings.mapDoubles(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static DoubleBinding mapDoubles(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Double defaultValue,
        @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Double>> mapper) {
        MappingBindings.mapDoubles(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static DoubleBinding mapDoubles(
        @Nonnull ObservableValue<? extends Number> observable1,
        @Nonnull ObservableValue<? extends Number> observable2,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<BiFunction<? super Number, ? super Number, Double>> mapper) {
        MappingBindings.mapDoubles(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static <A, B, R> ObjectBinding<R> mapObjects(
        @Nonnull ObservableValue<A> observable1,
        @Nonnull ObservableValue<B> observable2,
        @Nullable R defaultValue, @Nonnull BiFunction<? super A, ? super B, R> mapper) {
        MappingBindings.mapObjects(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static <A, B, R> ObjectBinding<R> mapObjects(
        @Nonnull ObservableValue<A> observable1,
        @Nonnull ObservableValue<B> observable2,
        @Nonnull Supplier<R> supplier, @Nonnull BiFunction<? super A, ? super B, R> mapper) {
        MappingBindings.mapObjects(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static <A, B, R> ObjectBinding<R> mapObjects(
        @Nonnull ObservableValue<A> observable1,
        @Nonnull ObservableValue<B> observable2,
        @Nullable R defaultValue, @Nonnull ObservableValue<BiFunction<? super A, ? super B, R>> mapper) {
        MappingBindings.mapObjects(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static <A, B, R> ObjectBinding<R> mapObjects(
        @Nonnull ObservableValue<A> observable1,
        @Nonnull ObservableValue<B> observable2,
        @Nonnull Supplier<R> supplier, @Nonnull ObservableValue<BiFunction<? super A, ? super B, R>> mapper) {
        MappingBindings.mapObjects(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static StringBinding mapStrings(
        @Nonnull ObservableValue<String> observable1,
        @Nonnull ObservableValue<String> observable2,
        @Nullable String defaultValue, @Nonnull BiFunction<String, String, String> mapper) {
        MappingBindings.mapStrings(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static StringBinding mapStrings(
        @Nonnull ObservableValue<String> observable1,
        @Nonnull ObservableValue<String> observable2,
        @Nonnull Supplier<String> supplier, @Nonnull BiFunction<String, String, String> mapper) {
        MappingBindings.mapStrings(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static StringBinding mapStrings(
        @Nonnull ObservableValue<String> observable1,
        @Nonnull ObservableValue<String> observable2,
        @Nullable String defaultValue, @Nonnull ObservableValue<BiFunction<String, String, String>> mapper) {
        MappingBindings.mapStrings(observable1, observable2, defaultValue, mapper)
    }

    @Nonnull
    static StringBinding mapStrings(
        @Nonnull ObservableValue<String> observable1,
        @Nonnull ObservableValue<String> observable2,
        @Nonnull Supplier<String> supplier, @Nonnull ObservableValue<BiFunction<String, String, String>> mapper) {
        MappingBindings.mapStrings(observable1, observable2, supplier, mapper)
    }

    @Nonnull
    static IntegerBinding mapInteger(@Nonnull ObservableValue<? extends Number> observable) {
        MappingBindings.mapInteger(observable)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<? extends Number> observable, @Nullable Integer defaultValue) {
        MappingBindings.mapInteger(observable, defaultValue)
    }

    @Nonnull
    static IntegerBinding mapInteger(
        @Nonnull ObservableValue<? extends Number> observable, @Nullable Supplier<Integer> supplier) {
        MappingBindings.mapInteger(observable, supplier)
    }

    @Nonnull
    static LongBinding mapLong(@Nonnull ObservableValue<? extends Number> observable) {
        MappingBindings.mapLong(observable)
    }

    @Nonnull
    static LongBinding mapLong(@Nonnull ObservableValue<? extends Number> observable, @Nullable Long defaultValue) {
        MappingBindings.mapLong(observable, defaultValue)
    }

    @Nonnull
    static LongBinding mapLong(
        @Nonnull ObservableValue<? extends Number> observable, @Nullable Supplier<Long> supplier) {
        MappingBindings.mapLong(observable, supplier)
    }

    @Nonnull
    static FloatBinding mapFloat(@Nonnull ObservableValue<? extends Number> observable) {
        MappingBindings.mapFloat(observable)
    }

    @Nonnull
    static FloatBinding mapFloat(@Nonnull ObservableValue<? extends Number> observable, @Nullable Float defaultValue) {
        MappingBindings.mapFloat(observable, defaultValue)
    }

    @Nonnull
    static FloatBinding mapFloat(
        @Nonnull ObservableValue<? extends Number> observable, @Nullable Supplier<Float> supplier) {
        MappingBindings.mapFloat(observable, supplier)
    }

    @Nonnull
    static DoubleBinding mapDouble(@Nonnull ObservableValue<? extends Number> observable) {
        MappingBindings.mapDouble(observable)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<? extends Number> observable, @Nullable Double defaultValue) {
        MappingBindings.mapDouble(observable, defaultValue)
    }

    @Nonnull
    static DoubleBinding mapDouble(
        @Nonnull ObservableValue<? extends Number> observable, @Nullable Supplier<Double> supplier) {
        MappingBindings.mapDouble(observable, supplier)
    }
}
