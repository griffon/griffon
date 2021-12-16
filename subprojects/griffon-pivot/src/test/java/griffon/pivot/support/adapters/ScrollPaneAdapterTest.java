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

public class ScrollPaneAdapterTest {
    private final ScrollPaneAdapter adapter = new ScrollPaneAdapter();

    @Test
    public void testCornerChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getCornerChanged());
        adapter.cornerChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setCornerChanged(callable);
        adapter.cornerChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testVerticalScrollBarPolicyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getVerticalScrollBarPolicyChanged());
        adapter.verticalScrollBarPolicyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setVerticalScrollBarPolicyChanged(callable);
        adapter.verticalScrollBarPolicyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testRowHeaderChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getRowHeaderChanged());
        adapter.rowHeaderChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setRowHeaderChanged(callable);
        adapter.rowHeaderChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testHorizontalScrollBarPolicyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getHorizontalScrollBarPolicyChanged());
        adapter.horizontalScrollBarPolicyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setHorizontalScrollBarPolicyChanged(callable);
        adapter.horizontalScrollBarPolicyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnHeaderChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnHeaderChanged());
        adapter.columnHeaderChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setColumnHeaderChanged(callable);
        adapter.columnHeaderChanged(null, null);
        assertTrue(invoked[0]);
    }

}
