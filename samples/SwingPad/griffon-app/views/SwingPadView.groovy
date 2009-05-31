/*
 * Copyright 2007-2008 the original author or authors.
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
 */

import java.awt.GridBagConstraints
import javax.swing.SwingConstants
import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI

build(SwingPadActions)

application( title: "SwingPad", size: [800,600], locationByPlatform: true,
             iconImage: imageIcon('/griffon-icon-48x48.png').image,
             iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                          imageIcon('/griffon-icon-32x32.png').image,
                          imageIcon('/griffon-icon-16x16.png').image]) {
   menuBar( build(SwingPadMenuBar) )
   toolBar( build(SwingPadToolBar) )
   widget( build(SwingPadContentPane) )
   jxstatusBar(id: 'statusPanel') {
      gridBagLayout()
      separator(constraints:gbc(gridwidth:GridBagConstraints.REMAINDER, fill:GridBagConstraints.HORIZONTAL))
      label('Welcome to SwingPad.',
         id: 'status', text: bind { model.status },
         constraints:gbc(weightx:1.0,
               anchor:GridBagConstraints.WEST,
               fill:GridBagConstraints.HORIZONTAL,
               insets: [1,3,1,3])
      )
      separator(orientation:SwingConstants.VERTICAL, constraints:gbc(fill:GridBagConstraints.VERTICAL))
      label('1:1',
         id: 'rowNumAndColNum',
         constraints:gbc(insets: [1,3,1,3])
      )
   }
   statusPanel.putClientProperty(BasicStatusBarUI.AUTO_ADD_SEPARATOR, false)
}

build(SwingPadStyles)