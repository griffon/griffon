/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.javafx.groovy

import griffon.annotations.core.Nonnull
import griffon.javafx.beans.binding.MatchingBindings
import groovy.transform.CompileStatic
import javafx.beans.binding.BooleanBinding
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet

import java.util.function.Function
import java.util.function.Predicate

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
@CompileStatic
final class MatchingBindingsExtension {
    @Nonnull
    static <T> BooleanBinding anyMatch(@Nonnull ObservableList<T> self, @Nonnull Predicate<? super T> predicate) {
        MatchingBindings.anyMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding anyMatch(
        @Nonnull ObservableList<T> self,
        @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.anyMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding anyMatch(
        @Nonnull ObservableList<T> self, @Nonnull ObservableValue<Predicate<? super T>> predicate) {
        MatchingBindings.anyMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding anyMatch(
        @Nonnull ObservableList<T> self,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.anyMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding noneMatch(@Nonnull ObservableList<T> self, @Nonnull Predicate<? super T> predicate) {
        MatchingBindings.noneMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding noneMatch(
        @Nonnull ObservableList<T> self,
        @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.noneMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding noneMatch(
        @Nonnull ObservableList<T> self, @Nonnull ObservableValue<Predicate<? super T>> predicate) {
        MatchingBindings.noneMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding noneMatch(
        @Nonnull ObservableList<T> self,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.noneMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding allMatch(@Nonnull ObservableList<T> self, @Nonnull Predicate<? super T> predicate) {
        MatchingBindings.allMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding allMatch(
        @Nonnull ObservableList<T> self,
        @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.allMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding allMatch(
        @Nonnull ObservableList<T> self, @Nonnull ObservableValue<Predicate<? super T>> predicate) {
        MatchingBindings.allMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding allMatch(
        @Nonnull ObservableList<T> self,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.allMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding anyMatch(@Nonnull ObservableSet<T> self, @Nonnull Predicate<? super T> predicate) {
        MatchingBindings.anyMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding anyMatch(
        @Nonnull ObservableSet<T> self,
        @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.anyMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding anyMatch(
        @Nonnull ObservableSet<T> self, @Nonnull ObservableValue<Predicate<? super T>> predicate) {
        MatchingBindings.anyMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding anyMatch(
        @Nonnull ObservableSet<T> self,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.anyMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding noneMatch(@Nonnull ObservableSet<T> self, @Nonnull Predicate<? super T> predicate) {
        MatchingBindings.noneMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding noneMatch(
        @Nonnull ObservableSet<T> self,
        @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.noneMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding noneMatch(
        @Nonnull ObservableSet<T> self, @Nonnull ObservableValue<Predicate<? super T>> predicate) {
        MatchingBindings.noneMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding noneMatch(
        @Nonnull ObservableSet<T> self,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.noneMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding allMatch(@Nonnull ObservableSet<T> self, @Nonnull Predicate<? super T> predicate) {
        MatchingBindings.allMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding allMatch(
        @Nonnull ObservableSet<T> self,
        @Nonnull Function<? super T, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.allMatch(self, mapper, predicate)
    }

    @Nonnull
    static <T> BooleanBinding allMatch(
        @Nonnull ObservableSet<T> self, @Nonnull ObservableValue<Predicate<? super T>> predicate) {
        MatchingBindings.allMatch(self, predicate)
    }

    @Nonnull
    static <T, R> BooleanBinding allMatch(
        @Nonnull ObservableSet<T> self,
        @Nonnull ObservableValue<Function<? super T, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.allMatch(self, mapper, predicate)
    }

    @Nonnull
    static <K, V> BooleanBinding anyMatch(@Nonnull ObservableMap<K, V> self, @Nonnull Predicate<? super V> predicate) {
        MatchingBindings.anyMatch(self, predicate)
    }

    @Nonnull
    static <K, V, R> BooleanBinding anyMatch(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Function<? super V, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.anyMatch(self, mapper, predicate)
    }

    @Nonnull
    static <K, V> BooleanBinding anyMatch(
        @Nonnull ObservableMap<K, V> self, @Nonnull ObservableValue<Predicate<? super V>> predicate) {
        MatchingBindings.anyMatch(self, predicate)
    }

    @Nonnull
    static <K, V, R> BooleanBinding anyMatch(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull ObservableValue<Function<? super V, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.anyMatch(self, mapper, predicate)
    }

    @Nonnull
    static <K, V> BooleanBinding noneMatch(@Nonnull ObservableMap<K, V> self, @Nonnull Predicate<? super V> predicate) {
        MatchingBindings.noneMatch(self, predicate)
    }

    @Nonnull
    static <K, V, R> BooleanBinding noneMatch(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Function<? super V, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.noneMatch(self, mapper, predicate)
    }

    @Nonnull
    static <K, V> BooleanBinding noneMatch(
        @Nonnull ObservableMap<K, V> self, @Nonnull ObservableValue<Predicate<? super V>> predicate) {
        MatchingBindings.noneMatch(self, predicate)
    }

    @Nonnull
    static <K, V, R> BooleanBinding noneMatch(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull ObservableValue<Function<? super V, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.noneMatch(self, mapper, predicate)
    }

    @Nonnull
    static <K, V> BooleanBinding allMatch(@Nonnull ObservableMap<K, V> self, @Nonnull Predicate<? super V> predicate) {
        MatchingBindings.allMatch(self, predicate)
    }

    @Nonnull
    static <K, V, R> BooleanBinding allMatch(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull Function<? super V, R> mapper, @Nonnull Predicate<? super R> predicate) {
        MatchingBindings.allMatch(self, mapper, predicate)
    }

    @Nonnull
    static <K, V> BooleanBinding allMatch(
        @Nonnull ObservableMap<K, V> self, @Nonnull ObservableValue<Predicate<? super V>> predicate) {
        MatchingBindings.allMatch(self, predicate)
    }

    @Nonnull
    static <K, V, R> BooleanBinding allMatch(
        @Nonnull ObservableMap<K, V> self,
        @Nonnull ObservableValue<Function<? super V, R>> mapper,
        @Nonnull ObservableValue<Predicate<? super R>> predicate) {
        MatchingBindings.allMatch(self, mapper, predicate)
    }
}
