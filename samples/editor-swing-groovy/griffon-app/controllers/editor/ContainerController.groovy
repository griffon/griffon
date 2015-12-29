/*
 * Copyright 2008-2016 the original author or authors.
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

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import javax.swing.JFileChooser

@ArtifactProviderFor(GriffonController)
class ContainerController {
    def model
    def builder

    void open() {
        def window = application.windowManager.startingWindow
        def openResult = builder.fileChooserWindow.showOpenDialog(window)
        if (JFileChooser.APPROVE_OPTION == openResult) {
            File file = new File(builder.fileChooserWindow.selectedFile.toString())
            // let's calculate an unique id for the next mvc group
            String mvcIdentifier = file.name + '-' + System.currentTimeMillis()
            createMVC('editor', mvcIdentifier, [
                document: new Document(file: file, title: file.name),
                tabGroup: builder.tabGroup,
                tabName: file.name])
        }
    }

    void save() {
        resolveEditorController().saveFile()
    }

    void close() {
        resolveEditorController()?.closeFile()
    }

    void quit() {
        application.shutdown()
    }

    private GriffonController resolveEditorController() {
        application.mvcGroupManager.controllers[model.mvcIdentifier]
    }
}
