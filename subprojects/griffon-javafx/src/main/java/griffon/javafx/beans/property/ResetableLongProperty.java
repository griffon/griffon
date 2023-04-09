/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResetableLongProperty extends AbstractResetableProperty<Number> {
    private LongProperty baseValue;
    private LongProperty value;

    public ResetableLongProperty() {
        super(null);
    }

    public ResetableLongProperty(@Nullable Long value) {
        super(value);
    }

    public ResetableLongProperty(@Nullable Object bean, @Nonnull String name) {
        super(bean, name);
    }

    public ResetableLongProperty(@Nullable Object bean, @Nonnull String name, @Nullable Long baseValue) {
        super(bean, name, baseValue);
    }

    @Nonnull
    @Override
    protected Property<Number> writableBaseValueProperty() {
        return writableBaseValueLongProperty();
    }

    @Nonnull
    protected LongProperty writableBaseValueLongProperty() {
        if (baseValue == null) {
            baseValue = new SimpleLongProperty(this, "baseValue");
        }
        return baseValue;
    }

    @Nonnull
    @Override
    public ReadOnlyProperty<Number> baseValueProperty() {
        return writableBaseValueProperty();
    }

    @Nonnull
    public ReadOnlyLongProperty baseValueLongProperty() {
        return writableBaseValueLongProperty();
    }

    @Nonnull
    @Override
    public Property<Number> valueProperty() {
        return valueLongProperty();
    }

    @Nonnull
    public LongProperty valueLongProperty() {
        if (value == null) {
            value = new SimpleLongProperty(this, "value");
        }
        return value;
    }
}
