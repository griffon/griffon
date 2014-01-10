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
public class TreeViewNodeAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TreeViewNodeListener {
    private CallableWithArgs<?> nodeInserted;
    private CallableWithArgs<?> nodesRemoved;
    private CallableWithArgs<?> nodeUpdated;
    private CallableWithArgs<?> nodesSorted;
    private CallableWithArgs<?> nodesCleared;

    public CallableWithArgs<?> getNodeInserted() {
        return this.nodeInserted;
    }

    public CallableWithArgs<?> getNodesRemoved() {
        return this.nodesRemoved;
    }

    public CallableWithArgs<?> getNodeUpdated() {
        return this.nodeUpdated;
    }

    public CallableWithArgs<?> getNodesSorted() {
        return this.nodesSorted;
    }

    public CallableWithArgs<?> getNodesCleared() {
        return this.nodesCleared;
    }


    public void setNodeInserted(CallableWithArgs<?> nodeInserted) {
        this.nodeInserted = nodeInserted;
    }

    public void setNodesRemoved(CallableWithArgs<?> nodesRemoved) {
        this.nodesRemoved = nodesRemoved;
    }

    public void setNodeUpdated(CallableWithArgs<?> nodeUpdated) {
        this.nodeUpdated = nodeUpdated;
    }

    public void setNodesSorted(CallableWithArgs<?> nodesSorted) {
        this.nodesSorted = nodesSorted;
    }

    public void setNodesCleared(CallableWithArgs<?> nodesCleared) {
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
