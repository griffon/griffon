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

import static griffon.util.GriffonNameUtils.requireNonBlank;

/**
 * @author Andres Almiray
 */
public class PreferenceChangeEvent {
    private final String path;
    private final String key;
    private Object oldValue;
    private Object newValue;

    public PreferenceChangeEvent(@Nonnull String path, @Nonnull String key, @Nullable Object oldValue, @Nullable Object newValue) {
        this.path = requireNonBlank(path, "Argument 'path' cannot be blank");
        this.key = requireNonBlank(key, "Argument 'key' cannot be blank");
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Nonnull
    public String getPath() {
        return path;
    }

    @Nonnull
    public String getKey() {
        return key;
    }

    @Nullable
    public Object getOldValue() {
        return oldValue;
    }

    @Nullable
    public Object getNewValue() {
        return newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreferenceChangeEvent that = (PreferenceChangeEvent) o;

        return path.equals(that.path) &&
            key.equals(that.key) &&
            !(newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) &&
            !(oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null);
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + (oldValue != null ? oldValue.hashCode() : 0);
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PreferenceChangeEvent{" +
            "path='" + path + '\'' +
            ", key='" + key + '\'' +
            ", oldValue=" + oldValue +
            ", newValue=" + newValue +
            '}';
    }
}
