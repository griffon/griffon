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
package griffon.javafx.collections

import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import java.util.function.Function

/**
 * @author Andres Almiray
 * @since 2.13.0
 */

fun <T> ObservableList<T>.uiThreadAware(): ObservableList<T> {
    return GriffonFXCollections.uiThreadAwareObservableList(this)
}

fun <T> ObservableSet<T>.uiThreadAware(): ObservableSet<T> {
    return GriffonFXCollections.uiThreadAwareObservableSet(this)
}

fun <K, V> ObservableMap<K, V>.uiThreadAware(): ObservableMap<K, V> {
    return GriffonFXCollections.uiThreadAwareObservableMap(this)
}

fun <T, S> ObservableList<S>.mappedWith(mapper: Function<S, T>): ObservableList<T> {
    return MappingObservableList<T, S>(this, mapper)
}

fun <T, S> ObservableList<S>.mappedWith(mapper: ObservableValue<Function<S, T>>): ObservableList<T> {
    return MappingObservableList<T, S>(this, mapper)
}