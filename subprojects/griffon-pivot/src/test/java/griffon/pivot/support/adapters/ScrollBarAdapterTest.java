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

public class ScrollBarAdapterTest {
    private final ScrollBarAdapter adapter = new ScrollBarAdapter();

    @Test
    public void testOrientationChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getOrientationChanged());
        adapter.orientationChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setOrientationChanged(callable);
        adapter.orientationChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testScopeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getScopeChanged());
        adapter.scopeChanged(null, 0, 0, 0);
        assertFalse(invoked[0]);

        adapter.setScopeChanged(callable);
        adapter.scopeChanged(null, 0, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testUnitIncrementChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getUnitIncrementChanged());
        adapter.unitIncrementChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setUnitIncrementChanged(callable);
        adapter.unitIncrementChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testBlockIncrementChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getBlockIncrementChanged());
        adapter.blockIncrementChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setBlockIncrementChanged(callable);
        adapter.blockIncrementChanged(null, 0);
        assertTrue(invoked[0]);
    }

}
