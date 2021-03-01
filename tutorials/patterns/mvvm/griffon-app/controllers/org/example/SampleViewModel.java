/*
 * Copyright 2016-2018 the original author or authors.
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

import griffon.core.artifact.GriffonController;
import griffon.annotations.inject.MVCMember;
import org.kordamp.jipsy.annotations.ServiceProviderFor;
import javax.application.threading.Threading;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import griffon.annotations.core.Nonnull;
import javax.inject.Inject;

@ServiceProviderFor(GriffonController.class)
public class SampleViewModel extends AbstractGriffonController {
    private SampleView view;
    private StringProperty input;
    private StringProperty output;

    @Nonnull
    public final StringProperty inputProperty() {
        if (input == null) {
            input = new SimpleStringProperty(this, "input");
        }
        return input;
    }

    public void setInput(String input) {
        inputProperty().set(input);
    }

    public String getInput() {
        return input == null ? null : inputProperty().get();
    }

    @Nonnull
    public final StringProperty outputProperty() {
        if (output == null) {
            output = new SimpleStringProperty(this, "output");
        }
        return output;
    }

    public void setOutput(String output) {
        outputProperty().set(output);
    }

    public String getOutput() {
        return output == null ? null : outputProperty().get();
    }

    @Inject
    private SampleService sampleService;

    @MVCMember
    public void setView(@Nonnull SampleView view) {
        this.view = view;
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    public void sayHello() {
        String input = getInput();                                             //<1>
        String output = sampleService.sayHello(input);                         //<2>
        setOutput(output);                                                     //<3>
    }
}