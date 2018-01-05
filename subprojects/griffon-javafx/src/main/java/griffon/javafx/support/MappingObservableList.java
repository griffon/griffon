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
package griffon.javafx.support;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author Andres Almiray
 * @see griffon.javafx.collections.MappingObservableList
 * @since 2.9.0
 * @deprecated use {@code griffon.javafx.collections.MappingObservableList} instead.
 */
@Deprecated
public class MappingObservableList<T, S> extends griffon.javafx.collections.MappingObservableList<T, S> {
    public MappingObservableList(@Nonnull ObservableList<? extends S> source, @Nonnull Function<S, T> mapper) {
        super(source, mapper);
    }

    public MappingObservableList(@Nonnull ObservableList<? extends S> source, @Nonnull ObservableValue<Function<S, T>> mapper) {
        super(source, mapper);
    }
}