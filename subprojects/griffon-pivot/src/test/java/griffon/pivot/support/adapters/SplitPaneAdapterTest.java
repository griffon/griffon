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

import org.junit.Test;
import static org.junit.Assert.*;
import griffon.core.CallableWithArgs;

public class SplitPaneAdapterTest {
    private SplitPaneAdapter adapter = new SplitPaneAdapter();

    @Test
    public void testOrientationChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getOrientationChanged());
        adapter.orientationChanged(null);
        assertFalse(invoked[0]);

        adapter.setOrientationChanged(callable);
        adapter.orientationChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testBottomRightChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getBottomRightChanged());
        adapter.bottomRightChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setBottomRightChanged(callable);
        adapter.bottomRightChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTopLeftChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTopLeftChanged());
        adapter.topLeftChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTopLeftChanged(callable);
        adapter.topLeftChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPrimaryRegionChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPrimaryRegionChanged());
        adapter.primaryRegionChanged(null);
        assertFalse(invoked[0]);

        adapter.setPrimaryRegionChanged(callable);
        adapter.primaryRegionChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSplitRatioChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSplitRatioChanged());
        adapter.splitRatioChanged(null, 0f);
        assertFalse(invoked[0]);

        adapter.setSplitRatioChanged(callable);
        adapter.splitRatioChanged(null, 0f);
        assertTrue(invoked[0]);
    }

    @Test
    public void testLockedChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getLockedChanged());
        adapter.lockedChanged(null);
        assertFalse(invoked[0]);

        adapter.setLockedChanged(callable);
        adapter.lockedChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testResizeModeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getResizeModeChanged());
        adapter.resizeModeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setResizeModeChanged(callable);
        adapter.resizeModeChanged(null, null);
        assertTrue(invoked[0]);
    }

}
