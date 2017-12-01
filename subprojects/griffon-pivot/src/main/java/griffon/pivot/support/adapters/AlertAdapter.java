/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
public class AlertAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.AlertListener {
    private CallableWithArgs<Void> messageTypeChanged;
    private CallableWithArgs<Void> messageChanged;
    private CallableWithArgs<Void> bodyChanged;
    private CallableWithArgs<Void> optionInserted;
    private CallableWithArgs<Void> optionsRemoved;
    private CallableWithArgs<Void> selectedOptionChanged;

    public CallableWithArgs<Void> getMessageTypeChanged() {
        return this.messageTypeChanged;
    }

    public CallableWithArgs<Void> getMessageChanged() {
        return this.messageChanged;
    }

    public CallableWithArgs<Void> getBodyChanged() {
        return this.bodyChanged;
    }

    public CallableWithArgs<Void> getOptionInserted() {
        return this.optionInserted;
    }

    public CallableWithArgs<Void> getOptionsRemoved() {
        return this.optionsRemoved;
    }

    public CallableWithArgs<Void> getSelectedOptionChanged() {
        return this.selectedOptionChanged;
    }


    public void setMessageTypeChanged(CallableWithArgs<Void> messageTypeChanged) {
        this.messageTypeChanged = messageTypeChanged;
    }

    public void setMessageChanged(CallableWithArgs<Void> messageChanged) {
        this.messageChanged = messageChanged;
    }

    public void setBodyChanged(CallableWithArgs<Void> bodyChanged) {
        this.bodyChanged = bodyChanged;
    }

    public void setOptionInserted(CallableWithArgs<Void> optionInserted) {
        this.optionInserted = optionInserted;
    }

    public void setOptionsRemoved(CallableWithArgs<Void> optionsRemoved) {
        this.optionsRemoved = optionsRemoved;
    }

    public void setSelectedOptionChanged(CallableWithArgs<Void> selectedOptionChanged) {
        this.selectedOptionChanged = selectedOptionChanged;
    }


    public void messageTypeChanged(org.apache.pivot.wtk.Alert arg0, org.apache.pivot.wtk.MessageType arg1) {
        if (messageTypeChanged != null) {
            messageTypeChanged.call(arg0, arg1);
        }
    }

    public void messageChanged(org.apache.pivot.wtk.Alert arg0, java.lang.String arg1) {
        if (messageChanged != null) {
            messageChanged.call(arg0, arg1);
        }
    }

    public void bodyChanged(org.apache.pivot.wtk.Alert arg0, org.apache.pivot.wtk.Component arg1) {
        if (bodyChanged != null) {
            bodyChanged.call(arg0, arg1);
        }
    }

    public void optionInserted(org.apache.pivot.wtk.Alert arg0, int arg1) {
        if (optionInserted != null) {
            optionInserted.call(arg0, arg1);
        }
    }

    public void optionsRemoved(org.apache.pivot.wtk.Alert arg0, int arg1, org.apache.pivot.collections.Sequence<?> arg2) {
        if (optionsRemoved != null) {
            optionsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void selectedOptionChanged(org.apache.pivot.wtk.Alert arg0, int arg1) {
        if (selectedOptionChanged != null) {
            selectedOptionChanged.call(arg0, arg1);
        }
    }

}
