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
import java.time.LocalDate
import java.time.LocalDateTime

@Unroll
class LocalDatePropertyEditorSpec extends PropertyEditorSpecSupport {
    void "LocalDate literal '#literal' with format '#format' should be equal to #value"() {
        setup:

        PropertyEditor editor = new LocalDatePropertyEditor()

        when:
        editor.format = format
        editor.value = literal

        then:

        value == editor.value

        where:
        literal                                  | format       | value
        null                                     | null         | null
        ''                                       | null         | null
        '1970-01-01'                             | null         | LocalDate.of(1970, 1, 1)
        0                                        | null         | LocalDate.of(1970, 1, 1)
        epochAsDate()                            | null         | LocalDate.of(1970, 1, 1)
        epochAsCalendar()                        | null         | LocalDate.of(1970, 1, 1)
        [1970, 1, 1]                             | null         | LocalDate.of(1970, 1, 1)
        ['1970', '1', '1']                       | null         | LocalDate.of(1970, 1, 1)
        [y: 1970, m: 1, d: 1]                    | null         | LocalDate.of(1970, 1, 1)
        [y: '1970', m: '1', d: 1]                | null         | LocalDate.of(1970, 1, 1)
        LocalDate.of(1970, 1, 1)                 | null         | LocalDate.of(1970, 1, 1)
        LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0) | null         | LocalDate.of(1970, 1, 1)
        ''                                       | 'yyyy-MM-dd' | null
        '1970-01-01'                             | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        0                                        | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        epochAsDate()                            | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        epochAsCalendar()                        | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        [1970, 1, 1]                             | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        ['1970', '1', '1']                       | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        [y: 1970, m: 1, d: 1]                    | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        [y: '1970', m: '1', d: 1]                | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        LocalDate.of(1970, 1, 1)                 | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0) | 'yyyy-MM-dd' | LocalDate.of(1970, 1, 1)
        []                                       | null         | null
        [:]                                      | null         | null
    }

    void "Invalid date literal '#literal'"() {
        setup:

        PropertyEditor editor = new LocalDatePropertyEditor()

        when:
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            'garbage',
            [1, 2],
            [1, 2, 3, 4],
            new Object()
        ]
    }
}
