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
public class MenuBarAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.MenuBarListener {
    private CallableWithArgs<?> activeItemChanged;
    private CallableWithArgs<?> itemInserted;
    private CallableWithArgs<?> itemsRemoved;

    public CallableWithArgs<?> getActiveItemChanged() {
        return this.activeItemChanged;
    }

    public CallableWithArgs<?> getItemInserted() {
        return this.itemInserted;
    }

    public CallableWithArgs<?> getItemsRemoved() {
        return this.itemsRemoved;
    }


    public void setActiveItemChanged(CallableWithArgs<?> activeItemChanged) {
        this.activeItemChanged = activeItemChanged;
    }

    public void setItemInserted(CallableWithArgs<?> itemInserted) {
        this.itemInserted = itemInserted;
    }

    public void setItemsRemoved(CallableWithArgs<?> itemsRemoved) {
        this.itemsRemoved = itemsRemoved;
    }


    public void activeItemChanged(org.apache.pivot.wtk.MenuBar arg0, org.apache.pivot.wtk.MenuBar.Item arg1) {
        if (activeItemChanged != null) {
            activeItemChanged.call(arg0, arg1);
        }
    }

    public void itemInserted(org.apache.pivot.wtk.MenuBar arg0, int arg1) {
        if (itemInserted != null) {
            itemInserted.call(arg0, arg1);
        }
    }

    public void itemsRemoved(org.apache.pivot.wtk.MenuBar arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (itemsRemoved != null) {
            itemsRemoved.call(arg0, arg1, arg2);
        }
    }

}
