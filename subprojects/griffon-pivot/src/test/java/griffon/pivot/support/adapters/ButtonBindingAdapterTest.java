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

public class ButtonBindingAdapterTest {
    private ButtonBindingAdapter adapter = new ButtonBindingAdapter();

    @Test
    public void testButtonDataKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getButtonDataKeyChanged());
        adapter.buttonDataKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setButtonDataKeyChanged(callable);
        adapter.buttonDataKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testButtonDataBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getButtonDataBindTypeChanged());
        adapter.buttonDataBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setButtonDataBindTypeChanged(callable);
        adapter.buttonDataBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testButtonDataBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getButtonDataBindMappingChanged());
        adapter.buttonDataBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setButtonDataBindMappingChanged(callable);
        adapter.buttonDataBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedKeyChanged());
        adapter.selectedKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedKeyChanged(callable);
        adapter.selectedKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedBindTypeChanged());
        adapter.selectedBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedBindTypeChanged(callable);
        adapter.selectedBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testSelectedBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getSelectedBindMappingChanged());
        adapter.selectedBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setSelectedBindMappingChanged(callable);
        adapter.selectedBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testStateKeyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getStateKeyChanged());
        adapter.stateKeyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setStateKeyChanged(callable);
        adapter.stateKeyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testStateBindTypeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getStateBindTypeChanged());
        adapter.stateBindTypeChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setStateBindTypeChanged(callable);
        adapter.stateBindTypeChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testStateBindMappingChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getStateBindMappingChanged());
        adapter.stateBindMappingChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setStateBindMappingChanged(callable);
        adapter.stateBindMappingChanged(null, null);
        assertTrue(invoked[0]);
    }

}
