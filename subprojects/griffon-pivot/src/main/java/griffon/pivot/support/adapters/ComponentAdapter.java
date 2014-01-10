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
public class ComponentAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentListener {
    private CallableWithArgs<?> tooltipTextChanged;
    private CallableWithArgs<?> parentChanged;
    private CallableWithArgs<?> sizeChanged;
    private CallableWithArgs<?> preferredSizeChanged;
    private CallableWithArgs<?> widthLimitsChanged;
    private CallableWithArgs<?> heightLimitsChanged;
    private CallableWithArgs<?> locationChanged;
    private CallableWithArgs<?> visibleChanged;
    private CallableWithArgs<?> cursorChanged;
    private CallableWithArgs<?> tooltipDelayChanged;
    private CallableWithArgs<?> dragSourceChanged;
    private CallableWithArgs<?> dropTargetChanged;
    private CallableWithArgs<?> menuHandlerChanged;
    private CallableWithArgs<?> nameChanged;

    public CallableWithArgs<?> getTooltipTextChanged() {
        return this.tooltipTextChanged;
    }

    public CallableWithArgs<?> getParentChanged() {
        return this.parentChanged;
    }

    public CallableWithArgs<?> getSizeChanged() {
        return this.sizeChanged;
    }

    public CallableWithArgs<?> getPreferredSizeChanged() {
        return this.preferredSizeChanged;
    }

    public CallableWithArgs<?> getWidthLimitsChanged() {
        return this.widthLimitsChanged;
    }

    public CallableWithArgs<?> getHeightLimitsChanged() {
        return this.heightLimitsChanged;
    }

    public CallableWithArgs<?> getLocationChanged() {
        return this.locationChanged;
    }

    public CallableWithArgs<?> getVisibleChanged() {
        return this.visibleChanged;
    }

    public CallableWithArgs<?> getCursorChanged() {
        return this.cursorChanged;
    }

    public CallableWithArgs<?> getTooltipDelayChanged() {
        return this.tooltipDelayChanged;
    }

    public CallableWithArgs<?> getDragSourceChanged() {
        return this.dragSourceChanged;
    }

    public CallableWithArgs<?> getDropTargetChanged() {
        return this.dropTargetChanged;
    }

    public CallableWithArgs<?> getMenuHandlerChanged() {
        return this.menuHandlerChanged;
    }

    public CallableWithArgs<?> getNameChanged() {
        return this.nameChanged;
    }


    public void setTooltipTextChanged(CallableWithArgs<?> tooltipTextChanged) {
        this.tooltipTextChanged = tooltipTextChanged;
    }

    public void setParentChanged(CallableWithArgs<?> parentChanged) {
        this.parentChanged = parentChanged;
    }

    public void setSizeChanged(CallableWithArgs<?> sizeChanged) {
        this.sizeChanged = sizeChanged;
    }

    public void setPreferredSizeChanged(CallableWithArgs<?> preferredSizeChanged) {
        this.preferredSizeChanged = preferredSizeChanged;
    }

    public void setWidthLimitsChanged(CallableWithArgs<?> widthLimitsChanged) {
        this.widthLimitsChanged = widthLimitsChanged;
    }

    public void setHeightLimitsChanged(CallableWithArgs<?> heightLimitsChanged) {
        this.heightLimitsChanged = heightLimitsChanged;
    }

    public void setLocationChanged(CallableWithArgs<?> locationChanged) {
        this.locationChanged = locationChanged;
    }

    public void setVisibleChanged(CallableWithArgs<?> visibleChanged) {
        this.visibleChanged = visibleChanged;
    }

    public void setCursorChanged(CallableWithArgs<?> cursorChanged) {
        this.cursorChanged = cursorChanged;
    }

    public void setTooltipDelayChanged(CallableWithArgs<?> tooltipDelayChanged) {
        this.tooltipDelayChanged = tooltipDelayChanged;
    }

    public void setDragSourceChanged(CallableWithArgs<?> dragSourceChanged) {
        this.dragSourceChanged = dragSourceChanged;
    }

    public void setDropTargetChanged(CallableWithArgs<?> dropTargetChanged) {
        this.dropTargetChanged = dropTargetChanged;
    }

    public void setMenuHandlerChanged(CallableWithArgs<?> menuHandlerChanged) {
        this.menuHandlerChanged = menuHandlerChanged;
    }

    public void setNameChanged(CallableWithArgs<?> nameChanged) {
        this.nameChanged = nameChanged;
    }


    public void tooltipTextChanged(org.apache.pivot.wtk.Component arg0, java.lang.String arg1) {
        if (tooltipTextChanged != null) {
            tooltipTextChanged.call(arg0, arg1);
        }
    }

    public void parentChanged(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.Container arg1) {
        if (parentChanged != null) {
            parentChanged.call(arg0, arg1);
        }
    }

    public void sizeChanged(org.apache.pivot.wtk.Component arg0, int arg1, int arg2) {
        if (sizeChanged != null) {
            sizeChanged.call(arg0, arg1, arg2);
        }
    }

    public void preferredSizeChanged(org.apache.pivot.wtk.Component arg0, int arg1, int arg2) {
        if (preferredSizeChanged != null) {
            preferredSizeChanged.call(arg0, arg1, arg2);
        }
    }

    public void widthLimitsChanged(org.apache.pivot.wtk.Component arg0, int arg1, int arg2) {
        if (widthLimitsChanged != null) {
            widthLimitsChanged.call(arg0, arg1, arg2);
        }
    }

    public void heightLimitsChanged(org.apache.pivot.wtk.Component arg0, int arg1, int arg2) {
        if (heightLimitsChanged != null) {
            heightLimitsChanged.call(arg0, arg1, arg2);
        }
    }

    public void locationChanged(org.apache.pivot.wtk.Component arg0, int arg1, int arg2) {
        if (locationChanged != null) {
            locationChanged.call(arg0, arg1, arg2);
        }
    }

    public void visibleChanged(org.apache.pivot.wtk.Component arg0) {
        if (visibleChanged != null) {
            visibleChanged.call(arg0);
        }
    }

    public void cursorChanged(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.Cursor arg1) {
        if (cursorChanged != null) {
            cursorChanged.call(arg0, arg1);
        }
    }

    public void tooltipDelayChanged(org.apache.pivot.wtk.Component arg0, int arg1) {
        if (tooltipDelayChanged != null) {
            tooltipDelayChanged.call(arg0, arg1);
        }
    }

    public void dragSourceChanged(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.DragSource arg1) {
        if (dragSourceChanged != null) {
            dragSourceChanged.call(arg0, arg1);
        }
    }

    public void dropTargetChanged(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.DropTarget arg1) {
        if (dropTargetChanged != null) {
            dropTargetChanged.call(arg0, arg1);
        }
    }

    public void menuHandlerChanged(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.MenuHandler arg1) {
        if (menuHandlerChanged != null) {
            menuHandlerChanged.call(arg0, arg1);
        }
    }

    public void nameChanged(org.apache.pivot.wtk.Component arg0, java.lang.String arg1) {
        if (nameChanged != null) {
            nameChanged.call(arg0, arg1);
        }
    }

}
