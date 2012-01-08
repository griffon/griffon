/*
 * Copyright 2007-2012 the original author or authors.
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

/**
 * @author Andres Almiray
 */

package griffon.samples.swingpad

import org.fife.ui.rtextarea.RTextArea
import java.awt.FlowLayout
import javax.swing.Action
import static griffon.util.GriffonNameUtils.isBlank

modifyFont = { sizeFilter, sizeMod ->
    def currentFont = model.font
    if(sizeFilter(currentFont.size)) return
    model.font = currentFont.deriveFont((currentFont.size + sizeMod) as float)
}

// creare an instance of RTextArea to initialize actions right away
rtextArea()

wrapAction = { int actionId, String id, Map params = [:] ->
   bean(new WrappingAction(RTextArea.getAction(actionId), [Action.ACCELERATOR_KEY]), id: id)
   noparent {
       params.each { key, value ->
           getVariable(id).putValue(key, value)
       }
   }
   getVariable(id)
}

actions {
   action(id: 'newAction',
      name: app.getMessage('application.action.New.name', 'New'),
      closure: controller.newAction,
      mnemonic: app.getMessage('application.action.New.mnemonic', 'N'),
      accelerator: shortcut(app.getMessage('application.action.New.shortcut', 'N')),
      shortDescription: app.getMessage('application.action.New.description', 'New'),
      smallIcon: silkIcon('page_white_cup')
   )
   action(id: 'openAction',
      name: app.getMessage('application.action.Open.name', 'Open...'),
      closure: controller.openAction,
      mnemonic: app.getMessage('application.action.Open.mnemonic', 'O'),
      accelerator: shortcut(app.getMessage('application.action.Open.shortcut', 'O')),
      shortDescription: app.getMessage('application.action.Open.description', 'Open'),
      smallIcon: silkIcon('folder_page')
   )
   action(id: 'quitAction',
      name: app.getMessage('application.action.Quit.name', 'Quit'),
      closure: controller.quitAction,
      mnemonic: app.getMessage('application.action.Quit.mnemonic', 'Q'),
      accelerator: shortcut(app.getMessage('application.action.Quit.shortcut', 'Q')),
      shortDescription: app.getMessage('application.action.Quit.description', 'Quit')
   )
   action(id: 'aboutAction',
      name: app.getMessage('application.action.About.name', 'About'),
      closure: controller.aboutAction,
      mnemonic: app.getMessage('application.action.About.mnemonic', 'B'),
      accelerator: shortcut(app.getMessage('application.action.About.shortcut', 'B')),
      shortDescription: app.getMessage('application.action.About.description', 'About')
   )
   action(id: 'nodeListAction',
      name: app.getMessage('application.action.NodeList.name', 'Node List'),
      closure: controller.nodeListAction,
      mnemonic: app.getMessage('application.action.NodeList.mnemonic', 'I'),
      accelerator: shortcut(app.getMessage('application.action.NodeList.shortcut', 'I')),
      shortDescription: app.getMessage('application.action.NodeList.description', 'Node list')
   )
   action(id: 'showTipsAction',
      name: app.getMessage('application.action.ShowTips.name', 'Show Tips'),
      closure: {
          jxtipOfTheDay(model: app.config.tipsModel, currentTip: 0).showDialog(app.windowManager.startingWindow, SwingPadUtils.PREFERENCES, true)
      },
      mnemonic: app.getMessage('application.action.ShowTips.mnemonic', 'T'),
      accelerator: shortcut(app.getMessage('application.action.ShowTips.shortcut', 'T')),
      shortDescription: app.getMessage('application.action.ShowTips.description', 'Show Tips')
   )
   action(id: 'preferencesAction',
      name: app.getMessage('application.action.Preferences.name', 'Preferences'),
      closure: controller.preferencesAction,
      mnemonic: app.getMessage('application.action.Preferences.mnemonic', 'E'),
      shortDescription: app.getMessage('application.action.Preferences.description', 'Preferences')
   )

   action(id: 'saveAction',
      name: app.getMessage('application.action.Save.name', 'Save'),
      closure: controller.saveAction,
      mnemonic: app.getMessage('application.action.Save.mnemonic', 'S'),
      accelerator: shortcut(app.getMessage('application.action.Save.shortcut', 'S')),
      shortDescription: app.getMessage('application.action.Save.description', 'Save'),
      smallIcon: silkIcon('disk'),
      enabled: bind {model.dirty},
   )
   action(id: 'saveAsAction',
      name: app.getMessage('application.action.SaveAs.name', 'Save as...'),
      closure: controller.saveAsAction,
      accelerator: shortcut(app.getMessage('application.action.SaveAs.shortcut', 'shift S')),
      enabled: bind {model.dirty},
   )
   action( id: 'clearRecentScriptsAction',
      name: app.getMessage('application.action.ClearRecentScripts.name', 'Clear list'),
      closure: controller.clearRecentScriptsAction,
      shortDescription: app.getMessage('application.action.ClearRecentScripts.name', 'Clears this list')
   )

   wrapAction(RTextArea.UNDO_ACTION, 'undoAction', [
      (Action.SMALL_ICON): silkIcon('arrow_undo')])
   wrapAction(RTextArea.REDO_ACTION, 'redoAction', [
      (Action.SMALL_ICON): silkIcon('arrow_redo')])
   wrapAction(RTextArea.CUT_ACTION, 'cutAction', [
      (Action.SMALL_ICON): silkIcon('cut')])
   wrapAction(RTextArea.COPY_ACTION, 'copyAction', [
      (Action.SMALL_ICON): silkIcon('page_copy')])
   wrapAction(RTextArea.PASTE_ACTION, 'pasteAction', [
      (Action.SMALL_ICON): silkIcon('page_paste')])
   wrapAction(RTextArea.DELETE_ACTION, 'deleteAction', [
      (Action.SMALL_ICON): silkIcon('delete')])
   wrapAction(RTextArea.SELECT_ALL_ACTION, 'selectAllAction')

   action(id: 'findAction',
      name: app.getMessage('application.action.Find.name', 'Find'),
      closure: controller.findAction,
      mnemonic: app.getMessage('application.action.Find.mnemonic', 'I'),
      accelerator: shortcut(app.getMessage('application.action.Find.shortcut', 'F')),
      shortDescription: app.getMessage('application.action.Find.description', 'Find')
   )
   action(id: 'findPreviousAction',
      name: app.getMessage('application.action.FindPrevious.name', 'Find Previous'),
      closure: controller.findPreviousAction,
      mnemonic: app.getMessage('application.action.FindPrevious.mnemonic', 'P'),
      accelerator: shortcut(app.getMessage('application.action.FindPrevious.shortcut', 'shift G')),
      shortDescription: app.getMessage('application.action.FindPrevious.description', 'Find previous')
   )
   action(id: 'findNextAction',
      name: app.getMessage('application.action.FindNext.name', 'Find Next'),
      closure: controller.findNextAction,
      mnemonic: app.getMessage('application.action.FindNext.mnemonic', 'N'),
      accelerator: shortcut(app.getMessage('application.action.FindNext.shortcut', 'G')),
      shortDescription: app.getMessage('application.action.FindNext.description', 'Find next')
   )
   action(id: 'replaceAction',
      name: app.getMessage('application.action.Replace.name', 'Replace'),
      closure: controller.replaceAction,
      mnemonic: app.getMessage('application.action.Replace.mnemonic', 'R'),
      accelerator: shortcut(app.getMessage('application.action.Preferences.shortcut', 'E')),
      shortDescription: app.getMessage('application.action.Replace.description', 'Replace')
   )
   action(id: 'replaceAllAction',
      name: app.getMessage('application.action.ReplaceAll.name', 'Replace All'),
      closure: controller.replaceAllAction,
      mnemonic: app.getMessage('application.action.ReplaceAll.mnemonic', 'A'),
      accelerator: shortcut(app.getMessage('application.action.FindPrevious.shortcut', 'shift E')),
      shortDescription: app.getMessage('application.action.ReplaceAll.description', 'Replace all')
   )

   action( id: 'addJarToClasspathAction',
      name: app.getMessage('application.action.AddJarToClasspath.name', 'Add JAR to ClassPath'),
      closure: controller.addJarToClasspathAction,
      mnemonic: app.getMessage('application.action.AddJarToClasspath.mnemonic', 'J'),
      accelerator: shortcut(app.getMessage('application.action.AddJarToClasspath.shortcut', 'J')),
      shortDescription: app.getMessage('application.action.AddJarToClasspath.description', 'Add JAR to classPath'),
      smallIcon: silkIcon('cup_add'),
   )

   action( id: 'addDirToClasspathAction',
      name: app.getMessage('application.action.AddDirToClasspath.name', 'Add Directory to ClassPath'),
      closure: controller.addDirToClasspathAction,
      mnemonic: app.getMessage('application.action.AddDirToClasspath.mnemonic', 'T'),
      accelerator: shortcut(app.getMessage('application.action.AddDirToClasspath.shortcut', 'D')),
      shortDescription: app.getMessage('application.action.AddDirToClasspath.description', 'Add directory to classPath'),
      smallIcon: silkIcon('folder_add')
   )

   action(id: 'largerFontAction',
      name: app.getMessage('application.action.LargerFont.name', 'Larger Font'),
      closure: { modifyFont({it > 40}, +2) },
      mnemonic: app.getMessage('application.action.LargerFont.mnemonic', 'L'),
      accelerator: shortcut(app.getMessage('application.action.LargerFont.shortcut', 'shift L')),
      shortDescription: app.getMessage('application.action.LargerFont.description', 'Larger font')
   )
   action(id: 'smallerFontAction',
      name: app.getMessage('application.action.SmallerFont.name', 'Smaller Font'),
      closure: { modifyFont({it < 5}, -2) },
      mnemonic: app.getMessage('application.action.SmallerFont.mnemonic', 'M'),
      accelerator: shortcut(app.getMessage('application.action.SmallerFont.shortcut', 'shift M')),
      shortDescription: app.getMessage('application.action.SmallerFont.description', 'Smaller font')
   )
   action(id: 'packComponentsAction',
      name: app.getMessage('application.action.PackComponents.name', 'Pack'),
      closure: { evt ->
          def newLayout = evt?.source?.state ? flowLayout(alignment: FlowLayout.LEFT, hgap: 0, vgap: 0) : borderLayout()
          if(!newLayout.class.isAssignableFrom(canvas.layout.class)) {
              canvas.layout = newLayout
              if(model.success) controller.runScriptAction()
          }
      },
      accelerator: shortcut(app.getMessage('application.action.PackComponents.shortcut', 'shift P')),
      shortDescription: app.getMessage('application.action.PackComponents.description', 'Pack')
   )
   action(id: 'showRulersAction',
      name: app.getMessage('application.action.ShowRulers.name', 'Show Rulers'),
      closure: { evt ->
          def rh = evt.source.state ? rowHeader    : emptyRowHeader
          def ch = evt.source.state ? columnHeader : emptyColumnHeader
          if(scroller.rowHeader.view != rh) {
              scroller.rowHeaderView = rh
              scroller.columnHeaderView = ch
              scroller.repaint()
          }
      },
      accelerator: shortcut(app.getMessage('application.action.ShowRulers.shortcut', 'shift R')),
      shortDescription: app.getMessage('application.action.ShowRulers.description', 'Show rulers')
   )
   action(id: 'showToolbarAction',
      name: app.getMessage('application.action.ShowToolbar.name', 'Show Toolbar'),
      closure: {toolbar.visible = it.source.selected},
      accelerator: shortcut(app.getMessage('application.action.ShowToolbar.shortcut', 'shift T')),
      shortDescription: app.getMessage('application.action.ShowToolbar.description', 'Show toolbar')
   )

   action(id: 'runScriptAction',
      name: app.getMessage('application.action.RunScript.name', 'Run Script'),
      closure: controller.runScriptAction,
      mnemonic: app.getMessage('application.action.RunScript.mnemonic', 'R'),
      accelerator: shortcut(app.getMessage('application.action.RunScript.shortcut', 'R')),
      shortDescription: app.getMessage('application.action.RunScript.description', 'Run script'),
      smallIcon: silkIcon('script_go'),
      enabled: bind('code', source: model, converter: {!isBlank(it)})
   )

   action(id: 'snapshotAction',
      name: app.getMessage('application.action.Snapshot.name', 'Take a Snapshot'),
      closure: controller.snapshotAction,
      mnemonic: app.getMessage('application.action.Snapshot.mnemonic', 'T'),
      shortDescription: app.getMessage('application.action.Snapshot.description', 'Take a snapshot'),
      smallIcon: silkIcon('camera')
   )

   silkIcon(id: 'verticalLayoutIcon', 'application_tile_vertical')
   silkIcon(id: 'horizontalLayoutIcon', 'application_tile_horizontal')

   action(id: 'toggleLayoutAction',
      name: app.getMessage('application.action.ToggleLayout.name', 'Toggle Layout'),
      closure: {
           model.layout = !model.layout
           toggleLayoutAction.putValue('SmallIcon', model.layout ? verticalLayoutIcon : horizontalLayoutIcon)
      },
      mnemonic: app.getMessage('application.action.ToggleLayout.mnemonic', 'Y'),
      accelerator: shortcut(app.getMessage('application.action.ToggleLayout.shortcut', 'shift Y')),
      shortDescription: app.getMessage('application.action.ToggleLayout.description', 'Toggle layout'),
      smallIcon: verticalLayoutIcon
   )
}
