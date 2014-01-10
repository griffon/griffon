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
public class FormAttributeAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.FormAttributeListener {
    private CallableWithArgs<Void> labelChanged;
    private CallableWithArgs<Void> requiredChanged;
    private CallableWithArgs<Void> flagChanged;

    public CallableWithArgs<Void> getLabelChanged() {
        return this.labelChanged;
    }

    public CallableWithArgs<Void> getRequiredChanged() {
        return this.requiredChanged;
    }

    public CallableWithArgs<Void> getFlagChanged() {
        return this.flagChanged;
    }


    public void setLabelChanged(CallableWithArgs<Void> labelChanged) {
        this.labelChanged = labelChanged;
    }

    public void setRequiredChanged(CallableWithArgs<Void> requiredChanged) {
        this.requiredChanged = requiredChanged;
    }

    public void setFlagChanged(CallableWithArgs<Void> flagChanged) {
        this.flagChanged = flagChanged;
    }


    public void labelChanged(org.apache.pivot.wtk.Form arg0, org.apache.pivot.wtk.Component arg1, java.lang.String arg2) {
        if (labelChanged != null) {
            labelChanged.call(arg0, arg1, arg2);
        }
    }

    public void requiredChanged(org.apache.pivot.wtk.Form arg0, org.apache.pivot.wtk.Component arg1) {
        if (requiredChanged != null) {
            requiredChanged.call(arg0, arg1);
        }
    }

    public void flagChanged(org.apache.pivot.wtk.Form arg0, org.apache.pivot.wtk.Component arg1, org.apache.pivot.wtk.Form.Flag arg2) {
        if (flagChanged != null) {
            flagChanged.call(arg0, arg1, arg2);
        }
    }

}
