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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TreeViewNodeAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TreeViewNodeListener {
    private CallableWithArgs<Void> nodeInserted;
    private CallableWithArgs<Void> nodesRemoved;
    private CallableWithArgs<Void> nodeUpdated;
    private CallableWithArgs<Void> nodesSorted;
    private CallableWithArgs<Void> nodesCleared;

    public CallableWithArgs<Void> getNodeInserted() {
        return this.nodeInserted;
    }

    public CallableWithArgs<Void> getNodesRemoved() {
        return this.nodesRemoved;
    }

    public CallableWithArgs<Void> getNodeUpdated() {
        return this.nodeUpdated;
    }

    public CallableWithArgs<Void> getNodesSorted() {
        return this.nodesSorted;
    }

    public CallableWithArgs<Void> getNodesCleared() {
        return this.nodesCleared;
    }


    public void setNodeInserted(CallableWithArgs<Void> nodeInserted) {
        this.nodeInserted = nodeInserted;
    }

    public void setNodesRemoved(CallableWithArgs<Void> nodesRemoved) {
        this.nodesRemoved = nodesRemoved;
    }

    public void setNodeUpdated(CallableWithArgs<Void> nodeUpdated) {
        this.nodeUpdated = nodeUpdated;
    }

    public void setNodesSorted(CallableWithArgs<Void> nodesSorted) {
        this.nodesSorted = nodesSorted;
    }

    public void setNodesCleared(CallableWithArgs<Void> nodesCleared) {
        this.nodesCleared = nodesCleared;
    }


    public void nodeInserted(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1, int arg2) {
        if (nodeInserted != null) {
            nodeInserted.call(arg0, arg1, arg2);
        }
    }

    public void nodesRemoved(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1, int arg2, int arg3) {
        if (nodesRemoved != null) {
            nodesRemoved.call(arg0, arg1, arg2, arg3);
        }
    }

    public void nodeUpdated(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1, int arg2) {
        if (nodeUpdated != null) {
            nodeUpdated.call(arg0, arg1, arg2);
        }
    }

    public void nodesSorted(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1) {
        if (nodesSorted != null) {
            nodesSorted.call(arg0, arg1);
        }
    }

    public void nodesCleared(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1) {
        if (nodesCleared != null) {
            nodesCleared.call(arg0, arg1);
        }
    }

}
