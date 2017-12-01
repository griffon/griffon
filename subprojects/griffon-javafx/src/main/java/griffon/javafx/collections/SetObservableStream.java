/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package griffon.javafx.collections;

import javafx.collections.ObservableSet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
class SetObservableStream<T> extends AbstractObservableStream<T> {
    SetObservableStream(@Nonnull ObservableSet<T> list) {
        this(list, Collections.emptyList());
    }

    private SetObservableStream(@Nonnull ObservableSet<?> list, @Nonnull List<StreamOp> operations) {
        super(list, operations);
    }

    @Nonnull
    @Override
    protected <E> ObservableStream<E> createInstance(@Nonnull List<StreamOp> operations) {
        return new SetObservableStream<>((ObservableSet) observable, operations);
    }

    @Nonnull
    @Override
    protected Stream createStream() {
        return ((ObservableSet) observable).stream();
    }
}
