/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package org.codehaus.griffon.compile.core.processor.annotation;

import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.kordamp.jipsy.processor.Initializer;
import org.kordamp.jipsy.processor.LogLocation;
import org.kordamp.jipsy.processor.Logger;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Andres Almiray
 */
public final class AnnotationHandlerCollector {
    private final Map<String, String> handlers = new TreeMap<>();
    private final Map<String, String> cached = new TreeMap<>();

    private final Initializer initializer;
    private final Logger logger;

    public AnnotationHandlerCollector(Initializer initializer, Logger logger) {
        this.initializer = initializer;
        this.logger = logger;
    }

    public boolean isModified() {
        if (cached.size() != handlers.size()) {
            return true;
        }

        for (Map.Entry<String, String> e : cached.entrySet()) {
            if (!handlers.containsKey(e.getKey())) {
                return true;
            }
            if (!e.getValue().equals(handlers.get(e.getKey()))) {
                return true;
            }
        }

        return false;
    }

    public String getAnnotationHandler(String type, String handlerType) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (!handlers.containsKey(type)) {
            handlers.put(type, handlerType);
        }
        return handlers.get(type);
    }

    public void load() {
        CharSequence initialData = initializer.initialData(AnnotationHandler.class.getName());
        if (initialData != null) {
            fromList(initialData.toString());
        }
        this.cached.putAll(handlers);
    }

    public void removeAnnotationHandler(String handler) {
        if (handler == null) {
            throw new NullPointerException("type");
        }

        logger.note(LogLocation.LOG_FILE, "Removing " + handler);
        Set<String> keys = new LinkedHashSet<>();
        for (String key : handlers.keySet()) {
            if (handler.equals(handlers.get(key))) {
                keys.add(key);
            }
        }
        for (String key : keys) {
            handlers.remove(key);
        }
    }

    @Override
    public String toString() {
        return handlers.toString();
    }

    public String toList() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : handlers.entrySet()) {
            sb.append(e.getKey())
                .append("=")
                .append(e.getValue())
                .append("\n");
        }
        return sb.toString();
    }

    public Map<String, String> handlers() {
        return Collections.unmodifiableMap(handlers);
    }

    public void fromList(String input) {
        if (input == null) {
            throw new NullPointerException("input");
        }
        String[] lines = input.split("\\n");
        for (String line : lines) {
            if (line.startsWith("#")) continue;
            if (line.trim().length() > 0) {
                String[] entry = line.trim().split("=");
                handlers.put(entry[0], entry[1]);
            }
        }
    }
}
