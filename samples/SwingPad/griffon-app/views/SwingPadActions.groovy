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
      name: 'Quit',
      closure: controller.exit,
      mnemonic: 'Q',
      accelerator: shortcut('Q'),
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
      accelerator: shortcut('shift P'),
      enabled: bind { !model.dirty || model.successfulScript }
   )
   action(id: 'showRulersAction',
      name: 'Rulers',
      closure: controller.showRulers,
      accelerator: shortcut('shift R')
   )
   action(id: 'showToolbarAction',
      name: 'Show Toolbar',
      closure: controller.showToolbar,
      accelerator: shortcut('shift T')
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
   action(id: 'suggestAction',
      name: 'Code Suggest',
      enabled: bind {model.dirty},
      closure: controller.suggestNodeName,
      //mnemonic: 'G',
      accelerator: shortcut('SPACE'),
      keyStroke: shortcut('SPACE')
   )
   action(id: 'completeAction',
      closure: controller.codeComplete
   )

   action(id: 'interruptAction',
      name: 'Interrupt',
      closure: controller.confirmRunInterrupt
   )

   action( id: 'addClasspathJarAction',
      name: 'Add Jar to ClassPath',
      closure: controller.addClasspathJar,
      mnemonic: 'J',
      smallIcon: imageIcon(resource:"icons/cup_add.png", class: SwingPadActions),
   )

   action( id: 'addClasspathDirAction',
      name: 'Add Directory to ClassPath',
      closure: controller.addClasspathDir,
      mnemonic: 'D',
      smallIcon: imageIcon(resource:"icons/folder_add.png", class: SwingPadActions),
   )

   action(id: 'snapshotAction',
      name: 'Snapshot',
      closure: controller.snapshot,
      mnemonic: 'T',
      accelerator: shortcut('T'),
      smallIcon: imageIcon(resource:"icons/camera.png", class: SwingPadActions),
      shortDescription: 'Take a snapshot'
   )

   action(id: 'flamingoAction',
      name: 'Flamingo',
      mnemonic: 'F',
      closure: controller.toggleFlamingoBuilder,
      enabled: isJdk16,
      smallIcon: imageIcon(resource:"icons/plugin.png", class: SwingPadActions),
      shortDescription: isJdk16 ? "Enable FlamingoBuilder" : "Requires Jre 1.6 or above"
   )

   action(id: 'trayAction',
      name: 'Tray',
      mnemonic: 'Y',
      closure: controller.toggleTrayBuilder,
      enabled: isJdk16,
      smallIcon: imageIcon(resource:"icons/plugin.png", class: SwingPadActions),
      shortDescription: isJdk16 ? "Enable TrayBuilder" : "Requires Jre 1.6 or above"
   )

   action(id: 'macwidgetsAction',
      name: 'MacWidgets',
      mnemonic: 'M',
      closure: controller.toggleMacwidgetsBuilder,
      enabled: isJdk16,
      smallIcon: imageIcon(resource:"icons/plugin.png", class: SwingPadActions),
      shortDescription: isJdk16 ? "Enable MacWidgetsBuilder" : "Requires Jre 1.6 or above"
   )

   imageIcon(id: "verticalLayoutIcon", resource:"icons/application_tile_vertical.png", class: SwingPadActions)
   imageIcon(id: "horizontalLayoutIcon", resource:"icons/application_tile_horizontal.png", class: SwingPadActions)

   action(id: 'toggleLayoutAction',
      name: 'Toggle Layout',
      closure: controller.toggleLayout,
      smallIcon: verticalLayoutIcon,
      accelerator: shortcut('shift Y')
   )
}