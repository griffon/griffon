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
package griffon.javafx.collections;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
public interface ObservableStream<T> {
    @Nonnull
    ObservableStream<T> limit(long maxSize);

    @Nonnull
    ObservableStream<T> limit(@Nonnull ObservableLongValue maxSize);

    @Nonnull
    ObservableStream<T> skip(long n);

    @Nonnull
    ObservableStream<T> skip(@Nonnull ObservableLongValue n);

    @Nonnull
    ObservableStream<T> distinct();

    @Nonnull
    ObservableStream<T> sorted();

    @Nonnull
    ObservableStream<T> sorted(@Nonnull Comparator<? super T> comparator);

    @Nonnull
    ObservableStream<T> sorted(@Nonnull ObservableValue<Comparator<? super T>> comparator);

    @Nonnull
    ObservableStream<T> filter(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    <R> ObservableStream<R> map(@Nonnull Function<? super T, ? extends R> mapper);

    @Nonnull
    <R> ObservableStream<R> flatMap(@Nonnull Function<? super T, ? extends ObservableStream<? extends R>> mapper);

    @Nonnull
    ObservableStream<T> filter(@Nonnull ObservableValue<Predicate<? super T>> predicate);

    @Nonnull
    <R> ObservableStream<R> map(@Nonnull ObservableValue<Function<? super T, ? extends R>> mapper);

    @Nonnull
    <R> ObservableStream<R> flatMap(@Nonnull ObservableValue<Function<? super T, ? extends ObservableStream<? extends R>>> mapper);

    @Nonnull
    ObjectBinding<T> reduce(@Nonnull BinaryOperator<T> accumulator);

    @Nonnull
    ObjectBinding<T> reduce(@Nullable T defaultValue, @Nonnull BinaryOperator<T> accumulator);

    @Nonnull
    ObjectBinding<T> reduce(@Nonnull Supplier<T> supplier, @Nonnull BinaryOperator<T> accumulator);

    @Nonnull
    <U> ObjectBinding<U> reduce(@Nullable U identity, @Nonnull BiFunction<U, ? super T, U> accumulator, @Nonnull BinaryOperator<U> combiner);

    @Nonnull
    ObjectBinding<T> reduce(@Nonnull ObservableValue<BinaryOperator<T>> accumulator);

    @Nonnull
    ObjectBinding<T> reduce(@Nullable T defaultValue, @Nonnull ObservableValue<BinaryOperator<T>> accumulator);

    @Nonnull
    ObjectBinding<T> reduce(@Nonnull Supplier<T> supplier, @Nonnull ObservableValue<BinaryOperator<T>> accumulator);

    @Nonnull
    <U> ObjectBinding<U> reduce(@Nonnull ObservableValue<U> identity, @Nonnull ObservableValue<BiFunction<U, ? super T, U>> accumulator, @Nonnull ObservableValue<BinaryOperator<U>> combiner);

    @Nonnull
    ObjectBinding<T> min(@Nonnull Comparator<? super T> comparator);

    @Nonnull
    ObjectBinding<T> max(@Nonnull Comparator<? super T> comparator);

    @Nonnull
    ObjectBinding<T> min(@Nullable T defaultValue, @Nonnull Comparator<? super T> comparator);

    @Nonnull
    ObjectBinding<T> max(@Nullable T defaultValue, @Nonnull Comparator<? super T> comparator);

    @Nonnull
    ObjectBinding<T> min(@Nonnull Supplier<T> supplier, @Nonnull Comparator<? super T> comparator);

    @Nonnull
    ObjectBinding<T> max(@Nonnull Supplier<T> supplier, @Nonnull Comparator<? super T> comparator);

    @Nonnull
    BooleanBinding anyMatch(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    BooleanBinding allMatch(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    BooleanBinding noneMatch(@Nonnull Predicate<? super T> predicate);

    @Nonnull
    ObjectBinding<T> min(@Nonnull ObservableValue<Comparator<? super T>> comparator);

    @Nonnull
    ObjectBinding<T> max(@Nonnull ObservableValue<Comparator<? super T>> comparator);

    @Nonnull
    ObjectBinding<T> min(@Nullable T defaultValue, @Nonnull ObservableValue<Comparator<? super T>> comparator);

    @Nonnull
    ObjectBinding<T> max(@Nullable T defaultValue, @Nonnull ObservableValue<Comparator<? super T>> comparator);

    @Nonnull
    ObjectBinding<T> min(@Nonnull Supplier<T> supplier, @Nonnull ObservableValue<Comparator<? super T>> comparator);

    @Nonnull
    ObjectBinding<T> max(@Nonnull Supplier<T> supplier, @Nonnull ObservableValue<Comparator<? super T>> comparator);

    @Nonnull
    BooleanBinding anyMatch(@Nonnull ObservableValue<Predicate<? super T>> predicate);

    @Nonnull
    BooleanBinding allMatch(@Nonnull ObservableValue<Predicate<? super T>> predicate);

    @Nonnull
    BooleanBinding noneMatch(@Nonnull ObservableValue<Predicate<? super T>> predicate);

    @Nonnull
    ObjectBinding<T> findFirst();

    @Nonnull
    ObjectBinding<T> findFirst(@Nullable T defaultValue);

    @Nonnull
    ObjectBinding<T> findFirst(@Nonnull Supplier<T> supplier);

    @Nonnull
    ObjectBinding<T> findAny();

    @Nonnull
    ObjectBinding<T> findAny(@Nullable T defaultValue);

    @Nonnull
    ObjectBinding<T> findAny(@Nonnull Supplier<T> supplier);
}
