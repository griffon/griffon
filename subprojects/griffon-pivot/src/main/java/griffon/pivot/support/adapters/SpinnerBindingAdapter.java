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
public class SpinnerBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SpinnerBindingListener {
    private CallableWithArgs<Void> selectedItemKeyChanged;
    private CallableWithArgs<Void> selectedItemBindTypeChanged;
    private CallableWithArgs<Void> selectedItemBindMappingChanged;
    private CallableWithArgs<Void> spinnerDataKeyChanged;
    private CallableWithArgs<Void> spinnerDataBindTypeChanged;
    private CallableWithArgs<Void> spinnerDataBindMappingChanged;

    public CallableWithArgs<Void> getSelectedItemKeyChanged() {
        return this.selectedItemKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedItemBindTypeChanged() {
        return this.selectedItemBindTypeChanged;
    }

    public CallableWithArgs<Void> getSelectedItemBindMappingChanged() {
        return this.selectedItemBindMappingChanged;
    }

    public CallableWithArgs<Void> getSpinnerDataKeyChanged() {
        return this.spinnerDataKeyChanged;
    }

    public CallableWithArgs<Void> getSpinnerDataBindTypeChanged() {
        return this.spinnerDataBindTypeChanged;
    }

    public CallableWithArgs<Void> getSpinnerDataBindMappingChanged() {
        return this.spinnerDataBindMappingChanged;
    }


    public void setSelectedItemKeyChanged(CallableWithArgs<Void> selectedItemKeyChanged) {
        this.selectedItemKeyChanged = selectedItemKeyChanged;
    }

    public void setSelectedItemBindTypeChanged(CallableWithArgs<Void> selectedItemBindTypeChanged) {
        this.selectedItemBindTypeChanged = selectedItemBindTypeChanged;
    }

    public void setSelectedItemBindMappingChanged(CallableWithArgs<Void> selectedItemBindMappingChanged) {
        this.selectedItemBindMappingChanged = selectedItemBindMappingChanged;
    }

    public void setSpinnerDataKeyChanged(CallableWithArgs<Void> spinnerDataKeyChanged) {
        this.spinnerDataKeyChanged = spinnerDataKeyChanged;
    }

    public void setSpinnerDataBindTypeChanged(CallableWithArgs<Void> spinnerDataBindTypeChanged) {
        this.spinnerDataBindTypeChanged = spinnerDataBindTypeChanged;
    }

    public void setSpinnerDataBindMappingChanged(CallableWithArgs<Void> spinnerDataBindMappingChanged) {
        this.spinnerDataBindMappingChanged = spinnerDataBindMappingChanged;
    }


    public void selectedItemKeyChanged(org.apache.pivot.wtk.Spinner arg0, java.lang.String arg1) {
        if (selectedItemKeyChanged != null) {
            selectedItemKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedItemBindTypeChanged(org.apache.pivot.wtk.Spinner arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedItemBindTypeChanged != null) {
            selectedItemBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedItemBindMappingChanged(org.apache.pivot.wtk.Spinner arg0, org.apache.pivot.wtk.Spinner.ItemBindMapping arg1) {
        if (selectedItemBindMappingChanged != null) {
            selectedItemBindMappingChanged.call(arg0, arg1);
        }
    }

    public void spinnerDataKeyChanged(org.apache.pivot.wtk.Spinner arg0, java.lang.String arg1) {
        if (spinnerDataKeyChanged != null) {
            spinnerDataKeyChanged.call(arg0, arg1);
        }
    }

    public void spinnerDataBindTypeChanged(org.apache.pivot.wtk.Spinner arg0, org.apache.pivot.wtk.BindType arg1) {
        if (spinnerDataBindTypeChanged != null) {
            spinnerDataBindTypeChanged.call(arg0, arg1);
        }
    }

    public void spinnerDataBindMappingChanged(org.apache.pivot.wtk.Spinner arg0, org.apache.pivot.wtk.Spinner.SpinnerDataBindMapping arg1) {
        if (spinnerDataBindMappingChanged != null) {
            spinnerDataBindMappingChanged.call(arg0, arg1);
        }
    }

}
