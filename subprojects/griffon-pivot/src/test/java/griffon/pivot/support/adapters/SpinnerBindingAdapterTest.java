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

public class SpinnerBindingAdapterTest {
    private SpinnerBindingAdapter adapter = new SpinnerBindingAdapter();

    @Test
    public void testSelectedItemKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedItemKeyChanged());
        adapter.selectedItemKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedItemKeyChanged(callable);
        adapter.selectedItemKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedItemBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedItemBindTypeChanged());
        adapter.selectedItemBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedItemBindTypeChanged(callable);
        adapter.selectedItemBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedItemBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedItemBindMappingChanged());
        adapter.selectedItemBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedItemBindMappingChanged(callable);
        adapter.selectedItemBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSpinnerDataKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSpinnerDataKeyChanged());
        adapter.spinnerDataKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSpinnerDataKeyChanged(callable);
        adapter.spinnerDataKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSpinnerDataBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSpinnerDataBindTypeChanged());
        adapter.spinnerDataBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSpinnerDataBindTypeChanged(callable);
        adapter.spinnerDataBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSpinnerDataBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSpinnerDataBindMappingChanged());
        adapter.spinnerDataBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSpinnerDataBindMappingChanged(callable);
        adapter.spinnerDataBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

}
