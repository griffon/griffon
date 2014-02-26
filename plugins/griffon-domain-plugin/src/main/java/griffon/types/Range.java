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

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
@ThreadSafe
public abstract class Range<T extends Comparable> implements Iterable<T> {
    private final T from;
    private final T to;

    public Range(@Nonnull T from, @Nonnull T to) {
        this.from = requireNonNull(from, "Argument 'from' cannot be null");
        this.to = requireNonNull(to, "Argument 'to' cannot be null");
    }

    @Nonnull
    public T getFrom() {
        return from;
    }

    @Nonnull
    public T getTo() {
        return to;
    }

    @SuppressWarnings("unchecked")
    public final Class<T> getType() {
        return (Class<T>) from.getClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range<?> that = (Range<?>) o;

        return from.equals(that.from) && to.equals(that.to);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + from + ".." + to + "]";
    }

    public abstract boolean contains(T value);
}
