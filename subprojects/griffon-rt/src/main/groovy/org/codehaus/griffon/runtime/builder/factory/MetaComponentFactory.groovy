/*
 * Copyright 2009-2013 the original author or authors.
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

import griffon.core.GriffonApplication
import griffon.core.MVCGroup
import griffon.util.ApplicationHolder

/**
 * Enables MVC groups to be used as component nodes
 *
 * @author Andres Almiray
 * @author Alexander Klein
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
        mvcName = attributes.containsKey('mvcId') ? attributes.remove('mvcId') : mvcName

        MVCGroup mvcGroup = ApplicationHolder.application.buildMVCGroup(mvcType, mvcName, attributes)
        def root = mvcGroup.getScriptResult('view')

        ApplicationHolder.application.addApplicationEventListener(GriffonApplication.Event.DESTROY_MVC_GROUP.name, { String parentId, MVCGroup childGroup, MVCGroup destroyedGroup ->
            if (destroyedGroup.mvcId == parentId) {
                childGroup.destroy()
            }
        }.curry(builder.mvcName, mvcGroup))

        builder.context.root = root
        builder.context.mvcGroup = mvcGroup
        root
    }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        try {
            return builder.context.mvcGroup.controller.metaClass.invokeMethod(builder.context.mvcGroup.controller, 'onHandleNodeAttributes', builder, node, attributes)
        } catch (MissingMethodException e) {
            return false
        }
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

    @Override
    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        safeInvoke(builder.parentContext.mvcGroup.builder, 'setChild', builder, parent, child)
    }

    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        safeInvoke(builder.context.mvcGroup.builder, 'setParent', builder, parent, child)
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        safeInvoke(builder.context.mvcGroup.builder, 'onNodeCompleted', builder, parent, node)
    }

    static protected def safeInvoke(Object obj, String method, Object... args) {
        try {
            return obj.metaClass.invokeMethod(obj, method, args)
        } catch (MissingMethodException e) {
            return null
        }
    }
}
