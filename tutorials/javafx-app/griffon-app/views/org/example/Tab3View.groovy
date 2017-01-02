/*
 * Copyright 2016-2017 the original author or authors.
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
package org.example

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.javafx.support.BindingUtils
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.StringProperty
import javafx.scene.control.Tab
import org.kordamp.ikonli.fontawesome.FontAwesome
import org.kordamp.ikonli.javafx.FontIcon

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class Tab3View {
    @MVCMember @Nonnull FactoryBuilderSupport builder
    @MVCMember @Nonnull SampleModel model
    @MVCMember @Nonnull AppView parentView

    private StringProperty uiInput
    private StringProperty uiOutput

    void initUI() {
        uiInput = BindingUtils.uiThreadAwareStringProperty(model.inputProperty())
        uiOutput = BindingUtils.uiThreadAwareStringProperty(model.outputProperty())

        builder.with {
            content = anchorPane {
                label(leftAnchor: 14, topAnchor: 14,
                    text: application.messageSource.getMessage('name.label'))
                textField(leftAnchor: 172, topAnchor: 11, prefWidth: 200,
                    text: bind(uiInput))
                button(leftAnchor: 172, topAnchor: 45, prefWidth: 200,
                    sayHelloAction)
                label(leftAnchor: 14, topAnchor: 80, prefWidth: 200,
                    text: bind(uiOutput))
            }
        }

        Tab tab = new Tab('GroovyFX')
        tab.graphic = new FontIcon(FontAwesome.FLASH)
        tab.content = builder.content
        tab.closable = false
        parentView.tabPane.tabs.add(tab)
    }
}
