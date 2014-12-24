/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.compile.core.processor.type;

import org.kordamp.jipsy.processor.Initializer;
import org.kordamp.jipsy.processor.LogLocation;
import org.kordamp.jipsy.processor.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.1.0
 */
public final class TypeCollector {
    private final Map<String, Type> types = new LinkedHashMap<>();
    private final Map<String, Type> cached = new LinkedHashMap<>();

    private final List<String> removed = new ArrayList<>();
    private final Initializer initializer;
    private final Logger logger;

    public TypeCollector(Initializer initializer, Logger logger) {
        this.initializer = initializer;
        this.logger = logger;
    }

    public void cache() {
        this.cached.putAll(types);
    }

    public boolean isModified() {
        if (cached.size() != types.size()) {
            return true;
        }

        for (Map.Entry<String, Type> e : cached.entrySet()) {
            if (!types.containsKey(e.getKey())) {
                return true;
            }
            if (!e.getValue().equals(types.get(e.getKey()))) {
                return true;
            }
        }

        return false;
    }

    public Type getType(String type) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (!types.containsKey(type)) {
            Type newType = new Type(logger, type);
            CharSequence initialData = initializer.initialData(type);
            if (initialData != null) {
                newType.fromProviderNamesList(initialData.toString());
                for (String provider : removed) {
                    newType.removeProvider(provider);
                }
            }
            types.put(type, newType);
        }
        return types.get(type);
    }

    public Collection<Type> types() {
        return Collections.unmodifiableMap(types).values();
    }

    public void removeProvider(String provider) {
        if (provider == null) {
            throw new NullPointerException("provider");
        }
        logger.note(LogLocation.LOG_FILE, "Removing " + provider);
        removed.add(provider);
        for (Type type : types.values()) {
            type.removeProvider(provider);
        }
    }

    @Override
    public String toString() {
        return types.values().toString();
    }
}
