/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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

public class ButtonAdapterTest {
    private final ButtonAdapter adapter = new ButtonAdapter();

    @Test
    public void testButtonDataChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getButtonDataChanged());
        adapter.buttonDataChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setButtonDataChanged(callable);
        adapter.buttonDataChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDataRendererChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getDataRendererChanged());
        adapter.dataRendererChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDataRendererChanged(callable);
        adapter.dataRendererChanged(null, null);
        assertTrue(invoked[0]);
    }

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
    public void testToggleButtonChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getToggleButtonChanged());
        adapter.toggleButtonChanged(null);
        assertFalse(invoked[0]);

        adapter.setToggleButtonChanged(callable);
        adapter.toggleButtonChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTriStateChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTriStateChanged());
        adapter.triStateChanged(null);
        assertFalse(invoked[0]);

        adapter.setTriStateChanged(callable);
        adapter.triStateChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testButtonGroupChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getButtonGroupChanged());
        adapter.buttonGroupChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setButtonGroupChanged(callable);
        adapter.buttonGroupChanged(null, null);
        assertTrue(invoked[0]);
    }

}
