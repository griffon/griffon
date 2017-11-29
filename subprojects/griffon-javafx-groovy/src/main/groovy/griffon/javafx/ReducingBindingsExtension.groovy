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
package griffon.javafx

import griffon.javafx.beans.binding.ReducingBindings
import groovy.transform.CompileStatic
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

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
@CompileStatic
final class ReducingBindingsExtension {
    @Nonnull
    static <K, V> ObjectBinding<V> reduce(
        @Nonnull ObservableMap<K, V> self, @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer) {
        ReducingBindings.reduce(self, defaultValue, reducer)
    }

    @Nonnull
    static <K, V> ObjectBinding<V> reduce(
        @Nonnull ObservableMap<K, V> self, @Nonnull Supplier<V> supplier, @Nonnull BinaryOperator<V> reducer) {
        ReducingBindings.reduce(self, supplier, reducer)
    }

    @Nonnull
    static <K, V> ObjectBinding<V> reduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull ObservableValue<BinaryOperator<V>> reducer) {
        ReducingBindings.reduce(self, defaultValue, reducer)
    }

    @Nonnull
    static <K, V> ObjectBinding<V> reduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier, @Nonnull ObservableValue<BinaryOperator<V>> reducer) {
        ReducingBindings.reduce(self, supplier, reducer)
    }

    @Nonnull
    static <K, V> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Boolean> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Boolean> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Integer> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Integer> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> LongBinding reduceThenMapToLong(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Long> mapper) {
        ReducingBindings.reduceThenMapToLong(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> LongBinding reduceThenMapToLong(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Long> mapper) {
        ReducingBindings.reduceThenMapToLong(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> LongBinding reduceThenMapToLong(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper) {
        ReducingBindings.reduceThenMapToLong(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> LongBinding reduceThenMapToLong(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper) {
        ReducingBindings.reduceThenMapToLong(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Float> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Float> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Double> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, Double> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> StringBinding reduceThenMapToString(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, String> mapper) {
        ReducingBindings.reduceThenMapToString(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> StringBinding reduceThenMapToString(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, String> mapper) {
        ReducingBindings.reduceThenMapToString(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V> StringBinding reduceThenMapToString(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, String>> mapper) {
        ReducingBindings.reduceThenMapToString(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V> StringBinding reduceThenMapToString(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer,
        @Nonnull ObservableValue<Function<? super V, String>> mapper) {
        ReducingBindings.reduceThenMapToString(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableList<T> self, @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer) {
        ReducingBindings.reduce(self, defaultValue, reducer)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableList<T> self, @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer) {
        ReducingBindings.reduce(self, supplier, reducer)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull ObservableValue<BinaryOperator<T>> reducer) {
        ReducingBindings.reduce(self, defaultValue, reducer)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull ObservableValue<BinaryOperator<T>> reducer) {
        ReducingBindings.reduce(self, supplier, reducer)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, String> mapper) {
        ReducingBindings.reduceThenMapToString(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, String> mapper) {
        ReducingBindings.reduceThenMapToString(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        ReducingBindings.reduceThenMapToString(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        ReducingBindings.reduceThenMapToString(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Integer> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Integer> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Long> mapper) {
        ReducingBindings.reduceThenMapToLong(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Long> mapper) {
        ReducingBindings.reduceThenMapToLong(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        ReducingBindings.reduceThenMapToLong(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        ReducingBindings.reduceThenMapToLong(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Float> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Float> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Double> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Double> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Boolean> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Boolean> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, ? extends Number> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Number> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, ? extends Number>> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Number>> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableSet<T> self, @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer) {
        ReducingBindings.reduce(self, defaultValue, reducer)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableSet<T> self, @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer) {
        ReducingBindings.reduce(self, supplier, reducer)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull ObservableValue<BinaryOperator<T>> reducer) {
        ReducingBindings.reduce(self, defaultValue, reducer)
    }

    @Nonnull
    static <T> ObjectBinding<T> reduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull ObservableValue<BinaryOperator<T>> reducer) {
        ReducingBindings.reduce(self, supplier, reducer)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, String> mapper) {
        ReducingBindings.reduceThenMapToString(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, String> mapper) {
        ReducingBindings.reduceThenMapToString(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        ReducingBindings.reduceThenMapToString(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> StringBinding reduceThenMapToString(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        ReducingBindings.reduceThenMapToString(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Integer> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Integer> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> IntegerBinding reduceThenMapToInteger(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        ReducingBindings.reduceThenMapToInteger(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Long> mapper) {
        ReducingBindings.reduceThenMapToLong(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Long> mapper) {
        ReducingBindings.reduceThenMapToLong(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        ReducingBindings.reduceThenMapToLong(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> LongBinding reduceThenMapToLong(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        ReducingBindings.reduceThenMapToLong(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Float> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Float> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> FloatBinding reduceThenMapToFloat(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        ReducingBindings.reduceThenMapToFloat(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Double> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Double> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> DoubleBinding reduceThenMapToDouble(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        ReducingBindings.reduceThenMapToDouble(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Boolean> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Boolean> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> BooleanBinding reduceThenMapToBoolean(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        ReducingBindings.reduceThenMapToBoolean(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, ? extends Number> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, Number> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, ? extends Number>> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T> NumberBinding reduceThenMapToNumber(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer,
        @Nonnull ObservableValue<Function<? super T, Number>> mapper) {
        ReducingBindings.reduceThenMapToNumber(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, R> mapper) {
        ReducingBindings.reduceThenMap(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, R> mapper) {
        ReducingBindings.reduceThenMap(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableList<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer, @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        ReducingBindings.reduceThenMap(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer, @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        ReducingBindings.reduceThenMap(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, R> mapper) {
        ReducingBindings.reduceThenMap(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> reducer, @Nonnull Function<? super T, R> mapper) {
        ReducingBindings.reduceThenMap(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableSet<T> self,
        @Nullable T defaultValue,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer, @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        ReducingBindings.reduceThenMap(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier,
        @Nonnull ObservableValue<BinaryOperator<T>> reducer, @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        ReducingBindings.reduceThenMap(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, R> mapper) {
        ReducingBindings.reduceThenMap(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier, @Nonnull BinaryOperator<V> reducer, @Nonnull Function<? super V, R> mapper) {
        ReducingBindings.reduceThenMap(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableMap<K, V> self,
        @Nullable V defaultValue,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer, @Nonnull ObservableValue<Function<? super V, R>> mapper) {
        ReducingBindings.reduceThenMap(self, defaultValue, reducer, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> reduceThenMap(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier,
        @Nonnull ObservableValue<BinaryOperator<V>> reducer, @Nonnull ObservableValue<Function<? super V, R>> mapper) {
        ReducingBindings.reduceThenMap(self, supplier, reducer, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable R defaultValue, @Nonnull Function<? super T, R> mapper, @Nonnull BinaryOperator<R> reducer) {
        ReducingBindings.mapThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<R> supplier, @Nonnull Function<? super T, R> mapper, @Nonnull BinaryOperator<R> reducer) {
        ReducingBindings.mapThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable R defaultValue,
        @Nonnull ObservableValue<Function<? super T, R>> mapper, @Nonnull ObservableValue<BinaryOperator<R>> reducer) {
        ReducingBindings.mapThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Function<? super T, R>> mapper, @Nonnull ObservableValue<BinaryOperator<R>> reducer) {
        ReducingBindings.mapThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable R defaultValue, @Nonnull Function<? super T, R> mapper, @Nonnull BinaryOperator<R> reducer) {
        ReducingBindings.mapThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<R> supplier, @Nonnull Function<? super T, R> mapper, @Nonnull BinaryOperator<R> reducer) {
        ReducingBindings.mapThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable R defaultValue,
        @Nonnull ObservableValue<Function<? super T, R>> mapper, @Nonnull ObservableValue<BinaryOperator<R>> reducer) {
        ReducingBindings.mapThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Function<? super T, R>> mapper, @Nonnull ObservableValue<BinaryOperator<R>> reducer) {
        ReducingBindings.mapThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable R defaultValue, @Nonnull Function<? super V, R> mapper, @Nonnull BinaryOperator<R> reducer) {
        ReducingBindings.mapThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<R> supplier, @Nonnull Function<? super V, R> mapper, @Nonnull BinaryOperator<R> reducer) {
        ReducingBindings.mapThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable R defaultValue,
        @Nonnull ObservableValue<Function<? super V, R>> mapper, @Nonnull ObservableValue<BinaryOperator<R>> reducer) {
        ReducingBindings.mapThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Function<? super V, R>> mapper, @Nonnull ObservableValue<BinaryOperator<R>> reducer) {
        ReducingBindings.mapThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Boolean defaultValue,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull BinaryOperator<Boolean> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull BinaryOperator<Boolean> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Boolean defaultValue,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Boolean>> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Boolean>> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Boolean defaultValue,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull BinaryOperator<Boolean> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull BinaryOperator<Boolean> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Boolean defaultValue,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Boolean>> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Boolean>> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Boolean defaultValue,
        @Nonnull Function<? super V, Boolean> mapper, @Nonnull BinaryOperator<Boolean> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Function<? super V, Boolean> mapper, @Nonnull BinaryOperator<Boolean> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Boolean defaultValue,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Boolean>> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Boolean>> reducer) {
        ReducingBindings.mapToBooleanThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Integer defaultValue,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull BinaryOperator<Integer> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull BinaryOperator<Integer> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Integer defaultValue,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Integer>> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Integer>> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Integer defaultValue,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull BinaryOperator<Integer> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull BinaryOperator<Integer> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Integer defaultValue,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Integer>> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Integer>> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Integer defaultValue,
        @Nonnull Function<? super V, Integer> mapper, @Nonnull BinaryOperator<Integer> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Function<? super V, Integer> mapper, @Nonnull BinaryOperator<Integer> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Integer defaultValue,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Integer>> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Integer>> reducer) {
        ReducingBindings.mapToIntegerThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Long defaultValue, @Nonnull Function<? super T, Long> mapper, @Nonnull BinaryOperator<Long> reducer) {
        ReducingBindings.mapToLongThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull Function<? super T, Long> mapper, @Nonnull BinaryOperator<Long> reducer) {
        ReducingBindings.mapToLongThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Long defaultValue,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Long>> reducer) {
        ReducingBindings.mapToLongThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Long>> reducer) {
        ReducingBindings.mapToLongThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Long defaultValue, @Nonnull Function<? super T, Long> mapper, @Nonnull BinaryOperator<Long> reducer) {
        ReducingBindings.mapToLongThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull Function<? super T, Long> mapper, @Nonnull BinaryOperator<Long> reducer) {
        ReducingBindings.mapToLongThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Long defaultValue,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Long>> reducer) {
        ReducingBindings.mapToLongThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Long>> reducer) {
        ReducingBindings.mapToLongThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Long defaultValue, @Nonnull Function<? super V, Long> mapper, @Nonnull BinaryOperator<Long> reducer) {
        ReducingBindings.mapToLongThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull Function<? super V, Long> mapper, @Nonnull BinaryOperator<Long> reducer) {
        ReducingBindings.mapToLongThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Long defaultValue,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Long>> reducer) {
        ReducingBindings.mapToLongThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Long>> reducer) {
        ReducingBindings.mapToLongThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Float defaultValue,
        @Nonnull Function<? super T, Float> mapper, @Nonnull BinaryOperator<Float> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Function<? super T, Float> mapper, @Nonnull BinaryOperator<Float> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Float defaultValue,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Float>> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Float>> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Float defaultValue,
        @Nonnull Function<? super T, Float> mapper, @Nonnull BinaryOperator<Float> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Function<? super T, Float> mapper, @Nonnull BinaryOperator<Float> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Float defaultValue,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Float>> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Float>> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Float defaultValue,
        @Nonnull Function<? super V, Float> mapper, @Nonnull BinaryOperator<Float> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Function<? super V, Float> mapper, @Nonnull BinaryOperator<Float> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Float defaultValue,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Float>> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Float>> reducer) {
        ReducingBindings.mapToFloatThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Double defaultValue,
        @Nonnull Function<? super T, Double> mapper, @Nonnull BinaryOperator<Double> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Function<? super T, Double> mapper, @Nonnull BinaryOperator<Double> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Double defaultValue,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Double>> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Double>> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Double defaultValue,
        @Nonnull Function<? super T, Double> mapper, @Nonnull BinaryOperator<Double> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Function<? super T, Double> mapper, @Nonnull BinaryOperator<Double> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Double defaultValue,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Double>> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Double>> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Double defaultValue,
        @Nonnull Function<? super V, Double> mapper, @Nonnull BinaryOperator<Double> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Function<? super V, Double> mapper, @Nonnull BinaryOperator<Double> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Double defaultValue,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Double>> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Double>> reducer) {
        ReducingBindings.mapToDoubleThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Number defaultValue,
        @Nonnull Function<? super T, Number> mapper, @Nonnull BinaryOperator<Number> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Number> supplier,
        @Nonnull Function<? super T, Number> mapper, @Nonnull BinaryOperator<Number> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable Number defaultValue,
        @Nonnull ObservableValue<Function<? super T, Number>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Number>> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Number> supplier,
        @Nonnull ObservableValue<Function<? super T, Number>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Number>> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Number defaultValue,
        @Nonnull Function<? super T, Number> mapper, @Nonnull BinaryOperator<Number> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Number> supplier,
        @Nonnull Function<? super T, Number> mapper, @Nonnull BinaryOperator<Number> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable Number defaultValue,
        @Nonnull ObservableValue<Function<? super T, Number>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Number>> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Number> supplier,
        @Nonnull ObservableValue<Function<? super T, Number>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Number>> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Number defaultValue,
        @Nonnull Function<? super V, Number> mapper, @Nonnull BinaryOperator<Number> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Number> supplier,
        @Nonnull Function<? super V, Number> mapper, @Nonnull BinaryOperator<Number> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable Number defaultValue,
        @Nonnull ObservableValue<Function<? super V, Number>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Number>> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> NumberBinding mapToNumberThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Number> supplier,
        @Nonnull ObservableValue<Function<? super V, Number>> mapper,
        @Nonnull ObservableValue<BinaryOperator<Number>> reducer) {
        ReducingBindings.mapToNumberThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable String defaultValue,
        @Nonnull Function<? super T, String> mapper, @Nonnull BinaryOperator<String> reducer) {
        ReducingBindings.mapToStringThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Function<? super T, String> mapper, @Nonnull BinaryOperator<String> reducer) {
        ReducingBindings.mapToStringThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableList<T> self,
        @Nullable String defaultValue,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<BinaryOperator<String>> reducer) {
        ReducingBindings.mapToStringThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<BinaryOperator<String>> reducer) {
        ReducingBindings.mapToStringThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable String defaultValue,
        @Nonnull Function<? super T, String> mapper, @Nonnull BinaryOperator<String> reducer) {
        ReducingBindings.mapToStringThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Function<? super T, String> mapper, @Nonnull BinaryOperator<String> reducer) {
        ReducingBindings.mapToStringThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nullable String defaultValue,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<BinaryOperator<String>> reducer) {
        ReducingBindings.mapToStringThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenReduce(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<BinaryOperator<String>> reducer) {
        ReducingBindings.mapToStringThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable String defaultValue,
        @Nonnull Function<? super V, String> mapper, @Nonnull BinaryOperator<String> reducer) {
        ReducingBindings.mapToStringThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Function<? super V, String> mapper, @Nonnull BinaryOperator<String> reducer) {
        ReducingBindings.mapToStringThenReduce(self, supplier, mapper, reducer)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nullable String defaultValue,
        @Nonnull ObservableValue<Function<? super V, String>> mapper,
        @Nonnull ObservableValue<BinaryOperator<String>> reducer) {
        ReducingBindings.mapToStringThenReduce(self, defaultValue, mapper, reducer)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenReduce(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Function<? super V, String>> mapper,
        @Nonnull ObservableValue<BinaryOperator<String>> reducer) {
        ReducingBindings.mapToStringThenReduce(self, supplier, mapper, reducer)
    }
}
