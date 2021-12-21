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
package griffon.util;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class ObjectUtils {
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Object[] EMPTY_ARGS = EMPTY_OBJECT_ARRAY;

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_COMPATIBLE_CLASSES = new LinkedHashMap<>();
    public static final Map<String, String> PRIMITIVE_TYPE_COMPATIBLE_TYPES = new LinkedHashMap<>();

    private static final Pattern GETTER_PATTERN_1 = Pattern.compile("^get[A-Z][\\w]*$");
    private static final Pattern GETTER_PATTERN_2 = Pattern.compile("^is[A-Z][\\w]*$");
    private static final Pattern SETTER_PATTERN = Pattern.compile("^set[A-Z][\\w]*$");
    private static final String ERROR_METHOD_NULL = "Argument 'method' must not be null";
    private static final String MESSAGE = "message";

    static {
        registerPrimitiveClassPair(Boolean.class, boolean.class);
        registerPrimitiveClassPair(Integer.class, int.class);
        registerPrimitiveClassPair(Short.class, short.class);
        registerPrimitiveClassPair(Byte.class, byte.class);
        registerPrimitiveClassPair(Character.class, char.class);
        registerPrimitiveClassPair(Long.class, long.class);
        registerPrimitiveClassPair(Float.class, float.class);
        registerPrimitiveClassPair(Double.class, double.class);
    }

    private ObjectUtils() {
        // prevent instantiation
    }

    /**
     * Just add two entries to the class compatibility map
     *
     * @param left
     * @param right
     */
    private static void registerPrimitiveClassPair(Class<?> left, Class<?> right) {
        PRIMITIVE_TYPE_COMPATIBLE_CLASSES.put(left, right);
        PRIMITIVE_TYPE_COMPATIBLE_CLASSES.put(right, left);
        PRIMITIVE_TYPE_COMPATIBLE_TYPES.put(left.getName(), right.getName());
        PRIMITIVE_TYPE_COMPATIBLE_TYPES.put(right.getName(), left.getName());
    }

    /**
     * Finds out if the given {@code Method} is an instance method, i.e,
     * it is public and non-static.
     *
     * @param method a Method reference
     * @return true if the method is an instance method, false otherwise.
     */
    public static boolean isInstanceMethod(@Nonnull Method method) {
        return isInstanceMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} is an instance method, i.e,
     * it is public and non-static.
     *
     * @param method a Method reference
     * @return true if the method is an instance method, false otherwise.
     */
    public static boolean isInstanceMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} is an instance method, i.e,
     * it is public and non-static.
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an instance method, false otherwise.
     */
    public static boolean isInstanceMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        int modifiers = method.getModifiers();
        return Modifier.isPublic(modifiers) &&
            !Modifier.isAbstract(modifiers) &&
            !Modifier.isStatic(modifiers);
    }

    /**
     * Finds out if the given {@code Method} is a getter method.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isGetterMethod(getMethod("getFoo"))       = true
     * isGetterMethod(getMethod("getfoo") )      = false
     * isGetterMethod(getMethod("mvcGroupInit")) = false
     * isGetterMethod(getMethod("isFoo"))        = true
     * isGetterMethod(getMethod("island"))       = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is a getter, false otherwise.
     */
    public static boolean isGetterMethod(@Nonnull Method method) {
        return isGetterMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} is a getter method.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isGetterMethod(getMethod("getFoo"))       = true
     * isGetterMethod(getMethod("getfoo") )      = false
     * isGetterMethod(getMethod("mvcGroupInit")) = false
     * isGetterMethod(getMethod("isFoo"))        = true
     * isGetterMethod(getMethod("island"))       = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is a getter, false otherwise.
     */
    public static boolean isGetterMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isGetterMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MetaMethod} is a getter method.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isGetterMethod(getMethod("getFoo"))       = true
     * isGetterMethod(getMethod("getfoo") )      = false
     * isGetterMethod(getMethod("mvcGroupInit")) = false
     * isGetterMethod(getMethod("isFoo"))        = true
     * isGetterMethod(getMethod("island"))       = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is a getter, false otherwise.
     */
    public static boolean isGetterMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            (GETTER_PATTERN_1.matcher(method.getName()).matches() || GETTER_PATTERN_2.matcher(method.getName()).matches());
    }

    /**
     * Finds out if the given {@code Method} is a setter method.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isGetterMethod(getMethod("setFoo"))       = true
     * isGetterMethod(getMethod("setfoo"))       = false
     * isGetterMethod(getMethod("mvcGroupInit")) = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is a setter, false otherwise.
     */
    public static boolean isSetterMethod(@Nonnull Method method) {
        return isSetterMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} is a setter method.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isGetterMethod(getMethod("setFoo"))       = true
     * isGetterMethod(getMethod("setfoo"))       = false
     * isGetterMethod(getMethod("mvcGroupInit")) = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is a setter, false otherwise.
     */
    public static boolean isSetterMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isSetterMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} is a setter method.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isGetterMethod(getMethod("setFoo"))       = true
     * isGetterMethod(getMethod("setfoo"))       = false
     * isGetterMethod(getMethod("mvcGroupInit")) = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is a setter, false otherwise.
     */
    public static boolean isSetterMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) && SETTER_PATTERN.matcher(method.getName()).matches();
    }

    /**
     * Checks that the specified condition is met. This method is designed
     * primarily for doing parameter validation in methods and constructors,
     * as demonstrated below:
     * <blockquote><pre>
     * public Foo(int[] array) {
     *     GriffonClassUtils.requireState(array.length > 0);
     * }
     * </pre></blockquote>
     *
     * @param condition the condition to check
     * @throws IllegalStateException if {@code condition} evaluates to false
     */
    public static void requireState(boolean condition) {
        if (!condition) {
            throw new IllegalStateException();
        }
    }

    /**
     * Checks that the specified condition is met and throws a customized
     * {@link IllegalStateException} if it is. This method is designed primarily
     * for doing parameter validation in methods and constructors with multiple
     * parameters, as demonstrated below:
     * <blockquote><pre>
     * public Foo(int[] array) {
     *     GriffonClassUtils.requireState(array.length > 0, "array must not be empty");
     * }
     * </pre></blockquote>
     *
     * @param condition the condition to check
     * @param message   detail message to be used in the event that a {@code
     *                  IllegalStateException} is thrown
     * @throws IllegalStateException if {@code condition} evaluates to false
     */
    public static void requireState(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static byte[] requireNonEmpty(@Nonnull byte[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static byte[] requireNonEmpty(@Nonnull byte[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static short[] requireNonEmpty(@Nonnull short[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static short[] requireNonEmpty(@Nonnull short[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static int[] requireNonEmpty(@Nonnull int[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static int[] requireNonEmpty(@Nonnull int[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static long[] requireNonEmpty(@Nonnull long[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static long[] requireNonEmpty(@Nonnull long[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static float[] requireNonEmpty(@Nonnull float[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static float[] requireNonEmpty(@Nonnull float[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static double[] requireNonEmpty(@Nonnull double[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static double[] requireNonEmpty(@Nonnull double[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static char[] requireNonEmpty(@Nonnull char[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static char[] requireNonEmpty(@Nonnull char[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static boolean[] requireNonEmpty(@Nonnull boolean[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static boolean[] requireNonEmpty(@Nonnull boolean[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param array the array to check
     * @throws NullPointerException  if {@code array} is null
     * @throws IllegalStateException if {@code array} is empty
     */
    public static <E> E[] requireNonEmpty(@Nonnull E[] array) {
        requireNonNull(array);
        requireState(array.length != 0);
        return array;
    }

    /**
     * Checks that the specified array is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param array   the array to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code array} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code array} is empty
     */
    public static <E> E[] requireNonEmpty(@Nonnull E[] array, @Nonnull String message) {
        requireNonNull(array);
        requireState(array.length != 0, requireNonBlank(message, MESSAGE));
        return array;
    }

    /**
     * Checks that the specified collection is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param collection the collection to check
     * @throws NullPointerException  if {@code collection} is null
     * @throws IllegalStateException if {@code collection} is empty
     */
    public static Collection<?> requireNonEmpty(@Nonnull Collection<?> collection) {
        requireNonNull(collection);
        requireState(!collection.isEmpty());
        return collection;
    }

    /**
     * Checks that the specified collection is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param collection the collection to check
     * @param message    detail message to be used in the event that a {@code
     *                   IllegalStateException} is thrown
     * @throws NullPointerException     if {@code collection} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code collection} is empty
     */
    public static Collection<?> requireNonEmpty(@Nonnull Collection<?> collection, @Nonnull String message) {
        requireNonNull(collection);
        requireState(!collection.isEmpty(), requireNonBlank(message, MESSAGE));
        return collection;
    }

    /**
     * Checks that the specified map is not empty, throwing a
     * {@link IllegalStateException} if it is.
     *
     * @param map the map to check
     * @throws NullPointerException  if {@code map} is null
     * @throws IllegalStateException if {@code map} is empty
     */
    public static Map<?, ?> requireNonEmpty(@Nonnull Map<?, ?> map) {
        requireNonNull(map);
        requireState(!map.isEmpty());
        return map;
    }

    /**
     * Checks that the specified map is not empty, throwing a customized
     * {@link IllegalStateException} if it is.
     *
     * @param map     the map to check
     * @param message detail message to be used in the event that a {@code
     *                IllegalStateException} is thrown
     * @throws NullPointerException     if {@code map} is null
     * @throws IllegalArgumentException if {@code message} is {@code blank}
     * @throws IllegalStateException    if {@code map} is empty
     */
    public static Map<?, ?> requireNonEmpty(@Nonnull Map<?, ?> map, @Nonnull String message) {
        requireNonNull(map);
        requireState(!map.isEmpty(), requireNonBlank(message, MESSAGE));
        return map;
    }

    /**
     * Convenience method for converting a collection to an Object[]
     *
     * @param c The collection
     * @return An object array
     */
    public static Object[] collectionToObjectArray(Collection<?> c) {
        if (c == null) {
            return EMPTY_OBJECT_ARRAY;
        }
        return c.toArray(new Object[c.size()]);
    }

    /**
     * Detect if left and right types are matching types. In particular,
     * test if one is a primitive type and the other is the corresponding
     * Java wrapper type. Primitive and wrapper classes may be passed to
     * either arguments.
     *
     * @param leftType
     * @param rightType
     * @return true if one of the classes is a native type and the other the object representation
     * of the same native type
     */
    public static boolean isMatchBetweenPrimitiveAndWrapperTypes(@Nonnull Class<?> leftType, @Nonnull Class<?> rightType) {
        requireNonNull(leftType, "Left type is null!");
        requireNonNull(rightType, "Right type is null!");
        return isMatchBetweenPrimitiveAndWrapperTypes(leftType.getName(), rightType.getName());
    }

    /**
     * Detect if left and right types are matching types. In particular,
     * test if one is a primitive type and the other is the corresponding
     * Java wrapper type. Primitive and wrapper classes may be passed to
     * either arguments.
     *
     * @param leftType
     * @param rightType
     * @return true if one of the classes is a native type and the other the object representation
     * of the same native type
     */
    public static boolean isMatchBetweenPrimitiveAndWrapperTypes(@Nonnull String leftType, @Nonnull String rightType) {
        requireNonBlank(leftType, "Left type is null!");
        requireNonBlank(rightType, "Right type is null!");
        String r = PRIMITIVE_TYPE_COMPATIBLE_TYPES.get(leftType);
        return r != null && r.equals(rightType);
    }

    /**
     * Returns true if the specified clazz parameter is either the same as, or is a superclass or super interface
     * of, the specified type parameter. Converts primitive types to compatible class automatically.
     *
     * @param clazz
     * @param type
     * @return True if the class is a taglib
     * @see java.lang.Class#isAssignableFrom(Class)
     */
    public static boolean isAssignableOrConvertibleFrom(@Nullable Class<?> clazz, @Nullable Class<?> type) {
        if (type == null || clazz == null) {
            return false;
        } else if (type.isPrimitive()) {
            // convert primitive type to compatible class
            Class<?> primitiveClass = PRIMITIVE_TYPE_COMPATIBLE_CLASSES.get(type);
            return primitiveClass != null && clazz.isAssignableFrom(primitiveClass);
        } else {
            return clazz.isAssignableFrom(type);
        }
    }

    /**
     * Retrieves a boolean value from a Map for the given key
     *
     * @param key The key that references the boolean value
     * @param map The map to look in
     * @return A boolean value which will be false if the map is null, the map doesn't contain the key or the value is false
     */
    public static boolean getBooleanFromMap(@Nullable String key, @Nullable Map<String, Object> map) {
        if (map == null) {
            return false;
        }
        if (map.containsKey(key)) {
            Object o = map.get(key);
            if (o == null) {
                return false;
            } else if (o instanceof Boolean) {
                return (Boolean) o;
            } else {
                return Boolean.valueOf(o.toString());
            }
        }
        return false;
    }
}
