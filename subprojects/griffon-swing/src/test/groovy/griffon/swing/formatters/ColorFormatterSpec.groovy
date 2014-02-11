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
package griffon.swing.formatters

import griffon.core.formatters.ParseException
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Color

import static griffon.swing.formatters.ColorFormatter.*

@Unroll
class ColorFormatterSpec extends Specification {
    void "Color #color with pattern #pattern produces literal #literal"() {
        given:
        ColorFormatter formatter = ColorFormatter.getInstance(pattern)

        when:
        String str = formatter.format(color)
        Color clr = formatter.parse(str)

        then:
        pattern ? pattern == formatter.pattern : true
        str == literal
        clr.toString() == color.toString()

        where:
        pattern                  | color     | literal
        PATTERN_SHORT            | Color.RED | '#f00'
        PATTERN_SHORT_WITH_ALPHA | Color.RED | '#f00f'
        PATTERN_LONG             | Color.RED | '#ff0000'
        PATTERN_LONG_WITH_ALPHA  | Color.RED | '#ff0000ff'
        null                     | Color.RED | '#ff0000'
        ''                       | Color.RED | '#ff0000'
    }

    void "Parse error for pattern #pattern with literal #literal"() {
        given:
        ColorFormatter formatter = ColorFormatter.getInstance(pattern)

        when:
        formatter.parse(literal)

        then:
        thrown(ParseException)

        where:
        pattern                  | literal
        PATTERN_SHORT            | null
        PATTERN_SHORT            | ''
        PATTERN_SHORT            | 'f00'
        PATTERN_SHORT            | '#f00f'
        PATTERN_SHORT_WITH_ALPHA | null
        PATTERN_SHORT_WITH_ALPHA | ''
        PATTERN_SHORT_WITH_ALPHA | 'f00f'
        PATTERN_SHORT_WITH_ALPHA | '#f00f0'
        PATTERN_LONG             | null
        PATTERN_LONG             | ''
        PATTERN_LONG             | 'ff0000'
        PATTERN_LONG             | '#ff0000f'
        PATTERN_LONG_WITH_ALPHA  | null
        PATTERN_LONG_WITH_ALPHA  | ''
        PATTERN_LONG_WITH_ALPHA  | 'ff0000ff'
        PATTERN_LONG_WITH_ALPHA  | '#ff0000ff0'
    }

    void "Illegal pattern #pattern"() {
        when:
        ColorFormatter.getInstance(pattern)

        then:
        thrown(IllegalArgumentException)

        where:
        pattern << '#CMYK'
    }
}
