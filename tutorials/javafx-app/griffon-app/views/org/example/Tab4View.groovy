/*
 * Copyright 2016 the original author or authors.
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
import griffon.javafx.support.fontawesome.FontAwesomeIcon
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.plugins.fontawesome.FontAwesome
import javafx.scene.control.Tab
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView

import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class Tab4View extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull FactoryBuilderSupport builder
    @MVCMember @Nonnull SampleController controller
    @MVCMember @Nonnull SampleModel model
    @MVCMember @Nonnull AppView parentView

    void initUI() {
        builder.with {
            content = builder.fxml(resource('/org/example/tab4.fxml')) {
                inputLabel.text = application.messageSource.getMessage('name.label')
                bean(input, text: bind(model.inputProperty))
                bean(output, text: bind(model.outputProperty))
            }
        }

        connectActions(builder.content, controller)

        Tab tab = new Tab('Hybrid')
        tab.graphic = new FontAwesomeIcon(FontAwesome.FA_ROCKET)
        tab.content = builder.content
        tab.closable = false
        parentView.tabPane.tabs.add(tab)
    }
}
