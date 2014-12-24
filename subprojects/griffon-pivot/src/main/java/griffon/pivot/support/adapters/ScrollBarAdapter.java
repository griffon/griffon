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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ScrollBarAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.ScrollBarListener {
    private CallableWithArgs<Void> orientationChanged;
    private CallableWithArgs<Void> scopeChanged;
    private CallableWithArgs<Void> unitIncrementChanged;
    private CallableWithArgs<Void> blockIncrementChanged;

    public CallableWithArgs<Void> getOrientationChanged() {
        return this.orientationChanged;
    }

    public CallableWithArgs<Void> getScopeChanged() {
        return this.scopeChanged;
    }

    public CallableWithArgs<Void> getUnitIncrementChanged() {
        return this.unitIncrementChanged;
    }

    public CallableWithArgs<Void> getBlockIncrementChanged() {
        return this.blockIncrementChanged;
    }


    public void setOrientationChanged(CallableWithArgs<Void> orientationChanged) {
        this.orientationChanged = orientationChanged;
    }

    public void setScopeChanged(CallableWithArgs<Void> scopeChanged) {
        this.scopeChanged = scopeChanged;
    }

    public void setUnitIncrementChanged(CallableWithArgs<Void> unitIncrementChanged) {
        this.unitIncrementChanged = unitIncrementChanged;
    }

    public void setBlockIncrementChanged(CallableWithArgs<Void> blockIncrementChanged) {
        this.blockIncrementChanged = blockIncrementChanged;
    }


    public void orientationChanged(org.apache.pivot.wtk.ScrollBar arg0, org.apache.pivot.wtk.Orientation arg1) {
        if (orientationChanged != null) {
            orientationChanged.call(arg0, arg1);
        }
    }

    public void scopeChanged(org.apache.pivot.wtk.ScrollBar arg0, int arg1, int arg2, int arg3) {
        if (scopeChanged != null) {
            scopeChanged.call(arg0, arg1, arg2, arg3);
        }
    }

    public void unitIncrementChanged(org.apache.pivot.wtk.ScrollBar arg0, int arg1) {
        if (unitIncrementChanged != null) {
            unitIncrementChanged.call(arg0, arg1);
        }
    }

    public void blockIncrementChanged(org.apache.pivot.wtk.ScrollBar arg0, int arg1) {
        if (blockIncrementChanged != null) {
            blockIncrementChanged.call(arg0, arg1);
        }
    }

}
