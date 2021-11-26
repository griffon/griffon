/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MovieAdapterTest {
    private final MovieAdapter adapter = new MovieAdapter();

    @Test
    public void testRegionUpdated() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getRegionUpdated());
        adapter.regionUpdated(null, 0, 0, 0, 0);
        assertFalse(invoked[0]);

        adapter.setRegionUpdated(callable);
        adapter.regionUpdated(null, 0, 0, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCurrentFrameChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getCurrentFrameChanged());
        adapter.currentFrameChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setCurrentFrameChanged(callable);
        adapter.currentFrameChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testLoopingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getLoopingChanged());
        adapter.loopingChanged(null);
        assertFalse(invoked[0]);

        adapter.setLoopingChanged(callable);
        adapter.loopingChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMovieStarted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMovieStarted());
        adapter.movieStarted(null);
        assertFalse(invoked[0]);

        adapter.setMovieStarted(callable);
        adapter.movieStarted(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMovieStopped() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMovieStopped());
        adapter.movieStopped(null);
        assertFalse(invoked[0]);

        adapter.setMovieStopped(callable);
        adapter.movieStopped(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSizeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSizeChanged());
        adapter.sizeChanged(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setSizeChanged(callable);
        adapter.sizeChanged(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testBaselineChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getBaselineChanged());
        adapter.baselineChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setBaselineChanged(callable);
        adapter.baselineChanged(null, 0);
        assertTrue(invoked[0]);
    }

}
