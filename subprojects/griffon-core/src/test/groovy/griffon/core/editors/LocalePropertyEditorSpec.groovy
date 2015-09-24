/*
 * Copyright 2008-2015 the original author or authors.
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
class LocalePropertyEditorSpec extends PropertyEditorSpecSupport {
    void "Locale literal '#literal' should be equal to #value"() {
        setup:
        PropertyEditor editor = new LocalePropertyEditor()

        when:
        editor.value = literal

        then:
        value == editor.value

        where:
        literal       | value
        null          | null
        ''            | Locale.US // force Locale due to base class. See https://github.com/griffon/griffon/issues/111
        'de'          | Locale.GERMAN
        'de_CH'       | new Locale('de', 'CH')
        'de_CH_Basel' | new Locale('de', 'CH', 'Basel')
        Locale.US     | Locale.US
    }

    void "Invalid biginteger literal '#literal'"() {
        setup:

        PropertyEditor editor = new LocalePropertyEditor()

        when:
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            1,
            [],
            [:],
            new Object()
        ]
    }
}
