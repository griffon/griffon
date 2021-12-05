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

public class WindowActionMappingAdapterTest {
    private final WindowActionMappingAdapter adapter = new WindowActionMappingAdapter();

    @Test
    public void testActionChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getActionChanged());
        adapter.actionChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setActionChanged(callable);
        adapter.actionChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testKeyStrokeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getKeyStrokeChanged());
        adapter.keyStrokeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setKeyStrokeChanged(callable);
        adapter.keyStrokeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testActionMappingsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getActionMappingsRemoved());
        adapter.actionMappingsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setActionMappingsRemoved(callable);
        adapter.actionMappingsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testActionMappingAdded() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getActionMappingAdded());
        adapter.actionMappingAdded(null);
        assertFalse(invoked[0]);

        adapter.setActionMappingAdded(callable);
        adapter.actionMappingAdded(null);
        assertTrue(invoked[0]);
    }

}
