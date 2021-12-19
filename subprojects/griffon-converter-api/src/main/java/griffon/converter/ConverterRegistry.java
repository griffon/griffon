/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2021 the original author or authors.
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
package griffon.converter;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

/**
 * The ConverterRegistry can be used to locate a converter for
 * any given type name. Converters must support the
 * {@code javax.griffon.converter.Converter} interface for converting a given value.
 * <p>
 *
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface ConverterRegistry {
    /**
     * Registers a converter class used to convert values of the given target class.
     *
     * @param targetType     the class object of the type to be converted. Must not be {@code null}
     * @param converterClass the class object of the converter class. Must not be {@code null}
     */
    <T> void registerConverter(@Nonnull Class<T> targetType, @Nonnull Class<? extends Converter<T>> converterClass);

    /**
     * Unregisters a converter class used to convert values of the given target class.
     * If the given {@code converterClass} is {@code null} then all registrations matching {@code targetType}
     * are removed.
     *
     * @param targetType     the class object of the type to be converted. Must not be {@code null}
     * @param converterClass the class object of the converter class. May be {@code null}
     */
    <T> void unregisterConverter(@Nonnull Class<T> targetType, @Nonnull Class<? extends Converter<T>> converterClass);

    /**
     * Locates a value converter for a given target type.
     *
     * @param targetType The Class object for the type to be converter. Must not be {@code null}
     *
     * @return A converter object for the given target class.
     * The result is {@code null} if no suitable converter can be found.
     */
    @Nullable
    <T> Converter<T> findConverter(@Nonnull Class<T> targetType);

    /**
     * Removes all currently registered converters.
     */
    void clear();
}
