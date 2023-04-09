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
package griffon.javafx.collections;

import griffon.annotations.core.Nonnull;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
class ListObservableStream<T> extends AbstractObservableStream<T> {
    ListObservableStream(@Nonnull ObservableList<T> list) {
        this(list, Collections.emptyList());
    }

    private ListObservableStream(@Nonnull ObservableList<?> list, @Nonnull List<StreamOp> operations) {
        super(list, operations);
    }

    @Nonnull
    @Override
    protected <E> ObservableStream<E> createInstance(@Nonnull List<StreamOp> operations) {
        return new ListObservableStream<>((ObservableList) observable, operations);
    }

    @Nonnull
    @Override
    protected Stream createStream() {
        return ((ObservableList) observable).stream();
    }
}
