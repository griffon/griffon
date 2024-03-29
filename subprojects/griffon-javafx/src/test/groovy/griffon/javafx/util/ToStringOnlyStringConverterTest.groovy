/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.javafx.util

import org.junit.jupiter.api.Test

class ToStringOnlyStringConverterTest {
    @Test
    void smokeTests() {
        // given:
        ToStringOnlyStringConverter<Integer> converter = new ToStringOnlyStringConverter<>()

        // expect:
        assert converter.toString(42) == '42'

        // given:
        converter = new ToStringOnlyStringConverter<>({ i -> (i * 2).toString() })

        // expect:
        assert converter.toString(21) == '42'
    }
}
