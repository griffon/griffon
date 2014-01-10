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
public class SplitPaneAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SplitPaneListener {
    private CallableWithArgs<?> topLeftChanged;
    private CallableWithArgs<?> bottomRightChanged;
    private CallableWithArgs<?> primaryRegionChanged;
    private CallableWithArgs<?> splitRatioChanged;
    private CallableWithArgs<?> lockedChanged;
    private CallableWithArgs<?> resizeModeChanged;
    private CallableWithArgs<?> orientationChanged;

    public CallableWithArgs<?> getTopLeftChanged() {
        return this.topLeftChanged;
    }

    public CallableWithArgs<?> getBottomRightChanged() {
        return this.bottomRightChanged;
    }

    public CallableWithArgs<?> getPrimaryRegionChanged() {
        return this.primaryRegionChanged;
    }

    public CallableWithArgs<?> getSplitRatioChanged() {
        return this.splitRatioChanged;
    }

    public CallableWithArgs<?> getLockedChanged() {
        return this.lockedChanged;
    }

    public CallableWithArgs<?> getResizeModeChanged() {
        return this.resizeModeChanged;
    }

    public CallableWithArgs<?> getOrientationChanged() {
        return this.orientationChanged;
    }


    public void setTopLeftChanged(CallableWithArgs<?> topLeftChanged) {
        this.topLeftChanged = topLeftChanged;
    }

    public void setBottomRightChanged(CallableWithArgs<?> bottomRightChanged) {
        this.bottomRightChanged = bottomRightChanged;
    }

    public void setPrimaryRegionChanged(CallableWithArgs<?> primaryRegionChanged) {
        this.primaryRegionChanged = primaryRegionChanged;
    }

    public void setSplitRatioChanged(CallableWithArgs<?> splitRatioChanged) {
        this.splitRatioChanged = splitRatioChanged;
    }

    public void setLockedChanged(CallableWithArgs<?> lockedChanged) {
        this.lockedChanged = lockedChanged;
    }

    public void setResizeModeChanged(CallableWithArgs<?> resizeModeChanged) {
        this.resizeModeChanged = resizeModeChanged;
    }

    public void setOrientationChanged(CallableWithArgs<?> orientationChanged) {
        this.orientationChanged = orientationChanged;
    }


    public void topLeftChanged(org.apache.pivot.wtk.SplitPane arg0, org.apache.pivot.wtk.Component arg1) {
        if (topLeftChanged != null) {
            topLeftChanged.call(arg0, arg1);
        }
    }

    public void bottomRightChanged(org.apache.pivot.wtk.SplitPane arg0, org.apache.pivot.wtk.Component arg1) {
        if (bottomRightChanged != null) {
            bottomRightChanged.call(arg0, arg1);
        }
    }

    public void primaryRegionChanged(org.apache.pivot.wtk.SplitPane arg0) {
        if (primaryRegionChanged != null) {
            primaryRegionChanged.call(arg0);
        }
    }

    public void splitRatioChanged(org.apache.pivot.wtk.SplitPane arg0, float arg1) {
        if (splitRatioChanged != null) {
            splitRatioChanged.call(arg0, arg1);
        }
    }

    public void lockedChanged(org.apache.pivot.wtk.SplitPane arg0) {
        if (lockedChanged != null) {
            lockedChanged.call(arg0);
        }
    }

    public void resizeModeChanged(org.apache.pivot.wtk.SplitPane arg0, org.apache.pivot.wtk.SplitPane.ResizeMode arg1) {
        if (resizeModeChanged != null) {
            resizeModeChanged.call(arg0, arg1);
        }
    }

    public void orientationChanged(org.apache.pivot.wtk.SplitPane arg0) {
        if (orientationChanged != null) {
            orientationChanged.call(arg0);
        }
    }

}
