/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
public class ListButtonAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ListButtonListener {
    private CallableWithArgs<Void> listDataChanged;
    private CallableWithArgs<Void> itemRendererChanged;
    private CallableWithArgs<Void> repeatableChanged;
    private CallableWithArgs<Void> disabledItemFilterChanged;
    private CallableWithArgs<Void> listSizeChanged;

    public CallableWithArgs<Void> getListDataChanged() {
        return this.listDataChanged;
    }

    public CallableWithArgs<Void> getItemRendererChanged() {
        return this.itemRendererChanged;
    }

    public CallableWithArgs<Void> getRepeatableChanged() {
        return this.repeatableChanged;
    }

    public CallableWithArgs<Void> getDisabledItemFilterChanged() {
        return this.disabledItemFilterChanged;
    }

    public CallableWithArgs<Void> getListSizeChanged() {
        return this.listSizeChanged;
    }


    public void setListDataChanged(CallableWithArgs<Void> listDataChanged) {
        this.listDataChanged = listDataChanged;
    }

    public void setItemRendererChanged(CallableWithArgs<Void> itemRendererChanged) {
        this.itemRendererChanged = itemRendererChanged;
    }

    public void setRepeatableChanged(CallableWithArgs<Void> repeatableChanged) {
        this.repeatableChanged = repeatableChanged;
    }

    public void setDisabledItemFilterChanged(CallableWithArgs<Void> disabledItemFilterChanged) {
        this.disabledItemFilterChanged = disabledItemFilterChanged;
    }

    public void setListSizeChanged(CallableWithArgs<Void> listSizeChanged) {
        this.listSizeChanged = listSizeChanged;
    }


    public void listDataChanged(org.apache.pivot.wtk.ListButton arg0, org.apache.pivot.collections.List<?> arg1) {
        if (listDataChanged != null) {
            listDataChanged.call(arg0, arg1);
        }
    }

    public void itemRendererChanged(org.apache.pivot.wtk.ListButton arg0, org.apache.pivot.wtk.ListView.ItemRenderer arg1) {
        if (itemRendererChanged != null) {
            itemRendererChanged.call(arg0, arg1);
        }
    }

    public void repeatableChanged(org.apache.pivot.wtk.ListButton arg0) {
        if (repeatableChanged != null) {
            repeatableChanged.call(arg0);
        }
    }

    public void disabledItemFilterChanged(org.apache.pivot.wtk.ListButton arg0, org.apache.pivot.util.Filter<?> arg1) {
        if (disabledItemFilterChanged != null) {
            disabledItemFilterChanged.call(arg0, arg1);
        }
    }

    public void listSizeChanged(org.apache.pivot.wtk.ListButton arg0, int arg1) {
        if (listSizeChanged != null) {
            listSizeChanged.call(arg0, arg1);
        }
    }

}
