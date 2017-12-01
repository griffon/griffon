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

public class ListButtonBindingAdapterTest {
    private ListButtonBindingAdapter adapter = new ListButtonBindingAdapter();

    @Test
    public void testListDataKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getListDataKeyChanged());
        adapter.listDataKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setListDataKeyChanged(callable);
        adapter.listDataKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testListDataBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getListDataBindTypeChanged());
        adapter.listDataBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setListDataBindTypeChanged(callable);
        adapter.listDataBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testListDataBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getListDataBindMappingChanged());
        adapter.listDataBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setListDataBindMappingChanged(callable);
        adapter.listDataBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedItemKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedItemKeyChanged());
        adapter.selectedItemKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedItemKeyChanged(callable);
        adapter.selectedItemKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedItemBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedItemBindTypeChanged());
        adapter.selectedItemBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedItemBindTypeChanged(callable);
        adapter.selectedItemBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedItemBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedItemBindMappingChanged());
        adapter.selectedItemBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedItemBindMappingChanged(callable);
        adapter.selectedItemBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

}
