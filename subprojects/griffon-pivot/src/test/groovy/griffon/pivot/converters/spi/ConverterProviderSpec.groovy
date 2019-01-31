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
package griffon.pivot.converters.spi

import griffon.pivot.converters.BoundsConverter
import griffon.pivot.converters.ColorConverter
import griffon.pivot.converters.DimensionsConverter
import griffon.pivot.converters.InsetsConverter
import griffon.pivot.converters.PointConverter
import org.apache.pivot.wtk.Bounds
import org.apache.pivot.wtk.Dimensions
import org.apache.pivot.wtk.Insets
import org.apache.pivot.wtk.Point
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.Converter
import javax.application.converter.spi.ConverterProvider
import java.awt.Color

@Unroll
class ConverterProviderSpec extends Specification {
    public <T> void "Provider for #targetType is related to #converterType"(Class<T> targetType, Class<? extends Converter<T>> converterType) {
        given:
        ServiceLoader<ConverterProvider> providers = ServiceLoader.load(ConverterProvider)

        when:
        ConverterProvider converterProvider = providers.iterator().find { it.targetType == targetType }

        then:
        converterProvider.converterType == converterType

        where:
        targetType | converterType
        Bounds     | BoundsConverter
        Color      | ColorConverter
        Dimensions | DimensionsConverter
        Insets     | InsetsConverter
        Point      | PointConverter
    }
}
