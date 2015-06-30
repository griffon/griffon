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
package griffon.exceptions

import spock.lang.Specification

class InstanceMethodInvocationExceptionSpec extends Specification {
    void 'Exception should have proper formatted message'() {
        setup:
        InstanceMethodInvocationException e = new InstanceMethodInvocationException(new TheClass(), methodName, args)

        expect:
        e.message.contains(regex)

        where:
        methodName  | args                      | regex
        'theMethod' | ['foo', 1] as Object[]    | 'TheClass.theMethod(java.lang.String,java.lang.Integer)'
        'theMethod' | ['foo', null] as Object[] | 'TheClass.theMethod(java.lang.String,java.lang.Object)'
    }

    private static class TheClass {
        public void theMethod(String arg0, Integer arg1) {
        }
    }
}
