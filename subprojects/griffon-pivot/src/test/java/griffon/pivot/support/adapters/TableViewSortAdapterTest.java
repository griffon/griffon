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

public class TableViewSortAdapterTest {
    private final TableViewSortAdapter adapter = new TableViewSortAdapter();

    @Test
    public void testSortChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSortChanged());
        adapter.sortChanged(null);
        assertFalse(invoked[0]);

        adapter.setSortChanged(callable);
        adapter.sortChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSortAdded() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSortAdded());
        adapter.sortAdded(null, null);
        assertFalse(invoked[0]);

        adapter.setSortAdded(callable);
        adapter.sortAdded(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSortUpdated() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSortUpdated());
        adapter.sortUpdated(null, null, null);
        assertFalse(invoked[0]);

        adapter.setSortUpdated(callable);
        adapter.sortUpdated(null, null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSortRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSortRemoved());
        adapter.sortRemoved(null, null, null);
        assertFalse(invoked[0]);

        adapter.setSortRemoved(callable);
        adapter.sortRemoved(null, null, null);
        assertTrue(invoked[0]);
    }

}
