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
import org.apache.pivot.wtk.ScrollPane

/**
 * @author Andres Almiray
 */
class ScrollPaneFactory extends ViewportFactory {
    public static final String DELEGATE_PROPERTY_COLUMN_HEADER = '_delegateProperty:columnHeader'
    public static final String DEFAULT_DELEGATE_PROPERTY_COLUMN_HEADER = 'columnHeader'
    public static final String DELEGATE_PROPERTY_ROW_HEADER = '_delegateProperty:rowHeader'
    public static final String DEFAULT_DELEGATE_PROPERTY_ROW_HEADER = 'rowHeader'
    public static final String DELEGATE_PROPERTY_CORNER = '_delegateProperty:corner'
    public static final String DEFAULT_DELEGATE_PROPERTY_CORNER = 'corner'
    public static final String CONTEXT_DATA_KEY = 'ScrollPaneFactoryData'

    ScrollPaneFactory() {
        super(ScrollPane)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def newChild = super.newInstance(builder, name, value, attributes)
        builder.context.scrollPaneFactoryClosure = { FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes ->
            if (builder.current == newChild) inspectChild(cBuilder, cNode, cAttributes)
        }
        builder.addAttributeDelegate(builder.context.scrollPaneFactoryClosure)
        builder.context[DELEGATE_PROPERTY_COLUMN_HEADER] = attributes.remove('columnHeader') ?: DEFAULT_DELEGATE_PROPERTY_COLUMN_HEADER
        builder.context[DELEGATE_PROPERTY_ROW_HEADER] = attributes.remove('rowHeader') ?: DEFAULT_DELEGATE_PROPERTY_ROW_HEADER
        builder.context[DELEGATE_PROPERTY_CORNER] = attributes.remove('corner') ?: DEFAULT_DELEGATE_PROPERTY_CORNER

        return newChild
    }

    static void inspectChild(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (!(node instanceof Component)) return
        def isColumnHeader = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_COLUMN_HEADER) ?: DEFAULT_DELEGATE_PROPERTY_COLUMN_HEADER)
        def isRowHeader = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_ROW_HEADER) ?: DEFAULT_DELEGATE_PROPERTY_ROW_HEADER)
        def isCorner = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_CORNER) ?: DEFAULT_DELEGATE_PROPERTY_CORNER)
        def scrollPaneContext = builder.context.get(CONTEXT_DATA_KEY) ?: [:]
        if (scrollPaneContext.isEmpty()) {
            builder.context.put(CONTEXT_DATA_KEY, scrollPaneContext)
        }
        scrollPaneContext.put(node, [isColumnHeader, isRowHeader, isCorner])
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof Component) {
            def settings = builder.context[CONTEXT_DATA_KEY]?.get(child) ?: [false, false, false]
            if (settings[0]) parent.columnHeader = child
            else if (settings[1]) parent.rowHeader = child
            else if (settings[2]) parent.corner = child
            else parent.view = child
        } else {
            super.setChild(builder, parent, child)
        }
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node)
        builder.removeAttributeDelegate(builder.context.scrollPaneFactoryClosure)
    }
}
