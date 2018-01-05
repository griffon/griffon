/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
public class LabelAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.LabelListener {
    private CallableWithArgs<Void> textChanged;
    private CallableWithArgs<Void> maximumLengthChanged;

    public CallableWithArgs<Void> getTextChanged() {
        return this.textChanged;
    }

    public void setTextChanged(CallableWithArgs<Void> textChanged) {
        this.textChanged = textChanged;
    }

    public CallableWithArgs<Void> getMaximumLengthChanged() {
        return maximumLengthChanged;
    }

    public void setMaximumLengthChanged(CallableWithArgs<Void> maximumLengthChanged) {
        this.maximumLengthChanged = maximumLengthChanged;
    }

    public void textChanged(org.apache.pivot.wtk.Label arg0, java.lang.String arg1) {
        if (textChanged != null) {
            textChanged.call(arg0, arg1);
        }
    }

    public void maximumLengthChanged(org.apache.pivot.wtk.Label arg0, int arg1) {
        if (maximumLengthChanged != null) {
            maximumLengthChanged.call(arg0, arg1);
        }
    }
}
