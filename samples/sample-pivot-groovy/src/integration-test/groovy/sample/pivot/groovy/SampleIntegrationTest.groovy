/*
 * Copyright 2008-2015 the original author or authors.
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
package sample.pivot.groovy

import griffon.pivot.test.GriffonPivotFuncRule
import org.apache.pivot.wtk.PushButton
import org.apache.pivot.wtk.TextInput
import org.junit.Rule
import org.junit.Test

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS

class SampleIntegrationTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
        System.setProperty('griffon.swing.edt.violations.check', 'true')
        System.setProperty('griffon.swing.edt.hang.monitor', 'true')
    }

    @Rule
    public final GriffonPivotFuncRule pivot = new GriffonPivotFuncRule()

    @Test
    void typeNameAndClickButton() {
        pivot.runInsideUISync {
            // given:
            pivot.find('input', TextInput).text = 'Griffon'

            // when:
            pivot.find('sayHelloButton', PushButton).press()
        }

        await().atMost(5, SECONDS)

        // then:
        pivot.runInsideUISync {
            assert 'Hello Griffon' == pivot.find('output', TextInput).text
        }
    }

    @Test
    void doNotTypeNameAndClickButton() {
        pivot.runInsideUISync {
            // given:
            pivot.find('input', TextInput).text = ''

            // when:
            pivot.find('sayHelloButton', PushButton).press()
        }

        await().atMost(5, SECONDS)

        // then:
        pivot.runInsideUISync {
            assert 'Howdy stranger!' == pivot.find('output', TextInput).text
        }
    }
}
