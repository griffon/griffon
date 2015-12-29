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
public class SplitPaneAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.SplitPaneListener {
    private CallableWithArgs<Void> topLeftChanged;
    private CallableWithArgs<Void> bottomRightChanged;
    private CallableWithArgs<Void> primaryRegionChanged;
    private CallableWithArgs<Void> splitRatioChanged;
    private CallableWithArgs<Void> lockedChanged;
    private CallableWithArgs<Void> resizeModeChanged;
    private CallableWithArgs<Void> orientationChanged;

    public CallableWithArgs<Void> getTopLeftChanged() {
        return this.topLeftChanged;
    }

    public CallableWithArgs<Void> getBottomRightChanged() {
        return this.bottomRightChanged;
    }

    public CallableWithArgs<Void> getPrimaryRegionChanged() {
        return this.primaryRegionChanged;
    }

    public CallableWithArgs<Void> getSplitRatioChanged() {
        return this.splitRatioChanged;
    }

    public CallableWithArgs<Void> getLockedChanged() {
        return this.lockedChanged;
    }

    public CallableWithArgs<Void> getResizeModeChanged() {
        return this.resizeModeChanged;
    }

    public CallableWithArgs<Void> getOrientationChanged() {
        return this.orientationChanged;
    }


    public void setTopLeftChanged(CallableWithArgs<Void> topLeftChanged) {
        this.topLeftChanged = topLeftChanged;
    }

    public void setBottomRightChanged(CallableWithArgs<Void> bottomRightChanged) {
        this.bottomRightChanged = bottomRightChanged;
    }

    public void setPrimaryRegionChanged(CallableWithArgs<Void> primaryRegionChanged) {
        this.primaryRegionChanged = primaryRegionChanged;
    }

    public void setSplitRatioChanged(CallableWithArgs<Void> splitRatioChanged) {
        this.splitRatioChanged = splitRatioChanged;
    }

    public void setLockedChanged(CallableWithArgs<Void> lockedChanged) {
        this.lockedChanged = lockedChanged;
    }

    public void setResizeModeChanged(CallableWithArgs<Void> resizeModeChanged) {
        this.resizeModeChanged = resizeModeChanged;
    }

    public void setOrientationChanged(CallableWithArgs<Void> orientationChanged) {
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
