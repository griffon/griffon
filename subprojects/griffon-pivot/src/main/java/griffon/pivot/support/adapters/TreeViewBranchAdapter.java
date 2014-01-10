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
public class TreeViewBranchAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TreeViewBranchListener {
    private CallableWithArgs<?> branchExpanded;
    private CallableWithArgs<?> branchCollapsed;

    public CallableWithArgs<?> getBranchExpanded() {
        return this.branchExpanded;
    }

    public CallableWithArgs<?> getBranchCollapsed() {
        return this.branchCollapsed;
    }


    public void setBranchExpanded(CallableWithArgs<?> branchExpanded) {
        this.branchExpanded = branchExpanded;
    }

    public void setBranchCollapsed(CallableWithArgs<?> branchCollapsed) {
        this.branchCollapsed = branchCollapsed;
    }


    public void branchExpanded(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1) {
        if (branchExpanded != null) {
            branchExpanded.call(arg0, arg1);
        }
    }

    public void branchCollapsed(org.apache.pivot.wtk.TreeView arg0, org.apache.pivot.collections.Sequence.Tree.Path arg1) {
        if (branchCollapsed != null) {
            branchCollapsed.call(arg0, arg1);
        }
    }

}
