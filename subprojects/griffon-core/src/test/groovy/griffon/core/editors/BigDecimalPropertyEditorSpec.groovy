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
class BigDecimalPropertyEditorSpec extends PropertyEditorSpecSupport {
    void "BigDecimal literal '#literal' with format '#format' should be equal to #value"() {
        setup:

        PropertyEditor editor = new BigDecimalPropertyEditor()

        when:
        editor.format = format
        editor.value = literal

        then:

        value == editor.value

        where:
        literal        | format     | value
        null           | null       | null
        ''             | null       | null
        '1'            | null       | BigDecimal.ONE
        '100%'         | 'percent'  | BigDecimal.ONE
        '$1.00'        | 'currency' | BigDecimal.ONE
        BigDecimal.ONE | null       | BigDecimal.ONE
        BigInteger.ONE | null       | BigDecimal.ONE
        1L             | null       | BigDecimal.ONE
    }

    void "Invalid bigdecimal literal '#literal'"() {
        setup:

        PropertyEditor editor = new BigDecimalPropertyEditor()

        when:
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            'garbage',
            '1, 2, 3',
            [],
            [1, 2, 3],
            [:],
            [key: 'value'],
            new Object()
        ]
    }
}
