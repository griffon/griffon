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

import spock.lang.Specification
import spock.lang.Unroll

import java.awt.geom.Point2D
import java.beans.PropertyEditor

@Unroll
class Point2DPropertyEditorSpec extends Specification {
    void "Point2D format '#format' should be equal to #point"() {
        setup:

        PropertyEditor editor = new Point2DPropertyEditor()

        when:
        editor.value = format

        then:

        point == editor.value

        where:
        point                      | format
        new Point2D.Double(10, 20) | '10,20'
        new Point2D.Double(10, 20) | '10, 20'
        new Point2D.Double(10, 20) | ' 10, 20'
        new Point2D.Double(10, 20) | ' 10, 20 '
        new Point2D.Double(10, 20) | [10, 20]
        new Point2D.Double(10, 20) | ['10', '20']
        new Point2D.Double(10, 10) | 10
        new Point2D.Double(10, 10) | '10'
        new Point2D.Double(10, 10) | [10]
        new Point2D.Double(10, 10) | ['10']
        new Point2D.Double(10, 20) | [x: 10, y: 20]
        new Point2D.Double(10, 20) | [x: '10', y: '20']
        new Point2D.Double(0, 0)   | [:]
        new Point2D.Double(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid point format '#format'"() {
        setup:

        PropertyEditor editor = new Point2DPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            '',
            '   ',
            'garbage',
            '1, 2, 3',
            [],
            [1, 2, 3],
            [x: 'a'],
            [y: 'b'],
            new Object()
        ]
    }
}
