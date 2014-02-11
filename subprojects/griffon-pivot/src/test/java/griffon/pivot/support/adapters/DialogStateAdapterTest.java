/*
 * Copyright 2008-2014 the original author or authors.
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

import org.junit.Test;
import static org.junit.Assert.*;
import griffon.core.CallableWithArgs;

public class DialogStateAdapterTest {
    private DialogStateAdapter adapter = new DialogStateAdapter();

    @Test
    public void testPreviewDialogClose() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewDialogClose());
        adapter.previewDialogClose(null, false);
        assertFalse(invoked[0]);

        adapter.setPreviewDialogClose(callable);
        adapter.previewDialogClose(null, false);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDialogCloseVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getDialogCloseVetoed());
        adapter.dialogCloseVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setDialogCloseVetoed(callable);
        adapter.dialogCloseVetoed(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testDialogClosed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getDialogClosed());
        adapter.dialogClosed(null, false);
        assertFalse(invoked[0]);

        adapter.setDialogClosed(callable);
        adapter.dialogClosed(null, false);
        assertTrue(invoked[0]);
    }

}
