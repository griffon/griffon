/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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

public class ColorChooserBindingAdapterTest {
    private final ColorChooserBindingAdapter adapter = new ColorChooserBindingAdapter();

    @Test
    public void testSelectedColorBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedColorBindTypeChanged());
        adapter.selectedColorBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedColorBindTypeChanged(callable);
        adapter.selectedColorBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedColorKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedColorKeyChanged());
        adapter.selectedColorKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedColorKeyChanged(callable);
        adapter.selectedColorKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedColorBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedColorBindMappingChanged());
        adapter.selectedColorBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedColorBindMappingChanged(callable);
        adapter.selectedColorBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

}
