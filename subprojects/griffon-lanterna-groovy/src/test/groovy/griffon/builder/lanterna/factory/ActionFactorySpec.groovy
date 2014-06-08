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
package griffon.builder.lanterna.factory

import griffon.builder.lanterna.LanternaBuilderCustomizer
import griffon.lanterna.support.LanternaAction
import griffon.util.BuilderCustomizer
import griffon.util.CompositeBuilder
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ActionFactorySpec extends Specification {
    void "Can create a LanternaAction with '#value'"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([new LanternaBuilderCustomizer()] as BuilderCustomizer[])

        when:
        builder.variables.invoked = false
        Map attrs = [id: 'someAction']
        if (name) attrs.name = name
        LanternaAction action = builder.action(attrs, value) {
            invoked = true
        }
        action.doAction()

        then:
        action != null
        builder.someAction != null
        builder.someAction == action
        action.name == name ?: 'execute'
        builder.invoked

        where:
        value                | name
        null                 | 'nulled'
        new LanternaAction() | 'lanterna'
        'execute'            | null
    }
}
