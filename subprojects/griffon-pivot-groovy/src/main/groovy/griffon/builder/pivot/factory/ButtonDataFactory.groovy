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

import org.apache.pivot.wtk.Button
import org.apache.pivot.wtk.content.ButtonData
import org.apache.pivot.wtk.media.Image

/**
 * @author Andres Almiray
 */
class ButtonDataFactory extends BeanFactory {
    ButtonDataFactory() {
        super(ButtonData, true)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Object bean = super.newInstance(builder, name, value, attributes)
        def icon = attributes.remove('icon')
        switch (icon?.class) {
            case URL: bean.setIcon((URL) icon); break
            case Image: bean.setIcon((Image) icon); break
            case String:
            case Gtring:
                bean.setIcon((String) icon.toString())
        }
        return bean
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        if (parent instanceof Button) parent.buttonData = node
    }
}
