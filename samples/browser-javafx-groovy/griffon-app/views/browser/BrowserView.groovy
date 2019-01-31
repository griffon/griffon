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
package browser

import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonView
import javafx.beans.value.ChangeListener
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import org.kordamp.jipsy.ServiceProviderFor

@ServiceProviderFor(GriffonView)
class BrowserView extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    BrowserModel model
    @MVCMember @Nonnull
    BrowserController controller

    void initUI() {
        builder.application(title: application.configuration['application.title'],
            width: 1024, height: 600, centerOnScreen: true) {
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

        setupListeners()

        builder.urlField.text = 'http://griffon-framework.org'
        controller.openUrl()
    }

    private void setupListeners() {
        builder.browser.engine.loadWorker.messageProperty().addListener({ observableValue, oldValue, newValue ->
            builder.model.status = newValue
            // update history and textField value if a link
            // was clicked. Sadly this check is brittle as we
            // rely on 'parsing' the message value *sigh*
            if (newValue.startsWith('Loading http')) {
                String url = newValue[8..-1]
                builder.urlField.text = url
            }
        } as ChangeListener)

        builder.browser.engine.loadWorker.progressProperty().addListener({ observableValue, oldValue, newValue ->
            if (newValue > 0.0d && newValue < 1.0d) {
                builder.model.status = "Loading: ${(newValue * 100).intValue()} %"
            }
        } as ChangeListener)
    }
}
