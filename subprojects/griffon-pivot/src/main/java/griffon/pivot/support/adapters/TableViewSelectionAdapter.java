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
import org.apache.pivot.wtk.Span;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TableViewSelectionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TableViewSelectionListener {
    private CallableWithArgs<Void> selectedRangesChanged;
    private CallableWithArgs<Void> selectedRangeAdded;
    private CallableWithArgs<Void> selectedRangeRemoved;
    private CallableWithArgs<Void> selectedRowChanged;

    public CallableWithArgs<Void> getSelectedRangesChanged() {
        return this.selectedRangesChanged;
    }

    public CallableWithArgs<Void> getSelectedRangeAdded() {
        return this.selectedRangeAdded;
    }

    public CallableWithArgs<Void> getSelectedRangeRemoved() {
        return this.selectedRangeRemoved;
    }

    public CallableWithArgs<Void> getSelectedRowChanged() {
        return this.selectedRowChanged;
    }


    public void setSelectedRangesChanged(CallableWithArgs<Void> selectedRangesChanged) {
        this.selectedRangesChanged = selectedRangesChanged;
    }

    public void setSelectedRangeAdded(CallableWithArgs<Void> selectedRangeAdded) {
        this.selectedRangeAdded = selectedRangeAdded;
    }

    public void setSelectedRangeRemoved(CallableWithArgs<Void> selectedRangeRemoved) {
        this.selectedRangeRemoved = selectedRangeRemoved;
    }

    public void setSelectedRowChanged(CallableWithArgs<Void> selectedRowChanged) {
        this.selectedRowChanged = selectedRowChanged;
    }


    public void selectedRangesChanged(org.apache.pivot.wtk.TableView arg0, org.apache.pivot.collections.Sequence<Span> arg1) {
        if (selectedRangesChanged != null) {
            selectedRangesChanged.call(arg0, arg1);
        }
    }

    public void selectedRangeAdded(org.apache.pivot.wtk.TableView arg0, int arg1, int arg2) {
        if (selectedRangeAdded != null) {
            selectedRangeAdded.call(arg0, arg1, arg2);
        }
    }

    public void selectedRangeRemoved(org.apache.pivot.wtk.TableView arg0, int arg1, int arg2) {
        if (selectedRangeRemoved != null) {
            selectedRangeRemoved.call(arg0, arg1, arg2);
        }
    }

    public void selectedRowChanged(org.apache.pivot.wtk.TableView arg0, java.lang.Object arg1) {
        if (selectedRowChanged != null) {
            selectedRowChanged.call(arg0, arg1);
        }
    }

}
