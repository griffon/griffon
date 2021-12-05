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

public class GridPaneAdapterTest {
    private final GridPaneAdapter adapter = new GridPaneAdapter();

    @Test
    public void testColumnCountChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnCountChanged());
        adapter.columnCountChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setColumnCountChanged(callable);
        adapter.columnCountChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCellInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getCellInserted());
        adapter.cellInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setCellInserted(callable);
        adapter.cellInserted(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCellsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getCellsRemoved());
        adapter.cellsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setCellsRemoved(callable);
        adapter.cellsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCellUpdated() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getCellUpdated());
        adapter.cellUpdated(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setCellUpdated(callable);
        adapter.cellUpdated(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testRowsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getRowsRemoved());
        adapter.rowsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setRowsRemoved(callable);
        adapter.rowsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testRowInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getRowInserted());
        adapter.rowInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setRowInserted(callable);
        adapter.rowInserted(null, 0);
        assertTrue(invoked[0]);
    }

}
