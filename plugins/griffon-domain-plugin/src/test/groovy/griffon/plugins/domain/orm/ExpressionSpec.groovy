/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.plugins.domain.orm

import spock.lang.Specification
import spock.lang.Unroll

import static griffon.plugins.domain.orm.Restrictions.*

@Unroll
class ExpressionSpec extends Specification {
    void "Expression '#expression1' is equal to '#expression2' but not '#expression3'"() {
        expect:
        expression1.toString() == literal
        expression1 == expression2
        expression1 != expression3
        expression1.hashCode() == expression2.hashCode()
        expression1.hashCode() != expression3.hashCode()

        where:
        literal         | expression1          | expression2          | expression3
        'a IS NULL'     | isNull('a')          | isNull('a')          | isNull('b')
        'a IS NOT NULL' | isNotNull('a')       | isNotNull('a')       | isNotNull('b')
        'a = b'         | eq('a', 'b')         | eq('a', 'b')         | eq('a', 'c')
        'a <> b'        | ne('a', 'b')         | ne('a', 'b')         | ne('a', 'c')
        'a > b'         | gt('a', 'b')         | gt('a', 'b')         | gt('a', 'c')
        'a < b'         | lt('a', 'b')         | lt('a', 'b')         | lt('a', 'c')
        'a >= b'        | ge('a', 'b')         | ge('a', 'b')         | ge('a', 'c')
        'a <= b'        | le('a', 'b')         | le('a', 'b')         | le('a', 'c')
        'a = b'         | eqProperty('a', 'b') | eqProperty('a', 'b') | eqProperty('a', 'c')
        'a = b'         | eqProperty('a', 'b') | eqProperty('b', 'a') | eqProperty('a', 'c')
        'a <> b'        | neProperty('a', 'b') | neProperty('a', 'b') | neProperty('a', 'c')
        'a <> b'        | neProperty('a', 'b') | neProperty('b', 'a') | neProperty('a', 'c')
        'a > b'         | gtProperty('a', 'b') | gtProperty('a', 'b') | gtProperty('b', 'a')
        'a < b'         | ltProperty('a', 'b') | ltProperty('a', 'b') | ltProperty('b', 'a')
        'a >= b'        | geProperty('a', 'b') | geProperty('a', 'b') | geProperty('b', 'a')
        'a <= b'        | leProperty('a', 'b') | leProperty('a', 'b') | leProperty('b', 'a')
        'a LIKE b'      | like('a', 'b')       | like('a', 'b')       | like('a', 'c')
        'a NOT LIKE b'  | notLike('a', 'b')    | notLike('a', 'b')    | notLike('a', 'c')
    }
}
