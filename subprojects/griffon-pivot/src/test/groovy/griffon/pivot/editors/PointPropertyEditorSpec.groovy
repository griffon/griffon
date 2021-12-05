/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.pivot.editors

import org.apache.pivot.wtk.Point
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.beans.PropertyEditor

@Unroll
class PointPropertyEditorSpec extends Specification {
    @Shared
    private static Point point = new Point(10, 20)

    void "Point format '#format' should be equal to #point"() {
        setup:

        PropertyEditor editor = new PointPropertyEditor()

        when:
        editor.value = format

        then:

        value == editor.value

        where:
        value             | format
        null              | null
        null              | ''
        null              | ' '
        null              | []
        null              | [:]
        new Point(10, 20) | '10,20'
        new Point(10, 20) | '10, 20'
        new Point(10, 20) | ' 10, 20'
        new Point(10, 20) | ' 10, 20 '
        new Point(10, 20) | [10, 20]
        new Point(10, 20) | ['10', '20']
        new Point(10, 10) | 10
        new Point(10, 10) | '10'
        new Point(10, 10) | [10]
        new Point(10, 10) | ['10']
        new Point(10, 20) | [x: 10, y: 20]
        new Point(10, 20) | [x: '10', y: '20']
        new Point(10, 20) | new Point(10, 20)
        new Point(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid point format '#format'"() {
        setup:

        PropertyEditor editor = new PointPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            [1, 2, 3],
            [new Object()],
            [x: new Object()],
            [x: 'a'],
            [y: 'b'],
            new Object()
        ]
    }

    void "Formatted point '#expected'"() {
        given:

        PropertyEditor editor = new PointPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value | expected
        null  | null
        point | '10, 20'
    }
}
