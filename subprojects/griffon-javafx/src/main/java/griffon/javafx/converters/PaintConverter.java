/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package griffon.javafx.converters;

import javafx.scene.paint.Paint;
import org.kordamp.jsr377.converter.AbstractConverter;

import javax.application.converter.ConversionException;
import javax.application.converter.Converter;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PaintConverter extends AbstractConverter<Paint> {
    private static final Converter[] CONVERTERS = new Converter[]{
        new ColorConverter(),
        new RadialGradientConverter(),
        new LinearGradientConverter()
    };

    @Override
    public Paint fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleInternal(String.valueOf(value).trim());
        } else if (value instanceof List || value instanceof Map) {
            return handleInternal(value);
        } else if (value instanceof Paint) {
            return (Paint) value;
        } else {
            throw illegalValue(value, Paint.class);
        }
    }

    private Paint handleInternal(Object value) {
        for (Converter converter : CONVERTERS) {
            try {
                return (Paint) converter.fromObject(value);
            } catch (Exception e) {
                // ignore
            }
        }
        throw illegalValue(value, Paint.class);
    }
}