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
package griffon.builder.pivot

/**
 * @author Andres Almiray
 */
final class FixedIterator implements Iterator {
    private final iterable
    private final boolean mutable
    private int index = 0;

    FixedIterator(iterable, boolean mutable = false) {
        this.iterable = iterable
        this.mutable = mutable
    }

    boolean hasNext() {
        return index > -1 && index < size()
    }

    Object next() {
        return iterable.get(index++)
    }

    void remove() {
        if (!mutable) {
            throw new UnsupportedOperationException("Immutable iterator!")
        }
        if (hasNext()) iterable.remove(index--)
    }

    private int size() {
        MetaClass mc = iterable.metaClass
        if (mc.respondsTo(iterable, 'getLength')) return iterable.getLength()
        if (mc.respondsTo(iterable, 'getSize')) return iterable.getSize()
        if (mc.respondsTo(iterable, 'getCount')) return iterable.getCount()
        -1
    }
}
