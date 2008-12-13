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

import groovy.ui.ConsoleTextEditor
import java.awt.Color
import java.awt.Font
import java.awt.Toolkit

import java.awt.FlowLayout
import static java.awt.BorderLayout.*
import javax.swing.BorderFactory as BF
import javax.swing.event.CaretListener
import javax.swing.event.DocumentListener
import java.awt.event.KeyEvent
import javax.swing.KeyStroke
import javax.swing.JTabbedPane
import javax.swing.ListSelectionModel
import static javax.swing.JSplitPane.HORIZONTAL_SPLIT
import ca.odell.glazedlists.swing.EventListModel

rowHeader = new ScrollPaneRuler(ScrollPaneRuler.VERTICAL)
columnHeader = new ScrollPaneRuler(ScrollPaneRuler.HORIZONTAL)

emptyRowHeader = label("")
emptyColumnHeader = label("")

splitPane(id: 'splitPane', resizeWeight: 0.45F,
      orientation: HORIZONTAL_SPLIT ) {
   tabbedPane( id: "tabs", constraints: CENTER, border: lineBorder(color:Color.BLACK, thickness:1) ) {
      panel( title: "Source", id: "sourceTab" ) {
         borderLayout()
         container( new ConsoleTextEditor(), id: 'editor', border: emptyBorder(0),
                     font: new Font( "Monospaced", Font.PLAIN, 14 ) ){
            action(runAction)
         }
      }
      panel( title: "Errors", id: "errorsTab" ) {
         borderLayout()
         scrollPane( border: emptyBorder(0) ) {
            textArea( id: 'errors', border: emptyBorder(0),
                     background: Color.WHITE, editable: false,
                     font: new Font( "Monospaced", Font.PLAIN, 10 ),
                     caretPosition: bind(reverse:true){ model.caretPosition },
                     text: bind(reverse:true){ model.errors } )
         }
      }
   }
   scrollPane( id: "scroller", constraints: CENTER,
               border: lineBorder(color:Color.BLACK, thickness:1),
               rowHeaderView: rowHeader, columnHeaderView: columnHeader ){
      panel( id: 'canvas', border: emptyBorder(0) ) {
         //borderLayout()
         flowLayout( alignment: FlowLayout.LEFT, hgap: 0, vgap: 0 )
      }
   }
}

jidePopup( id: 'popup', movable: false, resizable: false,
   popupMenuCanceled: { evt -> editor.textEditor.requestFocus() } ) {
   panel {
      borderLayout()
      scrollPane() {
         list( id: "suggestionList", 
               selectionMode: ListSelectionModel.SINGLE_SELECTION,
               model: new EventListModel(model.suggestions) )
      }
   }
}
popup.setDefaultFocusComponent(suggestionList)
suggestionList.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "complete")
suggestionList.actionMap.put("complete", completeAction)

def toolkit = Toolkit.getDefaultToolkit()
def screen = toolkit.getScreenSize()
rowHeader.opaque = true
rowHeader.preferredSize = [20,screen.width as int]
columnHeader.opaque = true
columnHeader.preferredSize = [screen.height as int,20]

canvas.addMouseListener( rowHeader )
canvas.addMouseMotionListener( rowHeader )
canvas.addMouseListener( columnHeader )
canvas.addMouseMotionListener( columnHeader )

inputArea = editor.textEditor
rootElement = inputArea.document.defaultRootElement
// attach ctrl-enter to editor
// need to wrap in actions to keep it from being added as a component
actions {
   container(inputArea, font:new Font("Monospaced", Font.PLAIN, 12), border:emptyBorder(4)) {
      action(runAction)
   }
}

inputArea.addCaretListener({ evt ->
   def cursorPos = inputArea.caretPosition
   def rowNum = rootElement.getElementIndex(cursorPos) + 1
   def rowElement = rootElement.getElement(rowNum - 1)
   def colNum = cursorPos - rowElement.startOffset + 1
   controller.builder.rowNumAndColNum.setText("$rowNum:$colNum")
} as CaretListener)

bean( model, content: bind { inputArea.text } )
bean( model, dirty: bind { inputArea.text?.size() > 0 } )

bean( undoAction, enabled: bind { editor.undoAction.enabled } )
bean( redoAction, enabled: bind { editor.redoAction.enabled } )
inputArea.document.addDocumentListener({ model.dirty = true } as DocumentListener)

return splitPane
