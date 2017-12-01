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

import com.googlecode.lanterna.gui.Component
import com.googlecode.lanterna.gui.Window
import com.googlecode.lanterna.gui.component.Table

/**
 * @author Andres Almiray
 */
class TableFactory extends ComponentFactory {
    TableFactory() {
        super(Table, false)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def title = attributes.remove('title')
        if (title == null && value instanceof CharSequence) title = value
        int cols = ((attributes.remove('columns') ?: attributes.remove('cols')) ?: 1) as int

        builder.context.cols = cols
        builder.context.row = []

        new Table(cols, title)
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (!(child instanceof Component) || (child instanceof Window)) {
            return
        }

        builder.parentContext.row << child

        if (builder.parentContext.row.size() == builder.parentContext.cols) {
            def components = []
            components.addAll(builder.parentContext.row)
            builder.parentContext.row.clear()
            parent.addRow(* components)
        }
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        if (builder.context.row) {
            def components = []
            components.addAll(builder.context.row)
            builder.context.row.clear()
            node.addRow(* components)
        }
    }
}
