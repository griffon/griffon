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

public class TabPaneSelectionAdapterTest {
    private TabPaneSelectionAdapter adapter = new TabPaneSelectionAdapter();

    @Test
    public void testPreviewSelectedIndexChange() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<org.apache.pivot.util.Vote> callable = new CallableWithArgs<org.apache.pivot.util.Vote>() {
            public org.apache.pivot.util.Vote call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPreviewSelectedIndexChange());
        adapter.previewSelectedIndexChange(null, 0);
        assertFalse(invoked[0]);

        adapter.setPreviewSelectedIndexChange(callable);
        adapter.previewSelectedIndexChange(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedIndexChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedIndexChanged());
        adapter.selectedIndexChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setSelectedIndexChanged(callable);
        adapter.selectedIndexChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedIndexChangeVetoed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedIndexChangeVetoed());
        adapter.selectedIndexChangeVetoed(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedIndexChangeVetoed(callable);
        adapter.selectedIndexChangeVetoed(null, null);
        assertTrue(invoked[0]);
    }

}
