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
package griffon.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.5.0
 */
public interface ObservableContext extends Context {
    void addContextEventListener(@Nonnull ContextEventListener listener);

    void removeContextEventListener(@Nonnull ContextEventListener listener);

    @Nonnull
    ContextEventListener[] getContextEventListeners();

    public static interface ContextEventListener {
        void contextChanged(@Nonnull ContextEvent contextEvent);
    }

    public static class ContextEvent {
        private final Type type;
        private final String key;
        private final Object oldValue;
        private final Object newValue;

        public ContextEvent(@Nonnull Type type, @Nonnull String key, @Nullable Object oldValue, @Nullable Object newValue) {
            this.type = requireNonNull(type, "Argument 'type' must not be null");
            this.key = requireNonBlank(key, "Argument 'key' must not be null");
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Nonnull
        public Type getType() {
            return type;
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

            ContextEvent that = (ContextEvent) o;

            if (!key.equals(that.key)) return false;
            if (newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) return false;
            if (oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null) return false;
            if (type != that.type) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + key.hashCode();
            result = 31 * result + (oldValue != null ? oldValue.hashCode() : 0);
            result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ContextEvent{");
            sb.append("type=").append(type);
            sb.append(", key='").append(key).append('\'');
            sb.append(", oldValue=").append(oldValue);
            sb.append(", newValue=").append(newValue);
            sb.append('}');
            return sb.toString();
        }

        public enum Type {
            ADD, REMOVE, UPDATE
        }
    }
}
