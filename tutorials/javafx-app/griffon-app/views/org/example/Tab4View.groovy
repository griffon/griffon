/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 the original author or authors.
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
import griffon.javafx.beans.binding.UIThreadAwareBindings
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.StringProperty
import javafx.scene.control.Tab
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import org.kordamp.ikonli.fontawesome.FontAwesome
import org.kordamp.ikonli.javafx.FontIcon

import griffon.annotations.core.Nonnull

@ArtifactProviderFor(GriffonView)
class Tab4View extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull FactoryBuilderSupport builder
    @MVCMember @Nonnull SampleController controller
    @MVCMember @Nonnull SampleModel model
    @MVCMember @Nonnull AppView parentView

    private StringProperty uiInput
    private StringProperty uiOutput

    void initUI() {
        uiInput = UIThreadAwareBindings.uiThreadAwareStringProperty(model.inputProperty())
        uiOutput = UIThreadAwareBindings.uiThreadAwareStringProperty(model.outputProperty())

        builder.with {
            content = builder.fxml(resource('/org/example/tab4.fxml')) {
                inputLabel.text = application.messageSource.getMessage('name.label')
                bean(input, text: bind(uiInput))
                bean(output, text: bind(uiOutput))
            }
        }

        connectActions(builder.content, controller)

        Tab tab = new Tab('Hybrid')
        tab.graphic = new FontIcon(FontAwesome.ROCKET)
        tab.content = builder.content
        tab.closable = false
        parentView.tabPane.tabs.add(tab)
    }
}
