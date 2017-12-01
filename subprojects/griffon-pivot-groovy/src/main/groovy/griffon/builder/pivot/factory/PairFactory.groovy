/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
 * @author Andres Almiray
 */
class PairFactory extends AbstractFactory {
    final Class beanClass
    final String prop1
    final String prop2

    private static final Class[] PARAMS = [Integer.TYPE, Integer.TYPE] as Class[]

    PairFactory(Class beanClass, String prop1, String prop2) {
        this.beanClass = beanClass
        this.prop1 = prop1
        this.prop2 = prop2
    }

    boolean isLeaf() {
        return true
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value instanceof GString) value = value as String
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
            return value
        }

        int value1 = (attributes.remove(prop1) ?: 0) as int
        int value2 = (attributes.remove(prop2) ?: 0) as int

        beanClass.getDeclaredConstructor(PARAMS).newInstance([value1, value2] as Object[])
    }
}
