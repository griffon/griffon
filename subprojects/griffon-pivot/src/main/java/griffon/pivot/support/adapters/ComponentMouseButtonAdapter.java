/*
 * Copyright 2008-2017 the original author or authors.
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
public class ComponentMouseButtonAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentMouseButtonListener {
    private CallableWithArgs<Boolean> mouseClick;
    private CallableWithArgs<Boolean> mouseDown;
    private CallableWithArgs<Boolean> mouseUp;

    public CallableWithArgs<Boolean> getMouseClick() {
        return this.mouseClick;
    }

    public CallableWithArgs<Boolean> getMouseDown() {
        return this.mouseDown;
    }

    public CallableWithArgs<Boolean> getMouseUp() {
        return this.mouseUp;
    }


    public void setMouseClick(CallableWithArgs<Boolean> mouseClick) {
        this.mouseClick = mouseClick;
    }

    public void setMouseDown(CallableWithArgs<Boolean> mouseDown) {
        this.mouseDown = mouseDown;
    }

    public void setMouseUp(CallableWithArgs<Boolean> mouseUp) {
        this.mouseUp = mouseUp;
    }


    public boolean mouseClick(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.Mouse.Button arg1, int arg2, int arg3, int arg4) {
        return mouseClick != null && mouseClick.call(arg0, arg1, arg2, arg3, arg4);
    }

    public boolean mouseDown(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.Mouse.Button arg1, int arg2, int arg3) {
        return mouseDown != null && mouseDown.call(arg0, arg1, arg2, arg3);
    }

    public boolean mouseUp(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.Mouse.Button arg1, int arg2, int arg3) {
        return mouseUp != null && mouseUp.call(arg0, arg1, arg2, arg3);
    }

}
