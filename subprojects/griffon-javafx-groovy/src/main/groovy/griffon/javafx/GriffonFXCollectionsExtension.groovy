/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.javafx

import griffon.javafx.collections.GriffonFXCollections
import griffon.javafx.collections.MappingObservableList
import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet

import javax.annotation.Nonnull
import java.util.function.Function

/**
 * @author Andres Almiray
 * @since 2.13.0-SNAPSHOT
 */
@CompileStatic
final class GriffonFXCollectionsExtension {
    @Nonnull
    static <T> ObservableList<T> uiThreadAware(@Nonnull ObservableList<T> self) {
        return GriffonFXCollections.uiThreadAwareObservableList(self)
    }

    @Nonnull
    static <T> ObservableSet<T> uiThreadAware(@Nonnull ObservableSet<T> self) {
        return GriffonFXCollections.uiThreadAwareObservableSet(self)
    }

    @Nonnull
    static <K, V> ObservableMap<K, V> uiThreadAware(@Nonnull ObservableMap<K, V> self) {
        return GriffonFXCollections.uiThreadAwareObservableMap(self)
    }

    @Nonnull
    static <T, S> ObservableList<T> mappedAs(@Nonnull ObservableList<S> self, @Nonnull Function<S, T> mapper) {
        new MappingObservableList<T, S>(self, mapper)
    }

    @Nonnull
    static <T, S> ObservableList<T> mappedAs(
        @Nonnull ObservableList<S> self, @Nonnull ObservableValue<Function<S, T>> mapper) {
        new MappingObservableList<T, S>(self, mapper)
    }
}
