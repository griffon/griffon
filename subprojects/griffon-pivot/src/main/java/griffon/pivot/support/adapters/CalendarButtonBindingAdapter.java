/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
public class CalendarButtonBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.CalendarButtonBindingListener {
    private CallableWithArgs<Void> selectedDateKeyChanged;
    private CallableWithArgs<Void> selectedDateBindTypeChanged;
    private CallableWithArgs<Void> selectedDateBindMappingChanged;

    public CallableWithArgs<Void> getSelectedDateKeyChanged() {
        return this.selectedDateKeyChanged;
    }

    public CallableWithArgs<Void> getSelectedDateBindTypeChanged() {
        return this.selectedDateBindTypeChanged;
    }

    public CallableWithArgs<Void> getSelectedDateBindMappingChanged() {
        return this.selectedDateBindMappingChanged;
    }


    public void setSelectedDateKeyChanged(CallableWithArgs<Void> selectedDateKeyChanged) {
        this.selectedDateKeyChanged = selectedDateKeyChanged;
    }

    public void setSelectedDateBindTypeChanged(CallableWithArgs<Void> selectedDateBindTypeChanged) {
        this.selectedDateBindTypeChanged = selectedDateBindTypeChanged;
    }

    public void setSelectedDateBindMappingChanged(CallableWithArgs<Void> selectedDateBindMappingChanged) {
        this.selectedDateBindMappingChanged = selectedDateBindMappingChanged;
    }


    public void selectedDateKeyChanged(org.apache.pivot.wtk.CalendarButton arg0, java.lang.String arg1) {
        if (selectedDateKeyChanged != null) {
            selectedDateKeyChanged.call(arg0, arg1);
        }
    }

    public void selectedDateBindTypeChanged(org.apache.pivot.wtk.CalendarButton arg0, org.apache.pivot.wtk.BindType arg1) {
        if (selectedDateBindTypeChanged != null) {
            selectedDateBindTypeChanged.call(arg0, arg1);
        }
    }

    public void selectedDateBindMappingChanged(org.apache.pivot.wtk.CalendarButton arg0, org.apache.pivot.wtk.Calendar.SelectedDateBindMapping arg1) {
        if (selectedDateBindMappingChanged != null) {
            selectedDateBindMappingChanged.call(arg0, arg1);
        }
    }

}
