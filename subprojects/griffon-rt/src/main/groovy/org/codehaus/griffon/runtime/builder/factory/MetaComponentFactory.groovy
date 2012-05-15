/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.builder.factory

import griffon.core.MVCGroup
import griffon.util.ApplicationHolder

/**
 * Enables MVC groups to be used as component nodes
 *
 * @author Andres Almiray
 */
class MetaComponentFactory extends AbstractFactory {
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        String mvcType = ''
        if (value != null && value instanceof CharSequence) {
            mvcType = value.toString()
        } else {
            throw new IllegalArgumentException("In $name value must be an MVC group type")
        }

        String mvcName = attributes.remove('mvcName')
        mvcName = attributes.remove('mvcId')

        MVCGroup mvcGroup = ApplicationHolder.application.buildMVCGroup(mvcType, mvcName, attributes)
        def root = mvcGroup.getScriptResult('view')
        builder.context.root = root
        builder.context.mvcGroup = mvcGroup
        root
    }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        false
    }

    boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        def root = builder.context.root
        builder = builder.context.mvcGroup.builder
        Closure handleChildContent = builder.getVariables().get('handleChildContent')
        if (handleChildContent != null) {
            handleChildContent(childContent)
        } else {
            builder.container(root, childContent)
        }
        false
    }

    boolean isHandlesNodeChildren() {
        false
    }
}
