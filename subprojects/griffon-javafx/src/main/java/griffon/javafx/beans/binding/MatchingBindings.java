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
package griffon.javafx.beans.binding;

import griffon.annotations.core.Nonnull;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static javafx.beans.binding.Bindings.createBooleanBinding;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public final class MatchingBindings {
    private static final String ERROR_ITEMS_NULL = "Argument 'items' must not be null";
    private static final String ERROR_PREDICATE_NULL = "Argument 'predicate' must not be null";
    private static final String ERROR_MAPPER_NULL = "Argument 'mapper' must not be null";

    private MatchingBindings() {
        // prevent instantiation
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding anyMatch(@Nonnull final ObservableList<T> items, @Nonnull final Predicate<? super T> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().anyMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding anyMatch(@Nonnull final ObservableList<T> items, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).anyMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding anyMatch(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().anyMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding anyMatch(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().map(mapperValue).anyMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding noneMatch(@Nonnull final ObservableList<T> items, @Nonnull final Predicate<? super T> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().noneMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding noneMatch(@Nonnull final ObservableList<T> items, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).noneMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding noneMatch(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().noneMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding noneMatch(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().map(mapperValue).noneMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding allMatch(@Nonnull final ObservableList<T> items, @Nonnull final Predicate<? super T> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().allMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding allMatch(@Nonnull final ObservableList<T> items, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).allMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding allMatch(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().allMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable list of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding allMatch(@Nonnull final ObservableList<T> items, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().map(mapperValue).allMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding anyMatch(@Nonnull final ObservableSet<T> items, @Nonnull final Predicate<? super T> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().anyMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding anyMatch(@Nonnull final ObservableSet<T> items, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).anyMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding anyMatch(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().anyMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding anyMatch(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().map(mapperValue).anyMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding noneMatch(@Nonnull final ObservableSet<T> items, @Nonnull final Predicate<? super T> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().noneMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding noneMatch(@Nonnull final ObservableSet<T> items, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).noneMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding noneMatch(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().noneMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding noneMatch(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().map(mapperValue).noneMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding allMatch(@Nonnull final ObservableSet<T> items, @Nonnull final Predicate<? super T> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().allMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding allMatch(@Nonnull final ObservableSet<T> items, @Nonnull final Function<? super T, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.stream().map(mapper).allMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T> BooleanBinding allMatch(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<Predicate<? super T>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super T> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().allMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable set of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <T, R> BooleanBinding allMatch(@Nonnull final ObservableSet<T> items, @Nonnull final ObservableValue<Function<? super T, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super T, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.stream().map(mapperValue).allMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding anyMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final Predicate<? super V> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.values().stream().anyMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V, R> BooleanBinding anyMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final Function<? super V, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.values().stream().map(mapper).anyMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding anyMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<Predicate<? super V>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super V> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.values().stream().anyMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>anyMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V, R> BooleanBinding anyMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<Function<? super V, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.values().stream().map(mapperValue).anyMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding noneMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final Predicate<? super V> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.values().stream().noneMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V, R> BooleanBinding noneMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final Function<? super V, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.values().stream().map(mapper).noneMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding noneMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<Predicate<? super V>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super V> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.values().stream().noneMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>noneMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V, R> BooleanBinding noneMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<Function<? super V, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.values().stream().map(mapperValue).noneMatch(predicateValue);
        }, items, predicate, mapper);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding allMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final Predicate<? super V> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.values().stream().allMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V, R> BooleanBinding allMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final Function<? super V, R> mapper, @Nonnull final Predicate<? super R> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> items.values().stream().map(mapper).allMatch(predicate), items);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V> BooleanBinding allMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<Predicate<? super V>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Predicate<? super V> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.values().stream().allMatch(predicateValue);
        }, items, predicate);
    }

    /**
     * Creates a boolean binding based on a <tt>allMatch</tt> predicate applied to the items.
     *
     * @param items     the observable map of items.
     * @param mapper    a non-interfering, stateless function to apply to the each value before matching.
     * @param predicate a non-interfering, stateless predicate to apply to the each element.
     *
     * @return a boolean binding
     */
    @Nonnull
    public static <K, V, R> BooleanBinding allMatch(@Nonnull final ObservableMap<K, V> items, @Nonnull final ObservableValue<Function<? super V, R>> mapper, @Nonnull final ObservableValue<Predicate<? super R>> predicate) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        requireNonNull(mapper, ERROR_MAPPER_NULL);
        requireNonNull(predicate, ERROR_PREDICATE_NULL);
        return createBooleanBinding(() -> {
            Function<? super V, R> mapperValue = mapper.getValue();
            requireNonNull(mapperValue, ERROR_MAPPER_NULL);
            Predicate<? super R> predicateValue = predicate.getValue();
            requireNonNull(predicateValue, ERROR_PREDICATE_NULL);
            return items.values().stream().map(mapperValue).allMatch(predicateValue);
        }, items, predicate, mapper);
    }
}
