/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.builder.core.factory

/**
 * This returns a mutable java.util.Collection of some sort, to which items are added.
 */
class CollectionFactory extends AbstractFactory {
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        FactoryBuilderSupport.checkValueIsNull(value, name)
        if (attributes.isEmpty()) {
            return new ArrayList()
        } else {
            def item = attributes.entrySet().iterator().next()
            throw new MissingPropertyException(
                "The builder element '$name' is a collections element and accepts no attributes",
                item.key as String, item.value as Class)
        }
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        parent.add(child)
    }
}
