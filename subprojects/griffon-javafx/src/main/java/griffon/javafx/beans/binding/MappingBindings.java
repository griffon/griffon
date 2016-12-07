/*
 * Copyright 2008-2016 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * Converts a string object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     *
     * @return an object binding.
     */
    @Nonnull
    public static ObjectBinding<String> mapToObject(@Nonnull final ObservableStringValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a boolean object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     *
     * @return an object binding.
     */
    @Nonnull
    public static ObjectBinding<Boolean> mapToObject(@Nonnull final ObservableBooleanValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a integer object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     *
     * @return an object binding.
     */
    @Nonnull
    public static ObjectBinding<Integer> mapToObject(@Nonnull final ObservableIntegerValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a long object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     *
     * @return an object binding.
     */
    @Nonnull
    public static ObjectBinding<Long> mapToObject(@Nonnull final ObservableLongValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a float object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     *
     * @return an object binding.
     */
    @Nonnull
    public static ObjectBinding<Float> mapToObject(@Nonnull final ObservableFloatValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a double object observable value into an object binding.
     *
     * @param observable the observable to be converted.
     *
     * @return an object binding.
     */
    @Nonnull
    public static ObjectBinding<Double> mapToObject(@Nonnull final ObservableDoubleValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createObjectBinding(observable::get, observable);
    }

    /**
     * Converts a boolean object observable value into a boolean binding.
     *
     * @param observable the observable to be converted.
     *
     * @return a boolean binding.
     */
    @Nonnull
    public static BooleanBinding mapToBoolean(@Nonnull final ObservableObjectValue<Boolean> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createBooleanBinding(observable::get, observable);
    }

    /**
     * Converts a integer object observable value into a integer binding.
     *
     * @param observable the observable to be converted.
     *
     * @return a integer binding.
     */
    @Nonnull
    public static IntegerBinding mapToInteger(@Nonnull final ObservableObjectValue<Integer> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createIntegerBinding(observable::get, observable);
    }

    /**
     * Converts a long object observable value into a long binding.
     *
     * @param observable the observable to be converted.
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapToLong(@Nonnull final ObservableObjectValue<Long> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createLongBinding(observable::get, observable);
    }

    /**
     * Converts a float object observable value into a float binding.
     *
     * @param observable the observable to be converted.
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapToFloat(@Nonnull final ObservableObjectValue<Float> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createFloatBinding(observable::get, observable);
    }

    /**
     * Converts a double object observable value into a double binding.
     *
     * @param observable the observable to be converted.
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapToDouble(@Nonnull final ObservableObjectValue<Double> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createDoubleBinding(observable::get, observable);
    }

    /**
     * Converts a literal object observable value into a string binding.
     *
     * @param observable the observable to be converted.
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding mapToString(@Nonnull final ObservableObjectValue<String> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return createStringBinding(observable::get, observable);
    }

    /**
     * Creates an observable list where all elements of the source list are mapped by the supplied function.
     *
     * @param source the source list.
     * @param mapper a non-interfering, stateless function to apply to the reduced value.
     *
     * @return an observable list.
     */
    @Nonnull
    public static <S, T> ObservableList<T> mapList(@Nonnull final ObservableList<? super S> source, @Nonnull final Function<S, T> mapper) {
        return new griffon.javafx.support.MappingObservableList<>((ObservableList<? extends S>) source, mapper);
    }

    /**
     * Creates an observable list where all elements of the source list are mapped by the supplied function.
     *
     * @param source the source list.
     * @param mapper a non-interfering, stateless function to apply to the reduced value.
     *
     * @return an observable list.
     */
    @Nonnull
    public static <S, T> ObservableList<T> mapList(@Nonnull final ObservableList<S> source, @Nonnull final ObservableValue<Function<S, T>> mapper) {
        return new griffon.javafx.support.MappingObservableList<>(source, mapper);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable the source observable.
     * @param mapper     a non-interfering, stateless function to apply to the reduced value.
     *
     * @return an object binding.
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapObject(@Nonnull final ObservableValue<T> observable, @Nonnull final Function<? super T, ? extends R> mapper) {
        return mapObject(observable, mapper, (R) null);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return an object binding.
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapObject(@Nonnull final ObservableValue<T> observable, @Nonnull final Function<? super T, ? extends R> mapper, @Nullable final R defaultValue) {
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
     *
     * @return an object binding.
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapObject(@Nonnull final ObservableValue<T> observable, @Nonnull final Function<? super T, ? extends R> mapper, @Nonnull final Supplier<R> supplier) {
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
     *
     * @return an object binding.
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapObject(@Nonnull final ObservableValue<T> observable, @Nonnull final ObservableValue<Function<? super T, ? extends R>> mapper) {
        return mapObject(observable, mapper, (R) null);
    }

    /**
     * Creates an object binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return an object binding.
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapObject(@Nonnull final ObservableValue<T> observable, @Nonnull final ObservableValue<Function<? super T, ? extends R>> mapper, @Nullable final R defaultValue) {
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
     *
     * @return an object binding.
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapObject(@Nonnull final ObservableValue<T> observable, @Nonnull final ObservableValue<Function<? super T, ? extends R>> mapper, @Nonnull final Supplier<R> supplier) {
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
     *
     * @return a boolean binding.
     */
    @Nonnull
    public static BooleanBinding mapBoolean(@Nonnull final ObservableValue<Boolean> observable, @Nonnull final Function<Boolean, Boolean> mapper) {
        return mapBoolean(observable, mapper, false);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a boolean binding.
     */
    @Nonnull
    public static BooleanBinding mapBoolean(@Nonnull final ObservableValue<Boolean> observable, @Nonnull final Function<Boolean, Boolean> mapper, @Nonnull final Boolean defaultValue) {
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
     *
     * @return a boolean binding.
     */
    @Nonnull
    public static BooleanBinding mapBoolean(@Nonnull final ObservableValue<Boolean> observable, @Nonnull final Function<Boolean, Boolean> mapper, @Nonnull final Supplier<Boolean> supplier) {
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
     *
     * @return a boolean binding.
     */
    @Nonnull
    public static BooleanBinding mapBoolean(@Nonnull final ObservableValue<Boolean> observable, @Nonnull final ObservableValue<Function<Boolean, Boolean>> mapper) {
        return mapBoolean(observable, mapper, false);
    }

    /**
     * Creates a boolean binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a boolean binding.
     */
    @Nonnull
    public static BooleanBinding mapBoolean(@Nonnull final ObservableValue<Boolean> observable, @Nonnull final ObservableValue<Function<Boolean, Boolean>> mapper, @Nonnull final Boolean defaultValue) {
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
     *
     * @return a boolean binding.
     */
    @Nonnull
    public static BooleanBinding mapBoolean(@Nonnull final ObservableValue<Boolean> observable, @Nonnull final ObservableValue<Function<Boolean, Boolean>> mapper, @Nonnull final Supplier<Boolean> supplier) {
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
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<Integer> observable, @Nonnull final Function<Integer, Integer> mapper) {
        return mapInteger(observable, mapper, 0);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<Integer> observable, @Nonnull final Function<Integer, Integer> mapper, @Nonnull final Integer defaultValue) {
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
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<Integer> observable, @Nonnull final Function<Integer, Integer> mapper, @Nonnull final Supplier<Integer> supplier) {
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
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<Integer> observable, @Nonnull final ObservableValue<Function<Integer, Integer>> mapper) {
        return mapInteger(observable, mapper, 0);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<Integer> observable, @Nonnull final ObservableValue<Function<Integer, Integer>> mapper, @Nonnull final Integer defaultValue) {
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
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<Integer> observable, @Nonnull final ObservableValue<Function<Integer, Integer>> mapper, @Nonnull final Supplier<Integer> supplier) {
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
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<Long> observable, @Nonnull final Function<Long, Long> mapper) {
        return mapLong(observable, mapper, 0L);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<Long> observable, @Nonnull final Function<Long, Long> mapper, @Nonnull final Long defaultValue) {
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
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<Long> observable, @Nonnull final Function<Long, Long> mapper, @Nonnull final Supplier<Long> supplier) {
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
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<Long> observable, @Nonnull final ObservableValue<Function<Long, Long>> mapper) {
        return mapLong(observable, mapper, 0L);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<Long> observable, @Nonnull final ObservableValue<Function<Long, Long>> mapper, @Nonnull final Long defaultValue) {
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
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<Long> observable, @Nonnull final ObservableValue<Function<Long, Long>> mapper, @Nonnull final Supplier<Long> supplier) {
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
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<Float> observable, @Nonnull final Function<Float, Float> mapper) {
        return mapFloat(observable, mapper, 0f);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<Float> observable, @Nonnull final Function<Float, Float> mapper, @Nonnull final Float defaultValue) {
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
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<Float> observable, @Nonnull final Function<Float, Float> mapper, @Nonnull final Supplier<Float> supplier) {
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
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<Float> observable, @Nonnull final ObservableValue<Function<Float, Float>> mapper) {
        return mapFloat(observable, mapper, 0f);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<Float> observable, @Nonnull final ObservableValue<Function<Float, Float>> mapper, @Nullable final Float defaultValue) {
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
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<Float> observable, @Nonnull final ObservableValue<Function<Float, Float>> mapper, @Nonnull final Supplier<Float> supplier) {
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
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<Double> observable, @Nonnull final Function<Double, Double> mapper) {
        return mapDouble(observable, mapper, 0d);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<Double> observable, @Nonnull final Function<Double, Double> mapper, @Nullable final Double defaultValue) {
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
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<Double> observable, @Nonnull final Function<Double, Double> mapper, @Nonnull final Supplier<Double> supplier) {
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
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<Double> observable, @Nonnull final ObservableValue<Function<Double, Double>> mapper) {
        return mapDouble(observable, mapper, 0d);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<Double> observable, @Nonnull final ObservableValue<Function<Double, Double>> mapper, @Nonnull final Double defaultValue) {
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
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<Double> observable, @Nonnull final ObservableValue<Function<Double, Double>> mapper, @Nonnull final Supplier<Double> supplier) {
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
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding mapString(@Nonnull final ObservableValue<String> observable, @Nonnull final Function<String, String> mapper) {
        return mapString(observable, mapper, "");
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding mapString(@Nonnull final ObservableValue<String> observable, @Nonnull final Function<String, String> mapper, @Nonnull final String defaultValue) {
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
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding mapString(@Nonnull final ObservableValue<String> observable, @Nonnull final Function<String, String> mapper, @Nonnull final Supplier<String> supplier) {
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
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding mapString(@Nonnull final ObservableValue<String> observable, @Nonnull final ObservableValue<Function<String, String>> mapper) {
        return mapString(observable, mapper, "");
    }

    /**
     * Creates a string binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param mapper       a non-interfering, stateless function to apply to the reduced value.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding mapString(@Nonnull final ObservableValue<String> observable, @Nonnull final ObservableValue<Function<String, String>> mapper, @Nonnull final String defaultValue) {
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
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding mapString(@Nonnull final ObservableValue<String> observable, @Nonnull final ObservableValue<Function<String, String>> mapper, @Nonnull final Supplier<String> supplier) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding mapBooleans(@Nonnull final ObservableValue<Boolean> observable1, @Nonnull final ObservableValue<Boolean> observable2, @Nonnull final Boolean defaultValue, @Nonnull final BiFunction<Boolean, Boolean, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding mapBooleans(@Nonnull final ObservableValue<Boolean> observable1, @Nonnull final ObservableValue<Boolean> observable2, @Nonnull final Supplier<Boolean> supplier, @Nonnull final BiFunction<Boolean, Boolean, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding mapBooleans(@Nonnull final ObservableValue<Boolean> observable1, @Nonnull final ObservableValue<Boolean> observable2, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<BiFunction<Boolean, Boolean, Boolean>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding mapBooleans(@Nonnull final ObservableValue<Boolean> observable1, @Nonnull final ObservableValue<Boolean> observable2, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<BiFunction<Boolean, Boolean, Boolean>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding mapIntegers(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Integer defaultValue, @Nonnull final BiFunction<? super Number, ? super Number, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding mapIntegers(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Integer> supplier, @Nonnull final BiFunction<? super Number, ? super Number, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding mapIntegers(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Integer>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding mapIntegers(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Integer>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding mapLongs(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Long defaultValue, @Nonnull final BiFunction<? super Number, ? super Number, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding mapLongs(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Long> supplier, @Nonnull final BiFunction<? super Number, ? super Number, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding mapLongs(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Long>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding mapLongs(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Long>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding mapFloats(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Float defaultValue, @Nonnull final BiFunction<? super Number, ? super Number, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding mapFloats(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Float> supplier, @Nonnull final BiFunction<? super Number, ? super Number, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding mapFloats(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Float>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding mapFloats(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Float>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding mapDoubles(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Double defaultValue, @Nonnull final BiFunction<? super Number, ? super Number, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding mapDoubles(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Double> supplier, @Nonnull final BiFunction<? super Number, ? super Number, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding mapDoubles(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Double>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding mapDoubles(@Nonnull final ObservableValue<? extends Number> observable1, @Nonnull final ObservableValue<? extends Number> observable2, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<BiFunction<? super Number, ? super Number, Double>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <A, B, R> ObjectBinding<R> mapObjects(@Nonnull final ObservableValue<A> observable1, @Nonnull final ObservableValue<B> observable2, @Nullable final R defaultValue, @Nonnull final BiFunction<? super A, ? super B, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <A, B, R> ObjectBinding<R> mapObjects(@Nonnull final ObservableValue<A> observable1, @Nonnull final ObservableValue<B> observable2, @Nonnull final Supplier<R> supplier, @Nonnull final BiFunction<? super A, ? super B, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <A, B, R> ObjectBinding<R> mapObjects(@Nonnull final ObservableValue<A> observable1, @Nonnull final ObservableValue<B> observable2, @Nullable final R defaultValue, @Nonnull final ObservableValue<BiFunction<? super A, ? super B, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <A, B, R> ObjectBinding<R> mapObjects(@Nonnull final ObservableValue<A> observable1, @Nonnull final ObservableValue<B> observable2, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<BiFunction<? super A, ? super B, R>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding mapStrings(@Nonnull final ObservableValue<String> observable1, @Nonnull final ObservableValue<String> observable2, @Nullable final String defaultValue, @Nonnull final BiFunction<String, String, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding mapStrings(@Nonnull final ObservableValue<String> observable1, @Nonnull final ObservableValue<String> observable2, @Nonnull final Supplier<String> supplier, @Nonnull final BiFunction<String, String, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding mapStrings(@Nonnull final ObservableValue<String> observable1, @Nonnull final ObservableValue<String> observable2, @Nullable final String defaultValue, @Nonnull final ObservableValue<BiFunction<String, String, String>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding mapStrings(@Nonnull final ObservableValue<String> observable1, @Nonnull final ObservableValue<String> observable2, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<BiFunction<String, String, String>> mapper) {
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
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<? extends Number> observable) {
        return mapInteger(observable, 0);
    }

    /**
     * Creates an integer binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Integer defaultValue) {
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
     *
     * @return an integer binding.
     */
    @Nonnull
    public static IntegerBinding mapInteger(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Supplier<Integer> supplier) {
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
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<? extends Number> observable) {
        return mapLong(observable, 0L);
    }

    /**
     * Creates a long binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Long defaultValue) {
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
     *
     * @return a long binding.
     */
    @Nonnull
    public static LongBinding mapLong(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Supplier<Long> supplier) {
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
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<? extends Number> observable) {
        return mapFloat(observable, 0f);
    }

    /**
     * Creates a float binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Float defaultValue) {
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
     *
     * @return a float binding.
     */
    @Nonnull
    public static FloatBinding mapFloat(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Supplier<Float> supplier) {
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
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<? extends Number> observable) {
        return mapDouble(observable, 0d);
    }

    /**
     * Creates a double binding containing the value of the mapper function applied to the source observable.
     *
     * @param observable   the source observable.
     * @param defaultValue the value to be returned if there is no value present, may be null.
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Double defaultValue) {
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
     *
     * @return a double binding.
     */
    @Nonnull
    public static DoubleBinding mapDouble(@Nonnull final ObservableValue<? extends Number> observable, @Nullable final Supplier<Double> supplier) {
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