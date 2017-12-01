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
import org.apache.pivot.wtk.Rollup

/**
 * @author Andres Almiray
 */
class RollupFactory extends ComponentFactory {
    public static final String DELEGATE_PROPERTY_HEADER = '_delegateProperty:header'
    public static final String DEFAULT_DELEGATE_PROPERTY_HEADER = 'header'
    public static final String CONTEXT_DATA_KEY = 'RollupFactoryData'

    RollupFactory() {
        super(Rollup)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def newChild = super.newInstance(builder, name, value, attributes)
        builder.context.rollupFactoryClosure = { FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes ->
            if (builder.current == newChild) inspectChild(cBuilder, cNode, cAttributes)
        }
        builder.addAttributeDelegate(builder.context.rollupFactoryClosure)
        builder.context[DELEGATE_PROPERTY_HEADER] = attributes.remove('header') ?: DEFAULT_DELEGATE_PROPERTY_HEADER

        return newChild
    }

    static void inspectChild(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (!(node instanceof Component)) return
        def header = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_HEADER) ?: DEFAULT_DELEGATE_PROPERTY_HEADER)
        def rollupContext = builder.context.get(CONTEXT_DATA_KEY) ?: [:]
        if (rollupContext.isEmpty()) {
            builder.context.put(CONTEXT_DATA_KEY, rollupContext)
        }
        rollupContext.put(node, [header])
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child instanceof Component) {
            def settings = builder.context[CONTEXT_DATA_KEY]?.get(child) ?: [null]
            if (settings[0]) parent.header = child
            else parent.content = child
        } else {
            super.setChild(builder, parent, child)
        }
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node)
        builder.removeAttributeDelegate(builder.context.rollupFactoryClosure)
    }
}
