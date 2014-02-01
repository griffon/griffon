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
import org.apache.pivot.util.Vote;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class RollupStateAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.RollupStateListener {
    private CallableWithArgs<Vote> previewExpandedChange;
    private CallableWithArgs<Void> expandedChangeVetoed;
    private CallableWithArgs<Void> expandedChanged;

    public CallableWithArgs<Vote> getPreviewExpandedChange() {
        return this.previewExpandedChange;
    }

    public CallableWithArgs<Void> getExpandedChangeVetoed() {
        return this.expandedChangeVetoed;
    }

    public CallableWithArgs<Void> getExpandedChanged() {
        return this.expandedChanged;
    }


    public void setPreviewExpandedChange(CallableWithArgs<Vote> previewExpandedChange) {
        this.previewExpandedChange = previewExpandedChange;
    }

    public void setExpandedChangeVetoed(CallableWithArgs<Void> expandedChangeVetoed) {
        this.expandedChangeVetoed = expandedChangeVetoed;
    }

    public void setExpandedChanged(CallableWithArgs<Void> expandedChanged) {
        this.expandedChanged = expandedChanged;
    }


    public org.apache.pivot.util.Vote previewExpandedChange(org.apache.pivot.wtk.Rollup arg0) {
        if (previewExpandedChange != null) {
            return previewExpandedChange.call(arg0);
        }
        return Vote.APPROVE;
    }

    public void expandedChangeVetoed(org.apache.pivot.wtk.Rollup arg0, org.apache.pivot.util.Vote arg1) {
        if (expandedChangeVetoed != null) {
            expandedChangeVetoed.call(arg0, arg1);
        }
    }

    public void expandedChanged(org.apache.pivot.wtk.Rollup arg0) {
        if (expandedChanged != null) {
            expandedChanged.call(arg0);
        }
    }

}
