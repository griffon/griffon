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

import org.apache.pivot.wtk.Component
import org.apache.pivot.wtk.TabPane

/**
 * @author Andres Almiray
 */
class TabPaneFactory extends ViewportFactory {
    public static final String DELEGATE_PROPERTY_ICON = '_delegateProperty:icon'
    public static final String DEFAULT_DELEGATE_PROPERTY_ICON = 'icon'
    public static final String DELEGATE_PROPERTY_LABEL = '_delegateProperty:label'
    public static final String DEFAULT_DELEGATE_PROPERTY_LABEL = 'label'
    public static final String DELEGATE_PROPERTY_CLOSEABLE = '_delegateProperty:closeable'
    public static final String DEFAULT_DELEGATE_PROPERTY_CLOSEABLE = 'closeable'
    public static final String DELEGATE_PROPERTY_CORNER = '_delegateProperty:corner'
    public static final String DEFAULT_DELEGATE_PROPERTY_CORNER = 'corner'
    public static final String CONTEXT_DATA_KEY = 'TabPaneFactoryData'

    TabPaneFactory() {
        super(TabPane)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def newChild = super.newInstance(builder, name, value, attributes)
        builder.context.tabPaneFactoryClosure = { FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes ->
            if (builder.current == newChild) inspectChild(cBuilder, cNode, cAttributes)
        }
        builder.addAttributeDelegate(builder.context.tabPaneFactoryClosure)
        builder.context[DELEGATE_PROPERTY_ICON] = attributes.remove('icon') ?: DEFAULT_DELEGATE_PROPERTY_ICON
        builder.context[DELEGATE_PROPERTY_LABEL] = attributes.remove('label') ?: DEFAULT_DELEGATE_PROPERTY_LABEL
        builder.context[DELEGATE_PROPERTY_CLOSEABLE] = attributes.remove('closeable') ?: DEFAULT_DELEGATE_PROPERTY_CLOSEABLE
        builder.context[DELEGATE_PROPERTY_CORNER] = attributes.remove('corner') ?: DEFAULT_DELEGATE_PROPERTY_CORNER

        return newChild
    }

    static void inspectChild(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (!(node instanceof Component)) return
        def icon = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_ICON) ?: DEFAULT_DELEGATE_PROPERTY_ICON)
        def label = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_LABEL) ?: DEFAULT_DELEGATE_PROPERTY_LABEL)
        def closeable = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_CLOSEABLE) ?: DEFAULT_DELEGATE_PROPERTY_CLOSEABLE)
        def isCorner = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_CORNER) ?: DEFAULT_DELEGATE_PROPERTY_CORNER)
        def tabPaneContext = builder.context.get(CONTEXT_DATA_KEY) ?: [:]
        if (tabPaneContext.isEmpty()) {
            builder.context.put(CONTEXT_DATA_KEY, tabPaneContext)
        }
        tabPaneContext.put(node, [icon, label, closeable, isCorner])
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof Component) {
            def settings = builder.context[CONTEXT_DATA_KEY]?.get(child) ?: [null, null, null, false]
            if (settings[3]) {
                parent.corner = child
            } else {
                parent.tabs.add(child)
                if (settings[0]) TabPane.setIcon(child, settings[0])
                if (settings[1]) TabPane.setLabel(child, settings[1])
                if (settings[2] != null) TabPane.setCloseable(child, settings[2])
            }
        } else {
            super.setChild(builder, parent, child)
        }
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node)
        builder.removeAttributeDelegate(builder.context.tabPaneFactoryClosure)
    }
}
