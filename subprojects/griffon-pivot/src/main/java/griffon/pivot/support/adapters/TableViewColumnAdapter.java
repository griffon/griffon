/*
 * Copyright 2012-2014 the original author or authors.
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

package griffon.pivot.support.adapters;

import griffon.core.CallableWithArgs;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TableViewColumnAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewColumnListener {
    private CallableWithArgs<?> columnInserted;
    private CallableWithArgs<?> columnsRemoved;
    private CallableWithArgs<?> columnWidthChanged;
    private CallableWithArgs<?> columnNameChanged;
    private CallableWithArgs<?> columnHeaderDataChanged;
    private CallableWithArgs<?> columnHeaderDataRendererChanged;
    private CallableWithArgs<?> columnWidthLimitsChanged;
    private CallableWithArgs<?> columnFilterChanged;
    private CallableWithArgs<?> columnCellRendererChanged;

    public CallableWithArgs<?> getColumnInserted() {
        return this.columnInserted;
    }

    public CallableWithArgs<?> getColumnsRemoved() {
        return this.columnsRemoved;
    }

    public CallableWithArgs<?> getColumnWidthChanged() {
        return this.columnWidthChanged;
    }

    public CallableWithArgs<?> getColumnNameChanged() {
        return this.columnNameChanged;
    }

    public CallableWithArgs<?> getColumnHeaderDataChanged() {
        return this.columnHeaderDataChanged;
    }

    public CallableWithArgs<?> getColumnHeaderDataRendererChanged() {
        return this.columnHeaderDataRendererChanged;
    }

    public CallableWithArgs<?> getColumnWidthLimitsChanged() {
        return this.columnWidthLimitsChanged;
    }

    public CallableWithArgs<?> getColumnFilterChanged() {
        return this.columnFilterChanged;
    }

    public CallableWithArgs<?> getColumnCellRendererChanged() {
        return this.columnCellRendererChanged;
    }


    public void setColumnInserted(CallableWithArgs<?> columnInserted) {
        this.columnInserted = columnInserted;
    }

    public void setColumnsRemoved(CallableWithArgs<?> columnsRemoved) {
        this.columnsRemoved = columnsRemoved;
    }

    public void setColumnWidthChanged(CallableWithArgs<?> columnWidthChanged) {
        this.columnWidthChanged = columnWidthChanged;
    }

    public void setColumnNameChanged(CallableWithArgs<?> columnNameChanged) {
        this.columnNameChanged = columnNameChanged;
    }

    public void setColumnHeaderDataChanged(CallableWithArgs<?> columnHeaderDataChanged) {
        this.columnHeaderDataChanged = columnHeaderDataChanged;
    }

    public void setColumnHeaderDataRendererChanged(CallableWithArgs<?> columnHeaderDataRendererChanged) {
        this.columnHeaderDataRendererChanged = columnHeaderDataRendererChanged;
    }

    public void setColumnWidthLimitsChanged(CallableWithArgs<?> columnWidthLimitsChanged) {
        this.columnWidthLimitsChanged = columnWidthLimitsChanged;
    }

    public void setColumnFilterChanged(CallableWithArgs<?> columnFilterChanged) {
        this.columnFilterChanged = columnFilterChanged;
    }

    public void setColumnCellRendererChanged(CallableWithArgs<?> columnCellRendererChanged) {
        this.columnCellRendererChanged = columnCellRendererChanged;
    }


    public void columnInserted(org.apache.pivot.wtk.TableView arg0, int arg1) {
        if (columnInserted != null) {
            columnInserted.call(arg0, arg1);
        }
    }

    public void columnsRemoved(org.apache.pivot.wtk.TableView arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (columnsRemoved != null) {
            columnsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void columnWidthChanged(org.apache.pivot.wtk.TableView.Column arg0, int arg1, boolean arg2) {
        if (columnWidthChanged != null) {
            columnWidthChanged.call(arg0, arg1, arg2);
        }
    }

    public void columnNameChanged(org.apache.pivot.wtk.TableView.Column arg0, java.lang.String arg1) {
        if (columnNameChanged != null) {
            columnNameChanged.call(arg0, arg1);
        }
    }

    public void columnHeaderDataChanged(org.apache.pivot.wtk.TableView.Column arg0, java.lang.Object arg1) {
        if (columnHeaderDataChanged != null) {
            columnHeaderDataChanged.call(arg0, arg1);
        }
    }

    public void columnHeaderDataRendererChanged(org.apache.pivot.wtk.TableView.Column arg0, org.apache.pivot.wtk.TableView.HeaderDataRenderer arg1) {
        if (columnHeaderDataRendererChanged != null) {
            columnHeaderDataRendererChanged.call(arg0, arg1);
        }
    }

    public void columnWidthLimitsChanged(org.apache.pivot.wtk.TableView.Column arg0, int arg1, int arg2) {
        if (columnWidthLimitsChanged != null) {
            columnWidthLimitsChanged.call(arg0, arg1, arg2);
        }
    }

    public void columnFilterChanged(org.apache.pivot.wtk.TableView.Column arg0, java.lang.Object arg1) {
        if (columnFilterChanged != null) {
            columnFilterChanged.call(arg0, arg1);
        }
    }

    public void columnCellRendererChanged(org.apache.pivot.wtk.TableView.Column arg0, org.apache.pivot.wtk.TableView.CellRenderer arg1) {
        if (columnCellRendererChanged != null) {
            columnCellRendererChanged.call(arg0, arg1);
        }
    }

}
