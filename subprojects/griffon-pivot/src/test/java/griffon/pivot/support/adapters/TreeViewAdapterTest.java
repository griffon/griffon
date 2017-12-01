/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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

public class TreeViewAdapterTest {
    private TreeViewAdapter adapter = new TreeViewAdapter();

    @Test
    public void testSelectModeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
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
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
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
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getDisabledCheckmarkFilterChanged());
        adapter.disabledCheckmarkFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDisabledCheckmarkFilterChanged(callable);
        adapter.disabledCheckmarkFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNodeRendererChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNodeRendererChanged());
        adapter.nodeRendererChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setNodeRendererChanged(callable);
        adapter.nodeRendererChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTreeDataChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTreeDataChanged());
        adapter.treeDataChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTreeDataChanged(callable);
        adapter.treeDataChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNodeEditorChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNodeEditorChanged());
        adapter.nodeEditorChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setNodeEditorChanged(callable);
        adapter.nodeEditorChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testShowMixedCheckmarkStateChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getShowMixedCheckmarkStateChanged());
        adapter.showMixedCheckmarkStateChanged(null);
        assertFalse(invoked[0]);

        adapter.setShowMixedCheckmarkStateChanged(callable);
        adapter.showMixedCheckmarkStateChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDisabledNodeFilterChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getDisabledNodeFilterChanged());
        adapter.disabledNodeFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDisabledNodeFilterChanged(callable);
        adapter.disabledNodeFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

}
