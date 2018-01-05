/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.builder.javafx.action

import griffon.core.GriffonApplication
import griffon.core.mvc.MVCGroupManager
import griffon.core.test.GriffonUnitRule
import javafx.embed.swing.JFXPanel
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject

class ActionSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
        new JFXPanel()
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private GriffonApplication application

    void 'Button gets configured using an Action'() {
        given:
        MVCGroupManager mvcGroupManager = application.mvcGroupManager
        FactoryBuilderSupport builder = mvcGroupManager.createBuilder(application)

        when:
        builder.noparent {
            actions {
                action(id: 'openAction',
                    name: 'Open',
                    onAction: { println 'Open' })
            }
            button(id: 'thebutton', openAction)
        }

        then:
        builder.thebutton.text == 'Open'
    }
}
