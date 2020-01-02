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
package griffon.builder.pivot.factory

/**
 * Copied from SwingBuilder to avoid dependency on Swing module.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class WidgetFactory extends ComponentFactory {
    final Class restrictedType
    protected final boolean leaf

    WidgetFactory(Class restrictedType, boolean leaf) {
        super(restrictedType)
        this.restrictedType = restrictedType
        this.leaf = leaf
    }

    boolean isLeaf() {
        return leaf
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value == null) {
            value = attributes.remove(name)
        }
        if ((value != null) && FactoryBuilderSupport.checkValueIsType(value, name, restrictedType)) {
            return value
        } else {
            throw new RuntimeException("$name must have either a value argument or an attribute named $name that must be of type $restrictedType.name")
        }
    }
}
