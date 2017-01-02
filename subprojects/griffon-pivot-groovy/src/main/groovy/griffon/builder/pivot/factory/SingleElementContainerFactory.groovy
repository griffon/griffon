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

/**
 * @author Andres Almiray
 */
class SingleElementContainerFactory extends ComponentFactory {
    private final String setter
    private final Class constrainedType

    SingleElementContainerFactory(Class containerClass, String property = 'content', Class constrainedType = Component) {
        super(containerClass)
        this.constrainedType = constrainedType
        this.setter = 'set' + property[0].toUpperCase() + property[1..-1]
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (child && constrainedType.isAssignableFrom(child.getClass())) parent."$setter"(child)
        else super.setChild(builder, parent, child)
    }
}
