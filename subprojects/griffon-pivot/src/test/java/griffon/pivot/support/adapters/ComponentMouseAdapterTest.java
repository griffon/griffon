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

import griffon.core.CallableWithArgs;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ComponentMouseAdapterTest {
    private ComponentMouseAdapter adapter = new ComponentMouseAdapter();

    @Test
    public void testMouseOut() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            }
        };

        assertNull(adapter.getMouseOut());
        adapter.mouseOut(null);
        assertFalse(invoked[0]);

        adapter.setMouseOut(callable);
        adapter.mouseOut(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMouseOver() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            }
        };

        assertNull(adapter.getMouseOver());
        adapter.mouseOver(null);
        assertFalse(invoked[0]);

        adapter.setMouseOver(callable);
        adapter.mouseOver(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testMouseMove() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Boolean> callable = new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return false;
            }
        };

        assertNull(adapter.getMouseMove());
        adapter.mouseMove(null, 0, 0);
        assertFalse(invoked[0]);

        adapter.setMouseMove(callable);
        adapter.mouseMove(null, 0, 0);
        assertTrue(invoked[0]);

        adapter.setMouseMove(new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return true;
            }
        });
        adapter.mouseMove(null, 0, 0);
        assertTrue(invoked[0]);
    }

}
