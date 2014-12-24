/*
 * Copyright 2008-2015 the original author or authors.
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

@ArtifactProviderFor(GriffonController)
class EditorController {
    def model
    def builder

    void mvcGroupInit(Map<String, Object> args) {
        model.document = args.document
        runOutsideUI {
            // load the file's text, outside the EDT
            String text = model.document.file.text
            // update the model inside the EDT
            runInsideUIAsync { model.document.contents = text }
        }
    }

    void saveFile() {
        // write text to file, outside the EDT
        model.document.file.text = builder.editor.text
        // update model.text, inside EDT
        runInsideUIAsync { model.document.contents = builder.editor.text }
    }

    void closeFile() {
        // remove tab
        builder.tabGroup.remove builder.tab
        // cleanup
        destroyMVCGroup model.mvcIdentifier
    }
}
