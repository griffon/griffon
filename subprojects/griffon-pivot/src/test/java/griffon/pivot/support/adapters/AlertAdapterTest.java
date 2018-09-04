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

public class AlertAdapterTest {
    private final AlertAdapter adapter = new AlertAdapter();

    @Test
    public void testMessageTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMessageTypeChanged());
        adapter.messageTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setMessageTypeChanged(callable);
        adapter.messageTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMessageChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMessageChanged());
        adapter.messageChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setMessageChanged(callable);
        adapter.messageChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testBodyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getBodyChanged());
        adapter.bodyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setBodyChanged(callable);
        adapter.bodyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testOptionInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getOptionInserted());
        adapter.optionInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setOptionInserted(callable);
        adapter.optionInserted(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testOptionsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getOptionsRemoved());
        adapter.optionsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setOptionsRemoved(callable);
        adapter.optionsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedOptionChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedOptionChanged());
        adapter.selectedOptionChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setSelectedOptionChanged(callable);
        adapter.selectedOptionChanged(null, 0);
        assertTrue(invoked[0]);
    }

}
