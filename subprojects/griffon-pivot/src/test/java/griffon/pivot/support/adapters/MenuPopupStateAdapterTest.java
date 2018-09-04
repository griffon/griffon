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

public class MenuPopupStateAdapterTest {
    private final MenuPopupStateAdapter adapter = new MenuPopupStateAdapter();

    @Test
    public void testPreviewMenuPopupClose() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getPreviewMenuPopupClose());
        adapter.previewMenuPopupClose(null, false);
        assertFalse(invoked[0]);

        adapter.setPreviewMenuPopupClose(callable);
        adapter.previewMenuPopupClose(null, false);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMenuPopupClosed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMenuPopupClosed());
        adapter.menuPopupClosed(null);
        assertFalse(invoked[0]);

        adapter.setMenuPopupClosed(callable);
        adapter.menuPopupClosed(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMenuPopupCloseVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMenuPopupCloseVetoed());
        adapter.menuPopupCloseVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setMenuPopupCloseVetoed(callable);
        adapter.menuPopupCloseVetoed(null, null);
        assertTrue(invoked[0]);
    }

}
