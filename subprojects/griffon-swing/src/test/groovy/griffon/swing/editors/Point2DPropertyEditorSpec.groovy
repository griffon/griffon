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
