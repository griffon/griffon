/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
import org.apache.pivot.wtk.Component;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ContainerAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ContainerListener {
    private CallableWithArgs<Void> componentInserted;
    private CallableWithArgs<Void> componentsRemoved;
    private CallableWithArgs<Void> focusTraversalPolicyChanged;
    private CallableWithArgs<Void> componentMoved;

    public CallableWithArgs<Void> getComponentInserted() {
        return this.componentInserted;
    }

    public CallableWithArgs<Void> getComponentsRemoved() {
        return this.componentsRemoved;
    }

    public CallableWithArgs<Void> getFocusTraversalPolicyChanged() {
        return this.focusTraversalPolicyChanged;
    }

    public CallableWithArgs<Void> getComponentMoved() {
        return this.componentMoved;
    }


    public void setComponentInserted(CallableWithArgs<Void> componentInserted) {
        this.componentInserted = componentInserted;
    }

    public void setComponentsRemoved(CallableWithArgs<Void> componentsRemoved) {
        this.componentsRemoved = componentsRemoved;
    }

    public void setFocusTraversalPolicyChanged(CallableWithArgs<Void> focusTraversalPolicyChanged) {
        this.focusTraversalPolicyChanged = focusTraversalPolicyChanged;
    }

    public void setComponentMoved(CallableWithArgs<Void> componentMoved) {
        this.componentMoved = componentMoved;
    }


    public void componentInserted(org.apache.pivot.wtk.Container arg0, int arg1) {
        if (componentInserted != null) {
            componentInserted.call(arg0, arg1);
        }
    }

    public void componentsRemoved(org.apache.pivot.wtk.Container arg0, int arg1, org.apache.pivot.collections.Sequence<Component> arg2) {
        if (componentsRemoved != null) {
            componentsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void focusTraversalPolicyChanged(org.apache.pivot.wtk.Container arg0, org.apache.pivot.wtk.FocusTraversalPolicy arg1) {
        if (focusTraversalPolicyChanged != null) {
            focusTraversalPolicyChanged.call(arg0, arg1);
        }
    }

    public void componentMoved(org.apache.pivot.wtk.Container arg0, int arg1, int arg2) {
        if (componentMoved != null) {
            componentMoved.call(arg0, arg1, arg2);
        }
    }

}
