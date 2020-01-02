/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleFloatProperty;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResetableFloatProperty extends AbstractResetableProperty<Number> {
    private FloatProperty baseValue;
    private FloatProperty value;

    public ResetableFloatProperty() {
        super(null);
    }

    public ResetableFloatProperty(@Nullable Float value) {
        super(value);
    }

    public ResetableFloatProperty(@Nullable Object bean, @Nonnull String name) {
        super(bean, name);
    }

    public ResetableFloatProperty(@Nullable Object bean, @Nonnull String name, @Nullable Float baseValue) {
        super(bean, name, baseValue);
    }

    @Nonnull
    @Override
    protected Property<Number> writableBaseValueProperty() {
        return writableBaseValueFloatProperty();
    }

    @Nonnull
    protected FloatProperty writableBaseValueFloatProperty() {
        if (baseValue == null) {
            baseValue = new SimpleFloatProperty(this, "baseValue");
        }
        return baseValue;
    }

    @Nonnull
    @Override
    public ReadOnlyProperty<Number> baseValueProperty() {
        return writableBaseValueProperty();
    }

    @Nonnull
    public ReadOnlyFloatProperty baseValueFloatProperty() {
        return writableBaseValueFloatProperty();
    }

    @Nonnull
    @Override
    public Property<Number> valueProperty() {
        return valueFloatProperty();
    }

    @Nonnull
    public FloatProperty valueFloatProperty() {
        if (value == null) {
            value = new SimpleFloatProperty(this, "value");
        }
        return value;
    }
}
