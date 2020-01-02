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
package griffon.builder.lanterna3.factory

import com.googlecode.lanterna.gui2.Component
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.Window

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
class ComponentFactory extends BeanFactory {
    ComponentFactory(Class beanClass) {
        super(beanClass)
    }

    ComponentFactory(Class beanClass, boolean leaf) {
        super(beanClass, leaf)
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (!(child instanceof Component) || (child instanceof Window)) {
            return
        }

        if (parent instanceof Window) {
            parent.component = child
        } else if(parent instanceof Panel) {
            child.addTo(parent)
        }
        /*
        try {
            def constraints = builder.context.constraints
            if (constraints != null) {
                parent.addComponent(child, constraints)
                builder.context.remove('constraints')
            } else {
                parent.addComponent(child)
            }
        } catch (MissingPropertyException mpe) {
            parent.addComponent(child)
        }
        */
    }
}