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
package sample.pivot.java;

import com.google.inject.Inject;
import griffon.core.mvc.MVCGroupManager;
import griffon.pivot.test.GriffonPivotFuncRule;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.junit.Rule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class SampleIntegrationTest {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        System.setProperty("griffon.swing.edt.violations.check", "true");
        System.setProperty("griffon.swing.edt.hang.monitor", "true");
    }

    @Rule
    public final GriffonPivotFuncRule pivot = new GriffonPivotFuncRule();

    @Inject
    private MVCGroupManager mvcGroupManager;

    @Test
    public void typeNameAndClickButton() {
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                // given:
                pivot.find("inputField", TextInput.class).setText("Griffon");

                // when:
                pivot.find("sayHelloButton", PushButton.class).press();
            }
        });

        SampleModel model = (SampleModel) mvcGroupManager.getModels().get("sample");
        await().atMost(5, SECONDS)
            .until(fieldIn(model)
                .ofType(String.class)
                .andWithName("output"),
                notNullValue());

        // then:
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                assertEquals("Hello Griffon", pivot.find("outputField", TextInput.class).getText());
            }
        });
    }

    @Test
    public void doNotTypeNameAndClickButton() {
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                // given:
                pivot.find("inputField", TextInput.class).setText("");

                // when:
                pivot.find("sayHelloButton", PushButton.class).press();
            }
        });

        SampleModel model = (SampleModel) mvcGroupManager.getModels().get("sample");
        await().atMost(5, SECONDS)
            .until(fieldIn(model)
                .ofType(String.class)
                .andWithName("output"),
                notNullValue());

        // then:
        pivot.runInsideUISync(new Runnable() {
            @Override
            public void run() {
                assertEquals("Howdy stranger!", pivot.find("outputField", TextInput.class).getText());
            }
        });
    }
}
