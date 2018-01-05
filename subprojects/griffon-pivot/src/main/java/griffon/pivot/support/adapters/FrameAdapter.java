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
public class FrameAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.FrameListener {
    private CallableWithArgs<Void> menuBarChanged;

    public CallableWithArgs<Void> getMenuBarChanged() {
        return this.menuBarChanged;
    }


    public void setMenuBarChanged(CallableWithArgs<Void> menuBarChanged) {
        this.menuBarChanged = menuBarChanged;
    }


    public void menuBarChanged(org.apache.pivot.wtk.Frame arg0, org.apache.pivot.wtk.MenuBar arg1) {
        if (menuBarChanged != null) {
            menuBarChanged.call(arg0, arg1);
        }
    }

}
