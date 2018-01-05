/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
public class TreeViewAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TreeViewListener {
    private CallableWithArgs<Void> selectModeChanged;
    private CallableWithArgs<Void> checkmarksEnabledChanged;
    private CallableWithArgs<Void> disabledCheckmarkFilterChanged;
    private CallableWithArgs<Void> nodeRendererChanged;
    private CallableWithArgs<Void> treeDataChanged;
    private CallableWithArgs<Void> nodeEditorChanged;
    private CallableWithArgs<Void> showMixedCheckmarkStateChanged;
    private CallableWithArgs<Void> disabledNodeFilterChanged;

    public CallableWithArgs<Void> getSelectModeChanged() {
        return this.selectModeChanged;
    }

    public CallableWithArgs<Void> getCheckmarksEnabledChanged() {
        return this.checkmarksEnabledChanged;
    }

    public CallableWithArgs<Void> getDisabledCheckmarkFilterChanged() {
        return this.disabledCheckmarkFilterChanged;
    }

    public CallableWithArgs<Void> getNodeRendererChanged() {
        return this.nodeRendererChanged;
    }

    public CallableWithArgs<Void> getTreeDataChanged() {
        return this.treeDataChanged;
    }

    public CallableWithArgs<Void> getNodeEditorChanged() {
        return this.nodeEditorChanged;
    }

    public CallableWithArgs<Void> getShowMixedCheckmarkStateChanged() {
        return this.showMixedCheckmarkStateChanged;
    }

    public CallableWithArgs<Void> getDisabledNodeFilterChanged() {
        return this.disabledNodeFilterChanged;
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

    public void setNodeRendererChanged(CallableWithArgs<Void> nodeRendererChanged) {
        this.nodeRendererChanged = nodeRendererChanged;
    }

    public void setTreeDataChanged(CallableWithArgs<Void> treeDataChanged) {
        this.treeDataChanged = treeDataChanged;
    }

    public void setNodeEditorChanged(CallableWithArgs<Void> nodeEditorChanged) {
        this.nodeEditorChanged = nodeEditorChanged;
    }

    public void setShowMixedCheckmarkStateChanged(CallableWithArgs<Void> showMixedCheckmarkStateChanged) {
        this.showMixedCheckmarkStateChanged = showMixedCheckmarkStateChanged;
    }

    public void setDisabledNodeFilterChanged(CallableWithArgs<Void> disabledNodeFilterChanged) {
        this.disabledNodeFilterChanged = disabledNodeFilterChanged;
    }


    public void selectModeChanged(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.wtk.TreeView.SelectMode arg1) {
        if (selectModeChanged != null) {
            selectModeChanged.call(arg0, arg1);
        }
    }

    public void checkmarksEnabledChanged(org.apache.pivot.wtk.TreeView arg0) {
        if (checkmarksEnabledChanged != null) {
            checkmarksEnabledChanged.call(arg0);
        }
    }

    public void disabledCheckmarkFilterChanged(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.util.Filter<?> arg1) {
        if (disabledCheckmarkFilterChanged != null) {
            disabledCheckmarkFilterChanged.call(arg0, arg1);
        }
    }

    public void nodeRendererChanged(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.wtk.TreeView.NodeRenderer arg1) {
        if (nodeRendererChanged != null) {
            nodeRendererChanged.call(arg0, arg1);
        }
    }

    public void treeDataChanged(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.List<?> arg1) {
        if (treeDataChanged != null) {
            treeDataChanged.call(arg0, arg1);
        }
    }

    public void nodeEditorChanged(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.wtk.TreeView.NodeEditor arg1) {
        if (nodeEditorChanged != null) {
            nodeEditorChanged.call(arg0, arg1);
        }
    }

    public void showMixedCheckmarkStateChanged(org.apache.pivot.wtk.TreeView arg0) {
        if (showMixedCheckmarkStateChanged != null) {
            showMixedCheckmarkStateChanged.call(arg0);
        }
    }

    public void disabledNodeFilterChanged(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.util.Filter<?> arg1) {
        if (disabledNodeFilterChanged != null) {
            disabledNodeFilterChanged.call(arg0, arg1);
        }
    }

}
