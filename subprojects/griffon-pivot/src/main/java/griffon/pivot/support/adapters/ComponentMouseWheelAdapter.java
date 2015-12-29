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
public class ComponentMouseWheelAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentMouseWheelListener {
    private CallableWithArgs<Boolean> mouseWheel;

    public CallableWithArgs<Boolean> getMouseWheel() {
        return this.mouseWheel;
    }


    public void setMouseWheel(CallableWithArgs<Boolean> mouseWheel) {
        this.mouseWheel = mouseWheel;
    }


    public boolean mouseWheel(org.apache.pivot.wtk.Component arg0, org.apache.pivot.wtk.Mouse.ScrollType arg1, int arg2, int arg3, int arg4, int arg5) {
        return mouseWheel != null && mouseWheel.call(arg0, arg1, arg2, arg3, arg4, arg5);
    }

}
