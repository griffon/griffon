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
package griffon.javafx.collections.match

import griffon.javafx.beans.binding.MatchingBindings
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

fun <T> ObservableList<T>.anyMatch(predicate: Predicate<in T>): BooleanBinding {
    return MatchingBindings.anyMatch(this, predicate)
}

fun <T, R> ObservableList<T>.anyMatch(mapper: Function<in T, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.anyMatch(this, mapper, predicate)
}

fun <T> ObservableList<T>.anyMatch(predicate: ObservableValue<Predicate<in T>>): BooleanBinding {
    return MatchingBindings.anyMatch(this, predicate)
}

fun <T, R> ObservableList<T>.anyMatch(mapper: ObservableValue<Function<in T, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.anyMatch(this, mapper, predicate)
}

fun <T> ObservableList<T>.noneMatch(predicate: Predicate<in T>): BooleanBinding {
    return MatchingBindings.noneMatch(this, predicate)
}

fun <T, R> ObservableList<T>.noneMatch(mapper: Function<in T, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.noneMatch(this, mapper, predicate)
}

fun <T> ObservableList<T>.noneMatch(predicate: ObservableValue<Predicate<in T>>): BooleanBinding {
    return MatchingBindings.noneMatch(this, predicate)
}

fun <T, R> ObservableList<T>.noneMatch(mapper: ObservableValue<Function<in T, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.noneMatch(this, mapper, predicate)
}

fun <T> ObservableList<T>.allMatch(predicate: Predicate<in T>): BooleanBinding {
    return MatchingBindings.allMatch(this, predicate)
}

fun <T, R> ObservableList<T>.allMatch(mapper: Function<in T, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.allMatch(this, mapper, predicate)
}

fun <T> ObservableList<T>.allMatch(predicate: ObservableValue<Predicate<in T>>): BooleanBinding {
    return MatchingBindings.allMatch(this, predicate)
}

fun <T, R> ObservableList<T>.allMatch(mapper: ObservableValue<Function<in T, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.allMatch(this, mapper, predicate)
}

fun <T> ObservableSet<T>.anyMatch(predicate: Predicate<in T>): BooleanBinding {
    return MatchingBindings.anyMatch(this, predicate)
}

fun <T, R> ObservableSet<T>.anyMatch(mapper: Function<in T, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.anyMatch(this, mapper, predicate)
}

fun <T> ObservableSet<T>.anyMatch(predicate: ObservableValue<Predicate<in T>>): BooleanBinding {
    return MatchingBindings.anyMatch(this, predicate)
}

fun <T, R> ObservableSet<T>.anyMatch(mapper: ObservableValue<Function<in T, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.anyMatch(this, mapper, predicate)
}

fun <T> ObservableSet<T>.noneMatch(predicate: Predicate<in T>): BooleanBinding {
    return MatchingBindings.noneMatch(this, predicate)
}

fun <T, R> ObservableSet<T>.noneMatch(mapper: Function<in T, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.noneMatch(this, mapper, predicate)
}

fun <T> ObservableSet<T>.noneMatch(predicate: ObservableValue<Predicate<in T>>): BooleanBinding {
    return MatchingBindings.noneMatch(this, predicate)
}

fun <T, R> ObservableSet<T>.noneMatch(mapper: ObservableValue<Function<in T, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.noneMatch(this, mapper, predicate)
}

fun <T> ObservableSet<T>.allMatch(predicate: Predicate<in T>): BooleanBinding {
    return MatchingBindings.allMatch(this, predicate)
}

fun <T, R> ObservableSet<T>.allMatch(mapper: Function<in T, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.allMatch(this, mapper, predicate)
}

fun <T> ObservableSet<T>.allMatch(predicate: ObservableValue<Predicate<in T>>): BooleanBinding {
    return MatchingBindings.allMatch(this, predicate)
}

fun <T, R> ObservableSet<T>.allMatch(mapper: ObservableValue<Function<in T, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.allMatch(this, mapper, predicate)
}

fun <K, V> ObservableMap<K, V>.anyMatch(predicate: Predicate<in V>): BooleanBinding {
    return MatchingBindings.anyMatch(this, predicate)
}

fun <K, V, R> ObservableMap<K, V>.anyMatch(mapper: Function<in V, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.anyMatch(this, mapper, predicate)
}

fun <K, V> ObservableMap<K, V>.anyMatch(predicate: ObservableValue<Predicate<in V>>): BooleanBinding {
    return MatchingBindings.anyMatch(this, predicate)
}

fun <K, V, R> ObservableMap<K, V>.anyMatch(mapper: ObservableValue<Function<in V, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.anyMatch(this, mapper, predicate)
}

fun <K, V> ObservableMap<K, V>.noneMatch(predicate: Predicate<in V>): BooleanBinding {
    return MatchingBindings.noneMatch(this, predicate)
}

fun <K, V, R> ObservableMap<K, V>.noneMatch(mapper: Function<in V, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.noneMatch(this, mapper, predicate)
}

fun <K, V> ObservableMap<K, V>.noneMatch(predicate: ObservableValue<Predicate<in V>>): BooleanBinding {
    return MatchingBindings.noneMatch(this, predicate)
}

fun <K, V, R> ObservableMap<K, V>.noneMatch(mapper: ObservableValue<Function<in V, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.noneMatch(this, mapper, predicate)
}

fun <K, V> ObservableMap<K, V>.allMatch(predicate: Predicate<in V>): BooleanBinding {
    return MatchingBindings.allMatch(this, predicate)
}

fun <K, V, R> ObservableMap<K, V>.allMatch(mapper: Function<in V, R>, predicate: Predicate<in R>): BooleanBinding {
    return MatchingBindings.allMatch(this, mapper, predicate)
}

fun <K, V> ObservableMap<K, V>.allMatch(predicate: ObservableValue<Predicate<in V>>): BooleanBinding {
    return MatchingBindings.allMatch(this, predicate)
}

fun <K, V, R> ObservableMap<K, V>.allMatch(mapper: ObservableValue<Function<in V, R>>, predicate: ObservableValue<Predicate<in R>>): BooleanBinding {
    return MatchingBindings.allMatch(this, mapper, predicate)
}
