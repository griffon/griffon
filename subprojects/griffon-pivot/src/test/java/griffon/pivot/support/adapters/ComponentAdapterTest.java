/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ComponentAdapterTest {
    private ComponentAdapter adapter = new ComponentAdapter();

    @Test
    public void testTooltipTextChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTooltipTextChanged());
        adapter.tooltipTextChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTooltipTextChanged(callable);
        adapter.tooltipTextChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testParentChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getParentChanged());
        adapter.parentChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setParentChanged(callable);
        adapter.parentChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSizeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSizeChanged());
        adapter.sizeChanged(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setSizeChanged(callable);
        adapter.sizeChanged(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPreferredSizeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreferredSizeChanged());
        adapter.preferredSizeChanged(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setPreferredSizeChanged(callable);
        adapter.preferredSizeChanged(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testWidthLimitsChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getWidthLimitsChanged());
        adapter.widthLimitsChanged(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setWidthLimitsChanged(callable);
        adapter.widthLimitsChanged(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testHeightLimitsChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getHeightLimitsChanged());
        adapter.heightLimitsChanged(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setHeightLimitsChanged(callable);
        adapter.heightLimitsChanged(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testLocationChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getLocationChanged());
        adapter.locationChanged(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setLocationChanged(callable);
        adapter.locationChanged(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testVisibleChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getVisibleChanged());
        adapter.visibleChanged(null);
        assertFalse(invoked[0]);

        adapter.setVisibleChanged(callable);
        adapter.visibleChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCursorChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getCursorChanged());
        adapter.cursorChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setCursorChanged(callable);
        adapter.cursorChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTooltipDelayChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTooltipDelayChanged());
        adapter.tooltipDelayChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setTooltipDelayChanged(callable);
        adapter.tooltipDelayChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDragSourceChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getDragSourceChanged());
        adapter.dragSourceChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDragSourceChanged(callable);
        adapter.dragSourceChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDropTargetChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getDropTargetChanged());
        adapter.dropTargetChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDropTargetChanged(callable);
        adapter.dropTargetChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMenuHandlerChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getMenuHandlerChanged());
        adapter.menuHandlerChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setMenuHandlerChanged(callable);
        adapter.menuHandlerChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNameChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNameChanged());
        adapter.nameChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setNameChanged(callable);
        adapter.nameChanged(null, null);
        assertTrue(invoked[0]);
    }

}
