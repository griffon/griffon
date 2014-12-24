/*
 * Copyright 2008-2015 the original author or authors.
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

public class TextInputContentAdapterTest {
    private TextInputContentAdapter adapter = new TextInputContentAdapter();

    @Test
    public void testTextChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTextChanged());
        adapter.textChanged(null);
        assertFalse(invoked[0]);

        adapter.setTextChanged(callable);
        adapter.textChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPreviewInsertText() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewInsertText());
        adapter.previewInsertText(null, null, 0);
        assertFalse(invoked[0]);

        adapter.setPreviewInsertText(callable);
        adapter.previewInsertText(null, null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTextInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTextInserted());
        adapter.textInserted(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setTextInserted(callable);
        adapter.textInserted(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testInsertTextVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getInsertTextVetoed());
        adapter.insertTextVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setInsertTextVetoed(callable);
        adapter.insertTextVetoed(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPreviewRemoveText() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewRemoveText());
        adapter.previewRemoveText(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setPreviewRemoveText(callable);
        adapter.previewRemoveText(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTextRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTextRemoved());
        adapter.textRemoved(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setTextRemoved(callable);
        adapter.textRemoved(null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testRemoveTextVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getRemoveTextVetoed());
        adapter.removeTextVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setRemoveTextVetoed(callable);
        adapter.removeTextVetoed(null, null);
        assertTrue(invoked[0]);
    }

}
