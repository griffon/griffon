/*
 * Copyright 2003-2010 the original author or authors.
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

import org.apache.pivot.wtk.Insets

/**
 * @author Andres Almiray
 */
class InsetsFactory extends AbstractFactory {
    boolean isLeaf() {
        return true
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value instanceof GString) value = value as String
        if (value instanceof String) {
            return new Insets(toInteger(value))
        }

        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, Insets)) {
            return value
        }

        return new Insets(
                toInteger(attributes.remove('top') ?: 0i),
                toInteger(attributes.remove('left') ?: 0i),
                toInteger(attributes.remove('bottom') ?: 0i),
                toInteger(attributes.remove('right') ?: 0i)
        )
    }

    private int toInteger(value) {
        if (value instanceof Number) return value.intValue()
        return Double.valueOf(value.toString()).intValue()
    }
}
