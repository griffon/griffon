/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.builder.core.factory

import griffon.annotations.core.Nonnull
import griffon.annotations.event.EventHandler
import griffon.core.GriffonApplication
import griffon.core.events.DestroyMVCGroupEvent
import griffon.core.mvc.MVCGroup
import griffon.core.mvc.MVCGroupManager

import static org.codehaus.griffon.runtime.groovy.mvc.GroovyAwareMVCGroup.CURRENT_MVCGROUP

/**
 * Enables MVC groups to be used as component nodes
 *
 * @author Andres Almiray
 * @author Alexander Klein
 */
@SuppressWarnings("rawtypes")
class MetaComponentFactory extends AbstractFactory {
    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        Map attrs = resolveAttributes(attributes)
        String mvcType = resolveMvcType(name, value, attrs)
        String mvcId = resolveMvcId(builder, name, value, attrs)
        Map mvcArgs = resolveMvcArgs(attrs)
        Map mvcArgsCopy = [*: mvcArgs]
        attributes.clear()
        attributes.putAll(attrs)

        MVCGroup parentGroup = builder.getVariables().get(CURRENT_MVCGROUP)
        def receiver = parentGroup ?: builder.application.mvcGroupManager
        MVCGroup mvcGroup = receiver.createMVCGroup(mvcType, mvcId, mvcArgs)
        def root = mvcGroup.rootNode

        new DestroyEventHandler(mvcId, mvcGroup, builder.application)

        builder.context.root = root
        builder.context.mvcGroup = mvcGroup
        builder.context.mvcArgs = mvcArgsCopy
        root
    }

    protected Map resolveAttributes(Map attributes) {
        [*: attributes]
    }

    protected String resolveMvcType(Object name, Object value, Map attributes) {
        String mvcType = ''
        if (value != null && value instanceof CharSequence) {
            return value.toString()
        }
        throw new IllegalArgumentException("In $name value must be an MVC group type")
    }

    protected String resolveMvcId(FactoryBuilderSupport builder, Object name, String mvcType, Map attributes) {
        String mvcId = attributes.remove('mvcId') ?: mvcType

        MVCGroupManager mvcGroupManager = builder.application.mvcGroupManager
        if (mvcGroupManager.findGroup(mvcId)) {
            mvcId += '-' + UUID.randomUUID().toString()
        }
        mvcId
    }

    protected Map resolveMvcArgs(Map attributes) {
        (attributes.remove('mvcArgs') ?: [:]) + [metaComponentArgs: attributes]
    }

    private static class DestroyEventHandler {
        private final String parentId
        private final MVCGroup childGroup
        private final GriffonApplication application

        DestroyEventHandler(String parentId, MVCGroup childGroup, GriffonApplication application) {
            this.parentId = parentId
            this.childGroup = childGroup
            this.application = application
            application.eventRouter.subscribe(this)
        }

        @EventHandler
        void handleDestroyMVCGroupEvent(@Nonnull DestroyMVCGroupEvent event) {
            MVCGroup destroyedGroup = event.group
            if (destroyedGroup.mvcId == parentId) {
                childGroup.destroy()
                application.eventRouter.unsubscribe(this)
            }
        }
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        try {
            return builder.context.mvcGroup.controller.metaClass.invokeMethod(builder.context.mvcGroup.controller, 'onHandleNodeAttributes', builder, node, attributes)
        } catch (MissingMethodException e) {
            return false
        }
    }

    @Override
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
        safeInvoke(builder.context.mvcGroup.controller, 'setChild', builder, parent, child)
        safeInvoke(builder.parentContext.mvcGroup.builder, 'setChild', builder, parent, child)
        super.setChild(builder, parent, child)
    }

    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        safeInvoke(builder.context.mvcGroup.controller, 'setParent', builder, parent, child)
        safeInvoke(builder.context.mvcGroup.builder, 'setParent', builder, parent, child)
        super.setParent(builder, parent, child)
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        safeInvoke(builder.context.mvcGroup.controller, 'onNodeCompleted', builder, parent, node)
        safeInvoke(builder.context.mvcGroup.builder, 'onNodeCompleted', builder, parent, node)
        super.onNodeCompleted(builder, parent, node)
    }

    static protected def safeInvoke(Object obj, String method, Object... args) {
        try {
            return obj.metaClass.invokeMethod(obj, method, args)
        } catch (MissingMethodException e) {
            return null
        }
    }
}
