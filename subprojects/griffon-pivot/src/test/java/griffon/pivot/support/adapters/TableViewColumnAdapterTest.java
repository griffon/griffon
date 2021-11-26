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

public class TableViewColumnAdapterTest {
    private final TableViewColumnAdapter adapter = new TableViewColumnAdapter();

    @Test
    public void testColumnInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnInserted());
        adapter.columnInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setColumnInserted(callable);
        adapter.columnInserted(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnsRemoved());
        adapter.columnsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setColumnsRemoved(callable);
        adapter.columnsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnWidthChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnWidthChanged());
        adapter.columnWidthChanged(null, 0, false);
        assertFalse(invoked[0]);

        adapter.setColumnWidthChanged(callable);
        adapter.columnWidthChanged(null, 0, false);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnNameChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnNameChanged());
        adapter.columnNameChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setColumnNameChanged(callable);
        adapter.columnNameChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnHeaderDataChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnHeaderDataChanged());
        adapter.columnHeaderDataChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setColumnHeaderDataChanged(callable);
        adapter.columnHeaderDataChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnHeaderDataRendererChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnHeaderDataRendererChanged());
        adapter.columnHeaderDataRendererChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setColumnHeaderDataRendererChanged(callable);
        adapter.columnHeaderDataRendererChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnWidthLimitsChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnWidthLimitsChanged());
        adapter.columnWidthLimitsChanged(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setColumnWidthLimitsChanged(callable);
        adapter.columnWidthLimitsChanged(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnFilterChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnFilterChanged());
        adapter.columnFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setColumnFilterChanged(callable);
        adapter.columnFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnCellRendererChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnCellRendererChanged());
        adapter.columnCellRendererChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setColumnCellRendererChanged(callable);
        adapter.columnCellRendererChanged(null, null);
        assertTrue(invoked[0]);
    }

}
