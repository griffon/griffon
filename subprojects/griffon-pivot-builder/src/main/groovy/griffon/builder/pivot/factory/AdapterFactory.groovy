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
class AdapterFactory extends BeanFactory {
    AdapterFactory(Class adapterClass) {
        super(adapterClass)
    }

    boolean isHandlesNodeChildren() {
        true
    }

    boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        childContent.delegate = node
        childContent.resolveStrategy = Closure.DELEGATE_FIRST
        childContent()
        false
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        String addListenersMethod = 'get' + beanClass.simpleName.capitalize() - 'Adapter' + 'Listeners'
        parent."$addListenersMethod"().add(child)
    }
}
