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
package griffon.javafx.beans.property;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;

import java.util.Objects;

import static griffon.util.StringUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public abstract class AbstractResetableProperty<T> implements ResetableProperty<T> {
    private static final Object DEFAULT_BEAN = null;
    private static final String DEFAULT_NAME = "";

    private final Object bean;
    private final String name;
    protected final BooleanBinding dirty;

    /**
     * The constructor of {@code AbstractResetableProperty}
     */
    public AbstractResetableProperty() {
        this(DEFAULT_BEAN, DEFAULT_NAME, null);
    }

    /**
     * The constructor of {@code AbstractResetableProperty}
     *
     * @param baseValue the base value  of this {@code AbstractResetableProperty}
     */
    public AbstractResetableProperty(@Nullable T baseValue) {
        this(DEFAULT_BEAN, DEFAULT_NAME, baseValue);
    }

    /**
     * The constructor of {@code AbstractResetableProperty}
     *
     * @param bean the bean of this {@code AbstractResetableProperty}
     * @param name the name of this {@code AbstractResetableProperty}
     */
    public AbstractResetableProperty(@Nullable Object bean, @Nonnull String name) {
        this(bean, name, null);
    }

    /**
     * The constructor of {@code AbstractResetableProperty}
     *
     * @param bean      the bean of this {@code AbstractResetableProperty}
     * @param name      the name of this {@code AbstractResetableProperty}
     * @param baseValue the base value  of this {@code AbstractResetableProperty}
     */
    public AbstractResetableProperty(@Nullable Object bean, @Nonnull String name, @Nullable T baseValue) {
        this.bean = bean;
        this.name = isBlank(name) ? DEFAULT_NAME : name;

        writableBaseValueProperty().setValue(baseValue);
        setValue(baseValue);

        dirty = createDirtyBinding();
    }

    @Nonnull
    protected BooleanBinding createDirtyBinding() {
        return Bindings.createBooleanBinding(this::checkValuesAreNotEqual, baseValueProperty(), valueProperty());
    }

    protected boolean checkValuesAreNotEqual() {
        return !Objects.equals(getBaseValue(), getValue());
    }

    @Nonnull
    protected abstract Property<T> writableBaseValueProperty();

    @Nonnull
    @Override
    public BooleanBinding dirtyProperty() {
        return dirty;
    }

    @Override
    @Nullable
    public T getBaseValue() {
        return baseValueProperty().getValue();
    }

    @Override
    @Nullable
    public T getValue() {
        return valueProperty().getValue();
    }

    @Nonnull
    @Override
    public ResetableProperty<T> setValue(@Nullable T value) {
        valueProperty().setValue(value);
        return this;
    }

    @Override
    public boolean isDirty() {
        return dirty.get();
    }

    @Nonnull
    @Override
    public ResetableProperty<T> rebase() {
        writableBaseValueProperty().setValue(getValue());
        return this;
    }

    @Nonnull
    @Override
    public ResetableProperty<T> reset() {
        setValue(getBaseValue());
        return this;
    }

    @Nullable
    @Override
    public Object getBean() {
        return bean;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
}
