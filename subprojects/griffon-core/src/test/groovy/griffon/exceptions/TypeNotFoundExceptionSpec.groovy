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

import java.lang.annotation.Annotation

import static griffon.util.AnnotationUtils.named

@Unroll
class TypeNotFoundExceptionSpec extends Specification {
    @Shared private Exception cause = new Exception()
    @Shared private Annotation qualifier = named('foo')

    void 'Exception should have proper formatted message'() {
        setup:
        TypeNotFoundException e = new TypeNotFoundException(*args)

        expect:
        e.message.contains(message)

        where:
        args                            | message
        [Object.name]                   | 'Could not find type ' + Object.name
        [Object.name, cause]            | 'Could not find type ' + Object.name
        [Object.name, qualifier, cause] | 'Could not find type ' + Object.name + ' with qualifier ' + qualifier
    }
}
