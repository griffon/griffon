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
public class TextInputAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TextInputListener {
    private CallableWithArgs<?> textValidChanged;
    private CallableWithArgs<?> textSizeChanged;
    private CallableWithArgs<?> maximumLengthChanged;
    private CallableWithArgs<?> passwordChanged;
    private CallableWithArgs<?> promptChanged;
    private CallableWithArgs<?> textValidatorChanged;
    private CallableWithArgs<?> strictValidationChanged;
    private CallableWithArgs<?> editableChanged;

    public CallableWithArgs<?> getTextValidChanged() {
        return this.textValidChanged;
    }

    public CallableWithArgs<?> getTextSizeChanged() {
        return this.textSizeChanged;
    }

    public CallableWithArgs<?> getMaximumLengthChanged() {
        return this.maximumLengthChanged;
    }

    public CallableWithArgs<?> getPasswordChanged() {
        return this.passwordChanged;
    }

    public CallableWithArgs<?> getPromptChanged() {
        return this.promptChanged;
    }

    public CallableWithArgs<?> getTextValidatorChanged() {
        return this.textValidatorChanged;
    }

    public CallableWithArgs<?> getStrictValidationChanged() {
        return this.strictValidationChanged;
    }

    public CallableWithArgs<?> getEditableChanged() {
        return this.editableChanged;
    }


    public void setTextValidChanged(CallableWithArgs<?> textValidChanged) {
        this.textValidChanged = textValidChanged;
    }

    public void setTextSizeChanged(CallableWithArgs<?> textSizeChanged) {
        this.textSizeChanged = textSizeChanged;
    }

    public void setMaximumLengthChanged(CallableWithArgs<?> maximumLengthChanged) {
        this.maximumLengthChanged = maximumLengthChanged;
    }

    public void setPasswordChanged(CallableWithArgs<?> passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public void setPromptChanged(CallableWithArgs<?> promptChanged) {
        this.promptChanged = promptChanged;
    }

    public void setTextValidatorChanged(CallableWithArgs<?> textValidatorChanged) {
        this.textValidatorChanged = textValidatorChanged;
    }

    public void setStrictValidationChanged(CallableWithArgs<?> strictValidationChanged) {
        this.strictValidationChanged = strictValidationChanged;
    }

    public void setEditableChanged(CallableWithArgs<?> editableChanged) {
        this.editableChanged = editableChanged;
    }


    public void textValidChanged(org.apache.pivot.wtk.TextInput arg0) {
        if (textValidChanged != null) {
            textValidChanged.call(arg0);
        }
    }

    public void textSizeChanged(org.apache.pivot.wtk.TextInput arg0, int arg1) {
        if (textSizeChanged != null) {
            textSizeChanged.call(arg0, arg1);
        }
    }

    public void maximumLengthChanged(org.apache.pivot.wtk.TextInput arg0, int arg1) {
        if (maximumLengthChanged != null) {
            maximumLengthChanged.call(arg0, arg1);
        }
    }

    public void passwordChanged(org.apache.pivot.wtk.TextInput arg0) {
        if (passwordChanged != null) {
            passwordChanged.call(arg0);
        }
    }

    public void promptChanged(org.apache.pivot.wtk.TextInput arg0, java.lang.String arg1) {
        if (promptChanged != null) {
            promptChanged.call(arg0, arg1);
        }
    }

    public void textValidatorChanged(org.apache.pivot.wtk.TextInput arg0, org.apache.pivot.wtk.validation.Validator arg1) {
        if (textValidatorChanged != null) {
            textValidatorChanged.call(arg0, arg1);
        }
    }

    public void strictValidationChanged(org.apache.pivot.wtk.TextInput arg0) {
        if (strictValidationChanged != null) {
            strictValidationChanged.call(arg0);
        }
    }

    public void editableChanged(org.apache.pivot.wtk.TextInput arg0) {
        if (editableChanged != null) {
            editableChanged.call(arg0);
        }
    }

}
