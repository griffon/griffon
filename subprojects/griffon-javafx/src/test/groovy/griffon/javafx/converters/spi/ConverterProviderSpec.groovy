/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.javafx.converters.spi

import griffon.javafx.converters.ColorConverter
import griffon.javafx.converters.Dimension2DConverter
import griffon.javafx.converters.GraphicConverter
import griffon.javafx.converters.ImageConverter
import griffon.javafx.converters.InsetsConverter
import griffon.javafx.converters.LinearGradientConverter
import griffon.javafx.converters.PaintConverter
import griffon.javafx.converters.Point2DConverter
import griffon.javafx.converters.RadialGradientConverter
import griffon.javafx.converters.Rectangle2DConverter
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Paint
import javafx.scene.paint.RadialGradient
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.Converter
import javax.application.converter.spi.ConverterProvider

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
        targetType     | converterType
        Color          | ColorConverter
        Dimension2D    | Dimension2DConverter
        Node           | GraphicConverter
        Image          | ImageConverter
        Insets         | InsetsConverter
        LinearGradient | LinearGradientConverter
        Paint          | PaintConverter
        Point2D        | Point2DConverter
        RadialGradient | RadialGradientConverter
        Rectangle2D    | Rectangle2DConverter
    }
}
