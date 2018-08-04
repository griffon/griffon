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
package console

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class ConsoleView extends AbstractSwingGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder                                            //<1>
    @MVCMember @Nonnull
    ConsoleModel model                                                       //<1>

    void initUI() {
        builder.with {
            actions {
                action(executeScriptAction,                                  //<2>
                    enabled: bind { model.enabled })
            }

            application(title: application.configuration['application.title'],
                pack: true, locationByPlatform: true, id: 'mainWindow',
                iconImage:   imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                             imageIcon('/griffon-icon-32x32.png').image,
                             imageIcon('/griffon-icon-16x16.png').image]) {
                panel(border: emptyBorder(6)) {
                    borderLayout()

                    scrollPane(constraints: CENTER) {
                        textArea(text: bind(target: model, 'scriptSource'),  //<3>
                            enabled: bind { model.enabled },                 //<2>
                            columns: 40, rows: 10)
                    }

                    hbox(constraints: SOUTH) {
                        button(executeScriptAction)                          //<4>
                        hstrut(5)
                        label('Result:')
                        hstrut(5)
                        textField(editable: false,
                                  text: bind { model.scriptResult })         //<5>
                    }
                }
            }
        }
    }
}
