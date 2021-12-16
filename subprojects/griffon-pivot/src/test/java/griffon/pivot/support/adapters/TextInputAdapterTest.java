/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

public class TextInputAdapterTest {
    private final TextInputAdapter adapter = new TextInputAdapter();

    @Test
    public void testTextValidChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTextValidChanged());
        adapter.textValidChanged(null);
        assertFalse(invoked[0]);

        adapter.setTextValidChanged(callable);
        adapter.textValidChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTextSizeChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTextSizeChanged());
        adapter.textSizeChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setTextSizeChanged(callable);
        adapter.textSizeChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMaximumLengthChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getMaximumLengthChanged());
        adapter.maximumLengthChanged(null, 0);
        assertFalse(invoked[0]);

        adapter.setMaximumLengthChanged(callable);
        adapter.maximumLengthChanged(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPasswordChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getPasswordChanged());
        adapter.passwordChanged(null);
        assertFalse(invoked[0]);

        adapter.setPasswordChanged(callable);
        adapter.passwordChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPromptChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getPromptChanged());
        adapter.promptChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setPromptChanged(callable);
        adapter.promptChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTextValidatorChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getTextValidatorChanged());
        adapter.textValidatorChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTextValidatorChanged(callable);
        adapter.textValidatorChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testStrictValidationChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getStrictValidationChanged());
        adapter.strictValidationChanged(null);
        assertFalse(invoked[0]);

        adapter.setStrictValidationChanged(callable);
        adapter.strictValidationChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testEditableChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getEditableChanged());
        adapter.editableChanged(null);
        assertFalse(invoked[0]);

        adapter.setEditableChanged(callable);
        adapter.editableChanged(null);
        assertTrue(invoked[0]);
    }

}
