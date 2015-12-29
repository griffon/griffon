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
import org.apache.pivot.util.Vote;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TextInputContentAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.TextInputContentListener {
    private CallableWithArgs<Void> textChanged;
    private CallableWithArgs<Vote> previewInsertText;
    private CallableWithArgs<Void> textInserted;
    private CallableWithArgs<Void> insertTextVetoed;
    private CallableWithArgs<Vote> previewRemoveText;
    private CallableWithArgs<Void> textRemoved;
    private CallableWithArgs<Void> removeTextVetoed;

    public CallableWithArgs<Void> getTextChanged() {
        return this.textChanged;
    }

    public CallableWithArgs<Vote> getPreviewInsertText() {
        return this.previewInsertText;
    }

    public CallableWithArgs<Void> getTextInserted() {
        return this.textInserted;
    }

    public CallableWithArgs<Void> getInsertTextVetoed() {
        return this.insertTextVetoed;
    }

    public CallableWithArgs<Vote> getPreviewRemoveText() {
        return this.previewRemoveText;
    }

    public CallableWithArgs<Void> getTextRemoved() {
        return this.textRemoved;
    }

    public CallableWithArgs<Void> getRemoveTextVetoed() {
        return this.removeTextVetoed;
    }


    public void setTextChanged(CallableWithArgs<Void> textChanged) {
        this.textChanged = textChanged;
    }

    public void setPreviewInsertText(CallableWithArgs<Vote> previewInsertText) {
        this.previewInsertText = previewInsertText;
    }

    public void setTextInserted(CallableWithArgs<Void> textInserted) {
        this.textInserted = textInserted;
    }

    public void setInsertTextVetoed(CallableWithArgs<Void> insertTextVetoed) {
        this.insertTextVetoed = insertTextVetoed;
    }

    public void setPreviewRemoveText(CallableWithArgs<Vote> previewRemoveText) {
        this.previewRemoveText = previewRemoveText;
    }

    public void setTextRemoved(CallableWithArgs<Void> textRemoved) {
        this.textRemoved = textRemoved;
    }

    public void setRemoveTextVetoed(CallableWithArgs<Void> removeTextVetoed) {
        this.removeTextVetoed = removeTextVetoed;
    }


    public void textChanged(org.apache.pivot.wtk.TextInput arg0) {
        if (textChanged != null) {
            textChanged.call(arg0);
        }
    }

    public org.apache.pivot.util.Vote previewInsertText(org.apache.pivot.wtk.TextInput arg0, java.lang.CharSequence arg1, int arg2) {
        if (previewInsertText != null) {
            return previewInsertText.call(arg0, arg1, arg2);
        }
        return Vote.APPROVE;
    }

    public void textInserted(org.apache.pivot.wtk.TextInput arg0, int arg1, int arg2) {
        if (textInserted != null) {
            textInserted.call(arg0, arg1, arg2);
        }
    }

    public void insertTextVetoed(org.apache.pivot.wtk.TextInput arg0, org.apache.pivot.util.Vote arg1) {
        if (insertTextVetoed != null) {
            insertTextVetoed.call(arg0, arg1);
        }
    }

    public org.apache.pivot.util.Vote previewRemoveText(org.apache.pivot.wtk.TextInput arg0, int arg1, int arg2) {
        if (previewRemoveText != null) {
            return previewRemoveText.call(arg0, arg1, arg2);
        }
        return Vote.APPROVE;
    }

    public void textRemoved(org.apache.pivot.wtk.TextInput arg0, int arg1, int arg2) {
        if (textRemoved != null) {
            textRemoved.call(arg0, arg1, arg2);
        }
    }

    public void removeTextVetoed(org.apache.pivot.wtk.TextInput arg0, org.apache.pivot.util.Vote arg1) {
        if (removeTextVetoed != null) {
            removeTextVetoed.call(arg0, arg1);
        }
    }

}
