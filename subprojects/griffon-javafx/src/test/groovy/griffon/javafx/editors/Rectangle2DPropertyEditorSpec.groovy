package griffon.javafx.editors

import spock.lang.Specification
import spock.lang.Unroll

import javafx.geometry.Rectangle2D
import java.beans.PropertyEditor

@Unroll
class Rectangle2DPropertyEditorSpec extends Specification {
    void "Rectangle2D format '#format' should be equal to #rectangle"() {
        setup:

        PropertyEditor editor = new Rectangle2DPropertyEditor()

        when:
        editor.value = format

        then:

        rectangle == editor.value

        where:
        rectangle                       | format
        new Rectangle2D(10, 20, 30, 40) | '10,20,30,40'
        new Rectangle2D(10, 20, 30, 40) | '10, 20, 30, 40'
        new Rectangle2D(10, 20, 30, 40) | ' 10, 20, 30, 40'
        new Rectangle2D(10, 20, 30, 40) | ' 10, 20, 30, 40 '
        new Rectangle2D(10, 20, 30, 40) | [10, 20, 30, 40]
        new Rectangle2D(10, 20, 30, 40) | ['10', '20', '30', '40']
        new Rectangle2D(10, 20, 30, 40) | [x: 10, y: 20, width: 30, height: 40]
        new Rectangle2D(10, 20, 30, 40) | [x: '10', y: '20', width: '30', height: '40']
        new Rectangle2D(10, 20, 30, 40) | [x: 10, y: 20, w: 30, h: 40]
        new Rectangle2D(10, 20, 30, 40) | [x: '10', y: '20', w: '30', h: '40']
        new Rectangle2D(0, 0, 0, 0)     | [:]
        new Rectangle2D(0, 0, 0, 0)     | [foo: 10, bar: 20]
    }

    void "Invalid rectangle format '#format'"() {
        setup:

        PropertyEditor editor = new Rectangle2DPropertyEditor()

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
