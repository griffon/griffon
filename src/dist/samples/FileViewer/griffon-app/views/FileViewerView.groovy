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

/**
 * @author Danno Ferrin
 */

openAction = action(closure: controller.openFile, name:"Open")

fileChooserWindow = fileChooser()
fileViewerFrame = application(title:'File Viewer',
  size:[500,300],
  locationByPlatform:true,
  iconImage: imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
    borderLayout()
	hbox(constraints:NORTH) {
        textField(columns:20, action:openAction,
            text: bind('fileName', target:model, id:'textBinding'))
        button("...", actionPerformed:controller.browse)
        button(openAction)
    }
    filesPane = tabbedPane(constraints:CENTER)
}
