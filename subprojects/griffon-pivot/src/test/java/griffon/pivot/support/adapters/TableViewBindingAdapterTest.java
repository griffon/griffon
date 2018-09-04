/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

public class TableViewBindingAdapterTest {
    private final TableViewBindingAdapter adapter = new TableViewBindingAdapter();

    @Test
    public void testTableDataKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTableDataKeyChanged());
        adapter.tableDataKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTableDataKeyChanged(callable);
        adapter.tableDataKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTableDataBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTableDataBindTypeChanged());
        adapter.tableDataBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTableDataBindTypeChanged(callable);
        adapter.tableDataBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTableDataBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTableDataBindMappingChanged());
        adapter.tableDataBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTableDataBindMappingChanged(callable);
        adapter.tableDataBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedRowKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedRowKeyChanged());
        adapter.selectedRowKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedRowKeyChanged(callable);
        adapter.selectedRowKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedRowBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedRowBindTypeChanged());
        adapter.selectedRowBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedRowBindTypeChanged(callable);
        adapter.selectedRowBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedRowBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedRowBindMappingChanged());
        adapter.selectedRowBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedRowBindMappingChanged(callable);
        adapter.selectedRowBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedRowsKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedRowsKeyChanged());
        adapter.selectedRowsKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedRowsKeyChanged(callable);
        adapter.selectedRowsKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedRowsBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedRowsBindTypeChanged());
        adapter.selectedRowsBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedRowsBindTypeChanged(callable);
        adapter.selectedRowsBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedRowsBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedRowsBindMappingChanged());
        adapter.selectedRowsBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedRowsBindMappingChanged(callable);
        adapter.selectedRowsBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

}
