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
package sample.javafx.groovy

import griffon.test.javafx.FunctionalJavaFXRunner
import griffon.test.javafx.GriffonTestFXClassRule
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith

import static org.testfx.api.FxAssert.verifyThat
import static org.testfx.matcher.control.LabeledMatchers.hasText

@RunWith(FunctionalJavaFXRunner)
class SampleFunctionalTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @ClassRule
    public static GriffonTestFXClassRule testfx = new GriffonTestFXClassRule('mainWindow')
    
    @Test
    void _01_doNotTypeNameAndClickButton() {
        // given:
        testfx.clickOn('#input').write('')

        // when:
        testfx.clickOn('#sayHelloActionTarget')

        // then:
        verifyThat('#output', hasText('Howdy stranger!'))
    }

    @Test
    void _02_typeNameAndClickButton() {
        // given:
        testfx.clickOn('#input').write('Griffon')

        // when:
        testfx.clickOn('#sayHelloActionTarget')

        // then:
        verifyThat('#output', hasText('Hello Griffon'))
    }
}