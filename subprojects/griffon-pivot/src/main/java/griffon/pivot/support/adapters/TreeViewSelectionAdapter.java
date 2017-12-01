/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
import org.apache.pivot.collections.Sequence;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TreeViewSelectionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TreeViewSelectionListener {
    private CallableWithArgs<Void> selectedPathsChanged;
    private CallableWithArgs<Void> selectedNodeChanged;
    private CallableWithArgs<Void> selectedPathAdded;
    private CallableWithArgs<Void> selectedPathRemoved;

    public CallableWithArgs<Void> getSelectedPathsChanged() {
        return this.selectedPathsChanged;
    }

    public CallableWithArgs<Void> getSelectedNodeChanged() {
        return this.selectedNodeChanged;
    }

    public CallableWithArgs<Void> getSelectedPathAdded() {
        return this.selectedPathAdded;
    }

    public CallableWithArgs<Void> getSelectedPathRemoved() {
        return this.selectedPathRemoved;
    }


    public void setSelectedPathsChanged(CallableWithArgs<Void> selectedPathsChanged) {
        this.selectedPathsChanged = selectedPathsChanged;
    }

    public void setSelectedNodeChanged(CallableWithArgs<Void> selectedNodeChanged) {
        this.selectedNodeChanged = selectedNodeChanged;
    }

    public void setSelectedPathAdded(CallableWithArgs<Void> selectedPathAdded) {
        this.selectedPathAdded = selectedPathAdded;
    }

    public void setSelectedPathRemoved(CallableWithArgs<Void> selectedPathRemoved) {
        this.selectedPathRemoved = selectedPathRemoved;
    }


    public void selectedPathsChanged(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence<Sequence.Tree.Path> arg1) {
        if (selectedPathsChanged != null) {
            selectedPathsChanged.call(arg0, arg1);
        }
    }

    public void selectedNodeChanged(org.apache.pivot.wtk.TreeView arg0, java.lang.Object arg1) {
        if (selectedNodeChanged != null) {
            selectedNodeChanged.call(arg0, arg1);
        }
    }

    public void selectedPathAdded(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1) {
        if (selectedPathAdded != null) {
            selectedPathAdded.call(arg0, arg1);
        }
    }

    public void selectedPathRemoved(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1) {
        if (selectedPathRemoved != null) {
            selectedPathRemoved.call(arg0, arg1);
        }
    }

}
