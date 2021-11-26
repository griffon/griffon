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

public class ElementAdapterTest {
    private final ElementAdapter adapter = new ElementAdapter();

    @Test
    public void testNodeInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getNodeInserted());
        adapter.nodeInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setNodeInserted(callable);
        adapter.nodeInserted(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNodesRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getNodesRemoved());
        adapter.nodesRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setNodesRemoved(callable);
        adapter.nodesRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testFontChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getFontChanged());
        adapter.fontChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setFontChanged(callable);
        adapter.fontChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testForegroundColorChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getForegroundColorChanged());
        adapter.foregroundColorChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setForegroundColorChanged(callable);
        adapter.foregroundColorChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testBackgroundColorChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getBackgroundColorChanged());
        adapter.backgroundColorChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setBackgroundColorChanged(callable);
        adapter.backgroundColorChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testUnderlineChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getUnderlineChanged());
        adapter.underlineChanged(null);
        assertFalse(invoked[0]);

        adapter.setUnderlineChanged(callable);
        adapter.underlineChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testStrikethroughChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getStrikethroughChanged());
        adapter.strikethroughChanged(null);
        assertFalse(invoked[0]);

        adapter.setStrikethroughChanged(callable);
        adapter.strikethroughChanged(null);
        assertTrue(invoked[0]);
    }

}
