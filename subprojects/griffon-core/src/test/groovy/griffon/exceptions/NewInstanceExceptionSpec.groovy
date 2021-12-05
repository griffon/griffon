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
package griffon.exceptions

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class NewInstanceExceptionSpec extends Specification {
    @Shared private static Exception cause = new Exception()

    void 'Exception should have proper formatted message'() {
        setup:
        NewInstanceException e = new NewInstanceException(*args)

        expect:
        e.message.contains(message)

        where:
        args            | message
        [String]        | 'Cannot create a new instance of type java.lang.String'
        [String, cause] | 'Cannot create a new instance of type java.lang.String'
    }
}
