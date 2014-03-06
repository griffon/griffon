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

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class FloatValue extends AbstractPrimitiveAtomicValue implements NumericAtomicValue {
    public FloatValue() {
    }

    public FloatValue(@Nonnull Float arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public FloatValue(@Nonnull Number arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    @Nullable
    public Float floatValue() {
        return (Float) value;
    }

    @Override
    public String toString() {
        return null == value ? (isPrimitive() ? "0.0" : null) : String.valueOf(value);
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value == null) {
            super.setValue(isPrimitive() ? 0f : null);
        } else if (value instanceof Float) {
            super.setValue(value);
        } else if (value instanceof Number) {
            super.setValue(((Number) value).floatValue());
        } else {
            throw new IllegalArgumentException("Invalid value " + value);
        }
    }

    @Nonnull
    public Class<?> getValueType() {
        return isPrimitive() ? Float.TYPE : Float.class;
    }
}
