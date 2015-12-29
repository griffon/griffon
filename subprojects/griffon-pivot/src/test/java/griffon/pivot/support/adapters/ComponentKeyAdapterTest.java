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

public class ComponentKeyAdapterTest {
    private ComponentKeyAdapter adapter = new ComponentKeyAdapter();

    @Test
    public void testKeyTyped() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Boolean> callable = new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return false;
            }
        };

        assertNull(adapter.getKeyTyped());
        adapter.keyTyped(null, '0');
        assertFalse(invoked[0]);

        adapter.setKeyTyped(callable);
        adapter.keyTyped(null, '0');
        assertTrue(invoked[0]);

        adapter.setKeyTyped(new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return true;
            }
        });
        adapter.keyTyped(null, '0');
        assertTrue(invoked[0]);
    }

    @Test
    public void testKeyPressed() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Boolean> callable = new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return false;
            }
        };

        assertNull(adapter.getKeyPressed());
        adapter.keyPressed(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setKeyPressed(callable);
        adapter.keyPressed(null, 0, null);
        assertTrue(invoked[0]);

        adapter.setKeyPressed(new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return true;
            }
        });
        adapter.keyPressed(null, 0, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testKeyReleased() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Boolean> callable = new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return false;
            }
        };

        assertNull(adapter.getKeyReleased());
        adapter.keyReleased(null, 0, null);
        assertFalse(invoked[0]);

        adapter.setKeyReleased(callable);
        adapter.keyReleased(null, 0, null);
        assertTrue(invoked[0]);

        adapter.setKeyReleased(new CallableWithArgs<Boolean>() {
            public Boolean call(Object... args) {
                invoked[0] = true;
                return true;
            }
        });
        adapter.keyReleased(null, 0, null);
        assertTrue(invoked[0]);
    }

}
