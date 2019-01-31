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
import org.apache.pivot.util.Vote;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SheetStateAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SheetStateListener {
    private CallableWithArgs<Vote> previewSheetClose;
    private CallableWithArgs<Void> sheetCloseVetoed;
    private CallableWithArgs<Void> sheetClosed;

    public CallableWithArgs<Vote> getPreviewSheetClose() {
        return this.previewSheetClose;
    }

    public CallableWithArgs<Void> getSheetCloseVetoed() {
        return this.sheetCloseVetoed;
    }

    public CallableWithArgs<Void> getSheetClosed() {
        return this.sheetClosed;
    }


    public void setPreviewSheetClose(CallableWithArgs<Vote> previewSheetClose) {
        this.previewSheetClose = previewSheetClose;
    }

    public void setSheetCloseVetoed(CallableWithArgs<Void> sheetCloseVetoed) {
        this.sheetCloseVetoed = sheetCloseVetoed;
    }

    public void setSheetClosed(CallableWithArgs<Void> sheetClosed) {
        this.sheetClosed = sheetClosed;
    }


    public org.apache.pivot.util.Vote previewSheetClose(org.apache.pivot.wtk.Sheet arg0, boolean arg1) {
        if (previewSheetClose != null) {
            return previewSheetClose.call(arg0, arg1);
        }
        return Vote.APPROVE;
    }

    public void sheetCloseVetoed(org.apache.pivot.wtk.Sheet arg0, org.apache.pivot.util.Vote arg1) {
        if (sheetCloseVetoed != null) {
            sheetCloseVetoed.call(arg0, arg1);
        }
    }

    public void sheetClosed(org.apache.pivot.wtk.Sheet arg0) {
        if (sheetClosed != null) {
            sheetClosed.call(arg0);
        }
    }

}
