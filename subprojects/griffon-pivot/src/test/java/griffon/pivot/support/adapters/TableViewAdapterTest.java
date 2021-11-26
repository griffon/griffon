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

public class TableViewAdapterTest {
    private final TableViewAdapter adapter = new TableViewAdapter();

    @Test
    public void testTableDataChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTableDataChanged());
        adapter.tableDataChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTableDataChanged(callable);
        adapter.tableDataChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testColumnSourceChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getColumnSourceChanged());
        adapter.columnSourceChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setColumnSourceChanged(callable);
        adapter.columnSourceChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testRowEditorChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getRowEditorChanged());
        adapter.rowEditorChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setRowEditorChanged(callable);
        adapter.rowEditorChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDisabledRowFilterChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getDisabledRowFilterChanged());
        adapter.disabledRowFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDisabledRowFilterChanged(callable);
        adapter.disabledRowFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectModeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectModeChanged());
        adapter.selectModeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectModeChanged(callable);
        adapter.selectModeChanged(null, null);
        assertTrue(invoked[0]);
    }

}
