package griffon.swing.editors

import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Dimension
import java.beans.PropertyEditor

@Unroll
class DimensionPropertyEditorSpec extends Specification {
    void "Dimension format '#format' should be equal to #dimension"() {
        setup:

        PropertyEditor editor = new DimensionPropertyEditor()

        when:
        editor.value = format

        then:

        dimension == editor.value

        where:
        dimension             | format
        new Dimension(10, 20) | '10,20'
        new Dimension(10, 20) | '10, 20'
        new Dimension(10, 20) | ' 10, 20'
        new Dimension(10, 20) | ' 10, 20 '
        new Dimension(10, 20) | [10, 20]
        new Dimension(10, 20) | ['10', '20']
        new Dimension(10, 10) | 10
        new Dimension(10, 10) | '10'
        new Dimension(10, 10) | [10]
        new Dimension(10, 10) | ['10']
        new Dimension(10, 20) | [width: 10, height: 20]
        new Dimension(10, 20) | [width: '10', height: '20']
        new Dimension(10, 20) | [w: 10, h: 20]
        new Dimension(10, 20) | [w: '10', h: '20']
        new Dimension(0, 0)   | [:]
        new Dimension(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid dimension format '#format'"() {
        setup:

        PropertyEditor editor = new DimensionPropertyEditor()

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
