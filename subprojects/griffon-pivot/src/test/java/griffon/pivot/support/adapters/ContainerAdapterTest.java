/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

public class ContainerAdapterTest {
    private final ContainerAdapter adapter = new ContainerAdapter();

    @Test
    public void testComponentInserted() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getComponentInserted());
        adapter.componentInserted(null, 0);
        assertFalse(invoked[0]);

        adapter.setComponentInserted(callable);
        adapter.componentInserted(null, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testComponentsRemoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getComponentsRemoved());
        adapter.componentsRemoved(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setComponentsRemoved(callable);
        adapter.componentsRemoved(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testFocusTraversalPolicyChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getFocusTraversalPolicyChanged());
        adapter.focusTraversalPolicyChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setFocusTraversalPolicyChanged(callable);
        adapter.focusTraversalPolicyChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testComponentMoved() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = args -> {
            invoked[0] = true;
            return null;
        };

        assertNull(adapter.getComponentMoved());
        adapter.componentMoved(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setComponentMoved(callable);
        adapter.componentMoved(null, 0, 0);
        assertTrue(invoked[0]);
    }

}
