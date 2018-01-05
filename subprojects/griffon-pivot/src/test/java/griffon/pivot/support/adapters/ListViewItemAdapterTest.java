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

public class ListViewItemAdapterTest {
    private ListViewItemAdapter adapter = new ListViewItemAdapter();

    @Test
    public void testItemsCleared() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getItemsCleared());
        adapter.itemsCleared(null);
        assertFalse(invoked[0]);

        adapter.setItemsCleared(callable);
        adapter.itemsCleared(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testItemsSorted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getItemsSorted());
        adapter.itemsSorted(null);
        assertFalse(invoked[0]);

        adapter.setItemsSorted(callable);
        adapter.itemsSorted(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testItemInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getItemInserted());
        adapter.itemInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setItemInserted(callable);
        adapter.itemInserted(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testItemUpdated() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getItemUpdated());
        adapter.itemUpdated(null, 0);
        assertFalse(invoked[0]);

        adapter.setItemUpdated(callable);
        adapter.itemUpdated(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testItemsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getItemsRemoved());
        adapter.itemsRemoved(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setItemsRemoved(callable);
        adapter.itemsRemoved(null, 0, 0);
        assertTrue(invoked[0]);
    }

}
