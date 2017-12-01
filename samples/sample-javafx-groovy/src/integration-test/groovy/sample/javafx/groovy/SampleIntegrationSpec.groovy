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
package sample.javafx.groovy

import griffon.javafx.test.GriffonTestFXRule
import org.junit.Rule
import spock.lang.Specification

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.LabeledMatchers.hasText

class SampleIntegrationSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public GriffonTestFXRule testfx = new GriffonTestFXRule('mainWindow')

    void 'Get default message if no input is given'() {
        given:
        testfx.clickOn('#input').write('')

        when:
        testfx.clickOn('#sayHelloActionTarget')

        then:
        verifyThat('#output', hasText('Howdy stranger!'))
    }

    void 'Get hello message if input is given'() {
        given:
        testfx.clickOn('#input').write('Griffon')

        when:
        testfx.clickOn('#sayHelloActionTarget')

        then:
        verifyThat('#output', hasText('Hello Griffon'))
    }
}