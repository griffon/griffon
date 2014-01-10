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
public class ListViewSelectionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ListViewSelectionListener {
    private CallableWithArgs<?> selectedRangesChanged;
    private CallableWithArgs<?> selectedRangeAdded;
    private CallableWithArgs<?> selectedRangeRemoved;
    private CallableWithArgs<?> selectedItemChanged;

    public CallableWithArgs<?> getSelectedRangesChanged() {
        return this.selectedRangesChanged;
    }

    public CallableWithArgs<?> getSelectedRangeAdded() {
        return this.selectedRangeAdded;
    }

    public CallableWithArgs<?> getSelectedRangeRemoved() {
        return this.selectedRangeRemoved;
    }

    public CallableWithArgs<?> getSelectedItemChanged() {
        return this.selectedItemChanged;
    }


    public void setSelectedRangesChanged(CallableWithArgs<?> selectedRangesChanged) {
        this.selectedRangesChanged = selectedRangesChanged;
    }

    public void setSelectedRangeAdded(CallableWithArgs<?> selectedRangeAdded) {
        this.selectedRangeAdded = selectedRangeAdded;
    }

    public void setSelectedRangeRemoved(CallableWithArgs<?> selectedRangeRemoved) {
        this.selectedRangeRemoved = selectedRangeRemoved;
    }

    public void setSelectedItemChanged(CallableWithArgs<?> selectedItemChanged) {
        this.selectedItemChanged = selectedItemChanged;
    }


    public void selectedRangesChanged(org.apache.pivot.wtk.ListView arg0, org.apache.pivot.collections.Sequence arg1) {
        if (selectedRangesChanged != null) {
            selectedRangesChanged.call(arg0, arg1);
        }
    }

    public void selectedRangeAdded(org.apache.pivot.wtk.ListView arg0, int arg1, int arg2) {
        if (selectedRangeAdded != null) {
            selectedRangeAdded.call(arg0, arg1, arg2);
        }
    }

    public void selectedRangeRemoved(org.apache.pivot.wtk.ListView arg0, int arg1, int arg2) {
        if (selectedRangeRemoved != null) {
            selectedRangeRemoved.call(arg0, arg1, arg2);
        }
    }

    public void selectedItemChanged(org.apache.pivot.wtk.ListView arg0, java.lang.Object arg1) {
        if (selectedItemChanged != null) {
            selectedItemChanged.call(arg0, arg1);
        }
    }

}
