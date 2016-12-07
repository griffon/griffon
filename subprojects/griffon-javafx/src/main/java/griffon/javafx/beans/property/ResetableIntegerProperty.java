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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResetableIntegerProperty extends AbstractResetableProperty<Number> {
    private IntegerProperty baseValue;
    private IntegerProperty value;

    public ResetableIntegerProperty() {
        super(null);
    }

    public ResetableIntegerProperty(@Nullable Integer value) {
        super(value);
    }

    public ResetableIntegerProperty(@Nullable Object bean, @Nonnull String name) {
        super(bean, name);
    }

    public ResetableIntegerProperty(@Nullable Object bean, @Nonnull String name, @Nullable Integer baseValue) {
        super(bean, name, baseValue);
    }

    @Nonnull
    @Override
    protected Property<Number> writableBaseValueProperty() {
        return writableBaseValueIntegerProperty();
    }

    @Nonnull
    protected IntegerProperty writableBaseValueIntegerProperty() {
        if (baseValue == null) {
            baseValue = new SimpleIntegerProperty(this, "baseValue");
        }
        return baseValue;
    }

    @Nonnull
    @Override
    public ReadOnlyProperty<Number> baseValueProperty() {
        return writableBaseValueProperty();
    }

    @Nonnull
    public ReadOnlyIntegerProperty baseValueIntegerProperty() {
        return writableBaseValueIntegerProperty();
    }

    @Nonnull
    @Override
    public Property<Number> valueProperty() {
        return valueIntegerProperty();
    }

    @Nonnull
    public IntegerProperty valueIntegerProperty() {
        if (value == null) {
            value = new SimpleIntegerProperty(this, "value");
        }
        return value;
    }
}
