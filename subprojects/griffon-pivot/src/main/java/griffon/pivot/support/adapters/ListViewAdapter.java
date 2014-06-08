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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ListViewAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ListViewListener {
    private CallableWithArgs<Void> listDataChanged;
    private CallableWithArgs<Void> itemRendererChanged;
    private CallableWithArgs<Void> disabledItemFilterChanged;
    private CallableWithArgs<Void> itemEditorChanged;
    private CallableWithArgs<Void> selectModeChanged;
    private CallableWithArgs<Void> checkmarksEnabledChanged;
    private CallableWithArgs<Void> disabledCheckmarkFilterChanged;

    public CallableWithArgs<Void> getListDataChanged() {
        return this.listDataChanged;
    }

    public CallableWithArgs<Void> getItemRendererChanged() {
        return this.itemRendererChanged;
    }

    public CallableWithArgs<Void> getDisabledItemFilterChanged() {
        return this.disabledItemFilterChanged;
    }

    public CallableWithArgs<Void> getItemEditorChanged() {
        return this.itemEditorChanged;
    }

    public CallableWithArgs<Void> getSelectModeChanged() {
        return this.selectModeChanged;
    }

    public CallableWithArgs<Void> getCheckmarksEnabledChanged() {
        return this.checkmarksEnabledChanged;
    }

    public CallableWithArgs<Void> getDisabledCheckmarkFilterChanged() {
        return this.disabledCheckmarkFilterChanged;
    }


    public void setListDataChanged(CallableWithArgs<Void> listDataChanged) {
        this.listDataChanged = listDataChanged;
    }

    public void setItemRendererChanged(CallableWithArgs<Void> itemRendererChanged) {
        this.itemRendererChanged = itemRendererChanged;
    }

    public void setDisabledItemFilterChanged(CallableWithArgs<Void> disabledItemFilterChanged) {
        this.disabledItemFilterChanged = disabledItemFilterChanged;
    }

    public void setItemEditorChanged(CallableWithArgs<Void> itemEditorChanged) {
        this.itemEditorChanged = itemEditorChanged;
    }

    public void setSelectModeChanged(CallableWithArgs<Void> selectModeChanged) {
        this.selectModeChanged = selectModeChanged;
    }

    public void setCheckmarksEnabledChanged(CallableWithArgs<Void> checkmarksEnabledChanged) {
        this.checkmarksEnabledChanged = checkmarksEnabledChanged;
    }

    public void setDisabledCheckmarkFilterChanged(CallableWithArgs<Void> disabledCheckmarkFilterChanged) {
        this.disabledCheckmarkFilterChanged = disabledCheckmarkFilterChanged;
    }


    public void listDataChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.collections.List<?> arg1) {
        if (listDataChanged != null) {
            listDataChanged.call(arg0, arg1);
        }
    }

    public void itemRendererChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.ListView.ItemRenderer arg1) {
        if (itemRendererChanged != null) {
            itemRendererChanged.call(arg0, arg1);
        }
    }

    public void disabledItemFilterChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.util.Filter<?> arg1) {
        if (disabledItemFilterChanged != null) {
            disabledItemFilterChanged.call(arg0, arg1);
        }
    }

    public void itemEditorChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.ListView.ItemEditor arg1) {
        if (itemEditorChanged != null) {
            itemEditorChanged.call(arg0, arg1);
        }
    }

    public void selectModeChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.wtk.ListView.SelectMode arg1) {
        if (selectModeChanged != null) {
            selectModeChanged.call(arg0, arg1);
        }
    }

    public void checkmarksEnabledChanged(org.apache.pivot.wtk.ListView arg0) {
        if (checkmarksEnabledChanged != null) {
            checkmarksEnabledChanged.call(arg0);
        }
    }

    public void disabledCheckmarkFilterChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.util.Filter<?> arg1) {
        if (disabledCheckmarkFilterChanged != null) {
            disabledCheckmarkFilterChanged.call(arg0, arg1);
        }
    }

}
