/*
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
import org.apache.pivot.wtk.TablePane

/**
 * @author Andres Almiray
 */
class TablePaneFactory extends ComponentFactory {
    static final String DELEGATE_PROPERTY_COLUMN_SPAN = '_delegateProperty:columnSpan'
    static final String DEFAULT_DELEGATE_PROPERTY_COLUMN_SPAN = 'columnSpan'
    static final String DELEGATE_PROPERTY_ROW_SPAN = '_delegateProperty:rowSpan'
    static final String DEFAULT_DELEGATE_PROPERTY_ROW_SPAN = 'rowSpan'
    static final String CONTEXT_DATA_KEY = 'TablePaneFactoryData'

    TablePaneFactory() {
        super(TablePane)
    }

/*
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def newChild = super.newInstance(builder, name, value, attributes)
        builder.context.tablePaneFactoryClosure = { FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes ->
            if (builder.current == newChild) inspectChild(cBuilder, cNode, cAttributes)
        }
        builder.addAttributeDelegate(builder.context.tablePaneFactoryClosure)
        builder.context[DELEGATE_PROPERTY_COLUMN_SPAN] = attributes.remove('columnSpan') ?: DEFAULT_DELEGATE_PROPERTY_COLUMN_SPAN
        builder.context[DELEGATE_PROPERTY_ROW_SPAN] = attributes.remove('rowSpan') ?: DEFAULT_DELEGATE_PROPERTY_ROW_SPAN

        return newChild
    }


    static void inspectChild(FactoryBuilderSupport builder, Object node, Map attributes) {
        if(!(node instanceof Component)) return
        def columnSpan = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_COLUMN_SPAN) ?: DEFAULT_DELEGATE_PROPERTY_COLUMN_SPAN)
        def rowSpan = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_ROW_SPAN) ?: DEFAULT_DELEGATE_PROPERTY_ROW_SPAN)
        def tablePaneContext = builder.context.get(CONTEXT_DATA_KEY) ?: [:]
        if (tablePaneContext.isEmpty()) {
            builder.context.put(CONTEXT_DATA_KEY, tablePaneContext)
        }
        tablePaneContext.put(node, [columnSpan, rowSpan])
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if(child instanceof Component) {
            def settings = builder.context[CONTEXT_DATA_KEY]?.get(child) ?: [null, null]
            parent.panels.add(child)
            if(settings[0] != null) TablePane.setColumnSpan(child, settings[0])
            if(settings[1] != null) TablePane.setRowSpan(child, settings[1])
        } else {
            super.setChild(builder, parent, child)
        }
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node)
        builder.removeAttributeDelegate(builder.context.tablePaneFactoryClosure)
    }
*/

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof TablePane.Column) parent.columns.add(child)
        if (child instanceof TablePane.Row) parent.rows.add(child)
    }
}

/**
 * @author Andres Almiray
 */
class TablePaneColumnFactory extends ComponentFactory {
    TablePaneColumnFactory() {
        super(TablePane.Column)
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof Component) parent.add(child)
    }
}

/**
 * @author Andres Almiray
 */
class TablePaneRowFactory extends ComponentFactory {
    TablePaneRowFactory() {
        super(TablePane.Row)
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof Component) parent.add(child)
    }
}
