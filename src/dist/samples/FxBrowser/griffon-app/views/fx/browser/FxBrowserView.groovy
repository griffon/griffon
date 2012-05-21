/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fx.browser

/**
 * @author Andres Almiray
 */

stage(title: 'Fx Browser', visible: true,
       width: 800,    height: 600,
    minWidth: 800, minHeight: 600) {
    scene {
        vbox {
            hbox {
                toolBar(hgrow: 'always') {
                    button(backAction,    skipName: true)
                    button(forwardAction, skipName: true)
                    button(reloadAction,  skipName: true)
                    textField(id: 'urlField', prefColumnCount: 56,
                            /*text: bind(target: model, targetProperty: 'url'),*/
                            onAction: controller.openUrl)
                    actions { bean(model, url: bind(view.urlField.textProperty())) }
                }
            }

            webView(id: 'browser')

            label(text: bind(model.statusProperty))
        }
    }
}
