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

import griffon.javafx.collections.MappingObservableList;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.function.BiFunction;
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
public final class MappingBindings {
    private static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";
    private static final String ERROR_SUPPLIER_NULL = "Argument 'supplier' must not be null";
    private static final String ERROR_OBSERVABLE_NULL = "Argument 'observable' must not be null";
    private static final String ERROR_OBSERVABLE1_NULL = "Argument 'observable1' must not be null";
    private static final String ERROR_OBSERVABLE2_NULL = "Argument 'observable2' must not be null";
    private static final String ERROR_DEFAULT_VALUE_NULL = "Argument 'defaultValue' must not be null";

    private MappingBindings() {
        // prevent instantiation
    }

    /**
     * Converts an observable into an object binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return an object binding.
     * @since 2.11.0
     */
    public static <T, R> ObjectBinding<R> mapAsObject(final ObservableValue<T> observable, final Function<T, R> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> mapper.apply(observable.getValue()), observable);
    }

    /**
     * Converts an observable into an object binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return an object binding.
     * @since 2.11.0
     */
    public static <T, R> ObjectBinding<R> mapAsObject(final ObservableValue<T> observable, final ObservableValue<Function<T, R>> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(observable.getValue());
        }, observable);
    }

    /**
     * Converts an observable into a boolean binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a boolean binding.
     * @since 2.11.0
     */
    public static <T> BooleanBinding mapAsBoolean(final ObservableValue<T> observable, final Function<T, Boolean> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> mapper.apply(observable.getValue()), observable);
    }

    /**
     * Converts an observable into a boolean binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a boolean binding.
     * @since 2.11.0
     */
    public static <T> BooleanBinding mapAsBoolean(final ObservableValue<T> observable, final ObservableValue<Function<T, Boolean>> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(observable.getValue());
        }, observable);
    }

    /**
     * Converts an observable into an integer binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return an integer binding.
     * @since 2.11.0
     */
    public static <T> IntegerBinding mapAsInteger(final ObservableValue<T> observable, final Function<T, Integer> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> mapper.apply(observable.getValue()), observable);
    }

    /**
     * Converts an observable into an integer binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return an integer binding.
     * @since 2.11.0
     */
    public static <T> IntegerBinding mapAsInteger(final ObservableValue<T> observable, final ObservableValue<Function<T, Integer>> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(observable.getValue());
        }, observable);
    }

    /**
     * Converts an observable into a long binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a long binding.
     * @since 2.11.0
     */
    public static <T> LongBinding mapAsLong(final ObservableValue<T> observable, final Function<T, Long> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> mapper.apply(observable.getValue()), observable);
    }

    /**
     * Converts an observable into a long binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a long binding.
     * @since 2.11.0
     */
    public static <T> LongBinding mapAsLong(final ObservableValue<T> observable, final ObservableValue<Function<T, Long>> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(observable.getValue());
        }, observable);
    }

    /**
     * Converts an observable into a float binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a float binding.
     * @since 2.11.0
     */
    public static <T> FloatBinding mapAsFloat(final ObservableValue<T> observable, final Function<T, Float> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> mapper.apply(observable.getValue()), observable);
    }

    /**
     * Converts an observable into a float binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a float binding.
     * @since 2.11.0
     */
    public static <T> FloatBinding mapAsFloat(final ObservableValue<T> observable, final ObservableValue<Function<T, Float>> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(observable.getValue());
        }, observable);
    }

    /**
     * Converts an observable into a double binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a double binding.
     * @since 2.11.0
     */
    public static <T> DoubleBinding mapAsDouble(final ObservableValue<T> observable, final Function<T, Double> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> mapper.apply(observable.getValue()), observable);
    }

    /**
     * Converts an observable into a double binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a double binding.
     * @since 2.11.0
     */
    public static <T> DoubleBinding mapAsDouble(final ObservableValue<T> observable, final ObservableValue<Function<T, Double>> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(observable.getValue());
        }, observable);
    }

    /**
     * Converts an observable into a string binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a string binding.
     * @since 2.11.0
     */
    public static <T> StringBinding mapAsString(final ObservableValue<T> observable, final Function<T, String> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> mapper.apply(observable.getValue()), observable);
    }

    /**
     * Converts an observable into a string binding.
     *
     * @param observable the observable to be converted.
     * @param mapper     a non-interfering, stateless function to apply to the observable value.
     * @return a string binding.
     * @since 2.11.0
     */
    public static <T> StringBinding mapAsString(final ObservableValue<T> observable, final ObservableValue<Function<T, String>> mapper) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return mapperValue.apply(observable.getValue());
        }, observable);
    }

    /**
     * Converts a string object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     * @return an object binding.
     */
    public static ObjectBinding<String> mapToObject(final ObservableStringValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a boolean object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     * @return an object binding.
     */
    public static ObjectBinding<Boolean> mapToObject(final ObservableBooleanValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a integer object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     * @return an object binding.
     */
    public static ObjectBinding<Integer> mapToObject(final ObservableIntegerValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a long object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     * @return an object binding.
     */
    public static ObjectBinding<Long> mapToObject(final ObservableLongValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a float object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     * @return an object binding.
     */
    public static ObjectBinding<Float> mapToObject(final ObservableFloatValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a double object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     * @return an object binding.
     */
    public static ObjectBinding<Double> mapToObject(final ObservableDoubleValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a boolean object observable value into a boolean binding.
     *
     * @param observable the observable to be converted.
     * @return a boolean binding.
     */
    public static BooleanBinding mapToBoolean(final ObservableObjectValue<Boolean> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createBooleanBinding(observable::get, observable);
    }

    /**
     * Converts a integer object observable value into a integer binding.
     *
     * @param observable the observable to be converted.
     * @return a integer binding.
     */
    public static IntegerBinding mapToInteger(final ObservableObjectValue<Integer> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createIntegerBinding(observable::get, observable);
    }

    /**
     * Converts a long object observable value into a long binding.
     *
     * @param observable the observable to be converted.
     * @return a long binding.
     */
    public static LongBinding mapToLong(final ObservableObjectValue<Long> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createLongBinding(observable::get, observable);
    }

    /**
     * Converts a float object observable value into a float binding.
     *
     * @param observable the observable to be converted.
     * @return a float binding.
     */
    public static FloatBinding mapToFloat(final ObservableObjectValue<Float> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createFloatBinding(observable::get, observable);
    }

    /**
     * Converts a double object observable value into a double binding.
     *
     * @param observable the observable to be converted.
     * @return a double binding.
     */
    public static DoubleBinding mapToDouble(final ObservableObjectValue<Double> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createDoubleBinding(observable::get, observable);
    }

    /**
     * Converts a literal object observable value into a string binding.
     *
     * @param observable the observable to be converted.
     * @return a string binding.
     */
    public static StringBinding mapToString(final ObservableObjectValue<String> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createStringBinding(observable::get, observable);
    }

    /**
     * Creates an observable list where all elements of the source list are mapped by the supplied function.
     *
     * @param source the source list.
     * @param mapper a non-interfering, stateless function to apply to the reduced value.
     * @return an observable list.
     */
    @SuppressWarnings("unchecked")
    public static <S, T> ObservableList<T> mapList(final ObservableList<? super S> source, final Function<S, T> mapper) {
        return new MappingObservableList<>((ObservableList<? extends S>) source, mapper);
    }

    /**
     * Creates an observable list where all elements of the source list are mapped by the supplied function.
     *
     * @param source the source list.
     * @param mapper a non-interfering, stateless function to apply to the reduced value.
     * @return an observable list.
     */
    public static <S, T> ObservableList<T> mapList(final ObservableList<S> source, final ObservableValue<Function<S, T>> mapper) {
        return new MappingObservableList<>(source, mapper);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding.
     */
    public static <T, R> ObjectBinding<R> mapObject(final ObservableValue<T> observable, final Function<? super T, ? extends R> mapper) {
        return mapObject(observable, mapper, (R) null);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return an object binding.
     */
    public static <T, R> ObjectBinding<R> mapObject(final ObservableValue<T> observable, final Function<? super T, ? extends R> mapper,  final R defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            T sourceValue = observable.getValue();
            return sourceValue == null ? defaultValue : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return an object binding.
     */
    public static <T, R> ObjectBinding<R> mapObject(final ObservableValue<T> observable, final Function<? super T, ? extends R> mapper, final Supplier<R> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> {
            T sourceValue = observable.getValue();
            return sourceValue == null ? supplier.get() : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return an object binding.
     */
    public static <T, R> ObjectBinding<R> mapObject(final ObservableValue<T> observable, final ObservableValue<Function<? super T, ? extends R>> mapper) {
        return mapObject(observable, mapper, (R) null);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return an object binding.
     */
    public static <T, R> ObjectBinding<R> mapObject(final ObservableValue<T> observable, final ObservableValue<Function<? super T, ? extends R>> mapper,  final R defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            T sourceValue = observable.getValue();
            Function<? super T, ? extends R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? defaultValue : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return an object binding.
     */
    public static <T, R> ObjectBinding<R> mapObject(final ObservableValue<T> observable, final ObservableValue<Function<? super T, ? extends R>> mapper, final Supplier<R> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createObjectBinding(() -> {
            T sourceValue = observable.getValue();
            Function<? super T, ? extends R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? supplier.get() : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding.
     */
    public static BooleanBinding mapBoolean(final ObservableValue<Boolean> observable, final Function<Boolean, Boolean> mapper) {
        return mapBoolean(observable, mapper, false);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a boolean binding.
     */
    public static BooleanBinding mapBoolean(final ObservableValue<Boolean> observable, final Function<Boolean, Boolean> mapper, final Boolean defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Boolean sourceValue = observable.getValue();
            return sourceValue == null ? defaultValue : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a boolean binding.
     */
    public static BooleanBinding mapBoolean(final ObservableValue<Boolean> observable, final Function<Boolean, Boolean> mapper, final Supplier<Boolean> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createBooleanBinding(() -> {
            Boolean sourceValue = observable.getValue();
            return sourceValue == null ? supplier.get() : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a boolean binding.
     */
    public static BooleanBinding mapBoolean(final ObservableValue<Boolean> observable, final ObservableValue<Function<Boolean, Boolean>> mapper) {
        return mapBoolean(observable, mapper, false);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a boolean binding.
     */
    public static BooleanBinding mapBoolean(final ObservableValue<Boolean> observable, final ObservableValue<Function<Boolean, Boolean>> mapper, final Boolean defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Boolean sourceValue = observable.getValue();
            Function<Boolean, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? defaultValue : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a boolean binding.
     */
    public static BooleanBinding mapBoolean(final ObservableValue<Boolean> observable, final ObservableValue<Function<Boolean, Boolean>> mapper, final Supplier<Boolean> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createBooleanBinding(() -> {
            Boolean sourceValue = observable.getValue();
            Function<Boolean, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? supplier.get() : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<Integer> observable, final Function<Integer, Integer> mapper) {
        return mapInteger(observable, mapper, 0);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<Integer> observable, final Function<Integer, Integer> mapper, final Integer defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Integer sourceValue = observable.getValue();
            return sourceValue == null ? defaultValue : mapper.apply(sourceValue);
        }, observable);
    }


    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<Integer> observable, final Function<Integer, Integer> mapper, final Supplier<Integer> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createIntegerBinding(() -> {
            Integer sourceValue = observable.getValue();
            return sourceValue == null ? supplier.get() : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<Integer> observable, final ObservableValue<Function<Integer, Integer>> mapper) {
        return mapInteger(observable, mapper, 0);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<Integer> observable, final ObservableValue<Function<Integer, Integer>> mapper, final Integer defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Integer sourceValue = observable.getValue();
            Function<Integer, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? defaultValue : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<Integer> observable, final ObservableValue<Function<Integer, Integer>> mapper, final Supplier<Integer> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createIntegerBinding(() -> {
            Integer sourceValue = observable.getValue();
            Function<Integer, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? supplier.get() : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<Long> observable, final Function<Long, Long> mapper) {
        return mapLong(observable, mapper, 0L);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<Long> observable, final Function<Long, Long> mapper, final Long defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Long sourceValue = observable.getValue();
            return sourceValue == null ? defaultValue : mapper.apply(sourceValue);
        }, observable);
    }


    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<Long> observable, final Function<Long, Long> mapper, final Supplier<Long> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createLongBinding(() -> {
            Long sourceValue = observable.getValue();
            return sourceValue == null ? supplier.get() : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<Long> observable, final ObservableValue<Function<Long, Long>> mapper) {
        return mapLong(observable, mapper, 0L);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<Long> observable, final ObservableValue<Function<Long, Long>> mapper, final Long defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Long sourceValue = observable.getValue();
            Function<Long, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? defaultValue : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<Long> observable, final ObservableValue<Function<Long, Long>> mapper, final Supplier<Long> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createLongBinding(() -> {
            Long sourceValue = observable.getValue();
            Function<Long, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? supplier.get() : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<Float> observable, final Function<Float, Float> mapper) {
        return mapFloat(observable, mapper, 0f);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<Float> observable, final Function<Float, Float> mapper, final Float defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Float sourceValue = observable.getValue();
            return sourceValue == null ? defaultValue : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<Float> observable, final Function<Float, Float> mapper, final Supplier<Float> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createFloatBinding(() -> {
            Float sourceValue = observable.getValue();
            return sourceValue == null ? supplier.get() : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<Float> observable, final ObservableValue<Function<Float, Float>> mapper) {
        return mapFloat(observable, mapper, 0f);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<Float> observable, final ObservableValue<Function<Float, Float>> mapper,  final Float defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Float sourceValue = observable.getValue();
            Function<Float, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? defaultValue : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<Float> observable, final ObservableValue<Function<Float, Float>> mapper, final Supplier<Float> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createFloatBinding(() -> {
            Float sourceValue = observable.getValue();
            Function<Float, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? supplier.get() : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<Double> observable, final Function<Double, Double> mapper) {
        return mapDouble(observable, mapper, 0d);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<Double> observable, final Function<Double, Double> mapper,  final Double defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Double sourceValue = observable.getValue();
            return sourceValue == null ? defaultValue : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<Double> observable, final Function<Double, Double> mapper, final Supplier<Double> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> {
            Double sourceValue = observable.getValue();
            return sourceValue == null ? supplier.get() : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<Double> observable, final ObservableValue<Function<Double, Double>> mapper) {
        return mapDouble(observable, mapper, 0d);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<Double> observable, final ObservableValue<Function<Double, Double>> mapper, final Double defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Double sourceValue = observable.getValue();
            Function<Double, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? defaultValue : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<Double> observable, final ObservableValue<Function<Double, Double>> mapper, final Supplier<Double> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> {
            Double sourceValue = observable.getValue();
            Function<Double, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? supplier.get() : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding.
     */
    public static StringBinding mapString(final ObservableValue<String> observable, final Function<String, String> mapper) {
        return mapString(observable, mapper, "");
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a string binding.
     */
    public static StringBinding mapString(final ObservableValue<String> observable, final Function<String, String> mapper, final String defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String sourceValue = observable.getValue();
            return sourceValue == null ? defaultValue : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a string binding.
     */
    public static StringBinding mapString(final ObservableValue<String> observable, final Function<String, String> mapper, final Supplier<String> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createStringBinding(() -> {
            String sourceValue = observable.getValue();
            return sourceValue == null ? supplier.get() : mapper.apply(sourceValue);
        }, observable);
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @return a string binding.
     */
    public static StringBinding mapString(final ObservableValue<String> observable, final ObservableValue<Function<String, String>> mapper) {
        return mapString(observable, mapper, "");
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a string binding.
     */
    public static StringBinding mapString(final ObservableValue<String> observable, final ObservableValue<Function<String, String>> mapper, final String defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String sourceValue = observable.getValue();
            Function<String, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? defaultValue : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a string binding.
     */
    public static StringBinding mapString(final ObservableValue<String> observable, final ObservableValue<Function<String, String>> mapper, final Supplier<String> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createStringBinding(() -> {
            String sourceValue = observable.getValue();
            Function<String, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return sourceValue == null ? supplier.get() : mapperValue.apply(sourceValue);
        }, observable, mapper);
    }

    /**
     * Returns a boolean binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there are no values present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a boolean binding
     */
    public static BooleanBinding mapBooleans(final ObservableValue<Boolean> observable1, final ObservableValue<Boolean> observable2, final Boolean defaultValue, final BiFunction<Boolean, Boolean, Boolean> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Boolean value1 = observable1.getValue();
            Boolean value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2);
    }

    /**
     * Returns a boolean binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a boolean binding
     */
    public static BooleanBinding mapBooleans(final ObservableValue<Boolean> observable1, final ObservableValue<Boolean> observable2, final Supplier<Boolean> supplier, final BiFunction<Boolean, Boolean, Boolean> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Boolean value1 = observable1.getValue();
            Boolean value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2);
    }

    /**
     * Returns a boolean binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a boolean binding
     */
    public static BooleanBinding mapBooleans(final ObservableValue<Boolean> observable1, final ObservableValue<Boolean> observable2, final Boolean defaultValue, final ObservableValue<BiFunction<Boolean, Boolean, Boolean>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Boolean value1 = observable1.getValue();
            Boolean value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<Boolean, Boolean, Boolean> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a boolean binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no value is present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a boolean binding
     */
    public static BooleanBinding mapBooleans(final ObservableValue<Boolean> observable1, final ObservableValue<Boolean> observable2, final Supplier<Boolean> supplier, final ObservableValue<BiFunction<Boolean, Boolean, Boolean>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Boolean value1 = observable1.getValue();
            Boolean value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<Boolean, Boolean, Boolean> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2, mapper);
    }

    /**
     * Returns an integer binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there are no values present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return an integer binding
     */
    public static IntegerBinding mapIntegers(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Integer defaultValue, final BiFunction<? super Number, ? super Number, Integer> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2);
    }

    /**
     * Returns an integer binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return an integer binding
     */
    public static IntegerBinding mapIntegers(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Integer> supplier, final BiFunction<? super Number, ? super Number, Integer> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2);
    }

    /**
     * Returns an integer binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return an integer binding
     */
    public static IntegerBinding mapIntegers(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Integer defaultValue, final ObservableValue<BiFunction<? super Number, ? super Number, Integer>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Integer> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2, mapper);
    }

    /**
     * Returns an integer binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no value is present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return an integer binding
     */
    public static IntegerBinding mapIntegers(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Integer> supplier, final ObservableValue<BiFunction<? super Number, ? super Number, Integer>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Integer> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a long binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there are no values present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a long binding
     */
    public static LongBinding mapLongs(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Long defaultValue, final BiFunction<? super Number, ? super Number, Long> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2);
    }

    /**
     * Returns a long binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a long binding
     */
    public static LongBinding mapLongs(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Long> supplier, final BiFunction<? super Number, ? super Number, Long> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2);
    }

    /**
     * Returns a long binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a long binding
     */
    public static LongBinding mapLongs(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Long defaultValue, final ObservableValue<BiFunction<? super Number, ? super Number, Long>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Long> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a long binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no value is present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a long binding
     */
    public static LongBinding mapLongs(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Long> supplier, final ObservableValue<BiFunction<? super Number, ? super Number, Long>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Long> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a float binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there are no values present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a float binding
     */
    public static FloatBinding mapFloats(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Float defaultValue, final BiFunction<? super Number, ? super Number, Float> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2);
    }

    /**
     * Returns a float binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a float binding
     */
    public static FloatBinding mapFloats(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Float> supplier, final BiFunction<? super Number, ? super Number, Float> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2);
    }

    /**
     * Returns a float binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a float binding
     */
    public static FloatBinding mapFloats(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Float defaultValue, final ObservableValue<BiFunction<? super Number, ? super Number, Float>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Float> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a float binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no value is present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a float binding
     */
    public static FloatBinding mapFloats(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Float> supplier, final ObservableValue<BiFunction<? super Number, ? super Number, Float>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Float> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a double binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there are no values present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a double binding
     */
    public static DoubleBinding mapDoubles(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Double defaultValue, final BiFunction<? super Number, ? super Number, Double> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2);
    }

    /**
     * Returns a double binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a double binding
     */
    public static DoubleBinding mapDoubles(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Double> supplier, final BiFunction<? super Number, ? super Number, Double> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2);
    }

    /**
     * Returns a double binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a double binding
     */
    public static DoubleBinding mapDoubles(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Double defaultValue, final ObservableValue<BiFunction<? super Number, ? super Number, Double>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Double> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a double binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no value is present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a double binding
     */
    public static DoubleBinding mapDoubles(final ObservableValue<? extends Number> observable1, final ObservableValue<? extends Number> observable2, final Supplier<Double> supplier, final ObservableValue<BiFunction<? super Number, ? super Number, Double>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Number value1 = observable1.getValue();
            Number value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super Number, ? super Number, Double> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return requireNonNull(supplier.get(), ERROR_DEFAULT_VALUE_NULL);
        }, observable1, observable2, mapper);
    }

    /**
     * Returns an object binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there are no values present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return an object binding
     */
    public static <A, B, R> ObjectBinding<R> mapObjects(final ObservableValue<A> observable1, final ObservableValue<B> observable2,  final R defaultValue, final BiFunction<? super A, ? super B, R> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            A value1 = observable1.getValue();
            B value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2);
    }

    /**
     * Returns an object binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returnedif no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return an object binding
     */
    public static <A, B, R> ObjectBinding<R> mapObjects(final ObservableValue<A> observable1, final ObservableValue<B> observable2, final Supplier<R> supplier, final BiFunction<? super A, ? super B, R> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            A value1 = observable1.getValue();
            B value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return supplier.get();
        }, observable1, observable2);
    }

    /**
     * Returns an object binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return an object binding
     */
    public static <A, B, R> ObjectBinding<R> mapObjects(final ObservableValue<A> observable1, final ObservableValue<B> observable2,  final R defaultValue, final ObservableValue<BiFunction<? super A, ? super B, R>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            A value1 = observable1.getValue();
            B value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super A, ? super B, R> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2, mapper);
    }

    /**
     * Returns an object binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return an object binding
     */
    public static <A, B, R> ObjectBinding<R> mapObjects(final ObservableValue<A> observable1, final ObservableValue<B> observable2, final Supplier<R> supplier, final ObservableValue<BiFunction<? super A, ? super B, R>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            A value1 = observable1.getValue();
            B value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<? super A, ? super B, R> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return supplier.get();
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a string binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there are no values present.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a string binding
     */
    public static StringBinding mapStrings(final ObservableValue<String> observable1, final ObservableValue<String> observable2,  final String defaultValue, final BiFunction<String, String, String> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String value1 = observable1.getValue();
            String value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2);
    }

    /**
     * Returns a string binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a string binding
     */
    public static StringBinding mapStrings(final ObservableValue<String> observable1, final ObservableValue<String> observable2, final Supplier<String> supplier, final BiFunction<String, String, String> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String value1 = observable1.getValue();
            String value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                return mapper.apply(value1, value2);
            }
            return supplier.get();
        }, observable1, observable2);
    }

    /**
     * Returns a string binding whose value is the combination of two observable values.
     *
     * @param observable1  the first observable value.
     * @param observable2  the second observable value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @param mapper       a non-interfering, stateless function to apply to the supplied values.
     * @return a string binding
     */
    public static StringBinding mapStrings(final ObservableValue<String> observable1, final ObservableValue<String> observable2,  final String defaultValue, final ObservableValue<BiFunction<String, String, String>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String value1 = observable1.getValue();
            String value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<String, String, String> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return defaultValue;
        }, observable1, observable2, mapper);
    }

    /**
     * Returns a string binding whose value is the combination of two observable values.
     *
     * @param observable1 the first observable value.
     * @param observable2 the second observable value.
     * @param supplier    a {@code Supplier} whose result is returned if no values are present.
     * @param mapper      a non-interfering, stateless function to apply to the supplied values.
     * @return a string binding
     */
    public static StringBinding mapStrings(final ObservableValue<String> observable1, final ObservableValue<String> observable2, final Supplier<String> supplier, final ObservableValue<BiFunction<String, String, String>> mapper) {
        requireNonNull(observable1, ERROR_OBSERVABLE1_NULL);
        requireNonNull(observable2, ERROR_OBSERVABLE2_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String value1 = observable1.getValue();
            String value2 = observable2.getValue();
            if (value1 != null && value2 != null) {
                BiFunction<String, String, String> function = mapper.getValue();
                return requireNonNull(function, ERROR_MAPPER_NULL).apply(value1, value2);
            }
            return supplier.get();
        }, observable1, observable2, mapper);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<? extends Number> observable) {
        return mapInteger(observable, 0);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<? extends Number> observable,  final Integer defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        if (observable instanceof IntegerBinding) {
            return (IntegerBinding) observable;
        }
        return createIntegerBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.intValue() : defaultValue;
        }, observable);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return an integer binding.
     */
    public static IntegerBinding mapInteger(final ObservableValue<? extends Number> observable,  final Supplier<Integer> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        if (observable instanceof IntegerBinding) {
            return (IntegerBinding) observable;
        }
        return createIntegerBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.intValue() : supplier.get();
        }, observable);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<? extends Number> observable) {
        return mapLong(observable, 0L);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<? extends Number> observable,  final Long defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        if (observable instanceof LongBinding) {
            return (LongBinding) observable;
        }
        return createLongBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.longValue() : defaultValue;
        }, observable);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a long binding.
     */
    public static LongBinding mapLong(final ObservableValue<? extends Number> observable,  final Supplier<Long> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        if (observable instanceof LongBinding) {
            return (LongBinding) observable;
        }
        return createLongBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.longValue() : supplier.get();
        }, observable);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<? extends Number> observable) {
        return mapFloat(observable, 0f);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<? extends Number> observable,  final Float defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        if (observable instanceof FloatBinding) {
            return (FloatBinding) observable;
        }
        return createFloatBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.floatValue() : defaultValue;
        }, observable);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a float binding.
     */
    public static FloatBinding mapFloat(final ObservableValue<? extends Number> observable,  final Supplier<Float> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        if (observable instanceof FloatBinding) {
            return (FloatBinding) observable;
        }
        return createFloatBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.floatValue() : supplier.get();
        }, observable);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<? extends Number> observable) {
        return mapDouble(observable, 0d);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<? extends Number> observable,  final Double defaultValue) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        if (observable instanceof DoubleBinding) {
            return (DoubleBinding) observable;
        }
        return createDoubleBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.doubleValue() : defaultValue;
        }, observable);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param supplier   a {@code Supplier} whose result is returned if no value is present.
     * @return a double binding.
     */
    public static DoubleBinding mapDouble(final ObservableValue<? extends Number> observable,  final Supplier<Double> supplier) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        if (observable instanceof DoubleBinding) {
            return (DoubleBinding) observable;
        }
        return createDoubleBinding(() -> {
            Number value = observable.getValue();
            return value != null ? value.doubleValue() : supplier.get();
        }, observable);
    }
}