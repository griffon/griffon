/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> reduce(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> reduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> reduce(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> reduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding reduceThenMapToLong(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding reduceThenMapToLong(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding reduceThenMapToLong(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Long>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding reduceThenMapToLong(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Long>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Float>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Float>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Double>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, Double>> mapper) {
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
     *
     * @return a String binding
     */
    @Nonnull
    public static <K, V> StringBinding reduceThenMapToString(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, String> mapper) {
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
     *
     * @return a String binding
     */
    @Nonnull
    public static <K, V> StringBinding reduceThenMapToString(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, String> mapper) {
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
     *
     * @return a String binding
     */
    @Nonnull
    public static <K, V> StringBinding reduceThenMapToString(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, String>> mapper) {
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
     *
     * @return a String binding
     */
    @Nonnull
    public static <K, V> StringBinding reduceThenMapToString(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, String>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, ? extends Number> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Number> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, ? extends Number>> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Number>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> reduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding reduceThenMapToString(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding reduceThenMapToInteger(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding reduceThenMapToLong(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding reduceThenMapToFloat(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding reduceThenMapToDouble(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding reduceThenMapToBoolean(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, ? extends Number> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, Number> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, ? extends Number>> mapper) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding reduceThenMapToNumber(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, Number>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableList<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final BinaryOperator<T> reducer, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableSet<T> items, @Nullable final T defaultValue, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<BinaryOperator<T>> reducer, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final BinaryOperator<V> reducer, @Nonnull final Function<? super V, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableMap<K, V> items, @Nullable final V defaultValue, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> reduceThenMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<BinaryOperator<V>> reducer, @Nonnull final ObservableValue<Function<? super V, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableList<T> items, @Nullable final R defaultValue, @Nonnull final Function<? super T, R> mapper, @Nonnull final BinaryOperator<R> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final Function<? super T, R> mapper, @Nonnull final BinaryOperator<R> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableList<T> items, @Nullable final R defaultValue, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<BinaryOperator<R>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<BinaryOperator<R>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final R defaultValue, @Nonnull final Function<? super T, R> mapper, @Nonnull final BinaryOperator<R> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final Function<? super T, R> mapper, @Nonnull final BinaryOperator<R> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final R defaultValue, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<BinaryOperator<R>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<BinaryOperator<R>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final R defaultValue, @Nonnull final Function<? super V, R> mapper, @Nonnull final BinaryOperator<R> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<R> supplier, @Nonnull final Function<? super V, R> mapper, @Nonnull final BinaryOperator<R> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final R defaultValue, @Nonnull final ObservableValue<Function<? super V, R>> mapper, @Nonnull final ObservableValue<BinaryOperator<R>> reducer) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Function<? super V, R>> mapper, @Nonnull final ObservableValue<BinaryOperator<R>> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Boolean defaultValue, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final BinaryOperator<Boolean> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final BinaryOperator<Boolean> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Boolean defaultValue, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<BinaryOperator<Boolean>> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<BinaryOperator<Boolean>> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Boolean defaultValue, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final BinaryOperator<Boolean> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final BinaryOperator<Boolean> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Boolean defaultValue, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<BinaryOperator<Boolean>> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<BinaryOperator<Boolean>> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Boolean defaultValue, @Nonnull final Function<? super V, Boolean> mapper, @Nonnull final BinaryOperator<Boolean> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Function<? super V, Boolean> mapper, @Nonnull final BinaryOperator<Boolean> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Boolean defaultValue, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper, @Nonnull final ObservableValue<BinaryOperator<Boolean>> reducer) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper, @Nonnull final ObservableValue<BinaryOperator<Boolean>> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Integer defaultValue, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final BinaryOperator<Integer> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final BinaryOperator<Integer> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Integer defaultValue, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<BinaryOperator<Integer>> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<BinaryOperator<Integer>> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Integer defaultValue, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final BinaryOperator<Integer> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final BinaryOperator<Integer> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Integer defaultValue, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<BinaryOperator<Integer>> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<BinaryOperator<Integer>> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Integer defaultValue, @Nonnull final Function<? super V, Integer> mapper, @Nonnull final BinaryOperator<Integer> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Function<? super V, Integer> mapper, @Nonnull final BinaryOperator<Integer> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Integer defaultValue, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper, @Nonnull final ObservableValue<BinaryOperator<Integer>> reducer) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper, @Nonnull final ObservableValue<BinaryOperator<Integer>> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Long defaultValue, @Nonnull final Function<? super T, Long> mapper, @Nonnull final BinaryOperator<Long> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Function<? super T, Long> mapper, @Nonnull final BinaryOperator<Long> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Long defaultValue, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<BinaryOperator<Long>> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<BinaryOperator<Long>> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Long defaultValue, @Nonnull final Function<? super T, Long> mapper, @Nonnull final BinaryOperator<Long> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Function<? super T, Long> mapper, @Nonnull final BinaryOperator<Long> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Long defaultValue, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<BinaryOperator<Long>> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<BinaryOperator<Long>> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Long defaultValue, @Nonnull final Function<? super V, Long> mapper, @Nonnull final BinaryOperator<Long> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Function<? super V, Long> mapper, @Nonnull final BinaryOperator<Long> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Long defaultValue, @Nonnull final ObservableValue<Function<? super V, Long>> mapper, @Nonnull final ObservableValue<BinaryOperator<Long>> reducer) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Function<? super V, Long>> mapper, @Nonnull final ObservableValue<BinaryOperator<Long>> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Float defaultValue, @Nonnull final Function<? super T, Float> mapper, @Nonnull final BinaryOperator<Float> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Function<? super T, Float> mapper, @Nonnull final BinaryOperator<Float> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Float defaultValue, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<BinaryOperator<Float>> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<BinaryOperator<Float>> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Float defaultValue, @Nonnull final Function<? super T, Float> mapper, @Nonnull final BinaryOperator<Float> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Function<? super T, Float> mapper, @Nonnull final BinaryOperator<Float> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Float defaultValue, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<BinaryOperator<Float>> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<BinaryOperator<Float>> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Float defaultValue, @Nonnull final Function<? super V, Float> mapper, @Nonnull final BinaryOperator<Float> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Function<? super V, Float> mapper, @Nonnull final BinaryOperator<Float> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Float defaultValue, @Nonnull final ObservableValue<Function<? super V, Float>> mapper, @Nonnull final ObservableValue<BinaryOperator<Float>> reducer) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Function<? super V, Float>> mapper, @Nonnull final ObservableValue<BinaryOperator<Float>> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Double defaultValue, @Nonnull final Function<? super T, Double> mapper, @Nonnull final BinaryOperator<Double> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Function<? super T, Double> mapper, @Nonnull final BinaryOperator<Double> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Double defaultValue, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<BinaryOperator<Double>> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<BinaryOperator<Double>> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Double defaultValue, @Nonnull final Function<? super T, Double> mapper, @Nonnull final BinaryOperator<Double> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Function<? super T, Double> mapper, @Nonnull final BinaryOperator<Double> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Double defaultValue, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<BinaryOperator<Double>> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<BinaryOperator<Double>> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Double defaultValue, @Nonnull final Function<? super V, Double> mapper, @Nonnull final BinaryOperator<Double> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Function<? super V, Double> mapper, @Nonnull final BinaryOperator<Double> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Double defaultValue, @Nonnull final ObservableValue<Function<? super V, Double>> mapper, @Nonnull final ObservableValue<BinaryOperator<Double>> reducer) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Function<? super V, Double>> mapper, @Nonnull final ObservableValue<BinaryOperator<Double>> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Number defaultValue, @Nonnull final Function<? super T, Number> mapper, @Nonnull final BinaryOperator<Number> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Number> supplier, @Nonnull final Function<? super T, Number> mapper, @Nonnull final BinaryOperator<Number> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableList<T> items, @Nullable final Number defaultValue, @Nonnull final ObservableValue<Function<? super T, Number>> mapper, @Nonnull final ObservableValue<BinaryOperator<Number>> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Number> supplier, @Nonnull final ObservableValue<Function<? super T, Number>> mapper, @Nonnull final ObservableValue<BinaryOperator<Number>> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Number defaultValue, @Nonnull final Function<? super T, Number> mapper, @Nonnull final BinaryOperator<Number> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Number> supplier, @Nonnull final Function<? super T, Number> mapper, @Nonnull final BinaryOperator<Number> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final Number defaultValue, @Nonnull final ObservableValue<Function<? super T, Number>> mapper, @Nonnull final ObservableValue<BinaryOperator<Number>> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <T> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Number> supplier, @Nonnull final ObservableValue<Function<? super T, Number>> mapper, @Nonnull final ObservableValue<BinaryOperator<Number>> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <K, V> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Number defaultValue, @Nonnull final Function<? super V, Number> mapper, @Nonnull final BinaryOperator<Number> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <K, V> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Number> supplier, @Nonnull final Function<? super V, Number> mapper, @Nonnull final BinaryOperator<Number> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <K, V> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final Number defaultValue, @Nonnull final ObservableValue<Function<? super V, Number>> mapper, @Nonnull final ObservableValue<BinaryOperator<Number>> reducer) {
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
     *
     * @return a number binding
     */
    @Nonnull
    public static <K, V> NumberBinding mapToNumberThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Number> supplier, @Nonnull final ObservableValue<Function<? super V, Number>> mapper, @Nonnull final ObservableValue<BinaryOperator<Number>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableList<T> items, @Nullable final String defaultValue, @Nonnull final Function<? super T, String> mapper, @Nonnull final BinaryOperator<String> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final Function<? super T, String> mapper, @Nonnull final BinaryOperator<String> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableList<T> items, @Nullable final String defaultValue, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<BinaryOperator<String>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<BinaryOperator<String>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final String defaultValue, @Nonnull final Function<? super T, String> mapper, @Nonnull final BinaryOperator<String> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final Function<? super T, String> mapper, @Nonnull final BinaryOperator<String> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableSet<T> items, @Nullable final String defaultValue, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<BinaryOperator<String>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenReduce(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<BinaryOperator<String>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final String defaultValue, @Nonnull final Function<? super V, String> mapper, @Nonnull final BinaryOperator<String> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<String> supplier, @Nonnull final Function<? super V, String> mapper, @Nonnull final BinaryOperator<String> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenReduce(@Nonnull final ObservableMap<K, V> items, @Nullable final String defaultValue, @Nonnull final ObservableValue<Function<? super V, String>> mapper, @Nonnull final ObservableValue<BinaryOperator<String>> reducer) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenReduce(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Function<? super V, String>> mapper, @Nonnull final ObservableValue<BinaryOperator<String>> reducer) {
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