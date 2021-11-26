/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 the original author or authors.
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
package org.example;

import griffon.core.test.GriffonUnitRule;
import griffon.core.test.TestFor;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnitParamsRunner.class)
@TestFor(SampleService.class)
public class SampleServiceTest {
    static {
        // force initialization JavaFX Toolkit
        new javafx.embed.swing.JFXPanel();
    }

    private SampleService service;

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Test
    @Parameters({",Howdy stranger!",
        "Test, Hello Test"})
    public void sayHello(String input, String output) {
        assertThat(service.sayHello(input), equalTo(output));
    }
}