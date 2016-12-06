/*
 * Copyright 2008-2016 the original author or authors.
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

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public interface ResetableProperty<T> {
    /**
     * A property that tracks the base value of this {@code ResetableProperty}.
     */
    @Nonnull
    ReadOnlyProperty<T> baseValueProperty();

    /**
     * A property that tracks the current value of this {@code ResetableProperty}.
     */
    @Nonnull
    Property<T> valueProperty();

    /**
     * A property that tracks if the current value differs form the base value.
     */
    @Nonnull
    BooleanBinding dirtyProperty();

    /**
     * Returns the base value of this {@code ResetableProperty}.
     *
     * @return the base value
     */
    @Nullable
    T getBaseValue();

    /**
     * Returns the current value of this {@code ResetableProperty}.
     *
     * @return the current value
     */
    @Nullable
    T getValue();

    /**
     * Sets the current value.
     *
     * @param value the new value
     */
    @Nonnull
    ResetableProperty<T> setValue(@Nullable T value);

    /**
     * Query if the current value differs from the base value.
     *
     * @return {@code true} if values differ, {@code false} otherwise
     */
    boolean isDirty();

    /**
     * Sets the current value as the base value.
     *
     * @return this {@code ResetableProperty}
     */
    @Nonnull
    ResetableProperty<T> rebase();

    /**
     * Sets the base value as the current value.
     *
     * @return this {@code ResetableProperty}
     */
    @Nonnull
    ResetableProperty<T> reset();

    /**
     * Returns the {@code Object} that contains this property. If this property
     * is not contained in an {@code Object}, {@code null} is returned.
     *
     * @return the containing {@code Object} or {@code null}
     */
    @Nullable
    Object getBean();

    /**
     * Returns the name of this property. If the property does not have a name,
     * this method returns an empty {@code String}.
     *
     * @return the name or an empty {@code String}
     */
    @Nonnull
    String getName();
}
