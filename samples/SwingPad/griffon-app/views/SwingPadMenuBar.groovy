/*
 * Copyright 2008 the original author or authors.
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
import javax.swing.event.PopupMenuListener

def makeSampleScriptAction = { id, name ->
   actions {
      action( id: "${id}Action",
         name: name,
         closure: { model.currentSample = id; controller.runSampleScript(it) },
         smallIcon: imageIcon(resource:"icons/script_gear.png", class: SwingPadActions)
      )
   }
   doOutside {
      model.samples[id] = Thread.currentThread().contextClassLoader.
                   getResourceAsStream("samples/${id}.txt").text
   }
   return this."${id}Action"
}

menuBar = menuBar {
   menu(text: 'File', mnemonic: 'F') {
       menuItem(newAction)
       menuItem(openAction)
       menu("Open Recent...",
         id: "openRecentMenu",
         mnemonic: 'E')
       separator()
       menuItem(saveAction)
       menuItem(saveAsAction)
       if( !isMacOSX ) {
           separator()
           menuItem(exitAction)
       }
   }

   menu(text: 'Edit', mnemonic: 'E') {
       menuItem(undoAction)
       menuItem(redoAction)
       separator()
       menuItem(cutAction)
       menuItem(copyAction)
       menuItem(pasteAction)
       separator()
       menuItem(selectAllAction)
       separator()
       menuItem(findAction)
       menuItem(findNextAction)
       menuItem(findPreviousAction)
       menuItem(replaceAction)
   }

   menu(text: 'View', mnemonic: 'V') {
       menuItem(largerFontAction)
       menuItem(smallerFontAction)
       separator()
       checkBoxMenuItem(packComponentsAction, selected: true)
       checkBoxMenuItem(showRulersAction, selected: true)
       checkBoxMenuItem(showToolbarAction, selected: controller.showToolbar)
       separator()
       menuItem(toggleLayoutAction)
       menuItem(snapshotAction)
   }

   menu(text: 'Builder', mnemonic: 'B') {
       checkBoxMenuItem(flamingoAction,
                        id: "flamingoMenu",
                        selected: controller.toggleFlamingoBuilder )
       checkBoxMenuItem(trayAction,
                        id: "trayMenu",
                        selected: controller.toggleTrayBuilder)
       checkBoxMenuItem(macwidgetsAction,
                        id: "macwidgetsMenu",
                        selected: controller.toggleMacwidgetsBuilder)
       checkBoxMenuItem(swingxtrasAction,
                        id: "swingxtrasMenu",
                        selected: controller.toggleSwingxtrasBuilder)
       flamingoMenu.selected = false
       trayMenu.selected = false
       macwidgetsMenu.selected = false
       swingxtrasMenu.selected = false
   }

   menu(text: 'Script', mnemonic: 'S') {
       menuItem(runAction)
       separator()
       menuItem(addClasspathJarAction)
       menuItem(addClasspathDirAction)
       separator()
       menu("Samples") {
          [ jide1: "Jide - Flair",
            jide2: "Jide - MeterProgressBar",
            swingx1: "SwingX - Flair",
            tray1: "Tray - Flair",
            flamingo1: "Flamingo - FlexiSlider",
            macwidgets1: "MacWidgets - Flair",
            swingxtras1: "Swingxtras - Flair",
            trident1: "Trident - ButtonFG",
            trident2: "Trident - Snake"].each { id, name ->
             menuItem(makeSampleScriptAction(id,name))
          }
       }
   }

   //if( !isMacOSX ) {
       glue()
       menu(text: 'Help', mnemonic: 'H') {
           menuItem(aboutAction)
           separator()
           menuItem(nodesAction)
       }
   //}
}

openRecentMenu.popupMenu.addPopupMenuListener([
   popupMenuWillBecomeVisible: {
     openRecentMenu.removeAll()
     model.recentScripts.eachWithIndex { file, int i ->
        openRecentMenu.add(action(
           name: "${i+1}. ${file.name}".toString(),
           mnemonic: 1+i,
           closure: { controller.openFile(file) }
        ))
     }
     if( model.recentScripts.size() ) {
        openRecentMenu.add(separator())
        openRecentMenu.add(clearRecentScriptsAction)
     }
   },
   popupMenuWillBecomeInvisible: {/*empty*/},
   popupMenuCanceled: {/*empty*/}
] as PopupMenuListener)

return menuBar