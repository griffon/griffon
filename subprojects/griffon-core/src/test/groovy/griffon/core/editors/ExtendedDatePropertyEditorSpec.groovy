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
import java.time.LocalDate
import java.time.LocalDateTime

@Unroll
class ExtendedDatePropertyEditorSpec extends PropertyEditorSpecSupport {
    void "Date literal '#literal' with format '#format' should be equal to #value"() {
        setup:

        PropertyEditor editor = new ExtendedDatePropertyEditor()

        when:
        editor.format = format
        editor.value = literal

        Date v1 = value ? clearTime(value) : value
        Date v2 = editor.value ? clearTime(editor.value) : editor.value

        then:

        v1?.time == v2?.time

        where:
        literal                                  | format | value
        null                                     | null   | null
        LocalDate.of(1970, 1, 1)                 | null   | epochAsDate()
        LocalDateTime.of(1970, 1, 1, 12, 13, 14) | null   | epochAsDate()
    }
}
