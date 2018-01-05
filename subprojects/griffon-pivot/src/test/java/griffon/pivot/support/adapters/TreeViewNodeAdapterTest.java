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
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TreeViewNodeAdapterTest {
    private TreeViewNodeAdapter adapter = new TreeViewNodeAdapter();

    @Test
    public void testNodeInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNodeInserted());
        adapter.nodeInserted(null, null, 0);
        assertFalse(invoked[0]);

        adapter.setNodeInserted(callable);
        adapter.nodeInserted(null, null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNodesRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNodesRemoved());
        adapter.nodesRemoved(null, null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setNodesRemoved(callable);
        adapter.nodesRemoved(null, null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNodeUpdated() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNodeUpdated());
        adapter.nodeUpdated(null, null, 0);
        assertFalse(invoked[0]);

        adapter.setNodeUpdated(callable);
        adapter.nodeUpdated(null, null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNodesSorted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNodesSorted());
        adapter.nodesSorted(null, null);
        assertFalse(invoked[0]);

        adapter.setNodesSorted(callable);
        adapter.nodesSorted(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testNodesCleared() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getNodesCleared());
        adapter.nodesCleared(null, null);
        assertFalse(invoked[0]);

        adapter.setNodesCleared(callable);
        adapter.nodesCleared(null, null);
        assertTrue(invoked[0]);
    }

}
