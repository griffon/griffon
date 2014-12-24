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
public class ImageAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.media.ImageListener {
    private CallableWithArgs<Void> regionUpdated;
    private CallableWithArgs<Void> baselineChanged;
    private CallableWithArgs<Void> sizeChanged;

    public CallableWithArgs<Void> getRegionUpdated() {
        return this.regionUpdated;
    }

    public CallableWithArgs<Void> getBaselineChanged() {
        return this.baselineChanged;
    }

    public CallableWithArgs<Void> getSizeChanged() {
        return this.sizeChanged;
    }


    public void setRegionUpdated(CallableWithArgs<Void> regionUpdated) {
        this.regionUpdated = regionUpdated;
    }

    public void setBaselineChanged(CallableWithArgs<Void> baselineChanged) {
        this.baselineChanged = baselineChanged;
    }

    public void setSizeChanged(CallableWithArgs<Void> sizeChanged) {
        this.sizeChanged = sizeChanged;
    }


    public void regionUpdated(org.apache.pivot.wtk.media.Image arg0, int arg1, int arg2, int arg3, int arg4) {
        if (regionUpdated != null) {
            regionUpdated.call(arg0, arg1, arg2, arg3, arg4);
        }
    }

    public void baselineChanged(org.apache.pivot.wtk.media.Image arg0, int arg1) {
        if (baselineChanged != null) {
            baselineChanged.call(arg0, arg1);
        }
    }

    public void sizeChanged(org.apache.pivot.wtk.media.Image arg0, int arg1, int arg2) {
        if (sizeChanged != null) {
            sizeChanged.call(arg0, arg1, arg2);
        }
    }

}
