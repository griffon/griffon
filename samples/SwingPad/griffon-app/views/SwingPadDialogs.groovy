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

import java.awt.Insets
import ca.odell.glazedlists.*
import ca.odell.glazedlists.gui.*
import ca.odell.glazedlists.swing.*

dialog(title: 'Groovy executing', id: 'runWaitDialog', modal: true,
       iconImage: imageIcon("/groovy/ui/ConsoleIcon.png").image) {
   vbox(border: emptyBorder(6)) {
       label(text: "Groovy is now executing. Please wait.", alignmentX: 0.5f)
       vstrut()
       button( interruptAction, margin: new Insets(10, 20, 10, 20),
               alignmentX: 0.5f)
    }
}

textMatcherEditor = new TextComponentMatcherEditor(
   textField(id: "filter"),
   {List baseList, info ->
      baseList << info.builder
      baseList << info.group
      baseList << info.node
   } as TextFilterator
)

filteredNodes = new FilterList(model.nodes, textMatcherEditor)
trackingSelectionModel = bean(new EventSelectionModel(filteredNodes),
   selectionMode: EventSelectionModel.SINGLE_SELECTION)

def createTableModel() {
   def columnNames = ["Builder", "Group", "Node"]
   new EventTableModel(filteredNodes, [
          getColumnCount: {columnNames.size()},
          getColumnName: {index -> columnNames[index]},
          getColumnValue: {object, index ->
             object."${columnNames[index].toLowerCase()}"
          }] as TableFormat)
}

dialog(title: 'Node List', id: 'nodeListDialog', modal: false, size: [460,600],
       iconImage: imageIcon("/groovy/ui/ConsoleIcon.png").image) {
    borderLayout()
    panel(constraints: NORTH) {
       borderLayout()
       label("Filter:", constraints: WEST)
       textField(filter, columns: 30, constraints: CENTER)
    }
    scrollPane(constraints: CENTER) {
       table(id: "nodesTable", model: createTableModel(),
          selectionModel: trackingSelectionModel)
    }
    new TableComparatorChooser(nodesTable,
        model.nodes, AbstractTableComparatorChooser.SINGLE_COLUMN)
}
