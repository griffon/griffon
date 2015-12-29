/*
 * Copyright 2008-2016 the original author or authors.
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
class EnumPropertyEditorSpec extends Specification {
    void "Enum literal '#literal' should be equal to #value"() {
        setup:

        PropertyEditor editor = new EnumPropertyEditor()

        when:
        editor.enumType = Numbers
        editor.value = literal

        then:

        editor.enumType == Numbers
        value == editor.value

        where:
        literal     | value
        null        | null
        ''          | null
        ' '         | null
        'ZERO'      | Numbers.ZERO
        Numbers.ONE | Numbers.ONE
    }

    void "Invalid enum literal '#literal'"() {
        setup:

        PropertyEditor editor = new EnumPropertyEditor()

        when:
        editor.enumType = Numbers
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            'garbage',
            [],
            [1, 2, 3],
            [:],
            [key: 'value'],
            new Object()
        ]
    }
}

enum Numbers {
    ZERO, ONE, TWO
}