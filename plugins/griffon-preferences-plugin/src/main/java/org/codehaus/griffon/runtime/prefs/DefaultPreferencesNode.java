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
package org.codehaus.griffon.runtime.prefs;

import griffon.plugins.preferences.NodeChangeEvent;
import griffon.plugins.preferences.PreferenceChangeEvent;
import griffon.plugins.preferences.Preferences;
import griffon.plugins.preferences.PreferencesNode;
import griffon.util.TypeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultPreferencesNode extends AbstractPreferencesNode {
    private static final String ERROR_KEY_BLANK = "Argument 'key' must not be null";
    private static final String ERROR_NODE_NAME_BLANK = "Argument 'nodeName' must not be null";

    private final Object lock = new Object[0];
    @GuardedBy("lock")
    private final Map<String, Object> properties = new LinkedHashMap<>();
    @GuardedBy("lock")
    private final Map<String, PreferencesNode> nodes = new LinkedHashMap<>();

    public DefaultPreferencesNode(@Nonnull Preferences preferences, @Nonnull String name) {
        super(preferences, null, name);
    }

    public DefaultPreferencesNode(@Nonnull Preferences preferences, @Nonnull PreferencesNode parent, @Nonnull String name) {
        super(preferences, parent, name);
    }

    @Nullable
    public Object getAt(@Nonnull String key) {
        synchronized (lock) {
            return properties.get(requireNonBlank(key, ERROR_KEY_BLANK));
        }
    }

    public void putAt(@Nonnull String key, @Nullable Object value) {
        Object oldValue = null;
        synchronized (lock) {
            oldValue = properties.get(requireNonBlank(key, ERROR_KEY_BLANK));
            properties.put(key, value);
        }
        if (!TypeUtils.equals(oldValue, value)) {
            firePreferencesChanged(path(), key, oldValue, value);
        }
    }

    private void firePreferencesChanged(@Nonnull String path, @Nonnull String key, @Nullable Object oldValue, @Nullable Object newValue) {
        preferences.preferenceChanged(new PreferenceChangeEvent(path, key, oldValue, newValue));
    }

    public void remove(@Nonnull String key) {
        Object oldValue = null;
        synchronized (lock) {
            oldValue = properties.remove(requireNonBlank(key, ERROR_KEY_BLANK));
        }
        if (oldValue != null)
            firePreferencesChanged(path(), key, oldValue, null);
    }

    public void clear() {
        synchronized (lock) {
            properties.clear();
        }
    }

    public boolean containsKey(@Nonnull String key) {
        synchronized (lock) {
            return properties.containsKey(requireNonBlank(key, ERROR_KEY_BLANK));
        }
    }

    @Nonnull
    public String[] keys() {
        synchronized (lock) {
            return properties.keySet().toArray(new String[properties.size()]);
        }
    }

    @Nonnull
    public Map<String, PreferencesNode> children() {
        synchronized (lock) {
            return Collections.unmodifiableMap(nodes);
        }
    }

    @Nonnull
    public DefaultPreferencesNode createChildNode(@Nonnull String nodeName) {
        return new DefaultPreferencesNode(preferences, this, requireNonBlank(nodeName, ERROR_NODE_NAME_BLANK));
    }

    public void storeChildNode(@Nonnull String nodeName, @Nonnull PreferencesNode node) {
        requireNonBlank(nodeName, ERROR_NODE_NAME_BLANK);
        requireNonNull(node, "Argument 'node' must not be null");
        synchronized (lock) {
            nodes.put(nodeName, node);
        }
        preferences.nodeChanged(new NodeChangeEvent(node.path(), NodeChangeEvent.Type.ADDED));
    }

    @Nullable
    public PreferencesNode removeChildNode(@Nonnull String nodeName) {
        PreferencesNode node = null;
        synchronized (lock) {
            node = nodes.remove(requireNonBlank(nodeName, ERROR_NODE_NAME_BLANK));
        }
        if (node != null) {
            preferences.nodeChanged(new NodeChangeEvent(node.path(), NodeChangeEvent.Type.REMOVED));
        }
        return node;
    }

    @Nullable
    public PreferencesNode getChildNode(@Nonnull String nodeName) {
        synchronized (lock) {
            return nodes.get(requireNonBlank(nodeName, ERROR_NODE_NAME_BLANK));
        }
    }
}
