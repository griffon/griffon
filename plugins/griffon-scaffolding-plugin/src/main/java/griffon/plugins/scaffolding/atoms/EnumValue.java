/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.plugins.scaffolding.atoms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 */
public class EnumValue<T extends Enum> extends AbstractAtomicValue implements NumericAtomicValue {
    private final Class<T> enumType;

    public EnumValue(@Nonnull Class<T> enumType) {
        this.enumType = enumType;
    }

    public EnumValue(@Nonnull Class<T> enumType, @Nonnull T enumValue) {
        this.enumType = enumType;
        setValue(enumValue);
    }

    public EnumValue(@Nonnull Class<T> enumType, @Nonnull CharSequence enumValue) {
        this.enumType = enumType;
        setValue(enumValue);
    }

    @Nonnull
    public Class<T> getEnumType() {
        return enumType;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public T enumValue() {
        return (T) value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value == null || enumType.isAssignableFrom(value.getClass())) {
            super.setValue(value);
        } else if (value instanceof CharSequence) {
            super.setValue(T.valueOf(enumType, value.toString()));
        } else {
            throw new IllegalArgumentException("Invalid value " + value);
        }
    }

    @Nonnull
    public Class<?> getValueType() {
        return enumType;
    }
}
