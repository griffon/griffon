/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.core.editors

import spock.lang.Unroll

import java.beans.PropertyEditor
import java.time.LocalDateTime
import java.time.LocalTime

@Unroll
class LocalTimePropertyEditorSpec extends PropertyEditorSpecSupport {
    void "LocalTime literal '#literal' with format '#format' should be equal to #value"() {
        setup:

        PropertyEditor editor = new LocalTimePropertyEditor()

        when:
        editor.format = format
        editor.value = literal

        then:

        value == editor.value

        where:
        literal                                          | format         | value
        null                                             | null           | null
        ''                                               | null           | null
        []                                               | null           | null
        '01:02:03.400'                                   | null           | LocalTime.of(1, 2, 3, 400000000)
        0                                                | null           | LocalTime.of(0, 0, 0, 0)
        epochAsDate()                                    | null           | LocalTime.of(0, 0, 0, 0)
        epochAsCalendar()                                | null           | LocalTime.of(0, 0, 0, 0)
        [1, 2, 3]                                        | null           | LocalTime.of(1, 2, 3)
        [1, 2, 3, 400000000]                             | null           | LocalTime.of(1, 2, 3, 400000000)
        ['1', '2', '3', '400000000']                     | null           | LocalTime.of(1, 2, 3, 400000000)
        LocalTime.of(1, 2, 3, 400000000)                 | null           | LocalTime.of(1, 2, 3, 400000000)
        LocalDateTime.of(1970, 1, 1, 1, 2, 3, 400000000) | null           | LocalTime.of(1, 2, 3, 400000000)
        ''                                               | 'HH:mm:ss.SSS' | null
        '01:02:03.400'                                   | 'HH:mm:ss.SSS' | LocalTime.of(1, 2, 3, 400000000)
        0                                                | 'HH:mm:ss.SSS' | LocalTime.of(0, 0, 0, 0)
        epochAsDate()                                    | 'HH:mm:ss.SSS' | LocalTime.of(0, 0, 0, 0)
        epochAsCalendar()                                | 'HH:mm:ss.SSS' | LocalTime.of(0, 0, 0, 0)
        [1, 2, 3]                                        | 'HH:mm:ss.SSS' | LocalTime.of(1, 2, 3, 0)
        [1, 2, 3, 400000000]                             | 'HH:mm:ss.SSS' | LocalTime.of(1, 2, 3, 400000000)
        ['1', '2', '3', '400000000']                     | 'HH:mm:ss.SSS' | LocalTime.of(1, 2, 3, 400000000)
        LocalTime.of(1, 2, 3, 400000000)                 | 'HH:mm:ss.SSS' | LocalTime.of(1, 2, 3, 400000000)
        LocalDateTime.of(1970, 1, 1, 1, 2, 3, 400000000) | 'HH:mm:ss.SSS' | LocalTime.of(1, 2, 3, 400000000)
    }

    void "Invalid date literal '#literal'"() {
        setup:

        PropertyEditor editor = new LocalTimePropertyEditor()

        when:
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            'garbage',
            [:],
            [1, 2],
            [1, 2, 3, 4, 5],
            new Object()
        ]
    }
}
