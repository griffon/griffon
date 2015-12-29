/*
 * Copyright 2008-2016 the original author or authors.
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
public class TextAreaBindingAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TextAreaBindingListener {
    private CallableWithArgs<Void> textKeyChanged;
    private CallableWithArgs<Void> textBindTypeChanged;
    private CallableWithArgs<Void> textBindMappingChanged;

    public CallableWithArgs<Void> getTextKeyChanged() {
        return this.textKeyChanged;
    }

    public CallableWithArgs<Void> getTextBindTypeChanged() {
        return this.textBindTypeChanged;
    }

    public CallableWithArgs<Void> getTextBindMappingChanged() {
        return this.textBindMappingChanged;
    }


    public void setTextKeyChanged(CallableWithArgs<Void> textKeyChanged) {
        this.textKeyChanged = textKeyChanged;
    }

    public void setTextBindTypeChanged(CallableWithArgs<Void> textBindTypeChanged) {
        this.textBindTypeChanged = textBindTypeChanged;
    }

    public void setTextBindMappingChanged(CallableWithArgs<Void> textBindMappingChanged) {
        this.textBindMappingChanged = textBindMappingChanged;
    }


    public void textKeyChanged(org.apache.pivot.wtk.TextArea arg0, java.lang.String arg1) {
        if (textKeyChanged != null) {
            textKeyChanged.call(arg0, arg1);
        }
    }

    public void textBindTypeChanged(org.apache.pivot.wtk.TextArea arg0, org.apache.pivot.wtk.BindType arg1) {
        if (textBindTypeChanged != null) {
            textBindTypeChanged.call(arg0, arg1);
        }
    }

    public void textBindMappingChanged(org.apache.pivot.wtk.TextArea arg0, org.apache.pivot.wtk.TextArea.TextBindMapping arg1) {
        if (textBindMappingChanged != null) {
            textBindMappingChanged.call(arg0, arg1);
        }
    }

}
