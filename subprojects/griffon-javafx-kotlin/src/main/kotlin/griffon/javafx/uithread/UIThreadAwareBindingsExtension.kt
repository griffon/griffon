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
package griffon.javafx.uithread

import griffon.javafx.beans.binding.UIThreadAwareBindings
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
import java.util.function.Consumer

/**
 * @author Andres Almiray
 * @since 2.13.0
 */

fun Runnable.uiThreadAwareInvalidationListener(): InvalidationListener {
    return UIThreadAwareBindings.uiThreadAwareInvalidationListener(this)
}

fun <T> Property<T>.uiThreadAwareBind(observable: ObservableValue<T>): ChangeListener<T> {
    return UIThreadAwareBindings.uiThreadAwareBind(this, observable)
}

fun <T> ObservableValue<T>.uiThreadAwareChangeListener(listener: ChangeListener<T>): ChangeListener<T> {
    return UIThreadAwareBindings.uiThreadAwareChangeListener(this, listener)
}

fun <T> ChangeListener<T>.uiThreadAwareChangeListener(): ChangeListener<T> {
    return UIThreadAwareBindings.uiThreadAwareChangeListener(this)
}

fun <T> ObservableValue<T>.uiThreadAwareChangeListener(consumer: Consumer<T>): ChangeListener<T> {
    return UIThreadAwareBindings.uiThreadAwareChangeListener(this, consumer)
}

fun <T> Consumer<T>.uiThreadAwareChangeListener(): ChangeListener<T> {
    return UIThreadAwareBindings.uiThreadAwareChangeListener(this)
}

fun <T> ObservableValue<T>.uiThreadAwareChangeListener(runnable: Runnable): ChangeListener<T> {
    return UIThreadAwareBindings.uiThreadAwareChangeListener(this, runnable)
}

fun <T> Runnable.uiThreadAwareChangeListener(): ChangeListener<T> {
    return UIThreadAwareBindings.uiThreadAwareChangeListener(this)
}

fun Observable.uiThreadAwareInvalidationListener(listener: InvalidationListener): InvalidationListener {
    return UIThreadAwareBindings.uiThreadAwareInvalidationListener(this, listener)
}

fun InvalidationListener.uiThreadAwareInvalidationListener(): InvalidationListener {
    return UIThreadAwareBindings.uiThreadAwareInvalidationListener(this)
}

fun Observable.uiThreadAwareInvalidationListener(consumer: Consumer<Observable>): InvalidationListener {
    return UIThreadAwareBindings.uiThreadAwareInvalidationListener(this, consumer)
}

fun Consumer<Observable>.uiThreadAwareInvalidationListener(): InvalidationListener {
    return UIThreadAwareBindings.uiThreadAwareInvalidationListener(this)
}

fun Observable.uiThreadAwareInvalidationListener(runnable: Runnable): InvalidationListener {
    return UIThreadAwareBindings.uiThreadAwareInvalidationListener(this, runnable)
}

fun <E> ObservableList<E>.uiThreadAwareListChangeListener(listener: ListChangeListener<E>): ListChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareListChangeListener(this, listener)
}

fun <E> ListChangeListener<E>.uiThreadAwareListChangeListener(): ListChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareListChangeListener(this)
}

fun <E> ObservableList<E>.uiThreadAwareListChangeListener(consumer: Consumer<ListChangeListener.Change<out E>>): ListChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareListChangeListener(this, consumer)
}

fun <E> Consumer<ListChangeListener.Change<out E>>.uiThreadAwareListChangeListener(): ListChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareListChangeListener(this)
}

fun <E> ObservableList<E>.uiThreadAwareListChangeListener(runnable: Runnable): ListChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareListChangeListener(this, runnable)
}

fun <E> Runnable.uiThreadAwareListChangeListener(): ListChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareListChangeListener(this)
}

fun <K, V> ObservableMap<K, V>.uiThreadAwareMapChangeListener(listener: MapChangeListener<K, V>): MapChangeListener<K, V> {
    return UIThreadAwareBindings.uiThreadAwareMapChangeListener(this, listener)
}

fun <K, V> MapChangeListener<K, V>.uiThreadAwareMapChangeListener(): MapChangeListener<K, V> {
    return UIThreadAwareBindings.uiThreadAwareMapChangeListener(this)
}

fun <K, V> ObservableMap<K, V>.uiThreadAwareMapChangeListener(consumer: Consumer<MapChangeListener.Change<out K, out V>>): MapChangeListener<K, V> {
    return UIThreadAwareBindings.uiThreadAwareMapChangeListener(this, consumer)
}

fun <K, V> Consumer<MapChangeListener.Change<out K, out V>>.uiThreadAwareMapChangeListener(): MapChangeListener<K, V> {
    return UIThreadAwareBindings.uiThreadAwareMapChangeListener(this)
}

fun <K, V> ObservableMap<K, V>.uiThreadAwareMapChangeListener(runnable: Runnable): MapChangeListener<K, V> {
    return UIThreadAwareBindings.uiThreadAwareMapChangeListener(this, runnable)
}

fun <K, V> Runnable.uiThreadAwareMapChangeListener(): MapChangeListener<K, V> {
    return UIThreadAwareBindings.uiThreadAwareMapChangeListener(this)
}

fun <E> ObservableSet<E>.uiThreadAwareSetChangeListener(listener: SetChangeListener<E>): SetChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareSetChangeListener(this, listener)
}

fun <E> SetChangeListener<E>.uiThreadAwareSetChangeListener(): SetChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareSetChangeListener(this)
}

fun <E> ObservableSet<E>.uiThreadAwareSetChangeListener(consumer: Consumer<SetChangeListener.Change<out E>>): SetChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareSetChangeListener(this, consumer)
}

fun <E> Consumer<SetChangeListener.Change<out E>>.uiThreadAwareSetChangeListener(): SetChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareSetChangeListener(this)
}

fun <E> ObservableSet<E>.uiThreadAwareSetChangeListener(runnable: Runnable): SetChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareSetChangeListener(this, runnable)
}

fun <E> Runnable.uiThreadAwareSetChangeListener(): SetChangeListener<E> {
    return UIThreadAwareBindings.uiThreadAwareSetChangeListener(this)
}

fun BooleanProperty.uiThreadAware(): BooleanProperty {
    return UIThreadAwareBindings.uiThreadAwareBooleanProperty(this)
}

fun IntegerProperty.uiThreadAware(): IntegerProperty {
    return UIThreadAwareBindings.uiThreadAwareIntegerProperty(this)
}

fun LongProperty.uiThreadAware(): LongProperty {
    return UIThreadAwareBindings.uiThreadAwareLongProperty(this)
}

fun FloatProperty.uiThreadAware(): FloatProperty {
    return UIThreadAwareBindings.uiThreadAwareFloatProperty(this)
}

fun DoubleProperty.uiThreadAware(): DoubleProperty {
    return UIThreadAwareBindings.uiThreadAwareDoubleProperty(this)
}

fun StringProperty.uiThreadAware(): StringProperty {
    return UIThreadAwareBindings.uiThreadAwareStringProperty(this)
}

fun Property<Boolean>.uiThreadAwarePropertyBoolean(): Property<Boolean> {
    return UIThreadAwareBindings.uiThreadAwarePropertyBoolean(this)
}

fun Property<Int>.uiThreadAwarePropertyInteger(): Property<Int> {
    return UIThreadAwareBindings.uiThreadAwarePropertyInteger(this)
}

fun Property<Long>.uiThreadAwarePropertyLong(): Property<Long> {
    return UIThreadAwareBindings.uiThreadAwarePropertyLong(this)
}

fun Property<Float>.uiThreadAwarePropertyFloat(): Property<Float> {
    return UIThreadAwareBindings.uiThreadAwarePropertyFloat(this)
}

fun Property<Double>.uiThreadAwarePropertyDouble(): Property<Double> {
    return UIThreadAwareBindings.uiThreadAwarePropertyDouble(this)
}

fun Property<String>.uiThreadAwarePropertyString(): Property<String> {
    return UIThreadAwareBindings.uiThreadAwarePropertyString(this)
}

fun <T> ObjectProperty<T>.uiThreadAware(): ObjectProperty<T> {
    return UIThreadAwareBindings.uiThreadAwareObjectProperty(this)
}

fun <E> ListProperty<E>.uiThreadAware(): ListProperty<E> {
    return UIThreadAwareBindings.uiThreadAwareListProperty(this)
}

fun <E> SetProperty<E>.uiThreadAware(): SetProperty<E> {
    return UIThreadAwareBindings.uiThreadAwareSetProperty(this)
}

fun <K, V> MapProperty<K, V>.uiThreadAware(): MapProperty<K, V> {
    return UIThreadAwareBindings.uiThreadAwareMapProperty(this)
}

fun <T> ObservableValue<T>.uiThreadAware(): ObservableValue<T> {
    return UIThreadAwareBindings.uiThreadAwareObservable(this)
}

fun ObservableStringValue.uiThreadAware(): ObservableStringValue {
    return UIThreadAwareBindings.uiThreadAwareObservableString(this)
}

fun ObservableBooleanValue.uiThreadAware(): ObservableBooleanValue {
    return UIThreadAwareBindings.uiThreadAwareObservableBoolean(this)
}

fun ObservableIntegerValue.uiThreadAware(): ObservableIntegerValue {
    return UIThreadAwareBindings.uiThreadAwareObservableInteger(this)
}

fun ObservableLongValue.uiThreadAware(): ObservableLongValue {
    return UIThreadAwareBindings.uiThreadAwareObservableLong(this)
}

fun ObservableFloatValue.uiThreadAware(): ObservableFloatValue {
    return UIThreadAwareBindings.uiThreadAwareObservableFloat(this)
}

fun ObservableDoubleValue.uiThreadAware(): ObservableDoubleValue {
    return UIThreadAwareBindings.uiThreadAwareObservableDouble(this)
}

fun BooleanBinding.uiThreadAware(): BooleanBinding {
    return UIThreadAwareBindings.uiThreadAwareBooleanBinding(this)
}

fun IntegerBinding.uiThreadAware(): IntegerBinding {
    return UIThreadAwareBindings.uiThreadAwareIntegerBinding(this)
}

fun LongBinding.uiThreadAware(): LongBinding {
    return UIThreadAwareBindings.uiThreadAwareLongBinding(this)
}

fun FloatBinding.uiThreadAware(): FloatBinding {
    return UIThreadAwareBindings.uiThreadAwareFloatBinding(this)
}

fun DoubleBinding.uiThreadAware(): DoubleBinding {
    return UIThreadAwareBindings.uiThreadAwareDoubleBinding(this)
}

fun StringBinding.uiThreadAware(): StringBinding {
    return UIThreadAwareBindings.uiThreadAwareStringBinding(this)
}

fun <T> ObjectBinding<T>.uiThreadAware(): ObjectBinding<T> {
    return UIThreadAwareBindings.uiThreadAwareObjectBinding(this)
}
