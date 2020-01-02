/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package sample.javafx.java;

import griffon.core.artifact.ArtifactManager;
import griffon.test.core.GriffonUnitExtension;
import griffon.test.core.TestFor;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(GriffonUnitExtension.class)
@TestFor(SampleController.class)
public class SampleControllerJupiterTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        // force initialization JavaFX Toolkit
        new JFXPanel();
    }

    @Inject
    private ArtifactManager artifactManager;

    private SampleController controller;

    @Test
    public void executeSayHelloActionWithNoInput() {
        // given:
        final SampleModel model = artifactManager.newInstance(SampleModel.class);
        controller.setModel(model);

        // when:
        controller.invokeAction("sayHello");
        await().atMost(2, SECONDS)
            .until(model::getOutput, notNullValue());

        // then:
        assertThat("Howdy stranger!", equalTo(model.getOutput()));
    }

    @Test
    public void executeSayHelloActionWithInput() {
        // given:
        final SampleModel model = artifactManager.newInstance(SampleModel.class);
        model.setInput("Griffon");
        controller.setModel(model);

        // when:
        controller.invokeAction("sayHello");
        await().atMost(2, SECONDS)
            .until(model::getOutput, notNullValue());

        // then:
        assertThat("Hello Griffon", equalTo(model.getOutput()));
    }
}

