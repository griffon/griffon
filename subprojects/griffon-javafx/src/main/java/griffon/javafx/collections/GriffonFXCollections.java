/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.javafx.beans.binding.UIThreadAware;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public final class GriffonFXCollections {
    private static final String ERROR_SOURCE_NULL = "Argument 'source' must not be null";

    private GriffonFXCollections() {
        // prevent instantiation
    }

    /**
     * Wraps an <tt>ObservableList</tt>, publishing updates inside the UI thread.
     *
     * @param source the <tt>ObservableList</tt> to be wrapped
     * @param <E>    the list's parameter type.
     *
     * @return a new <tt>ObservableList</tt>
     */
    @Nonnull
    public static <E> ObservableList<E> uiThreadAwareObservableList(@Nonnull ObservableList<E> source) {
        requireNonNull(source, ERROR_SOURCE_NULL);
        return source instanceof UIThreadAware ? source : new UIThreadAwareObservableList<>(source);
    }

    private static class UIThreadAwareObservableList<E> extends DelegatingObservableList<E> implements UIThreadAware {
        protected UIThreadAwareObservableList(ObservableList<E> delegate) {
            super(delegate);
        }

        @Override
        protected void sourceChanged(@Nonnull final ListChangeListener.Change<? extends E> c) {
            if (Platform.isFxApplicationThread()) {
                fireChange(c);
            } else {
                Platform.runLater(() -> fireChange(c));
            }
        }
    }

    /**
     * Wraps an <tt>ObservableSet</tt>, publishing updates inside the UI thread.
     *
     * @param source the <tt>ObservableSet</tt> to be wrapped
     * @param <E>    the set's parameter type.
     *
     * @return a new <tt>ObservableSet</tt>
     */
    @Nonnull
    public static <E> ObservableSet<E> uiThreadAwareObservableSet(@Nonnull ObservableSet<E> source) {
        requireNonNull(source, ERROR_SOURCE_NULL);
        return source instanceof UIThreadAware ? source : new UIThreadAwareObservableSet<>(source);
    }

    private static class UIThreadAwareObservableSet<E> extends DelegatingObservableSet<E> implements UIThreadAware {
        protected UIThreadAwareObservableSet(ObservableSet<E> delegate) {
            super(delegate);
        }

        @Override
        protected void sourceChanged(@Nonnull final SetChangeListener.Change<? extends E> c) {
            if (Platform.isFxApplicationThread()) {
                fireChange(c);
            } else {
                Platform.runLater(() -> fireChange(c));
            }
        }
    }

    /**
     * Wraps an <tt>ObservableMap</tt>, publishing updates inside the UI thread.
     *
     * @param source the <tt>ObservableMap</tt> to be wrapped
     * @param <K>    the type of keys maintained by the map
     * @param <V>    the type of mapped values
     *
     * @return a new <tt>ObservableMap</tt>
     */
    @Nonnull
    public static <K, V> ObservableMap<K, V> uiThreadAwareObservableMap(@Nonnull ObservableMap<K, V> source) {
        requireNonNull(source, ERROR_SOURCE_NULL);
        return source instanceof UIThreadAware ? source : new UIThreadAwareObservableMap<>(source);
    }

    private static class UIThreadAwareObservableMap<K, V> extends DelegatingObservableMap<K, V> implements UIThreadAware {
        protected UIThreadAwareObservableMap(ObservableMap<K, V> delegate) {
            super(delegate);
        }

        @Override
        protected void sourceChanged(@Nonnull final MapChangeListener.Change<? extends K, ? extends V> c) {
            if (Platform.isFxApplicationThread()) {
                fireChange(c);
            } else {
                Platform.runLater(() -> fireChange(c));
            }
        }
    }

    @Nonnull
    public static <E> ObservableStream<E> observableStream(@Nonnull ObservableList<E> list) {
        return new ListObservableStream<>(list);
    }

    @Nonnull
    public static <E> ObservableStream<E> observableStream(@Nonnull ObservableSet<E> set) {
        return new SetObservableStream<>(set);
    }

    @Nonnull
    public static <K, V> ObservableStream<V> observableStream(@Nonnull ObservableMap<K, V> map) {
        return new MapObservableStream<>(map);
    }
}
