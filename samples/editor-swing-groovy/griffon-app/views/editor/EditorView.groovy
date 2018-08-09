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
package editor

import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonView
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView
import org.kordamp.jipsy.ServiceProviderFor

@ServiceProviderFor(GriffonView)
class EditorView extends AbstractSwingGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    EditorModel model

    void initUI() {
        builder.with {
            tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
                scrollPane(title: tabName, id: 'tab', clientProperties: [mvcIdentifier: mvcId]) {
                    textArea(id: 'editor', text: bind { model.document.contents })
                }
            }
            bean(model.document, dirty: bind { editor.text != model.document.contents })
        }
    }
}
