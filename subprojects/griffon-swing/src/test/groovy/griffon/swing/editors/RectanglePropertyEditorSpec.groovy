package griffon.swing.editors

import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Rectangle
import java.beans.PropertyEditor

@Unroll
class RectanglePropertyEditorSpec extends Specification {
    void "Rectangle format '#format' should be equal to #rectangle"() {
        setup:

        PropertyEditor editor = new RectanglePropertyEditor()

        when:
        editor.value = format

        then:

        rectangle == editor.value

        where:
        rectangle                     | format
        new Rectangle(10, 20, 30, 40) | '10,20,30,40'
        new Rectangle(10, 20, 30, 40) | '10, 20, 30, 40'
        new Rectangle(10, 20, 30, 40) | ' 10, 20, 30, 40'
        new Rectangle(10, 20, 30, 40) | ' 10, 20, 30, 40 '
        new Rectangle(10, 20, 30, 40) | [10, 20, 30, 40]
        new Rectangle(10, 20, 30, 40) | ['10', '20', '30', '40']
        new Rectangle(10, 20, 30, 40) | [x: 10, y: 20, width: 30, height: 40]
        new Rectangle(10, 20, 30, 40) | [x: '10', y: '20', width: '30', height: '40']
        new Rectangle(10, 20, 30, 40) | [x: 10, y: 20, w: 30, h: 40]
        new Rectangle(10, 20, 30, 40) | [x: '10', y: '20', w: '30', h: '40']
        new Rectangle(0, 0, 0, 0)     | [:]
        new Rectangle(0, 0, 0, 0)     | [foo: 10, bar: 20]
    }

    void "Invalid rectangle format '#format'"() {
        setup:

        PropertyEditor editor = new RectanglePropertyEditor()

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
                '1, 2, 3, 4, 5',
                [],
                [1, 2, 3],
                [1, 2, 3, 4, 5],
                [x: 'a'],
                [y: 'b'],
                new Object()
        ]
    }
}
