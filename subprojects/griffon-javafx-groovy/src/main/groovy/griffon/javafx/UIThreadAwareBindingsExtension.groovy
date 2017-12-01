/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package griffon.javafx

import griffon.javafx.beans.binding.UIThreadAwareBindings
import groovy.transform.CompileStatic
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.DoubleBinding
import javafx.beans.binding.FloatBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.binding.LongBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.FloatProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ListProperty
import javafx.beans.property.LongProperty
import javafx.beans.property.MapProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.SetProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableDoubleValue
import javafx.beans.value.ObservableFloatValue
import javafx.beans.value.ObservableIntegerValue
import javafx.beans.value.ObservableLongValue
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener

import javax.annotation.Nonnull
import java.util.function.Consumer

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
@CompileStatic
final class UIThreadAwareBindingsExtension {
    @Nonnull
    static <T> ChangeListener<T> uiThreadAwareBind(
        @Nonnull Property<T> property, @Nonnull ObservableValue<T> observable) {
        UIThreadAwareBindings.uiThreadAwareBind(property, observable)
    }

    @Nonnull
    static <T> ChangeListener<T> uiThreadAwareChangeListener(
        @Nonnull ObservableValue<T> observable, @Nonnull ChangeListener<T> listener) {
        UIThreadAwareBindings.uiThreadAwareChangeListener(observable, listener)
    }

    @Nonnull
    static <T> ChangeListener<T> uiThreadAwareChangeListener(@Nonnull ChangeListener<T> listener) {
        UIThreadAwareBindings.uiThreadAwareChangeListener(listener)
    }

    @Nonnull
    static <T> ChangeListener<T> uiThreadAwareChangeListener(
        @Nonnull ObservableValue<T> observable, @Nonnull Consumer<T> consumer) {
        UIThreadAwareBindings.uiThreadAwareChangeListener(observable, consumer)
    }

    @Nonnull
    static <T> ChangeListener<T> uiThreadAwareChangeListener(@Nonnull Consumer<T> consumer) {
        UIThreadAwareBindings.uiThreadAwareChangeListener(consumer)
    }

    @Nonnull
    static <T> ChangeListener<T> uiThreadAwareChangeListener(
        @Nonnull ObservableValue<T> observable, @Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareChangeListener(observable, runnable)
    }

    @Nonnull
    static <T> ChangeListener<T> uiThreadAwareChangeListener(@Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareChangeListener(runnable)
    }

    @Nonnull
    static InvalidationListener uiThreadAwareInvalidationListener(
        @Nonnull Observable observable, @Nonnull InvalidationListener listener) {
        UIThreadAwareBindings.uiThreadAwareInvalidationListener(observable, listener)
    }

    @Nonnull
    static InvalidationListener uiThreadAwareInvalidationListener(@Nonnull InvalidationListener listener) {
        UIThreadAwareBindings.uiThreadAwareInvalidationListener(listener)
    }

    @Nonnull
    static InvalidationListener uiThreadAwareInvalidationListener(
        @Nonnull Observable observable, @Nonnull Consumer<Observable> consumer) {
        UIThreadAwareBindings.uiThreadAwareInvalidationListener(observable, consumer)
    }

    @Nonnull
    static InvalidationListener uiThreadAwareInvalidationListener(@Nonnull Consumer<Observable> consumer) {
        UIThreadAwareBindings.uiThreadAwareInvalidationListener(consumer)
    }

    @Nonnull
    static InvalidationListener uiThreadAwareInvalidationListener(
        @Nonnull Observable observable, @Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareInvalidationListener(observable, runnable)
    }

    @Nonnull
    static InvalidationListener uiThreadAwareInvalidationListener(@Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareInvalidationListener(runnable)
    }

    @Nonnull
    static <E> ListChangeListener<E> uiThreadAwareListChangeListener(
        @Nonnull ObservableList<E> observable, @Nonnull ListChangeListener<E> listener) {
        UIThreadAwareBindings.uiThreadAwareListChangeListener(observable, listener)
    }

    @Nonnull
    static <E> ListChangeListener<E> uiThreadAwareListChangeListener(@Nonnull ListChangeListener<E> listener) {
        UIThreadAwareBindings.uiThreadAwareListChangeListener(listener)
    }

    @Nonnull
    static <E> ListChangeListener<E> uiThreadAwareListChangeListener(
        @Nonnull ObservableList<E> observable, @Nonnull Consumer<ListChangeListener.Change<? extends E>> consumer) {
        UIThreadAwareBindings.uiThreadAwareListChangeListener(observable, consumer)
    }

    @Nonnull
    static <E> ListChangeListener<E> uiThreadAwareListChangeListener(
        @Nonnull Consumer<ListChangeListener.Change<? extends E>> consumer) {
        UIThreadAwareBindings.uiThreadAwareListChangeListener(consumer)
    }

    @Nonnull
    static <E> ListChangeListener<E> uiThreadAwareListChangeListener(
        @Nonnull ObservableList<E> observable, @Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareListChangeListener(observable, runnable)
    }

    @Nonnull
    static <E> ListChangeListener<E> uiThreadAwareListChangeListener(@Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareListChangeListener(runnable)
    }

    @Nonnull
    static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(
        @Nonnull ObservableMap<K, V> observable, @Nonnull MapChangeListener<K, V> listener) {
        UIThreadAwareBindings.uiThreadAwareMapChangeListener(observable, listener)
    }

    @Nonnull
    static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(@Nonnull MapChangeListener<K, V> listener) {
        UIThreadAwareBindings.uiThreadAwareMapChangeListener(listener)
    }

    @Nonnull
    static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(
        @Nonnull ObservableMap<K, V> observable,
        @Nonnull Consumer<MapChangeListener.Change<? extends K, ? extends V>> consumer) {
        UIThreadAwareBindings.uiThreadAwareMapChangeListener(observable, consumer)
    }

    @Nonnull
    static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(
        @Nonnull Consumer<MapChangeListener.Change<? extends K, ? extends V>> consumer) {
        UIThreadAwareBindings.uiThreadAwareMapChangeListener(consumer)
    }

    @Nonnull
    static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(
        @Nonnull ObservableMap<K, V> observable, @Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareMapChangeListener(observable, runnable)
    }

    @Nonnull
    static <K, V> MapChangeListener<K, V> uiThreadAwareMapChangeListener(@Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareMapChangeListener(runnable)
    }

    @Nonnull
    static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(
        @Nonnull ObservableSet<E> observable, @Nonnull SetChangeListener<E> listener) {
        UIThreadAwareBindings.uiThreadAwareSetChangeListener(observable, listener)
    }

    @Nonnull
    static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(@Nonnull SetChangeListener<E> listener) {
        UIThreadAwareBindings.uiThreadAwareSetChangeListener(listener)
    }

    @Nonnull
    static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(
        @Nonnull ObservableSet<E> observable, @Nonnull Consumer<SetChangeListener.Change<? extends E>> consumer) {
        UIThreadAwareBindings.uiThreadAwareSetChangeListener(observable, consumer)
    }

    @Nonnull
    static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(
        @Nonnull Consumer<SetChangeListener.Change<? extends E>> consumer) {
        UIThreadAwareBindings.uiThreadAwareSetChangeListener(consumer)
    }

    @Nonnull
    static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(
        @Nonnull ObservableSet<E> observable, @Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareSetChangeListener(observable, runnable)
    }

    @Nonnull
    static <E> SetChangeListener<E> uiThreadAwareSetChangeListener(@Nonnull Runnable runnable) {
        UIThreadAwareBindings.uiThreadAwareSetChangeListener(runnable)
    }

    @Nonnull
    static BooleanProperty uiThreadAware(@Nonnull BooleanProperty observable) {
        UIThreadAwareBindings.uiThreadAwareBooleanProperty(observable)
    }

    @Nonnull
    static IntegerProperty uiThreadAware(@Nonnull IntegerProperty observable) {
        UIThreadAwareBindings.uiThreadAwareIntegerProperty(observable)
    }

    @Nonnull
    static LongProperty uiThreadAware(@Nonnull LongProperty observable) {
        UIThreadAwareBindings.uiThreadAwareLongProperty(observable)
    }

    @Nonnull
    static FloatProperty uiThreadAware(@Nonnull FloatProperty observable) {
        UIThreadAwareBindings.uiThreadAwareFloatProperty(observable)
    }

    @Nonnull
    static DoubleProperty uiThreadAware(@Nonnull DoubleProperty observable) {
        UIThreadAwareBindings.uiThreadAwareDoubleProperty(observable)
    }

    @Nonnull
    static StringProperty uiThreadAware(@Nonnull StringProperty observable) {
        UIThreadAwareBindings.uiThreadAwareStringProperty(observable)
    }

    @Nonnull
    static Property<Boolean> uiThreadAwarePropertyBoolean(@Nonnull Property<Boolean> observable) {
        UIThreadAwareBindings.uiThreadAwarePropertyBoolean(observable)
    }

    @Nonnull
    static Property<Integer> uiThreadAwarePropertyInteger(@Nonnull Property<Integer> observable) {
        UIThreadAwareBindings.uiThreadAwarePropertyInteger(observable)
    }

    @Nonnull
    static Property<Long> uiThreadAwarePropertyLong(@Nonnull Property<Long> observable) {
        UIThreadAwareBindings.uiThreadAwarePropertyLong(observable)
    }

    @Nonnull
    static Property<Float> uiThreadAwarePropertyFloat(@Nonnull Property<Float> observable) {
        UIThreadAwareBindings.uiThreadAwarePropertyFloat(observable)
    }

    @Nonnull
    static Property<Double> uiThreadAwarePropertyDouble(@Nonnull Property<Double> observable) {
        UIThreadAwareBindings.uiThreadAwarePropertyDouble(observable)
    }

    @Nonnull
    static Property<String> uiThreadAwarePropertyString(@Nonnull Property<String> observable) {
        UIThreadAwareBindings.uiThreadAwarePropertyString(observable)
    }

    @Nonnull
    static <T> ObjectProperty<T> uiThreadAware(@Nonnull ObjectProperty<T> observable) {
        UIThreadAwareBindings.uiThreadAwareObjectProperty(observable)
    }

    @Nonnull
    static <E> ListProperty<E> uiThreadAware(@Nonnull ListProperty<E> observable) {
        UIThreadAwareBindings.uiThreadAwareListProperty(observable)
    }

    @Nonnull
    static <E> SetProperty<E> uiThreadAware(@Nonnull SetProperty<E> observable) {
        UIThreadAwareBindings.uiThreadAwareSetProperty(observable)
    }

    @Nonnull
    static <K, V> MapProperty<K, V> uiThreadAware(@Nonnull MapProperty<K, V> observable) {
        UIThreadAwareBindings.uiThreadAwareMapProperty(observable)
    }

    @Nonnull
    static <T> ObservableValue<T> uiThreadAware(@Nonnull ObservableValue<T> observable) {
        UIThreadAwareBindings.uiThreadAwareObservable(observable)
    }

    @Nonnull
    static ObservableStringValue uiThreadAware(@Nonnull ObservableStringValue observable) {
        UIThreadAwareBindings.uiThreadAwareObservableString(observable)
    }

    @Nonnull
    static ObservableBooleanValue uiThreadAware(@Nonnull ObservableBooleanValue observable) {
        UIThreadAwareBindings.uiThreadAwareObservableBoolean(observable)
    }

    @Nonnull
    static ObservableIntegerValue uiThreadAware(@Nonnull ObservableIntegerValue observable) {
        UIThreadAwareBindings.uiThreadAwareObservableInteger(observable)
    }

    @Nonnull
    static ObservableLongValue uiThreadAware(@Nonnull ObservableLongValue observable) {
        UIThreadAwareBindings.uiThreadAwareObservableLong(observable)
    }

    @Nonnull
    static ObservableFloatValue uiThreadAware(@Nonnull ObservableFloatValue observable) {
        UIThreadAwareBindings.uiThreadAwareObservableFloat(observable)
    }

    @Nonnull
    static ObservableDoubleValue uiThreadAware(@Nonnull ObservableDoubleValue observable) {
        UIThreadAwareBindings.uiThreadAwareObservableDouble(observable)
    }

    @Nonnull
    static BooleanBinding uiThreadAware(@Nonnull BooleanBinding observable) {
        UIThreadAwareBindings.uiThreadAwareBooleanBinding(observable)
    }

    @Nonnull
    static IntegerBinding uiThreadAware(@Nonnull IntegerBinding observable) {
        UIThreadAwareBindings.uiThreadAwareIntegerBinding(observable)
    }

    @Nonnull
    static LongBinding uiThreadAware(@Nonnull LongBinding observable) {
        UIThreadAwareBindings.uiThreadAwareLongBinding(observable)
    }

    @Nonnull
    static FloatBinding uiThreadAware(@Nonnull FloatBinding observable) {
        UIThreadAwareBindings.uiThreadAwareFloatBinding(observable)
    }

    @Nonnull
    static DoubleBinding uiThreadAware(@Nonnull DoubleBinding observable) {
        UIThreadAwareBindings.uiThreadAwareDoubleBinding(observable)
    }

    @Nonnull
    static StringBinding uiThreadAware(@Nonnull StringBinding observable) {
        UIThreadAwareBindings.uiThreadAwareStringBinding(observable)
    }

    @Nonnull
    static <T> ObjectBinding<T> uiThreadAware(@Nonnull ObjectBinding<T> observable) {
        UIThreadAwareBindings.uiThreadAwareObjectBinding(observable)
    }
}
