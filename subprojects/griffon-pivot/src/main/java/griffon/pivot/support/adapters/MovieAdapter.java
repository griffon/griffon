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
public class MovieAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.media.MovieListener {
    private CallableWithArgs<Void> regionUpdated;
    private CallableWithArgs<Void> baselineChanged;
    private CallableWithArgs<Void> sizeChanged;
    private CallableWithArgs<Void> currentFrameChanged;
    private CallableWithArgs<Void> loopingChanged;
    private CallableWithArgs<Void> movieStarted;
    private CallableWithArgs<Void> movieStopped;

    public CallableWithArgs<Void> getRegionUpdated() {
        return this.regionUpdated;
    }

    public CallableWithArgs<Void> getBaselineChanged() {
        return this.baselineChanged;
    }

    public CallableWithArgs<Void> getSizeChanged() {
        return this.sizeChanged;
    }

    public CallableWithArgs<Void> getCurrentFrameChanged() {
        return this.currentFrameChanged;
    }

    public CallableWithArgs<Void> getLoopingChanged() {
        return this.loopingChanged;
    }

    public CallableWithArgs<Void> getMovieStarted() {
        return this.movieStarted;
    }

    public CallableWithArgs<Void> getMovieStopped() {
        return this.movieStopped;
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

    public void setCurrentFrameChanged(CallableWithArgs<Void> currentFrameChanged) {
        this.currentFrameChanged = currentFrameChanged;
    }

    public void setLoopingChanged(CallableWithArgs<Void> loopingChanged) {
        this.loopingChanged = loopingChanged;
    }

    public void setMovieStarted(CallableWithArgs<Void> movieStarted) {
        this.movieStarted = movieStarted;
    }

    public void setMovieStopped(CallableWithArgs<Void> movieStopped) {
        this.movieStopped = movieStopped;
    }


    public void regionUpdated(org.apache.pivot.wtk.media.Movie arg0, int arg1, int arg2, int arg3, int arg4) {
        if (regionUpdated != null) {
            regionUpdated.call(arg0, arg1, arg2, arg3, arg4);
        }
    }

    public void baselineChanged(org.apache.pivot.wtk.media.Movie arg0, int arg1) {
        if (baselineChanged != null) {
            baselineChanged.call(arg0, arg1);
        }
    }

    public void sizeChanged(org.apache.pivot.wtk.media.Movie arg0, int arg1, int arg2) {
        if (sizeChanged != null) {
            sizeChanged.call(arg0, arg1, arg2);
        }
    }

    public void currentFrameChanged(org.apache.pivot.wtk.media.Movie arg0, int arg1) {
        if (currentFrameChanged != null) {
            currentFrameChanged.call(arg0, arg1);
        }
    }

    public void loopingChanged(org.apache.pivot.wtk.media.Movie arg0) {
        if (loopingChanged != null) {
            loopingChanged.call(arg0);
        }
    }

    public void movieStarted(org.apache.pivot.wtk.media.Movie arg0) {
        if (movieStarted != null) {
            movieStarted.call(arg0);
        }
    }

    public void movieStopped(org.apache.pivot.wtk.media.Movie arg0) {
        if (movieStopped != null) {
            movieStopped.call(arg0);
        }
    }

}
