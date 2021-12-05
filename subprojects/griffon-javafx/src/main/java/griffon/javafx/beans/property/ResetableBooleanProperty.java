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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.10.0
 */
public class ResetableBooleanProperty extends AbstractResetableProperty<Boolean> {
    private BooleanProperty baseValue;
    private BooleanProperty value;

    public ResetableBooleanProperty() {
        super(null);
    }

    public ResetableBooleanProperty(@Nullable Boolean value) {
        super(value);
    }

    public ResetableBooleanProperty(@Nullable Object bean, @Nonnull String name) {
        super(bean, name);
    }

    public ResetableBooleanProperty(@Nullable Object bean, @Nonnull String name, @Nullable Boolean baseValue) {
        super(bean, name, baseValue);
    }

    @Override
    protected Property<Boolean> writableBaseValueProperty() {
        return writableBaseValueBooleanProperty();
    }

    protected BooleanProperty writableBaseValueBooleanProperty() {
        if (baseValue == null) {
            baseValue = new SimpleBooleanProperty(this, "baseValue");
        }
        return baseValue;
    }

    @Override
    public ReadOnlyProperty<Boolean> baseValueProperty() {
        return writableBaseValueProperty();
    }

    public ReadOnlyBooleanProperty baseValueBooleanProperty() {
        return writableBaseValueBooleanProperty();
    }

    @Override
    public Property<Boolean> valueProperty() {
        return valueBooleanProperty();
    }

    public BooleanProperty valueBooleanProperty() {
        if (value == null) {
            value = new SimpleBooleanProperty(this, "value");
        }
        return value;
    }
}
