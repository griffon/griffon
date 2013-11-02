package griffon.swing.editors

import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Font
import java.beans.PropertyEditor

@Unroll
class FontPropertyEditorSpec extends Specification {
    void "Font format '#format' should be equal to #font"() {
        setup:

        PropertyEditor editor = new FontPropertyEditor()

        when:
        editor.value = format

        then:

        font == editor.value

        where:
        font                                               | format
        new Font('Helvetica', Font.PLAIN, 12)              | 'Helvetica-PLAIN-12'
        new Font('Helvetica', Font.BOLD, 12)               | 'Helvetica-BOLD-12'
        new Font('Helvetica', Font.ITALIC, 12)             | 'Helvetica-ITALIC-12'
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | 'Helvetica-BOLDITALIC-12'
        new Font('Helvetica', Font.PLAIN, 12)              | ['Helvetica', 'PLAIN', '12']
        new Font('Helvetica', Font.BOLD, 12)               | ['Helvetica', 'BOLD', '12']
        new Font('Helvetica', Font.ITALIC, 12)             | ['Helvetica', 'ITALIC', '12']
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | ['Helvetica', 'BOLDITALIC', '12']
        new Font('Helvetica', Font.PLAIN, 12)              | [family: 'Helvetica', style: 'PLAIN', size: '12']
        new Font('Helvetica', Font.BOLD, 12)               | [family: 'Helvetica', style: 'BOLD', size: '12']
        new Font('Helvetica', Font.ITALIC, 12)             | [family: 'Helvetica', style: 'ITALIC', size: '12']
        new Font('Helvetica', Font.BOLD | Font.ITALIC, 12) | [family: 'Helvetica', style: 'BOLDITALIC', size: '12']
    }

    void "Invalid font format '#format'"() {
        setup:

        PropertyEditor editor = new FontPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            '',
            '   ',
            'garbage',
            'foo-bar-baz',
            'Helvetica-FOO-12',
            'Helvetica-BOLD-baz',
            [],
            ['Helvetica'],
            ['Helvetica', 'BOLD'],
            [family: 'Helvetica'],
            new Object()
        ]
    }
}
