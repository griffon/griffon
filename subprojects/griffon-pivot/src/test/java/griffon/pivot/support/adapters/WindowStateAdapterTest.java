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

public class WindowStateAdapterTest {
    private WindowStateAdapter adapter = new WindowStateAdapter();

    @Test
    public void testPreviewWindowOpen() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewWindowOpen());
        adapter.previewWindowOpen(null);
        assertFalse(invoked[0]);

        adapter.setPreviewWindowOpen(callable);
        adapter.previewWindowOpen(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testWindowOpenVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getWindowOpenVetoed());
        adapter.windowOpenVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setWindowOpenVetoed(callable);
        adapter.windowOpenVetoed(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPreviewWindowClose() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewWindowClose());
        adapter.previewWindowClose(null);
        assertFalse(invoked[0]);

        adapter.setPreviewWindowClose(callable);
        adapter.previewWindowClose(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testWindowCloseVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getWindowCloseVetoed());
        adapter.windowCloseVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setWindowCloseVetoed(callable);
        adapter.windowCloseVetoed(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testWindowOpened() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getWindowOpened());
        adapter.windowOpened(null);
        assertFalse(invoked[0]);

        adapter.setWindowOpened(callable);
        adapter.windowOpened(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testWindowClosed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getWindowClosed());
        adapter.windowClosed(null, null, null);
        assertFalse(invoked[0]);

        adapter.setWindowClosed(callable);
        adapter.windowClosed(null, null, null);
        assertTrue(invoked[0]);
    }

}
