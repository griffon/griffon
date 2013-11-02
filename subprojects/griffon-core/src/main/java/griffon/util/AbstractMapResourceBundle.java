/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static griffon.util.GriffonNameUtils.requireNonBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractMapResourceBundle extends ResourceBundle {
    private final Map<String, Object> entries = new LinkedHashMap<>();

    public AbstractMapResourceBundle() {
        initialize(entries);
    }

    protected abstract void initialize(@Nonnull Map<String, Object> entries);

    @Nullable
    @Override
    protected final Object handleGetObject(@Nonnull String key) {
        return entries.get(requireNonBlank(key, "Argument 'key' cannot be blank"));
    }

    @Nonnull
    @Override
    public final Enumeration<String> getKeys() {
        return new IteratorAsEnumeration<>(entries.keySet().iterator());
    }

    private static class IteratorAsEnumeration<E> implements Enumeration<E> {
        private final Iterator<E> iterator;

        public IteratorAsEnumeration(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public E nextElement() {
            return iterator.next();
        }
    }
}
