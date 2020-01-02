/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonView
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView
import org.kordamp.jipsy.ServiceProviderFor

@ServiceProviderFor(GriffonView)
class SampleView extends AbstractSwingGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder                                                              //<1>
    @MVCMember @Nonnull
    SampleModel model                                                                          //<1>

    void initUI() {
        builder.with {
            application(title: application.configuration['application.title'],                 //<2>
                id: 'mainWindow', size: [320, 160],
                iconImage: imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                    imageIcon('/griffon-icon-32x32.png').image,
                    imageIcon('/griffon-icon-16x16.png').image]) {
                gridLayout(rows: 4, cols: 1)
                label(application.messageSource.getMessage('name.label'))
                textField(id: 'inputField', text: bind(target: model, 'input'))                 //<3>
                button(sayHelloAction, id: 'sayHelloButton')                                    //<4>
                label(id: 'outputLabel', text: bind { model.output })                           //<3>
            }
        }
    }
}
