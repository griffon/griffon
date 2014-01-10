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
public class LabelBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.LabelBindingListener {
    private CallableWithArgs<?> textKeyChanged;
    private CallableWithArgs<?> textBindTypeChanged;
    private CallableWithArgs<?> textBindMappingChanged;

    public CallableWithArgs<?> getTextKeyChanged() {
        return this.textKeyChanged;
    }

    public CallableWithArgs<?> getTextBindTypeChanged() {
        return this.textBindTypeChanged;
    }

    public CallableWithArgs<?> getTextBindMappingChanged() {
        return this.textBindMappingChanged;
    }


    public void setTextKeyChanged(CallableWithArgs<?> textKeyChanged) {
        this.textKeyChanged = textKeyChanged;
    }

    public void setTextBindTypeChanged(CallableWithArgs<?> textBindTypeChanged) {
        this.textBindTypeChanged = textBindTypeChanged;
    }

    public void setTextBindMappingChanged(CallableWithArgs<?> textBindMappingChanged) {
        this.textBindMappingChanged = textBindMappingChanged;
    }


    public void textKeyChanged(org.apache.pivot.wtk.Label arg0, java.lang.String arg1) {
        if (textKeyChanged != null) {
            textKeyChanged.call(arg0, arg1);
        }
    }

    public void textBindTypeChanged(org.apache.pivot.wtk.Label arg0, org.apache.pivot.wtk.BindType arg1) {
        if (textBindTypeChanged != null) {
            textBindTypeChanged.call(arg0, arg1);
        }
    }

    public void textBindMappingChanged(org.apache.pivot.wtk.Label arg0, org.apache.pivot.wtk.Label.TextBindMapping arg1) {
        if (textBindMappingChanged != null) {
            textBindMappingChanged.call(arg0, arg1);
        }
    }

}
