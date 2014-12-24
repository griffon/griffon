/*
 * Copyright 2008-2015 the original author or authors.
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
public class MenuPopupStateAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.MenuPopupStateListener {
    private CallableWithArgs<Vote> previewMenuPopupClose;
    private CallableWithArgs<Void> menuPopupClosed;
    private CallableWithArgs<Void> menuPopupCloseVetoed;

    public CallableWithArgs<Vote> getPreviewMenuPopupClose() {
        return this.previewMenuPopupClose;
    }

    public CallableWithArgs<Void> getMenuPopupClosed() {
        return this.menuPopupClosed;
    }

    public CallableWithArgs<Void> getMenuPopupCloseVetoed() {
        return this.menuPopupCloseVetoed;
    }


    public void setPreviewMenuPopupClose(CallableWithArgs<Vote> previewMenuPopupClose) {
        this.previewMenuPopupClose = previewMenuPopupClose;
    }

    public void setMenuPopupClosed(CallableWithArgs<Void> menuPopupClosed) {
        this.menuPopupClosed = menuPopupClosed;
    }

    public void setMenuPopupCloseVetoed(CallableWithArgs<Void> menuPopupCloseVetoed) {
        this.menuPopupCloseVetoed = menuPopupCloseVetoed;
    }


    public org.apache.pivot.util.Vote previewMenuPopupClose(org.apache.pivot.wtk.MenuPopup arg0, boolean arg1) {
        if (previewMenuPopupClose != null) {
            return previewMenuPopupClose.call(arg0, arg1);
        }
        return Vote.APPROVE;
    }

    public void menuPopupClosed(org.apache.pivot.wtk.MenuPopup arg0) {
        if (menuPopupClosed != null) {
            menuPopupClosed.call(arg0);
        }
    }

    public void menuPopupCloseVetoed(org.apache.pivot.wtk.MenuPopup arg0, org.apache.pivot.util.Vote arg1) {
        if (menuPopupCloseVetoed != null) {
            menuPopupCloseVetoed.call(arg0, arg1);
        }
    }

}
