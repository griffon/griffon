/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.core.editors

import spock.lang.Specification
import spock.lang.Unroll

import java.beans.PropertyEditor

@Unroll
class PropertyEditorResolverSpec extends Specification {
    void "PropertyEditor for type #type should be #editorClass"() {
        setup:
        PropertyEditorResolver.clear()
        PropertyEditorResolver.registerEditor(String, StringPropertyEditor)

        expect:
        PropertyEditorResolver.findEditor(type)?.class == editorClass

        where:
        type    | editorClass
        Numbers | EnumPropertyEditor
        String  | StringPropertyEditor
    }

    void "PropertyEditor at index 0 is called (value)"() {
        setup:
        APropertyEditor.called = 0
        BPropertyEditor.called = 0
        PropertyEditorResolver.registerEditor(Object, APropertyEditor)
        PropertyEditorResolver.registerEditor(Object, BPropertyEditor)

        when:
        PropertyEditor editor = PropertyEditorResolver.findEditor(Object)
        editor.value = 'Groovy'

        then:
        editor.value == 'Groovy'
        APropertyEditor.called == 1
        BPropertyEditor.called == 0
    }

    void "PropertyEditor at index 1 is called (value)"() {
        setup:
        APropertyEditor.called = 0
        BPropertyEditor.called = 0
        PropertyEditorResolver.registerEditor(Object, APropertyEditor)
        PropertyEditorResolver.registerEditor(Object, BPropertyEditor)

        when:
        PropertyEditor editor = PropertyEditorResolver.findEditor(Object)
        editor.value = 1

        then:
        editor.value == 1
        APropertyEditor.called == 0
        BPropertyEditor.called == 1
    }

    void "PropertyEditor at index 0 is called (text)"() {
        setup:
        APropertyEditor.called = 0
        BPropertyEditor.called = 0
        PropertyEditorResolver.registerEditor(Object, APropertyEditor)
        PropertyEditorResolver.registerEditor(Object, BPropertyEditor)

        when:
        PropertyEditor editor = PropertyEditorResolver.findEditor(Object)
        editor.asText = 'Groovy'

        then:
        editor.asText == 'Groovy'
        APropertyEditor.called == 1
        BPropertyEditor.called == 0
    }

    void "Register and unregister an editor class"() {
        given:
        PropertyEditorResolver.clear()
        PropertyEditorResolver.registerEditor(Object, APropertyEditor)

        when:
        PropertyEditor editor = PropertyEditorResolver.findEditor(Object)

        then:
        editor instanceof APropertyEditor

        when:
        editor = PropertyEditorResolver.unregisterEditor(Object)

        then: 'editor is a NoopPropertyEditor'
        !(editor instanceof APropertyEditor)
    }

    void "Register non-null and null editor for the same target class"() {
        given:
        PropertyEditorResolver.clear()

        when:
        PropertyEditorResolver.registerEditor(Object, APropertyEditor)
        PropertyEditor editor = PropertyEditorResolver.findEditor(Object)

        then:
        editor instanceof APropertyEditor

        when:
        PropertyEditorResolver.registerEditor(Object, null)
        editor = PropertyEditorResolver.unregisterEditor(Object)

        then: 'editor is a NoopPropertyEditor'
        !(editor instanceof APropertyEditor)
    }

    static class APropertyEditor extends AbstractPropertyEditor {
        static int called = 0

        @Override
        void setValue(Object value) {
            if (value instanceof String) {
                super.setValue(value)
                called++
            } else {
                throw illegalValue(value, Object)
            }
        }
    }

    static class BPropertyEditor extends AbstractPropertyEditor {
        static int called = 0

        @Override
        void setValue(Object value) {
            if (value instanceof Number) {
                super.setValue(value)
                called++
            } else {
                throw illegalValue(value, Object)
            }
        }
    }
}
