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
import org.apache.pivot.util.Vote;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TabPaneSelectionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TabPaneSelectionListener {
    private CallableWithArgs<Vote> previewSelectedIndexChange;
    private CallableWithArgs<Void> selectedIndexChanged;
    private CallableWithArgs<Void> selectedIndexChangeVetoed;

    public CallableWithArgs<Vote> getPreviewSelectedIndexChange() {
        return this.previewSelectedIndexChange;
    }

    public CallableWithArgs<Void> getSelectedIndexChanged() {
        return this.selectedIndexChanged;
    }

    public CallableWithArgs<Void> getSelectedIndexChangeVetoed() {
        return this.selectedIndexChangeVetoed;
    }


    public void setPreviewSelectedIndexChange(CallableWithArgs<Vote> previewSelectedIndexChange) {
        this.previewSelectedIndexChange = previewSelectedIndexChange;
    }

    public void setSelectedIndexChanged(CallableWithArgs<Void> selectedIndexChanged) {
        this.selectedIndexChanged = selectedIndexChanged;
    }

    public void setSelectedIndexChangeVetoed(CallableWithArgs<Void> selectedIndexChangeVetoed) {
        this.selectedIndexChangeVetoed = selectedIndexChangeVetoed;
    }


    public org.apache.pivot.util.Vote previewSelectedIndexChange(org.apache.pivot.wtk.TabPane arg0, int arg1) {
        if (previewSelectedIndexChange != null) {
            return previewSelectedIndexChange.call(arg0, arg1);
        }
        return Vote.APPROVE;
    }

    public void selectedIndexChanged(org.apache.pivot.wtk.TabPane arg0, int arg1) {
        if (selectedIndexChanged != null) {
            selectedIndexChanged.call(arg0, arg1);
        }
    }

    public void selectedIndexChangeVetoed(org.apache.pivot.wtk.TabPane arg0, org.apache.pivot.util.Vote arg1) {
        if (selectedIndexChangeVetoed != null) {
            selectedIndexChangeVetoed.call(arg0, arg1);
        }
    }

}
