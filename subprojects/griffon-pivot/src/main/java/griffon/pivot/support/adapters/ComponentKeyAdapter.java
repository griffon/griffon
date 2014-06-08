/*
 * Copyright 2008-2014 the original author or authors.
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
public class ComponentKeyAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ComponentKeyListener {
    private CallableWithArgs<Boolean> keyTyped;
    private CallableWithArgs<Boolean> keyPressed;
    private CallableWithArgs<Boolean> keyReleased;

    public CallableWithArgs<Boolean> getKeyTyped() {
        return this.keyTyped;
    }

    public CallableWithArgs<Boolean> getKeyPressed() {
        return this.keyPressed;
    }

    public CallableWithArgs<Boolean> getKeyReleased() {
        return this.keyReleased;
    }


    public void setKeyTyped(CallableWithArgs<Boolean> keyTyped) {
        this.keyTyped = keyTyped;
    }

    public void setKeyPressed(CallableWithArgs<Boolean> keyPressed) {
        this.keyPressed = keyPressed;
    }

    public void setKeyReleased(CallableWithArgs<Boolean> keyReleased) {
        this.keyReleased = keyReleased;
    }


    public boolean keyTyped(org.apache.pivot.wtk.Component arg0, char arg1) {
        return keyTyped != null && keyTyped.call(arg0, arg1);
    }

    public boolean keyPressed(org.apache.pivot.wtk.Component arg0, int arg1, org.apache.pivot.wtk.Keyboard.KeyLocation arg2) {
        return keyPressed != null && keyPressed.call(arg0, arg1, arg2);
    }

    public boolean keyReleased(org.apache.pivot.wtk.Component arg0, int arg1, org.apache.pivot.wtk.Keyboard.KeyLocation arg2) {
        return keyReleased != null && keyReleased.call(arg0, arg1, arg2);
    }

}
