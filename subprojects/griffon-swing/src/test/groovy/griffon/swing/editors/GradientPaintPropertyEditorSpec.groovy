package griffon.swing.editors

import spock.lang.Specification
import spock.lang.Unroll

import java.awt.GradientPaint
import java.beans.PropertyEditor

import static java.awt.Color.BLACK
import static java.awt.Color.WHITE

@Unroll
class GradientPaintPropertyEditorSpec extends Specification {
    void "GradientPaint format '#format' is supported"() {
        setup:

        PropertyEditor editor = new GradientPaintPropertyEditor()

        when:
        editor.value = format

        then:

        paintsAreEqual gradientPaint, editor.value

        where:
        gradientPaint                                   | format
        new GradientPaint(0f, 0f, BLACK, 1f, 1f, WHITE) | '0,0,BLACK,1,1,WHITE'
        new GradientPaint(0f, 0f, BLACK, 1f, 1f, WHITE) | [0, 0, BLACK, 1, 1, WHITE]
        new GradientPaint(0f, 0f, BLACK, 1f, 1f, WHITE) | [0, 0, 'BLACK', 1, 1, 'WHITE']
        new GradientPaint(0f, 0f, BLACK, 1f, 1f, WHITE) | [x1: 0, y1: 0, c1: BLACK, x2: 1, y2: 1, c2: WHITE]
        new GradientPaint(0f, 0f, BLACK, 1f, 1f, WHITE) | [x1: 0, y1: 0, c1: 'BLACK', x2: 1, y2: 1, c2: 'WHITE']
        new GradientPaint(0f, 0f, BLACK, 1f, 1f, WHITE) | '0, 0 | 1, 1 | BLACK, WHITE'
    }

    private static void paintsAreEqual(GradientPaint p1, GradientPaint p2) {
        assert p1.point1 == p2.point1 &&
            p1.point2 == p2.point2 &&
            p1.color1 == p2.color1 &&
            p1.color2 == p2.color2 &&
            p1.cyclic == p2.cyclic
    }

    void "Invalid gradientPaint format '#format'"() {
        setup:

        PropertyEditor editor = new GradientPaintPropertyEditor()

        when:
        editor.value = format
        println editor.value

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
            [c1: 'a'],
            new Object()
        ]
    }
}
