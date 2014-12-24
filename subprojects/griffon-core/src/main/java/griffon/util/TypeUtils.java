/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.util;

import griffon.core.editors.ExtendedPropertyEditor;
import griffon.core.editors.PropertyEditorResolver;
import griffon.exceptions.GriffonException;
import griffon.exceptions.TypeConversionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * Utility class for dealing with type conversions.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class TypeUtils {

    private static final String ERROR_VALUE_NULL = "Argument 'value' must not be null";

    private TypeUtils() {
        // prevent instantiation
    }

    public static boolean castToBoolean(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return "true".equalsIgnoreCase(String.valueOf(value));
    }

    public static char castToChar(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Character) {
            return (Character) value;
        }
        return String.valueOf(value).charAt(0);
    }

    public static byte castToByte(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        return Byte.valueOf(String.valueOf(value));
    }

    public static short castToShort(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        return Short.valueOf(String.valueOf(value));
    }

    public static int castToInt(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    public static long castToLong(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    public static float castToFloat(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return Float.valueOf(String.valueOf(value));
    }

    public static double castToDouble(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.valueOf(String.valueOf(value));
    }

    public static BigInteger castToBigInteger(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        return new BigInteger(String.valueOf(value));
    }

    public static BigDecimal castToBigDecimal(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(String.valueOf(value));
    }

    @Nullable
    public static Number castToNumber(@Nonnull Object value) {
        requireNonNull(value, ERROR_VALUE_NULL);
        if (value instanceof Number) {
            return (Number) value;
        }
        throw new IllegalArgumentException("Don't know how to cast '" + value + "' to " + Number.class.getName());
    }

    public static boolean castToBoolean(@Nullable Object value, boolean defaultValue) {
        return value == null ? defaultValue : castToBoolean(value);
    }

    public static char castToChar(@Nullable Object value, char defaultValue) {
        return value == null ? defaultValue : castToChar(value);
    }

    public static byte castToByte(@Nullable Object value, byte defaultValue) {
        return value == null ? defaultValue : castToByte(value);
    }

    public static short castToShort(@Nullable Object value, short defaultValue) {
        return value == null ? defaultValue : castToShort(value);
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

    @Nullable
    public static BigInteger castToBigInteger(@Nullable Object value, @Nullable BigInteger defaultValue) {
        return value == null ? defaultValue : castToBigInteger(value);
    }

    @Nullable
    public static BigDecimal castToBigDecimal(@Nullable Object value, @Nullable BigDecimal defaultValue) {
        return value == null ? defaultValue : castToBigDecimal(value);
    }

    @Nonnull
    public static <T> T convertValue(@Nonnull Class<T> targetType, @Nonnull Object value) {
        return convertValue(targetType, value, null);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T convertValue(@Nonnull Class<T> targetType, @Nonnull Object value, @Nullable String format) {
        requireNonNull(targetType, "Argument 'targetType' must not be null");
        requireNonNull(value, ERROR_VALUE_NULL);
        if (targetType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (null == format) {
            if (isBoolean(targetType) && isBoolean(value.getClass())) {
                return (T) value;
            } else if (isCharacter(targetType) && isCharacter(value.getClass())) {
                return (T) value;
            } else if (isNumber(targetType) && isNumber(value.getClass())) {
                if (isByte(targetType)) {
                    return (T) ((Byte) castToByte(value));
                } else if (isShort(targetType)) {
                    return (T) ((Short) castToShort(value));
                } else if (isInteger(targetType)) {
                    return (T) ((Integer) castToInt(value));
                } else if (isLong(targetType)) {
                    return (T) ((Long) castToLong(value));
                } else if (isFloat(targetType)) {
                    return (T) ((Float) castToFloat(value));
                } else if (isDouble(targetType)) {
                    return (T) ((Double) castToDouble(value));
                } else if (isBigInteger(targetType)) {
                    return targetType.cast(BigInteger.valueOf(((Number) value).longValue()));
                } else if (isBigDecimal(targetType)) {
                    return targetType.cast(BigDecimal.valueOf(((Number) value).doubleValue()));
                }
            }
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

    public static boolean isBoolean(@Nonnull Class<?> type) {
        return Boolean.class == type || Boolean.TYPE == type;
    }

    public static boolean isCharacter(@Nonnull Class<?> type) {
        return Character.class == type || Character.TYPE == type;
    }

    public static boolean isByte(@Nonnull Class<?> type) {
        return Byte.class == type || Byte.TYPE == type;
    }

    public static boolean isShort(@Nonnull Class<?> type) {
        return Short.class == type || Short.TYPE == type;
    }

    public static boolean isInteger(@Nonnull Class<?> type) {
        return Integer.class == type || Integer.TYPE == type;
    }

    public static boolean isLong(@Nonnull Class<?> type) {
        return Long.class == type || Long.TYPE == type;
    }

    public static boolean isFloat(@Nonnull Class<?> type) {
        return Float.class == type || Float.TYPE == type;
    }

    public static boolean isDouble(@Nonnull Class<?> type) {
        return Double.class == type || Double.TYPE == type;
    }

    public static boolean isBigInteger(@Nonnull Class<?> type) {
        return BigInteger.class == type;
    }

    public static boolean isBigDecimal(@Nonnull Class<?> type) {
        return BigDecimal.class == type;
    }

    public static boolean isNumber(@Nonnull Class<?> type) {
        return Number.class == type ||
            isByte(type) ||
            isShort(type) ||
            isInteger(type) ||
            isLong(type) ||
            isFloat(type) ||
            isDouble(type) ||
            isBigInteger(type) ||
            isBigDecimal(type);
    }

    // == The following methods taken from
    // org.codehaus.groovy.runtime.DefaultGroovyMethods
    // org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation

    public static boolean equals(@Nullable Object left, @Nullable Object right) {
        if (left == right) return true;
        if (left == null || right == null) return false;

        // handle arrays on both sides as special case for efficiency
        Class<?> leftClass = left.getClass();
        Class<?> rightClass = right.getClass();
        if (leftClass.isArray() && rightClass.isArray()) {
            return arrayEqual(left, right);
        }
        if (leftClass.isArray() && leftClass.getComponentType().isPrimitive()) {
            left = primitiveArrayToList(left);
        }
        if (rightClass.isArray() && rightClass.getComponentType().isPrimitive()) {
            right = primitiveArrayToList(right);
        }
        if (left instanceof Object[] && right instanceof List) {
            return equals((Object[]) left, (List) right);
        }
        if (left instanceof List && right instanceof Object[]) {
            return equals((List) left, (Object[]) right);
        }
        if (left instanceof List && right instanceof List) {
            return equals((List) left, (List) right);
        }
        if (left instanceof Map.Entry && right instanceof Map.Entry) {
            Object k1 = ((Map.Entry) left).getKey();
            Object k2 = ((Map.Entry) right).getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = ((Map.Entry) left).getValue();
                Object v2 = ((Map.Entry) right).getValue();
                if (v1 == v2 || (v1 != null && equals(v1, v2)))
                    return true;
            }
            return false;
        }

        return left.equals(right);
    }

    public static boolean arrayEqual(@Nullable Object left, @Nullable Object right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return false;
        }
        if (Array.getLength(left) != Array.getLength(right)) {
            return false;
        }
        for (int i = 0; i < Array.getLength(left); i++) {
            Object l = Array.get(left, i);
            Object r = Array.get(right, i);
            if (!equals(l, r)) return false;
        }
        return true;
    }

    /**
     * Compare the contents of this array to the contents of the given array.
     *
     * @param left  an int array
     * @param right the array being compared
     * @return true if the contents of both arrays are equal.
     */
    public static boolean equals(int[] left, int[] right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return false;
        }
        if (left == right) {
            return true;
        }
        if (left.length != right.length) {
            return false;
        }
        for (int i = 0; i < left.length; i++) {
            if (left[i] != right[i]) return false;
        }
        return true;
    }

    /**
     * Determines if the contents of this array are equal to the
     * contents of the given list, in the same order.  This returns
     * <code>false</code> if either collection is <code>null</code>.
     *
     * @param left  an array
     * @param right the List being compared
     * @return true if the contents of both collections are equal
     */
    public static boolean equals(Object[] left, List right) {
        return doEquals(left, right);
    }

    /**
     * Determines if the contents of this list are equal to the
     * contents of the given array in the same order.  This returns
     * <code>false</code> if either collection is <code>null</code>.
     *
     * @param left  a List
     * @param right the Object[] being compared to
     * @return true if the contents of both collections are equal
     */
    public static boolean equals(List left, Object[] right) {
        return doEquals(right, left);
    }

    private static boolean doEquals(Object[] left, List<?> right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return false;
        }
        if (left.length != right.size()) {
            return false;
        }
        for (int i = left.length - 1; i >= 0; i--) {
            final Object o1 = left[i];
            final Object o2 = right.get(i);
            if (o1 == null) {
                if (o2 != null) return false;
            } else if (!equals(o1, o2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare the contents of two Lists.  Order matters.
     * If numbers exist in the Lists, then they are compared as numbers,
     * for example 2 == 2L. If both lists are <code>null</code>, the result
     * is true; rightwise if either list is <code>null</code>, the result
     * is <code>false</code>.
     *
     * @param left  a List
     * @param right the List being compared to
     * @return boolean   <code>true</code> if the contents of both lists are identical,
     * <code>false</code> rightwise.
     */
    public static <T> boolean equals(List<T> left, List<T> right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return false;
        }
        if (left == right) {
            return true;
        }
        if (left.size() != right.size()) {
            return false;
        }
        final Iterator<T> it1 = left.iterator(), it2 = right.iterator();
        while (it1.hasNext()) {
            final T o1 = it1.next();
            final T o2 = it2.next();
            if (o1 == null) {
                if (o2 != null) return false;
            } else if (!equals(o1, o2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compare the contents of two Sets for equality using Groovy's coercion rules.
     * <p/>
     * Returns <tt>true</tt> if the two sets have the same size, and every member
     * of the specified set is contained in this set (or equivalently, every member
     * of this set is contained in the specified set).
     * If numbers exist in the sets, then they are compared as numbers,
     * for example 2 == 2L.  If both sets are <code>null</code>, the result
     * is true; rightwise if either set is <code>null</code>, the result
     * is <code>false</code>.
     *
     * @param left  a Set
     * @param right the Set being compared to
     * @return <tt>true</tt> if the contents of both sets are identical
     * @since 1.8.0
     */
    public static <T> boolean equals(Set<T> left, Set<T> right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return false;
        }
        if (left == right) {
            return true;
        }
        if (left.size() != right.size()) {
            return false;
        }
        final Iterator<T> it1 = left.iterator();
        Collection<T> rightItems = new HashSet<T>(right);
        while (it1.hasNext()) {
            final Object o1 = it1.next();
            final Iterator<T> it2 = rightItems.iterator();
            T foundItem = null;
            boolean found = false;
            while (it2.hasNext() && foundItem == null) {
                final T o2 = it2.next();
                if (equals(o1, o2)) {
                    foundItem = o2;
                    found = true;
                }
            }
            if (!found) return false;
            rightItems.remove(foundItem);
        }
        return rightItems.size() == 0;
    }


    /**
     * Compares two Maps treating coerced numerical values as identical.
     * <p/>
     *
     * @param left  this Map
     * @param right the Map being compared to
     * @return <tt>true</tt> if the contents of both maps are identical
     */
    public static <K, V> boolean equals(Map<K, V> left, Map<K, V> right) {
        if (left == null) {
            return right == null;
        }
        if (right == null) {
            return false;
        }
        if (left == right) {
            return true;
        }
        if (left.size() != right.size()) {
            return false;
        }
        if (!left.keySet().equals(right.keySet())) {
            return false;
        }
        for (K key : left.keySet()) {
            if (!equals(left.get(key), right.get(key))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Allows conversion of arrays into a mutable List
     *
     * @param array an array
     * @return the array as a List
     */
    public static List primitiveArrayToList(Object array) {
        int size = Array.getLength(array);
        List list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            Object item = Array.get(array, i);
            if (item != null && item.getClass().isArray() && item.getClass().getComponentType().isPrimitive()) {
                item = primitiveArrayToList(item);
            }
            list.add(item);
        }
        return list;
    }

    private static boolean isValidCharacterString(Object value) {
        if (value instanceof String) {
            String s = (String) value;
            if (s.length() == 1) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static int compareTo(Object left, Object right) {
        if (left == right) {
            return 0;
        }
        if (left == null) {
            return -1;
        } else if (right == null) {
            return 1;
        }
        if (left instanceof Comparable) {
            if (left instanceof Number) {
                if (isValidCharacterString(right)) {
                    return compareTo((Number) left, (Character) castToChar(right));
                }
                if (right instanceof Character || right instanceof Number) {
                    return compareTo((Number) left, castToNumber(right));
                }
            } else if (left instanceof Character) {
                if (isValidCharacterString(right)) {
                    return compareTo((Character) left, (Character) castToChar(right));
                }
                if (right instanceof Number) {
                    return compareTo((Character) left, (Number) right);
                }
            } else if (right instanceof Number) {
                if (isValidCharacterString(left)) {
                    return compareTo((Character) castToChar(left), (Number) right);
                }
            } else if (left instanceof String && right instanceof Character) {
                return ((String) left).compareTo(right.toString());
            } else if (left instanceof CharSequence && right instanceof CharSequence) {
                return String.valueOf(left).compareTo(String.valueOf(right));
            }
            if (left.getClass().isAssignableFrom(right.getClass())
                || (right.getClass() != Object.class && right.getClass().isAssignableFrom(left.getClass())) //GROOVY-4046
                || (left instanceof CharSequence && right instanceof CharSequence)) {
                Comparable comparable = (Comparable) left;
                return comparable.compareTo(right);
            }
        }

        throw new GriffonException("Cannot compare " + left.getClass().getName() + " with value '" +
            left + "' and " + right.getClass().getName() + " with value '" + right + "'");
    }

    public static int compareTo(Character left, Number right) {
        return compareTo(Integer.valueOf(left), right);
    }

    public static int compareTo(Number left, Character right) {
        return compareTo(left, Integer.valueOf(right));
    }

    public static int compareTo(Character left, Character right) {
        return compareTo(Integer.valueOf(left), right);
    }

    public static int compareTo(Number left, Number right) {
        if (isFloatingPoint(left) || isFloatingPoint(right)) {
            return Double.compare(left.doubleValue(), right.doubleValue());
        }
        if (isBigDecimal(left) || isBigDecimal(right)) {
            return castToBigDecimal(left).compareTo(castToBigDecimal(right));
        }
        if (isBigInteger(left) || isBigInteger(right)) {
            return castToBigInteger(left).compareTo(castToBigInteger(right));
        }
        if (isLong(left) || isLong(right)) {
            return Long.compare(left.longValue(), right.longValue());
        }
        return Integer.compare(left.intValue(), right.intValue());
    }

    public static boolean isFloatingPoint(Number number) {
        return number instanceof Double || number instanceof Float;
    }

    public static boolean isInteger(Number number) {
        return number instanceof Integer;
    }

    public static boolean isLong(Number number) {
        return number instanceof Long;
    }

    public static boolean isBigDecimal(Number number) {
        return number instanceof BigDecimal;
    }

    public static boolean isBigInteger(Number number) {
        return number instanceof BigInteger;
    }
}
