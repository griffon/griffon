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
package griffon.javafx.converters.spi;

import griffon.converter.Converter;
import griffon.converter.spi.ConverterProvider;
import griffon.javafx.converters.PaintConverter;
import javafx.scene.paint.Paint;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
@ServiceProviderFor(ConverterProvider.class)
public class PaintConverterProvider implements ConverterProvider<Paint> {
    @Override
    public Class<Paint> getTargetType() {
        return Paint.class;
    }

    @Override
    public Class<? extends Converter<Paint>> getConverterType() {
        return PaintConverter.class;
    }
}
