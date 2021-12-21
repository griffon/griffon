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
package sample.javafx.java;

import griffon.test.javafx.GriffonIntegrationTestFXExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SampleIntegrationJupiterTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @RegisterExtension
    public static final GriffonIntegrationTestFXExtension TESTFX = GriffonIntegrationTestFXExtension.builder()
        .build();

    @Test
    public void typeNameAndClickButton() {
        // given:
        TESTFX.clickOn("#input").write("Griffon");

        // when:
        TESTFX.clickOn("#sayHelloActionTarget");

        // then:
        verifyThat("#output", hasText("Hello Griffon"));
    }

    @Test
    public void doNotTypeNameAndClickButton() {
        // given:
        TESTFX.clickOn("#input").write("");

        // when:
        TESTFX.clickOn("#sayHelloActionTarget");

        // then:
        verifyThat("#output", hasText("Howdy stranger!"));
    }
}