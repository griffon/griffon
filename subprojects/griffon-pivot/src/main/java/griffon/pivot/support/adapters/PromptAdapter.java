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
public class PromptAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.PromptListener {
    private CallableWithArgs<?> messageTypeChanged;
    private CallableWithArgs<?> messageChanged;
    private CallableWithArgs<?> bodyChanged;
    private CallableWithArgs<?> optionInserted;
    private CallableWithArgs<?> optionsRemoved;
    private CallableWithArgs<?> selectedOptionChanged;

    public CallableWithArgs<?> getMessageTypeChanged() {
        return this.messageTypeChanged;
    }

    public CallableWithArgs<?> getMessageChanged() {
        return this.messageChanged;
    }

    public CallableWithArgs<?> getBodyChanged() {
        return this.bodyChanged;
    }

    public CallableWithArgs<?> getOptionInserted() {
        return this.optionInserted;
    }

    public CallableWithArgs<?> getOptionsRemoved() {
        return this.optionsRemoved;
    }

    public CallableWithArgs<?> getSelectedOptionChanged() {
        return this.selectedOptionChanged;
    }


    public void setMessageTypeChanged(CallableWithArgs<?> messageTypeChanged) {
        this.messageTypeChanged = messageTypeChanged;
    }

    public void setMessageChanged(CallableWithArgs<?> messageChanged) {
        this.messageChanged = messageChanged;
    }

    public void setBodyChanged(CallableWithArgs<?> bodyChanged) {
        this.bodyChanged = bodyChanged;
    }

    public void setOptionInserted(CallableWithArgs<?> optionInserted) {
        this.optionInserted = optionInserted;
    }

    public void setOptionsRemoved(CallableWithArgs<?> optionsRemoved) {
        this.optionsRemoved = optionsRemoved;
    }

    public void setSelectedOptionChanged(CallableWithArgs<?> selectedOptionChanged) {
        this.selectedOptionChanged = selectedOptionChanged;
    }


    public void messageTypeChanged(org.apache.pivot.wtk.Prompt arg0, org.apache.pivot.wtk.MessageType arg1) {
        if (messageTypeChanged != null) {
            messageTypeChanged.call(arg0, arg1);
        }
    }

    public void messageChanged(org.apache.pivot.wtk.Prompt arg0, java.lang.String arg1) {
        if (messageChanged != null) {
            messageChanged.call(arg0, arg1);
        }
    }

    public void bodyChanged(org.apache.pivot.wtk.Prompt arg0, org.apache.pivot.wtk.Component arg1) {
        if (bodyChanged != null) {
            bodyChanged.call(arg0, arg1);
        }
    }

    public void optionInserted(org.apache.pivot.wtk.Prompt arg0, int arg1) {
        if (optionInserted != null) {
            optionInserted.call(arg0, arg1);
        }
    }

    public void optionsRemoved(org.apache.pivot.wtk.Prompt arg0, int arg1, org.apache.pivot.collections.Sequence arg2) {
        if (optionsRemoved != null) {
            optionsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void selectedOptionChanged(org.apache.pivot.wtk.Prompt arg0, int arg1) {
        if (selectedOptionChanged != null) {
            selectedOptionChanged.call(arg0, arg1);
        }
    }

}
