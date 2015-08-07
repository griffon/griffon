/*
 * Copyright 2008-2015 the original author or authors.
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

import com.googlecode.openbeans.PropertyEditor
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class BufferedImagePropertyEditorSpec extends Specification {

    void "BufferedImage format '#format' should be equal to #image"() {
        setup:
        PropertyEditor editor = new BufferedImagePropertyEditor()

        when:

        editor.value = format

        then:
        image == editor.value

        where:
        image | format
        null  | null
        null  | ''
        null  | ' '
    }

    void "Invalid image format '#format'"() {
        setup:

        PropertyEditor editor = new BufferedImagePropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            [],
            [1],
            [1, 2],
            [1, 2, 3, 4, 5],
            [new Object()],
            'F00',
            '#F0',
            '#FF0000FF00',
            ['HH', 'FF', '00'],
            new Object(),
        ]
    }
}
