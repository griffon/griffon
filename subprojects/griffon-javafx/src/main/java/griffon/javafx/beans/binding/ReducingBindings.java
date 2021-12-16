/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createFloatBinding;
import static javafx.beans.binding.Bindings.createIntegerBinding;
import static javafx.beans.binding.Bindings.createLongBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public final class ReducingBindings {
    private static final String ERROR_ITEMS_NULL = "Argument 'items' must not be null";
    private static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";
    private static final String ERROR_REDUCER_NULL = "Argument 'reducer' must not be null";
    private static final String ERROR_SUPPLIER_NULL = "Argument 'supplier' must not be null";

    private ReducingBindings() {
        // prevent instantiation
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> reduce(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> items.values().stream().reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> reduce(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> items.values().stream().reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> reduce(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            return items.values().stream().reduce(operator).orElse(defaultValue);
        }, items, reducer);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> reduce(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            return items.values().stream().reduce(operator).orElseGet(supplier);
        }, items, reducer);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding reduceThenMapToBoolean(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer, final Function<? super V, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding reduceThenMapToBoolean(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer, final Function<? super V, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createBooleanBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding reduceThenMapToBoolean(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Boolean>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding reduceThenMapToBoolean(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Boolean>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding reduceThenMapToInteger(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer, final Function<? super V, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding reduceThenMapToInteger(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer, final Function<? super V, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createIntegerBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding reduceThenMapToInteger(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Integer>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding reduceThenMapToInteger(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Integer>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <K, V> LongBinding reduceThenMapToLong(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer, final Function<? super V, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <K, V> LongBinding reduceThenMapToLong(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer, final Function<? super V, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createLongBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <K, V> LongBinding reduceThenMapToLong(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Long>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <K, V> LongBinding reduceThenMapToLong(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Long>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <K, V> FloatBinding reduceThenMapToFloat(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer, final Function<? super V, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <K, V> FloatBinding reduceThenMapToFloat(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer, final Function<? super V, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createFloatBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <K, V> FloatBinding reduceThenMapToFloat(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Float>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <K, V> FloatBinding reduceThenMapToFloat(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Float>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <K, V> DoubleBinding reduceThenMapToDouble(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer, final Function<? super V, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <K, V> DoubleBinding reduceThenMapToDouble(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer, final Function<? super V, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <K, V> DoubleBinding reduceThenMapToDouble(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Double>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <K, V> DoubleBinding reduceThenMapToDouble(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, Double>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a String binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a String binding
     */
    public static <K, V> StringBinding reduceThenMapToString(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer, final Function<? super V, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a String binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a String binding
     */
    public static <K, V> StringBinding reduceThenMapToString(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer, final Function<? super V, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createStringBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a String binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a String binding
     */
    public static <K, V> StringBinding reduceThenMapToString(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, String>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a String binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a String binding
     */
    public static <K, V> StringBinding reduceThenMapToString(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, String>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> items.stream().reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list.
     *
     * @param items    the observable list of elements.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> items.stream().reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            return items.stream().reduce(operator).orElse(defaultValue);
        }, items, reducer);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list.
     *
     * @param items    the observable list of elements.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            return items.stream().reduce(operator).orElseGet(supplier);
        }, items, reducer);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        final Function<? super T, String> mapperValue = mapper != null ? mapper : String::valueOf;
        return createStringBinding(() -> mapperValue.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        final Function<? super T, String> mapperValue = mapper != null ? mapper : String::valueOf;
        return createStringBinding(() -> mapperValue.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue() != null ? mapper.getValue() : String::valueOf;
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue() != null ? mapper.getValue() : String::valueOf;
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createBooleanBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, ? extends Number> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Number> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, ? extends Number>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, ? extends Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue)).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Number>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, ? extends Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier)).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> items.stream().reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set.
     *
     * @param items    the observable set of elements.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> items.stream().reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            return items.stream().reduce(operator).orElse(defaultValue);
        }, items, reducer);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set.
     *
     * @param items    the observable set of elements.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> reduce(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            return items.stream().reduce(operator).orElseGet(supplier);
        }, items, reducer);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        final Function<? super T, String> mapperValue = mapper != null ? mapper : String::valueOf;
        return createStringBinding(() -> mapperValue.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        final Function<? super T, String> mapperValue = mapper != null ? mapper : String::valueOf;
        return createStringBinding(() -> mapperValue.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue() != null ? mapper.getValue() : String::valueOf;
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding
     */
    public static <T> StringBinding reduceThenMapToString(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue() != null ? mapper.getValue() : String::valueOf;
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createIntegerBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding
     */
    public static <T> IntegerBinding reduceThenMapToInteger(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createLongBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding
     */
    public static <T> LongBinding reduceThenMapToLong(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createFloatBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding
     */
    public static <T> FloatBinding reduceThenMapToFloat(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding
     */
    public static <T> DoubleBinding reduceThenMapToDouble(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createBooleanBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding
     */
    public static <T> BooleanBinding reduceThenMapToBoolean(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, ? extends Number> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, Number> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, ? extends Number>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, ? extends Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue)).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return a number binding
     */
    public static <T> NumberBinding reduceThenMapToNumber(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, Number>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, ? extends Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier)).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableList<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableList<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableList<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to the reduced value.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableSet<T> items,  final T defaultValue, final BinaryOperator<T> reducer, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableSet<T> items, final Supplier<T> supplier, final BinaryOperator<T> reducer, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> mapper.apply(items.stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableSet<T> items,  final T defaultValue, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to the reduced value.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> reduceThenMap(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<BinaryOperator<T>> reducer, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<T> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> reduceThenMap(final ObservableMap<K, V> items,  final V defaultValue, final BinaryOperator<V> reducer, final Function<? super V, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(defaultValue)), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> reduceThenMap(final ObservableMap<K, V> items, final Supplier<V> supplier, final BinaryOperator<V> reducer, final Function<? super V, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElseGet(supplier)), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> reduceThenMap(final ObservableMap<K, V> items,  final V defaultValue, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, R>> mapper) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(defaultValue));
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to the reduced value.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @param mapper   a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> reduceThenMap(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<BinaryOperator<V>> reducer, final ObservableValue<Function<? super V, R>> mapper) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<V> operator = reducer.getValue();
            requireNonNull(operator, ERROR_REDUCER_NULL);
            final Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(items.values().stream().reduce(operator).orElseGet(supplier));
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableList<T> items,  final R defaultValue, final Function<? super T, R> mapper, final BinaryOperator<R> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableList<T> items, final Supplier<R> supplier, final Function<? super T, R> mapper, final BinaryOperator<R> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableList<T> items,  final R defaultValue, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<BinaryOperator<R>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<R> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableList<T> items, final Supplier<R> supplier, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<BinaryOperator<R>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<R> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableSet<T> items,  final R defaultValue, final Function<? super T, R> mapper, final BinaryOperator<R> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableSet<T> items, final Supplier<R> supplier, final Function<? super T, R> mapper, final BinaryOperator<R> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableSet<T> items,  final R defaultValue, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<BinaryOperator<R>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<R> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenReduce(final ObservableSet<T> items, final Supplier<R> supplier, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<BinaryOperator<R>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<R> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenReduce(final ObservableMap<K, V> items,  final R defaultValue, final Function<? super V, R> mapper, final BinaryOperator<R> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenReduce(final ObservableMap<K, V> items, final Supplier<R> supplier, final Function<? super V, R> mapper, final BinaryOperator<R> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenReduce(final ObservableMap<K, V> items,  final R defaultValue, final ObservableValue<Function<? super V, R>> mapper, final ObservableValue<BinaryOperator<R>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<R> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns an object binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenReduce(final ObservableMap<K, V> items, final Supplier<R> supplier, final ObservableValue<Function<? super V, R>> mapper, final ObservableValue<BinaryOperator<R>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            BinaryOperator<R> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableList<T> items,  final Boolean defaultValue, final Function<? super T, Boolean> mapper, final BinaryOperator<Boolean> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableList<T> items, final Supplier<Boolean> supplier, final Function<? super T, Boolean> mapper, final BinaryOperator<Boolean> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableList<T> items,  final Boolean defaultValue, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<BinaryOperator<Boolean>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<Boolean> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableList<T> items, final Supplier<Boolean> supplier, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<BinaryOperator<Boolean>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<Boolean> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableSet<T> items,  final Boolean defaultValue, final Function<? super T, Boolean> mapper, final BinaryOperator<Boolean> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableSet<T> items, final Supplier<Boolean> supplier, final Function<? super T, Boolean> mapper, final BinaryOperator<Boolean> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableSet<T> items,  final Boolean defaultValue, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<BinaryOperator<Boolean>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<Boolean> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenReduce(final ObservableSet<T> items, final Supplier<Boolean> supplier, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<BinaryOperator<Boolean>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<Boolean> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenReduce(final ObservableMap<K, V> items,  final Boolean defaultValue, final Function<? super V, Boolean> mapper, final BinaryOperator<Boolean> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenReduce(final ObservableMap<K, V> items, final Supplier<Boolean> supplier, final Function<? super V, Boolean> mapper, final BinaryOperator<Boolean> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenReduce(final ObservableMap<K, V> items,  final Boolean defaultValue, final ObservableValue<Function<? super V, Boolean>> mapper, final ObservableValue<BinaryOperator<Boolean>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<Boolean> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a boolean binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenReduce(final ObservableMap<K, V> items, final Supplier<Boolean> supplier, final ObservableValue<Function<? super V, Boolean>> mapper, final ObservableValue<BinaryOperator<Boolean>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            BinaryOperator<Boolean> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableList<T> items,  final Integer defaultValue, final Function<? super T, Integer> mapper, final BinaryOperator<Integer> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableList<T> items, final Supplier<Integer> supplier, final Function<? super T, Integer> mapper, final BinaryOperator<Integer> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableList<T> items,  final Integer defaultValue, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<BinaryOperator<Integer>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<Integer> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableList<T> items, final Supplier<Integer> supplier, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<BinaryOperator<Integer>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<Integer> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableSet<T> items,  final Integer defaultValue, final Function<? super T, Integer> mapper, final BinaryOperator<Integer> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableSet<T> items, final Supplier<Integer> supplier, final Function<? super T, Integer> mapper, final BinaryOperator<Integer> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableSet<T> items,  final Integer defaultValue, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<BinaryOperator<Integer>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<Integer> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenReduce(final ObservableSet<T> items, final Supplier<Integer> supplier, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<BinaryOperator<Integer>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<Integer> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenReduce(final ObservableMap<K, V> items,  final Integer defaultValue, final Function<? super V, Integer> mapper, final BinaryOperator<Integer> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenReduce(final ObservableMap<K, V> items, final Supplier<Integer> supplier, final Function<? super V, Integer> mapper, final BinaryOperator<Integer> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenReduce(final ObservableMap<K, V> items,  final Integer defaultValue, final ObservableValue<Function<? super V, Integer>> mapper, final ObservableValue<BinaryOperator<Integer>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<Integer> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns an integer binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenReduce(final ObservableMap<K, V> items, final Supplier<Integer> supplier, final ObservableValue<Function<? super V, Integer>> mapper, final ObservableValue<BinaryOperator<Integer>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            BinaryOperator<Integer> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableList<T> items,  final Long defaultValue, final Function<? super T, Long> mapper, final BinaryOperator<Long> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableList<T> items, final Supplier<Long> supplier, final Function<? super T, Long> mapper, final BinaryOperator<Long> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableList<T> items,  final Long defaultValue, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<BinaryOperator<Long>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<Long> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableList<T> items, final Supplier<Long> supplier, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<BinaryOperator<Long>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<Long> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableSet<T> items,  final Long defaultValue, final Function<? super T, Long> mapper, final BinaryOperator<Long> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableSet<T> items, final Supplier<Long> supplier, final Function<? super T, Long> mapper, final BinaryOperator<Long> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableSet<T> items,  final Long defaultValue, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<BinaryOperator<Long>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<Long> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenReduce(final ObservableSet<T> items, final Supplier<Long> supplier, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<BinaryOperator<Long>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<Long> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenReduce(final ObservableMap<K, V> items,  final Long defaultValue, final Function<? super V, Long> mapper, final BinaryOperator<Long> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenReduce(final ObservableMap<K, V> items, final Supplier<Long> supplier, final Function<? super V, Long> mapper, final BinaryOperator<Long> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenReduce(final ObservableMap<K, V> items,  final Long defaultValue, final ObservableValue<Function<? super V, Long>> mapper, final ObservableValue<BinaryOperator<Long>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<Long> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a long binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenReduce(final ObservableMap<K, V> items, final Supplier<Long> supplier, final ObservableValue<Function<? super V, Long>> mapper, final ObservableValue<BinaryOperator<Long>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            BinaryOperator<Long> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableList<T> items,  final Float defaultValue, final Function<? super T, Float> mapper, final BinaryOperator<Float> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableList<T> items, final Supplier<Float> supplier, final Function<? super T, Float> mapper, final BinaryOperator<Float> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableList<T> items,  final Float defaultValue, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<BinaryOperator<Float>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<Float> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableList<T> items, final Supplier<Float> supplier, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<BinaryOperator<Float>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<Float> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableSet<T> items,  final Float defaultValue, final Function<? super T, Float> mapper, final BinaryOperator<Float> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableSet<T> items, final Supplier<Float> supplier, final Function<? super T, Float> mapper, final BinaryOperator<Float> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableSet<T> items,  final Float defaultValue, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<BinaryOperator<Float>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<Float> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenReduce(final ObservableSet<T> items, final Supplier<Float> supplier, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<BinaryOperator<Float>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<Float> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenReduce(final ObservableMap<K, V> items,  final Float defaultValue, final Function<? super V, Float> mapper, final BinaryOperator<Float> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenReduce(final ObservableMap<K, V> items, final Supplier<Float> supplier, final Function<? super V, Float> mapper, final BinaryOperator<Float> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenReduce(final ObservableMap<K, V> items,  final Float defaultValue, final ObservableValue<Function<? super V, Float>> mapper, final ObservableValue<BinaryOperator<Float>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<Float> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a float binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenReduce(final ObservableMap<K, V> items, final Supplier<Float> supplier, final ObservableValue<Function<? super V, Float>> mapper, final ObservableValue<BinaryOperator<Float>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            BinaryOperator<Float> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableList<T> items,  final Double defaultValue, final Function<? super T, Double> mapper, final BinaryOperator<Double> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableList<T> items, final Supplier<Double> supplier, final Function<? super T, Double> mapper, final BinaryOperator<Double> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableList<T> items,  final Double defaultValue, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<BinaryOperator<Double>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Double> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableList<T> items, final Supplier<Double> supplier, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<BinaryOperator<Double>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Double> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableSet<T> items,  final Double defaultValue, final Function<? super T, Double> mapper, final BinaryOperator<Double> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableSet<T> items, final Supplier<Double> supplier, final Function<? super T, Double> mapper, final BinaryOperator<Double> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableSet<T> items,  final Double defaultValue, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<BinaryOperator<Double>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Double> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenReduce(final ObservableSet<T> items, final Supplier<Double> supplier, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<BinaryOperator<Double>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Double> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenReduce(final ObservableMap<K, V> items,  final Double defaultValue, final Function<? super V, Double> mapper, final BinaryOperator<Double> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenReduce(final ObservableMap<K, V> items, final Supplier<Double> supplier, final Function<? super V, Double> mapper, final BinaryOperator<Double> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenReduce(final ObservableMap<K, V> items,  final Double defaultValue, final ObservableValue<Function<? super V, Double>> mapper, final ObservableValue<BinaryOperator<Double>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Double> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a double binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenReduce(final ObservableMap<K, V> items, final Supplier<Double> supplier, final ObservableValue<Function<? super V, Double>> mapper, final ObservableValue<BinaryOperator<Double>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Double> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableList<T> items,  final Number defaultValue, final Function<? super T, Number> mapper, final BinaryOperator<Number> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableList<T> items, final Supplier<Number> supplier, final Function<? super T, Number> mapper, final BinaryOperator<Number> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableList<T> items,  final Number defaultValue, final ObservableValue<Function<? super T, Number>> mapper, final ObservableValue<BinaryOperator<Number>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Number> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableList<T> items, final Supplier<Number> supplier, final ObservableValue<Function<? super T, Number>> mapper, final ObservableValue<BinaryOperator<Number>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Number> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableSet<T> items,  final Number defaultValue, final Function<? super T, Number> mapper, final BinaryOperator<Number> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableSet<T> items, final Supplier<Number> supplier, final Function<? super T, Number> mapper, final BinaryOperator<Number> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableSet<T> items,  final Number defaultValue, final ObservableValue<Function<? super T, Number>> mapper, final ObservableValue<BinaryOperator<Number>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Number> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <T> NumberBinding mapToNumberThenReduce(final ObservableSet<T> items, final Supplier<Number> supplier, final ObservableValue<Function<? super T, Number>> mapper, final ObservableValue<BinaryOperator<Number>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Number> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <K, V> NumberBinding mapToNumberThenReduce(final ObservableMap<K, V> items,  final Number defaultValue, final Function<? super V, Number> mapper, final BinaryOperator<Number> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <K, V> NumberBinding mapToNumberThenReduce(final ObservableMap<K, V> items, final Supplier<Number> supplier, final Function<? super V, Number> mapper, final BinaryOperator<Number> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier).doubleValue(), items);
    }

    /**
     * Returns a number binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <K, V> NumberBinding mapToNumberThenReduce(final ObservableMap<K, V> items,  final Number defaultValue, final ObservableValue<Function<? super V, Number>> mapper, final ObservableValue<BinaryOperator<Number>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Number> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a number binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a number binding
     */
    public static <K, V> NumberBinding mapToNumberThenReduce(final ObservableMap<K, V> items, final Supplier<Number> supplier, final ObservableValue<Function<? super V, Number>> mapper, final ObservableValue<BinaryOperator<Number>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            BinaryOperator<Number> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, Number> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier).doubleValue();
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableList<T> items,  final String defaultValue, final Function<? super T, String> mapper, final BinaryOperator<String> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableList<T> items, final Supplier<String> supplier, final Function<? super T, String> mapper, final BinaryOperator<String> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable list of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableList<T> items,  final String defaultValue, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<BinaryOperator<String>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<String> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the list. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable list of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableList<T> items, final Supplier<String> supplier, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<BinaryOperator<String>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<String> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableSet<T> items,  final String defaultValue, final Function<? super T, String> mapper, final BinaryOperator<String> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableSet<T> items, final Supplier<String> supplier, final Function<? super T, String> mapper, final BinaryOperator<String> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items        the observable set of elements.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each element.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableSet<T> items,  final String defaultValue, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<BinaryOperator<String>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<String> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all elements in the set. The mapper function is applied to each element before reduction.
     *
     * @param items    the observable set of elements.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each element.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenReduce(final ObservableSet<T> items, final Supplier<String> supplier, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<BinaryOperator<String>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<String> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenReduce(final ObservableMap<K, V> items,  final String defaultValue, final Function<? super V, String> mapper, final BinaryOperator<String> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(defaultValue), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenReduce(final ObservableMap<K, V> items, final Supplier<String> supplier, final Function<? super V, String> mapper, final BinaryOperator<String> reducer) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElseGet(supplier), items);
    }

    /**
     * Returns a string binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items        the observable map.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to each value.
     * @param reducer      an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenReduce(final ObservableMap<K, V> items,  final String defaultValue, final ObservableValue<Function<? super V, String>> mapper, final ObservableValue<BinaryOperator<String>> reducer) {
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<String> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(defaultValue);
        }, items, reducer, mapper);
    }

    /**
     * Returns a string binding whose value is the reduction of all values in the map. The mapper function is applied to each value before reduction.
     *
     * @param items    the observable map.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to each value.
     * @param reducer  an associative, non-interfering, stateless function for combining two values.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenReduce(final ObservableMap<K, V> items, final Supplier<String> supplier, final ObservableValue<Function<? super V, String>> mapper, final ObservableValue<BinaryOperator<String>> reducer) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(reducer, ERROR_REDUCER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            BinaryOperator<String> reducerValue = reducer.getValue();
            requireNonNull(reducerValue, ERROR_REDUCER_NULL);
            final Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElseGet(supplier);
        }, items, reducer, mapper);
    }
}