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
package org.example.calculator;

import griffon.core.artifact.GriffonController;
import griffon.core.mvc.MVCGroup;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import griffon.annotations.core.Nonnull;
import java.util.Map;

@ArtifactProviderFor(GriffonController.class)
public class AppController extends AbstractGriffonController {
    private AppView view;

    @MVCMember
    public void setView(@Nonnull AppView view) {
        this.view = view;
    }

    @Override
    public void mvcGroupInit(Map<String, Object> args) {
        final MVCGroup calculator = createMVCGroup("calculator");
        runInsideUIAsync(new Runnable() {
            @Override
            public void run() {
                CalculatorView calculatorView = (CalculatorView) calculator.getView();
                view.getContainer().setCenter(calculatorView.getCalculatorNode());
            }
        });
    }
}