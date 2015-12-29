/*
 * Copyright 2008-2016 the original author or authors.
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
public class ButtonGroupAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ButtonGroupListener {
    private CallableWithArgs<Void> buttonAdded;
    private CallableWithArgs<Void> buttonRemoved;
    private CallableWithArgs<Void> selectionChanged;

    public CallableWithArgs<Void> getButtonAdded() {
        return this.buttonAdded;
    }

    public CallableWithArgs<Void> getButtonRemoved() {
        return this.buttonRemoved;
    }

    public CallableWithArgs<Void> getSelectionChanged() {
        return this.selectionChanged;
    }


    public void setButtonAdded(CallableWithArgs<Void> buttonAdded) {
        this.buttonAdded = buttonAdded;
    }

    public void setButtonRemoved(CallableWithArgs<Void> buttonRemoved) {
        this.buttonRemoved = buttonRemoved;
    }

    public void setSelectionChanged(CallableWithArgs<Void> selectionChanged) {
        this.selectionChanged = selectionChanged;
    }


    public void buttonAdded(org.apache.pivot.wtk.ButtonGroup arg0, org.apache.pivot.wtk.Button arg1) {
        if (buttonAdded != null) {
            buttonAdded.call(arg0, arg1);
        }
    }

    public void buttonRemoved(org.apache.pivot.wtk.ButtonGroup arg0, org.apache.pivot.wtk.Button arg1) {
        if (buttonRemoved != null) {
            buttonRemoved.call(arg0, arg1);
        }
    }

    public void selectionChanged(org.apache.pivot.wtk.ButtonGroup arg0, org.apache.pivot.wtk.Button arg1) {
        if (selectionChanged != null) {
            selectionChanged.call(arg0, arg1);
        }
    }

}
