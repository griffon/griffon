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
public class MovieAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.media.MovieListener {
    private CallableWithArgs<?> regionUpdated;
    private CallableWithArgs<?> baselineChanged;
    private CallableWithArgs<?> sizeChanged;
    private CallableWithArgs<?> currentFrameChanged;
    private CallableWithArgs<?> loopingChanged;
    private CallableWithArgs<?> movieStarted;
    private CallableWithArgs<?> movieStopped;

    public CallableWithArgs<?> getRegionUpdated() {
        return this.regionUpdated;
    }

    public CallableWithArgs<?> getBaselineChanged() {
        return this.baselineChanged;
    }

    public CallableWithArgs<?> getSizeChanged() {
        return this.sizeChanged;
    }

    public CallableWithArgs<?> getCurrentFrameChanged() {
        return this.currentFrameChanged;
    }

    public CallableWithArgs<?> getLoopingChanged() {
        return this.loopingChanged;
    }

    public CallableWithArgs<?> getMovieStarted() {
        return this.movieStarted;
    }

    public CallableWithArgs<?> getMovieStopped() {
        return this.movieStopped;
    }


    public void setRegionUpdated(CallableWithArgs<?> regionUpdated) {
        this.regionUpdated = regionUpdated;
    }

    public void setBaselineChanged(CallableWithArgs<?> baselineChanged) {
        this.baselineChanged = baselineChanged;
    }

    public void setSizeChanged(CallableWithArgs<?> sizeChanged) {
        this.sizeChanged = sizeChanged;
    }

    public void setCurrentFrameChanged(CallableWithArgs<?> currentFrameChanged) {
        this.currentFrameChanged = currentFrameChanged;
    }

    public void setLoopingChanged(CallableWithArgs<?> loopingChanged) {
        this.loopingChanged = loopingChanged;
    }

    public void setMovieStarted(CallableWithArgs<?> movieStarted) {
        this.movieStarted = movieStarted;
    }

    public void setMovieStopped(CallableWithArgs<?> movieStopped) {
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
