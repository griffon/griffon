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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ColorChooserButtonBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ColorChooserButtonBindingListener {
    private CallableWithArgs<Void> selectedColorBindMappingChanged;
    private CallableWithArgs<Void> selectedColorKeyChanged;
    private CallableWithArgs<Void> selectedColorBindTypeChanged;

    public CallableWithArgs<Void> getSelectedColorBindMappingChanged() {
        return this.selectedColorBindMappingChanged;
    }

    public CallableWithArgs<Void> getSelectedColorKeyChanged() {
        return this.selectedColorKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedColorBindTypeChanged() {
        return this.selectedColorBindTypeChanged;
    }


    public void setSelectedColorBindMappingChanged(CallableWithArgs<Void> selectedColorBindMappingChanged) {
        this.selectedColorBindMappingChanged = selectedColorBindMappingChanged;
    }

    public void setSelectedColorKeyChanged(CallableWithArgs<Void> selectedColorKeyChanged) {
        this.selectedColorKeyChanged = selectedColorKeyChanged;
    }

    public void setSelectedColorBindTypeChanged(CallableWithArgs<Void> selectedColorBindTypeChanged) {
        this.selectedColorBindTypeChanged = selectedColorBindTypeChanged;
    }


    public void selectedColorBindMappingChanged(org.apache.pivot.wtk.ColorChooserButton arg0, org.apache.pivot.wtk.ColorChooser.SelectedColorBindMapping arg1) {
        if (selectedColorBindMappingChanged != null) {
            selectedColorBindMappingChanged.call(arg0, arg1);
        }
    }

    public void selectedColorKeyChanged(org.apache.pivot.wtk.ColorChooserButton arg0, java.lang.String arg1) {
        if (selectedColorKeyChanged != null) {
            selectedColorKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedColorBindTypeChanged(org.apache.pivot.wtk.ColorChooserButton arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedColorBindTypeChanged != null) {
            selectedColorBindTypeChanged.call(arg0, arg1);
        }
    }

}
