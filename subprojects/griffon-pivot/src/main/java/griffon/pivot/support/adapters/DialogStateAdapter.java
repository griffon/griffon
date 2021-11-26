/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
public class DialogStateAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.DialogStateListener {
    private CallableWithArgs<Vote> previewDialogClose;
    private CallableWithArgs<Void> dialogCloseVetoed;
    private CallableWithArgs<Void> dialogClosed;

    public CallableWithArgs<Vote> getPreviewDialogClose() {
        return this.previewDialogClose;
    }

    public CallableWithArgs<Void> getDialogCloseVetoed() {
        return this.dialogCloseVetoed;
    }

    public CallableWithArgs<Void> getDialogClosed() {
        return this.dialogClosed;
    }


    public void setPreviewDialogClose(CallableWithArgs<Vote> previewDialogClose) {
        this.previewDialogClose = previewDialogClose;
    }

    public void setDialogCloseVetoed(CallableWithArgs<Void> dialogCloseVetoed) {
        this.dialogCloseVetoed = dialogCloseVetoed;
    }

    public void setDialogClosed(CallableWithArgs<Void> dialogClosed) {
        this.dialogClosed = dialogClosed;
    }


    public org.apache.pivot.util.Vote previewDialogClose(org.apache.pivot.wtk.Dialog arg0, boolean arg1) {
        if (previewDialogClose != null) {
            return previewDialogClose.call(arg0, arg1);
        }
        return Vote.APPROVE;
    }

    public void dialogCloseVetoed(org.apache.pivot.wtk.Dialog arg0, org.apache.pivot.util.Vote arg1) {
        if (dialogCloseVetoed != null) {
            dialogCloseVetoed.call(arg0, arg1);
        }
    }

    public void dialogClosed(org.apache.pivot.wtk.Dialog arg0, boolean arg1) {
        if (dialogClosed != null) {
            dialogClosed.call(arg0, arg1);
        }
    }

}
