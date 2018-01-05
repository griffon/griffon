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

import griffon.javafx.beans.binding.FilteringBindings
import groovy.transform.CompileStatic
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

import javax.annotation.Nonnull
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
@CompileStatic
final class FilteringBindingsExtension {
    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableList<T> self, @Nonnull T defaultValue, @Nonnull Predicate<? super T> filter) {
        FilteringBindings.filterThenFindFirst(self, defaultValue, filter)
    }

    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableList<T> self, @Nonnull Supplier<T> supplier, @Nonnull Predicate<? super T> filter) {
        FilteringBindings.filterThenFindFirst(self, supplier, filter)
    }

    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull T defaultValue, @Nonnull ObservableValue<Predicate<? super T>> filter) {
        FilteringBindings.filterThenFindFirst(self, defaultValue, filter)
    }

    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull ObservableValue<Predicate<? super T>> filter) {
        FilteringBindings.filterThenFindFirst(self, supplier, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableList<Boolean> self,
        @Nonnull Boolean defaultValue, @Nonnull Predicate<? super Boolean> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, defaultValue, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableList<Boolean> self,
        @Nonnull Supplier<Boolean> supplier, @Nonnull Predicate<? super Boolean> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, supplier, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableList<Boolean> self,
        @Nonnull Boolean defaultValue, @Nonnull ObservableValue<Predicate<? super Boolean>> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, defaultValue, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableList<Boolean> self,
        @Nonnull Supplier<Boolean> supplier, @Nonnull ObservableValue<Predicate<? super Boolean>> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, supplier, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableList<Integer> self,
        @Nonnull Integer defaultValue, @Nonnull Predicate<? super Integer> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, defaultValue, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableList<Integer> self,
        @Nonnull Supplier<Integer> supplier, @Nonnull Predicate<? super Integer> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, supplier, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableList<Integer> self,
        @Nonnull Integer defaultValue, @Nonnull ObservableValue<Predicate<? super Integer>> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, defaultValue, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableList<Integer> self,
        @Nonnull Supplier<Integer> supplier, @Nonnull ObservableValue<Predicate<? super Integer>> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, supplier, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableList<Long> self, @Nonnull Long defaultValue, @Nonnull Predicate<? super Long> filter) {
        FilteringBindings.filterThenFindFirstLong(self, defaultValue, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableList<Long> self,
        @Nonnull Supplier<Long> supplier, @Nonnull Predicate<? super Long> filter) {
        FilteringBindings.filterThenFindFirstLong(self, supplier, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableList<Long> self,
        @Nonnull Long defaultValue, @Nonnull ObservableValue<Predicate<? super Long>> filter) {
        FilteringBindings.filterThenFindFirstLong(self, defaultValue, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableList<Long> self,
        @Nonnull Supplier<Long> supplier, @Nonnull ObservableValue<Predicate<? super Long>> filter) {
        FilteringBindings.filterThenFindFirstLong(self, supplier, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableList<Float> self, @Nonnull Float defaultValue, @Nonnull Predicate<? super Float> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, defaultValue, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableList<Float> self,
        @Nonnull Supplier<Float> supplier, @Nonnull Predicate<? super Float> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, supplier, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableList<Float> self,
        @Nonnull Float defaultValue, @Nonnull ObservableValue<Predicate<? super Float>> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, defaultValue, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableList<Float> self,
        @Nonnull Supplier<Float> supplier, @Nonnull ObservableValue<Predicate<? super Float>> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, supplier, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableList<Double> self,
        @Nonnull Double defaultValue, @Nonnull Predicate<? super Double> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, defaultValue, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableList<Double> self,
        @Nonnull Supplier<Double> supplier, @Nonnull Predicate<? super Double> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, supplier, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableList<Double> self,
        @Nonnull Double defaultValue, @Nonnull ObservableValue<Predicate<? super Double>> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, defaultValue, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableList<Double> self,
        @Nonnull Supplier<Double> supplier, @Nonnull ObservableValue<Predicate<? super Double>> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, supplier, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableList<String> self,
        @Nonnull String defaultValue, @Nonnull Predicate<? super String> filter) {
        FilteringBindings.filterThenFindFirstString(self, defaultValue, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableList<String> self,
        @Nonnull Supplier<String> supplier, @Nonnull Predicate<? super String> filter) {
        FilteringBindings.filterThenFindFirstString(self, supplier, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableList<String> self,
        @Nonnull String defaultValue, @Nonnull ObservableValue<Predicate<? super String>> filter) {
        FilteringBindings.filterThenFindFirstString(self, defaultValue, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableList<String> self,
        @Nonnull Supplier<String> supplier, @Nonnull ObservableValue<Predicate<? super String>> filter) {
        FilteringBindings.filterThenFindFirstString(self, supplier, filter)
    }

    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableSet<T> self, @Nonnull T defaultValue, @Nonnull Predicate<? super T> filter) {
        FilteringBindings.filterThenFindFirst(self, defaultValue, filter)
    }

    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableSet<T> self, @Nonnull Supplier<T> supplier, @Nonnull Predicate<? super T> filter) {
        FilteringBindings.filterThenFindFirst(self, supplier, filter)
    }

    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull T defaultValue, @Nonnull ObservableValue<Predicate<? super T>> filter) {
        FilteringBindings.filterThenFindFirst(self, defaultValue, filter)
    }

    @Nonnull
    static <T> ObjectBinding<T> filterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<T> supplier, @Nonnull ObservableValue<Predicate<? super T>> filter) {
        FilteringBindings.filterThenFindFirst(self, supplier, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableSet<Boolean> self,
        @Nonnull Boolean defaultValue, @Nonnull Predicate<? super Boolean> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, defaultValue, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableSet<Boolean> self,
        @Nonnull Supplier<Boolean> supplier, @Nonnull Predicate<? super Boolean> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, supplier, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableSet<Boolean> self,
        @Nonnull Boolean defaultValue, @Nonnull ObservableValue<Predicate<? super Boolean>> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, defaultValue, filter)
    }

    @Nonnull
    static BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableSet<Boolean> self,
        @Nonnull Supplier<Boolean> supplier, @Nonnull ObservableValue<Predicate<? super Boolean>> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, supplier, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableSet<Integer> self,
        @Nonnull Integer defaultValue, @Nonnull Predicate<? super Integer> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, defaultValue, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableSet<Integer> self,
        @Nonnull Supplier<Integer> supplier, @Nonnull Predicate<? super Integer> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, supplier, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableSet<Integer> self,
        @Nonnull Integer defaultValue, @Nonnull ObservableValue<Predicate<? super Integer>> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, defaultValue, filter)
    }

    @Nonnull
    static IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableSet<Integer> self,
        @Nonnull Supplier<Integer> supplier, @Nonnull ObservableValue<Predicate<? super Integer>> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, supplier, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableSet<Long> self, @Nonnull Long defaultValue, @Nonnull Predicate<? super Long> filter) {
        FilteringBindings.filterThenFindFirstLong(self, defaultValue, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableSet<Long> self, @Nonnull Supplier<Long> supplier, @Nonnull Predicate<? super Long> filter) {
        FilteringBindings.filterThenFindFirstLong(self, supplier, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableSet<Long> self,
        @Nonnull Long defaultValue, @Nonnull ObservableValue<Predicate<? super Long>> filter) {
        FilteringBindings.filterThenFindFirstLong(self, defaultValue, filter)
    }

    @Nonnull
    static LongBinding filterThenFindFirstLong(
        @Nonnull ObservableSet<Long> self,
        @Nonnull Supplier<Long> supplier, @Nonnull ObservableValue<Predicate<? super Long>> filter) {
        FilteringBindings.filterThenFindFirstLong(self, supplier, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableSet<Float> self, @Nonnull Float defaultValue, @Nonnull Predicate<? super Float> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, defaultValue, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableSet<Float> self,
        @Nonnull Supplier<Float> supplier, @Nonnull Predicate<? super Float> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, supplier, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableSet<Float> self,
        @Nonnull Float defaultValue, @Nonnull ObservableValue<Predicate<? super Float>> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, defaultValue, filter)
    }

    @Nonnull
    static FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableSet<Float> self,
        @Nonnull Supplier<Float> supplier, @Nonnull ObservableValue<Predicate<? super Float>> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, supplier, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableSet<Double> self, @Nonnull Double defaultValue, @Nonnull Predicate<? super Double> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, defaultValue, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableSet<Double> self,
        @Nonnull Supplier<Double> supplier, @Nonnull Predicate<? super Double> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, supplier, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableSet<Double> self,
        @Nonnull Double defaultValue, @Nonnull ObservableValue<Predicate<? super Double>> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, defaultValue, filter)
    }

    @Nonnull
    static DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableSet<Double> self,
        @Nonnull Supplier<Double> supplier, @Nonnull ObservableValue<Predicate<? super Double>> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, supplier, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableSet<String> self, @Nonnull String defaultValue, @Nonnull Predicate<? super String> filter) {
        FilteringBindings.filterThenFindFirstString(self, defaultValue, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableSet<String> self,
        @Nonnull Supplier<String> supplier, @Nonnull Predicate<? super String> filter) {
        FilteringBindings.filterThenFindFirstString(self, supplier, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableSet<String> self,
        @Nonnull String defaultValue, @Nonnull ObservableValue<Predicate<? super String>> filter) {
        FilteringBindings.filterThenFindFirstString(self, defaultValue, filter)
    }

    @Nonnull
    static StringBinding filterThenFindFirstString(
        @Nonnull ObservableSet<String> self,
        @Nonnull Supplier<String> supplier, @Nonnull ObservableValue<Predicate<? super String>> filter) {
        FilteringBindings.filterThenFindFirstString(self, supplier, filter)
    }

    @Nonnull
    static <K, V> ObjectBinding<V> filterThenFindFirst(
        @Nonnull ObservableMap<K, V> self, @Nonnull V defaultValue, @Nonnull Predicate<? super V> filter) {
        FilteringBindings.filterThenFindFirst(self, defaultValue, filter)
    }

    @Nonnull
    static <K, V> ObjectBinding<V> filterThenFindFirst(
        @Nonnull ObservableMap<K, V> self, @Nonnull Supplier<V> supplier, @Nonnull Predicate<? super V> filter) {
        FilteringBindings.filterThenFindFirst(self, supplier, filter)
    }

    @Nonnull
    static <K, V> ObjectBinding<V> filterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull V defaultValue, @Nonnull ObservableValue<Predicate<? super V>> filter) {
        FilteringBindings.filterThenFindFirst(self, defaultValue, filter)
    }

    @Nonnull
    static <K, V> ObjectBinding<V> filterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<V> supplier, @Nonnull ObservableValue<Predicate<? super V>> filter) {
        FilteringBindings.filterThenFindFirst(self, supplier, filter)
    }

    @Nonnull
    static <K> BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableMap<K, Boolean> self,
        @Nonnull Boolean defaultValue, @Nonnull Predicate<? super Boolean> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, defaultValue, filter)
    }

    @Nonnull
    static <K> BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableMap<K, Boolean> self,
        @Nonnull Supplier<Boolean> supplier, @Nonnull Predicate<? super Boolean> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, supplier, filter)
    }

    @Nonnull
    static <K> BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableMap<K, Boolean> self,
        @Nonnull Boolean defaultValue, @Nonnull ObservableValue<Predicate<? super Boolean>> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, defaultValue, filter)
    }

    @Nonnull
    static <K> BooleanBinding filterThenFindFirstBoolean(
        @Nonnull ObservableMap<K, Boolean> self,
        @Nonnull Supplier<Boolean> supplier, @Nonnull ObservableValue<Predicate<? super Boolean>> filter) {
        FilteringBindings.filterThenFindFirstBoolean(self, supplier, filter)
    }

    @Nonnull
    static <K> IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableMap<K, Integer> self,
        @Nonnull Integer defaultValue, @Nonnull Predicate<? super Integer> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, defaultValue, filter)
    }

    @Nonnull
    static <K> IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableMap<K, Integer> self,
        @Nonnull Supplier<Integer> supplier, @Nonnull Predicate<? super Integer> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, supplier, filter)
    }

    @Nonnull
    static <K> IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableMap<K, Integer> self,
        @Nonnull Integer defaultValue, @Nonnull ObservableValue<Predicate<? super Integer>> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, defaultValue, filter)
    }

    @Nonnull
    static <K> IntegerBinding filterThenFindFirstInteger(
        @Nonnull ObservableMap<K, Integer> self,
        @Nonnull Supplier<Integer> supplier, @Nonnull ObservableValue<Predicate<? super Integer>> filter) {
        FilteringBindings.filterThenFindFirstInteger(self, supplier, filter)
    }

    @Nonnull
    static <K> LongBinding filterThenFindFirstLong(
        @Nonnull ObservableMap<K, Long> self, @Nonnull Long defaultValue, @Nonnull Predicate<? super Long> filter) {
        FilteringBindings.filterThenFindFirstLong(self, defaultValue, filter)
    }

    @Nonnull
    static <K> LongBinding filterThenFindFirstLong(
        @Nonnull ObservableMap<K, Long> self,
        @Nonnull Supplier<Long> supplier, @Nonnull Predicate<? super Long> filter) {
        FilteringBindings.filterThenFindFirstLong(self, supplier, filter)
    }

    @Nonnull
    static <K> LongBinding filterThenFindFirstLong(
        @Nonnull ObservableMap<K, Long> self,
        @Nonnull Long defaultValue, @Nonnull ObservableValue<Predicate<? super Long>> filter) {
        FilteringBindings.filterThenFindFirstLong(self, defaultValue, filter)
    }

    @Nonnull
    static <K> LongBinding filterThenFindFirstLong(
        @Nonnull ObservableMap<K, Long> self,
        @Nonnull Supplier<Long> supplier, @Nonnull ObservableValue<Predicate<? super Long>> filter) {
        FilteringBindings.filterThenFindFirstLong(self, supplier, filter)
    }

    @Nonnull
    static <K> FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableMap<K, Float> self, @Nonnull Float defaultValue, @Nonnull Predicate<? super Float> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, defaultValue, filter)
    }

    @Nonnull
    static <K> FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableMap<K, Float> self,
        @Nonnull Supplier<Float> supplier, @Nonnull Predicate<? super Float> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, supplier, filter)
    }

    @Nonnull
    static <K> FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableMap<K, Float> self,
        @Nonnull Float defaultValue, @Nonnull ObservableValue<Predicate<? super Float>> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, defaultValue, filter)
    }

    @Nonnull
    static <K> FloatBinding filterThenFindFirstFloat(
        @Nonnull ObservableMap<K, Float> self,
        @Nonnull Supplier<Float> supplier, @Nonnull ObservableValue<Predicate<? super Float>> filter) {
        FilteringBindings.filterThenFindFirstFloat(self, supplier, filter)
    }

    @Nonnull
    static <K> DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableMap<K, Double> self,
        @Nonnull Double defaultValue, @Nonnull Predicate<? super Double> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, defaultValue, filter)
    }

    @Nonnull
    static <K> DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableMap<K, Double> self,
        @Nonnull Supplier<Double> supplier, @Nonnull Predicate<? super Double> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, supplier, filter)
    }

    @Nonnull
    static <K> DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableMap<K, Double> self,
        @Nonnull Double defaultValue, @Nonnull ObservableValue<Predicate<? super Double>> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, defaultValue, filter)
    }

    @Nonnull
    static <K> DoubleBinding filterThenFindFirstDouble(
        @Nonnull ObservableMap<K, Double> self,
        @Nonnull Supplier<Double> supplier, @Nonnull ObservableValue<Predicate<? super Double>> filter) {
        FilteringBindings.filterThenFindFirstDouble(self, supplier, filter)
    }

    @Nonnull
    static <K> StringBinding filterThenFindFirstString(
        @Nonnull ObservableMap<K, String> self,
        @Nonnull String defaultValue, @Nonnull Predicate<? super String> filter) {
        FilteringBindings.filterThenFindFirstString(self, defaultValue, filter)
    }

    @Nonnull
    static <K> StringBinding filterThenFindFirstString(
        @Nonnull ObservableMap<K, String> self,
        @Nonnull Supplier<String> supplier, @Nonnull Predicate<? super String> filter) {
        FilteringBindings.filterThenFindFirstString(self, supplier, filter)
    }

    @Nonnull
    static <K> StringBinding filterThenFindFirstString(
        @Nonnull ObservableMap<K, String> self,
        @Nonnull String defaultValue, @Nonnull ObservableValue<Predicate<? super String>> filter) {
        FilteringBindings.filterThenFindFirstString(self, defaultValue, filter)
    }

    @Nonnull
    static <K> StringBinding filterThenFindFirstString(
        @Nonnull ObservableMap<K, String> self,
        @Nonnull Supplier<String> supplier, @Nonnull ObservableValue<Predicate<? super String>> filter) {
        FilteringBindings.filterThenFindFirstString(self, supplier, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull R defaultValue, @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<R> supplier, @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull R defaultValue,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull Predicate<Boolean> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull Predicate<Boolean> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<Predicate<Boolean>> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<Predicate<Boolean>> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull Predicate<Integer> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull Predicate<Integer> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<Predicate<Integer>> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<Predicate<Integer>> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Long defaultValue, @Nonnull Function<? super T, Long> mapper, @Nonnull Predicate<Long> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Long> supplier, @Nonnull Function<? super T, Long> mapper, @Nonnull Predicate<Long> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Long defaultValue,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper, @Nonnull ObservableValue<Predicate<Long>> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper, @Nonnull ObservableValue<Predicate<Long>> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Float defaultValue, @Nonnull Function<? super T, Float> mapper, @Nonnull Predicate<Float> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Function<? super T, Float> mapper, @Nonnull Predicate<Float> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Float defaultValue,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<Predicate<Float>> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<Predicate<Float>> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Double defaultValue, @Nonnull Function<? super T, Double> mapper, @Nonnull Predicate<Double> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Function<? super T, Double> mapper, @Nonnull Predicate<Double> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Double defaultValue,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<Predicate<Double>> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<Predicate<Double>> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull String defaultValue, @Nonnull Function<? super T, String> mapper, @Nonnull Predicate<String> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Function<? super T, String> mapper, @Nonnull Predicate<String> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull String defaultValue,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<Predicate<String>> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<Predicate<String>> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull R defaultValue, @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<R> supplier, @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull R defaultValue,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull Predicate<Boolean> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Function<? super T, Boolean> mapper, @Nonnull Predicate<Boolean> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<Predicate<Boolean>> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper,
        @Nonnull ObservableValue<Predicate<Boolean>> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull Predicate<Integer> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Function<? super T, Integer> mapper, @Nonnull Predicate<Integer> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<Predicate<Integer>> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper,
        @Nonnull ObservableValue<Predicate<Integer>> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Long defaultValue, @Nonnull Function<? super T, Long> mapper, @Nonnull Predicate<Long> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Long> supplier, @Nonnull Function<? super T, Long> mapper, @Nonnull Predicate<Long> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Long defaultValue,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper, @Nonnull ObservableValue<Predicate<Long>> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper, @Nonnull ObservableValue<Predicate<Long>> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Float defaultValue, @Nonnull Function<? super T, Float> mapper, @Nonnull Predicate<Float> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Function<? super T, Float> mapper, @Nonnull Predicate<Float> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Float defaultValue,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<Predicate<Float>> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper,
        @Nonnull ObservableValue<Predicate<Float>> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Double defaultValue, @Nonnull Function<? super T, Double> mapper, @Nonnull Predicate<Double> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Function<? super T, Double> mapper, @Nonnull Predicate<Double> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Double defaultValue,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<Predicate<Double>> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper,
        @Nonnull ObservableValue<Predicate<Double>> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull String defaultValue, @Nonnull Function<? super T, String> mapper, @Nonnull Predicate<String> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Function<? super T, String> mapper, @Nonnull Predicate<String> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull String defaultValue,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<Predicate<String>> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <T> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Function<? super T, String>> mapper,
        @Nonnull ObservableValue<Predicate<String>> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull R defaultValue, @Nonnull Function<? super V, R> mapper, @Nonnull Predicate<? super R> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<R> supplier, @Nonnull Function<? super V, R> mapper, @Nonnull Predicate<? super R> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull R defaultValue,
        @Nonnull ObservableValue<Function<? super V, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Function<? super V, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> filter) {
        FilteringBindings.mapThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Boolean defaultValue,
        @Nonnull Function<? super V, Boolean> mapper, @Nonnull Predicate<Boolean> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Function<? super V, Boolean> mapper, @Nonnull Predicate<Boolean> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Boolean defaultValue,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper,
        @Nonnull ObservableValue<Predicate<Boolean>> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper,
        @Nonnull ObservableValue<Predicate<Boolean>> filter) {
        FilteringBindings.mapToBooleanThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Integer defaultValue,
        @Nonnull Function<? super V, Integer> mapper, @Nonnull Predicate<Integer> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Function<? super V, Integer> mapper, @Nonnull Predicate<Integer> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Integer defaultValue,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper,
        @Nonnull ObservableValue<Predicate<Integer>> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper,
        @Nonnull ObservableValue<Predicate<Integer>> filter) {
        FilteringBindings.mapToIntegerThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Long defaultValue, @Nonnull Function<? super V, Long> mapper, @Nonnull Predicate<Long> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Long> supplier, @Nonnull Function<? super V, Long> mapper, @Nonnull Predicate<Long> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Long defaultValue,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper, @Nonnull ObservableValue<Predicate<Long>> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> LongBinding mapToLongThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper, @Nonnull ObservableValue<Predicate<Long>> filter) {
        FilteringBindings.mapToLongThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Float defaultValue, @Nonnull Function<? super V, Float> mapper, @Nonnull Predicate<Float> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Function<? super V, Float> mapper, @Nonnull Predicate<Float> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Float defaultValue,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper,
        @Nonnull ObservableValue<Predicate<Float>> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper,
        @Nonnull ObservableValue<Predicate<Float>> filter) {
        FilteringBindings.mapToFloatThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Double defaultValue, @Nonnull Function<? super V, Double> mapper, @Nonnull Predicate<Double> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Function<? super V, Double> mapper, @Nonnull Predicate<Double> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Double defaultValue,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper,
        @Nonnull ObservableValue<Predicate<Double>> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper,
        @Nonnull ObservableValue<Predicate<Double>> filter) {
        FilteringBindings.mapToDoubleThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull String defaultValue, @Nonnull Function<? super V, String> mapper, @Nonnull Predicate<String> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Function<? super V, String> mapper, @Nonnull Predicate<String> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull String defaultValue,
        @Nonnull ObservableValue<Function<? super V, String>> mapper,
        @Nonnull ObservableValue<Predicate<String>> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, defaultValue, mapper, filter)
    }

    @Nonnull
    static <K, V> StringBinding mapToStringThenFilterThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Function<? super V, String>> mapper,
        @Nonnull ObservableValue<Predicate<String>> filter) {
        FilteringBindings.mapToStringThenFilterThenFindFirst(self, supplier, mapper, filter)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull R defaultValue, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, R> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<R> supplier, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, R> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull R defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Boolean> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Boolean> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Integer> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Integer> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Long defaultValue, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Long> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Long> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Long defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Float defaultValue, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Float> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Float> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Float defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Double defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Double> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Double> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Double defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull String defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, String> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, String> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull String defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableList<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull R defaultValue, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, R> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<R> supplier, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, R> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull R defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, R>> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Boolean> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Boolean> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Boolean defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Boolean>> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Integer> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Integer> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Integer defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Integer>> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Long defaultValue, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Long> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Long> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Long defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Long>> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Float defaultValue, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Float> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Float> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Float defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Float>> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Double defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Double> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Double> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Double defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, Double>> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull String defaultValue,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, String> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, String> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull String defaultValue,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <T> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableSet<T> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Predicate<? super T>> filter,
        @Nonnull ObservableValue<Function<? super T, String>> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull R defaultValue, @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, R> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<R> supplier, @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, R> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull R defaultValue,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, R>> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<R> supplier,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, R>> mapper) {
        FilteringBindings.filterThenMapThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Boolean defaultValue,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Boolean> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Boolean> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Boolean defaultValue,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Boolean> supplier,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Boolean>> mapper) {
        FilteringBindings.filterThenMapToBooleanThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Integer defaultValue,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Integer> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Integer> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Integer defaultValue,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Integer> supplier,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Integer>> mapper) {
        FilteringBindings.filterThenMapToIntegerThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Long defaultValue, @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Long> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Long> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Long defaultValue,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> LongBinding filterThenMapToLongThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Long> supplier,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Long>> mapper) {
        FilteringBindings.filterThenMapToLongThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Float defaultValue, @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Float> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Float> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Float defaultValue,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Float> supplier,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Float>> mapper) {
        FilteringBindings.filterThenMapToFloatThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Double defaultValue,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Double> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, Double> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Double defaultValue,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<Double> supplier,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, Double>> mapper) {
        FilteringBindings.filterThenMapToDoubleThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull String defaultValue,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, String> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull Predicate<? super V> filter, @Nonnull Function<? super V, String> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, supplier, filter, mapper)
    }

    @Nonnull
    static <K, V> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull String defaultValue,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, String>> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, defaultValue, filter, mapper)
    }

    @Nonnull
    static <K, V> StringBinding filterThenMapToStringThenFindFirst(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Supplier<String> supplier,
        @Nonnull ObservableValue<Predicate<? super V>> filter,
        @Nonnull ObservableValue<Function<? super V, String>> mapper) {
        FilteringBindings.filterThenMapToStringThenFindFirst(self, supplier, filter, mapper)
    }
}
