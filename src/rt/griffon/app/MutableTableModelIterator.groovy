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

import javax.swing.table.DefaultTableModel

/**
 *
 * @author Andres.Almiray
 */
class MutableTableModelIterator implements Iterator {
    private final DefaultTableModel modelDelegate
    private int row = 0

    MutableTableModelIterator(DefaultTableModel model) {
        modelDelegate = model
    }

    public boolean hasNext() {
        row > -1 && row < modelDelegate.getRowCount()
    }

    public Object next() {
        int cols = modelDelegate.getColumnCount()
        Object[] rowData = new Object[cols]
        (0..<cols).each{ int col-> rowData[col] = modelDelegate.getValueAt(row,col) }
        row++
        return rowData
    }

    public void remove() {
        if(hasNext()) modelDelegate.removeRow(row--)
    }
}