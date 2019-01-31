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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ContainerMouseAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ContainerMouseListener {
    private CallableWithArgs<Boolean> mouseWheel;
    private CallableWithArgs<Boolean> mouseDown;
    private CallableWithArgs<Boolean> mouseUp;
    private CallableWithArgs<Boolean> mouseMove;

    public CallableWithArgs<Boolean> getMouseWheel() {
        return this.mouseWheel;
    }

    public CallableWithArgs<Boolean> getMouseDown() {
        return this.mouseDown;
    }

    public CallableWithArgs<Boolean> getMouseUp() {
        return this.mouseUp;
    }

    public CallableWithArgs<Boolean> getMouseMove() {
        return this.mouseMove;
    }


    public void setMouseWheel(CallableWithArgs<Boolean> mouseWheel) {
        this.mouseWheel = mouseWheel;
    }

    public void setMouseDown(CallableWithArgs<Boolean> mouseDown) {
        this.mouseDown = mouseDown;
    }

    public void setMouseUp(CallableWithArgs<Boolean> mouseUp) {
        this.mouseUp = mouseUp;
    }

    public void setMouseMove(CallableWithArgs<Boolean> mouseMove) {
        this.mouseMove = mouseMove;
    }


    public boolean mouseWheel(org.apache.pivot.wtk.Container arg0, org.apache.pivot.wtk.Mouse.ScrollType arg1, int arg2, int arg3, int arg4, int arg5) {
        return mouseWheel != null && mouseWheel.call(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    public boolean mouseDown(org.apache.pivot.wtk.Container arg0, org.apache.pivot.wtk.Mouse.Button arg1, int arg2, int arg3) {
        return mouseDown != null && mouseDown.call(arg0, arg1, arg2, arg3);
    }

    public boolean mouseUp(org.apache.pivot.wtk.Container arg0, org.apache.pivot.wtk.Mouse.Button arg1, int arg2, int arg3) {
        return mouseUp != null && mouseUp.call(arg0, arg1, arg2, arg3);
    }

    public boolean mouseMove(org.apache.pivot.wtk.Container arg0, int arg1, int arg2) {
        return mouseMove != null && mouseMove.call(arg0, arg1, arg2);
    }

}
