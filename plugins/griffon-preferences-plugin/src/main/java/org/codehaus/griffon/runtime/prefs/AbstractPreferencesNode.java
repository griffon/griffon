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

import griffon.plugins.preferences.Preferences;
import griffon.plugins.preferences.PreferencesNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractPreferencesNode implements PreferencesNode {
    protected final Preferences preferences;
    protected PreferencesNode parent;
    protected final String name;
    private String path;

    public AbstractPreferencesNode(@Nonnull Preferences preferences, @Nullable PreferencesNode parent, @Nonnull String name) {
        this.preferences = requireNonNull(preferences, "Argument 'preferences' must not be null");
        this.name = requireNonBlank(name, "Argument 'name' must not be null");
        this.parent = parent;
    }

    @Nonnull
    public String name() {
        return name;
    }

    @Nonnull
    public String path() {
        if (null == path) {
            if (null == parent) {
                path = PATH_SEPARATOR;
            } else if (parent.isRoot()) {
                path = parent.path() + name;
            } else {
                path = parent.path() + PATH_SEPARATOR + name;
            }
        }
        return path;
    }

    @Nullable
    public PreferencesNode parent() {
        return parent;
    }

    public boolean isRoot() {
        return path().equals(PATH_SEPARATOR);
    }

    @Nonnull
    public PreferencesNode merge(@Nonnull PreferencesNode other) {
        requireNonNull(other, "Argument 'other' must not be null");
        for (String key : other.keys()) {
            putAt(key, other.getAt(key));
        }
        for (Map.Entry<String, PreferencesNode> child : other.children().entrySet()) {
            final String childNodeName = child.getKey();
            PreferencesNode newChild = children().get(childNodeName);
            if (newChild == null) newChild = createChildNode(childNodeName);
            newChild.merge(child.getValue());
            storeChildNode(childNodeName, newChild);
        }
        return this;
    }

    public boolean containsNode(@Nonnull Class<?> clazz) {
        requireNonNull(clazz, "Argument 'clazz' must not be null");
        return containsNode(clazz.getName());
    }

    public boolean containsNode(@Nonnull String path) {
        requireNonNull(path, "Argument 'path' must not be null");
        String[] parsedPath = parsePath(path);
        return parsedPath.length != 0 && getChildNode(parsedPath[0]) != null;
    }

    @Nullable
    public PreferencesNode node(@Nonnull Class<?> clazz) {
        requireNonNull(clazz, "Argument 'clazz' must not be null");
        return node(clazz.getName());
    }

    @Nullable
    public PreferencesNode node(@Nonnull String path) {
        requireNonNull(path, "Argument 'path' must not be null");
        String[] parsedPath = parsePath(path);
        if (parsedPath.length == 0) return null;
        String nodeName = parsedPath[0];

        PreferencesNode node = getChildNode(nodeName);
        if (node == null) {
            node = createChildNode(nodeName);
            storeChildNode(nodeName, node);
        }
        if (!isBlank(parsedPath[1])) {
            node = node.node(parsedPath[1]);
        }

        return node;
    }

    @Nullable
    public PreferencesNode removeNode(@Nonnull Class<?> clazz) {
        requireNonNull(clazz, "Argument 'clazz' must not be null");
        return removeNode(clazz.getName());
    }

    @Nullable
    public PreferencesNode removeNode(@Nonnull String path) {
        String[] parsedPath = parsePath(path);
        if (parsedPath.length == 0) return null;
        String nodeName = parsedPath[0];

        PreferencesNode node = getChildNode(nodeName);
        if (node != null) {
            if (!isBlank(parsedPath[1])) {
                node = node.removeNode(parsedPath[1]);
            } else {
                node = removeChildNode(nodeName);
            }
        }

        return node;
    }

    @Nonnull
    private String[] parsePath(String path) {
        if (isBlank(path) ||
            (!isRoot() && (path.startsWith(PATH_SEPARATOR)) ||
                path.endsWith(PATH_SEPARATOR))) return new String[0];
        path = path.replace('.', PATH_SEPARATOR.charAt(0));
        if (isRoot() && path.startsWith(PATH_SEPARATOR)) {
            path = path.substring(1);
        }
        int split = path.indexOf(PATH_SEPARATOR);
        String head = split < 0 ? path : path.substring(0, split);
        String tail = split > 0 ? path.substring(split + 1) : null;
        return new String[]{head, tail};
    }
}
