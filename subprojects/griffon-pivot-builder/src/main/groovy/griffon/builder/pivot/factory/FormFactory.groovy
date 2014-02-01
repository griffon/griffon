/*
 * Copyright 2008-2014 the original author or authors.
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
import org.apache.pivot.wtk.Form
import org.apache.pivot.wtk.MessageType

/**
 * @author Andres Almiray
 */
class FormFlagFactory extends PivotBeanFactory {
    FormFlagFactory() {
        super(Form.Flag)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        new Form.Flag(
                attributes.remove('type') ?: MessageType.ERROR,
                attributes.remove('message') ?: '<empty>'
        )
    }
}

/**
 * @author Andres Almiray
 */
class FormSectionFactory extends ContainerFactory {
    public static final String DELEGATE_PROPERTY_FORM_LABEL = "_delegateProperty:formLabel"
    public static final String DEFAULT_DELEGATE_PROPERTY_FORM_LABEL = "formLabel"
//    public static final String DELEGATE_PROPERTY_FORM_FLAG = "_delegateProperty:formFlag"
//    public static final String DEFAULT_DELEGATE_PROPERTY_FORM_FLAG = "formFlag"
    public static final String SECTIONS_CONTEXT_DATA_KEY = "FormSections"

    FormSectionFactory() {
        super(Form.Section)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        def newChild = super.newInstance(builder, name, value, attributes)
        builder.context.formSectionFactoryClosure = { FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes ->
            if (builder.current == newChild) inspectChild(cBuilder, cNode, cAttributes)
        }
        builder.addAttributeDelegate(builder.context.formSectionFactoryClosure)
        builder.context[DELEGATE_PROPERTY_FORM_LABEL] = attributes.remove("formLabel") ?: DEFAULT_DELEGATE_PROPERTY_FORM_LABEL
//        builder.context[DELEGATE_PROPERTY_FORM_FLAG] = attributes.remove("formFlag") ?: DEFAULT_DELEGATE_PROPERTY_FORM_FLAG
        builder.context.get(SECTIONS_CONTEXT_DATA_KEY, [])

        return newChild
    }


    static void inspectChild(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (!(node instanceof Component)) return
        def formLabel = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_FORM_LABEL) ?: DEFAULT_DELEGATE_PROPERTY_FORM_LABEL)
//        def formFlag = attributes.remove(builder?.parentContext?.getAt(DELEGATE_PROPERTY_FORM_FLAG) ?: DEFAULT_DELEGATE_PROPERTY_FORM_FLAG)
        if (builder?.parentContext) {
            builder.parentContext[SECTIONS_CONTEXT_DATA_KEY] << [component: node, formLabel: formLabel/*, formFlag: formFlag*/]
        }
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        if (parent instanceof Form) parent.sections.add(node)
        else super.setParent(builder, parent, node)
    }

    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        def sectionData = builder.context.remove(SECTIONS_CONTEXT_DATA_KEY)
        sectionData?.each { chunk ->
            if (chunk?.formLabel) Form.setLabel(chunk.component, chunk.formLabel)
        }
        super.onNodeCompleted(builder, parent, node)
        builder.removeAttributeDelegate(builder.context.formSectionFactoryClosure)
    }
}
