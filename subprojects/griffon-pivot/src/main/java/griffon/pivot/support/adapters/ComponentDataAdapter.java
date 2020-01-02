/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
public class ComponentDataAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentDataListener {
    private CallableWithArgs<Void> valueAdded;
    private CallableWithArgs<Void> valueUpdated;
    private CallableWithArgs<Void> valueRemoved;

    public CallableWithArgs<Void> getValueAdded() {
        return this.valueAdded;
    }

    public CallableWithArgs<Void> getValueUpdated() {
        return this.valueUpdated;
    }

    public CallableWithArgs<Void> getValueRemoved() {
        return this.valueRemoved;
    }


    public void setValueAdded(CallableWithArgs<Void> valueAdded) {
        this.valueAdded = valueAdded;
    }

    public void setValueUpdated(CallableWithArgs<Void> valueUpdated) {
        this.valueUpdated = valueUpdated;
    }

    public void setValueRemoved(CallableWithArgs<Void> valueRemoved) {
        this.valueRemoved = valueRemoved;
    }


    public void valueAdded(org.apache.pivot.wtk.Component arg0, java.lang.String arg1) {
        if (valueAdded != null) {
            valueAdded.call(arg0, arg1);
        }
    }

    public void valueUpdated(org.apache.pivot.wtk.Component arg0, java.lang.String arg1, java.lang.Object arg2) {
        if (valueUpdated != null) {
            valueUpdated.call(arg0, arg1, arg2);
        }
    }

    public void valueRemoved(org.apache.pivot.wtk.Component arg0, java.lang.String arg1, java.lang.Object arg2) {
        if (valueRemoved != null) {
            valueRemoved.call(arg0, arg1, arg2);
        }
    }

}
