/*
 * Copyright 2008-2012 the original author or authors.
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

import javax.swing.JFileChooser

/**
 * @author Danno Ferrin
 */
class FileViewerController {
    def view
    def model

    def openFile = { evt = null ->
    	File f = new File(model.fileName)
    	// create the new MVC Group
    	buildMVCGroup('FilePanel', f.path,
    	    loadedFile: f, lastModified:f.lastModified(), fileText: f.text,
                filesPane:view.filesPane, tabName:f.name)
    }

    def browse = { evt = null ->
    	def openResult = view.fileChooserWindow.showOpenDialog(view.fileViewerFrame)
    	if (JFileChooser.APPROVE_OPTION == openResult) {
    		model.fileName = view.fileChooserWindow.selectedFile.toString()
    		view.textBinding.reverseUpdate()
    	}
    }
}
