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

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public interface PropertyContainer {
        @Nonnull
        Property<?>[] properties();
    }

    public interface PropertyExtractor<E> {
        @Nonnull
        Property<?>[] properties(@Nullable E instance);
    }

    private final Map<E, List<ListenerSubscription>> subscriptions = new LinkedHashMap<>();
    private final PropertyExtractor<E> propertyExtractor;

    public ElementObservableList() {
        this(FXCollections.observableArrayList(), new DefaultPropertyExtractor<>());
    }

    public ElementObservableList(@Nonnull PropertyExtractor<E> propertyExtractor) {
        this(FXCollections.observableArrayList(), propertyExtractor);
    }

    public ElementObservableList(@Nonnull ObservableList<E> delegate) {
        this(delegate, new DefaultPropertyExtractor<>());
    }

    public ElementObservableList(@Nonnull ObservableList<E> delegate, @Nonnull PropertyExtractor<E> propertyExtractor) {
        super(delegate);
        this.propertyExtractor = requireNonNull(propertyExtractor, "Argument 'propertyExtractor' must not be null");
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
        for (Property<?> property : propertyExtractor.properties(element)) {
            elementSubscriptions.add(createChangeListener(element, property));
        }
        subscriptions.put(element, elementSubscriptions);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private ListenerSubscription createChangeListener(@Nonnull final E element, @Nonnull final Property<?> property) {
        final ChangeListener listener = (observable, oldValue, newValue) -> fireChange(changeFor(element));
        property.addListener(listener);
        return () -> property.removeListener(listener);
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

    private static class DefaultPropertyExtractor<T> implements PropertyExtractor<T> {
        private static final Logger LOG = LoggerFactory.getLogger(DefaultPropertyExtractor.class);

        private final Map<Class<?>, List<Method>> propertyMetadata = new LinkedHashMap<>();

        @Nonnull
        @Override
        public Property<?>[] properties(@Nullable T instance) {
            if (instance == null) {
                return new Property[0];
            }

            if (instance instanceof PropertyContainer) {
                return ((PropertyContainer) instance).properties();
            }

            Class<?> klass = instance.getClass();
            List<Method> metadata = propertyMetadata.get(klass);
            if (metadata == null) {
                metadata = harvestMetadata(klass);
                propertyMetadata.put(klass, metadata);
            }

            Property[] properties = new Property[metadata.size()];
            for (int i = 0; i < properties.length; i++) {
                try {
                    properties[i] = (Property) metadata.get(i).invoke(instance);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(e.getTargetException());
                }
            }
            return properties;
        }

        private List<Method> harvestMetadata(@Nonnull Class<?> klass) {
            List<Method> metadata = new ArrayList<>();

            for (Method method : klass.getMethods()) {
                if (Property.class.isAssignableFrom(method.getReturnType()) &&
                    method.getName().endsWith("Property") &&
                    method.getParameterCount() == 0) {
                    metadata.add(method);
                }
            }

            return metadata;
        }
    }
}
