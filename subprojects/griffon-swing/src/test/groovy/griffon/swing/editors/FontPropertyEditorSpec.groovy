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
package griffon.swing.editors

import griffon.util.TestUtils
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Font
import java.beans.PropertyEditor

@Unroll
@IgnoreIf({ TestUtils.headless })
class FontPropertyEditorSpec extends Specification {
    void "Font format '#format' should be equal to #font"() {
        setup:

        PropertyEditor editor = new FontPropertyEditor()

        when:
        editor.value = format

        then:

        font == editor.value

        where:
        font                                               | format
        null                                               | null
        null                                               | ''
        null                                               | ' '
        null                                               | []
        null                                               | [:]
        new Font('Helvetica', Font.PLAIN, 12)              | 'Helvetica-PLAIN-12'
        new Font('Helvetica', Font.BOLD, 12)               | 'Helvetica-BOLD-12'
        new Font('Helvetica', Font.ITALIC, 12)             | 'Helvetica-ITALIC-12'
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | 'Helvetica-BOLDITALIC-12'
        new Font('Helvetica', Font.PLAIN, 12)              | ['Helvetica', 'PLAIN', '12']
        new Font('Helvetica', Font.BOLD, 12)               | ['Helvetica', 'BOLD', '12']
        new Font('Helvetica', Font.ITALIC, 12)             | ['Helvetica', 'ITALIC', '12']
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | ['Helvetica', 'BOLDITALIC', '12']
        new Font('Helvetica', Font.PLAIN, 12)              | [family: 'Helvetica', style: 'PLAIN', size: '12']
        new Font('Helvetica', Font.BOLD, 12)               | [family: 'Helvetica', style: 'BOLD', size: '12']
        new Font('Helvetica', Font.ITALIC, 12)             | [family: 'Helvetica', style: 'ITALIC', size: '12']
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | [family: 'Helvetica', style: 'BOLDITALIC', size: '12']
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | new Font('Helvetica', Font.BOLD | Font.ITALIC, 12)
    }

    void "Invalid font format '#format'"() {
        setup:

        PropertyEditor editor = new FontPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            'foo-bar-baz',
            'Helvetica-FOO-12',
            'Helvetica-BOLD-baz',
            ['Helvetica'],
            ['Helvetica', 'BOLD'],
            [family: 'Helvetica'],
            new Object()
        ]
    }

    void "Formatted font '#expected'"() {
        given:

        PropertyEditor editor = new FontPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value                                              | expected
        null                                               | null
        new Font('Helvetica', Font.PLAIN, 12)              | 'Helvetica-PLAIN-12'
        new Font('Helvetica', Font.BOLD, 12)               | 'Helvetica-BOLD-12'
        new Font('Helvetica', Font.ITALIC, 12)             | 'Helvetica-ITALIC-12'
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | 'Helvetica-BOLDITALIC-12'
    }
}
