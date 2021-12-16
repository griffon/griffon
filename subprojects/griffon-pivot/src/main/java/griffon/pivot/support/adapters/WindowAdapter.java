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
import org.apache.pivot.wtk.media.Image;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class WindowAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.WindowListener {
    private CallableWithArgs<Void> iconAdded;
    private CallableWithArgs<Void> iconInserted;
    private CallableWithArgs<Void> iconsRemoved;
    private CallableWithArgs<Void> titleChanged;
    private CallableWithArgs<Void> contentChanged;
    private CallableWithArgs<Void> activeChanged;
    private CallableWithArgs<Void> maximizedChanged;

    public CallableWithArgs<Void> getIconAdded() {
        return this.iconAdded;
    }

    public CallableWithArgs<Void> getIconInserted() {
        return this.iconInserted;
    }

    public CallableWithArgs<Void> getIconsRemoved() {
        return this.iconsRemoved;
    }

    public CallableWithArgs<Void> getTitleChanged() {
        return this.titleChanged;
    }

    public CallableWithArgs<Void> getContentChanged() {
        return this.contentChanged;
    }

    public CallableWithArgs<Void> getActiveChanged() {
        return this.activeChanged;
    }

    public CallableWithArgs<Void> getMaximizedChanged() {
        return this.maximizedChanged;
    }


    public void setIconAdded(CallableWithArgs<Void> iconAdded) {
        this.iconAdded = iconAdded;
    }

    public void setIconInserted(CallableWithArgs<Void> iconInserted) {
        this.iconInserted = iconInserted;
    }

    public void setIconsRemoved(CallableWithArgs<Void> iconsRemoved) {
        this.iconsRemoved = iconsRemoved;
    }

    public void setTitleChanged(CallableWithArgs<Void> titleChanged) {
        this.titleChanged = titleChanged;
    }

    public void setContentChanged(CallableWithArgs<Void> contentChanged) {
        this.contentChanged = contentChanged;
    }

    public void setActiveChanged(CallableWithArgs<Void> activeChanged) {
        this.activeChanged = activeChanged;
    }

    public void setMaximizedChanged(CallableWithArgs<Void> maximizedChanged) {
        this.maximizedChanged = maximizedChanged;
    }


    public void iconAdded(org.apache.pivot.wtk.Window arg0, org.apache.pivot.wtk.media.Image arg1) {
        if (iconAdded != null) {
            iconAdded.call(arg0, arg1);
        }
    }

    public void iconInserted(org.apache.pivot.wtk.Window arg0, org.apache.pivot.wtk.media.Image arg1, int arg2) {
        if (iconInserted != null) {
            iconInserted.call(arg0, arg1, arg2);
        }
    }

    public void iconsRemoved(org.apache.pivot.wtk.Window arg0, int arg1, org.apache.pivot.collections.Sequence<Image> arg2) {
        if (iconsRemoved != null) {
            iconsRemoved.call(arg0, arg1, arg2);
        }
    }

    public void titleChanged(org.apache.pivot.wtk.Window arg0, java.lang.String arg1) {
        if (titleChanged != null) {
            titleChanged.call(arg0, arg1);
        }
    }

    public void contentChanged(org.apache.pivot.wtk.Window arg0, org.apache.pivot.wtk.Component arg1) {
        if (contentChanged != null) {
            contentChanged.call(arg0, arg1);
        }
    }

    public void activeChanged(org.apache.pivot.wtk.Window arg0, org.apache.pivot.wtk.Window arg1) {
        if (activeChanged != null) {
            activeChanged.call(arg0, arg1);
        }
    }

    public void maximizedChanged(org.apache.pivot.wtk.Window arg0) {
        if (maximizedChanged != null) {
            maximizedChanged.call(arg0);
        }
    }

}
