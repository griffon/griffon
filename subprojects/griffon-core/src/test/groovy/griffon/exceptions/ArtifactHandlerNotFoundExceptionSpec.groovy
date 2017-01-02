/*
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

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ArtifactHandlerNotFoundExceptionSpec extends Specification {
    @Shared private Exception cause = new Exception()

    void 'Exception should have proper formatted message'() {
        setup:
        ArtifactHandlerNotFoundException e = new ArtifactHandlerNotFoundException(*args)

        expect:
        e.message.contains(message)

        where:
        args                 | message
        [String.name]        | 'Could not find an ArtifactHandler for type java.lang.String'
        [String.name, cause] | 'Could not find an ArtifactHandler for type java.lang.String'
    }
}
