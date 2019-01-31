/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.builder.lanterna.factory

import griffon.builder.lanterna.LanternaBuilderCustomizer
import griffon.lanterna.support.LanternaAction
import griffon.util.groovy.BuilderCustomizer
import griffon.util.groovy.CompositeBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ButtonFactorySpec extends Specification {
    @Shared
    private Closure runnable = { -> }

    void "Can create a button"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([new LanternaBuilderCustomizer()] as BuilderCustomizer[])

        when:
        builder.actions {
            action(id: 'theAction', name: 'TheAction')
            if (closure) {
                button([id: 'theButton'] + attrs, value, closure)
            } else {
                button([id: 'theButton'] + attrs, value)
            }
        }

        then:
        builder.theButton != null
        builder.theButton.text == text

        where:
        text       | attrs                                            | value                          | closure
        ''         | [:]                                              | null                           | null
        'lanterna' | [:]                                              | 'lanterna'                     | null
        ''         | [:]                                              | 1                              | null
        'lanterna' | [:]                                              | new LanternaAction('lanterna') | null
        '1'        | [text: 1]                                        | null                           | null
        'lanterna' | [text: 'lanterna']                               | null                           | null
        'lanterna' | [text: 'lanterna', action: runnable]             | null                           | null
        'lanterna' | [text: 'lanterna', action: new LanternaAction()] | null                           | null
        'lanterna' | [:]                                              | 'lanterna'                     | runnable
    }
}
