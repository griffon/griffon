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
public class TextAreaAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TextAreaListener {
    private CallableWithArgs<Void> maximumLengthChanged;
    private CallableWithArgs<Void> editableChanged;

    public CallableWithArgs<Void> getMaximumLengthChanged() {
        return this.maximumLengthChanged;
    }

    public CallableWithArgs<Void> getEditableChanged() {
        return this.editableChanged;
    }


    public void setMaximumLengthChanged(CallableWithArgs<Void> maximumLengthChanged) {
        this.maximumLengthChanged = maximumLengthChanged;
    }

    public void setEditableChanged(CallableWithArgs<Void> editableChanged) {
        this.editableChanged = editableChanged;
    }


    public void maximumLengthChanged(org.apache.pivot.wtk.TextArea arg0, int arg1) {
        if (maximumLengthChanged != null) {
            maximumLengthChanged.call(arg0, arg1);
        }
    }

    public void editableChanged(org.apache.pivot.wtk.TextArea arg0) {
        if (editableChanged != null) {
            editableChanged.call(arg0);
        }
    }

}
