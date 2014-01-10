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
public class ListViewBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ListViewBindingListener {
    private CallableWithArgs<?> listDataKeyChanged;
    private CallableWithArgs<?> listDataBindTypeChanged;
    private CallableWithArgs<?> listDataBindMappingChanged;
    private CallableWithArgs<?> selectedItemKeyChanged;
    private CallableWithArgs<?> selectedItemBindTypeChanged;
    private CallableWithArgs<?> selectedItemBindMappingChanged;
    private CallableWithArgs<?> selectedItemsKeyChanged;
    private CallableWithArgs<?> selectedItemsBindTypeChanged;
    private CallableWithArgs<?> selectedItemsBindMappingChanged;
    private CallableWithArgs<?> checkedItemsKeyChanged;
    private CallableWithArgs<?> checkedItemsBindTypeChanged;
    private CallableWithArgs<?> checkedItemsBindMappingChanged;

    public CallableWithArgs<?> getListDataKeyChanged() {
        return this.listDataKeyChanged;
    }

    public CallableWithArgs<?> getListDataBindTypeChanged() {
        return this.listDataBindTypeChanged;
    }

    public CallableWithArgs<?> getListDataBindMappingChanged() {
        return this.listDataBindMappingChanged;
    }

    public CallableWithArgs<?> getSelectedItemKeyChanged() {
        return this.selectedItemKeyChanged;
    }

    public CallableWithArgs<?> getSelectedItemBindTypeChanged() {
        return this.selectedItemBindTypeChanged;
    }

    public CallableWithArgs<?> getSelectedItemBindMappingChanged() {
        return this.selectedItemBindMappingChanged;
    }

    public CallableWithArgs<?> getSelectedItemsKeyChanged() {
        return this.selectedItemsKeyChanged;
    }

    public CallableWithArgs<?> getSelectedItemsBindTypeChanged() {
        return this.selectedItemsBindTypeChanged;
    }

    public CallableWithArgs<?> getSelectedItemsBindMappingChanged() {
        return this.selectedItemsBindMappingChanged;
    }

    public CallableWithArgs<?> getCheckedItemsKeyChanged() {
        return this.checkedItemsKeyChanged;
    }

    public CallableWithArgs<?> getCheckedItemsBindTypeChanged() {
        return this.checkedItemsBindTypeChanged;
    }

    public CallableWithArgs<?> getCheckedItemsBindMappingChanged() {
        return this.checkedItemsBindMappingChanged;
    }


    public void setListDataKeyChanged(CallableWithArgs<?> listDataKeyChanged) {
        this.listDataKeyChanged = listDataKeyChanged;
    }

    public void setListDataBindTypeChanged(CallableWithArgs<?> listDataBindTypeChanged) {
        this.listDataBindTypeChanged = listDataBindTypeChanged;
    }

    public void setListDataBindMappingChanged(CallableWithArgs<?> listDataBindMappingChanged) {
        this.listDataBindMappingChanged = listDataBindMappingChanged;
    }

    public void setSelectedItemKeyChanged(CallableWithArgs<?> selectedItemKeyChanged) {
        this.selectedItemKeyChanged = selectedItemKeyChanged;
    }

    public void setSelectedItemBindTypeChanged(CallableWithArgs<?> selectedItemBindTypeChanged) {
        this.selectedItemBindTypeChanged = selectedItemBindTypeChanged;
    }

    public void setSelectedItemBindMappingChanged(CallableWithArgs<?> selectedItemBindMappingChanged) {
        this.selectedItemBindMappingChanged = selectedItemBindMappingChanged;
    }

    public void setSelectedItemsKeyChanged(CallableWithArgs<?> selectedItemsKeyChanged) {
        this.selectedItemsKeyChanged = selectedItemsKeyChanged;
    }

    public void setSelectedItemsBindTypeChanged(CallableWithArgs<?> selectedItemsBindTypeChanged) {
        this.selectedItemsBindTypeChanged = selectedItemsBindTypeChanged;
    }

    public void setSelectedItemsBindMappingChanged(CallableWithArgs<?> selectedItemsBindMappingChanged) {
        this.selectedItemsBindMappingChanged = selectedItemsBindMappingChanged;
    }

    public void setCheckedItemsKeyChanged(CallableWithArgs<?> checkedItemsKeyChanged) {
        this.checkedItemsKeyChanged = checkedItemsKeyChanged;
    }

    public void setCheckedItemsBindTypeChanged(CallableWithArgs<?> checkedItemsBindTypeChanged) {
        this.checkedItemsBindTypeChanged = checkedItemsBindTypeChanged;
    }

    public void setCheckedItemsBindMappingChanged(CallableWithArgs<?> checkedItemsBindMappingChanged) {
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
