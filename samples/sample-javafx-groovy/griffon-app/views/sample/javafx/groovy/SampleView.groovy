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
package sample.javafx.groovy

import griffon.annotations.core.Nonnull
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class SampleView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder                                                              //<1>
    @MVCMember @Nonnull
    SampleModel model                                                                          //<1>

    void initUI() {
        builder.application(title: application.configuration['application.title'],
            name: 'mainWindow', sizeToScene: true, centerOnScreen: true) {                     //<2>
            scene(fill: WHITE, width: 400, height: 120,
                stylesheets: ['/bootstrapfx.css', '/sample/javafx/groovy/sample.css']) {
                anchorPane {
                    label(leftAnchor: 14, topAnchor: 11,
                          text: application.messageSource.getMessage('name.label'))
                    textField(leftAnchor: 172, topAnchor: 11, prefWidth: 200, id: 'input',
                              text: bind(model.inputProperty()))                               //<3>
                    button(leftAnchor: 172, topAnchor: 45, prefWidth: 200,
                           styleClass: ['btn', 'btn-primary'],
                           id: 'sayHelloActionTarget', sayHelloAction)                         //<4>
                    label(leftAnchor: 14, topAnchor: 80, prefWidth: 200, id: 'output',
                        text: bind(model.outputProperty()))                                    //<3>
                }
            }
        }
    }
}
