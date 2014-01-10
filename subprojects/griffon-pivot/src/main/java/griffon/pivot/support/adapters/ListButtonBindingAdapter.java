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
public class ListButtonBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ListButtonBindingListener {
    private CallableWithArgs<Void> listDataKeyChanged;
    private CallableWithArgs<Void> listDataBindTypeChanged;
    private CallableWithArgs<Void> listDataBindMappingChanged;
    private CallableWithArgs<Void> selectedItemKeyChanged;
    private CallableWithArgs<Void> selectedItemBindTypeChanged;
    private CallableWithArgs<Void> selectedItemBindMappingChanged;

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


    public void listDataKeyChanged(org.apache.pivot.wtk.ListButton arg0, java.lang.String arg1) {
        if (listDataKeyChanged != null) {
            listDataKeyChanged.call(arg0, arg1);
        }
    }

    public void listDataBindTypeChanged(org.apache.pivot.wtk.ListButton arg0, org.apache.pivot.wtk.BindType arg1) {
        if (listDataBindTypeChanged != null) {
            listDataBindTypeChanged.call(arg0, arg1);
        }
    }

    public void listDataBindMappingChanged(org.apache.pivot.wtk.ListButton arg0, org.apache.pivot.wtk.ListView.ListDataBindMapping arg1) {
        if (listDataBindMappingChanged != null) {
            listDataBindMappingChanged.call(arg0, arg1);
        }
    }

    public void selectedItemKeyChanged(org.apache.pivot.wtk.ListButton arg0, java.lang.String arg1) {
        if (selectedItemKeyChanged != null) {
            selectedItemKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedItemBindTypeChanged(org.apache.pivot.wtk.ListButton arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedItemBindTypeChanged != null) {
            selectedItemBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedItemBindMappingChanged(org.apache.pivot.wtk.ListButton arg0, org.apache.pivot.wtk.ListView.ItemBindMapping arg1) {
        if (selectedItemBindMappingChanged != null) {
            selectedItemBindMappingChanged.call(arg0, arg1);
        }
    }

}
