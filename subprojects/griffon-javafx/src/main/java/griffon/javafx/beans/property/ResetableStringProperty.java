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
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResetableStringProperty extends AbstractResetableProperty<String> {
    private StringProperty baseValue;
    private StringProperty value;

    public ResetableStringProperty() {
        super(null);
    }

    public ResetableStringProperty(@Nullable String value) {
        super(value);
    }

    public ResetableStringProperty(@Nullable Object bean, @Nonnull String name) {
        super(bean, name);
    }

    public ResetableStringProperty(@Nullable Object bean, @Nonnull String name, @Nullable String baseValue) {
        super(bean, name, baseValue);
    }

    @Nonnull
    @Override
    protected Property<String> writableBaseValueProperty() {
        return writableBaseValueStringProperty();
    }

    @Nonnull
    protected StringProperty writableBaseValueStringProperty() {
        if (baseValue == null) {
            baseValue = new SimpleStringProperty(this, "baseValue");
        }
        return baseValue;
    }

    @Nonnull
    @Override
    public ReadOnlyProperty<String> baseValueProperty() {
        return writableBaseValueProperty();
    }

    @Nonnull
    public ReadOnlyStringProperty baseValueStringProperty() {
        return writableBaseValueStringProperty();
    }

    @Nonnull
    @Override
    public Property<String> valueProperty() {
        return valueStringProperty();
    }

    @Nonnull
    public StringProperty valueStringProperty() {
        if (value == null) {
            value = new SimpleStringProperty(this, "value");
        }
        return value;
    }
}
