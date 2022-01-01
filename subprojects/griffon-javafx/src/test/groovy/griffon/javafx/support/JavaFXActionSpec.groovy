/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.javafx.support

import javafx.embed.swing.JFXPanel
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.input.KeyCombination
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class JavaFXActionSpec extends Specification {
    static {
        new JFXPanel()
    }

    void "Check #property property"() {
        given:
        JavaFXAction action = new JavaFXAction()

        expect:
        !action."get${property.capitalize()}"()

        when:
        action."set${property.capitalize()}"(value)

        then:
        value == action."get${property.capitalize()}"()

        where:
        property            | value
        'name'              | 'foo'
        'description'       | 'foo'
        'icon'              | 'foo'
        'enabled'           | true
        'selected'          | true
        'visible'           | true
        'styleClass'        | 'foo'
        'style'             | 'foo'
        'graphicStyleClass' | 'foo'
        'graphicStyle'      | 'foo'
        'graphic'           | new Label()
        'accelerator'       | KeyCombination.NO_MATCH
        'onAction'          | { e -> } as EventHandler<ActionEvent>
    }
}
