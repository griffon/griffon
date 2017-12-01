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
package griffon.builder.lanterna.factory

import com.googlecode.lanterna.gui.component.Panel

/**
 * @author Andres Almiray
 */
class LayoutFactory extends BeanFactory {
    private Map contextProps
    static final String DELEGATE_PROPERTY_CONSTRAINT = '_delegateProperty:Constraint'
    static final String DEFAULT_DELEGATE_PROPERTY_CONSTRAINT = 'constraints'

    LayoutFactory(Class klass) {
        super(klass, true)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        builder.context[DELEGATE_PROPERTY_CONSTRAINT] = attributes.remove('constraintsProperty') ?: DEFAULT_DELEGATE_PROPERTY_CONSTRAINT
        Object o = super.newInstance(builder, name, value, attributes)
        addLayoutProperties(builder.context)
        return o
    }

    protected void addLayoutProperties(Map context, Class layoutClass) {
        if (contextProps == null) {
            contextProps = [:]
            layoutClass.fields.each {
                String name = it.name
                if (name.toUpperCase() == name) {
                    contextProps[name] = layoutClass."$name"
                }
            }
        }

        context.putAll(contextProps)
    }

    protected void addLayoutProperties(Map context) {
        addLayoutProperties(context, beanClass)
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent instanceof Panel) {
            parent.layoutManager = child
        }
    }

    static constraintsAttributeDelegate(builder, node, attributes) {
        def constraintsAttr = builder?.context?.getAt(DELEGATE_PROPERTY_CONSTRAINT) ?: DEFAULT_DELEGATE_PROPERTY_CONSTRAINT
        if (attributes.containsKey(constraintsAttr)) {
            builder.context.constraints = attributes.remove(constraintsAttr)
        }
    }
}