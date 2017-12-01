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
package griffon.builder.lanterna.factory

import com.googlecode.lanterna.gui.component.ActionListBox
import griffon.builder.lanterna.LanternaBuilderCustomizer
import griffon.util.BuilderCustomizer
import griffon.util.CompositeBuilder
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ActionListBoxFactorySpec extends Specification {
    void "Can create an ActionListBox"() {
        given:
        CompositeBuilder builder = new CompositeBuilder([new LanternaBuilderCustomizer()] as BuilderCustomizer[])

        when:
        ActionListBox listBox = builder.actionListBox(id: 'listBox') {
            action(id: 'someAction')
            actions()
        }

        then:
        listBox != null
        builder.listBox != null
        builder.listBox == listBox
        listBox.size == 1
        listBox.getItemAt(0).title == builder.someAction.name
    }
}
