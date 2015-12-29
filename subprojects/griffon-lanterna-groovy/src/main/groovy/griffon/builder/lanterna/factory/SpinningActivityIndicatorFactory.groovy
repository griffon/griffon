/*
 * Copyright 2008-2016 the original author or authors.
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
package griffon.builder.lanterna.factory

import com.googlecode.lanterna.gui.component.SpinningActivityIndicator

/**
 * @author Andres Almiray
 */
class SpinningActivityIndicatorFactory extends ComponentFactory {
    SpinningActivityIndicatorFactory() {
        super(SpinningActivityIndicator, true)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def chars = attributes.remove('chars') ?: value
        if (chars instanceof CharSequence) {
            switch (chars.toString().toLowerCase()) {
                case 'trigrams':
                    return new SpinningActivityIndicator(SpinningActivityIndicator.TRIGRAMS)
                case 'dice':
                    return new SpinningActivityIndicator(SpinningActivityIndicator.DICE)
                case 'chevrons':
                    return new SpinningActivityIndicator(SpinningActivityIndicator.CHEVRONS)
                case 'bars':
                default:
                    return new SpinningActivityIndicator(SpinningActivityIndicator.BARS)
            }
        }
        if (chars instanceof List) return new SpinningActivityIndicator(chars as char[])
        throw new IllegalArgumentException("In $name you must define a value of type char[]")
    }
}
