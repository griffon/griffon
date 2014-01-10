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
public class ButtonBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ButtonBindingListener {
    private CallableWithArgs<Void> buttonDataKeyChanged;
    private CallableWithArgs<Void> buttonDataBindTypeChanged;
    private CallableWithArgs<Void> buttonDataBindMappingChanged;
    private CallableWithArgs<Void> selectedKeyChanged;
    private CallableWithArgs<Void> selectedBindTypeChanged;
    private CallableWithArgs<Void> selectedBindMappingChanged;
    private CallableWithArgs<Void> stateKeyChanged;
    private CallableWithArgs<Void> stateBindTypeChanged;
    private CallableWithArgs<Void> stateBindMappingChanged;

    public CallableWithArgs<Void> getButtonDataKeyChanged() {
        return this.buttonDataKeyChanged;
    }

    public CallableWithArgs<Void> getButtonDataBindTypeChanged() {
        return this.buttonDataBindTypeChanged;
    }

    public CallableWithArgs<Void> getButtonDataBindMappingChanged() {
        return this.buttonDataBindMappingChanged;
    }

    public CallableWithArgs<Void> getSelectedKeyChanged() {
        return this.selectedKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedBindTypeChanged() {
        return this.selectedBindTypeChanged;
    }

    public CallableWithArgs<Void> getSelectedBindMappingChanged() {
        return this.selectedBindMappingChanged;
    }

    public CallableWithArgs<Void> getStateKeyChanged() {
        return this.stateKeyChanged;
    }

    public CallableWithArgs<Void> getStateBindTypeChanged() {
        return this.stateBindTypeChanged;
    }

    public CallableWithArgs<Void> getStateBindMappingChanged() {
        return this.stateBindMappingChanged;
    }


    public void setButtonDataKeyChanged(CallableWithArgs<Void> buttonDataKeyChanged) {
        this.buttonDataKeyChanged = buttonDataKeyChanged;
    }

    public void setButtonDataBindTypeChanged(CallableWithArgs<Void> buttonDataBindTypeChanged) {
        this.buttonDataBindTypeChanged = buttonDataBindTypeChanged;
    }

    public void setButtonDataBindMappingChanged(CallableWithArgs<Void> buttonDataBindMappingChanged) {
        this.buttonDataBindMappingChanged = buttonDataBindMappingChanged;
    }

    public void setSelectedKeyChanged(CallableWithArgs<Void> selectedKeyChanged) {
        this.selectedKeyChanged = selectedKeyChanged;
    }

    public void setSelectedBindTypeChanged(CallableWithArgs<Void> selectedBindTypeChanged) {
        this.selectedBindTypeChanged = selectedBindTypeChanged;
    }

    public void setSelectedBindMappingChanged(CallableWithArgs<Void> selectedBindMappingChanged) {
        this.selectedBindMappingChanged = selectedBindMappingChanged;
    }

    public void setStateKeyChanged(CallableWithArgs<Void> stateKeyChanged) {
        this.stateKeyChanged = stateKeyChanged;
    }

    public void setStateBindTypeChanged(CallableWithArgs<Void> stateBindTypeChanged) {
        this.stateBindTypeChanged = stateBindTypeChanged;
    }

    public void setStateBindMappingChanged(CallableWithArgs<Void> stateBindMappingChanged) {
        this.stateBindMappingChanged = stateBindMappingChanged;
    }


    public void buttonDataKeyChanged(org.apache.pivot.wtk.Button arg0, java.lang.String arg1) {
        if (buttonDataKeyChanged != null) {
            buttonDataKeyChanged.call(arg0, arg1);
        }
    }

    public void buttonDataBindTypeChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.BindType arg1) {
        if (buttonDataBindTypeChanged != null) {
            buttonDataBindTypeChanged.call(arg0, arg1);
        }
    }

    public void buttonDataBindMappingChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.Button.ButtonDataBindMapping arg1) {
        if (buttonDataBindMappingChanged != null) {
            buttonDataBindMappingChanged.call(arg0, arg1);
        }
    }

    public void selectedKeyChanged(org.apache.pivot.wtk.Button arg0, java.lang.String arg1) {
        if (selectedKeyChanged != null) {
            selectedKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedBindTypeChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedBindTypeChanged != null) {
            selectedBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedBindMappingChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.Button.SelectedBindMapping arg1) {
        if (selectedBindMappingChanged != null) {
            selectedBindMappingChanged.call(arg0, arg1);
        }
    }

    public void stateKeyChanged(org.apache.pivot.wtk.Button arg0, java.lang.String arg1) {
        if (stateKeyChanged != null) {
            stateKeyChanged.call(arg0, arg1);
        }
    }

    public void stateBindTypeChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.BindType arg1) {
        if (stateBindTypeChanged != null) {
            stateBindTypeChanged.call(arg0, arg1);
        }
    }

    public void stateBindMappingChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.Button.StateBindMapping arg1) {
        if (stateBindMappingChanged != null) {
            stateBindMappingChanged.call(arg0, arg1);
        }
    }

}
