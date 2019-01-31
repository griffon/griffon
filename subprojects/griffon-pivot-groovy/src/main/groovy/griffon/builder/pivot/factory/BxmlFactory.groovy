/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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

import org.apache.pivot.beans.BXMLSerializer

import java.lang.reflect.Field

import static griffon.builder.pivot.PivotUtils.setBeanProperty

/**
 * @author Andres Almiray
 */
class BxmlFactory extends AbstractFactory {
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value instanceof GString) value = value.toString()
        if (!value) {
            if (attributes.containsKey('src')) {
                value = attributes.remove('src').toString()
            } else {
                throw new IllegalArgumentException("In $name you must define a value for src: or a node value that points to a bxml resource.")
            }
        }
        if (!value.startsWith('/')) value = '/' + value

        def bxml = new BXMLSerializer()
        def root = bxml.readObject(builder.app, value)
        def rootId = attributes['id'] ? attributes['id'] + '_' : ''

        Field namedObjectsField = BXMLSerializer.class.getDeclaredField('namedObjects')
        namedObjectsField.setAccessible(true)
        def pivotMap = namedObjectsField.get(bxml)
        pivotMap.each { id ->
            builder.setVariable(rootId + id, pivotMap.get(id))
        }

        return root
    }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        attributes.each { property, value ->
            setBeanProperty(property, value, node)
        }

        return false
    }
}
