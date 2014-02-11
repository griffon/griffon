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

import org.junit.Test;
import static org.junit.Assert.*;
import griffon.core.CallableWithArgs;

public class MeterAdapterTest {
    private MeterAdapter adapter = new MeterAdapter();

    @Test
    public void testOrientationChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getOrientationChanged());
        adapter.orientationChanged(null);
        assertFalse(invoked[0]);

        adapter.setOrientationChanged(callable);
        adapter.orientationChanged(null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testTextChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getTextChanged());
        adapter.textChanged(null, null);
        assertFalse(invoked[0]);

        adapter.setTextChanged(callable);
        adapter.textChanged(null, null);
        assertTrue(invoked[0]);
    }

    @Test
    public void testPercentageChanged() {
        final boolean[] invoked = new boolean[1];
        CallableWithArgs<Void> callable = new CallableWithArgs<Void>() {
            public Void call(Object... args) {
                invoked[0] = true;
                return null;
            } 
        };

        assertNull(adapter.getPercentageChanged());
        adapter.percentageChanged(null, 0d);
        assertFalse(invoked[0]);

        adapter.setPercentageChanged(callable);
        adapter.percentageChanged(null, 0d);
        assertTrue(invoked[0]);
    }

}
