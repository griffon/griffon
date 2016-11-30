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

import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static javafx.beans.binding.Bindings.createDoubleBinding;
import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public final class CollectionBindings {
    private static final String ERROR_ITEMS_NULL = "Argument 'items' must not be null";
    private static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";
    private static final String ERROR_SUPPLIER_NULL = "Argument 'supplier' must not be null";
    private static final String ERROR_DELIMITER_NULL = "Argument 'delimiter' must not be null";
    private static final String ERROR_DEFAULT_VALUE_NULL = "Argument 'defaultValue' must not be null";

    private CollectionBindings() {
        // prevent instantiation
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
     * @param mapper    a non-interfering, stateless function to apply to the each element.
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
     * @param mapper    a non-interfering, stateless function to apply to the each element.
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
     * @param mapper    a non-interfering, stateless function to apply to the each element.
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
     * @param mapper    a non-interfering, stateless function to apply to the each element.
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
     * @param delimiter the sequence of characters to be used between each entry.
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
     * @param mapper    a non-interfering, stateless function to apply to the each entry.
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
     * @param delimiter the sequence of characters to be used between each entry.
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
     * @param mapper    a non-interfering, stateless function to apply to the each entry.
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
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding minInList(@Nonnull final ObservableList<? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding minInList(@Nonnull final ObservableList<? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding maxInList(@Nonnull final ObservableList<? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding maxInList(@Nonnull final ObservableList<? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding averageInList(@Nonnull final ObservableList<? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding averageInList(@Nonnull final ObservableList<? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable list.
     *
     * @param items the observable list of items.
     *
     * @return a number binding.
     */
    @Nonnull
    public static NumberBinding sumOfList(@Nonnull final ObservableList<? extends Number> items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInList(@Nonnull final ObservableList<T> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInList(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInList(@Nonnull final ObservableList<T> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInList(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInList(@Nonnull final ObservableList<T> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInList(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable list.
     *
     * @param items  the observable list of items.
     * @param mapper a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding.
     */
    @Nonnull
    public <T> NumberBinding sumOfList(@Nonnull final ObservableList<T> items, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInList(@Nonnull final ObservableList<T> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).min().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInList(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).min().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInList(@Nonnull final ObservableList<T> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).max().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInList(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).max().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInList(@Nonnull final ObservableList<T> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).average().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInList(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).average().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable list.
     *
     * @param items  the observable list of items.
     * @param mapper a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding.
     */
    @Nonnull
    public <T> NumberBinding sumOfList(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).sum();
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding minInSet(@Nonnull final ObservableSet<? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding minInSet(@Nonnull final ObservableSet<? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding maxInSet(@Nonnull final ObservableSet<? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding maxInSet(@Nonnull final ObservableSet<? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding averageInSet(@Nonnull final ObservableSet<? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static NumberBinding averageInSet(@Nonnull final ObservableSet<? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable set.
     *
     * @param items the observable set of items.
     *
     * @return a number binding.
     */
    @Nonnull
    public static NumberBinding sumOfSet(@Nonnull final ObservableSet<? extends Number> items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable set.
     *
     * @param items  the observable set of items.
     * @param mapper a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding.
     */
    @Nonnull
    public <T> NumberBinding sumOfSet(@Nonnull final ObservableSet<T> items, @Nonnull final ToDoubleFunction<? super T> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(mapper).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).min().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding minInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).min().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).max().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding maxInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).max().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).average().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding
     */
    @Nonnull
    public <T> NumberBinding averageInSet(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).average().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable set.
     *
     * @param items  the observable set of items.
     * @param mapper a non-interfering, stateless function to apply to the each element.
     *
     * @return a number binding.
     */
    @Nonnull
    public <T> NumberBinding sumOfSet(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<ToDoubleFunction<? super T>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super T> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.stream().mapToDouble(mapperValue).sum();
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static <K> NumberBinding minInMap(@Nonnull final ObservableMap<K, ? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static <K> NumberBinding minInMap(@Nonnull final ObservableMap<K, ? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static <K> NumberBinding maxInMap(@Nonnull final ObservableMap<K, ? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static <K> NumberBinding maxInMap(@Nonnull final ObservableMap<K, ? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     *
     * @return a number binding
     */
    @Nonnull
    public static <K> NumberBinding averageInMap(@Nonnull final ObservableMap<K, ? extends Number> items, @Nonnull final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     *
     * @return a number binding
     */
    @Nonnull
    public static <K> NumberBinding averageInMap(@Nonnull final ObservableMap<K, ? extends Number> items, @Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the values of the given observable map.
     *
     * @param items the observable map of items.
     *
     * @return a number binding.
     */
    @Nonnull
    public static <K> NumberBinding sumOfMap(@Nonnull final ObservableMap<K, ? extends Number> items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding minInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super V> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(mapper).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding minInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super V> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(mapper).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding maxInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super V> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(mapper).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding maxInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super V> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(mapper).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding averageInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Number defaultValue, @Nonnull final ToDoubleFunction<? super V> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(mapper).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding averageInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ToDoubleFunction<? super V> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(mapper).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the values of the given observable map.
     *
     * @param items  the observable map of items.
     * @param mapper a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding.
     */
    @Nonnull
    public <K, V> NumberBinding sumOfMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final ToDoubleFunction<? super V> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(mapper).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding minInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).min().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding minInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).min().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding maxInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).max().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding maxInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).max().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding averageInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Number defaultValue, @Nonnull final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).average().orElse(defaultValue.doubleValue());
        }, items, mapper);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding
     */
    @Nonnull
    public <K, V> NumberBinding averageInMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<? extends Number> supplier, @Nonnull final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).average().orElseGet(resolveDoubleSupplier(supplier));
        }, items, mapper);
    }

    /**
     * Creates a number binding that contains the sum of the values of the given observable map.
     *
     * @param items  the observable map of items.
     * @param mapper a non-interfering, stateless function to apply to the each value.
     *
     * @return a number binding.
     */
    @Nonnull
    public <K, V> NumberBinding sumOfMap(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).sum();
        }, items, mapper);
    }

    @Nonnull
    private static DoubleSupplier resolveDoubleSupplier(@Nonnull final Supplier<? extends Number> supplier) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return () -> supplier.get().doubleValue();
    }
}