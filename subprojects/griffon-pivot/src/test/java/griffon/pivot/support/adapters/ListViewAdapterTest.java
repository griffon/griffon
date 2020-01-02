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

public class ListViewAdapterTest {
    private final ListViewAdapter adapter = new ListViewAdapter();

    @Test
    public void testListDataChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getListDataChanged());
        adapter.listDataChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setListDataChanged(callable);
        adapter.listDataChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testItemRendererChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getItemRendererChanged());
        adapter.itemRendererChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setItemRendererChanged(callable);
        adapter.itemRendererChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDisabledItemFilterChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getDisabledItemFilterChanged());
        adapter.disabledItemFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDisabledItemFilterChanged(callable);
        adapter.disabledItemFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testItemEditorChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getItemEditorChanged());
        adapter.itemEditorChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setItemEditorChanged(callable);
        adapter.itemEditorChanged(null, null);
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

    @Test
    public void testCheckmarksEnabledChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getCheckmarksEnabledChanged());
        adapter.checkmarksEnabledChanged(null);
        assertFalse(invoked[0]);

        adapter.setCheckmarksEnabledChanged(callable);
        adapter.checkmarksEnabledChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDisabledCheckmarkFilterChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getDisabledCheckmarkFilterChanged());
        adapter.disabledCheckmarkFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDisabledCheckmarkFilterChanged(callable);
        adapter.disabledCheckmarkFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

}
