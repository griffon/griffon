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
public class CalendarSelectionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.CalendarSelectionListener {
    private CallableWithArgs<Void> selectedDateChanged;

    public CallableWithArgs<Void> getSelectedDateChanged() {
        return this.selectedDateChanged;
    }


    public void setSelectedDateChanged(CallableWithArgs<Void> selectedDateChanged) {
        this.selectedDateChanged = selectedDateChanged;
    }


    public void selectedDateChanged(org.apache.pivot.wtk.Calendar arg0, org.apache.pivot.util.CalendarDate arg1) {
        if (selectedDateChanged != null) {
            selectedDateChanged.call(arg0, arg1);
        }
    }

}
