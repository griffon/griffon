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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.function.Function;
import java.util.function.Predicate;
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
public final class FilteringBindings {
    private static final String ERROR_ITEMS_NULL = "Argument 'items' must not be null";
    private static final String ERROR_FILTER_NULL = "Argument 'filter' must not be null";
    private static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";
    private static final String ERROR_SUPPLIER_NULL = "Argument 'supplier' must not be null";

    private FilteringBindings() {
        // prevent instantiation
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableList<T> items, final T defaultValue, final Predicate<? super T> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableList<T> items, final Supplier<T> supplier, final Predicate<? super T> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableList<T> items, final T defaultValue, final ObservableValue<Predicate<? super T>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableList<T> items, final Supplier<T> supplier, final ObservableValue<Predicate<? super T>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableList<Boolean> items, final Boolean defaultValue, final Predicate<? super Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableList<Boolean> items, final Supplier<Boolean> supplier, final Predicate<? super Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableList<Boolean> items, final Boolean defaultValue, final ObservableValue<Predicate<? super Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableList<Boolean> items, final Supplier<Boolean> supplier, final ObservableValue<Predicate<? super Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableList<Integer> items, final Integer defaultValue, final Predicate<? super Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableList<Integer> items, final Supplier<Integer> supplier, final Predicate<? super Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableList<Integer> items, final Integer defaultValue, final ObservableValue<Predicate<? super Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Predicate<? super Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableList<Integer> items, final Supplier<Integer> supplier, final ObservableValue<Predicate<? super Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Predicate<? super Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableList<Long> items, final Long defaultValue, final Predicate<? super Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableList<Long> items, final Supplier<Long> supplier, final Predicate<? super Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableList<Long> items, final Long defaultValue, final ObservableValue<Predicate<? super Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Predicate<? super Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableList<Long> items, final Supplier<Long> supplier, final ObservableValue<Predicate<? super Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Predicate<? super Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableList<Float> items, final Float defaultValue, final Predicate<? super Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableList<Float> items, final Supplier<Float> supplier, final Predicate<? super Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableList<Float> items, final Float defaultValue, final ObservableValue<Predicate<? super Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Predicate<? super Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableList<Float> items, final Supplier<Float> supplier, final ObservableValue<Predicate<? super Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Predicate<? super Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableList<Double> items, final Double defaultValue, final Predicate<? super Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableList<Double> items, final Supplier<Double> supplier, final Predicate<? super Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableList<Double> items, final Double defaultValue, final ObservableValue<Predicate<? super Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Predicate<? super Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableList<Double> items, final Supplier<Double> supplier, final ObservableValue<Predicate<? super Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Predicate<? super Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableList<String> items, final String defaultValue, final Predicate<? super String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableList<String> items, final Supplier<String> supplier, final Predicate<? super String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableList<String> items, final String defaultValue, final ObservableValue<Predicate<? super String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Predicate<? super String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableList<String> items, final Supplier<String> supplier, final ObservableValue<Predicate<? super String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Predicate<? super String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableSet<T> items, final T defaultValue, final Predicate<? super T> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableSet<T> items, final Supplier<T> supplier, final Predicate<? super T> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableSet<T> items, final T defaultValue, final ObservableValue<Predicate<? super T>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an object binding
     */
    public static <T> ObjectBinding<T> filterThenFindFirst(final ObservableSet<T> items, final Supplier<T> supplier, final ObservableValue<Predicate<? super T>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableSet<Boolean> items, final Boolean defaultValue, final Predicate<? super Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableSet<Boolean> items, final Supplier<Boolean> supplier, final Predicate<? super Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableSet<Boolean> items, final Boolean defaultValue, final ObservableValue<Predicate<? super Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a boolean binding
     */
    public static BooleanBinding filterThenFindFirstBoolean(final ObservableSet<Boolean> items, final Supplier<Boolean> supplier, final ObservableValue<Predicate<? super Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableSet<Integer> items, final Integer defaultValue, final Predicate<? super Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableSet<Integer> items, final Supplier<Integer> supplier, final Predicate<? super Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableSet<Integer> items, final Integer defaultValue, final ObservableValue<Predicate<? super Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Predicate<? super Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return an integer binding
     */
    public static IntegerBinding filterThenFindFirstInteger(final ObservableSet<Integer> items, final Supplier<Integer> supplier, final ObservableValue<Predicate<? super Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Predicate<? super Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableSet<Long> items, final Long defaultValue, final Predicate<? super Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableSet<Long> items, final Supplier<Long> supplier, final Predicate<? super Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableSet<Long> items, final Long defaultValue, final ObservableValue<Predicate<? super Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Predicate<? super Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a long binding
     */
    public static LongBinding filterThenFindFirstLong(final ObservableSet<Long> items, final Supplier<Long> supplier, final ObservableValue<Predicate<? super Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Predicate<? super Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableSet<Float> items, final Float defaultValue, final Predicate<? super Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableSet<Float> items, final Supplier<Float> supplier, final Predicate<? super Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableSet<Float> items, final Float defaultValue, final ObservableValue<Predicate<? super Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Predicate<? super Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a float binding
     */
    public static FloatBinding filterThenFindFirstFloat(final ObservableSet<Float> items, final Supplier<Float> supplier, final ObservableValue<Predicate<? super Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Predicate<? super Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableSet<Double> items, final Double defaultValue, final Predicate<? super Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableSet<Double> items, final Supplier<Double> supplier, final Predicate<? super Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableSet<Double> items, final Double defaultValue, final ObservableValue<Predicate<? super Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Predicate<? super Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a double binding
     */
    public static DoubleBinding filterThenFindFirstDouble(final ObservableSet<Double> items, final Supplier<Double> supplier, final ObservableValue<Predicate<? super Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Predicate<? super Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableSet<String> items, final String defaultValue, final Predicate<? super String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableSet<String> items, final Supplier<String> supplier, final Predicate<? super String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableSet<String> items, final String defaultValue, final ObservableValue<Predicate<? super String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Predicate<? super String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element.
     * @return a string binding
     */
    public static StringBinding filterThenFindFirstString(final ObservableSet<String> items, final Supplier<String> supplier, final ObservableValue<Predicate<? super String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Predicate<? super String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> filterThenFindFirst(final ObservableMap<K, V> items, final V defaultValue, final Predicate<? super V> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.values().stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> filterThenFindFirst(final ObservableMap<K, V> items, final Supplier<V> supplier, final Predicate<? super V> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.values().stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> filterThenFindFirst(final ObservableMap<K, V> items, final V defaultValue, final ObservableValue<Predicate<? super V>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return an object binding
     */
    public static <K, V> ObjectBinding<V> filterThenFindFirst(final ObservableMap<K, V> items, final Supplier<V> supplier, final ObservableValue<Predicate<? super V>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a boolean binding
     */
    public static <K> BooleanBinding filterThenFindFirstBoolean(final ObservableMap<K, Boolean> items, final Boolean defaultValue, final Predicate<? super Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.values().stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a boolean binding
     */
    public static <K> BooleanBinding filterThenFindFirstBoolean(final ObservableMap<K, Boolean> items, final Supplier<Boolean> supplier, final Predicate<? super Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.values().stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a boolean binding
     */
    public static <K> BooleanBinding filterThenFindFirstBoolean(final ObservableMap<K, Boolean> items, final Boolean defaultValue, final ObservableValue<Predicate<? super Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a boolean binding
     */
    public static <K> BooleanBinding filterThenFindFirstBoolean(final ObservableMap<K, Boolean> items, final Supplier<Boolean> supplier, final ObservableValue<Predicate<? super Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return an integer binding
     */
    public static <K> IntegerBinding filterThenFindFirstInteger(final ObservableMap<K, Integer> items, final Integer defaultValue, final Predicate<? super Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.values().stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return an integer binding
     */
    public static <K> IntegerBinding filterThenFindFirstInteger(final ObservableMap<K, Integer> items, final Supplier<Integer> supplier, final Predicate<? super Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.values().stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return an integer binding
     */
    public static <K> IntegerBinding filterThenFindFirstInteger(final ObservableMap<K, Integer> items, final Integer defaultValue, final ObservableValue<Predicate<? super Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Predicate<? super Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return an integer binding
     */
    public static <K> IntegerBinding filterThenFindFirstInteger(final ObservableMap<K, Integer> items, final Supplier<Integer> supplier, final ObservableValue<Predicate<? super Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Predicate<? super Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a long binding
     */
    public static <K> LongBinding filterThenFindFirstLong(final ObservableMap<K, Long> items, final Long defaultValue, final Predicate<? super Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.values().stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a long binding
     */
    public static <K> LongBinding filterThenFindFirstLong(final ObservableMap<K, Long> items, final Supplier<Long> supplier, final Predicate<? super Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.values().stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a long binding
     */
    public static <K> LongBinding filterThenFindFirstLong(final ObservableMap<K, Long> items, final Long defaultValue, final ObservableValue<Predicate<? super Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Predicate<? super Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a long binding
     */
    public static <K> LongBinding filterThenFindFirstLong(final ObservableMap<K, Long> items, final Supplier<Long> supplier, final ObservableValue<Predicate<? super Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Predicate<? super Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a float binding
     */
    public static <K> FloatBinding filterThenFindFirstFloat(final ObservableMap<K, Float> items, final Float defaultValue, final Predicate<? super Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.values().stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a float binding
     */
    public static <K> FloatBinding filterThenFindFirstFloat(final ObservableMap<K, Float> items, final Supplier<Float> supplier, final Predicate<? super Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.values().stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a float binding
     */
    public static <K> FloatBinding filterThenFindFirstFloat(final ObservableMap<K, Float> items, final Float defaultValue, final ObservableValue<Predicate<? super Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Predicate<? super Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a float binding
     */
    public static <K> FloatBinding filterThenFindFirstFloat(final ObservableMap<K, Float> items, final Supplier<Float> supplier, final ObservableValue<Predicate<? super Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Predicate<? super Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a double binding
     */
    public static <K> DoubleBinding filterThenFindFirstDouble(final ObservableMap<K, Double> items, final Double defaultValue, final Predicate<? super Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.values().stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a double binding
     */
    public static <K> DoubleBinding filterThenFindFirstDouble(final ObservableMap<K, Double> items, final Supplier<Double> supplier, final Predicate<? super Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.values().stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a double binding
     */
    public static <K> DoubleBinding filterThenFindFirstDouble(final ObservableMap<K, Double> items, final Double defaultValue, final ObservableValue<Predicate<? super Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Predicate<? super Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a double binding
     */
    public static <K> DoubleBinding filterThenFindFirstDouble(final ObservableMap<K, Double> items, final Supplier<Double> supplier, final ObservableValue<Predicate<? super Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Predicate<? super Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a string binding
     */
    public static <K> StringBinding filterThenFindFirstString(final ObservableMap<K, String> items, final String defaultValue, final Predicate<? super String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.values().stream().filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a string binding
     */
    public static <K> StringBinding filterThenFindFirstString(final ObservableMap<K, String> items, final Supplier<String> supplier, final Predicate<? super String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.values().stream().filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value
     * @return a string binding
     */
    public static <K> StringBinding filterThenFindFirstString(final ObservableMap<K, String> items, final String defaultValue, final ObservableValue<Predicate<? super String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Predicate<? super String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElse(defaultValue);
        }, items, filter);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value
     * @return a string binding
     */
    public static <K> StringBinding filterThenFindFirstString(final ObservableMap<K, String> items, final Supplier<String> supplier, final ObservableValue<Predicate<? super String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Predicate<? super String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).findFirst().orElseGet(supplier);
        }, items, filter);
    }

    /**
     * Creates an object binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableList<T> items, final R defaultValue, final Function<? super T, R> mapper, final Predicate<? super R> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<R> supplier, final Function<? super T, R> mapper, final Predicate<? super R> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableList<T> items, final R defaultValue, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<Predicate<? super R>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<R> supplier, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<Predicate<? super R>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableList<T> items, final Boolean defaultValue, final Function<? super T, Boolean> mapper, final Predicate<Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Boolean> supplier, final Function<? super T, Boolean> mapper, final Predicate<Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableList<T> items, final Boolean defaultValue, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<Predicate<Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Boolean> supplier, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<Predicate<Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableList<T> items, final Integer defaultValue, final Function<? super T, Integer> mapper, final Predicate<Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Integer> supplier, final Function<? super T, Integer> mapper, final Predicate<Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableList<T> items, final Integer defaultValue, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<Predicate<Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Integer> supplier, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<Predicate<Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableList<T> items, final Long defaultValue, final Function<? super T, Long> mapper, final Predicate<Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Long> supplier, final Function<? super T, Long> mapper, final Predicate<Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableList<T> items, final Long defaultValue, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<Predicate<Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Long> supplier, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<Predicate<Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableList<T> items, final Float defaultValue, final Function<? super T, Float> mapper, final Predicate<Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Float> supplier, final Function<? super T, Float> mapper, final Predicate<Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableList<T> items, final Float defaultValue, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<Predicate<Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Float> supplier, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<Predicate<Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableList<T> items, final Double defaultValue, final Function<? super T, Double> mapper, final Predicate<Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Double> supplier, final Function<? super T, Double> mapper, final Predicate<Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableList<T> items, final Double defaultValue, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<Predicate<Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<Double> supplier, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<Predicate<Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableList<T> items, final String defaultValue, final Function<? super T, String> mapper, final Predicate<String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<String> supplier, final Function<? super T, String> mapper, final Predicate<String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first element of an observable list after mapping and filtering.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableList<T> items, final String defaultValue, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<Predicate<String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable list after mapping and filtering.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableList<T> items, final Supplier<String> supplier, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<Predicate<String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableSet<T> items, final R defaultValue, final Function<? super T, R> mapper, final Predicate<? super R> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<R> supplier, final Function<? super T, R> mapper, final Predicate<? super R> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableSet<T> items, final R defaultValue, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<Predicate<? super R>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<R> supplier, final ObservableValue<Function<? super T, R>> mapper, final ObservableValue<Predicate<? super R>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableSet<T> items, final Boolean defaultValue, final Function<? super T, Boolean> mapper, final Predicate<Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Boolean> supplier, final Function<? super T, Boolean> mapper, final Predicate<Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableSet<T> items, final Boolean defaultValue, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<Predicate<Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a boolean binding
     */
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Boolean> supplier, final ObservableValue<Function<? super T, Boolean>> mapper, final ObservableValue<Predicate<Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableSet<T> items, final Integer defaultValue, final Function<? super T, Integer> mapper, final Predicate<Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Integer> supplier, final Function<? super T, Integer> mapper, final Predicate<Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableSet<T> items, final Integer defaultValue, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<Predicate<Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return an integer binding
     */
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Integer> supplier, final ObservableValue<Function<? super T, Integer>> mapper, final ObservableValue<Predicate<Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableSet<T> items, final Long defaultValue, final Function<? super T, Long> mapper, final Predicate<Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Long> supplier, final Function<? super T, Long> mapper, final Predicate<Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableSet<T> items, final Long defaultValue, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<Predicate<Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a long binding
     */
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Long> supplier, final ObservableValue<Function<? super T, Long>> mapper, final ObservableValue<Predicate<Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableSet<T> items, final Float defaultValue, final Function<? super T, Float> mapper, final Predicate<Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Float> supplier, final Function<? super T, Float> mapper, final Predicate<Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableSet<T> items, final Float defaultValue, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<Predicate<Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a float binding
     */
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Float> supplier, final ObservableValue<Function<? super T, Float>> mapper, final ObservableValue<Predicate<Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableSet<T> items, final Double defaultValue, final Function<? super T, Double> mapper, final Predicate<Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Double> supplier, final Function<? super T, Double> mapper, final Predicate<Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableSet<T> items, final Double defaultValue, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<Predicate<Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a double binding
     */
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<Double> supplier, final ObservableValue<Function<? super T, Double>> mapper, final ObservableValue<Predicate<Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableSet<T> items, final String defaultValue, final Function<? super T, String> mapper, final Predicate<String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<String> supplier, final Function<? super T, String> mapper, final Predicate<String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first element of an observable set after mapping and filtering.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableSet<T> items, final String defaultValue, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<Predicate<String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable set after mapping and filtering.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each element before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. after mapping.
     * @return a string binding
     */
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(final ObservableSet<T> items, final Supplier<String> supplier, final ObservableValue<Function<? super T, String>> mapper, final ObservableValue<Predicate<String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableMap<K, V> items, final R defaultValue, final Function<? super V, R> mapper, final Predicate<? super R> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<R> supplier, final Function<? super V, R> mapper, final Predicate<? super R> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableMap<K, V> items, final R defaultValue, final ObservableValue<Function<? super V, R>> mapper, final ObservableValue<Predicate<? super R>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<R> supplier, final ObservableValue<Function<? super V, R>> mapper, final ObservableValue<Predicate<? super R>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createObjectBinding(() -> {
            Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableMap<K, V> items, final Boolean defaultValue, final Function<? super V, Boolean> mapper, final Predicate<Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Boolean> supplier, final Function<? super V, Boolean> mapper, final Predicate<Boolean> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableMap<K, V> items, final Boolean defaultValue, final ObservableValue<Function<? super V, Boolean>> mapper, final ObservableValue<Predicate<Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Boolean> supplier, final ObservableValue<Function<? super V, Boolean>> mapper, final ObservableValue<Predicate<Boolean>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createBooleanBinding(() -> {
            Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Boolean> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableMap<K, V> items, final Integer defaultValue, final Function<? super V, Integer> mapper, final Predicate<Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Integer> supplier, final Function<? super V, Integer> mapper, final Predicate<Integer> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableMap<K, V> items, final Integer defaultValue, final ObservableValue<Function<? super V, Integer>> mapper, final ObservableValue<Predicate<Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Integer> supplier, final ObservableValue<Function<? super V, Integer>> mapper, final ObservableValue<Predicate<Integer>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createIntegerBinding(() -> {
            Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Integer> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(final ObservableMap<K, V> items, final Long defaultValue, final Function<? super V, Long> mapper, final Predicate<Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Long> supplier, final Function<? super V, Long> mapper, final Predicate<Long> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(final ObservableMap<K, V> items, final Long defaultValue, final ObservableValue<Function<? super V, Long>> mapper, final ObservableValue<Predicate<Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a long binding
     */
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Long> supplier, final ObservableValue<Function<? super V, Long>> mapper, final ObservableValue<Predicate<Long>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createLongBinding(() -> {
            Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Long> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableMap<K, V> items, final Float defaultValue, final Function<? super V, Float> mapper, final Predicate<Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Float> supplier, final Function<? super V, Float> mapper, final Predicate<Float> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableMap<K, V> items, final Float defaultValue, final ObservableValue<Function<? super V, Float>> mapper, final ObservableValue<Predicate<Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a float binding
     */
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Float> supplier, final ObservableValue<Function<? super V, Float>> mapper, final ObservableValue<Predicate<Float>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createFloatBinding(() -> {
            Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Float> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableMap<K, V> items, final Double defaultValue, final Function<? super V, Double> mapper, final Predicate<Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Double> supplier, final Function<? super V, Double> mapper, final Predicate<Double> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableMap<K, V> items, final Double defaultValue, final ObservableValue<Function<? super V, Double>> mapper, final ObservableValue<Predicate<Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a double binding
     */
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<Double> supplier, final ObservableValue<Function<? super V, Double>> mapper, final ObservableValue<Predicate<Double>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createDoubleBinding(() -> {
            Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<Double> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(final ObservableMap<K, V> items, final String defaultValue, final Function<? super V, String> mapper, final Predicate<String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<String> supplier, final Function<? super V, String> mapper, final Predicate<String> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> items.values().stream().map(mapper).filter(filter).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first value of an observable map after mapping and filtering.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param mapper       a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter       a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(final ObservableMap<K, V> items, final String defaultValue, final ObservableValue<Function<? super V, String>> mapper, final ObservableValue<Predicate<String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first value of an observable map after mapping and filtering.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param mapper   a non-interfering, stateless function to apply to the each value before filtering.
     * @param filter   a non-interfering, stateless predicate to apply to the each value after mapping.
     * @return a string binding
     */
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(final ObservableMap<K, V> items, final Supplier<String> supplier, final ObservableValue<Function<? super V, String>> mapper, final ObservableValue<Predicate<String>> filter) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        return createStringBinding(() -> {
            Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<String> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().map(mapperValue).filter(filterValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableList<T> items, final R defaultValue, final Predicate<? super T> filter, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableList<T> items, final Supplier<R> supplier, final Predicate<? super T> filter, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableList<T> items, final R defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableList<T> items, final Supplier<R> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableList<T> items, final Boolean defaultValue, final Predicate<? super T> filter, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableList<T> items, final Supplier<Boolean> supplier, final Predicate<? super T> filter, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableList<T> items, final Boolean defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableList<T> items, final Supplier<Boolean> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableList<T> items, final Integer defaultValue, final Predicate<? super T> filter, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableList<T> items, final Supplier<Integer> supplier, final Predicate<? super T> filter, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableList<T> items, final Integer defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableList<T> items, final Supplier<Integer> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableList<T> items, final Long defaultValue, final Predicate<? super T> filter, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableList<T> items, final Supplier<Long> supplier, final Predicate<? super T> filter, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableList<T> items, final Long defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableList<T> items, final Supplier<Long> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableList<T> items, final Float defaultValue, final Predicate<? super T> filter, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableList<T> items, final Supplier<Float> supplier, final Predicate<? super T> filter, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableList<T> items, final Float defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableList<T> items, final Supplier<Float> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableList<T> items, final Double defaultValue, final Predicate<? super T> filter, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableList<T> items, final Supplier<Double> supplier, final Predicate<? super T> filter, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableList<T> items, final Double defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableList<T> items, final Supplier<Double> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableList<T> items, final String defaultValue, final Predicate<? super T> filter, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableList<T> items, final Supplier<String> supplier, final Predicate<? super T> filter, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering and mapping.
     *
     * @param items        the observable list of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableList<T> items, final String defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable list after filtering and mapping.
     *
     * @param items    the observable list of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableList<T> items, final Supplier<String> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableSet<T> items, final R defaultValue, final Predicate<? super T> filter, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableSet<T> items, final Supplier<R> supplier, final Predicate<? super T> filter, final Function<? super T, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableSet<T> items, final R defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an object binding
     */
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableSet<T> items, final Supplier<R> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, R>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableSet<T> items, final Boolean defaultValue, final Predicate<? super T> filter, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableSet<T> items, final Supplier<Boolean> supplier, final Predicate<? super T> filter, final Function<? super T, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableSet<T> items, final Boolean defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a boolean binding
     */
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableSet<T> items, final Supplier<Boolean> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableSet<T> items, final Integer defaultValue, final Predicate<? super T> filter, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableSet<T> items, final Supplier<Integer> supplier, final Predicate<? super T> filter, final Function<? super T, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableSet<T> items, final Integer defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return an integer binding
     */
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableSet<T> items, final Supplier<Integer> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Function<? super T, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableSet<T> items, final Long defaultValue, final Predicate<? super T> filter, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableSet<T> items, final Supplier<Long> supplier, final Predicate<? super T> filter, final Function<? super T, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableSet<T> items, final Long defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a long binding
     */
    public static <T> LongBinding filterThenMapToLongThenFindFirst(final ObservableSet<T> items, final Supplier<Long> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Function<? super T, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableSet<T> items, final Float defaultValue, final Predicate<? super T> filter, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableSet<T> items, final Supplier<Float> supplier, final Predicate<? super T> filter, final Function<? super T, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableSet<T> items, final Float defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a float binding
     */
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableSet<T> items, final Supplier<Float> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Function<? super T, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableSet<T> items, final Double defaultValue, final Predicate<? super T> filter, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableSet<T> items, final Supplier<Double> supplier, final Predicate<? super T> filter, final Function<? super T, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableSet<T> items, final Double defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a double binding
     */
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableSet<T> items, final Supplier<Double> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Function<? super T, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableSet<T> items, final String defaultValue, final Predicate<? super T> filter, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableSet<T> items, final Supplier<String> supplier, final Predicate<? super T> filter, final Function<? super T, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering and mapping.
     *
     * @param items        the observable set of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableSet<T> items, final String defaultValue, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first element of an observable set after filtering and mapping.
     *
     * @param items    the observable set of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each element. before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each element after filtering.
     * @return a string binding
     */
    public static <T> StringBinding filterThenMapToStringThenFindFirst(final ObservableSet<T> items, final Supplier<String> supplier, final ObservableValue<Predicate<? super T>> filter, final ObservableValue<Function<? super T, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            Function<? super T, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super T> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableMap<K, V> items, final R defaultValue, final Predicate<? super V> filter, final Function<? super V, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableMap<K, V> items, final Supplier<R> supplier, final Predicate<? super V> filter, final Function<? super V, R> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableMap<K, V> items, final R defaultValue, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, R>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an object binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return an object binding
     */
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(final ObservableMap<K, V> items, final Supplier<R> supplier, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, R>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createObjectBinding(() -> {
            Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableMap<K, V> items, final Boolean defaultValue, final Predicate<? super V> filter, final Function<? super V, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableMap<K, V> items, final Supplier<Boolean> supplier, final Predicate<? super V> filter, final Function<? super V, Boolean> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableMap<K, V> items, final Boolean defaultValue, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a boolean binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a boolean binding
     */
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(final ObservableMap<K, V> items, final Supplier<Boolean> supplier, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Boolean>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createBooleanBinding(() -> {
            Function<? super V, Boolean> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableMap<K, V> items, final Integer defaultValue, final Predicate<? super V> filter, final Function<? super V, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableMap<K, V> items, final Supplier<Integer> supplier, final Predicate<? super V> filter, final Function<? super V, Integer> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableMap<K, V> items, final Integer defaultValue, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates an integer binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return an integer binding
     */
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(final ObservableMap<K, V> items, final Supplier<Integer> supplier, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Integer>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createIntegerBinding(() -> {
            Function<? super V, Integer> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a long binding
     */
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(final ObservableMap<K, V> items, final Long defaultValue, final Predicate<? super V> filter, final Function<? super V, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a long binding
     */
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(final ObservableMap<K, V> items, final Supplier<Long> supplier, final Predicate<? super V> filter, final Function<? super V, Long> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a long binding
     */
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(final ObservableMap<K, V> items, final Long defaultValue, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a long binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a long binding
     */
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(final ObservableMap<K, V> items, final Supplier<Long> supplier, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Long>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createLongBinding(() -> {
            Function<? super V, Long> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a float binding
     */
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableMap<K, V> items, final Float defaultValue, final Predicate<? super V> filter, final Function<? super V, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a float binding
     */
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableMap<K, V> items, final Supplier<Float> supplier, final Predicate<? super V> filter, final Function<? super V, Float> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a float binding
     */
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableMap<K, V> items, final Float defaultValue, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a float binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a float binding
     */
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(final ObservableMap<K, V> items, final Supplier<Float> supplier, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Float>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createFloatBinding(() -> {
            Function<? super V, Float> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a double binding
     */
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableMap<K, V> items, final Double defaultValue, final Predicate<? super V> filter, final Function<? super V, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a double binding
     */
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableMap<K, V> items, final Supplier<Double> supplier, final Predicate<? super V> filter, final Function<? super V, Double> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a double binding
     */
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableMap<K, V> items, final Double defaultValue, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a double binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a double binding
     */
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(final ObservableMap<K, V> items, final Supplier<Double> supplier, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, Double>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createDoubleBinding(() -> {
            Function<? super V, Double> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a string binding
     */
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(final ObservableMap<K, V> items, final String defaultValue, final Predicate<? super V> filter, final Function<? super V, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElse(defaultValue), items);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a string binding
     */
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(final ObservableMap<K, V> items, final Supplier<String> supplier, final Predicate<? super V> filter, final Function<? super V, String> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> items.values().stream().filter(filter).map(mapper).findFirst().orElseGet(supplier), items);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering and mapping.
     *
     * @param items        the observable map of items.
     * @param defaultValue the value to be returned if there is no value present.
     * @param filter       a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper       a non-interfering, stateless function to apply to the each value after filtering.
     * @return a string binding
     */
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(final ObservableMap<K, V> items, final String defaultValue, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElse(defaultValue);
        }, items, mapper, filter);
    }

    /**
     * Creates a string binding with the first value of an observable map after filtering and mapping.
     *
     * @param items    the observable map of items.
     * @param supplier a {@code Supplier} whose result is returned if no value is present.
     * @param filter   a non-interfering, stateless predicate to apply to the each value before mapping.
     * @param mapper   a non-interfering, stateless function to apply to the each value after filtering.
     * @return a string binding
     */
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(final ObservableMap<K, V> items, final Supplier<String> supplier, final ObservableValue<Predicate<? super V>> filter, final ObservableValue<Function<? super V, String>> mapper) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(supplier, ERROR_SUPPLIER_NULL);
        requireNonNull(filter, ERROR_FILTER_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        return createStringBinding(() -> {
            Function<? super V, String> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super V> filterValue = filter.getValue();
            requireNonNull(filterValue, ERROR_FILTER_NULL);
            return items.values().stream().filter(filterValue).map(mapperValue).findFirst().orElseGet(supplier);
        }, items, mapper, filter);
    }
}
