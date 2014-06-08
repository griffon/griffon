/*
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.TablePane;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TablePaneAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TablePaneListener {
    private CallableWithArgs<Void> cellInserted;
    private CallableWithArgs<Void> cellsRemoved;
    private CallableWithArgs<Void> cellUpdated;
    private CallableWithArgs<Void> rowHeightChanged;
    private CallableWithArgs<Void> rowHighlightedChanged;
    private CallableWithArgs<Void> columnInserted;
    private CallableWithArgs<Void> columnsRemoved;
    private CallableWithArgs<Void> columnWidthChanged;
    private CallableWithArgs<Void> columnHighlightedChanged;
    private CallableWithArgs<Void> rowsRemoved;
    private CallableWithArgs<Void> rowInserted;

    public CallableWithArgs<Void> getCellInserted() {
        return this.cellInserted;
    }

    public CallableWithArgs<Void> getCellsRemoved() {
        return this.cellsRemoved;
    }

    public CallableWithArgs<Void> getCellUpdated() {
        return this.cellUpdated;
    }

    public CallableWithArgs<Void> getRowHeightChanged() {
        return this.rowHeightChanged;
    }

    public CallableWithArgs<Void> getRowHighlightedChanged() {
        return this.rowHighlightedChanged;
    }

    public CallableWithArgs<Void> getColumnInserted() {
        return this.columnInserted;
    }

    public CallableWithArgs<Void> getColumnsRemoved() {
        return this.columnsRemoved;
    }

    public CallableWithArgs<Void> getColumnWidthChanged() {
        return this.columnWidthChanged;
    }

    public CallableWithArgs<Void> getColumnHighlightedChanged() {
        return this.columnHighlightedChanged;
    }

    public CallableWithArgs<Void> getRowsRemoved() {
        return this.rowsRemoved;
    }

    public CallableWithArgs<Void> getRowInserted() {
        return this.rowInserted;
    }


    public void setCellInserted(CallableWithArgs<Void> cellInserted) {
        this.cellInserted = cellInserted;
    }

    public void setCellsRemoved(CallableWithArgs<Void> cellsRemoved) {
        this.cellsRemoved = cellsRemoved;
    }

    public void setCellUpdated(CallableWithArgs<Void> cellUpdated) {
        this.cellUpdated = cellUpdated;
    }

    public void setRowHeightChanged(CallableWithArgs<Void> rowHeightChanged) {
        this.rowHeightChanged = rowHeightChanged;
    }

    public void setRowHighlightedChanged(CallableWithArgs<Void> rowHighlightedChanged) {
        this.rowHighlightedChanged = rowHighlightedChanged;
    }

    public void setColumnInserted(CallableWithArgs<Void> columnInserted) {
        this.columnInserted = columnInserted;
    }

    public void setColumnsRemoved(CallableWithArgs<Void> columnsRemoved) {
        this.columnsRemoved = columnsRemoved;
    }

    public void setColumnWidthChanged(CallableWithArgs<Void> columnWidthChanged) {
        this.columnWidthChanged = columnWidthChanged;
    }

    public void setColumnHighlightedChanged(CallableWithArgs<Void> columnHighlightedChanged) {
        this.columnHighlightedChanged = columnHighlightedChanged;
    }

    public void setRowsRemoved(CallableWithArgs<Void> rowsRemoved) {
        this.rowsRemoved = rowsRemoved;
    }

    public void setRowInserted(CallableWithArgs<Void> rowInserted) {
        this.rowInserted = rowInserted;
    }


    public void cellInserted(org.apache.pivot.wtk.TablePane.Row arg0, int arg1) {
        if (cellInserted != null) {
            cellInserted.call(arg0, arg1);
        }
    }

    public void cellsRemoved(org.apache.pivot.wtk.TablePane.Row arg0, int arg1, org.apache.pivot.collections.Sequence<Component> arg2) {
        if (cellsRemoved != null) {
            cellsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void cellUpdated(org.apache.pivot.wtk.TablePane.Row arg0, int arg1, org.apache.pivot.wtk.Component arg2) {
        if (cellUpdated != null) {
            cellUpdated.call(arg0, arg1, arg2);
        }
    }

    public void rowHeightChanged(org.apache.pivot.wtk.TablePane.Row arg0, int arg1, boolean arg2) {
        if (rowHeightChanged != null) {
            rowHeightChanged.call(arg0, arg1, arg2);
        }
    }

    public void rowHighlightedChanged(org.apache.pivot.wtk.TablePane.Row arg0) {
        if (rowHighlightedChanged != null) {
            rowHighlightedChanged.call(arg0);
        }
    }

    public void columnInserted(org.apache.pivot.wtk.TablePane arg0, int arg1) {
        if (columnInserted != null) {
            columnInserted.call(arg0, arg1);
        }
    }

    public void columnsRemoved(org.apache.pivot.wtk.TablePane arg0, int arg1, org.apache.pivot.collections.Sequence<TablePane.Column> arg2) {
        if (columnsRemoved != null) {
            columnsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void columnWidthChanged(org.apache.pivot.wtk.TablePane.Column arg0, int arg1, boolean arg2) {
        if (columnWidthChanged != null) {
            columnWidthChanged.call(arg0, arg1, arg2);
        }
    }

    public void columnHighlightedChanged(org.apache.pivot.wtk.TablePane.Column arg0) {
        if (columnHighlightedChanged != null) {
            columnHighlightedChanged.call(arg0);
        }
    }

    public void rowsRemoved(org.apache.pivot.wtk.TablePane arg0, int arg1, org.apache.pivot.collections.Sequence<TablePane.Row> arg2) {
        if (rowsRemoved != null) {
            rowsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void rowInserted(org.apache.pivot.wtk.TablePane arg0, int arg1) {
        if (rowInserted != null) {
            rowInserted.call(arg0, arg1);
        }
    }

}
