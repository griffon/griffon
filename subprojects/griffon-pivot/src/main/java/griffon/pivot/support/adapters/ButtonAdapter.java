/*
 * Copyright 2008-2017 the original author or authors.
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
public class ButtonAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ButtonListener {
    private CallableWithArgs<Void> buttonDataChanged;
    private CallableWithArgs<Void> dataRendererChanged;
    private CallableWithArgs<Void> actionChanged;
    private CallableWithArgs<Void> toggleButtonChanged;
    private CallableWithArgs<Void> triStateChanged;
    private CallableWithArgs<Void> buttonGroupChanged;

    public CallableWithArgs<Void> getButtonDataChanged() {
        return this.buttonDataChanged;
    }

    public CallableWithArgs<Void> getDataRendererChanged() {
        return this.dataRendererChanged;
    }

    public CallableWithArgs<Void> getActionChanged() {
        return this.actionChanged;
    }

    public CallableWithArgs<Void> getToggleButtonChanged() {
        return this.toggleButtonChanged;
    }

    public CallableWithArgs<Void> getTriStateChanged() {
        return this.triStateChanged;
    }

    public CallableWithArgs<Void> getButtonGroupChanged() {
        return this.buttonGroupChanged;
    }


    public void setButtonDataChanged(CallableWithArgs<Void> buttonDataChanged) {
        this.buttonDataChanged = buttonDataChanged;
    }

    public void setDataRendererChanged(CallableWithArgs<Void> dataRendererChanged) {
        this.dataRendererChanged = dataRendererChanged;
    }

    public void setActionChanged(CallableWithArgs<Void> actionChanged) {
        this.actionChanged = actionChanged;
    }

    public void setToggleButtonChanged(CallableWithArgs<Void> toggleButtonChanged) {
        this.toggleButtonChanged = toggleButtonChanged;
    }

    public void setTriStateChanged(CallableWithArgs<Void> triStateChanged) {
        this.triStateChanged = triStateChanged;
    }

    public void setButtonGroupChanged(CallableWithArgs<Void> buttonGroupChanged) {
        this.buttonGroupChanged = buttonGroupChanged;
    }


    public void buttonDataChanged(org.apache.pivot.wtk.Button arg0, java.lang.Object arg1) {
        if (buttonDataChanged != null) {
            buttonDataChanged.call(arg0, arg1);
        }
    }

    public void dataRendererChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.Button.DataRenderer arg1) {
        if (dataRendererChanged != null) {
            dataRendererChanged.call(arg0, arg1);
        }
    }

    public void actionChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.Action arg1) {
        if (actionChanged != null) {
            actionChanged.call(arg0, arg1);
        }
    }

    public void toggleButtonChanged(org.apache.pivot.wtk.Button arg0) {
        if (toggleButtonChanged != null) {
            toggleButtonChanged.call(arg0);
        }
    }

    public void triStateChanged(org.apache.pivot.wtk.Button arg0) {
        if (triStateChanged != null) {
            triStateChanged.call(arg0);
        }
    }

    public void buttonGroupChanged(org.apache.pivot.wtk.Button arg0, org.apache.pivot.wtk.ButtonGroup arg1) {
        if (buttonGroupChanged != null) {
            buttonGroupChanged.call(arg0, arg1);
        }
    }

}
