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
public class ContainerAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ContainerListener {
    private CallableWithArgs<?> componentInserted;
    private CallableWithArgs<?> componentsRemoved;
    private CallableWithArgs<?> focusTraversalPolicyChanged;
    private CallableWithArgs<?> componentMoved;

    public CallableWithArgs<?> getComponentInserted() {
        return this.componentInserted;
    }

    public CallableWithArgs<?> getComponentsRemoved() {
        return this.componentsRemoved;
    }

    public CallableWithArgs<?> getFocusTraversalPolicyChanged() {
        return this.focusTraversalPolicyChanged;
    }

    public CallableWithArgs<?> getComponentMoved() {
        return this.componentMoved;
    }


    public void setComponentInserted(CallableWithArgs<?> componentInserted) {
        this.componentInserted = componentInserted;
    }

    public void setComponentsRemoved(CallableWithArgs<?> componentsRemoved) {
        this.componentsRemoved = componentsRemoved;
    }

    public void setFocusTraversalPolicyChanged(CallableWithArgs<?> focusTraversalPolicyChanged) {
        this.focusTraversalPolicyChanged = focusTraversalPolicyChanged;
    }

    public void setComponentMoved(CallableWithArgs<?> componentMoved) {
        this.componentMoved = componentMoved;
    }


    public void componentInserted(org.apache.pivot.wtk.Container arg0, int arg1) {
        if (componentInserted != null) {
            componentInserted.call(arg0, arg1);
        }
    }

    public void componentsRemoved(org.apache.pivot.wtk.Container arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
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
