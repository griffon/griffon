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

public class ComponentMouseButtonAdapterTest {
    private final ComponentMouseButtonAdapter adapter = new ComponentMouseButtonAdapter();

    @Test
    public void testMouseClick() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Boolean> callable = args -> {
            invoked[0] = true;
            return false;
        };

        assertNull(adapter.getMouseClick());
        adapter.mouseClick(null, null, 0, 0, 0);
        assertFalse(invoked[0]);

        adapter.setMouseClick(callable);
        adapter.mouseClick(null, null, 0, 0, 0);
        assertTrue(invoked[0]);

        adapter.setMouseClick(args -> {
            invoked[0] = true;
            return true;
        });
        adapter.mouseClick(null, null, 0, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMouseDown() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Boolean> callable = args -> {
            invoked[0] = true;
            return false;
        };

        assertNull(adapter.getMouseDown());
        adapter.mouseDown(null, null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setMouseDown(callable);
        adapter.mouseDown(null, null, 0, 0);
        assertTrue(invoked[0]);

        adapter.setMouseDown(args -> {
            invoked[0] = true;
            return true;
        });
        adapter.mouseDown(null, null, 0, 0);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMouseUp() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Boolean> callable = args -> {
            invoked[0] = true;
            return false;
        };

        assertNull(adapter.getMouseUp());
        adapter.mouseUp(null, null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setMouseUp(callable);
        adapter.mouseUp(null, null, 0, 0);
        assertTrue(invoked[0]);

        adapter.setMouseUp(args -> {
            invoked[0] = true;
            return true;
        });
        adapter.mouseUp(null, null, 0, 0);
        assertTrue(invoked[0]);
    }

}
