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
public class SpinnerAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SpinnerListener {
    private CallableWithArgs<Void> itemRendererChanged;
    private CallableWithArgs<Void> spinnerDataChanged;
    private CallableWithArgs<Void> circularChanged;

    public CallableWithArgs<Void> getItemRendererChanged() {
        return this.itemRendererChanged;
    }

    public CallableWithArgs<Void> getSpinnerDataChanged() {
        return this.spinnerDataChanged;
    }

    public CallableWithArgs<Void> getCircularChanged() {
        return this.circularChanged;
    }


    public void setItemRendererChanged(CallableWithArgs<Void> itemRendererChanged) {
        this.itemRendererChanged = itemRendererChanged;
    }

    public void setSpinnerDataChanged(CallableWithArgs<Void> spinnerDataChanged) {
        this.spinnerDataChanged = spinnerDataChanged;
    }

    public void setCircularChanged(CallableWithArgs<Void> circularChanged) {
        this.circularChanged = circularChanged;
    }


    public void itemRendererChanged(org.apache.pivot.wtk.Spinner arg0, org.apache.pivot.wtk.Spinner.ItemRenderer arg1) {
        if (itemRendererChanged != null) {
            itemRendererChanged.call(arg0, arg1);
        }
    }

    public void spinnerDataChanged(org.apache.pivot.wtk.Spinner arg0, org.apache.pivot.collections.List<?> arg1) {
        if (spinnerDataChanged != null) {
            spinnerDataChanged.call(arg0, arg1);
        }
    }

    public void circularChanged(org.apache.pivot.wtk.Spinner arg0) {
        if (circularChanged != null) {
            circularChanged.call(arg0);
        }
    }

}
