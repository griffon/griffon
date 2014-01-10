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
public class WindowActionMappingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.WindowActionMappingListener {
    private CallableWithArgs<?> actionMappingAdded;
    private CallableWithArgs<?> actionMappingsRemoved;
    private CallableWithArgs<?> keyStrokeChanged;
    private CallableWithArgs<?> actionChanged;

    public CallableWithArgs<?> getActionMappingAdded() {
        return this.actionMappingAdded;
    }

    public CallableWithArgs<?> getActionMappingsRemoved() {
        return this.actionMappingsRemoved;
    }

    public CallableWithArgs<?> getKeyStrokeChanged() {
        return this.keyStrokeChanged;
    }

    public CallableWithArgs<?> getActionChanged() {
        return this.actionChanged;
    }


    public void setActionMappingAdded(CallableWithArgs<?> actionMappingAdded) {
        this.actionMappingAdded = actionMappingAdded;
    }

    public void setActionMappingsRemoved(CallableWithArgs<?> actionMappingsRemoved) {
        this.actionMappingsRemoved = actionMappingsRemoved;
    }

    public void setKeyStrokeChanged(CallableWithArgs<?> keyStrokeChanged) {
        this.keyStrokeChanged = keyStrokeChanged;
    }

    public void setActionChanged(CallableWithArgs<?> actionChanged) {
        this.actionChanged = actionChanged;
    }


    public void actionMappingAdded(org.apache.pivot.wtk.Window arg0) {
        if (actionMappingAdded != null) {
            actionMappingAdded.call(arg0);
        }
    }

    public void actionMappingsRemoved(org.apache.pivot.wtk.Window arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (actionMappingsRemoved != null) {
            actionMappingsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void keyStrokeChanged(org.apache.pivot.wtk.Window.ActionMapping arg0, org.apache.pivot.wtk.Keyboard.KeyStroke arg1) {
        if (keyStrokeChanged != null) {
            keyStrokeChanged.call(arg0, arg1);
        }
    }

    public void actionChanged(org.apache.pivot.wtk.Window.ActionMapping arg0, org.apache.pivot.wtk.Action arg1) {
        if (actionChanged != null) {
            actionChanged.call(arg0, arg1);
        }
    }

}
