/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TableViewBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewBindingListener {
    private CallableWithArgs<Void> tableDataKeyChanged;
    private CallableWithArgs<Void> tableDataBindTypeChanged;
    private CallableWithArgs<Void> tableDataBindMappingChanged;
    private CallableWithArgs<Void> selectedRowKeyChanged;
    private CallableWithArgs<Void> selectedRowBindTypeChanged;
    private CallableWithArgs<Void> selectedRowBindMappingChanged;
    private CallableWithArgs<Void> selectedRowsKeyChanged;
    private CallableWithArgs<Void> selectedRowsBindTypeChanged;
    private CallableWithArgs<Void> selectedRowsBindMappingChanged;

    public CallableWithArgs<Void> getTableDataKeyChanged() {
        return this.tableDataKeyChanged;
    }

    public CallableWithArgs<Void> getTableDataBindTypeChanged() {
        return this.tableDataBindTypeChanged;
    }

    public CallableWithArgs<Void> getTableDataBindMappingChanged() {
        return this.tableDataBindMappingChanged;
    }

    public CallableWithArgs<Void> getSelectedRowKeyChanged() {
        return this.selectedRowKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedRowBindTypeChanged() {
        return this.selectedRowBindTypeChanged;
    }

    public CallableWithArgs<Void> getSelectedRowBindMappingChanged() {
        return this.selectedRowBindMappingChanged;
    }

    public CallableWithArgs<Void> getSelectedRowsKeyChanged() {
        return this.selectedRowsKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedRowsBindTypeChanged() {
        return this.selectedRowsBindTypeChanged;
    }

    public CallableWithArgs<Void> getSelectedRowsBindMappingChanged() {
        return this.selectedRowsBindMappingChanged;
    }


    public void setTableDataKeyChanged(CallableWithArgs<Void> tableDataKeyChanged) {
        this.tableDataKeyChanged = tableDataKeyChanged;
    }

    public void setTableDataBindTypeChanged(CallableWithArgs<Void> tableDataBindTypeChanged) {
        this.tableDataBindTypeChanged = tableDataBindTypeChanged;
    }

    public void setTableDataBindMappingChanged(CallableWithArgs<Void> tableDataBindMappingChanged) {
        this.tableDataBindMappingChanged = tableDataBindMappingChanged;
    }

    public void setSelectedRowKeyChanged(CallableWithArgs<Void> selectedRowKeyChanged) {
        this.selectedRowKeyChanged = selectedRowKeyChanged;
    }

    public void setSelectedRowBindTypeChanged(CallableWithArgs<Void> selectedRowBindTypeChanged) {
        this.selectedRowBindTypeChanged = selectedRowBindTypeChanged;
    }

    public void setSelectedRowBindMappingChanged(CallableWithArgs<Void> selectedRowBindMappingChanged) {
        this.selectedRowBindMappingChanged = selectedRowBindMappingChanged;
    }

    public void setSelectedRowsKeyChanged(CallableWithArgs<Void> selectedRowsKeyChanged) {
        this.selectedRowsKeyChanged = selectedRowsKeyChanged;
    }

    public void setSelectedRowsBindTypeChanged(CallableWithArgs<Void> selectedRowsBindTypeChanged) {
        this.selectedRowsBindTypeChanged = selectedRowsBindTypeChanged;
    }

    public void setSelectedRowsBindMappingChanged(CallableWithArgs<Void> selectedRowsBindMappingChanged) {
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
