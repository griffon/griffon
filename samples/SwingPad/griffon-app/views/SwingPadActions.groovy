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

import static griffon.util.GriffonApplicationUtils.*

import groovy.ui.Console
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

actions {
   action( id: 'newAction',
      name: 'New',
      closure: controller.&newScript,
      mnemonic: 'N',
      accelerator: shortcut('N'),
      smallIcon: imageIcon(resource:"icons/page.png", class: Console),
      shortDescription: 'New Swing script'
   )
   action( id: 'openAction',
      name: 'Open...',
      closure: controller.open,
      mnemonic: 'O',
      accelerator: shortcut('O'),
      smallIcon: imageIcon(resource:"icons/folder_page.png", class: Console),
      shortDescription: 'Open a Swing script'
   )
   action( id: 'exitAction',
      name: isMacOSX ? 'Quit' : 'Exit',
      closure: controller.exit,
      mnemonic: isMacOSX ? 'Q' : 'X',
      accelerator: shortcut(isMacOSX ? 'Q' : 'X'),
   )
   action( id: 'aboutAction',
      name: 'About',
      closure: controller.about,
      mnemonic: 'B',
      accelerator: shortcut('B')
   )

   action( id: 'saveAction',
      name: 'Save',
      enabled: bind {model.dirty},
      closure: controller.save,
      mnemonic: 'S',
      accelerator: shortcut('S'),
      smallIcon: imageIcon(resource:"icons/disk.png", class: Console),
      shortDescription: 'Save Swing script'
   )
   action( id: 'saveAsAction',
      name: 'Save as...',
      enabled: bind {model.dirty},
      closure: controller.saveAs
   )

   action(id: 'undoAction',
      name: 'Undo',
      mnemonic: 'U',
      accelerator: shortcut('Z'),
      smallIcon: imageIcon(resource:"icons/arrow_undo.png", class: Console),
      shortDescription: 'Undo'
   )
   action(id: 'redoAction',
      name: 'Redo',
      mnemonic: 'R',
      accelerator: shortcut('shift Y'),
      smallIcon: imageIcon(resource:"icons/arrow_redo.png", class: Console),
      shortDescription: 'Redo'
   )
   action(id: 'findAction',
      name: 'Find...',
      closure: controller.find,
      mnemonic: 'F',
      accelerator: shortcut('F'),
      smallIcon: imageIcon(resource:"icons/find.png", class: Console),
      shortDescription: 'Find'
   )
   action(id: 'findNextAction',
      name: 'Find Next',
      closure: controller.findNext,
      mnemonic: 'N',
      accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0)
   )
   action(id: 'findPreviousAction',
      name: 'Find Previous',
      closure: controller.findPrevious,
      mnemonic: 'V',
      accelerator: KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK)
   )
   action(id: 'replaceAction',
      name: 'Replace...',
      closure: controller.replace,
      mnemonic: 'E',
      accelerator: shortcut('H'),
      smallIcon: imageIcon(resource:"icons/text_replace.png", class: Console),
      shortDescription: 'Replace'
   )
   action(id: 'cutAction',
      name: 'Cut',
      closure: controller.cut,
      mnemonic: 'T',
      accelerator: shortcut('X'),
      smallIcon: imageIcon(resource:"icons/cut.png", class: Console),
      shortDescription: 'Cut'
   )
   action(id: 'copyAction',
      name: 'Copy',
      closure: controller.copy,
      mnemonic: 'C',
      accelerator: shortcut('C'),
      smallIcon: imageIcon(resource:"icons/page_copy.png", class: Console),
      shortDescription: 'Copy'
   )
   action(id: 'pasteAction',
      name: 'Paste',
      closure: controller.paste,
      mnemonic: 'P',
      accelerator: shortcut('V'),
      smallIcon: imageIcon(resource:"icons/page_paste.png", class: Console),
      shortDescription: 'Paste'
   )
   action(id: 'selectAllAction',
      name: 'Select All',
      closure: controller.selectAll,
      mnemonic: 'A',
      accelerator: shortcut('A')
   )

   action(id: 'largerFontAction',
      name: 'Larger Font',
      closure: controller.largerFont,
      mnemonic: 'L',
      accelerator: shortcut('shift L')
   )
   action(id: 'smallerFontAction',
      name: 'Smaller Font',
      closure: controller.smallerFont,
      mnemonic: 'S',
      accelerator: shortcut('shift S')
   )
   action(id: 'packComponentsAction',
      name: 'Pack',
      closure: controller.packComponents,
      mnemonic: 'K',
      accelerator: shortcut('shift P')
   )

   action(id: 'runAction',
      name: 'Run',
      enabled: bind {model.dirty},
      closure: controller.runScript,
      mnemonic: 'R',
      keyStroke: shortcut('ENTER'),
      accelerator: shortcut('R'),
      smallIcon: imageIcon(resource:"icons/script_go.png", class: Console),
      shortDescription: 'Execute Groovy Script'
   )

   action(id: 'interruptAction',
      name: 'Interrupt',
      closure: controller.confirmRunInterrupt
   )

   action( id: 'addClasspathJarAction',
      name: 'Add Jar to ClassPath',
      closure: controller.addClasspathJar,
      mnemonic: 'J',
   )

   action( id: 'addClasspathDirAction',
      name: 'Add Directory to ClassPath',
      closure: controller.addClasspathDir,
      mnemonic: 'D',
   )

   action(id: 'snapshotAction',
      name: 'Snapshot',
      closure: controller.snapshot,
      mnemonic: 'T',
      accelerator: shortcut('T'),
      smallIcon: imageIcon(resource:"icons/camera.png", class: SwingPadActions),
      shortDescription: 'Take a snapshot'
   )
}
