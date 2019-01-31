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

import com.sun.javafx.collections.MapListenerHelper;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.AbstractMap;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public abstract class ObservableMapBase<K, V> extends AbstractMap<K, V> implements ObservableMap<K, V> {
    private MapListenerHelper<K, V> listenerHelper;

    @Override
    public final void addListener(MapChangeListener<? super K, ? super V> listener) {
        listenerHelper = MapListenerHelper.addListener(listenerHelper, listener);
    }

    @Override
    public final void removeListener(MapChangeListener<? super K, ? super V> listener) {
        listenerHelper = MapListenerHelper.removeListener(listenerHelper, listener);
    }

    @Override
    public final void addListener(InvalidationListener listener) {
        listenerHelper = MapListenerHelper.addListener(listenerHelper, listener);
    }

    @Override
    public final void removeListener(InvalidationListener listener) {
        listenerHelper = MapListenerHelper.removeListener(listenerHelper, listener);
    }

    protected final void fireChange(MapChangeListener.Change<? extends K, ? extends V> change) {
        MapListenerHelper.fireValueChangedEvent(listenerHelper, change);
    }

    /**
     * Returns true if there are some listeners registered for this list.
     */
    protected final boolean hasListeners() {
        return MapListenerHelper.hasListeners(listenerHelper);
    }
}
