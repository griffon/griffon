/*
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

public class ImageViewBindingAdapterTest {
    private ImageViewBindingAdapter adapter = new ImageViewBindingAdapter();

    @Test
    public void testImageKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getImageKeyChanged());
        adapter.imageKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setImageKeyChanged(callable);
        adapter.imageKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testImageBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getImageBindTypeChanged());
        adapter.imageBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setImageBindTypeChanged(callable);
        adapter.imageBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testImageBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getImageBindMappingChanged());
        adapter.imageBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setImageBindMappingChanged(callable);
        adapter.imageBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

}
