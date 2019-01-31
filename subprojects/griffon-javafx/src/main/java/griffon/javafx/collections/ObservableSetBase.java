/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.javafx.collections;

import com.sun.javafx.collections.SetListenerHelper;
import griffon.annotations.core.Nonnull;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.AbstractSet;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public abstract class ObservableSetBase<E> extends AbstractSet<E> implements ObservableSet<E> {
    private SetListenerHelper<E> listenerHelper;

    @Override
    public final void addListener(InvalidationListener listener) {
        listenerHelper = SetListenerHelper.addListener(listenerHelper, listener);
    }

    @Override
    public final void removeListener(InvalidationListener listener) {
        listenerHelper = SetListenerHelper.removeListener(listenerHelper, listener);
    }

    @Override
    public final void addListener(SetChangeListener<? super E> listener) {
        listenerHelper = SetListenerHelper.addListener(listenerHelper, listener);
    }

    @Override
    public final void removeListener(SetChangeListener<? super E> listener) {
        listenerHelper = SetListenerHelper.removeListener(listenerHelper, listener);
    }

    protected final void fireChange(SetChangeListener.Change<? extends E> change) {
        SetListenerHelper.fireValueChangedEvent(listenerHelper, change);
    }

    /**
     * Returns true if there are some listeners registered for this list.
     */
    protected final boolean hasListeners() {
        return SetListenerHelper.hasListeners(listenerHelper);
    }

    public static class BaseAddChange<T> extends SetChangeListener.Change<T> {
        private final T added;

        public BaseAddChange(@Nonnull ObservableSet<T> set, @Nonnull T added) {
            super(requireNonNull(set, "Argument 'set' must not be null"));
            this.added = requireNonNull(added, "Argument 'added' must not be null");
        }

        @Override
        public boolean wasAdded() {
            return true;
        }

        @Override
        public boolean wasRemoved() {
            return false;
        }

        @Override
        public T getElementAdded() {
            return added;
        }

        @Override
        public T getElementRemoved() {
            return null;
        }

        @Override
        public String toString() {
            return "added " + added;
        }
    }

    public static class BaseRemoveChange<T> extends SetChangeListener.Change<T> {
        private final T removed;

        public BaseRemoveChange(@Nonnull ObservableSet<T> set, @Nonnull T removed) {
            super(requireNonNull(set, "Argument 'set' must not be null"));
            this.removed = requireNonNull(removed, "Argument 'removed' must not be null");
        }

        @Override
        public boolean wasAdded() {
            return false;
        }

        @Override
        public boolean wasRemoved() {
            return true;
        }

        @Override
        public T getElementAdded() {
            return null;
        }

        @Override
        public T getElementRemoved() {
            return removed;
        }

        @Override
        public String toString() {
            return "removed " + removed;
        }
    }
}
