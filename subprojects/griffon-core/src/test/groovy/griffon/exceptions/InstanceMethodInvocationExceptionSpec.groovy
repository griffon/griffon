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
package griffon.exceptions

import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

@Unroll
class InstanceMethodInvocationExceptionSpec extends Specification {
    void 'Exception should have proper formatted message (1)'() {
        setup:
        InstanceMethodInvocationException e = new InstanceMethodInvocationException(new TheClass(), methodName, args)

        expect:
        e.message.contains(message)

        where:
        methodName  | args                      | message
        'theMethod' | ['foo', 1] as Object[]    | 'TheClass.theMethod(java.lang.String,java.lang.Integer)'
        'theMethod' | ['foo', null] as Object[] | 'TheClass.theMethod(java.lang.String,java.lang.Object)'
    }

    void 'Exception should have proper formatted message (2)'() {
        setup:
        Method method = TheClass.declaredMethods.find { it.name == 'theMethod' }
        InstanceMethodInvocationException e = new InstanceMethodInvocationException(new TheClass(), method)

        expect:
        e.message.contains(message)

        where:
        message << ['TheClass.theMethod(java.lang.String,java.lang.Integer)']
    }

    private static class TheClass {
        void theMethod(String arg0, Integer arg1) {
        }
    }
}
