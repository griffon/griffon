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
package griffon.javafx.support;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.FloatBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SetProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static javafx.beans.binding.Bindings.createBooleanBinding;
import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createFloatBinding;
import static javafx.beans.binding.Bindings.createIntegerBinding;
import static javafx.beans.binding.Bindings.createLongBinding;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public class BindingUtils {
    private static final String ERROR_ITEMS_NULL = "Argument 'items' must not be null";
    private static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";
    private static final String ERROR_REDUCER_NULL = "Argument 'reducer' must not be null";
    private static final String ERROR_SUPPLIER_NULL = "Argument 'supplier' must not be null";
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' must not be null";
    private static final String ERROR_CONSUMER_NULL = "Argument 'consumer' must not be null";
    private static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";
    private static final String ERROR_DELIMITER_NULL = "Argument 'delimiter' must not be null";
    private static final String ERROR_OBSERVABLE_NULL = "Argument 'observable' must not be null";
    private static final String ERROR_OBSERVABLE1_NULL = "Argument 'observable1' must not be null";
    private static final String ERROR_OBSERVABLE2_NULL = "Argument 'observable2' must not be null";
    private static final String ERROR_DEFAULT_VALUE_NULL = "Argument 'defaultValue' must not be null";

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
        return new MappingObservableList<>((ObservableList<? extends S>) source, mapper);
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
        return new MappingObservableList<>(source, mapper);
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
     * Registers a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped change listener.
     */
    public static <T> void uiThreadAwareChangeListener(@Nonnull final ObservableValue<T> observable, @Nonnull ChangeListener<T> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareChangeListener(listener));
    }

    /**
     * Creates a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped change listener.
     *
     * @return a {@code ChangeListener}.
     */
    @Nonnull
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(@Nonnull ChangeListener<T> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareChangeListener<T>(listener);
    }

    /**
     * Registers a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     */
    public static <T> void uiThreadAwareChangeListener(@Nonnull final ObservableValue<T> observable, @Nonnull final Consumer<T> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareChangeListener(consumer));
    }

    /**
     * Creates a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code newValue} argument.
     *
     * @return a {@code ChangeListener}.
     */
    @Nonnull
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(@Nonnull final Consumer<T> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareChangeListener<>((observable, oldValue, newValue) -> consumer.accept(newValue));
    }

    /**
     * Registers a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     */
    public static <T> void uiThreadAwareChangeListener(@Nonnull final ObservableValue<T> observable, @Nonnull final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareChangeListener(runnable));
    }

    /**
     * Creates a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     *
     * @return a {@code ChangeListener}.
     */
    @Nonnull
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareChangeListener<>((observable, oldValue, newValue) -> runnable.run());
    }

    /**
     * Registers a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped invalidation listener.
     */
    public static void uiThreadAwareInvalidationListener(@Nonnull final Observable observable, @Nonnull InvalidationListener listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareInvalidationListener(listener));
    }

    /**
     * Creates a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped invalidation listener.
     *
     * @return a {@code InvalidationListener}.
     */
    @Nonnull
    public static InvalidationListener uiThreadAwareInvalidationListener(@Nonnull InvalidationListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareInvalidationListener(listener);
    }

    /**
     * Registers a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code observable} argument.
     */
    public static void uiThreadAwareInvalidationListener(@Nonnull final Observable observable, @Nonnull final Consumer<Observable> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareInvalidationListener(consumer));
    }

    /**
     * Creates a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code observable} argument.
     *
     * @return a {@code InvalidationListener}.
     */
    @Nonnull
    public static InvalidationListener uiThreadAwareInvalidationListener(@Nonnull final Consumer<Observable> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareInvalidationListener(consumer::accept);
    }

    /**
     * Registers a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     */
    public static void uiThreadAwareInvalidationListener(@Nonnull final Observable observable, @Nonnull final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareInvalidationListener(runnable));
    }

    /**
     * Creates a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     *
     * @return a {@code InvalidationListener}.
     */
    @Nonnull
    public static InvalidationListener uiThreadAwareInvalidationListener(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareInvalidationListener(observable -> runnable.run());
    }

    /**
     * Registers a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped list change listener.
     */
    public static <E> void uiThreadAwareListChangeListener(@Nonnull final ObservableList<E> observable, @Nonnull ListChangeListener<E> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareListChangeListener(listener));
    }

    /**
     * Creates a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped list change listener.
     *
     * @return a {@code ListChangeListener}.
     */
    @Nonnull
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(@Nonnull ListChangeListener<E> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareListChangeListener<E>(listener);
    }

    /**
     * Registers a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     */
    public static <E> void uiThreadAwareListChangeListener(@Nonnull final ObservableList<E> observable, @Nonnull final Consumer<ListChangeListener.Change<? extends E>> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareListChangeListener(consumer));
    }

    /**
     * Creates a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code change} argument.
     *
     * @return a {@code ListChangeListener}.
     */
    @Nonnull
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(@Nonnull final Consumer<ListChangeListener.Change<? extends E>> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareListChangeListener<E>(consumer::accept);
    }

    /**
     * Registers a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     */
    public static <E> void uiThreadAwareListChangeListener(@Nonnull final ObservableList<E> observable, @Nonnull final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareListChangeListener(runnable));
    }

    /**
     * Creates a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     *
     * @return a {@code ListChangeListener}.
     */
    @Nonnull
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareListChangeListener<>(change -> runnable.run());
    }

    /**
     * Registers a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped map change listener.
     */
    public static <K, V> void uiThreadAwareMapChangeListener(@Nonnull final ObservableMap<K, V> observable, @Nonnull MapChangeListener<K, V> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareMapChangeListener(listener));
    }

    /**
     * Creates a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped map change listener.
     *
     * @return a {@code MapChangeListener}.
     */
    @Nonnull
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(@Nonnull MapChangeListener<K, V> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareMapChangeListener<K, V>(listener);
    }

    /**
     * Registers a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     */
    public static <K, V> void uiThreadAwareMapChangeListener(@Nonnull final ObservableMap<K, V> observable, @Nonnull final Consumer<MapChangeListener.Change<? extends K, ? extends V>> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareMapChangeListener(consumer));
    }

    /**
     * Creates a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code change} argument.
     *
     * @return a {@code MapChangeListener}.
     */
    @Nonnull
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(@Nonnull final Consumer<MapChangeListener.Change<? extends K, ? extends V>> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareMapChangeListener<K, V>(consumer::accept);
    }

    /**
     * Registers a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     */
    public static <K, V> void uiThreadAwareMapChangeListener(@Nonnull final ObservableMap<K, V> observable, @Nonnull final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareMapChangeListener(runnable));
    }

    /**
     * Creates a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     *
     * @return a {@code MapChangeListener}.
     */
    @Nonnull
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareMapChangeListener<>(change -> runnable.run());
    }

    /**
     * Registers a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped set change listener.
     */
    public static <E> void uiThreadAwareSetChangeListener(@Nonnull final ObservableSet<E> observable, @Nonnull SetChangeListener<E> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareSetChangeListener(listener));
    }

    /**
     * Creates a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped set change listener.
     *
     * @return a {@code SetChangeListener}.
     */
    @Nonnull
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(@Nonnull SetChangeListener<E> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareSetChangeListener<E>(listener);
    }

    /**
     * Registers a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     */
    public static <E> void uiThreadAwareSetChangeListener(@Nonnull final ObservableSet<E> observable, @Nonnull final Consumer<SetChangeListener.Change<? extends E>> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareSetChangeListener(consumer));
    }

    /**
     * Creates a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code change} argument.
     *
     * @return a {@code SetChangeListener}.
     */
    @Nonnull
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(@Nonnull final Consumer<SetChangeListener.Change<? extends E>> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareSetChangeListener<E>(consumer::accept);
    }

    /**
     * Registers a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     */
    public static <E> void uiThreadAwareSetChangeListener(@Nonnull final ObservableSet<E> observable, @Nonnull final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        observable.addListener(uiThreadAwareSetChangeListener(runnable));
    }

    /**
     * Creates a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     *
     * @return a {@code SetChangeListener}.
     */
    @Nonnull
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareSetChangeListener<>(change -> runnable.run());
    }

    /**
     * Creates an observable boolean property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean property to wrap.
     *
     * @return an observable boolean property.
     */
    @Nonnull
    public static BooleanProperty uiThreadAwareBooleanProperty(@Nonnull BooleanProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareBooleanProperty(observable);
    }

    /**
     * Creates an observable integer property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer property to wrap.
     *
     * @return an observable integer property.
     */
    @Nonnull
    public static IntegerProperty uiThreadAwareIntegerProperty(@Nonnull IntegerProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareIntegerProperty(observable);
    }

    /**
     * Creates an observable long property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long property to wrap.
     *
     * @return an observable long property.
     */
    @Nonnull
    public static LongProperty uiThreadAwareLongProperty(@Nonnull LongProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareLongProperty(observable);
    }

    /**
     * Creates an observable float property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float property to wrap.
     *
     * @return an observable float property.
     */
    @Nonnull
    public static FloatProperty uiThreadAwareFloatProperty(@Nonnull FloatProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareFloatProperty(observable);
    }

    /**
     * Creates an observable double property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double property to wrap.
     *
     * @return an observable double property.
     */
    @Nonnull
    public static DoubleProperty uiThreadAwareDoubleProperty(@Nonnull DoubleProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareDoubleProperty(observable);
    }

    /**
     * Creates an observable string property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string property to wrap.
     *
     * @return an observable string property.
     */
    @Nonnull
    public static StringProperty uiThreadAwareStringProperty(@Nonnull StringProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareStringProperty(observable);
    }

    /**
     * Creates an observable boolean property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean property to wrap.
     *
     * @return an observable boolean property.
     */
    @Nonnull
    public static Property<Boolean> uiThreadAwarePropertyBoolean(@Nonnull Property<Boolean> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyBoolean(observable);
    }

    /**
     * Creates an observable integer property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer property to wrap.
     *
     * @return an observable integer property.
     */
    @Nonnull
    public static Property<Integer> uiThreadAwarePropertyInteger(@Nonnull Property<Integer> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyInteger(observable);
    }

    /**
     * Creates an observable long property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long property to wrap.
     *
     * @return an observable long property.
     */
    @Nonnull
    public static Property<Long> uiThreadAwarePropertyLong(@Nonnull Property<Long> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyLong(observable);
    }

    /**
     * Creates an observable float property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float property to wrap.
     *
     * @return an observable float property.
     */
    @Nonnull
    public static Property<Float> uiThreadAwarePropertyFloat(@Nonnull Property<Float> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyFloat(observable);
    }

    /**
     * Creates an observable double property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double property to wrap.
     *
     * @return an observable double property.
     */
    @Nonnull
    public static Property<Double> uiThreadAwarePropertyDouble(@Nonnull Property<Double> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyDouble(observable);
    }

    /**
     * Creates an observable string property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string property to wrap.
     *
     * @return an observable string property.
     */
    @Nonnull
    public static Property<String> uiThreadAwarePropertyString(@Nonnull Property<String> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyString(observable);
    }

    /**
     * Creates an observable object property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable object property to wrap.
     *
     * @return an observable object property.
     */
    @Nonnull
    public static <T> ObjectProperty<T> uiThreadAwareObjectProperty(@Nonnull final ObjectProperty<T> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObjectProperty<>(observable);
    }

    /**
     * Creates an observable list property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable list property to wrap.
     *
     * @return an observable list property.
     */
    @Nonnull
    public static <E> ListProperty<E> uiThreadAwareListProperty(@Nonnull ListProperty<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareListProperty<>(observable);
    }

    /**
     * Creates an observable set property that notifies its seteners inside the UI thread.
     *
     * @param observable the observable set property to wrap.
     *
     * @return an observable set property.
     */
    @Nonnull
    public static <E> SetProperty<E> uiThreadAwareSetProperty(@Nonnull SetProperty<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareSetProperty<>(observable);
    }

    /**
     * Creates an observable map property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable map property to wrap.
     *
     * @return an observable map property.
     */
    @Nonnull
    public static <K, V> MapProperty<K, V> uiThreadAwareMapProperty(@Nonnull MapProperty<K, V> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareMapProperty<>(observable);
    }

    /**
     * Creates an observable value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable to wrap.
     *
     * @return an observable value.
     */
    @Nonnull
    public static <T> ObservableValue<T> uiThreadAwareObservable(@Nonnull final ObservableValue<T> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableValue<>(observable);
    }

    /**
     * Creates an observable string value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string to wrap.
     *
     * @return an observable string value.
     */
    @Nonnull
    public static ObservableStringValue uiThreadAwareObservableString(@Nonnull final ObservableStringValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableStringValue(observable);
    }

    /**
     * Creates an observable boolean value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean to wrap.
     *
     * @return an observable boolean value.
     */
    @Nonnull
    public static ObservableBooleanValue uiThreadAwareObservableBoolean(@Nonnull final ObservableBooleanValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableBooleanValue(observable);
    }

    /**
     * Creates an observable integer value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer to wrap.
     *
     * @return an observable integer value.
     */
    @Nonnull
    public static ObservableIntegerValue uiThreadAwareObservableInteger(@Nonnull final ObservableIntegerValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableIntegerValue(observable);
    }

    /**
     * Creates an observable long value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long to wrap.
     *
     * @return an observable long value.
     */
    @Nonnull
    public static ObservableLongValue uiThreadAwareObservableLong(@Nonnull final ObservableLongValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableLongValue(observable);
    }

    /**
     * Creates an observable float value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float to wrap.
     *
     * @return an observable float value.
     */
    @Nonnull
    public static ObservableFloatValue uiThreadAwareObservableFloat(@Nonnull final ObservableFloatValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableFloatValue(observable);
    }

    /**
     * Creates an observable double value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double to wrap.
     *
     * @return an observable double value.
     */
    @Nonnull
    public static ObservableDoubleValue uiThreadAwareObservableDouble(@Nonnull final ObservableDoubleValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableDoubleValue(observable);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable list of items.
     * @param delimiter the sequence of characters to be used between each element.
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding joinList(@Nonnull final ObservableList<?> items, @Nullable final String delimiter) {
        return joinList(items, delimiter, String::valueOf);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable list of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the reduced value.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <T> StringBinding joinList(@Nonnull final ObservableList<T> items, @Nullable final String delimiter, @Nonnull final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        final String value = delimiter == null ? "" : delimiter;
        return createStringBinding(() -> items.stream().map(mapper).collect(joining(value)), items);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable list of items.
     * @param delimiter the sequence of characters to be used between each element.
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding joinList(@Nonnull final ObservableList<?> items, @Nonnull final ObservableValue<String> delimiter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        return createStringBinding(() -> {
            String value = delimiter.getValue();
            value = value == null ? "" : value;
            return items.stream().map(String::valueOf).collect(joining(value));
        }, items, delimiter);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable list of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the reduced value.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <T> StringBinding joinList(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<String> delimiter, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String value = delimiter.getValue();
            value = value == null ? "" : value;
            final Function<? super T, String> mapperValue = mapper.getValue() != null ? mapper.getValue() : String::valueOf;
            return items.stream().map(mapperValue).collect(joining(value));
        }, items, delimiter, mapper);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable set of items.
     * @param delimiter the sequence of characters to be used between each element.
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding joinSet(@Nonnull final ObservableSet<?> items, @Nullable final String delimiter) {
        return joinSet(items, delimiter, String::valueOf);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable set of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the reduced value.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <T> StringBinding joinSet(@Nonnull final ObservableSet<T> items, @Nullable final String delimiter, @Nonnull final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        final String value = delimiter == null ? "" : delimiter;
        return createStringBinding(() -> items.stream().map(mapper).collect(joining(value)), items);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable set of items.
     * @param delimiter the sequence of characters to be used between each element.
     *
     * @return a string binding.
     */
    @Nonnull
    public static StringBinding joinSet(@Nonnull final ObservableSet<?> items, @Nonnull final ObservableValue<String> delimiter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        return createStringBinding(() -> {
            String value = delimiter.getValue();
            value = value == null ? "" : value;
            return items.stream().map(String::valueOf).collect(joining(value));
        }, items, delimiter);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable set of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the reduced value.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <T> StringBinding joinSet(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<String> delimiter, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            String value = delimiter.getValue();
            value = value == null ? "" : value;
            final Function<? super T, String> mapperValue = mapper.getValue() != null ? mapper.getValue() : String::valueOf;
            return items.stream().map(mapperValue).collect(joining(value));
        }, items, delimiter, mapper);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable map of items.
     * @param delimiter the sequence of characters to be used between each element.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <K, V> StringBinding joinMap(@Nonnull final ObservableMap<K, V> items, @Nullable final String delimiter) {
        return joinMap(items, delimiter, entry -> String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue()));
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable map of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the reduced value.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <K, V> StringBinding joinMap(@Nonnull final ObservableMap<K, V> items, @Nullable final String delimiter, @Nonnull final Function<Map.Entry<K, V>, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        final String value = delimiter == null ? "," : delimiter;
        return createStringBinding(() -> items.entrySet().stream().map(mapper).collect(joining(value)), items);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable map of items.
     * @param delimiter the sequence of characters to be used between each element.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <K, V> StringBinding joinMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<String> delimiter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        final Function<Map.Entry<K, V>, String> mapper = entry -> String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue());
        return createStringBinding(() -> {
            String value = delimiter.getValue();
            value = value == null ? "" : value;
            return items.entrySet().stream().map(mapper).collect(joining(value));
        }, items, delimiter);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable map of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the reduced value.
     *
     * @return a string binding.
     */
    @Nonnull
    public static <K, V> StringBinding joinMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<String> delimiter, @Nonnull final ObservableValue<Function<Map.Entry<K, V>, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        final Function<Map.Entry<K, V>, String> mv = entry -> String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue());
        return createStringBinding(() -> {
            String value = delimiter.getValue();
            value = value == null ? "" : value;
            final Function<Map.Entry<K, V>, String> mapperValue = mapper.getValue() != null ? mapper.getValue() : mv;
            return items.entrySet().stream().map(mapperValue).collect(joining(value));
        }, items, delimiter, mapper);
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
        return createObjectBinding(() -> items.values().stream().reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().reduce(operator).orElse(supplier.get());
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
        return createBooleanBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(supplier.get()));
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
        return createIntegerBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(supplier.get()));
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
        return createLongBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(supplier.get()));
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
        return createFloatBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(supplier.get()));
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
        return createDoubleBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(supplier.get()));
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
        return createStringBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(supplier.get()));
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
        return createObjectBinding(() -> items.stream().reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().reduce(operator).orElse(supplier.get());
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
        return createStringBinding(() -> mapperValue.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createIntegerBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createLongBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createFloatBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createBooleanBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())).doubleValue(), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get())).doubleValue();
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
        return createObjectBinding(() -> items.stream().reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().reduce(operator).orElse(supplier.get());
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
        return createStringBinding(() -> mapperValue.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createIntegerBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createLongBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createFloatBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createBooleanBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createDoubleBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())).doubleValue(), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get())).doubleValue();
        }, items, reducer, mapper);
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
        return createObjectBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createObjectBinding(() -> mapper.apply(items.stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.stream().reduce(operator).orElse(supplier.get()));
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
        return createObjectBinding(() -> mapper.apply(items.values().stream().reduce(reducer).orElse(supplier.get())), items);
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
            return mapperValue.apply(items.values().stream().reduce(operator).orElse(supplier.get()));
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
        return createObjectBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createObjectBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createObjectBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createBooleanBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createBooleanBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createBooleanBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createIntegerBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createIntegerBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createIntegerBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createLongBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createLongBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createLongBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createFloatBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createFloatBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createFloatBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createDoubleBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createDoubleBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createStringBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createStringBinding(() -> items.stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
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
        return createStringBinding(() -> items.values().stream().map(mapper).reduce(reducer).orElse(supplier.get()), items);
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
            return items.values().stream().map(mapperValue).reduce(reducerValue).orElse(supplier.get());
        }, items, reducer, mapper);
    }
}