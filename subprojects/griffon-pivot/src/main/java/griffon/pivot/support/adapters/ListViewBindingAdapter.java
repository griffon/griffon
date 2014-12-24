/*
 * Copyright 2008-2015 the original author or authors.
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
public class ListViewBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ListViewBindingListener {
    private CallableWithArgs<Void> listDataKeyChanged;
    private CallableWithArgs<Void> listDataBindTypeChanged;
    private CallableWithArgs<Void> listDataBindMappingChanged;
    private CallableWithArgs<Void> selectedItemKeyChanged;
    private CallableWithArgs<Void> selectedItemBindTypeChanged;
    private CallableWithArgs<Void> selectedItemBindMappingChanged;
    private CallableWithArgs<Void> selectedItemsKeyChanged;
    private CallableWithArgs<Void> selectedItemsBindTypeChanged;
    private CallableWithArgs<Void> selectedItemsBindMappingChanged;
    private CallableWithArgs<Void> checkedItemsKeyChanged;
    private CallableWithArgs<Void> checkedItemsBindTypeChanged;
    private CallableWithArgs<Void> checkedItemsBindMappingChanged;

    public CallableWithArgs<Void> getListDataKeyChanged() {
        return this.listDataKeyChanged;
    }

    public CallableWithArgs<Void> getListDataBindTypeChanged() {
        return this.listDataBindTypeChanged;
    }

    public CallableWithArgs<Void> getListDataBindMappingChanged() {
        return this.listDataBindMappingChanged;
    }

    public CallableWithArgs<Void> getSelectedItemKeyChanged() {
        return this.selectedItemKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedItemBindTypeChanged() {
        return this.selectedItemBindTypeChanged;
    }

    public CallableWithArgs<Void> getSelectedItemBindMappingChanged() {
        return this.selectedItemBindMappingChanged;
    }

    public CallableWithArgs<Void> getSelectedItemsKeyChanged() {
        return this.selectedItemsKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedItemsBindTypeChanged() {
        return this.selectedItemsBindTypeChanged;
    }

    public CallableWithArgs<Void> getSelectedItemsBindMappingChanged() {
        return this.selectedItemsBindMappingChanged;
    }

    public CallableWithArgs<Void> getCheckedItemsKeyChanged() {
        return this.checkedItemsKeyChanged;
    }

    public CallableWithArgs<Void> getCheckedItemsBindTypeChanged() {
        return this.checkedItemsBindTypeChanged;
    }

    public CallableWithArgs<Void> getCheckedItemsBindMappingChanged() {
        return this.checkedItemsBindMappingChanged;
    }


    public void setListDataKeyChanged(CallableWithArgs<Void> listDataKeyChanged) {
        this.listDataKeyChanged = listDataKeyChanged;
    }

    public void setListDataBindTypeChanged(CallableWithArgs<Void> listDataBindTypeChanged) {
        this.listDataBindTypeChanged = listDataBindTypeChanged;
    }

    public void setListDataBindMappingChanged(CallableWithArgs<Void> listDataBindMappingChanged) {
        this.listDataBindMappingChanged = listDataBindMappingChanged;
    }

    public void setSelectedItemKeyChanged(CallableWithArgs<Void> selectedItemKeyChanged) {
        this.selectedItemKeyChanged = selectedItemKeyChanged;
    }

    public void setSelectedItemBindTypeChanged(CallableWithArgs<Void> selectedItemBindTypeChanged) {
        this.selectedItemBindTypeChanged = selectedItemBindTypeChanged;
    }

    public void setSelectedItemBindMappingChanged(CallableWithArgs<Void> selectedItemBindMappingChanged) {
        this.selectedItemBindMappingChanged = selectedItemBindMappingChanged;
    }

    public void setSelectedItemsKeyChanged(CallableWithArgs<Void> selectedItemsKeyChanged) {
        this.selectedItemsKeyChanged = selectedItemsKeyChanged;
    }

    public void setSelectedItemsBindTypeChanged(CallableWithArgs<Void> selectedItemsBindTypeChanged) {
        this.selectedItemsBindTypeChanged = selectedItemsBindTypeChanged;
    }

    public void setSelectedItemsBindMappingChanged(CallableWithArgs<Void> selectedItemsBindMappingChanged) {
        this.selectedItemsBindMappingChanged = selectedItemsBindMappingChanged;
    }

    public void setCheckedItemsKeyChanged(CallableWithArgs<Void> checkedItemsKeyChanged) {
        this.checkedItemsKeyChanged = checkedItemsKeyChanged;
    }

    public void setCheckedItemsBindTypeChanged(CallableWithArgs<Void> checkedItemsBindTypeChanged) {
        this.checkedItemsBindTypeChanged = checkedItemsBindTypeChanged;
    }

    public void setCheckedItemsBindMappingChanged(CallableWithArgs<Void> checkedItemsBindMappingChanged) {
        this.checkedItemsBindMappingChanged = checkedItemsBindMappingChanged;
    }


    public void listDataKeyChanged(org.apache.pivot.wtk.ListView arg0, java.lang.String arg1) {
        if (listDataKeyChanged != null) {
            listDataKeyChanged.call(arg0, arg1);
        }
    }

    public void listDataBindTypeChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (listDataBindTypeChanged != null) {
            listDataBindTypeChanged.call(arg0, arg1);
        }
    }

    public void listDataBindMappingChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.ListView.ListDataBindMapping arg1) {
        if (listDataBindMappingChanged != null) {
            listDataBindMappingChanged.call(arg0, arg1);
        }
    }

    public void selectedItemKeyChanged(org.apache.pivot.wtk.ListView arg0, java.lang.String arg1) {
        if (selectedItemKeyChanged != null) {
            selectedItemKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedItemBindTypeChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedItemBindTypeChanged != null) {
            selectedItemBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedItemBindMappingChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.ListView.ItemBindMapping arg1) {
        if (selectedItemBindMappingChanged != null) {
            selectedItemBindMappingChanged.call(arg0, arg1);
        }
    }

    public void selectedItemsKeyChanged(org.apache.pivot.wtk.ListView arg0, java.lang.String arg1) {
        if (selectedItemsKeyChanged != null) {
            selectedItemsKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedItemsBindTypeChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedItemsBindTypeChanged != null) {
            selectedItemsBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedItemsBindMappingChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.ListView.ItemBindMapping arg1) {
        if (selectedItemsBindMappingChanged != null) {
            selectedItemsBindMappingChanged.call(arg0, arg1);
        }
    }

    public void checkedItemsKeyChanged(org.apache.pivot.wtk.ListView arg0, java.lang.String arg1) {
        if (checkedItemsKeyChanged != null) {
            checkedItemsKeyChanged.call(arg0, arg1);
        }
    }

    public void checkedItemsBindTypeChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.BindType arg1) {
        if (checkedItemsBindTypeChanged != null) {
            checkedItemsBindTypeChanged.call(arg0, arg1);
        }
    }

    public void checkedItemsBindMappingChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.ListView.ItemBindMapping arg1) {
        if (checkedItemsBindMappingChanged != null) {
            checkedItemsBindMappingChanged.call(arg0, arg1);
        }
    }

}
