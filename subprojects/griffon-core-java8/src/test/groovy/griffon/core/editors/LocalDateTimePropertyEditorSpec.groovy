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
class LocalDateTimePropertyEditorSpec extends PropertyEditorSpecSupport {
    void "LocalDateTime literal '#literal' with format '#format' should be equal to #value"() {
        setup:

        PropertyEditor editor = new LocalDateTimePropertyEditor()

        when:
        editor.format = format
        editor.value = literal

        then:

        value == editor.value

        where:
        literal                                  | format                | value
        null                                     | null                  | null
        []                                       | null                  | null
        ''                                       | null                  | null
        "1970-01-01T12:13:14"                    | null                  | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        0                                        | null                  | LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
        epochAsDate()                            | null                  | LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
        epochAsCalendar()                        | null                  | LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
        [1970, 1, 1,]                            | null                  | LocalDateTime.of(1970, 1, 1, 0, 0, 0)
        LocalDate.of(1970, 1, 1)                 | null                  | LocalDateTime.of(1970, 1, 1, 0, 0, 0)
        [1970, 1, 1, 12,]                        | null                  | LocalDateTime.of(1970, 1, 1, 12, 0, 0)
        [1970, 1, 1, 12, 13]                     | null                  | LocalDateTime.of(1970, 1, 1, 12, 13, 0)
        [1970, 1, 1, 12, 13, 14]                 | null                  | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        [1970, 1, 1, 12, 13, 14, 0]              | null                  | LocalDateTime.of(1970, 1, 1, 12, 13, 14, 0)
        ['1970', '1', '1', '12', '13', '14']     | null                  | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        LocalDateTime.of(1970, 1, 1, 12, 13, 14) | null                  | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        LocalDateTime.of(1970, 1, 1, 12, 13, 14) | null                  | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        ''                                       | 'yyyy-MM-dd HH:mm:ss' | null
        '1970-01-01 12:13:14'                    | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        0                                        | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
        epochAsDate()                            | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
        epochAsCalendar()                        | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0)
        LocalDate.of(1970, 1, 1)                 | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 0, 0, 0)
        [1970, 1, 1,]                            | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 0, 0, 0)
        [1970, 1, 1, 12,]                        | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 0, 0)
        [1970, 1, 1, 12, 13]                     | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 13, 0)
        [1970, 1, 1, 12, 13, 14]                 | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        [1970, 1, 1, 12, 13, 14, 0]              | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 13, 14, 0)
        ['1970', '1', '1', '12', '13', '14']     | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        LocalDateTime.of(1970, 1, 1, 12, 13, 14) | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
        LocalDateTime.of(1970, 1, 1, 12, 13, 14) | 'yyyy-MM-dd HH:mm:ss' | LocalDateTime.of(1970, 1, 1, 12, 13, 14)
    }

    void "Invalid date literal '#literal'"() {
        setup:

        PropertyEditor editor = new LocalDateTimePropertyEditor()

        when:
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            'garbage',
            [1, 2],
            [1, 2, 3, 4, 5, 6, 7, 8],
            [:],
            new Object()
        ]
    }
}
