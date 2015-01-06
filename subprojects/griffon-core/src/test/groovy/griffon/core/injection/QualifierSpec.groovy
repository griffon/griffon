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
package griffon.core.injection

import griffon.util.AnnotationUtils
import spock.lang.Specification

import javax.inject.Named

class QualifierSpec extends Specification {
    def "Test Qualified settings"() {
        setup:
        Object o1 = new Object()
        Object o2 = new Object()
        Named q1 = AnnotationUtils.named('o1')
        Qualified qd1 = new Qualified(o1, q1)
        Qualified qd2 = new Qualified(o1, q1)
        Qualified qd3 = new Qualified(o1, AnnotationUtils.named('o2'))
        Qualified qd4 = new Qualified(o2, q1)
        Qualified qd5 = new Qualified(o2, AnnotationUtils.named('o2'))
        Qualified qd6 = new Qualified(o2, null)

        expect:
        qd1.instance == o1
        qd1.qualifier == q1

        qd1 == qd1
        qd1 == qd2
        qd1 != qd3
        qd1 != qd4
        qd1 != qd5
        qd1 != qd6
        qd1 != null
        qd1 != o1

        qd1.hashCode() == qd1.hashCode()
        qd1.hashCode() == qd2.hashCode()
        qd1.hashCode() != qd3.hashCode()
        qd1.hashCode() != qd4.hashCode()
        qd1.hashCode() != qd5.hashCode()
        qd1.hashCode() != qd6.hashCode()
    }
}
