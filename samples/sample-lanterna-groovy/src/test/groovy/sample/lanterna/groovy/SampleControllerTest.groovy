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
package sample.lanterna.groovy

import griffon.core.artifact.ArtifactManager
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import griffon.core.threading.UIThreadManager
import griffon.inject.BindTo
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject

import static java.util.concurrent.TimeUnit.SECONDS
import static org.awaitility.Awaitility.await
import static org.hamcrest.Matchers.notNullValue

@TestFor(SampleController)
class SampleControllerTest {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Inject
    private ArtifactManager artifactManager

    private SampleController controller

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Test
    void executeSayHelloActionWithNoInput() {
        TestBuilder builder = new TestBuilder()

        controller.builder = builder
        controller.invokeAction('sayHello')

        await().atMost(2, SECONDS)
            .until({ builder.output.text }, notNullValue())
        assert 'Howdy stranger!' == builder.output.text
    }

    @Test
    void executeSayHelloActionWithInput() {
        TestBuilder builder = new TestBuilder()
        builder.input.text = 'Griffon'

        controller.builder = builder
        controller.invokeAction('sayHello')

        await().atMost(2, SECONDS)
            .until({ builder.output.text }, notNullValue())
        assert 'Hello Griffon' == builder.output.text
    }

    @javax.inject.Singleton
    @BindTo(UIThreadManager.class)
    private DefaultUIThreadManager uiThreadManager
}

class TestBuilder extends FactoryBuilderSupport {
    TestBuilder() {
        this.getVariables().putAll(
            input: [:],
            output: [:]
        )
    }
}
