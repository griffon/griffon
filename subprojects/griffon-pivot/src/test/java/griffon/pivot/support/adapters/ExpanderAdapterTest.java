/*
 * Copyright 2008-2016 the original author or authors.
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

public class ExpanderAdapterTest {
    private ExpanderAdapter adapter = new ExpanderAdapter();

    @Test
    public void testCollapsibleChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getCollapsibleChanged());
        adapter.collapsibleChanged(null);
        assertFalse(invoked[0]);

        adapter.setCollapsibleChanged(callable);
        adapter.collapsibleChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPreviewExpandedChange() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewExpandedChange());
        adapter.previewExpandedChange(null);
        assertFalse(invoked[0]);

        adapter.setPreviewExpandedChange(callable);
        adapter.previewExpandedChange(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testExpandedChangeVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getExpandedChangeVetoed());
        adapter.expandedChangeVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setExpandedChangeVetoed(callable);
        adapter.expandedChangeVetoed(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testExpandedChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getExpandedChanged());
        adapter.expandedChanged(null);
        assertFalse(invoked[0]);

        adapter.setExpandedChanged(callable);
        adapter.expandedChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTitleChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTitleChanged());
        adapter.titleChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTitleChanged(callable);
        adapter.titleChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testContentChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getContentChanged());
        adapter.contentChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setContentChanged(callable);
        adapter.contentChanged(null, null);
        assertTrue(invoked[0]);
    }

}
