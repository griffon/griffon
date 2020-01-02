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

public class FileBrowserSheetAdapterTest {
    private final FileBrowserSheetAdapter adapter = new FileBrowserSheetAdapter();

    @Test
    public void testRootDirectoryChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getRootDirectoryChanged());
        adapter.rootDirectoryChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setRootDirectoryChanged(callable);
        adapter.rootDirectoryChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedFilesChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedFilesChanged());
        adapter.selectedFilesChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedFilesChanged(callable);
        adapter.selectedFilesChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDisabledFileFilterChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getDisabledFileFilterChanged());
        adapter.disabledFileFilterChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setDisabledFileFilterChanged(callable);
        adapter.disabledFileFilterChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testModeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getModeChanged());
        adapter.modeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setModeChanged(callable);
        adapter.modeChanged(null, null);
        assertTrue(invoked[0]);
    }

}
