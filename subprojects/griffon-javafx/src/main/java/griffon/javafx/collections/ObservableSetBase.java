/*
 * Copyright 2008-2017 the original author or authors.
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
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.AbstractSet;

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
}
