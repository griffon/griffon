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
package griffon.builder.pivot.factory

import org.apache.pivot.wtk.Accordion
import org.apache.pivot.wtk.Component

/**
 * @author Andres Almiray
 */
class AccordionFactory extends ViewportFactory {
    public static final String DELEGATE_PROPERTY_ICON = '_delegateProperty:icon'
    public static final String DEFAULT_DELEGATE_PROPERTY_ICON = 'icon'
    public static final String DELEGATE_PROPERTY_LABEL = '_delegateProperty:label'
    public static final String DEFAULT_DELEGATE_PROPERTY_LABEL = 'label'
    public static final String CONTEXT_DATA_KEY = 'AccordionFactoryData'

    AccordionFactory() {
        super(Accordion)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def newChild = super.newInstance(builder, name, value, attributes)
        builder.context.accordionFactoryClosure = { FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes ->
            if (builder.current == newChild) inspectChild(cBuilder, cNode, cAttributes)
        }
        builder.addAttributeDelegate(builder.context.accordionFactoryClosure)
        builder.context[DELEGATE_PROPERTY_ICON] = attributes.remove('icon') ?: DEFAULT_DELEGATE_PROPERTY_ICON
        builder.context[DELEGATE_PROPERTY_LABEL] = attributes.remove('label') ?: DEFAULT_DELEGATE_PROPERTY_LABEL

        return newChild
    }

    static void inspectChild(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (!(node instanceof Component)) return
        def icon = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_ICON) ?: DEFAULT_DELEGATE_PROPERTY_ICON)
        def label = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_LABEL) ?: DEFAULT_DELEGATE_PROPERTY_LABEL)
        def accordionContext = builder.context.get(CONTEXT_DATA_KEY) ?: [:]
        if (accordionContext.isEmpty()) {
            builder.context.put(CONTEXT_DATA_KEY, accordionContext)
        }
        accordionContext.put(node, [icon, label])
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof Component) {
            def settings = builder.context[CONTEXT_DATA_KEY]?.get(child) ?: [null, null]
            parent.panels.add(child)
            if (settings[0]) Accordion.setIcon(child, settings[0])
            if (settings[1]) Accordion.setLabel(child, settings[1])
        } else {
            super.setChild(builder, parent, child)
        }
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node)
        builder.removeAttributeDelegate(builder.context.accordionFactoryClosure)
    }
}
