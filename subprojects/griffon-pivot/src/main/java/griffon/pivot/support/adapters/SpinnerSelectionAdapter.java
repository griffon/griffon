/*
 * Copyright 2008-2014 the original author or authors.
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
public class SpinnerSelectionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SpinnerSelectionListener {
    private CallableWithArgs<Void> selectedIndexChanged;
    private CallableWithArgs<Void> selectedItemChanged;

    public CallableWithArgs<Void> getSelectedIndexChanged() {
        return this.selectedIndexChanged;
    }

    public CallableWithArgs<Void> getSelectedItemChanged() {
        return this.selectedItemChanged;
    }


    public void setSelectedIndexChanged(CallableWithArgs<Void> selectedIndexChanged) {
        this.selectedIndexChanged = selectedIndexChanged;
    }

    public void setSelectedItemChanged(CallableWithArgs<Void> selectedItemChanged) {
        this.selectedItemChanged = selectedItemChanged;
    }


    public void selectedIndexChanged(org.apache.pivot.wtk.Spinner arg0, int arg1) {
        if (selectedIndexChanged != null) {
            selectedIndexChanged.call(arg0, arg1);
        }
    }

    public void selectedItemChanged(org.apache.pivot.wtk.Spinner arg0, java.lang.Object arg1) {
        if (selectedItemChanged != null) {
            selectedItemChanged.call(arg0, arg1);
        }
    }

}
