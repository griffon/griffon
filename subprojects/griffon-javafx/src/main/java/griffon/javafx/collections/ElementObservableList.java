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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ElementObservableList<E extends ElementObservableList.PropertyContainer> extends DelegatingObservableList<E> {
    public interface PropertyContainer {
        Property<?>[] properties();
    }

    private final Map<E, List<ListenerSubscription>> subscriptions = new LinkedHashMap<>();

    public ElementObservableList() {
        this(FXCollections.observableArrayList());
    }

    public ElementObservableList(@Nonnull ObservableList<E> delegate) {
        super(delegate);
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

    private void registerListeners(@Nonnull E contact) {
        if (subscriptions.containsKey(contact)) {
            return;
        }

        List<ListenerSubscription> elementSubscriptions = new ArrayList<>();
        for (Property<?> property : contact.properties()) {
            elementSubscriptions.add(createChangeListener(contact, property));
        }
        subscriptions.put(contact, elementSubscriptions);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private ListenerSubscription createChangeListener(@Nonnull final E contact, @Nonnull final Property<?> property) {
        final ChangeListener listener = (observable, oldValue, newValue) -> fireChange(changeFor(contact));
        property.addListener(listener);
        return () -> property.removeListener(listener);
    }

    @Nonnull
    private ListChangeListener.Change<? extends E> changeFor(@Nonnull final E contact) {
        final int position = indexOf(contact);
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

    private void unregisterListeners(@Nonnull E contact) {
        List<ListenerSubscription> registeredSubscriptions = subscriptions.remove(contact);
        if (registeredSubscriptions != null) {
            registeredSubscriptions.forEach(ListenerSubscription::unsubscribe);
        }
    }


    private interface ListenerSubscription {
        void unsubscribe();
    }
}
