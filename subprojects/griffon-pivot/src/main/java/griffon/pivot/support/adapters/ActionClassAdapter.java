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
public class ActionClassAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ActionClassListener {
    private CallableWithArgs<Void> actionAdded;
    private CallableWithArgs<Void> actionUpdated;
    private CallableWithArgs<Void> actionRemoved;

    public CallableWithArgs<Void> getActionAdded() {
        return this.actionAdded;
    }

    public CallableWithArgs<Void> getActionUpdated() {
        return this.actionUpdated;
    }

    public CallableWithArgs<Void> getActionRemoved() {
        return this.actionRemoved;
    }


    public void setActionAdded(CallableWithArgs<Void> actionAdded) {
        this.actionAdded = actionAdded;
    }

    public void setActionUpdated(CallableWithArgs<Void> actionUpdated) {
        this.actionUpdated = actionUpdated;
    }

    public void setActionRemoved(CallableWithArgs<Void> actionRemoved) {
        this.actionRemoved = actionRemoved;
    }


    public void actionAdded(java.lang.String arg0) {
        if (actionAdded != null) {
            actionAdded.call(arg0);
        }
    }

    public void actionUpdated(java.lang.String arg0, org.apache.pivot.wtk.Action arg1) {
        if (actionUpdated != null) {
            actionUpdated.call(arg0, arg1);
        }
    }

    public void actionRemoved(java.lang.String arg0, org.apache.pivot.wtk.Action arg1) {
        if (actionRemoved != null) {
            actionRemoved.call(arg0, arg1);
        }
    }

}
