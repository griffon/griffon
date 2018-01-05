/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

@Unroll
class DatePropertyEditorSpec extends PropertyEditorSpecSupport {
    void "Date literal '#literal' with format '#format' should be equal to #value"() {
        setup:

        PropertyEditor editor = new DatePropertyEditor()

        when:
        editor.format = format
        editor.value = literal

        Date v1 = value ? clearTime(value) : value
        Date v2 = editor.value ? clearTime(editor.value) : editor.value

        then:

        v1?.time == v2?.time

        where:
        literal               | format                | value
        null                  | null                  | null
        ''                    | null                  | null
        '1/1/70 12:00 AM'     | null                  | epochAsDate()
        '0'                   | null                  | epochAsDate()
        0                     | null                  | epochAsDate()
        epochAsDate()         | null                  | epochAsDate()
        epochAsCalendar()     | null                  | epochAsDate()
        ''                    | 'yyyy-MM-dd HH:mm:ss' | null
        '1970-01-01 00:00:00' | 'yyyy-MM-dd HH:mm:ss' | epochAsDate()
        0                     | 'yyyy-MM-dd HH:mm:ss' | epochAsDate()
        epochAsDate()         | 'yyyy-MM-dd HH:mm:ss' | epochAsDate()
        epochAsCalendar()     | 'yyyy-MM-dd HH:mm:ss' | epochAsDate()
    }

    void "Invalid date literal '#literal'"() {
        setup:

        PropertyEditor editor = new DatePropertyEditor()

        when:
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            'garbage',
            [],
            [1, 2, 3],
            [:],
            [key: 'value'],
            new Object()
        ]
    }
}
