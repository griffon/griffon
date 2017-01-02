/*
 * Copyright 2016-2017 the original author or authors.
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
package org.example.calculator;

import griffon.core.artifact.GriffonController;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.opendolphin.core.client.ClientDolphin;

import javax.inject.Inject;

import static org.example.calculator.CalculatorPM.COMMAND_DIV;
import static org.example.calculator.CalculatorPM.COMMAND_MUL;
import static org.example.calculator.CalculatorPM.COMMAND_SUB;
import static org.example.calculator.CalculatorPM.COMMAND_SUM;

@ArtifactProviderFor(GriffonController.class)
public class CalculatorController extends AbstractGriffonController {
    @Inject
    private ClientDolphin clientDolphin;

    public void sum() {
        clientDolphin.send(COMMAND_SUM);
    }

    public void sub() {
        clientDolphin.send(COMMAND_SUB);
    }

    public void mul() {
        clientDolphin.send(COMMAND_MUL);
    }

    public void div() {
        clientDolphin.send(COMMAND_DIV);
    }
}