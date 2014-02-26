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
package griffon.types;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;

/**
 * @author Andres Almiray
 */
@ThreadSafe
public class IntRange extends Range<Integer> {
    public IntRange(@Nonnull Integer from, @Nonnull Integer to) {
        super(from, to);
    }

    @Override
    public boolean contains(Integer value) {
        return value != null && getFrom() <= value && value <= getTo();
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int cursor = getFrom();

            @Override
            public boolean hasNext() {
                return cursor < getTo() + 1;
            }

            @Override
            public Integer next() {
                return cursor++;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
