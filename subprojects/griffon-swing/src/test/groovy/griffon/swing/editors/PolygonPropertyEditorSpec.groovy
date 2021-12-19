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
package griffon.swing.editors

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.*
import java.beans.PropertyEditor

@Unroll
class PolygonPropertyEditorSpec extends Specification {
    @Shared
    private static Polygon polygon = new Polygon([0, 2] as int[], [1, 3] as int[], 2)

    void "Polygon format '#format' should be supported"() {
        setup:

        PropertyEditor editor = new PolygonPropertyEditor()

        when:
        editor.value = format

        then:

        polygonsAreEqual value, editor.value

        where:
        value   | format
        null    | null
        null    | ''
        null    | ' '
        null    | []
        polygon | '0,1,2,3'
        polygon | '0, 1, 2, 3'
        polygon | ' 0, 1, 2, 3'
        polygon | ' 0, 1, 2, 3 '
        polygon | [0, 1, 2, 3]
        polygon | ['0', '1', '2', '3']
        polygon | polygon
    }

    private static void polygonsAreEqual(Polygon p1, Polygon p2) {
        if (p1 == null) {
            assert p2 == null
        } else {
            assert p1.xpoints == p2.xpoints &&
                p1.ypoints == p2.ypoints &&
                p1.npoints == p2.npoints
        }
    }

    void "Invalid polygon format '#format'"() {
        setup:

        PropertyEditor editor = new PolygonPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            '1, 2, 3, a',
            [1, 2, 3],
            [new Object(), new Object(), new Object(), new Object()],
            [new Object()],
            new Object()
        ]
    }

    void "Formatted polygon '#expected'"() {
        given:

        PropertyEditor editor = new PolygonPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value   | expected
        null    | null
        polygon | '0, 1, 2, 3'
    }
}
