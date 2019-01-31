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
package sample.pivot.groovy

import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonView
import org.codehaus.griffon.runtime.pivot.artifact.AbstractPivotGriffonView
import org.kordamp.jipsy.ServiceProviderFor

@ServiceProviderFor(GriffonView)
class SampleView extends AbstractPivotGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder                                                              //<1>

    void initUI() {
        builder.with {
            application(title: application.configuration['application.title'],
                        id: 'mainWindow', maximized: true) {                                   //<2>
                vbox(styles: "{horizontalAlignment:'center', verticalAlignment:'center'}") {
                    label(application.messageSource.getMessage('name.label'))
                    textInput(id: 'input')
                    button(id: 'sayHelloButton', sayHelloAction)                               //<3>
                    textInput(id: 'output', editable: false)
                }
            }
        }
    }
}
