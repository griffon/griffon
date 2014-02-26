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

import static griffon.util.GriffonClassUtils.invokeExactStaticMethod;

/**
 * @author Andres Almiray
 */
@ThreadSafe
public class EnumRange<E extends Enum> extends Range<E> {
    public EnumRange(@Nonnull E from, @Nonnull E to) {
        super(from, to);
    }

    @Override
    public boolean contains(E value) {
        return value != null &&
            getFrom().ordinal() <= value.ordinal() &&
            value.ordinal() <= getTo().ordinal();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int cursor = getFrom().ordinal();

            @Override
            public boolean hasNext() {
                return cursor < getTo().ordinal() + 1;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                E[] values = (E[]) invokeExactStaticMethod(getType(), "values");
                return (E) values[cursor++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
