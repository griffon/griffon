/*
 * Copyright 2008-2014 the original author or authors.
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

import org.apache.pivot.wtk.Bounds
import org.apache.pivot.wtk.Dimensions
import org.apache.pivot.wtk.Point

/**
 * @author Andres Almiray
 */
class BoundsFactory extends AbstractFactory {
    boolean isLeaf() {
        return true
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, Bounds)) {
            return value
        }
        if (value != null && value instanceof Bounds) {
            return value
        } else if( value != null) {
            throw new IllegalArgumentException("In $name value must be of type ${Bounds.class.name}")
        }

        if (attributes.containsKey('origin') || attributes.containsKey('size')) {
            Point origin = attributes.remove('origin') ?: new Point(0i, 0i)
            Dimensions size = attributes.remove('size') ?: new Dimensions(0i, 0i)
            return new Bounds(origin, size)
        }

        return new Bounds(
                toInteger(attributes.remove('x') ?: 0i),
                toInteger(attributes.remove('y') ?: 0i),
                toInteger(attributes.remove('width') ?: 0i),
                toInteger(attributes.remove('height') ?: 0i)
        )
    }

    private int toInteger(value) {
        if (value instanceof Number) return value.intValue()
        return Double.valueOf(value.toString()).intValue()
    }
}
