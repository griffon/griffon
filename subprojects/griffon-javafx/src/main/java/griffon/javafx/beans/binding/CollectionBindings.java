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

import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

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
 * @since 2.10.0
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
     * @return a string binding.
     */
    public static StringBinding joinList(final ObservableList<?> items,  final String delimiter) {
        return joinList(items, delimiter, String::valueOf);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable list of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the each element.
     * @return a string binding.
     */
    public static <T> StringBinding joinList(final ObservableList<T> items,  final String delimiter, final Function<? super T, String> mapper) {
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
     * @return a string binding.
     */
    public static StringBinding joinList(final ObservableList<?> items, final ObservableValue<String> delimiter) {
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
     * @return a string binding.
     */
    public static <T> StringBinding joinList(final ObservableList<T> items, final ObservableValue<String> delimiter, final ObservableValue<Function<? super T, String>> mapper) {
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
     * @return a string binding.
     */
    public static StringBinding joinSet(final ObservableSet<?> items,  final String delimiter) {
        return joinSet(items, delimiter, String::valueOf);
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable set of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the each element.
     * @return a string binding.
     */
    public static <T> StringBinding joinSet(final ObservableSet<T> items,  final String delimiter, final Function<? super T, String> mapper) {
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
     * @return a string binding.
     */
    public static StringBinding joinSet(final ObservableSet<?> items, final ObservableValue<String> delimiter) {
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
     * @return a string binding.
     */
    public static <T> StringBinding joinSet(final ObservableSet<T> items, final ObservableValue<String> delimiter, final ObservableValue<Function<? super T, String>> mapper) {
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
     * @return a string binding.
     */
    public static <K, V> StringBinding joinMap(final ObservableMap<K, V> items,  final String delimiter) {
        return joinMap(items, delimiter, entry -> entry.getKey() + "=" + entry.getValue());
    }

    /**
     * Creates a string binding that constructs a sequence of characters separated by a delimiter.
     *
     * @param items     the observable map of items.
     * @param delimiter the sequence of characters to be used between each element.
     * @param mapper    a non-interfering, stateless function to apply to the each entry.
     * @return a string binding.
     */
    public static <K, V> StringBinding joinMap(final ObservableMap<K, V> items,  final String delimiter, final Function<Map.Entry<K, V>, String> mapper) {
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
     * @return a string binding.
     */
    public static <K, V> StringBinding joinMap(final ObservableMap<K, V> items, final ObservableValue<String> delimiter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        final Function<Map.Entry<K, V>, String> mapper = entry -> entry.getKey() + "=" + entry.getValue();
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
     * @return a string binding.
     */
    public static <K, V> StringBinding joinMap(final ObservableMap<K, V> items, final ObservableValue<String> delimiter, final ObservableValue<Function<Map.Entry<K, V>, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(delimiter, ERROR_DELIMITER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        final Function<Map.Entry<K, V>, String> mv = entry -> entry.getKey() + "=" + entry.getValue();
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
     * @return a number binding
     */
    public static NumberBinding minInList(final ObservableList<? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static NumberBinding minInList(final ObservableList<? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @return a number binding
     */
    public static NumberBinding maxInList(final ObservableList<? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static NumberBinding maxInList(final ObservableList<? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @return a number binding
     */
    public static NumberBinding averageInList(final ObservableList<? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static NumberBinding averageInList(final ObservableList<? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable list.
     *
     * @param items the observable list of items.
     * @return a number binding.
     */
    public static NumberBinding sumOfList(final ObservableList<? extends Number> items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     * @return a number binding
     */
    public static <T> NumberBinding minInList(final ObservableList<T> items, final Number defaultValue, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding minInList(final ObservableList<T> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInList(final ObservableList<T> items, final Number defaultValue, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInList(final ObservableList<T> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInList(final ObservableList<T> items, final Number defaultValue, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInList(final ObservableList<T> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding.
     */
    public static <T> NumberBinding sumOfList(final ObservableList<T> items, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding minInList(final ObservableList<T> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding minInList(final ObservableList<T> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInList(final ObservableList<T> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInList(final ObservableList<T> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInList(final ObservableList<T> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInList(final ObservableList<T> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding.
     */
    public static <T> NumberBinding sumOfList(final ObservableList<T> items, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static NumberBinding minInSet(final ObservableSet<? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static NumberBinding minInSet(final ObservableSet<? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @return a number binding
     */
    public static NumberBinding maxInSet(final ObservableSet<? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static NumberBinding maxInSet(final ObservableSet<? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @return a number binding
     */
    public static NumberBinding averageInSet(final ObservableSet<? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst elements.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static NumberBinding averageInSet(final ObservableSet<? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the items of the given observable set.
     *
     * @param items the observable set of items.
     * @return a number binding.
     */
    public static NumberBinding sumOfSet(final ObservableSet<? extends Number> items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        return createDoubleBinding(() -> items.stream().mapToDouble(Number::doubleValue).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst elements.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element.
     * @return a number binding
     */
    public static <T> NumberBinding minInSet(final ObservableSet<T> items, final Number defaultValue, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding minInSet(final ObservableSet<T> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInSet(final ObservableSet<T> items, final Number defaultValue, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInSet(final ObservableSet<T> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInSet(final ObservableSet<T> items, final Number defaultValue, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInSet(final ObservableSet<T> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding.
     */
    public static <T> NumberBinding sumOfSet(final ObservableSet<T> items, final ToDoubleFunction<? super T> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding minInSet(final ObservableSet<T> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding minInSet(final ObservableSet<T> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInSet(final ObservableSet<T> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding maxInSet(final ObservableSet<T> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInSet(final ObservableSet<T> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <T> NumberBinding averageInSet(final ObservableSet<T> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding.
     */
    public static <T> NumberBinding sumOfSet(final ObservableSet<T> items, final ObservableValue<ToDoubleFunction<? super T>> mapper) {
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
     * @return a number binding
     */
    public static <K> NumberBinding minInMap(final ObservableMap<K, ? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).min().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static <K> NumberBinding minInMap(final ObservableMap<K, ? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).min().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @return a number binding
     */
    public static <K> NumberBinding maxInMap(final ObservableMap<K, ? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).max().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the maximum value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static <K> NumberBinding maxInMap(final ObservableMap<K, ? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).max().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @return a number binding
     */
    public static <K> NumberBinding averageInMap(final ObservableMap<K, ? extends Number> items, final Number defaultValue) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(defaultValue, ERROR_DEFAULT_VALUE_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).average().orElse(defaultValue.doubleValue()), items);
    }

    /**
     * Creates a number binding that computes the average value amongst values.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @return a number binding
     */
    public static <K> NumberBinding averageInMap(final ObservableMap<K, ? extends Number> items, final Supplier<? extends Number> supplier) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).average().orElseGet(resolveDoubleSupplier(supplier)), items);
    }

    /**
     * Creates a number binding that contains the sum of the values of the given observable map.
     *
     * @param items the observable map of items.
     * @return a number binding.
     */
    public static <K> NumberBinding sumOfMap(final ObservableMap<K, ? extends Number> items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        return createDoubleBinding(() -> items.values().stream().mapToDouble(Number::doubleValue).sum(), items);
    }

    /**
     * Creates a number binding that computes the minimum value amongst values.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value.
     * @return a number binding
     */
    public static <K, V> NumberBinding minInMap(final ObservableMap<K, V> items, final Number defaultValue, final ToDoubleFunction<? super V> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding minInMap(final ObservableMap<K, V> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super V> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding maxInMap(final ObservableMap<K, V> items, final Number defaultValue, final ToDoubleFunction<? super V> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding maxInMap(final ObservableMap<K, V> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super V> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding averageInMap(final ObservableMap<K, V> items, final Number defaultValue, final ToDoubleFunction<? super V> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding averageInMap(final ObservableMap<K, V> items, final Supplier<? extends Number> supplier, final ToDoubleFunction<? super V> mapper) {
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
     * @return a number binding.
     */
    public static <K, V> NumberBinding sumOfMap(final ObservableMap<K, V> items, final ToDoubleFunction<? super V> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding minInMap(final ObservableMap<K, V> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super V>> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding minInMap(final ObservableMap<K, V> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super V>> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding maxInMap(final ObservableMap<K, V> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super V>> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding maxInMap(final ObservableMap<K, V> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super V>> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding averageInMap(final ObservableMap<K, V> items, final Number defaultValue, final ObservableValue<ToDoubleFunction<? super V>> mapper) {
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
     * @return a number binding
     */
    public static <K, V> NumberBinding averageInMap(final ObservableMap<K, V> items, final Supplier<? extends Number> supplier, final ObservableValue<ToDoubleFunction<? super V>> mapper) {
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
     * @return a number binding.
     */
    public static <K, V> NumberBinding sumOfMap(final ObservableMap<K, V> items, final ObservableValue<ToDoubleFunction<? super V>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            ToDoubleFunction<? super V> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            return items.values().stream().mapToDouble(mapperValue).sum();
        }, items, mapper);
    }

    private static DoubleSupplier resolveDoubleSupplier(final Supplier<? extends Number> supplier) {
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        return () -> supplier.get().doubleValue();
    }
}