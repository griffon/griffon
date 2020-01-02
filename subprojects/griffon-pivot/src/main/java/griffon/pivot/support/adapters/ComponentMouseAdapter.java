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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ComponentMouseAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentMouseListener {
    private CallableWithArgs<Void> mouseOut;
    private CallableWithArgs<Void> mouseOver;
    private CallableWithArgs<Boolean> mouseMove;

    public CallableWithArgs<Void> getMouseOut() {
        return this.mouseOut;
    }

    public CallableWithArgs<Void> getMouseOver() {
        return this.mouseOver;
    }

    public CallableWithArgs<Boolean> getMouseMove() {
        return this.mouseMove;
    }


    public void setMouseOut(CallableWithArgs<Void> mouseOut) {
        this.mouseOut = mouseOut;
    }

    public void setMouseOver(CallableWithArgs<Void> mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMouseMove(CallableWithArgs<Boolean> mouseMove) {
        this.mouseMove = mouseMove;
    }


    public void mouseOut(org.apache.pivot.wtk.Component arg0) {
        if (mouseOut != null) {
            mouseOut.call(arg0);
        }
    }

    public void mouseOver(org.apache.pivot.wtk.Component arg0) {
        if (mouseOver != null) {
            mouseOver.call(arg0);
        }
    }

    public boolean mouseMove(org.apache.pivot.wtk.Component arg0, int arg1, int arg2) {
        return mouseMove != null && mouseMove.call(arg0, arg1, arg2);
    }

}
