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
package griffon.util;

import javax.annotation.Nonnull;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import static griffon.util.GriffonNameUtils.requireNonBlank;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class PropertiesResourceBundle extends ResourceBundle {
    private final Map<String, Object> storage;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PropertiesResourceBundle(@Nonnull Properties properties) {
        storage = new LinkedHashMap(properties);
    }

    public Object handleGetObject(String key) {
        requireNonBlank(key, "Argument 'key' must not be null");
        return storage.get(key);
    }

    public Enumeration<String> getKeys() {
        ResourceBundle parent = this.parent;
        final Enumeration<String> parentEnumeration = parent != null ? parent.getKeys() : null;
        final Set<String> keySet = storage.keySet();
        final Iterator<String> iterator = keySet.iterator();

        return new Enumeration<String>() {
            private String next = null;

            @Override
            public boolean hasMoreElements() {
                if (this.next == null) {
                    if (iterator.hasNext()) {
                        this.next = iterator.next();
                    } else if (parentEnumeration != null) {
                        while (this.next == null && parentEnumeration.hasMoreElements()) {
                            this.next = parentEnumeration.nextElement();
                            if (keySet.contains(this.next)) {
                                this.next = null;
                            }
                        }
                    }
                }

                return this.next != null;
            }

            @Override
            public String nextElement() {
                if (hasMoreElements()) {
                    String key = this.next;
                    this.next = null;
                    return key;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    @Override
    protected Set<String> handleKeySet() {
        return storage.keySet();
    }
}
