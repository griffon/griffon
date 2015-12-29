/*
 * Copyright 2008-2016 the original author or authors.
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

import org.apache.pivot.wtk.Action

/**
 * @author Andres Almiray
 */
class ButtonFactory extends ComponentFactory {
    ButtonFactory(Class beanClass) {
        super(beanClass, false)
    }

    ButtonFactory(Class beanClass, boolean leaf) {
        super(beanClass, leaf)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if(value instanceof Action) {
            Object bean = beanClass.newInstance()
            bean.action = value
            bean.buttonData = value.name
            return bean
        }

        if (value instanceof CharSequence) value = value.toString()
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
            return value
        }
        Object bean = beanClass.newInstance()
        if (value instanceof String) bean.buttonData = value

        if (attributes.containsKey('actionId')) {
            bean.setAction(attributes.remove('actionId').toString())
        }

        return bean
    }
}
