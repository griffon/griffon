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

public class TabPaneAdapterTest {
    private TabPaneAdapter adapter = new TabPaneAdapter();

    @Test
    public void testTabInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTabInserted());
        adapter.tabInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setTabInserted(callable);
        adapter.tabInserted(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPreviewRemoveTabs() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewRemoveTabs());
        adapter.previewRemoveTabs(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setPreviewRemoveTabs(callable);
        adapter.previewRemoveTabs(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testRemoveTabsVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getRemoveTabsVetoed());
        adapter.removeTabsVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setRemoveTabsVetoed(callable);
        adapter.removeTabsVetoed(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTabsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTabsRemoved());
        adapter.tabsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setTabsRemoved(callable);
        adapter.tabsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTabDataRendererChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTabDataRendererChanged());
        adapter.tabDataRendererChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTabDataRendererChanged(callable);
        adapter.tabDataRendererChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCloseableChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getCloseableChanged());
        adapter.closeableChanged(null);
        assertFalse(invoked[0]);

        adapter.setCloseableChanged(callable);
        adapter.closeableChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCollapsibleChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getCollapsibleChanged());
        adapter.collapsibleChanged(null);
        assertFalse(invoked[0]);

        adapter.setCollapsibleChanged(callable);
        adapter.collapsibleChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testCornerChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getCornerChanged());
        adapter.cornerChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setCornerChanged(callable);
        adapter.cornerChanged(null, null);
        assertTrue(invoked[0]);
    }

}
