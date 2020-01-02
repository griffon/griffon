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

public class FileBrowserAdapterTest {
    private final FileBrowserAdapter adapter = new FileBrowserAdapter();

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
    public void testSelectedFileAdded() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedFileAdded());
        adapter.selectedFileAdded(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedFileAdded(callable);
        adapter.selectedFileAdded(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedFileRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getSelectedFileRemoved());
        adapter.selectedFileRemoved(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedFileRemoved(callable);
        adapter.selectedFileRemoved(null, null);
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
    public void testMultiSelectChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMultiSelectChanged());
        adapter.multiSelectChanged(null);
        assertFalse(invoked[0]);

        adapter.setMultiSelectChanged(callable);
        adapter.multiSelectChanged(null);
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

}
