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
package griffon.javafx.collections;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ElementObservableList<E> extends DelegatingObservableList<E> {
    public interface ObservableValueContainer {
        @Nonnull
        ObservableValue<?>[] observableValues();
    }

    public interface ObservableValueExtractor<E> {
        @Nonnull
        ObservableValue<?>[] observableValues(@Nullable E instance);
    }

    private final Map<E, List<ListenerSubscription>> subscriptions = new LinkedHashMap<>();
    private final ObservableValueExtractor<E> observableValueExtractor;

    public ElementObservableList() {
        this(FXCollections.observableArrayList(), new DefaultObservableValueExtractor<>());
    }

    public ElementObservableList(@Nonnull ObservableValueExtractor<E> observableValueExtractor) {
        this(FXCollections.observableArrayList(), observableValueExtractor);
    }

    public ElementObservableList(@Nonnull ObservableList<E> delegate) {
        this(delegate, new DefaultObservableValueExtractor<>());
    }

    public ElementObservableList(@Nonnull ObservableList<E> delegate, @Nonnull ObservableValueExtractor<E> observableValueExtractor) {
        super(delegate);
        this.observableValueExtractor = requireNonNull(observableValueExtractor, "Argument 'observableValueExtractor' must not be null");
    }

    @Override
    protected void sourceChanged(@Nonnull ListChangeListener.Change<? extends E> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::registerListeners);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::unregisterListeners);
            }
        }
        fireChange(c);
    }

    private void registerListeners(@Nonnull E element) {
        if (subscriptions.containsKey(element)) {
            return;
        }

        List<ListenerSubscription> elementSubscriptions = new ArrayList<>();
        for (ObservableValue<?> observable : observableValueExtractor.observableValues(element)) {
            elementSubscriptions.add(createChangeListener(element, observable));
        }
        subscriptions.put(element, elementSubscriptions);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private ListenerSubscription createChangeListener(@Nonnull final E element, @Nonnull final ObservableValue<?> observable) {
        final ChangeListener listener = (value, oldValue, newValue) -> fireChange(changeFor(element));
        observable.addListener(listener);
        return () -> observable.removeListener(listener);
    }

    @Nonnull
    private ListChangeListener.Change<? extends E> changeFor(@Nonnull final E element) {
        final int position = indexOf(element);
        final int[] permutations = new int[0];

        return new ListChangeListener.Change<E>(this) {
            private boolean invalid = true;

            @Override
            public boolean next() {
                if (invalid) {
                    invalid = false;
                    return true;
                }
                return false;
            }

            @Override
            public void reset() {
                invalid = true;
            }

            @Override
            public int getFrom() {
                return position;
            }

            @Override
            public int getTo() {
                return position + 1;
            }

            @Override
            public List<E> getRemoved() {
                return Collections.emptyList();
            }

            @Override
            protected int[] getPermutation() {
                return permutations;
            }

            @Override
            public boolean wasUpdated() {
                return true;
            }
        };
    }

    private void unregisterListeners(@Nonnull E element) {
        List<ListenerSubscription> registeredSubscriptions = subscriptions.remove(element);
        if (registeredSubscriptions != null) {
            registeredSubscriptions.forEach(ListenerSubscription::unsubscribe);
        }
    }

    private interface ListenerSubscription {
        void unsubscribe();
    }

    private static class DefaultObservableValueExtractor<T> implements ObservableValueExtractor<T> {
        private final Map<Class<?>, List<Method>> observableValueMetadata = new LinkedHashMap<>();

        @Nonnull
        @Override
        public ObservableValue<?>[] observableValues(@Nullable T instance) {
            if (instance == null) {
                return new ObservableValue[0];
            }

            if (instance instanceof ElementObservableList.ObservableValueContainer) {
                return ((ObservableValueContainer) instance).observableValues();
            }

            List<Method> metadata = observableValueMetadata.computeIfAbsent(instance.getClass(), this::harvestMetadata);

            ObservableValue[] observableValues = new ObservableValue[metadata.size()];
            for (int i = 0; i < observableValues.length; i++) {
                try {
                    observableValues[i] = (ObservableValue) metadata.get(i).invoke(instance);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(e.getTargetException());
                }
            }
            return observableValues;
        }

        private List<Method> harvestMetadata(@Nonnull Class<?> klass) {
            List<Method> metadata = new ArrayList<>();

            for (Method method : klass.getMethods()) {
                if (ObservableValue.class.isAssignableFrom(method.getReturnType()) &&
                    method.getParameterCount() == 0) {
                    metadata.add(method);
                }
            }

            return metadata;
        }
    }
}
