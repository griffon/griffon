/*
 * Copyright 2009 the original author or authors.
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
package griffon.app

import java.awt.Container
import javax.swing.ButtonGroup
import javax.swing.ListModel
import javax.swing.DefaultListModel
import javax.swing.MutableComboBoxModel
import javax.swing.JComboBox
import javax.swing.table.TableModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeNode
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JPopupMenu
import javax.swing.JTabbedPane
import javax.swing.JToolBar

/**
 *
 * @author Andres.Almiray
 */
class SwingMetaMethods {
    static void enhanceAll() {
        enhanceContainers()
        enhanceButtons()
        enhanceListModels()
        enhanceComboBoxModels()
        enhanceTableModels()
        enhanceTreeModels()
        enhanceMenus()
        enhanceTabs()
        enhanceToolBars()
    }

    static void enhanceContainers() {
       Class klass = Container
       if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getComponentCount() },
                getAt: { int i -> delegate.getComponent(i) },
                leftShift: { java.awt.Component c -> delegate.add(c) },
                iterator: {-> delegate.getComponents().iterator() },
                clear: {-> delegate.removeAll() }
            ])
        }
    }

    static void enhanceButtons() {
       Class klass = ButtonGroup
       if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getButtonCount() },
                getAt: { int i -> SwingMetaMethods.buttonGroupGetAt(delegate,i) },
                leftShift: { javax.swing.AbstractButton b -> delegate.add(b) },
                iterator: {-> delegate.getElements().iterator() }
            ])
        }
    }

    static void enhanceListModels() {
        Class klass = ListModel
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                getAt: { int i -> delegate.getElementAt(i) },
                size: {-> delegate.getSize() },
                iterator: {-> new ImmutableListModelIterator(delegate) }
            ])
        }

        klass = DefaultListModel
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                leftShift: { e -> delegate.addElement(e) },
                putAt: { int i, e -> delegate.set(i,e) },
                clear: {-> delegate.removeAllElements() },
                iterator: {-> new MutableListModelIterator(delegate) }
            ])
        }
    }

    static void enhanceComboBoxModels() {
        Class klass = JComboBox

        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                clear: {-> delegate.removeAllItems() },
                getAt: { int i -> delegate.getItemAt(i) },
                size: {-> delegate.getItemCount() },
                leftShift: { e -> delegate.addItem(e) }
            ])
        }
        klass = MutableComboBoxModel
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                leftShift: { e -> delegate.addElement(e) },
                putAt: { int i, e -> delegate.insertElementAt(e,i) },
                iterator: {-> new MutableListModelIterator(delegate) }
            ])
        }
    }

    static void enhanceTableModels() {
        Class klass = TableModel
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getRowCount() },
                getAt: { int i -> SwingMetaMethods.tableModelGetAt(delegate,i) },
                iterator: {-> new ImmutableTableModelIterator(delegate) }
            ])
        }

        klass = DefaultTableModel
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                leftShift: { row -> SwingMetaMethods.tableModelLeftShift(delegate,row) },
                putAt: { int i, row -> SwingMetaMethods.tableModelPutAt(delegate, i, row) },
                iterator: {-> new MutableTableModelIterator(delegate) }
            ])
        }

        klass = TableColumnModel
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getColumnCount() },
                getAt: { int i -> delegate.getColumn(i) },
                leftShift: { TableColumn col -> delegate.addColumn(col) },
                iterator: {-> new MutableTableColumnModelIterator(delegate) }
            ])
        }
    }

    static void enhanceTreeModels() {
        Class klass = TreePath
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getPathCount() },
                getAt: { int i -> delegate.getPath()[i] },
                iterator: {-> delegate.getPath().iterator() },
                leftShift: { o -> delegate.pathByAddingChild(o) }
            ])
        }

        klass = TreeNode
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getChildCount() },
                getAt: { int i -> delegate.getChildAt(i) },
                iterator: {-> delegate.children().iterator() }
            ])
        }

        klass = MutableTreeNode
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                putAt: { int i, MutableTreeNode n -> delegate.insert(n,i) },
                leftShift: { MutableTreeNode n -> delegate.insert(n,delegate.getChildCount()) }
            ])
        }

        klass = DefaultMutableTreeNode
        if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                leftShift: { MutableTreeNode n -> delegate.add(n) },
                clear: {-> delegate.removeAllChildren() }
            ])
        }
    }

    static void enhanceMenus() {
       Class klass = JMenu
       if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getMenuComponentCount() },
                getAt: { int i -> delegate.getMenuComponent(i) },
                leftShift: { e -> SwingMetaMethods.menuLeftShift(delegate,e) },
                iterator: {-> delegate.getMenuComponents().iterator() }
            ])
        }

       klass = JMenuBar
       if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getMenuCount() },
                getAt: { int i -> delegate.getMenu(i) },
                leftShift: { JMenu m -> delegate.add(m) },
                iterator: {-> delegate.getSubElements().iterator() }
            ])
        }

       klass = JPopupMenu
       if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                leftShift: { e -> SwingMetaMethods.menuLeftShift(delegate,e) },
                iterator: {-> delegate.getSubElements().iterator() }
            ])
        }
    }

    static void enhanceTabs() {
       Class klass = JTabbedPane
       if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                size: {-> delegate.getTabCount() },
                getAt: { int i -> delegate.getTabComponentAt(i) },
                putAt: { int i, java.awt.Component c-> delegate.setTabComponentAt(i,c) },
                clear: {-> delegate.removeAll() }
            ])
        }
    }

    static void enhanceToolBars() {
       Class klass = JToolBar
       if( !AbstractSyntheticMetaMethods.hasBeenEnhanced(klass) ) {
            AbstractSyntheticMetaMethods.enhance(klass,[
                leftShift: { javax.swing.Action a -> delegate.add(a) },
                getAt: { int i -> delegate.getComponentAtIndex(i) }
            ])
        }
    }

    private static menuLeftShift( JMenu delegate, element ) {
       if( element instanceof javax.swing.Action ||
           element instanceof javax.swing.JMenuItem ||
           element instanceof java.awt.Component ||
           element instanceof String ) {
           delegate.add(element)
       } else if( element instanceof GString ) {
          delegate.add(element.toString())
       }
    }

    private static tableModelLeftShift( DefaultTableModel delegate, row ) {
       if( !row ) {
           // adds an empty row
           delegate.addRow(null)
           return
       }
       delegate.addRow(buildRowData(delegate,row))
    }

    private static tableModelPutAt( DefaultTableModel delegate, int rowIndex, row ) {
       if( !row ) {
           // adds an empty row
           delegate.insertRow(rowIndex, null)
           return
       }
       delegate.insertRow(rowIndex, buildRowData(delegate,row))
    }

    private static buildRowData( DefaultTableModel delegate, row ) {
       // if row.size < model.size -> row will be padded with nulls
       // if row.size > model.size -> additional columns will be discared
       int cols = delegate.getColumnCount()
       Object[] rowData = new Object[cols]
       int i = 0
       row.each { if (i < cols) rowData[i++] = it }
       return rowData
    }

    private static tableModelGetAt( TableModel delegate, int rowIndex ) {
        int cols = delegate.getColumnCount()
        Object[] rowData = new Object[cols]
        (0..<cols).each{ col-> rowData[col] = delegate.getValueAt(rowIndex,col) }
        return rowData
    }

    private static buttonGroupGetAt( ButtonGroup delegate, int index ) {
        int size = delegate.getButtonCount()
        if( index < 0 || index >= size ) return null
        Enumeration buttons = delegate.getElements()
        for( int i = 0; buttons.hasMoreElements() && i < index; i++ ) {
           def b = buttons.nextElement()
           if( i == index ) return b
        }
    }
}
