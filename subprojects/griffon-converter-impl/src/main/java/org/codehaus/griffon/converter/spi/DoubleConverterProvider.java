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
package org.codehaus.griffon.converter.spi;

import griffon.annotations.core.Nonnull;
import griffon.converter.Converter;
import griffon.converter.spi.ConverterProvider;
import org.codehaus.griffon.converter.DoubleConverter;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
@ServiceProviderFor(ConverterProvider.class)
public class DoubleConverterProvider implements ConverterProvider<Double> {
    @Nonnull
    @Override
    public Class<Double> getTargetType() {
        return Double.class;
    }

    @Nonnull
    @Override
    public Class<? extends Converter<Double>> getConverterType() {
        return DoubleConverter.class;
    }
}