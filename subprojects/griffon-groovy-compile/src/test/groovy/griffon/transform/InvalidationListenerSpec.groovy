/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package griffon.transform

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class InvalidationListenerSpec extends Specification {
    void "InvalidationListener AST transformation attaches a closure reference as listener (weak=#weak)"() {
        given:
        String script = """
        class Bean {
            @griffon.transform.javafx.FXObservable
            @griffon.transform.javafx.InvalidationListener(value=snoop, weak=$weak)
            String name

            int count = 0

            private snoop = { ++count }
        }
        new Bean()
        """

        when:
        def bean = new GroovyShell().evaluate(script)
        bean.name = 'griffon'

        then:
        bean.count == 1

        where:
        weak << [false, true]
    }

    void "InvalidationListener AST transformation attaches a closure as listener (weak=#weak)"() {
        given:
        String script = """
        class Bean {
            @griffon.transform.javafx.FXObservable
            @griffon.transform.javafx.InvalidationListener(value={ ++count }, weak=$weak)
            String name

            int count = 0
        }
        new Bean()
        """

        when:
        def bean = new GroovyShell().evaluate(script)
        bean.name = 'griffon'

        then:
        bean.count == 1

        where:
        weak << [false, true]
    }


    void "InvalidationListener AST transformation attaches a closure reference literal as listener (weak=#weak)"() {
        given:
        String script = """
        class Bean {
            @griffon.transform.javafx.FXObservable
            @griffon.transform.javafx.InvalidationListener(value='snoop', weak=$weak)
            String name

            int count = 0

            private snoop = { ++count }
        }
        new Bean()
        """

        when:
        def bean = new GroovyShell().evaluate(script)
        bean.name = 'griffon'

        then:
        bean.count == 1

        where:
        weak << [false, true]
    }
}
