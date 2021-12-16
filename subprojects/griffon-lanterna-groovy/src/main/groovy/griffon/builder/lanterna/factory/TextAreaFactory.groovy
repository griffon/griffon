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
package griffon.builder.lanterna.factory

import com.googlecode.lanterna.gui.component.TextArea
import com.googlecode.lanterna.terminal.TerminalSize

/**
 * @author Andres Almiray
 */
class TextAreaFactory extends ComponentFactory {
    TextAreaFactory() {
        super(TextArea, true)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def text = attributes.remove('text')
        if (text == null && value instanceof CharSequence) text = value
        if (text == null) text = ''
        def size = attributes.remove('size')
        def cols = attributes.remove('columns') ?: attributes.remove('cols')
        def rows = attributes.remove('rows')
        if (cols != null && rows != null) size = new TerminalSize(cols as int, rows as int)
        size != null ? new TextArea(size, text) : new TextArea(size)
    }
}
