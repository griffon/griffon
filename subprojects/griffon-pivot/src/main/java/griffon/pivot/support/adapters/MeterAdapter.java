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
public class MeterAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.MeterListener {
    private CallableWithArgs<Void> textChanged;
    private CallableWithArgs<Void> orientationChanged;
    private CallableWithArgs<Void> percentageChanged;

    public CallableWithArgs<Void> getTextChanged() {
        return this.textChanged;
    }

    public CallableWithArgs<Void> getOrientationChanged() {
        return this.orientationChanged;
    }

    public CallableWithArgs<Void> getPercentageChanged() {
        return this.percentageChanged;
    }


    public void setTextChanged(CallableWithArgs<Void> textChanged) {
        this.textChanged = textChanged;
    }

    public void setOrientationChanged(CallableWithArgs<Void> orientationChanged) {
        this.orientationChanged = orientationChanged;
    }

    public void setPercentageChanged(CallableWithArgs<Void> percentageChanged) {
        this.percentageChanged = percentageChanged;
    }


    public void textChanged(org.apache.pivot.wtk.Meter arg0, java.lang.String arg1) {
        if (textChanged != null) {
            textChanged.call(arg0, arg1);
        }
    }

    public void orientationChanged(org.apache.pivot.wtk.Meter arg0) {
        if (orientationChanged != null) {
            orientationChanged.call(arg0);
        }
    }

    public void percentageChanged(org.apache.pivot.wtk.Meter arg0, double arg1) {
        if (percentageChanged != null) {
            percentageChanged.call(arg0, arg1);
        }
    }

}
