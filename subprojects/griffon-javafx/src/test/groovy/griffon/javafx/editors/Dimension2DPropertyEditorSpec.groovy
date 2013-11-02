package griffon.javafx.editors

import spock.lang.Specification
import spock.lang.Unroll

import javafx.geometry.Dimension2D
import java.beans.PropertyEditor

@Unroll
class Dimension2DPropertyEditorSpec extends Specification {
    void "Dimension2D format '#format' should be equal to #dimension"() {
        setup:

        PropertyEditor editor = new Dimension2DPropertyEditor()

        when:
        editor.value = format

        then:

        dimension == editor.value

        where:
        dimension               | format
        new Dimension2D(10, 20) | '10,20'
        new Dimension2D(10, 20) | '10, 20'
        new Dimension2D(10, 20) | ' 10, 20'
        new Dimension2D(10, 20) | ' 10, 20 '
        new Dimension2D(10, 20) | [10, 20]
        new Dimension2D(10, 20) | ['10', '20']
        new Dimension2D(10, 10) | 10
        new Dimension2D(10, 10) | '10'
        new Dimension2D(10, 10) | [10]
        new Dimension2D(10, 10) | ['10']
        new Dimension2D(10, 20) | [width: 10, height: 20]
        new Dimension2D(10, 20) | [width: '10', height: '20']
        new Dimension2D(10, 20) | [w: 10, h: 20]
        new Dimension2D(10, 20) | [w: '10', h: '20']
        new Dimension2D(0, 0)   | [:]
        new Dimension2D(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid dimension format '#format'"() {
        setup:

        PropertyEditor editor = new Dimension2DPropertyEditor()

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
                [width: 'a'],
                [w: 'b'],
                new Object()
        ]
    }
}
