/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.util;

import griffon.core.editors.ExtendedPropertyEditor;
import griffon.core.editors.PropertyEditorResolver;
import griffon.exceptions.TypeConversionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.PropertyEditor;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * Utility class for dealing with type conversions.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class TypeUtils {
    private TypeUtils() {
        // prevent instantiation
    }

    public static boolean castToBoolean(@Nullable Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return "true".equalsIgnoreCase(String.valueOf(value));
    }

    public static int castToInt(@Nullable Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    public static long castToLong(@Nullable Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    public static float castToFloat(@Nullable Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return Float.valueOf(String.valueOf(value));
    }

    public static double castToDouble(@Nullable Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.valueOf(String.valueOf(value));
    }

    @Nullable
    public static Number castToNumber(@Nullable Object value) {
        if (value instanceof Number) {
            return (Number) value;
        }
        throw new IllegalArgumentException("Don't know how to cast '" + value + "' to " + Number.class.getName());
    }

    public static boolean castToBoolean(@Nullable Object value, boolean defaultValue) {
        return value == null ? defaultValue : castToBoolean(value);
    }

    public static int castToInt(@Nullable Object value, int defaultValue) {
        return value == null ? defaultValue : castToInt(value);
    }

    public static long castToLong(@Nullable Object value, long defaultValue) {
        return value == null ? defaultValue : castToLong(value);
    }

    public static float castToFloat(@Nullable Object value, float defaultValue) {
        return value == null ? defaultValue : castToFloat(value);
    }

    public static double castToDouble(@Nullable Object value, double defaultValue) {
        return value == null ? defaultValue : castToDouble(value);
    }

    @Nullable
    public static Number castToNumber(@Nullable Object value, @Nullable Number defaultValue) {
        return value == null ? defaultValue : castToNumber(value);
    }

    @Nonnull
    public static <T> T convertValue(@Nonnull Class<T> targetType, @Nonnull Object value) {
        return convertValue(targetType, value, null);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T convertValue(@Nonnull Class<T> targetType, @Nonnull Object value, @Nullable String format) {
        requireNonNull(targetType, "Argument 'targetType' cannot be null");
        requireNonNull(value, "Argument 'value' cannot be null");
        if (targetType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        PropertyEditor targetEditor = resolveTargetPropertyEditor(targetType, format);
        if (targetEditor != null) {
            targetEditor.setValue(value);
            return (T) targetEditor.getValue();
        }

        throw new TypeConversionException(value, targetType);
    }

    @Nullable
    private static <T> PropertyEditor resolveTargetPropertyEditor(@Nonnull Class<T> targetType, @Nullable String format) {
        PropertyEditor editor = doResolveTargetPropertyEditor(targetType);
        if (editor instanceof ExtendedPropertyEditor && !isBlank(format)) {
            ((ExtendedPropertyEditor) editor).setFormat(format);
        }
        return editor;
    }

    @Nullable
    private static <T> PropertyEditor doResolveTargetPropertyEditor(@Nonnull Class<T> targetType) {
        return PropertyEditorResolver.findEditor(targetType);
    }
}
