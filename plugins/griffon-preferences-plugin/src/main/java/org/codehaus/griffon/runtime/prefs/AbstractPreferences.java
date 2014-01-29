/*
 * Copyright 2012-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.prefs;

import griffon.plugins.preferences.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractPreferences implements Preferences {
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' cannot be null";
    private static final String ERROR_EVENT_NULL = "Argument 'event' cannot be null";

    private final List<NodeChangeListener> nodeChangeListeners = new ArrayList<>();
    private final List<PreferenceChangeListener> changeListeners = new ArrayList<>();

    public void addNodeChangeListener(@Nonnull NodeChangeListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (nodeChangeListeners.contains(listener)) return;
        nodeChangeListeners.add(listener);
    }

    public void removeNodeChangeListener(@Nonnull NodeChangeListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        nodeChangeListeners.remove(listener);
    }

    @Nonnull
    public NodeChangeListener[] getNodeChangeListeners() {
        return nodeChangeListeners.toArray(new NodeChangeListener[nodeChangeListeners.size()]);
    }

    public void addPreferencesChangeListener(@Nonnull PreferenceChangeListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (changeListeners.contains(listener)) return;
        changeListeners.add(listener);
    }

    public void removePreferencesChangeListener(@Nonnull PreferenceChangeListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        changeListeners.remove(listener);
    }

    @Nonnull
    public PreferenceChangeListener[] getPreferencesChangeListeners() {
        return changeListeners.toArray(new PreferenceChangeListener[changeListeners.size()]);
    }

    public void preferenceChanged(@Nonnull PreferenceChangeEvent event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        for (PreferenceChangeListener listener : changeListeners) {
            listener.preferenceChanged(event);
        }
    }

    public void nodeChanged(@Nonnull NodeChangeEvent event) {
        requireNonNull(event, ERROR_EVENT_NULL);
        for (NodeChangeListener listener : nodeChangeListeners) {
            listener.nodeChanged(event);
        }
    }

    public boolean containsNode(@Nonnull Class<?> clazz) {
        return getRoot().containsNode(clazz);
    }

    public boolean containsNode(@Nonnull String path) {
        return getRoot().containsNode(path);
    }

    public PreferencesNode node(@Nonnull Class<?> clazz) {
        return getRoot().node(clazz);
    }

    public PreferencesNode node(@Nonnull String path) {
        return getRoot().node(path);
    }

    public PreferencesNode removeNode(@Nonnull Class<?> clazz) {
        return getRoot().removeNode(clazz);
    }

    public PreferencesNode removeNode(@Nonnull String path) {
        return getRoot().removeNode(path);
    }
}
