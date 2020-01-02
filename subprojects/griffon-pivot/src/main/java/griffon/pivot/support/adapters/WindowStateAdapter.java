/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
public class WindowStateAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.WindowStateListener {
    private CallableWithArgs<Vote> previewWindowOpen;
    private CallableWithArgs<Void> windowOpenVetoed;
    private CallableWithArgs<Vote> previewWindowClose;
    private CallableWithArgs<Void> windowCloseVetoed;
    private CallableWithArgs<Void> windowOpened;
    private CallableWithArgs<Void> windowClosed;

    public CallableWithArgs<Vote> getPreviewWindowOpen() {
        return this.previewWindowOpen;
    }

    public CallableWithArgs<Void> getWindowOpenVetoed() {
        return this.windowOpenVetoed;
    }

    public CallableWithArgs<Vote> getPreviewWindowClose() {
        return this.previewWindowClose;
    }

    public CallableWithArgs<Void> getWindowCloseVetoed() {
        return this.windowCloseVetoed;
    }

    public CallableWithArgs<Void> getWindowOpened() {
        return this.windowOpened;
    }

    public CallableWithArgs<Void> getWindowClosed() {
        return this.windowClosed;
    }


    public void setPreviewWindowOpen(CallableWithArgs<Vote> previewWindowOpen) {
        this.previewWindowOpen = previewWindowOpen;
    }

    public void setWindowOpenVetoed(CallableWithArgs<Void> windowOpenVetoed) {
        this.windowOpenVetoed = windowOpenVetoed;
    }

    public void setPreviewWindowClose(CallableWithArgs<Vote> previewWindowClose) {
        this.previewWindowClose = previewWindowClose;
    }

    public void setWindowCloseVetoed(CallableWithArgs<Void> windowCloseVetoed) {
        this.windowCloseVetoed = windowCloseVetoed;
    }

    public void setWindowOpened(CallableWithArgs<Void> windowOpened) {
        this.windowOpened = windowOpened;
    }

    public void setWindowClosed(CallableWithArgs<Void> windowClosed) {
        this.windowClosed = windowClosed;
    }


    public org.apache.pivot.util.Vote previewWindowOpen(org.apache.pivot.wtk.Window arg0) {
        if (previewWindowOpen != null) {
            return previewWindowOpen.call(arg0);
        }
        return Vote.APPROVE;
    }

    public void windowOpenVetoed(org.apache.pivot.wtk.Window arg0, org.apache.pivot.util.Vote arg1) {
        if (windowOpenVetoed != null) {
            windowOpenVetoed.call(arg0, arg1);
        }
    }

    public org.apache.pivot.util.Vote previewWindowClose(org.apache.pivot.wtk.Window arg0) {
        if (previewWindowClose != null) {
            return previewWindowClose.call(arg0);
        }
        return Vote.APPROVE;
    }

    public void windowCloseVetoed(org.apache.pivot.wtk.Window arg0, org.apache.pivot.util.Vote arg1) {
        if (windowCloseVetoed != null) {
            windowCloseVetoed.call(arg0, arg1);
        }
    }

    public void windowOpened(org.apache.pivot.wtk.Window arg0) {
        if (windowOpened != null) {
            windowOpened.call(arg0);
        }
    }

    public void windowClosed(org.apache.pivot.wtk.Window arg0, org.apache.pivot.wtk.Display arg1, org.apache.pivot.wtk.Window arg2) {
        if (windowClosed != null) {
            windowClosed.call(arg0, arg1, arg2);
        }
    }

}
