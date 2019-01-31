/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResetableDoubleProperty extends AbstractResetableProperty<Number> {
    private DoubleProperty baseValue;
    private DoubleProperty value;

    public ResetableDoubleProperty() {
        super(null);
    }

    public ResetableDoubleProperty(@Nullable Double value) {
        super(value);
    }

    public ResetableDoubleProperty(@Nullable Object bean, @Nonnull String name) {
        super(bean, name);
    }

    public ResetableDoubleProperty(@Nullable Object bean, @Nonnull String name, @Nullable Double baseValue) {
        super(bean, name, baseValue);
    }

    @Nonnull
    @Override
    protected Property<Number> writableBaseValueProperty() {
        return writableBaseValueDoubleProperty();
    }

    @Nonnull
    protected DoubleProperty writableBaseValueDoubleProperty() {
        if (baseValue == null) {
            baseValue = new SimpleDoubleProperty(this, "baseValue");
        }
        return baseValue;
    }

    @Nonnull
    @Override
    public ReadOnlyProperty<Number> baseValueProperty() {
        return writableBaseValueProperty();
    }

    @Nonnull
    public ReadOnlyDoubleProperty baseValueDoubleProperty() {
        return writableBaseValueDoubleProperty();
    }

    @Nonnull
    @Override
    public Property<Number> valueProperty() {
        return valueDoubleProperty();
    }

    @Nonnull
    public DoubleProperty valueDoubleProperty() {
        if (value == null) {
            value = new SimpleDoubleProperty(this, "value");
        }
        return value;
    }
}
