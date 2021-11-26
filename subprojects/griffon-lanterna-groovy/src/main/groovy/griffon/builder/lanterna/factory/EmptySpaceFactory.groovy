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

import com.googlecode.lanterna.gui.component.EmptySpace

/**
 * @author Andres Almiray
 */
class EmptySpaceFactory extends ComponentFactory {
    EmptySpaceFactory() {
        super(EmptySpace, true)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def width = attributes.remove('width') ?: 1
        def height = attributes.remove('height') ?: 1
        new EmptySpace(width as int, height as int)
    }
}
