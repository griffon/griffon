/*
 * Copyright 2008-2017 the original author or authors.
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
public class MovieViewAdapter implements GriffonPivotAdapter, org.apache.pivot.wtk.MovieViewListener {
    private CallableWithArgs<Void> movieChanged;

    public CallableWithArgs<Void> getMovieChanged() {
        return this.movieChanged;
    }


    public void setMovieChanged(CallableWithArgs<Void> movieChanged) {
        this.movieChanged = movieChanged;
    }


    public void movieChanged(org.apache.pivot.wtk.MovieView arg0, org.apache.pivot.wtk.media.Movie arg1) {
        if (movieChanged != null) {
            movieChanged.call(arg0, arg1);
        }
    }

}
