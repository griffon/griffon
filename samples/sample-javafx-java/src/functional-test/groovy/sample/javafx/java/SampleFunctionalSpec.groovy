/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package sample.javafx.java

import griffon.javafx.test.GriffonTestFXClassRule
import spock.lang.Specification
import spock.lang.Stepwise

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.LabeledMatchers.hasText

@Stepwise
class SampleFunctionalSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    private static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule('mainWindow')

    void setupSpec() {
        testfx.setup()
    }

    void cleanupSpec() {
        testfx.cleanup()
    }

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