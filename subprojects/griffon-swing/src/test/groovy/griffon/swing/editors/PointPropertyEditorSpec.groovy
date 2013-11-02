package griffon.swing.editors

import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Point
import java.beans.PropertyEditor

@Unroll
class PointPropertyEditorSpec extends Specification {
    void "Point format '#format' should be equal to #point"() {
        setup:

        PropertyEditor editor = new PointPropertyEditor()

        when:
        editor.value = format

        then:

        point == editor.value

        where:
        point         | format
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
        new Point(0, 0)   | [:]
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
