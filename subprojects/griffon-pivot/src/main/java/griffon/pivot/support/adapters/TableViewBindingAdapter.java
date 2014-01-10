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
public class TableViewBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewBindingListener {
    private CallableWithArgs<?> tableDataKeyChanged;
    private CallableWithArgs<?> tableDataBindTypeChanged;
    private CallableWithArgs<?> tableDataBindMappingChanged;
    private CallableWithArgs<?> selectedRowKeyChanged;
    private CallableWithArgs<?> selectedRowBindTypeChanged;
    private CallableWithArgs<?> selectedRowBindMappingChanged;
    private CallableWithArgs<?> selectedRowsKeyChanged;
    private CallableWithArgs<?> selectedRowsBindTypeChanged;
    private CallableWithArgs<?> selectedRowsBindMappingChanged;

    public CallableWithArgs<?> getTableDataKeyChanged() {
        return this.tableDataKeyChanged;
    }

    public CallableWithArgs<?> getTableDataBindTypeChanged() {
        return this.tableDataBindTypeChanged;
    }

    public CallableWithArgs<?> getTableDataBindMappingChanged() {
        return this.tableDataBindMappingChanged;
    }

    public CallableWithArgs<?> getSelectedRowKeyChanged() {
        return this.selectedRowKeyChanged;
    }

    public CallableWithArgs<?> getSelectedRowBindTypeChanged() {
        return this.selectedRowBindTypeChanged;
    }

    public CallableWithArgs<?> getSelectedRowBindMappingChanged() {
        return this.selectedRowBindMappingChanged;
    }

    public CallableWithArgs<?> getSelectedRowsKeyChanged() {
        return this.selectedRowsKeyChanged;
    }

    public CallableWithArgs<?> getSelectedRowsBindTypeChanged() {
        return this.selectedRowsBindTypeChanged;
    }

    public CallableWithArgs<?> getSelectedRowsBindMappingChanged() {
        return this.selectedRowsBindMappingChanged;
    }


    public void setTableDataKeyChanged(CallableWithArgs<?> tableDataKeyChanged) {
        this.tableDataKeyChanged = tableDataKeyChanged;
    }

    public void setTableDataBindTypeChanged(CallableWithArgs<?> tableDataBindTypeChanged) {
        this.tableDataBindTypeChanged = tableDataBindTypeChanged;
    }

    public void setTableDataBindMappingChanged(CallableWithArgs<?> tableDataBindMappingChanged) {
        this.tableDataBindMappingChanged = tableDataBindMappingChanged;
    }

    public void setSelectedRowKeyChanged(CallableWithArgs<?> selectedRowKeyChanged) {
        this.selectedRowKeyChanged = selectedRowKeyChanged;
    }

    public void setSelectedRowBindTypeChanged(CallableWithArgs<?> selectedRowBindTypeChanged) {
        this.selectedRowBindTypeChanged = selectedRowBindTypeChanged;
    }

    public void setSelectedRowBindMappingChanged(CallableWithArgs<?> selectedRowBindMappingChanged) {
        this.selectedRowBindMappingChanged = selectedRowBindMappingChanged;
    }

    public void setSelectedRowsKeyChanged(CallableWithArgs<?> selectedRowsKeyChanged) {
        this.selectedRowsKeyChanged = selectedRowsKeyChanged;
    }

    public void setSelectedRowsBindTypeChanged(CallableWithArgs<?> selectedRowsBindTypeChanged) {
        this.selectedRowsBindTypeChanged = selectedRowsBindTypeChanged;
    }

    public void setSelectedRowsBindMappingChanged(CallableWithArgs<?> selectedRowsBindMappingChanged) {
        this.selectedRowsBindMappingChanged = selectedRowsBindMappingChanged;
    }


    public void tableDataKeyChanged(org.apache.pivot.wtk.TableView arg0, java.lang.String arg1) {
        if (tableDataKeyChanged != null) {
            tableDataKeyChanged.call(arg0, arg1);
        }
    }

    public void tableDataBindTypeChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (tableDataBindTypeChanged != null) {
            tableDataBindTypeChanged.call(arg0, arg1);
        }
    }

    public void tableDataBindMappingChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.TableView.TableDataBindMapping arg1) {
        if (tableDataBindMappingChanged != null) {
            tableDataBindMappingChanged.call(arg0, arg1);
        }
    }

    public void selectedRowKeyChanged(org.apache.pivot.wtk.TableView arg0, java.lang.String arg1) {
        if (selectedRowKeyChanged != null) {
            selectedRowKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedRowBindTypeChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedRowBindTypeChanged != null) {
            selectedRowBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedRowBindMappingChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.TableView.SelectedRowBindMapping arg1) {
        if (selectedRowBindMappingChanged != null) {
            selectedRowBindMappingChanged.call(arg0, arg1);
        }
    }

    public void selectedRowsKeyChanged(org.apache.pivot.wtk.TableView arg0, java.lang.String arg1) {
        if (selectedRowsKeyChanged != null) {
            selectedRowsKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedRowsBindTypeChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedRowsBindTypeChanged != null) {
            selectedRowsBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedRowsBindMappingChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.wtk.TableView.SelectedRowBindMapping arg1) {
        if (selectedRowsBindMappingChanged != null) {
            selectedRowsBindMappingChanged.call(arg0, arg1);
        }
    }

}
