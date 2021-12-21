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
package sample.swing.groovy

import griffon.annotations.core.Nonnull
import griffon.core.artifact.GriffonController
import griffon.annotations.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class SampleController {
    @MVCMember @Nonnull
    SampleModel model                                                      //<1>

    @Inject
    private SampleService sampleService                                    //<2>

    @ControllerAction
    void sayHello() {                                                      //<3>
        String result = sampleService.sayHello(model.input)
        model.output = result                                              //<4>
    }
}
