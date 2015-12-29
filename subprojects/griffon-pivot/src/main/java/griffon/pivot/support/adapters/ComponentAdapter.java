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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ComponentAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentListener {
    private CallableWithArgs<Void> tooltipTextChanged;
    private CallableWithArgs<Void> parentChanged;
    private CallableWithArgs<Void> sizeChanged;
    private CallableWithArgs<Void> preferredSizeChanged;
    private CallableWithArgs<Void> widthLimitsChanged;
    private CallableWithArgs<Void> heightLimitsChanged;
    private CallableWithArgs<Void> locationChanged;
    private CallableWithArgs<Void> visibleChanged;
    private CallableWithArgs<Void> cursorChanged;
    private CallableWithArgs<Void> tooltipDelayChanged;
    private CallableWithArgs<Void> dragSourceChanged;
    private CallableWithArgs<Void> dropTargetChanged;
    private CallableWithArgs<Void> menuHandlerChanged;
    private CallableWithArgs<Void> nameChanged;

    public CallableWithArgs<Void> getTooltipTextChanged() {
        return this.tooltipTextChanged;
    }

    public CallableWithArgs<Void> getParentChanged() {
        return this.parentChanged;
    }

    public CallableWithArgs<Void> getSizeChanged() {
        return this.sizeChanged;
    }

    public CallableWithArgs<Void> getPreferredSizeChanged() {
        return this.preferredSizeChanged;
    }

    public CallableWithArgs<Void> getWidthLimitsChanged() {
        return this.widthLimitsChanged;
    }

    public CallableWithArgs<Void> getHeightLimitsChanged() {
        return this.heightLimitsChanged;
    }

    public CallableWithArgs<Void> getLocationChanged() {
        return this.locationChanged;
    }

    public CallableWithArgs<Void> getVisibleChanged() {
        return this.visibleChanged;
    }

    public CallableWithArgs<Void> getCursorChanged() {
        return this.cursorChanged;
    }

    public CallableWithArgs<Void> getTooltipDelayChanged() {
        return this.tooltipDelayChanged;
    }

    public CallableWithArgs<Void> getDragSourceChanged() {
        return this.dragSourceChanged;
    }

    public CallableWithArgs<Void> getDropTargetChanged() {
        return this.dropTargetChanged;
    }

    public CallableWithArgs<Void> getMenuHandlerChanged() {
        return this.menuHandlerChanged;
    }

    public CallableWithArgs<Void> getNameChanged() {
        return this.nameChanged;
    }


    public void setTooltipTextChanged(CallableWithArgs<Void> tooltipTextChanged) {
        this.tooltipTextChanged = tooltipTextChanged;
    }

    public void setParentChanged(CallableWithArgs<Void> parentChanged) {
        this.parentChanged = parentChanged;
    }

    public void setSizeChanged(CallableWithArgs<Void> sizeChanged) {
        this.sizeChanged = sizeChanged;
    }

    public void setPreferredSizeChanged(CallableWithArgs<Void> preferredSizeChanged) {
        this.preferredSizeChanged = preferredSizeChanged;
    }

    public void setWidthLimitsChanged(CallableWithArgs<Void> widthLimitsChanged) {
        this.widthLimitsChanged = widthLimitsChanged;
    }

    public void setHeightLimitsChanged(CallableWithArgs<Void> heightLimitsChanged) {
        this.heightLimitsChanged = heightLimitsChanged;
    }

    public void setLocationChanged(CallableWithArgs<Void> locationChanged) {
        this.locationChanged = locationChanged;
    }

    public void setVisibleChanged(CallableWithArgs<Void> visibleChanged) {
        this.visibleChanged = visibleChanged;
    }

    public void setCursorChanged(CallableWithArgs<Void> cursorChanged) {
        this.cursorChanged = cursorChanged;
    }

    public void setTooltipDelayChanged(CallableWithArgs<Void> tooltipDelayChanged) {
        this.tooltipDelayChanged = tooltipDelayChanged;
    }

    public void setDragSourceChanged(CallableWithArgs<Void> dragSourceChanged) {
        this.dragSourceChanged = dragSourceChanged;
    }

    public void setDropTargetChanged(CallableWithArgs<Void> dropTargetChanged) {
        this.dropTargetChanged = dropTargetChanged;
    }

    public void setMenuHandlerChanged(CallableWithArgs<Void> menuHandlerChanged) {
        this.menuHandlerChanged = menuHandlerChanged;
    }

    public void setNameChanged(CallableWithArgs<Void> nameChanged) {
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
