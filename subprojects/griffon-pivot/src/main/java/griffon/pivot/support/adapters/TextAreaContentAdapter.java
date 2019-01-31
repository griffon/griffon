/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
import org.apache.pivot.wtk.TextArea;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TextAreaContentAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TextAreaContentListener {
    private CallableWithArgs<Void> paragraphInserted;
    private CallableWithArgs<Void> paragraphsRemoved;
    private CallableWithArgs<Void> textChanged;

    public CallableWithArgs<Void> getParagraphInserted() {
        return this.paragraphInserted;
    }

    public CallableWithArgs<Void> getParagraphsRemoved() {
        return this.paragraphsRemoved;
    }

    public CallableWithArgs<Void> getTextChanged() {
        return this.textChanged;
    }


    public void setParagraphInserted(CallableWithArgs<Void> paragraphInserted) {
        this.paragraphInserted = paragraphInserted;
    }

    public void setParagraphsRemoved(CallableWithArgs<Void> paragraphsRemoved) {
        this.paragraphsRemoved = paragraphsRemoved;
    }

    public void setTextChanged(CallableWithArgs<Void> textChanged) {
        this.textChanged = textChanged;
    }


    public void paragraphInserted(org.apache.pivot.wtk.TextArea arg0, int arg1) {
        if (paragraphInserted != null) {
            paragraphInserted.call(arg0, arg1);
        }
    }

    public void paragraphsRemoved(org.apache.pivot.wtk.TextArea arg0, int arg1, org.apache.pivot.collections.Sequence<TextArea.Paragraph> arg2) {
        if (paragraphsRemoved != null) {
            paragraphsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void textChanged(org.apache.pivot.wtk.TextArea arg0) {
        if (textChanged != null) {
            textChanged.call(arg0);
        }
    }

}
