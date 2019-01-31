/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
public class SliderValueAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SliderValueListener {
    private CallableWithArgs<Void> valueChanged;

    public CallableWithArgs<Void> getValueChanged() {
        return this.valueChanged;
    }


    public void setValueChanged(CallableWithArgs<Void> valueChanged) {
        this.valueChanged = valueChanged;
    }


    public void valueChanged(org.apache.pivot.wtk.Slider arg0, int arg1) {
        if (valueChanged != null) {
            valueChanged.call(arg0, arg1);
        }
    }

}
