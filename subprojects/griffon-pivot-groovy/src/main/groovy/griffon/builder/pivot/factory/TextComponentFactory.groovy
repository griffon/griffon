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
package griffon.builder.pivot.factory

/**
 * @author Andres Almiray
 */
class TextComponentFactory extends ComponentFactory {
    TextComponentFactory(Class beanClass) {
        super(beanClass, false)
    }

    TextComponentFactory(Class beanClass, boolean leaf) {
        super(beanClass, leaf)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value instanceof CharSequence) value = value.toString()
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
            return value
        }
        Object bean = beanClass.newInstance()
        if (value instanceof String) bean.text = value

        return bean
    }
}
