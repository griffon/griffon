/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final T defaultValue, @Nonnull final Predicate<? super T> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final Predicate<? super T> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final T defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableList<Boolean> items, @Nonnull final Boolean defaultValue, @Nonnull final Predicate<? super Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableList<Boolean> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Predicate<? super Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableList<Boolean> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Predicate<? super Boolean>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableList<Boolean> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Predicate<? super Boolean>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableList<Integer> items, @Nonnull final Integer defaultValue, @Nonnull final Predicate<? super Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableList<Integer> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Predicate<? super Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableList<Integer> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Predicate<? super Integer>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableList<Integer> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Predicate<? super Integer>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableList<Long> items, @Nonnull final Long defaultValue, @Nonnull final Predicate<? super Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableList<Long> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Predicate<? super Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableList<Long> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Predicate<? super Long>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableList<Long> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Predicate<? super Long>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableList<Float> items, @Nonnull final Float defaultValue, @Nonnull final Predicate<? super Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableList<Float> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Predicate<? super Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableList<Float> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Predicate<? super Float>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableList<Float> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Predicate<? super Float>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableList<Double> items, @Nonnull final Double defaultValue, @Nonnull final Predicate<? super Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableList<Double> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Predicate<? super Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableList<Double> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Predicate<? super Double>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableList<Double> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Predicate<? super Double>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableList<String> items, @Nonnull final String defaultValue, @Nonnull final Predicate<? super String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableList<String> items, @Nonnull final Supplier<String> supplier, @Nonnull final Predicate<? super String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableList<String> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Predicate<? super String>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableList<String> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Predicate<? super String>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final T defaultValue, @Nonnull final Predicate<? super T> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final Predicate<? super T> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final T defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T> ObjectBinding<T> filterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<T> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableSet<Boolean> items, @Nonnull final Boolean defaultValue, @Nonnull final Predicate<? super Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableSet<Boolean> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Predicate<? super Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableSet<Boolean> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Predicate<? super Boolean>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableSet<Boolean> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Predicate<? super Boolean>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableSet<Integer> items, @Nonnull final Integer defaultValue, @Nonnull final Predicate<? super Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableSet<Integer> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Predicate<? super Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableSet<Integer> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Predicate<? super Integer>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableSet<Integer> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Predicate<? super Integer>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableSet<Long> items, @Nonnull final Long defaultValue, @Nonnull final Predicate<? super Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableSet<Long> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Predicate<? super Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableSet<Long> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Predicate<? super Long>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static LongBinding filterThenFindFirstLong(@Nonnull final ObservableSet<Long> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Predicate<? super Long>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableSet<Float> items, @Nonnull final Float defaultValue, @Nonnull final Predicate<? super Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableSet<Float> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Predicate<? super Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableSet<Float> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Predicate<? super Float>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableSet<Float> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Predicate<? super Float>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableSet<Double> items, @Nonnull final Double defaultValue, @Nonnull final Predicate<? super Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableSet<Double> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Predicate<? super Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableSet<Double> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Predicate<? super Double>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableSet<Double> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Predicate<? super Double>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableSet<String> items, @Nonnull final String defaultValue, @Nonnull final Predicate<? super String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableSet<String> items, @Nonnull final Supplier<String> supplier, @Nonnull final Predicate<? super String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableSet<String> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Predicate<? super String>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static StringBinding filterThenFindFirstString(@Nonnull final ObservableSet<String> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Predicate<? super String>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> filterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final V defaultValue, @Nonnull final Predicate<? super V> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> filterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final Predicate<? super V> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> filterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final V defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V> ObjectBinding<V> filterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<V> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K> BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableMap<K, Boolean> items, @Nonnull final Boolean defaultValue, @Nonnull final Predicate<? super Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K> BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableMap<K, Boolean> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Predicate<? super Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K> BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableMap<K, Boolean> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Predicate<? super Boolean>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K> BooleanBinding filterThenFindFirstBoolean(@Nonnull final ObservableMap<K, Boolean> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Predicate<? super Boolean>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K> IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableMap<K, Integer> items, @Nonnull final Integer defaultValue, @Nonnull final Predicate<? super Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K> IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableMap<K, Integer> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Predicate<? super Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K> IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableMap<K, Integer> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Predicate<? super Integer>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K> IntegerBinding filterThenFindFirstInteger(@Nonnull final ObservableMap<K, Integer> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Predicate<? super Integer>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K> LongBinding filterThenFindFirstLong(@Nonnull final ObservableMap<K, Long> items, @Nonnull final Long defaultValue, @Nonnull final Predicate<? super Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K> LongBinding filterThenFindFirstLong(@Nonnull final ObservableMap<K, Long> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Predicate<? super Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K> LongBinding filterThenFindFirstLong(@Nonnull final ObservableMap<K, Long> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Predicate<? super Long>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K> LongBinding filterThenFindFirstLong(@Nonnull final ObservableMap<K, Long> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Predicate<? super Long>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K> FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableMap<K, Float> items, @Nonnull final Float defaultValue, @Nonnull final Predicate<? super Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K> FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableMap<K, Float> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Predicate<? super Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K> FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableMap<K, Float> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Predicate<? super Float>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K> FloatBinding filterThenFindFirstFloat(@Nonnull final ObservableMap<K, Float> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Predicate<? super Float>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K> DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableMap<K, Double> items, @Nonnull final Double defaultValue, @Nonnull final Predicate<? super Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K> DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableMap<K, Double> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Predicate<? super Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K> DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableMap<K, Double> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Predicate<? super Double>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K> DoubleBinding filterThenFindFirstDouble(@Nonnull final ObservableMap<K, Double> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Predicate<? super Double>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K> StringBinding filterThenFindFirstString(@Nonnull final ObservableMap<K, String> items, @Nonnull final String defaultValue, @Nonnull final Predicate<? super String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K> StringBinding filterThenFindFirstString(@Nonnull final ObservableMap<K, String> items, @Nonnull final Supplier<String> supplier, @Nonnull final Predicate<? super String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K> StringBinding filterThenFindFirstString(@Nonnull final ObservableMap<K, String> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Predicate<? super String>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K> StringBinding filterThenFindFirstString(@Nonnull final ObservableMap<K, String> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Predicate<? super String>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final R defaultValue, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final R defaultValue, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Boolean defaultValue, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final Predicate<Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final Predicate<Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<Predicate<Boolean>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<Predicate<Boolean>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Integer defaultValue, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final Predicate<Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final Predicate<Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<Predicate<Integer>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<Predicate<Integer>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Long defaultValue, @Nonnull final Function<? super T, Long> mapper, @Nonnull final Predicate<Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Function<? super T, Long> mapper, @Nonnull final Predicate<Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<Predicate<Long>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<Predicate<Long>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Float defaultValue, @Nonnull final Function<? super T, Float> mapper, @Nonnull final Predicate<Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Function<? super T, Float> mapper, @Nonnull final Predicate<Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<Predicate<Float>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<Predicate<Float>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Double defaultValue, @Nonnull final Function<? super T, Double> mapper, @Nonnull final Predicate<Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Function<? super T, Double> mapper, @Nonnull final Predicate<Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<Predicate<Double>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<Predicate<Double>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final String defaultValue, @Nonnull final Function<? super T, String> mapper, @Nonnull final Predicate<String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final Function<? super T, String> mapper, @Nonnull final Predicate<String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<Predicate<String>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<Predicate<String>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final R defaultValue, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final R defaultValue, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Boolean defaultValue, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final Predicate<Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Function<? super T, Boolean> mapper, @Nonnull final Predicate<Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<Predicate<Boolean>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper, @Nonnull final ObservableValue<Predicate<Boolean>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Integer defaultValue, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final Predicate<Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Function<? super T, Integer> mapper, @Nonnull final Predicate<Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<Predicate<Integer>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper, @Nonnull final ObservableValue<Predicate<Integer>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Long defaultValue, @Nonnull final Function<? super T, Long> mapper, @Nonnull final Predicate<Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Function<? super T, Long> mapper, @Nonnull final Predicate<Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<Predicate<Long>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Function<? super T, Long>> mapper, @Nonnull final ObservableValue<Predicate<Long>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Float defaultValue, @Nonnull final Function<? super T, Float> mapper, @Nonnull final Predicate<Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Function<? super T, Float> mapper, @Nonnull final Predicate<Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<Predicate<Float>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Function<? super T, Float>> mapper, @Nonnull final ObservableValue<Predicate<Float>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Double defaultValue, @Nonnull final Function<? super T, Double> mapper, @Nonnull final Predicate<Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Function<? super T, Double> mapper, @Nonnull final Predicate<Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<Predicate<Double>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Function<? super T, Double>> mapper, @Nonnull final ObservableValue<Predicate<Double>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final String defaultValue, @Nonnull final Function<? super T, String> mapper, @Nonnull final Predicate<String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final Function<? super T, String> mapper, @Nonnull final Predicate<String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<Predicate<String>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Function<? super T, String>> mapper, @Nonnull final ObservableValue<Predicate<String>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final R defaultValue, @Nonnull final Function<? super V, R> mapper, @Nonnull final Predicate<? super R> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<R> supplier, @Nonnull final Function<? super V, R> mapper, @Nonnull final Predicate<? super R> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final R defaultValue, @Nonnull final ObservableValue<Function<? super V, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> mapThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Function<? super V, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Boolean defaultValue, @Nonnull final Function<? super V, Boolean> mapper, @Nonnull final Predicate<Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Function<? super V, Boolean> mapper, @Nonnull final Predicate<Boolean> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper, @Nonnull final ObservableValue<Predicate<Boolean>> filter) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding mapToBooleanThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper, @Nonnull final ObservableValue<Predicate<Boolean>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Integer defaultValue, @Nonnull final Function<? super V, Integer> mapper, @Nonnull final Predicate<Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Function<? super V, Integer> mapper, @Nonnull final Predicate<Integer> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper, @Nonnull final ObservableValue<Predicate<Integer>> filter) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding mapToIntegerThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper, @Nonnull final ObservableValue<Predicate<Integer>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Long defaultValue, @Nonnull final Function<? super V, Long> mapper, @Nonnull final Predicate<Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Function<? super V, Long> mapper, @Nonnull final Predicate<Long> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Function<? super V, Long>> mapper, @Nonnull final ObservableValue<Predicate<Long>> filter) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding mapToLongThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Function<? super V, Long>> mapper, @Nonnull final ObservableValue<Predicate<Long>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Float defaultValue, @Nonnull final Function<? super V, Float> mapper, @Nonnull final Predicate<Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Function<? super V, Float> mapper, @Nonnull final Predicate<Float> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Function<? super V, Float>> mapper, @Nonnull final ObservableValue<Predicate<Float>> filter) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding mapToFloatThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Function<? super V, Float>> mapper, @Nonnull final ObservableValue<Predicate<Float>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Double defaultValue, @Nonnull final Function<? super V, Double> mapper, @Nonnull final Predicate<Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Function<? super V, Double> mapper, @Nonnull final Predicate<Double> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Function<? super V, Double>> mapper, @Nonnull final ObservableValue<Predicate<Double>> filter) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding mapToDoubleThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Function<? super V, Double>> mapper, @Nonnull final ObservableValue<Predicate<Double>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final String defaultValue, @Nonnull final Function<? super V, String> mapper, @Nonnull final Predicate<String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<String> supplier, @Nonnull final Function<? super V, String> mapper, @Nonnull final Predicate<String> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Function<? super V, String>> mapper, @Nonnull final ObservableValue<Predicate<String>> filter) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding mapToStringThenFilterThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Function<? super V, String>> mapper, @Nonnull final ObservableValue<Predicate<String>> filter) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final R defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final R defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Boolean defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Integer defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Long defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Float defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Double defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final String defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableList<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final R defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final R defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <T, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, R>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Boolean defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Boolean>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Integer defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <T> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Integer>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Long defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <T> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Long>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Float defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <T> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Float>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Double defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <T> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, Double>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final String defaultValue, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final Predicate<? super T> filter, @Nonnull final Function<? super T, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <T> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableSet<T> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Predicate<? super T>> filter, @Nonnull final ObservableValue<Function<? super T, String>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final R defaultValue, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<R> supplier, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, R> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final R defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, R>> mapper) {
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
     *
     * @return an object binding
     */
    @Nonnull
    public static <K, V, R> ObjectBinding<R> filterThenMapThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<R> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, R>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Boolean defaultValue, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Boolean> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Boolean defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper) {
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
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding filterThenMapToBooleanThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Boolean> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Boolean>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Integer defaultValue, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Integer> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Integer defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper) {
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
     *
     * @return an integer binding
     */
    @Nonnull
    public static <K, V> IntegerBinding filterThenMapToIntegerThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Integer> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Integer>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Long defaultValue, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Long> supplier, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Long> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Long defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Long>> mapper) {
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
     *
     * @return a long binding
     */
    @Nonnull
    public static <K, V> LongBinding filterThenMapToLongThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Long> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Long>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Float defaultValue, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Float> supplier, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Float> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Float defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Float>> mapper) {
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
     *
     * @return a float binding
     */
    @Nonnull
    public static <K, V> FloatBinding filterThenMapToFloatThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Float> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Float>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Double defaultValue, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Double> supplier, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, Double> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Double defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Double>> mapper) {
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
     *
     * @return a double binding
     */
    @Nonnull
    public static <K, V> DoubleBinding filterThenMapToDoubleThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<Double> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, Double>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final String defaultValue, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<String> supplier, @Nonnull final Predicate<? super V> filter, @Nonnull final Function<? super V, String> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final String defaultValue, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, String>> mapper) {
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
     *
     * @return a string binding
     */
    @Nonnull
    public static <K, V> StringBinding filterThenMapToStringThenFindFirst(@Nonnull final ObservableMap<K, V> items, @Nonnull final Supplier<String> supplier, @Nonnull final ObservableValue<Predicate<? super V>> filter, @Nonnull final ObservableValue<Function<? super V, String>> mapper) {
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
