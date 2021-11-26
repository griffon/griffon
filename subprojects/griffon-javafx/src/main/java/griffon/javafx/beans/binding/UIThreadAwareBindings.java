/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.FloatBinding;
import javafx.beans.binding.FloatExpression;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.ListExpression;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.LongExpression;
import javafx.beans.binding.MapExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.SetExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableFloatValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.9.0
 */
public final class UIThreadAwareBindings {
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' must not be null";
    private static final String ERROR_CONSUMER_NULL = "Argument 'consumer' must not be null";
    private static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";
    private static final String ERROR_OBSERVABLE_NULL = "Argument 'observable' must not be null";
    private static final String ERROR_PROPERTY_NULL = "Argument 'property' must not be null";
    private static final String ERROR_BINDING_NULL = "Argument 'binding' must not be null";

    private UIThreadAwareBindings() {
        // prevent instantiation
    }

    /**
     * Registers a {@code ChangeListener} on the supplied observable that will notify the target property.
     *
     * @param property   the property that will be notified of value changes.
     * @param observable the observable on which the listener will be registered.
     * @return the wrapped change listener.
     */
    public static <T> ChangeListener<T> uiThreadAwareBind(final Property<T> property, final ObservableValue<T> observable) {
        requireNonNull(property, ERROR_PROPERTY_NULL);
        property.setValue(observable.getValue());
        return uiThreadAwareChangeListener(observable, (v, o, n) -> property.setValue(n));
    }

    /**
     * Registers a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped change listener.
     * @return a {@code ChangeListener}.
     */
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(final ObservableValue<T> observable, ChangeListener<T> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        ChangeListener<T> uiListener = uiThreadAwareChangeListener(listener);
        observable.addListener(uiListener);
        return uiListener;
    }

    /**
     * Creates a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped change listener.
     * @return a {@code ChangeListener}.
     */
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(ChangeListener<T> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareChangeListener<>(listener);
    }

    /**
     * Registers a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     * @return a {@code ChangeListener}.
     */
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(final ObservableValue<T> observable, final Consumer<T> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        ChangeListener<T> listener = uiThreadAwareChangeListener(consumer);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code newValue} argument.
     * @return a {@code ChangeListener}.
     */
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(final Consumer<T> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareChangeListener<>((observable, oldValue, newValue) -> consumer.accept(newValue));
    }

    /**
     * Registers a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     * @return a {@code ChangeListener}.
     */
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(final ObservableValue<T> observable, final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        ChangeListener<T> listener = uiThreadAwareChangeListener(runnable);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code ChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     * @return a {@code ChangeListener}.
     */
    public static <T> ChangeListener<T> uiThreadAwareChangeListener(final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareChangeListener<>((observable, oldValue, newValue) -> runnable.run());
    }

    /**
     * Registers a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped invalidation listener.
     * @return an {@code InvalidationListener}.
     */
    public static InvalidationListener uiThreadAwareInvalidationListener(final Observable observable, InvalidationListener listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        InvalidationListener uiListener = uiThreadAwareInvalidationListener(listener);
        observable.addListener(uiListener);
        return uiListener;
    }

    /**
     * Creates a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped invalidation listener.
     * @return a {@code InvalidationListener}.
     */
    public static InvalidationListener uiThreadAwareInvalidationListener(InvalidationListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareInvalidationListener(listener);
    }

    /**
     * Registers a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code observable} argument.
     * @return a {@code InvalidationListener}.
     */
    public static InvalidationListener uiThreadAwareInvalidationListener(final Observable observable, final Consumer<Observable> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        InvalidationListener listener = uiThreadAwareInvalidationListener(consumer);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code observable} argument.
     * @return a {@code InvalidationListener}.
     */
    public static InvalidationListener uiThreadAwareInvalidationListener(final Consumer<Observable> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareInvalidationListener(consumer::accept);
    }

    /**
     * Registers a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     * @return a {@code InvalidationListener}.
     */
    public static InvalidationListener uiThreadAwareInvalidationListener(final Observable observable, final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        InvalidationListener listener = uiThreadAwareInvalidationListener(runnable);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code InvalidationListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     * @return a {@code InvalidationListener}.
     */
    public static InvalidationListener uiThreadAwareInvalidationListener(final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareInvalidationListener(observable -> runnable.run());
    }

    /**
     * Registers a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped list change listener.
     * @return a {@code ListChangeListener}.
     */
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(final ObservableList<E> observable, ListChangeListener<E> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        ListChangeListener<E> uiListener = uiThreadAwareListChangeListener(listener);
        observable.addListener(uiListener);
        return listener;
    }

    /**
     * Creates a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped list change listener.
     * @return a {@code ListChangeListener}.
     */
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(ListChangeListener<E> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareListChangeListener<>(listener);
    }

    /**
     * Registers a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     * @return a {@code ListChangeListener}.
     */
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(final ObservableList<E> observable, final Consumer<ListChangeListener.Change<? extends E>> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        ListChangeListener<E> listener = uiThreadAwareListChangeListener(consumer);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code change} argument.
     * @return a {@code ListChangeListener}.
     */
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(final Consumer<ListChangeListener.Change<? extends E>> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareListChangeListener<E>(consumer::accept);
    }

    /**
     * Registers a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     * @return a {@code ListChangeListener}.
     */
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(final ObservableList<E> observable, final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        ListChangeListener<E> listener = uiThreadAwareListChangeListener(runnable);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code ListChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     * @return a {@code ListChangeListener}.
     */
    public static <E> ListChangeListener<E> uiThreadAwareListChangeListener(final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareListChangeListener<>(change -> runnable.run());
    }

    /**
     * Registers a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped map change listener.
     * @return a {@code MapChangeListener}.
     */
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(final ObservableMap<K, V> observable, MapChangeListener<K, V> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        MapChangeListener<K, V> uiListener = uiThreadAwareMapChangeListener(listener);
        observable.addListener(uiListener);
        return listener;
    }

    /**
     * Creates a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped map change listener.
     * @return a {@code MapChangeListener}.
     */
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(MapChangeListener<K, V> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareMapChangeListener<>(listener);
    }

    /**
     * Registers a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     * @return a {@code MapChangeListener}.
     */
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(final ObservableMap<K, V> observable, final Consumer<MapChangeListener.Change<? extends K, ? extends V>> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        MapChangeListener<K, V> listener = uiThreadAwareMapChangeListener(consumer);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code change} argument.
     * @return a {@code MapChangeListener}.
     */
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(final Consumer<MapChangeListener.Change<? extends K, ? extends V>> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareMapChangeListener<K, V>(consumer::accept);
    }

    /**
     * Registers a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     * @return a {@code MapChangeListener}.
     */
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(final ObservableMap<K, V> observable, final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        MapChangeListener<K, V> listener = uiThreadAwareMapChangeListener(runnable);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code MapChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     * @return a {@code MapChangeListener}.
     */
    public static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareMapChangeListener<>(change -> runnable.run());
    }

    /**
     * Registers a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param listener   the wrapped set change listener.
     * @return a {@code SetChangeListener}.
     */
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(final ObservableSet<E> observable, SetChangeListener<E> listener) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        SetChangeListener<E> uiListener = uiThreadAwareSetChangeListener(listener);
        observable.addListener(uiListener);
        return listener;
    }

    /**
     * Creates a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param listener the wrapped set change listener.
     * @return a {@code SetChangeListener}.
     */
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(SetChangeListener<E> listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        return listener instanceof UIThreadAware ? listener : new UIThreadAwareSetChangeListener<>(listener);
    }

    /**
     * Registers a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param consumer   the consumer of the {@code newValue} argument.
     * @return a {@code SetChangeListener}.
     */
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(final ObservableSet<E> observable, final Consumer<SetChangeListener.Change<? extends E>> consumer) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        SetChangeListener<E> listener = uiThreadAwareSetChangeListener(consumer);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param consumer the consumer of the {@code change} argument.
     * @return a {@code SetChangeListener}.
     */
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(final Consumer<SetChangeListener.Change<? extends E>> consumer) {
        requireNonNull(consumer, ERROR_CONSUMER_NULL);
        return new UIThreadAwareSetChangeListener<E>(consumer::accept);
    }

    /**
     * Registers a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param observable the observable on which the listener will be registered.
     * @param runnable   the code to be executed when the listener is notified.
     * @return a {@code SetChangeListener}.
     */
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(final ObservableSet<E> observable, final Runnable runnable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        SetChangeListener<E> listener = uiThreadAwareSetChangeListener(runnable);
        observable.addListener(listener);
        return listener;
    }

    /**
     * Creates a {@code SetChangeListener} that always handles notifications inside the UI thread.
     *
     * @param runnable the code to be executed when the listener is notified.
     * @return a {@code SetChangeListener}.
     */
    public static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        return new UIThreadAwareSetChangeListener<>(change -> runnable.run());
    }

    /**
     * Creates an observable boolean property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean property to wrap.
     * @return an observable boolean property.
     */
    public static BooleanProperty uiThreadAwareBooleanProperty(BooleanProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareBooleanProperty(observable);
    }

    /**
     * Creates an observable integer property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer property to wrap.
     * @return an observable integer property.
     */
    public static IntegerProperty uiThreadAwareIntegerProperty(IntegerProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareIntegerProperty(observable);
    }

    /**
     * Creates an observable long property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long property to wrap.
     * @return an observable long property.
     */
    public static LongProperty uiThreadAwareLongProperty(LongProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareLongProperty(observable);
    }

    /**
     * Creates an observable float property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float property to wrap.
     * @return an observable float property.
     */
    public static FloatProperty uiThreadAwareFloatProperty(FloatProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareFloatProperty(observable);
    }

    /**
     * Creates an observable double property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double property to wrap.
     * @return an observable double property.
     */
    public static DoubleProperty uiThreadAwareDoubleProperty(DoubleProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareDoubleProperty(observable);
    }

    /**
     * Creates an observable string property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string property to wrap.
     * @return an observable string property.
     */
    public static StringProperty uiThreadAwareStringProperty(StringProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareStringProperty(observable);
    }

    /**
     * Creates an observable boolean property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean property to wrap.
     * @return an observable boolean property.
     */
    public static Property<Boolean> uiThreadAwarePropertyBoolean(Property<Boolean> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyBoolean(observable);
    }

    /**
     * Creates an observable integer property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer property to wrap.
     * @return an observable integer property.
     */
    public static Property<Integer> uiThreadAwarePropertyInteger(Property<Integer> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyInteger(observable);
    }

    /**
     * Creates an observable long property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long property to wrap.
     * @return an observable long property.
     */
    public static Property<Long> uiThreadAwarePropertyLong(Property<Long> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyLong(observable);
    }

    /**
     * Creates an observable float property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float property to wrap.
     * @return an observable float property.
     */
    public static Property<Float> uiThreadAwarePropertyFloat(Property<Float> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyFloat(observable);
    }

    /**
     * Creates an observable double property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double property to wrap.
     * @return an observable double property.
     */
    public static Property<Double> uiThreadAwarePropertyDouble(Property<Double> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyDouble(observable);
    }

    /**
     * Creates an observable string property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string property to wrap.
     * @return an observable string property.
     */
    public static Property<String> uiThreadAwarePropertyString(Property<String> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwarePropertyString(observable);
    }

    /**
     * Creates an observable object property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable object property to wrap.
     * @return an observable object property.
     */
    public static <T> ObjectProperty<T> uiThreadAwareObjectProperty(final ObjectProperty<T> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObjectProperty<>(observable);
    }

    /**
     * Creates an observable list property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable list property to wrap.
     * @return an observable list property.
     */
    public static <E> ListProperty<E> uiThreadAwareListProperty(ListProperty<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareListProperty<>(observable);
    }

    /**
     * Creates an observable set property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable set property to wrap.
     * @return an observable set property.
     */
    public static <E> SetProperty<E> uiThreadAwareSetProperty(SetProperty<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareSetProperty<>(observable);
    }

    /**
     * Creates an observable map property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable map property to wrap.
     * @return an observable map property.
     */
    public static <K, V> MapProperty<K, V> uiThreadAwareMapProperty(MapProperty<K, V> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareMapProperty<>(observable);
    }

    /**
     * Creates an observable value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable to wrap.
     * @return an observable value.
     */
    public static <T> ObservableValue<T> uiThreadAwareObservable(final ObservableValue<T> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableObjectValue<>(observable);
    }

    /**
     * Creates an observable string value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string to wrap.
     * @return an observable string value.
     */
    public static ObservableStringValue uiThreadAwareObservableString(final ObservableStringValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableStringValue(observable);
    }

    /**
     * Creates an observable boolean value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean to wrap.
     * @return an observable boolean value.
     */
    public static ObservableBooleanValue uiThreadAwareObservableBoolean(final ObservableBooleanValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableBooleanValue(observable);
    }

    /**
     * Creates an observable integer value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer to wrap.
     * @return an observable integer value.
     */
    public static ObservableIntegerValue uiThreadAwareObservableInteger(final ObservableIntegerValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableIntegerValue(observable);
    }

    /**
     * Creates an observable long value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long to wrap.
     * @return an observable long value.
     */
    public static ObservableLongValue uiThreadAwareObservableLong(final ObservableLongValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableLongValue(observable);
    }

    /**
     * Creates an observable float value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float to wrap.
     * @return an observable float value.
     */
    public static ObservableFloatValue uiThreadAwareObservableFloat(final ObservableFloatValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableFloatValue(observable);
    }

    /**
     * Creates an observable double value that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double to wrap.
     * @return an observable double value.
     */
    public static ObservableDoubleValue uiThreadAwareObservableDouble(final ObservableDoubleValue observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObservableDoubleValue(observable);
    }

    /**
     * Creates a boolean binding that notifies its listeners inside the UI thread.
     *
     * @param binding the boolean binding to wrap.
     * @return a boolean binding.
     * @since 2.13.0
     */
    public static BooleanBinding uiThreadAwareBooleanBinding(BooleanBinding binding) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        return binding instanceof UIThreadAware ? binding : new UIThreadAwareBooleanBinding(binding);
    }

    /**
     * Creates an integer binding that notifies its listeners inside the UI thread.
     *
     * @param binding the integer binding to wrap.
     * @return an integer binding.
     * @since 2.13.0
     */
    public static IntegerBinding uiThreadAwareIntegerBinding(IntegerBinding binding) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        return binding instanceof UIThreadAware ? binding : new UIThreadAwareIntegerBinding(binding);
    }

    /**
     * Creates a long binding that notifies its listeners inside the UI thread.
     *
     * @param binding the long binding to wrap.
     * @return a long binding.
     * @since 2.13.0
     */
    public static LongBinding uiThreadAwareLongBinding(LongBinding binding) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        return binding instanceof UIThreadAware ? binding : new UIThreadAwareLongBinding(binding);
    }

    /**
     * Creates a float binding that notifies its listeners inside the UI thread.
     *
     * @param binding the float binding to wrap.
     * @return a float binding.
     * @since 2.13.0
     */
    public static FloatBinding uiThreadAwareFloatBinding(FloatBinding binding) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        return binding instanceof UIThreadAware ? binding : new UIThreadAwareFloatBinding(binding);
    }

    /**
     * Creates a double binding that notifies its listeners inside the UI thread.
     *
     * @param binding the double binding to wrap.
     * @return a double binding.
     * @since 2.13.0
     */
    public static DoubleBinding uiThreadAwareDoubleBinding(DoubleBinding binding) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        return binding instanceof UIThreadAware ? binding : new UIThreadAwareDoubleBinding(binding);
    }

    /**
     * Creates a string binding that notifies its listeners inside the UI thread.
     *
     * @param binding the string binding to wrap.
     * @return a string binding.
     * @since 2.13.0
     */
    public static StringBinding uiThreadAwareStringBinding(StringBinding binding) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        return binding instanceof UIThreadAware ? binding : new UIThreadAwareStringBinding(binding);
    }

    /**
     * Creates an object binding that notifies its listeners inside the UI thread.
     *
     * @param binding the object binding to wrap.
     * @return an object binding.
     * @since 2.13.0
     */
    public static <T> ObjectBinding<T> uiThreadAwareObjectBinding(ObjectBinding<T> binding) {
        requireNonNull(binding, ERROR_BINDING_NULL);
        return binding instanceof UIThreadAware ? binding : new UIThreadAwareObjectBinding<>(binding);
    }

    /**
     * Creates an observable boolean property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean property to wrap.
     * @return an observable read-only boolean property.
     * @since 2.16.0
     */
    public static ReadOnlyBooleanProperty uiThreadAwareReadOnlyBooleanProperty(ReadOnlyBooleanProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyBooleanProperty(observable);
    }

    /**
     * Creates an observable integer property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer property to wrap.
     * @return an observable read-only integer property.
     * @since 2.16.0
     */
    public static ReadOnlyIntegerProperty uiThreadAwareReadOnlyIntegerProperty(ReadOnlyIntegerProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyIntegerProperty(observable);
    }

    /**
     * Creates an observable long property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long property to wrap.
     * @return an observable read-only long property.
     * @since 2.16.0
     */
    public static ReadOnlyLongProperty uiThreadAwareReadOnlyLongProperty(ReadOnlyLongProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyLongProperty(observable);
    }

    /**
     * Creates an observable float property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float property to wrap.
     * @return an observable read-only float property.
     * @since 2.16.0
     */
    public static ReadOnlyFloatProperty uiThreadAwareReadOnlyFloatProperty(ReadOnlyFloatProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyFloatProperty(observable);
    }

    /**
     * Creates an observable double property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double property to wrap.
     * @return an observable read-only double property.
     * @since 2.16.0
     */
    public static ReadOnlyDoubleProperty uiThreadAwareReadOnlyDoubleProperty(ReadOnlyDoubleProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyDoubleProperty(observable);
    }

    /**
     * Creates an observable string property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string property to wrap.
     * @return an observable read-only string property.
     * @since 2.16.0
     */
    public static ReadOnlyStringProperty uiThreadAwareReadOnlyStringProperty(ReadOnlyStringProperty observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyStringProperty(observable);
    }

    /**
     * Creates an observable object property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable object property to wrap.
     * @return an observable read-only object property.
     * @since 2.16.0
     */
    public static <T> ReadOnlyObjectProperty<T> uiThreadAwareReadOnlyObjectProperty(final ReadOnlyObjectProperty<T> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyObjectProperty<>(observable);
    }

    /**
     * Creates an observable list property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable list property to wrap.
     * @return an observable read-only list property.
     * @since 2.16.0
     */
    public static <E> ReadOnlyListProperty<E> uiThreadAwareReadOnlyListProperty(ReadOnlyListProperty<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyListProperty<>(observable);
    }

    /**
     * Creates an observable set property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable set property to wrap.
     * @return an observable read-only set property.
     * @since 2.16.0
     */
    public static <E> ReadOnlySetProperty<E> uiThreadAwareReadOnlySetProperty(ReadOnlySetProperty<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlySetProperty<>(observable);
    }

    /**
     * Creates an observable map property that notifies its listeners inside the UI thread.
     *
     * @param observable the observable map property to wrap.
     * @return an observable read-only map property.
     * @since 2.16.0
     */
    public static <K, V> ReadOnlyMapProperty<K, V> uiThreadAwareReadOnlyMapProperty(ReadOnlyMapProperty<K, V> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareReadOnlyMapProperty<>(observable);
    }

    /**
     * Creates an observable boolean expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable boolean expression to wrap.
     * @return an observable  boolean expression.
     * @since 2.16.0
     */
    public static BooleanExpression uiThreadAwareBooleanExpression(BooleanExpression observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareBooleanExpression(observable);
    }

    /**
     * Creates an observable integer expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable integer expression to wrap.
     * @return an observable  integer expression.
     * @since 2.16.0
     */
    public static IntegerExpression uiThreadAwareIntegerExpression(IntegerExpression observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareIntegerExpression(observable);
    }

    /**
     * Creates an observable long expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable long expression to wrap.
     * @return an observable  long expression.
     * @since 2.16.0
     */
    public static LongExpression uiThreadAwareLongExpression(LongExpression observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareLongExpression(observable);
    }

    /**
     * Creates an observable float expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable float expression to wrap.
     * @return an observable  float expression.
     * @since 2.16.0
     */
    public static FloatExpression uiThreadAwareFloatExpression(FloatExpression observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareFloatExpression(observable);
    }

    /**
     * Creates an observable double expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable double expression to wrap.
     * @return an observable  double expression.
     * @since 2.16.0
     */
    public static DoubleExpression uiThreadAwareDoubleExpression(DoubleExpression observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareDoubleExpression(observable);
    }

    /**
     * Creates an observable string expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable string expression to wrap.
     * @return an observable  string expression.
     * @since 2.16.0
     */
    public static StringExpression uiThreadAwareStringExpression(StringExpression observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareStringExpression(observable);
    }

    /**
     * Creates an observable object expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable object expression to wrap.
     * @return an observable  object expression.
     * @since 2.16.0
     */
    public static <T> ObjectExpression<T> uiThreadAwareObjectExpression(final ObjectExpression<T> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareObjectExpression<>(observable);
    }

    /**
     * Creates an observable list expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable list expression to wrap.
     * @return an observable  list expression.
     * @since 2.16.0
     */
    public static <E> ListExpression<E> uiThreadAwareListExpression(ListExpression<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareListExpression<>(observable);
    }

    /**
     * Creates an observable set expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable set expression to wrap.
     * @return an observable  set expression.
     * @since 2.16.0
     */
    public static <E> SetExpression<E> uiThreadAwareSetExpression(SetExpression<E> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareSetExpression<>(observable);
    }

    /**
     * Creates an observable map expression that notifies its listeners inside the UI thread.
     *
     * @param observable the observable map expression to wrap.
     * @return an observable  map expression.
     * @since 2.16.0
     */
    public static <K, V> MapExpression<K, V> uiThreadAwareMapExpression(MapExpression<K, V> observable) {
        requireNonNull(observable, ERROR_OBSERVABLE_NULL);
        return observable instanceof UIThreadAware ? observable : new UIThreadAwareMapExpression<>(observable);
    }
}