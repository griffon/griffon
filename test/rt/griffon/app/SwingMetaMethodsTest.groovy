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
 * limitations under the License.
 */
package griffon.app

import javax.swing.*
import javax.swing.table.*
import javax.swing.tree.*

/**
 *
 * @author Andres.Almiray
 */
class SwingMetaMethodsTest extends GroovySwingTestCase {
    void testListModel() {
      testInEDT {
        SwingMetaMethods.enhanceListModels()

        def list = [1,2,3,4,5]
        def model = [
           getSize: {-> list.size() },
           getElementAt: { int i -> list[i] }
        ] as AbstractListModel

        assert model.size() == list.size()
        assert model[2] == list[2]
        assert [2,4,6,8,10] == model.collect([]){ it * 2 }
      }
    }

    void testDefaultListModel() {
      testInEDT {
        SwingMetaMethods.enhanceListModels()

        def list = [1,2,3,4,5]
        def model = new DefaultListModel()
        list.each{ model << it }

        assert model.size() == list.size()
        assert model[2] == list[2]
        assert [2,4,6,8,10] == model.collect([]){ it * 2 }
        model[2] = 42
        assert 42 == model[2]
      }
    }

    void testMutableComboModel() {
      testInEDT {
        SwingMetaMethods.enhanceComboBoxModels()

        def list = [1,2,3,4,5]
        def model = new DefaultComboBoxModel()
        list.each{ model << it }

        assert model.size() == list.size()
        assert model[2] == list[2]
        assert [2,4,6,8,10] == model.collect([]){ it * 2 }
        model[2] = 42
        assert model.size() == 6
        assert 42 == model[2]
      }
    }

    void testTableModel() {
      testInEDT {
        SwingMetaMethods.enhanceTableModels()

        def data = [
           [1,11,111,1111],
           [2,22,222,2222],
           [3,33,333,3333]
        ]
        def model = [
           getColumnCount: {-> 4 },
           getRowCount: {-> 3 },
           getValueAt: { int r, int c -> data[r][c] }
        ] as AbstractTableModel

        assert model.size() == 3
        assert model[1] == [2,22,222,2222]
        assert [1,2,3] == model.collect([]) { row -> row[0] }
      }
    }

    void testDefaultTableModel() {
      testInEDT {
        SwingMetaMethods.enhanceTableModels()

        def model = new DefaultTableModel(0i,4i)
        assert model.size() == 0

        model << null
        model << [1]
        model << [2,22]
        model << [3,33,333]
        model << [4,44,444,4444]
        model << [5,55,555,5555,5555]

        assert model.size() == 6
        assert model[0] == [null,null,null,null]
        assert model[1] == [1,null,null,null]
        assert model[2] == [2,22,null,null]
        assert model[3] == [3,33,333,null]
        assert model[4] == [4,44,444,4444]
        assert model[5] == [5,55,555,5555]

        model[2] = [9,9,9,9]
        assert model.size() == 7
        assert model[2] == [9,9,9,9]
        assert model[3] == [2,22,null,null]
      }
    }

    void testDefaultTableColumnModel() {
      testInEDT {
        SwingMetaMethods.enhanceTableModels()

        def model = new DefaultTableColumnModel()
        assert model.size() == 0

        model << new TableColumn(2)
        model << new TableColumn(1)
        model << new TableColumn(0)
        assert model.size() == 3

        assert 2 == model[0].modelIndex
        assert [2,1,0] == model*.getModelIndex()
      }
    }

    void testDefaultMutableTreeNode() {
      testInEDT {
        SwingMetaMethods.enhanceTreeModels()

        def root = new DefaultMutableTreeNode("root")
        assert 0 == root.size()
        root << new DefaultMutableTreeNode("one")
        root << new DefaultMutableTreeNode("two")
        root << new DefaultMutableTreeNode("three")

        assert 3 == root.size()
        assert ["one","two","three"] == root*.getUserObject()

        root[2] << new DefaultMutableTreeNode("A")
        root[2] << new DefaultMutableTreeNode("B")
        assert root[2].size() == 2
        assert ["A","B"] == root[2]*.getUserObject()
        root[2][1] = new DefaultMutableTreeNode("C")
        assert root[2].size() == 3
        assert ["A","C","B"] == root[2]*.getUserObject()
        root[2].clear()
        assert 0 == root[2].size()
      }
    }

    void testTreePath() {
      testInEDT {
        SwingMetaMethods.enhanceTreeModels()

        def path = new TreePath(["A","B"] as Object[])
        assert path.size() == 2
        assert "A" == path[0]
        assert ["A","B"] == path.collect([]){it}

        path = path << "C"
        assert path.size() == 3
        assert "C" == path[2]
        assert ["A","B","C"] == path.collect([]){it}
      }
    }
}