/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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

import org.apache.pivot.wtk.Button
import org.apache.pivot.wtk.ButtonGroup
import org.apache.pivot.wtk.RadioButton

/**
 * Copied from SwingBuilder to avoid dependency on Swing module.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class ButtonGroupFactory extends BeanFactory {
    public static final String DELEGATE_PROPERTY_BUTTON_GROUP = '_delegateProperty:buttonGroup'
    public static final String DEFAULT_DELEGATE_PROPERTY_BUTTON_GROUP = 'buttonGroup'

    ButtonGroupFactory() {
        super(ButtonGroup, false)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        builder.context[DELEGATE_PROPERTY_BUTTON_GROUP] = attributes.remove('buttonGroupProperty') ?: DEFAULT_DELEGATE_PROPERTY_BUTTON_GROUP
        return super.newInstance(builder, name, value, attributes)
    }

    static buttonGroupAttributeDelegate(FactoryBuilderSupport builder, Object node, Map attributes) {
        def buttonGroupAttr = builder?.context?.getAt(DELEGATE_PROPERTY_BUTTON_GROUP) ?: DEFAULT_DELEGATE_PROPERTY_BUTTON_GROUP
        if (attributes.containsKey(buttonGroupAttr)) {
            def o = attributes.get(buttonGroupAttr)
            if ((o instanceof ButtonGroup) && (node instanceof Button)) {
                if (!(node instanceof RadioButton)) node.toggleButton = true
                node.buttonGroup = o
                attributes.remove(buttonGroupAttr)
            }
        }
    }
}
