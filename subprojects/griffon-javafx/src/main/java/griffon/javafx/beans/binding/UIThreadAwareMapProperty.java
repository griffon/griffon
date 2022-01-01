/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.javafx.beans.binding;

import griffon.annotations.core.Nonnull;
import javafx.beans.InvalidationListener;
import javafx.beans.property.MapProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
class UIThreadAwareMapProperty<K, V> extends MapPropertyDecorator<K, V> implements UIThreadAware {
    UIThreadAwareMapProperty(@Nonnull MapProperty<K, V> delegate) {
        super(delegate);
    }

    @Override
    public void set(final ObservableMap<K, V> value) {
        if (isFxApplicationThread()) {
            getDelegate().set(value);
        } else {
            runLater(() -> getDelegate().set(value));
        }
    }

    @Override
    public void addListener(MapChangeListener<? super K, ? super V> listener) {
        if (listener instanceof UIThreadAware) {
            getDelegate().addListener(listener);
        } else {
            getDelegate().addListener(new UIThreadAwareMapChangeListener<>(listener));
        }
    }

    @Override
    public void removeListener(MapChangeListener<? super K, ? super V> listener) {
        if (listener instanceof UIThreadAware) {
            getDelegate().removeListener(listener);
        } else {
            getDelegate().removeListener(new UIThreadAwareMapChangeListener<>(listener));
        }
    }

    @Override
    public void addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
        if (listener instanceof UIThreadAware) {
            getDelegate().addListener(listener);
        } else {
            getDelegate().addListener(new UIThreadAwareChangeListener<>(listener));
        }
    }

    @Override
    public void removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
        if (listener instanceof UIThreadAware) {
            getDelegate().removeListener(listener);
        } else {
            getDelegate().removeListener(new UIThreadAwareChangeListener<>(listener));
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        if (listener instanceof UIThreadAware) {
            getDelegate().addListener(listener);
        } else {
            getDelegate().addListener(new UIThreadAwareInvalidationListener(listener));
        }
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        if (listener instanceof UIThreadAware) {
            getDelegate().removeListener(listener);
        } else {
            getDelegate().removeListener(new UIThreadAwareInvalidationListener(listener));
        }
    }
}
