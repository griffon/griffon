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
package griffon.swing.converters.spi

import griffon.swing.converters.BufferedImageConverter
import griffon.swing.converters.ColorConverter
import griffon.swing.converters.DimensionConverter
import griffon.swing.converters.FontConverter
import griffon.swing.converters.GradientPaintConverter
import griffon.swing.converters.IconConverter
import griffon.swing.converters.ImageConverter
import griffon.swing.converters.InsetsConverter
import griffon.swing.converters.LinearGradientPaintConverter
import griffon.swing.converters.Point2DConverter
import griffon.swing.converters.PointConverter
import griffon.swing.converters.PolygonConverter
import griffon.swing.converters.RadialGradientPaintConverter
import griffon.swing.converters.Rectangle2DConverter
import griffon.swing.converters.RectangleConverter
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.Converter
import javax.application.converter.spi.ConverterProvider
import javax.swing.Icon
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Image
import java.awt.Insets
import java.awt.LinearGradientPaint
import java.awt.Point
import java.awt.Polygon
import java.awt.RadialGradientPaint
import java.awt.Rectangle
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

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
        targetType          | converterType
        BufferedImage       | BufferedImageConverter
        Color               | ColorConverter
        Dimension           | DimensionConverter
        Font                | FontConverter
        GradientPaint       | GradientPaintConverter
        Icon                | IconConverter
        Image               | ImageConverter
        Insets              | InsetsConverter
        LinearGradientPaint | LinearGradientPaintConverter
        Point2D             | Point2DConverter
        Point               | PointConverter
        Polygon             | PolygonConverter
        RadialGradientPaint | RadialGradientPaintConverter
        Rectangle2D         | Rectangle2DConverter
        Rectangle           | RectangleConverter
    }
}
