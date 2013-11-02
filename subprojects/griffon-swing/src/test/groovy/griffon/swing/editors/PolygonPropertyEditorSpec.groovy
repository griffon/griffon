package griffon.swing.editors

import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Polygon
import java.beans.PropertyEditor

@Unroll
class PolygonPropertyEditorSpec extends Specification {
    void "Polygon format '#format' should be supported"() {
        setup:

        PropertyEditor editor = new PolygonPropertyEditor()

        when:
        editor.value = format

        then:

        polygonsAreEqual polygon, editor.value

        where:
        polygon                                          | format
        new Polygon([0, 2] as int[], [1, 3] as int[], 2) | '0,1,2,3'
        new Polygon([0, 2] as int[], [1, 3] as int[], 2) | '0, 1, 2, 3'
        new Polygon([0, 2] as int[], [1, 3] as int[], 2) | ' 0, 1, 2, 3'
        new Polygon([0, 2] as int[], [1, 3] as int[], 2) | ' 0, 1, 2, 3 '
        new Polygon([0, 2] as int[], [1, 3] as int[], 2) | [0, 1, 2, 3]
        new Polygon([0, 2] as int[], [1, 3] as int[], 2) | ['0', '1', '2', '3']
    }

    private static void polygonsAreEqual(Polygon p1, Polygon p2) {
        assert p1.xpoints == p2.xpoints &&
            p1.ypoints == p2.ypoints &&
            p1.npoints == p2.npoints
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
            '',
            '   ',
            'garbage',
            '1, 2, 3',
            [],
            [1, 2, 3],
            new Object()
        ]
    }
}
