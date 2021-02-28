/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.util

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GriffonApplicationUtilsSpec extends Specification {
    def "Locale literal '#literal' is equal to #locale"() {
        expect:
        locale == GriffonApplicationUtils.parseLocale(literal)

        where:
        literal       | locale
        ''            | Locale.default
        ' ' | Locale.default
        'en'          | Locale.ENGLISH
        'en_GB'       | Locale.UK
        'de_CH_Basel' | new Locale('de', 'CH', 'Basel')
        'de_CH_Basel_45' | Locale.default
    }
}
