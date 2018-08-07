/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package sample.lanterna.groovy

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonView

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class SampleView extends AbstractLanternaGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder                                                              //<1>

    void initUI() {
        builder.with {
            application(id: 'mainWindow') {                                                    //<2>
                verticalLayout()
                label(application.messageSource.getMessage('name.label'))
                textBox(id: 'input')
                button(sayHelloAction)                                                         //<3>
                label(id: 'output')
            }
        }
    }
}
