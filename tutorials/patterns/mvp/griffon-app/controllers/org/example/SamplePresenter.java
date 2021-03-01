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
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import griffon.annotations.core.Nonnull;
import javax.inject.Inject;
import java.util.Map;

@ServiceProviderFor(GriffonController.class)
public class SamplePresenter extends AbstractGriffonController {
    private SampleModel model;
    private SampleView view;

    @Inject
    private SampleService sampleService;

    @MVCMember
    public void setModel(@Nonnull SampleModel model) {
        this.model = model;
    }

    @MVCMember
    public void setView(@Nonnull SampleView view) {
        this.view = view;
    }

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        model.outputProperty().addListener((observable, oldValue, newValue) -> {
            runInsideUIAsync(() -> view.getOutput().setText(newValue));        //<4>
        });
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    public void sayHello() {
        String input = view.getInput().getText();                              //<1>
        String output = sampleService.sayHello(input);                         //<2>
        model.setOutput(output);                                               //<3>
    }
}