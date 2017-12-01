/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package griffon.exceptions

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class FieldExceptionSpec extends Specification {
    void 'Exception should have proper formatted message'() {
        setup:
        FieldException e = value ? new FieldException(receiver, fieldName, value) : new FieldException(receiver, fieldName)

        expect:
        e.message.contains(message)

        where:
        receiver   | fieldName | value | message
        new Bean() | 'foo'     | null  | 'Cannot get field foo from bean'
        Bean       | 'foo'     | null  | 'class griffon.exceptions.FieldExceptionSpec$Bean does not have a field named foo'
        new Bean() | 'foo'     | 'foo' | 'Cannot set field foo with value foo on bean'
    }

    private static class Bean {
        String toString() { 'bean' }
    }
}
