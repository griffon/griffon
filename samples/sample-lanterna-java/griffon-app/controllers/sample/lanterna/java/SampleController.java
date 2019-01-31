/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package sample.lanterna.java;

import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.MVCMember;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ControllerAction;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Inject;

@ServiceProviderFor(GriffonController.class)
public class SampleController extends AbstractGriffonController {
    private SampleModel model;                                          //<1>

    @Inject
    private SampleService sampleService;                                //<2>

    @MVCMember
    public void setModel(@Nonnull SampleModel model) {
        this.model = model;
    }

    @ControllerAction
    public void sayHello() {                                            //<3>
        final String result = sampleService.sayHello(model.getInput());
        executeInsideUIAsync(new Runnable() {                           //<4>
            @Override
            public void run() {
                model.setOutput(result);
            }
        });
    }
}