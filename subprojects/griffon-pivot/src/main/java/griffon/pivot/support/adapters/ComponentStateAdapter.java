/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
public class ComponentStateAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentStateListener {
    private CallableWithArgs<?> enabledChanged;
    private CallableWithArgs<?> focusedChanged;

    public CallableWithArgs<?> getEnabledChanged() {
        return this.enabledChanged;
    }

    public CallableWithArgs<?> getFocusedChanged() {
        return this.focusedChanged;
    }


    public void setEnabledChanged(CallableWithArgs<?> enabledChanged) {
        this.enabledChanged = enabledChanged;
    }

    public void setFocusedChanged(CallableWithArgs<?> focusedChanged) {
        this.focusedChanged = focusedChanged;
    }


    public void enabledChanged(org.apache.pivot.wtk.Component arg0) {
        if (enabledChanged != null) {
            enabledChanged.call(arg0);
        }
    }

    public void focusedChanged(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.Component arg1) {
        if (focusedChanged != null) {
            focusedChanged.call(arg0, arg1);
        }
    }

}
