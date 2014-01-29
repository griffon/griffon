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

package griffon.plugins.preferences;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public interface PreferencesNode {
    String PATH_SEPARATOR = "/";

    @Nonnull
    String name();

    @Nonnull
    String path();

    @Nullable
    PreferencesNode parent();

    @Nullable
    Object getAt(@Nonnull String key);

    void putAt(@Nonnull String key, @Nullable Object value);

    boolean isRoot();

    void remove(@Nonnull String key);

    void clear();

    @Nonnull
    String[] keys();

    boolean containsNode(@Nonnull Class<?> clazz);

    boolean containsNode(@Nonnull String path);

    boolean containsKey(@Nonnull String key);

    @Nonnull
    Map<String, PreferencesNode> children();

    @Nullable
    PreferencesNode node(@Nonnull Class<?> clazz);

    @Nullable
    PreferencesNode node(@Nonnull String path);

    @Nullable
    PreferencesNode removeNode(@Nonnull Class<?> clazz);

    @Nullable
    PreferencesNode removeNode(@Nonnull String path);

    @Nullable
    PreferencesNode getChildNode(@Nonnull String nodeName);

    @Nonnull
    PreferencesNode createChildNode(@Nonnull String nodeName);

    void storeChildNode(@Nonnull String nodeName, @Nonnull PreferencesNode node);

    @Nullable
    PreferencesNode removeChildNode(@Nonnull String nodeName);

    @Nonnull
    PreferencesNode merge(@Nonnull PreferencesNode other);
}
