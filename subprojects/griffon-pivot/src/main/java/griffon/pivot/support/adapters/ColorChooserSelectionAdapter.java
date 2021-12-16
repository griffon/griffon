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
public class ColorChooserSelectionAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ColorChooserSelectionListener {
    private CallableWithArgs<Void> selectedColorChanged;

    public CallableWithArgs<Void> getSelectedColorChanged() {
        return this.selectedColorChanged;
    }


    public void setSelectedColorChanged(CallableWithArgs<Void> selectedColorChanged) {
        this.selectedColorChanged = selectedColorChanged;
    }


    public void selectedColorChanged(org.apache.pivot.wtk.ColorChooser arg0, java.awt.Color arg1) {
        if (selectedColorChanged != null) {
            selectedColorChanged.call(arg0, arg1);
        }
    }

}
