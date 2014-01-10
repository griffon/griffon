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
public class TableViewAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewListener {
    private CallableWithArgs<?> selectModeChanged;
    private CallableWithArgs<?> columnSourceChanged;
    private CallableWithArgs<?> rowEditorChanged;
    private CallableWithArgs<?> tableDataChanged;
    private CallableWithArgs<?> disabledRowFilterChanged;

    public CallableWithArgs<?> getSelectModeChanged() {
        return this.selectModeChanged;
    }

    public CallableWithArgs<?> getColumnSourceChanged() {
        return this.columnSourceChanged;
    }

    public CallableWithArgs<?> getRowEditorChanged() {
        return this.rowEditorChanged;
    }

    public CallableWithArgs<?> getTableDataChanged() {
        return this.tableDataChanged;
    }

    public CallableWithArgs<?> getDisabledRowFilterChanged() {
        return this.disabledRowFilterChanged;
    }


    public void setSelectModeChanged(CallableWithArgs<?> selectModeChanged) {
        this.selectModeChanged = selectModeChanged;
    }

    public void setColumnSourceChanged(CallableWithArgs<?> columnSourceChanged) {
        this.columnSourceChanged = columnSourceChanged;
    }

    public void setRowEditorChanged(CallableWithArgs<?> rowEditorChanged) {
        this.rowEditorChanged = rowEditorChanged;
    }

    public void setTableDataChanged(CallableWithArgs<?> tableDataChanged) {
        this.tableDataChanged = tableDataChanged;
    }

    public void setDisabledRowFilterChanged(CallableWithArgs<?> disabledRowFilterChanged) {
        this.disabledRowFilterChanged = disabledRowFilterChanged;
    }


    public void selectModeChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.TableView.SelectMode arg1) {
        if (selectModeChanged != null) {
            selectModeChanged.call(arg0, arg1);
        }
    }

    public void columnSourceChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.TableView arg1) {
        if (columnSourceChanged != null) {
            columnSourceChanged.call(arg0, arg1);
        }
    }

    public void rowEditorChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.TableView.RowEditor arg1) {
        if (rowEditorChanged != null) {
            rowEditorChanged.call(arg0, arg1);
        }
    }

    public void tableDataChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.collections.List arg1) {
        if (tableDataChanged != null) {
            tableDataChanged.call(arg0, arg1);
        }
    }

    public void disabledRowFilterChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.util.Filter arg1) {
        if (disabledRowFilterChanged != null) {
            disabledRowFilterChanged.call(arg0, arg1);
        }
    }

}
