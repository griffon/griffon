/*
 * Copyright 2009-2012 the original author or authors.
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

/**
 * @author Andres Almiray
 */
class EventListenerFactory extends AbstractFactory {
    private static final Class[] PARAMS = [FactoryBuilderSupport] as Class[]
    final Class adapterClass
    final List<Class> parents = []
    final String getListenersMethod

    EventListenerFactory(Class adapterClass, List<Class> parents, String listenersMethod) {
        this.adapterClass = adapterClass
        this.parents.addAll(parents)
        this.getListenersMethod = listenersMethod
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
    throws InstantiationException, IllegalAccessException {
        if (value && adapterClass.isAssignableFrom(value.getClass())) return value
        adapterClass.getDeclaredConstructor(PARAMS).newInstance([builder] as Object[])
    }

    boolean isHandlesNodeChildren() {
        return true
    }

    boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        node.delegate = childContent.delegate
        childContent.delegate = node
        childContent()
        return false
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        if (!parent) return
        for (candidate in parents) {
            if (candidate.isAssignableFrom(parent.getClass())) {
                parent."$getListenersMethod"().add(node)
                break
            }
        }
    }
}
