/*
 * Copyright 2008-2014 the original author or authors.
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
package browser

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class BrowserView {
    def builder
    def model
    def controller

    void initUI() {
        builder.application(title: application.configuration['application.title'],
            width: 800, height: 600, centerOnScreen: true) {
            scene {
                vbox {
                    hbox {
                        toolBar(hgrow: 'always') {
                            button(backAction, skipName: true)
                            button(forwardAction, skipName: true)
                            button(reloadAction, skipName: true)
                            textField(id: 'urlField', prefColumnCount: 56,
                                onAction: controller.&openUrl)
                            actions { bean(model, url: bind(urlField.textProperty())) }
                        }
                    }

                    webView(id: 'browser')

                    label(text: bind(model.statusProperty))
                }
            }
        }

        builder.urlField.text = 'http://griffon-framework.org'
        controller.openUrl()
    }
}
