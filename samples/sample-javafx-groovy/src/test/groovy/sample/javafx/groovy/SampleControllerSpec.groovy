/*
 * Copyright 2008-2016 the original author or authors.
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

import griffon.core.artifact.ArtifactManager
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import javafx.embed.swing.JFXPanel
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS
import static org.hamcrest.Matchers.notNullValue

@TestFor(SampleController)
class SampleControllerSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
        // force initialization JavaFX Toolkit
        new JFXPanel()
    }

    @Inject
    private ArtifactManager artifactManager

    private SampleController controller

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    def 'Execute HelloAction with no input'() {
        given:
        SampleModel model = artifactManager.newInstance(SampleModel)
        controller.model = model

        when:
        controller.invokeAction('sayHello')
        await().atMost(2, SECONDS)
            .until({ model.output }, notNullValue())

        then:
        'Howdy stranger!' == model.output
    }

    def 'Execute HelloAction with input'() {
        given:
        final SampleModel model = artifactManager.newInstance(SampleModel)
        model.input = 'Griffon'
        controller.model = model

        when:
        controller.invokeAction('sayHello')
        await().atMost(2, SECONDS)
            .until({ model.output }, notNullValue())

        then:
        'Hello Griffon' == model.output
    }
}
