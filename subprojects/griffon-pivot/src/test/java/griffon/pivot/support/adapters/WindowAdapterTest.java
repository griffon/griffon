/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class WindowAdapterTest {
    private final WindowAdapter adapter = new WindowAdapter();

    @Test
    public void testTitleChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTitleChanged());
        adapter.titleChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTitleChanged(callable);
        adapter.titleChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testContentChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getContentChanged());
        adapter.contentChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setContentChanged(callable);
        adapter.contentChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testActiveChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getActiveChanged());
        adapter.activeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setActiveChanged(callable);
        adapter.activeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMaximizedChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMaximizedChanged());
        adapter.maximizedChanged(null);
        assertFalse(invoked[0]);

        adapter.setMaximizedChanged(callable);
        adapter.maximizedChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testIconAdded() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getIconAdded());
        adapter.iconAdded(null, null);
        assertFalse(invoked[0]);

        adapter.setIconAdded(callable);
        adapter.iconAdded(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testIconInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getIconInserted());
        adapter.iconInserted(null, null, 0);
        assertFalse(invoked[0]);

        adapter.setIconInserted(callable);
        adapter.iconInserted(null, null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testIconsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getIconsRemoved());
        adapter.iconsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setIconsRemoved(callable);
        adapter.iconsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

}
