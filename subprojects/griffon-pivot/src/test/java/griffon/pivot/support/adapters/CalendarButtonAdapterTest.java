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

public class CalendarButtonAdapterTest {
    private final CalendarButtonAdapter adapter = new CalendarButtonAdapter();

    @Test
    public void testYearChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getYearChanged());
        adapter.yearChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setYearChanged(callable);
        adapter.yearChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMonthChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMonthChanged());
        adapter.monthChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setMonthChanged(callable);
        adapter.monthChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testLocaleChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getLocaleChanged());
        adapter.localeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setLocaleChanged(callable);
        adapter.localeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDisabledDateFilterChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getDisabledDateFilterChanged());
        adapter.disabledDateFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDisabledDateFilterChanged(callable);
        adapter.disabledDateFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

}
