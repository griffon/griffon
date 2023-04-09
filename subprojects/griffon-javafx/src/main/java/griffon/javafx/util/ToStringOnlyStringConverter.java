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
package griffon.javafx.util;

import griffon.annotations.core.Nonnull;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class ToStringOnlyStringConverter<T> extends StringConverterAdapter<T> {
    private final Function<T, String> converter;

    public ToStringOnlyStringConverter() {
        this(t -> t == null ? "" : String.valueOf(t));
    }

    public ToStringOnlyStringConverter(@Nonnull Function<T, String> converter) {
        this.converter = requireNonNull(converter, "Argument 'converter' must not be null");
    }

    @Override
    public String toString(T object) {
        return converter.apply(object);
    }
}
