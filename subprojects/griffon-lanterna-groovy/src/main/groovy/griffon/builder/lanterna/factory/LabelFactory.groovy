/*
 * Copyright 2008-2015 the original author or authors.
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

import com.googlecode.lanterna.gui.component.Label
import com.googlecode.lanterna.terminal.Terminal

/**
 * @author Andres Almiray
 */
class LabelFactory extends ComponentFactory {
    LabelFactory() {
        super(Label, true)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def text = attributes.remove('text')
        if (text == null && value instanceof CharSequence) text = value
        if (text != null) {
            text = text.toString()
        } else {
            return new Label()
        }

        def fixedWidth = attributes.remove('fixedWidth')
        def textBold = attributes.remove('textBold') ?: false
        Terminal.Color color = attributes.remove('color') ?: Terminal.Color.BLACK

        if (fixedWidth != null) {
            return new Label(text, fixedWidth as int, color, textBold as boolean)
        } else {
            return new Label(text, color, textBold as boolean)
        }
    }
}
