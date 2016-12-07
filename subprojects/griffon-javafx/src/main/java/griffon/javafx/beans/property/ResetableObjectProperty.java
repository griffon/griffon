/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.javafx.beans.property;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResetableObjectProperty<E> extends AbstractResetableProperty<E> {
    private ObjectProperty<E> baseValue;
    private ObjectProperty<E> value;

    public ResetableObjectProperty() {
        super(null);
    }

    public ResetableObjectProperty(@Nullable E value) {
        super(value);
    }

    public ResetableObjectProperty(@Nullable Object bean, @Nonnull String name) {
        super(bean, name);
    }

    public ResetableObjectProperty(@Nullable Object bean, @Nonnull String name, @Nullable E baseValue) {
        super(bean, name, baseValue);
    }

    @Nonnull
    @Override
    protected Property<E> writableBaseValueProperty() {
        return writableBaseValueObjectProperty();
    }

    @Nonnull
    protected ObjectProperty writableBaseValueObjectProperty() {
        if (baseValue == null) {
            baseValue = new SimpleObjectProperty<>(this, "baseValue");
        }
        return baseValue;
    }

    @Nonnull
    @Override
    public ReadOnlyProperty<E> baseValueProperty() {
        return writableBaseValueProperty();
    }

    @Nonnull
    public ReadOnlyObjectProperty baseValueObjectProperty() {
        return writableBaseValueObjectProperty();
    }

    @Nonnull
    @Override
    public Property<E> valueProperty() {
        return valueObjectProperty();
    }

    @Nonnull
    public ObjectProperty<E> valueObjectProperty() {
        if (value == null) {
            value = new SimpleObjectProperty<>(this, "value");
        }
        return value;
    }
}
