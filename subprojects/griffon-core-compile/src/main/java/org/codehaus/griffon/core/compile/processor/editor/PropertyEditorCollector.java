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
package org.codehaus.griffon.core.compile.processor.editor;

import org.kordamp.jipsy.processor.Initializer;
import org.kordamp.jipsy.processor.LogLocation;
import org.kordamp.jipsy.processor.Logger;

import java.beans.PropertyEditor;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Andres Almiray
 */
public final class PropertyEditorCollector {
    private final Map<String, String> editors = new TreeMap<>();
    private final Map<String, String> cached = new TreeMap<>();

    private final Initializer initializer;
    private final Logger logger;

    public PropertyEditorCollector(Initializer initializer, Logger logger) {
        this.initializer = initializer;
        this.logger = logger;
    }

    public String getPropertyEditor(String type, String editorType) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (!editors.containsKey(type)) {
            editors.put(type, editorType);
        }
        return editors.get(type);
    }

    public boolean isModified() {
        if (cached.size() != editors.size()) {
            return true;
        }

        for (Map.Entry<String, String> e : cached.entrySet()) {
            if (!editors.containsKey(e.getKey())) {
                return true;
            }
            if (!e.getValue().equals(editors.get(e.getKey()))) {
                return true;
            }
        }

        return false;
    }

    public void load() {
        CharSequence initialData = initializer.initialData(PropertyEditor.class.getName());
        if (initialData != null) {
            fromList(initialData.toString());
        }
        cached.putAll(editors);
    }

    public void removeEditor(String editor) {
        if (editor == null) {
            throw new NullPointerException("type");
        }

        logger.note(LogLocation.LOG_FILE, "Removing " + editor);
        Set<String> keys = new LinkedHashSet<>();
        for (String key : editors.keySet()) {
            if (editor.equals(editors.get(key))) {
                keys.add(key);
            }
        }
        for (String key : keys) {
            editors.remove(key);
        }
    }

    @Override
    public String toString() {
        return editors.toString();
    }

    public String toList() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : editors.entrySet()) {
            sb.append(e.getKey())
                .append("=")
                .append(e.getValue())
                .append("\n");
        }
        return sb.toString();
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
                editors.put(entry[0], entry[1]);
            }
        }
    }
}
