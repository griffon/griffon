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

public class CalendarButtonBindingAdapterTest {
    private final CalendarButtonBindingAdapter adapter = new CalendarButtonBindingAdapter();

    @Test
    public void testSelectedDateKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedDateKeyChanged());
        adapter.selectedDateKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedDateKeyChanged(callable);
        adapter.selectedDateKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedDateBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedDateBindTypeChanged());
        adapter.selectedDateBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedDateBindTypeChanged(callable);
        adapter.selectedDateBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedDateBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedDateBindMappingChanged());
        adapter.selectedDateBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedDateBindMappingChanged(callable);
        adapter.selectedDateBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

}
