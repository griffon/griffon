/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import org.apache.pivot.wtk.text.Node;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class NodeAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.text.NodeListener {
    private CallableWithArgs<Void> rangeInserted;
    private CallableWithArgs<Void> rangeRemoved;
    private CallableWithArgs<Void> nodeInserted;
    private CallableWithArgs<Void> nodesRemoved;
    private CallableWithArgs<Void> offsetChanged;
    private CallableWithArgs<Void> parentChanged;

    public CallableWithArgs<Void> getRangeInserted() {
        return this.rangeInserted;
    }

    public CallableWithArgs<Void> getRangeRemoved() {
        return this.rangeRemoved;
    }

    public CallableWithArgs<Void> getNodeInserted() {
        return this.nodeInserted;
    }

    public CallableWithArgs<Void> getNodesRemoved() {
        return this.nodesRemoved;
    }

    public CallableWithArgs<Void> getOffsetChanged() {
        return this.offsetChanged;
    }

    public CallableWithArgs<Void> getParentChanged() {
        return this.parentChanged;
    }


    public void setRangeInserted(CallableWithArgs<Void> rangeInserted) {
        this.rangeInserted = rangeInserted;
    }

    public void setRangeRemoved(CallableWithArgs<Void> rangeRemoved) {
        this.rangeRemoved = rangeRemoved;
    }

    public void setNodeInserted(CallableWithArgs<Void> nodeInserted) {
        this.nodeInserted = nodeInserted;
    }

    public void setNodesRemoved(CallableWithArgs<Void> nodesRemoved) {
        this.nodesRemoved = nodesRemoved;
    }

    public void setOffsetChanged(CallableWithArgs<Void> offsetChanged) {
        this.offsetChanged = offsetChanged;
    }

    public void setParentChanged(CallableWithArgs<Void> parentChanged) {
        this.parentChanged = parentChanged;
    }


    public void rangeInserted(org.apache.pivot.wtk.text.Node arg0, int arg1, int arg2) {
        if (rangeInserted != null) {
            rangeInserted.call(arg0, arg1, arg2);
        }
    }

    public void rangeRemoved(org.apache.pivot.wtk.text.Node arg0, int arg1, int arg2) {
        if (rangeRemoved != null) {
            rangeRemoved.call(arg0, arg1, arg2);
        }
    }

    public void nodeInserted(org.apache.pivot.wtk.text.Node arg0, int arg1) {
        if (nodeInserted != null) {
            nodeInserted.call(arg0, arg1);
        }
    }

    public void nodesRemoved(org.apache.pivot.wtk.text.Node arg0, org.apache.pivot.collections.Sequence<Node> arg1, int arg2) {
        if (nodesRemoved != null) {
            nodesRemoved.call(arg0, arg1, arg2);
        }
    }

    public void offsetChanged(org.apache.pivot.wtk.text.Node arg0, int arg1) {
        if (offsetChanged != null) {
            offsetChanged.call(arg0, arg1);
        }
    }

    public void parentChanged(org.apache.pivot.wtk.text.Node arg0, org.apache.pivot.wtk.text.Element arg1) {
        if (parentChanged != null) {
            parentChanged.call(arg0, arg1);
        }
    }

}
