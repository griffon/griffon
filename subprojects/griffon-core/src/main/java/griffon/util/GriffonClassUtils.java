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
package griffon.util;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.event.EventPublisher;
import griffon.core.i18n.MessageSource;
import griffon.core.mvc.MVCHandler;
import griffon.core.resources.ResourceHandler;
import griffon.core.resources.ResourceResolver;
import griffon.core.threading.ThreadingHandler;
import griffon.exceptions.BeanInstantiationException;
import griffon.exceptions.FieldException;
import griffon.exceptions.InstanceMethodInvocationException;
import griffon.exceptions.PropertyException;
import griffon.exceptions.StaticMethodInvocationException;

import javax.application.event.EventHandler;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static griffon.util.AnnotationUtils.findAnnotation;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static griffon.util.MethodUtils.invokeExactMethod;
import static griffon.util.MethodUtils.invokeMethod;
import static java.util.Objects.requireNonNull;

/**
 * Class containing utility methods for dealing with Griffon class artifacts.<p>
 * Contains utility methods copied from commons-lang and commons-beanutils in order
 * to reduce dependencies on external libraries.<p>
 * <p>
 * <b>Contains code copied from commons-beanutils and commons-langs</b>
 *
 * @author Graeme Rocher (Grails 0.1)
 */
public class GriffonClassUtils {
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Object[] EMPTY_ARGS = EMPTY_OBJECT_ARRAY;

    private static final String PROPERTY_GET_PREFIX = "get";
    private static final String PROPERTY_IS_PREFIX = "is";
    private static final String PROPERTY_SET_PREFIX = "set";
    private static final String ON_SHUTDOWN_METHOD_NAME = "onShutdown";
    public static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_COMPATIBLE_CLASSES = new LinkedHashMap<>();
    public static final Map<String, String> PRIMITIVE_TYPE_COMPATIBLE_TYPES = new LinkedHashMap<>();

    private static final Pattern EVENT_HANDLER_PATTERN = Pattern.compile("^on[A-Z][\\w]*$");
    private static final Pattern CONTRIBUTION_PATTERN = Pattern.compile("^with[A-Z][a-z0-9_]*[\\w]*$");
    private static final Pattern GETTER_PATTERN_1 = Pattern.compile("^get[A-Z][\\w]*$");
    private static final Pattern GETTER_PATTERN_2 = Pattern.compile("^is[A-Z][\\w]*$");
    private static final Pattern SETTER_PATTERN = Pattern.compile("^set[A-Z][\\w]*$");
    private static final Set<MethodDescriptor> BASIC_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> ARTIFACT_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> MVC_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> THREADING_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> EVENT_PUBLISHER_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> OBSERVABLE_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> RESOURCE_HANDLER_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> MESSAGE_SOURCE_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> RESOURCE_RESOLVER_METHODS = new TreeSet<>();
    private static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    private static final String ERROR_METHOD_NAME_BLANK = "Argument 'methodName' must not be blank";
    private static final String ERROR_OBJECT_NULL = "Argument 'object' must not be null";
    private static final String ERROR_CLAZZ_NULL = "Argument 'clazz' must not be null";
    private static final String ERROR_DESCRIPTOR_NULL = "Argument 'descriptor' must not be null";
    private static final String ERROR_BEAN_NULL = "Argument 'bean' must not be null";
    private static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";
    private static final String ERROR_PROPERTIES_NULL = "Argument 'properties' must not be null";
    private static final String ERROR_FIELDS_NULL = "Argument 'fields' must not be null";
    private static final String ERROR_PROPERTY_NAME_BLANK = "Argument 'propertyName' must not be blank";
    private static final String ERROR_METHOD_NULL = "Argument 'method' must not be null";
    private static final String MESSAGE = "message";

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

    static {
        registerPrimitiveClassPair(Boolean.class, boolean.class);
        registerPrimitiveClassPair(Integer.class, int.class);
        registerPrimitiveClassPair(Short.class, short.class);
        registerPrimitiveClassPair(Byte.class, byte.class);
        registerPrimitiveClassPair(Character.class, char.class);
        registerPrimitiveClassPair(Long.class, long.class);
        registerPrimitiveClassPair(Float.class, float.class);
        registerPrimitiveClassPair(Double.class, double.class);

        for (Method method : Object.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method);
            if (!BASIC_METHODS.contains(md)) {
                BASIC_METHODS.add(md);
            }
        }

        try {
            Class<?> groovyObjectClass = GriffonClassUtils.class.getClassLoader().loadClass("groovy.lang.GroovyObject");
            for (Method method : groovyObjectClass.getMethods()) {
                MethodDescriptor md = MethodDescriptor.forMethod(method, true);
                if (!BASIC_METHODS.contains(md)) {
                    BASIC_METHODS.add(md);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            // ignore
        }

        try {
            Class<?> groovyObjectClass = GriffonClassUtils.class.getClassLoader().loadClass("groovy.lang.GroovyObjectSupport");
            for (Method method : groovyObjectClass.getMethods()) {
                MethodDescriptor md = MethodDescriptor.forMethod(method);
                if (!BASIC_METHODS.contains(md)) {
                    BASIC_METHODS.add(md);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            // ignore
        }

        for (Method method : GriffonArtifact.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!ARTIFACT_METHODS.contains(md)) {
                ARTIFACT_METHODS.add(md);
            }
        }

        // MVC_METHODS.add(new MethodDescriptor("getMvcGroup"));
        for (Method method : MVCHandler.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!MVC_METHODS.contains(md)) {
                MVC_METHODS.add(md);
            }
        }
        for (Method method : GriffonMvcArtifact.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!MVC_METHODS.contains(md)) {
                MVC_METHODS.add(md);
            }
        }

        // GriffonView
        MVC_METHODS.add(new MethodDescriptor("initUI"));
        // GriffonController
        MVC_METHODS.add(new MethodDescriptor("invokeAction", new Class<?>[]{String.class, Object[].class}));
        MVC_METHODS.add(new MethodDescriptor("invokeAction", new Class<?>[]{String.class, Object[].class}, Modifier.PUBLIC | Modifier.TRANSIENT));

        for (Method method : ThreadingHandler.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!THREADING_METHODS.contains(md)) {
                THREADING_METHODS.add(md);
            }
        }
        // Special case due to the usage of varargs
        //THREADING_METHODS.add(new MethodDescriptor("executeFuture", new Class<?>[]{Object[].class}));

        for (Method method : EventPublisher.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!EVENT_PUBLISHER_METHODS.contains(md)) {
                EVENT_PUBLISHER_METHODS.add(md);
            }
        }


        try {
            Class<?> observableClass = GriffonClassUtils.class.getClassLoader().loadClass("griffon.beans.Observable");
            for (Method method : observableClass.getMethods()) {
                MethodDescriptor md = MethodDescriptor.forMethod(method, true);
                if (!OBSERVABLE_METHODS.contains(md)) {
                    OBSERVABLE_METHODS.add(md);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            // ignore
        }

        try {
            Class<?> vetoableClass = GriffonClassUtils.class.getClassLoader().loadClass("griffon.beans.Vetoable");
            for (Method method : vetoableClass.getMethods()) {
                MethodDescriptor md = MethodDescriptor.forMethod(method, true);
                if (!OBSERVABLE_METHODS.contains(md)) {
                    OBSERVABLE_METHODS.add(md);
                }
            }
        } catch (ClassNotFoundException cnfe) {
            // ignore
        }

        for (Method method : ResourceHandler.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!RESOURCE_HANDLER_METHODS.contains(md)) {
                RESOURCE_HANDLER_METHODS.add(md);
            }
        }

        for (Method method : MessageSource.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!MESSAGE_SOURCE_METHODS.contains(md)) {
                MESSAGE_SOURCE_METHODS.add(md);
            }
        }

        for (Method method : ResourceResolver.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method, true);
            if (!RESOURCE_RESOLVER_METHODS.contains(md)) {
                RESOURCE_RESOLVER_METHODS.add(md);
            }
        }
    }

    private GriffonClassUtils() {
        // prevent instantiation
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     * Finds out if the given Method represents an event handler
     * by matching its name against the following pattern:
     * "^on[A-Z][\\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isEventHandler(getMethod("onBootstrapEnd")) = true
     * isEventHandler(getMethod("mvcGroupInit"))   = false
     * isEventHandler(getMethod("online"))         = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method name matches the given event handler
     * pattern, false otherwise.
     */
    public static boolean isEventHandler(@Nonnull Method method) {
        return isEventHandler(method, false);
    }

    /**
     * Finds out if the given Method represents an event handler
     * by matching its name against the following pattern:
     * "^on[A-Z][\\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isEventHandler(getMethod("onBootstrapEnd")) = true
     * isEventHandler(getMethod("mvcGroupInit"))   = false
     * isEventHandler(getMethod("online"))         = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method name matches the given event handler
     * pattern, false otherwise.
     */
    public static boolean isEventHandler(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isEventHandler(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given Method represents an event handler
     * by matching its name against the following pattern:
     * "^on[A-Z][\\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isEventHandler(getMethod("onBootstrapEnd")) = true
     * isEventHandler(getMethod("mvcGroupInit"))   = false
     * isEventHandler(getMethod("online"))         = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method name matches the given event handler
     * pattern, false otherwise.
     */
    public static boolean isEventHandler(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            findAnnotation(method.getAnnotations(), EventHandler.class) != null &&
            method.getParameterTypes().length == 1;
    }

    /**
     * Finds out if the given {@code Method} belongs either to the
     * {@code Object} class or the {@code GroovyObject} class.<p>
     *
     * @param method a Method reference
     *
     * @return true if the method belongs to {@code Object} or
     * {@code GroovyObject}, false otherwise.
     */
    public static boolean isBasicMethod(@Nonnull Method method) {
        return isBasicMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs either to the
     * {@code Object} class or the {@code GroovyObject} class.<p>
     *
     * @param method a Method reference
     *
     * @return true if the method belongs to {@code Object} or
     * {@code GroovyObject}, false otherwise.
     */
    public static boolean isBasicMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isBasicMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs either to the
     * {@code Object} class or the {@code GroovyObject} class.<p>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method belongs to {@code Object} or
     * {@code GroovyObject}, false otherwise.
     */
    public static boolean isBasicMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) && BASIC_METHODS.contains(method);
    }

    /**
     * Finds out if the given string represents the name of a
     * contribution method by matching against the following pattern:
     * "^with[A-Z][a-z0-9_]*[\w]*$"<p>
     * <p>
     * <pre>
     * isContributionMethod("withRest")     = true
     * isContributionMethod("withMVCGroup") = false
     * isContributionMethod("without")      = false
     * </pre>
     *
     * @param name the name of a possible contribution method
     *
     * @return true if the name matches the given contribution method
     * pattern, false otherwise.
     */
    public static boolean isContributionMethod(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        return CONTRIBUTION_PATTERN.matcher(name).matches();
    }

    /**
     * Finds out if the given Method represents a contribution method
     * by matching its name against the following pattern:
     * "^with[A-Z][a-z0-9_]*[\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isContributionMethod(getMethod("withRest"))     = true
     * isContributionMethod(getMethod("withMVCGroup")) = false
     * isContributionMethod(getMethod("without"))      = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method name matches the given contribution method
     * pattern, false otherwise.
     */
    public static boolean isContributionMethod(@Nonnull Method method) {
        return isContributionMethod(method, false);
    }

    /**
     * Finds out if the given Method represents a contribution method
     * by matching its name against the following pattern:
     * "^with[A-Z][a-z0-9_]*[\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isContributionMethod(getMethod("withRest"))     = true
     * isContributionMethod(getMethod("withMVCGroup")) = false
     * isContributionMethod(getMethod("without"))      = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method name matches the given contribution method
     * pattern, false otherwise.
     */
    public static boolean isContributionMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isContributionMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given Method represents a contribution method
     * by matching its name against the following pattern:
     * "^with[A-Z][a-z0-9_]*[\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isContributionMethod(getMethod("withRest"))     = true
     * isContributionMethod(getMethod("withMVCGroup")) = false
     * isContributionMethod(getMethod("without"))      = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method name matches the given contribution method
     * pattern, false otherwise.
     */
    public static boolean isContributionMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            CONTRIBUTION_PATTERN.matcher(method.getName()).matches();
    }

    /**
     * Finds out if the given {@code Method} was injected by the Groovy
     * compiler.<p>
     * Performs a basic checks against the method's name, returning true
     * if the name starts with either "super$" or "this$".
     *
     * @param method a Method reference
     *
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isGroovyInjectedMethod(@Nonnull Method method) {
        return isGroovyInjectedMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} was injected by the Groovy
     * compiler.<p>
     * Performs a basic checks against the method's name, returning true
     * if the name starts with either "super$" or "this$".
     *
     * @param method a Method reference
     *
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isGroovyInjectedMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isGroovyInjectedMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} was injected by the Groovy
     * compiler.<p>
     * Performs a basic checks against the method's name, returning true
     * if the name starts with either "super$" or "this$".
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isGroovyInjectedMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            (method.getName().startsWith("super$") || method.getName().startsWith("this$"));
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @return true if the method is a setter, false otherwise.
     */
    public static boolean isSetterMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) && SETTER_PATTERN.matcher(method.getName()).matches();
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined Artifact methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isArtifactMethod(getMethod("newInstance"))    = true
     * isArtifactMethod(getMethod("griffonDestroy")) = false
     * isArtifactMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an Artifact method, false otherwise.
     */
    public static boolean isArtifactMethod(@Nonnull Method method) {
        return isArtifactMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined Artifact methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isArtifactMethod(getMethod("newInstance"))    = true
     * isArtifactMethod(getMethod("griffonDestroy")) = false
     * isArtifactMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an Artifact method, false otherwise.
     */
    public static boolean isArtifactMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isArtifactMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined Artifact methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isArtifactMethod(getMethod("newInstance"))    = true
     * isArtifactMethod(getMethod("griffonDestroy")) = false
     * isArtifactMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is an Artifact method, false otherwise.
     */
    public static boolean isArtifactMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            ARTIFACT_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined MVC methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isMvcMethod(getMethod("mvcGroupInit"))    = true
     * isMvcMethod(getMethod("mvcGroupDestroy")) = true
     * isMvcMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an MVC method, false otherwise.
     */
    public static boolean isMvcMethod(@Nonnull Method method) {
        return isMvcMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined MVC methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isMvcMethod(getMethod("mvcGroupInit"))    = true
     * isMvcMethod(getMethod("mvcGroupDestroy")) = true
     * isMvcMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an MVC method, false otherwise.
     */
    public static boolean isMvcMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isMvcMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined MVC methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isMvcMethod(getMethod("mvcGroupInit"))    = true
     * isMvcMethod(getMethod("mvcGroupDestroy")) = true
     * isMvcMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is an MVC method, false otherwise.
     */
    public static boolean isMvcMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            MVC_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined threading methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isThreadingMethod(getMethod("execOutsideUI"))    = true
     * isThreadingMethod(getMethod("doLater"))          = true
     * isThreadingMethod(getMethod("foo"))              = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is a threading method, false otherwise.
     */
    public static boolean isThreadingMethod(@Nonnull Method method) {
        return isThreadingMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined threading methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isThreadingMethod(getMethod("execOutsideUI"))    = true
     * isThreadingMethod(getMethod("doLater"))          = true
     * isThreadingMethod(getMethod("foo"))              = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is a threading method, false otherwise.
     */
    public static boolean isThreadingMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isThreadingMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined threading methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isThreadingMethod(getMethod("execOutsideUI"))    = true
     * isThreadingMethod(getMethod("doLater"))          = true
     * isThreadingMethod(getMethod("foo"))              = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is a threading method, false otherwise.
     */
    public static boolean isThreadingMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            THREADING_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined event publisher methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isEventPublisherMethod(getMethod("addEventPublisher"))  = true
     * isEventPublisherMethod(getMethod("publishEvent"))       = true
     * isEventPublisherMethod(getMethod("foo"))                = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an @EventPublisher method, false otherwise.
     */
    public static boolean isEventPublisherMethod(@Nonnull Method method) {
        return isEventPublisherMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined event publisher methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isEventPublisherMethod(getMethod("addEventPublisher"))  = true
     * isEventPublisherMethod(getMethod("publishEvent"))       = true
     * isEventPublisherMethod(getMethod("foo"))                = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an @EventPublisher method, false otherwise.
     */
    public static boolean isEventPublisherMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isEventPublisherMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined event publisher methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isEventPublisherMethod(getMethod("addEventPublisher"))  = true
     * isEventPublisherMethod(getMethod("publishEvent"))       = true
     * isEventPublisherMethod(getMethod("foo"))                = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is an @EventPublisher method, false otherwise.
     */
    public static boolean isEventPublisherMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            EVENT_PUBLISHER_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined observable methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isObservableMethod(getMethod("addPropertyChangeListener"))  = true
     * isObservableMethod(getMethod("getPropertyChangeListeners")) = true
     * isObservableMethod(getMethod("foo"))                        = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isObservableMethod(@Nonnull Method method) {
        return isObservableMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined observable methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isObservableMethod(getMethod("addPropertyChangeListener"))  = true
     * isObservableMethod(getMethod("getPropertyChangeListeners")) = true
     * isObservableMethod(getMethod("foo"))                        = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isObservableMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isObservableMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined observable methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isObservableMethod(getMethod("addPropertyChangeListener"))  = true
     * isObservableMethod(getMethod("getPropertyChangeListeners")) = true
     * isObservableMethod(getMethod("foo"))                        = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isObservableMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            OBSERVABLE_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined resources methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isResourceHandlerMethod(getMethod("getResourceAsURL"))    = true
     * isResourceHandlerMethod(getMethod("getResourceAsStream")) = true
     * isResourceHandlerMethod(getMethod("foo"))                 = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an ResourceHandler method, false otherwise.
     */
    public static boolean isResourceHandlerMethod(@Nonnull Method method) {
        return isResourceHandlerMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined resources methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isResourceHandlerMethod(getMethod("getResourceAsURL"))    = true
     * isResourceHandlerMethod(getMethod("getResourceAsStream")) = true
     * isResourceHandlerMethod(getMethod("foo"))                 = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an ResourceHandler method, false otherwise.
     */
    public static boolean isResourceHandlerMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isResourceHandlerMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined resources methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isResourceHandlerMethod(getMethod("getResourceAsURL"))    = true
     * isResourceHandlerMethod(getMethod("getResourceAsStream")) = true
     * isResourceHandlerMethod(getMethod("foo"))                 = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is an ResourceHandler method, false otherwise.
     */
    public static boolean isResourceHandlerMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            RESOURCE_HANDLER_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined message source methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isMessageSourceMethod(getMethod("getMessage"))    = true
     * isMessageSourceMethod(getMethod("foo"))           = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an MessageSource method, false otherwise.
     */
    public static boolean isMessageSourceMethod(@Nonnull Method method) {
        return isMessageSourceMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined message source methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isMessageSourceMethod(getMethod("getMessage"))    = true
     * isMessageSourceMethod(getMethod("foo"))           = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an MessageSource method, false otherwise.
     */
    public static boolean isMessageSourceMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isMessageSourceMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined message source methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isMessageSourceMethod(getMethod("getMessage"))    = true
     * isMessageSourceMethod(getMethod("foo"))           = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is an MessageSource method, false otherwise.
     */
    public static boolean isMessageSourceMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            MESSAGE_SOURCE_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined resource resolver methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isResourceResolverMethod(getMethod("resolveResource")) = true
     * isResourceResolverMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an ResourceResolver method, false otherwise.
     */
    public static boolean isResourceResolverMethod(@Nonnull Method method) {
        return isResourceResolverMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined resource resolver methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isResourceResolverMethod(getMethod("resolveResource")) = true
     * isResourceResolverMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a Method reference
     *
     * @return true if the method is an ResourceResolver method, false otherwise.
     */
    public static boolean isResourceResolverMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isResourceResolverMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined resource resolver methods by convention.
     * <p>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isResourceResolverMethod(getMethod("resolveResource")) = true
     * isResourceResolverMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method is an ResourceResolver method, false otherwise.
     */
    public static boolean isResourceResolverMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            RESOURCE_RESOLVER_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} is an instance method, i.e,
     * it is public and non-static.
     *
     * @param method a Method reference
     *
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
     *
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
     *
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
     * Finds out if the given {@code Method} matches the following criteria:<ul>
     * <li>isInstanceMethod(method)</li>
     * <li>! isBasicMethod(method)</li>
     * <li>! isGroovyInjectedMethod(method)</li>
     * <li>! isThreadingMethod(method)</li>
     * <li>! isArtifactMethod(method)</li>
     * <li>! isMvcMethod(method)</li>
     * <li>! isServiceMethod(method)</li>
     * <li>! isEventPublisherMethod(method)</li>
     * <li>! isObservableMethod(method)</li>
     * <li>! isResourceHandlerMethod(method)</li>
     * <li>! isGetterMethod(method)</li>
     * <li>! isSetterMethod(method)</li>
     * <li>! isContributionMethod(method)</li>
     * </ul>
     *
     * @param method a Method reference
     *
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isPlainMethod(@Nonnull Method method) {
        return isPlainMethod(method, false);
    }

    /**
     * Finds out if the given {@code Method} matches the following criteria:<ul>
     * <li>isInstanceMethod(method)</li>
     * <li>! isBasicMethod(method)</li>
     * <li>! isGroovyInjectedMethod(method)</li>
     * <li>! isThreadingMethod(method)</li>
     * <li>! isArtifactMethod(method)</li>
     * <li>! isMvcMethod(method)</li>
     * <li>! isServiceMethod(method)</li>
     * <li>! isEventPublisherMethod(method)</li>
     * <li>! isObservableMethod(method)</li>
     * <li>! isResourceHandlerMethod(method)</li>
     * <li>! isGetterMethod(method)</li>
     * <li>! isSetterMethod(method)</li>
     * <li>! isContributionMethod(method)</li>
     * </ul>
     *
     * @param method a Method reference
     *
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isPlainMethod(@Nonnull Method method, boolean removeAbstractModifier) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isPlainMethod(MethodDescriptor.forMethod(method, removeAbstractModifier));
    }

    /**
     * Finds out if the given {@code MethodDescriptor} matches the following criteria:<ul>
     * <li>isInstanceMethod(method)</li>
     * <li>! isBasicMethod(method)</li>
     * <li>! isGroovyInjectedMethod(method)</li>
     * <li>! isThreadingMethod(method)</li>
     * <li>! isArtifactMethod(method)</li>
     * <li>! isMvcMethod(method)</li>
     * <li>! isServiceMethod(method)</li>
     * <li>! isEventPublisherMethod(method)</li>
     * <li>! isObservableMethod(method)</li>
     * <li>! isResourceHandlerMethod(method)</li>
     * <li>! isGetterMethod(method)</li>
     * <li>! isSetterMethod(method)</li>
     * <li>! isContributionMethod(method)</li>
     * </ul>
     *
     * @param method a MethodDescriptor reference
     *
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isPlainMethod(@Nonnull MethodDescriptor method) {
        requireNonNull(method, ERROR_METHOD_NULL);
        return isInstanceMethod(method) &&
            !isBasicMethod(method) &&
            !isGroovyInjectedMethod(method) &&
            !isThreadingMethod(method) &&
            !isArtifactMethod(method) &&
            !isMvcMethod(method) &&
            !isEventPublisherMethod(method) &&
            !isObservableMethod(method) &&
            !isResourceHandlerMethod(method) &&
            !isGetterMethod(method) &&
            !isSetterMethod(method) &&
            !isContributionMethod(method);
    }

    /**
     * Returns true if the specified property in the specified class is of the specified type
     *
     * @param clazz        The class which contains the property
     * @param propertyName The property name
     * @param type         The type to check
     *
     * @return A boolean value
     */
    public static boolean isPropertyOfType(Class<?> clazz, String propertyName, Class<?> type) {
        try {
            Class<?> propType = getPropertyType(clazz, propertyName);
            return propType != null && propType.equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Instantiates a Class, wrapping any exceptions in a RuntimeException.
     *
     * @param clazz target Class for which an object will be instantiated
     *
     * @return the newly instantiated object.
     *
     * @throws BeanInstantiationException if an error occurs when creating the object
     */
    @Nonnull
    public static Object instantiateClass(@Nonnull Class<?> clazz) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException("Could not create an instance of " + clazz, e);
        }
    }

    @Nonnull
    public static Object instantiate(@Nonnull Class<?> clazz, @Nullable Object[] args) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        try {
            if (args == null) {
                args = EMPTY_OBJECT_ARRAY;
            }
            int arguments = args.length;
            Class<?>[] parameterTypes = new Class<?>[arguments];
            for (int i = 0; i < arguments; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            return clazz.getDeclaredConstructor(parameterTypes).newInstance(args);
        } catch (Exception e) {
            throw new BeanInstantiationException("Could not create an instance of " + clazz, e);
        }
    }

    /**
     * Returns the value of the specified property and type from an instance of the specified Griffon class
     *
     * @param clazz        The name of the class which contains the property
     * @param propertyName The property name
     * @param propertyType The property type
     *
     * @return The value of the property or null if none exists
     */
    @Nullable
    public static Object getPropertyValueOfNewInstance(@Nullable Class<?> clazz, @Nullable String propertyName, Class<?> propertyType) {
        // validate
        if (clazz == null || GriffonNameUtils.isBlank(propertyName)) {
            return null;
        }

        Object instance;
        try {
            instance = instantiateClass(clazz);
        } catch (BeanInstantiationException e) {
            return null;
        }

        return getPropertyOrStaticPropertyOrFieldValue(instance, propertyName);
    }

    /**
     * Returns the value of the specified property and type from an instance of the specified Griffon class
     *
     * @param clazz        The name of the class which contains the property
     * @param propertyName The property name
     *
     * @return The value of the property or null if none exists
     */
    public static Object getPropertyValueOfNewInstance(Class<?> clazz, String propertyName) {
        // validate
        if (clazz == null || GriffonNameUtils.isBlank(propertyName)) {
            return null;
        }

        Object instance;
        try {
            instance = instantiateClass(clazz);
        } catch (BeanInstantiationException e) {
            return null;
        }

        return getPropertyOrStaticPropertyOrFieldValue(instance, propertyName);
    }

    /**
     * Retrieves a PropertyDescriptor for the specified instance and property value
     *
     * @param instance      The instance
     * @param propertyValue The value of the property
     *
     * @return The PropertyDescriptor
     */
    public static PropertyDescriptor getPropertyDescriptorForValue(Object instance, Object propertyValue) {
        if (instance == null || propertyValue == null) { return null; }

        PropertyDescriptor[] descriptors = getPropertyDescriptors(instance.getClass());

        for (PropertyDescriptor pd : descriptors) {
            if (isAssignableOrConvertibleFrom(pd.getPropertyType(), propertyValue.getClass())) {
                Object value;
                try {
                    value = getReadMethod(pd).invoke(instance, (Object[]) null);
                } catch (Exception e) {
                    throw new RuntimeException("Problem calling readMethod of " + pd, e);
                }
                if (propertyValue.equals(value)) { return pd; }
            }
        }
        return null;
    }

    /**
     * Returns the type of the given property contained within the specified class
     *
     * @param clazz        The class which contains the property
     * @param propertyName The name of the property
     *
     * @return The property type or null if none exists
     */
    @Nullable
    public static Class<?> getPropertyType(@Nullable Class<?> clazz, @Nullable String propertyName) {
        if (clazz == null || GriffonNameUtils.isBlank(propertyName)) {
            return null;
        }

        try {
            PropertyDescriptor desc = getPropertyDescriptor(clazz, propertyName);
            if (desc != null) {
                return desc.getPropertyType();
            } else {
                return null;
            }
        } catch (Exception e) {
            // if there are any errors in instantiating just return null for the moment
            return null;
        }
    }

    /**
     * Retrieves all the properties of the given class for the given type
     *
     * @param clazz        The class to retrieve the properties from
     * @param propertyType The type of the properties you wish to retrieve
     *
     * @return An array of PropertyDescriptor instances
     */
    @Nonnull
    public static PropertyDescriptor[] getPropertiesOfType(@Nullable Class<?> clazz, @Nullable Class<?> propertyType) {
        if (clazz == null || propertyType == null) {
            return new PropertyDescriptor[0];
        }

        Set<PropertyDescriptor> properties = new HashSet<>();
        try {
            PropertyDescriptor[] descriptors = getPropertyDescriptors(clazz);

            for (PropertyDescriptor descriptor : descriptors) {
                Class<?> currentPropertyType = descriptor.getPropertyType();
                if (isTypeInstanceOfPropertyType(propertyType, currentPropertyType)) {
                    properties.add(descriptor);
                }
            }
        } catch (Exception e) {
            // if there are any errors in instantiating just return null for the moment
            return new PropertyDescriptor[0];
        }
        return properties.toArray(new PropertyDescriptor[properties.size()]);
    }

    private static boolean isTypeInstanceOfPropertyType(Class<?> type, Class<?> propertyType) {
        return propertyType.isAssignableFrom(type) && !propertyType.equals(Object.class);
    }

    /**
     * Retrieves all the properties of the given class which are assignable to the given type
     *
     * @param clazz             The class to retrieve the properties from
     * @param propertySuperType The type of the properties you wish to retrieve
     *
     * @return An array of PropertyDescriptor instances
     */
    public static PropertyDescriptor[] getPropertiesAssignableToType(Class<?> clazz, Class<?> propertySuperType) {
        if (clazz == null || propertySuperType == null) { return new PropertyDescriptor[0]; }

        Set<PropertyDescriptor> properties = new HashSet<>();
        try {
            PropertyDescriptor[] descriptors = getPropertyDescriptors(clazz);

            for (PropertyDescriptor descriptor : descriptors) {
                if (propertySuperType.isAssignableFrom(descriptor.getPropertyType())) {
                    properties.add(descriptor);
                }
            }
        } catch (Exception e) {
            return new PropertyDescriptor[0];
        }
        return properties.toArray(new PropertyDescriptor[properties.size()]);
    }

    /**
     * Retrieves a property of the given class of the specified name and type
     *
     * @param clazz        The class to retrieve the property from
     * @param propertyName The name of the property
     * @param propertyType The type of the property
     *
     * @return A PropertyDescriptor instance or null if none exists
     */
    public static PropertyDescriptor getProperty(Class<?> clazz, String propertyName, Class<?> propertyType) {
        if (clazz == null || propertyName == null || propertyType == null) { return null; }

        try {
            PropertyDescriptor pd = getPropertyDescriptor(clazz, propertyName);
            if (pd != null && pd.getPropertyType().equals(propertyType)) {
                return pd;
            } else {
                return null;
            }
        } catch (Exception e) {
            // if there are any errors in instantiating just return null for the moment
            return null;
        }
    }

    /**
     * Convenience method for converting a collection to an Object[]
     *
     * @param c The collection
     *
     * @return An object array
     */
    public static Object[] collectionToObjectArray(Collection<?> c) {
        if (c == null) { return EMPTY_OBJECT_ARRAY; }
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
     *
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
     *
     * @return true if one of the classes is a native type and the other the object representation
     * of the same native type
     */
    public static boolean isMatchBetweenPrimitiveAndWrapperTypes(@Nonnull String leftType, @Nonnull String rightType) {
        requireNonBlank(leftType, "Left type is null!");
        requireNonBlank(rightType, "Right type is null!");
        String r = PRIMITIVE_TYPE_COMPATIBLE_TYPES.get(leftType);
        return r != null && r.equals(rightType);
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    private static Method findDeclaredMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, Class[] parameterTypes) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonBlank(methodName, ERROR_METHOD_NAME_BLANK);
        while (clazz != null) {
            try {
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                if (method != null) { return method; }
            } catch (NoSuchMethodException | SecurityException e) {
                // skip
            }
            clazz = clazz.getSuperclass();
        }

        return null;
    }

    /**
     * <p>Work out if the specified property is readable and static. Java introspection does not
     * recognize this concept of static properties but Groovy does. We also consider public static fields
     * as static properties with no getters/setters</p>
     *
     * @param clazz        The class to check for static property
     * @param propertyName The property name
     *
     * @return true if the property with name propertyName has a static getter method
     */
    public static boolean isStaticProperty(@Nonnull Class<?> clazz, @Nonnull String propertyName) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonBlank(propertyName, ERROR_PROPERTY_NAME_BLANK);
        Method getter = findDeclaredMethod(clazz, getGetterName(propertyName), null);
        if (getter != null) {
            return isPublicStatic(getter);
        } else {
            try {
                Field f = clazz.getDeclaredField(propertyName);
                if (f != null) {
                    return isPublicStatic(f);
                }
            } catch (NoSuchFieldException ignore) {
                //ignore
            }
        }

        return false;
    }

    /**
     * Determine whether the method is declared public static
     *
     * @param m the method to be tested
     *
     * @return True if the method is declared public static
     */
    public static boolean isPublicStatic(@Nonnull Method m) {
        requireNonNull(m, "Argument 'method' must not be null");
        final int modifiers = m.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
    }

    /**
     * Determine whether the field is declared public static
     *
     * @param f the field to be tested
     *
     * @return True if the field is declared public static
     */
    public static boolean isPublicStatic(@Nonnull Field f) {
        requireNonNull(f, "Argument 'field' must not be null");
        final int modifiers = f.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
    }

    /**
     * Calculate the name for a getter method to retrieve the specified property
     *
     * @param propertyName the name of the property
     *
     * @return The name for the getter method for this property, if it were to exist, i.e. getConstraints
     */
    @Nonnull
    public static String getGetterName(@Nonnull String propertyName) {
        requireNonBlank(propertyName, ERROR_PROPERTY_NAME_BLANK);
        return PROPERTY_GET_PREFIX + Character.toUpperCase(propertyName.charAt(0))
            + propertyName.substring(1);
    }

    /**
     * <p>Get a static property value, which has a public static getter or is just a public static field.</p>
     *
     * @param clazz The class to check for static property
     * @param name  The property name
     *
     * @return The value if there is one, or null if unset OR there is no such property
     */
    @Nullable
    public static Object getStaticPropertyValue(@Nonnull Class<?> clazz, @Nonnull String name) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);
        Method getter = findDeclaredMethod(clazz, getGetterName(name), null);
        try {
            if (getter != null) {
                return getter.invoke(null, (Object[]) null);
            } else {
                Field f = clazz.getDeclaredField(name);
                if (f != null) {
                    return f.get(null);
                }
            }
        } catch (Exception ignore) {
            //ignore
        }
        return null;
    }

    /**
     * <p>Looks for a property of the reference instance with a given name.</p>
     * <p>If found its value is returned. We follow the Java bean conventions with augmentation for groovy support
     * and static fields/properties. We will therefore match, in this order:
     * </p>
     * <ol>
     * <li>Standard public bean property (with getter or just public field, using normal introspection)
     * <li>Public static property with getter method
     * <li>Public static field
     * </ol>
     *
     * @return property value or null if no property found
     */
    @Nullable
    public static Object getPropertyOrStaticPropertyOrFieldValue(@Nonnull Object obj, @Nonnull String name) {
        requireNonNull(obj, ERROR_OBJECT_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);
        if (isReadable(obj, name)) {
            try {
                return getProperty(obj, name);
            } catch (Exception e) {
                throw new PropertyException(obj, name);
            }
        } else {
            // Look for public fields
            if (isPublicField(obj, name)) {
                return getFieldValue(obj, name);
            }

            // Look for statics
            Class<?> clazz = obj.getClass();
            if (isStaticProperty(clazz, name)) {
                return getStaticPropertyValue(clazz, name);
            } else {
                return null;
            }
        }
    }

    /**
     * Get the value of a declared field on an object
     *
     * @param obj  the instance that owns the field
     * @param name the name of the file to lookup
     *
     * @return The object value or null if there is no such field or access problems
     */
    @Nullable
    public static Object getFieldValue(@Nonnull Object obj, @Nonnull String name) {
        requireNonNull(obj, ERROR_OBJECT_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        Class<?> clazz = obj.getClass();
        Class<?> c = clazz;
        while (c != null && !c.equals(Object.class)) {
            Field field = null;
            boolean wasAccessible = false;
            try {
                field = c.getDeclaredField(name);
                wasAccessible = field.isAccessible();
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception e) {
                // ignore
            } finally {
                if (field != null) {
                    field.setAccessible(wasAccessible);
                }
            }
            c = c.getSuperclass();
        }

        return null;
    }

    /**
     * Get the a declared field on an object
     *
     * @param obj  the instance that owns the field
     * @param name the name of the file to lookup
     *
     * @return The field or null if there is no such field or access problems
     */
    @Nullable
    public static Field getField(@Nonnull Object obj, @Nonnull String name) {
        requireNonNull(obj, ERROR_OBJECT_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);
        return getField(obj.getClass(), name);
    }

    /**
     * Get the a declared field on a class
     *
     * @param clazz the clazz that owns the field
     * @param name  the name of the file to lookup
     *
     * @return The field or null if there is no such field or access problems
     */
    @Nullable
    public static Field getField(@Nonnull Class<?> clazz, @Nonnull String name) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        Class<?> c = clazz;
        while (c != null && !c.equals(Object.class)) {
            try {
                return c.getDeclaredField(name);
            } catch (Exception e) {
                // ignore
            }
            c = c.getSuperclass();
        }
        return null;
    }

    /**
     * Returns an array of {@code Field} objects reflecting all the fields
     * declared by the class and its hierarchy, represented by this
     * {@code Class} object. This includes public, protected, default
     * (package) access, and private fields, but excludes inherited fields.
     * <p>
     * <p> The elements in the returned array are not sorted and are not in any
     * particular order.
     *
     * @param clazz the clazz that will be queried.
     *
     * @return the array of {@code Field} objects representing all the
     * declared fields of this class and its hierarchy
     */
    public static Field[] getAllDeclaredFields(@Nonnull Class<?> clazz) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);

        List<Field> fields = new ArrayList<>();

        Class<?> c = clazz;
        while (c != null && !c.equals(Object.class)) {
            Field[] declaredFields = c.getDeclaredFields();
            if (declaredFields != null && declaredFields.length > 0) {
                fields.addAll(Arrays.asList(declaredFields));
            }
            c = c.getSuperclass();
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Work out if the specified object has a public field with the name supplied.
     *
     * @param obj  the instance that owns the field
     * @param name the name of the file to lookup
     *
     * @return True if a public field with the name exists
     */
    public static boolean isPublicField(@Nonnull Object obj, @Nonnull String name) {
        requireNonNull(obj, ERROR_OBJECT_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);
        Class<?> clazz = obj.getClass();
        Field f;
        try {
            f = clazz.getDeclaredField(name);
            return Modifier.isPublic(f.getModifiers());
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Checks whether the specified property is inherited from a super class
     *
     * @param clz          The class to check
     * @param propertyName The property name
     *
     * @return True if the property is inherited
     */
    public static boolean isPropertyInherited(@Nullable Class<?> clz, @Nonnull String propertyName) {
        if (clz == null) { return false; }
        requireNonBlank(propertyName, ERROR_PROPERTY_NAME_BLANK);
        Class<?> superClass = clz.getSuperclass();

        PropertyDescriptor pd;
        try {
            pd = getPropertyDescriptor(superClass, propertyName);
        } catch (Exception e) {
            throw new PropertyException(superClass, propertyName, e);
        }
        return pd != null && pd.getReadMethod() != null;
    }

    /**
     * Creates a concrete collection for the supplied interface
     *
     * @param interfaceType The interface
     *
     * @return ArrayList for List, TreeSet for SortedSet, HashSet for Set etc.
     */
    @Nonnull
    public static Collection<?> createConcreteCollection(@Nonnull Class<?> interfaceType) {
        requireNonNull(interfaceType, ERROR_TYPE_NULL);
        Collection<?> elements;
        if (interfaceType.equals(List.class)) {
            elements = new ArrayList<>();
        } else if (interfaceType.equals(SortedSet.class)) {
            elements = new TreeSet<>();
        } else {
            elements = new LinkedHashSet<>();
        }
        return elements;
    }

    /**
     * Retrieves the name of a setter for the specified property name
     *
     * @param propertyName The property name
     *
     * @return The setter equivalent
     */
    @Nonnull
    public static String getSetterName(@Nonnull String propertyName) {
        requireNonBlank(propertyName, ERROR_PROPERTY_NAME_BLANK);
        return PROPERTY_SET_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    /**
     * Returns true if the name of the method specified and the number of arguments make it a javabean property
     *
     * @param name True if its a Javabean property
     * @param args The arguments
     *
     * @return True if it is a javabean property method
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean isGetter(@Nullable String name, @Nullable Class[] args) {
        if (GriffonNameUtils.isBlank(name) || args == null) { return false; }
        if (args.length != 0) { return false; }

        if (name.startsWith(PROPERTY_GET_PREFIX)) {
            name = name.substring(3);
            return name.length() > 0 && Character.isUpperCase(name.charAt(0));
        } else if (name.startsWith(PROPERTY_IS_PREFIX)) {
            name = name.substring(2);
            return name.length() > 0 && Character.isUpperCase(name.charAt(0));
        }
        return false;
    }

    /**
     * Returns a property name equivalent for the given getter name or null if it is not a getter
     *
     * @param getterName The getter name
     *
     * @return The property name equivalent
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static String getPropertyForGetter(@Nullable String getterName) {
        if (GriffonNameUtils.isBlank(getterName)) { return null; }

        if (getterName.startsWith(PROPERTY_GET_PREFIX)) {
            String prop = getterName.substring(3);
            return convertPropertyName(prop);
        } else if (getterName.startsWith(PROPERTY_IS_PREFIX)) {
            String prop = getterName.substring(2);
            return convertPropertyName(prop);
        }
        return null;
    }

    @Nonnull
    private static String convertPropertyName(@Nonnull String prop) {
        if (Character.isUpperCase(prop.charAt(0)) && Character.isUpperCase(prop.charAt(1))) {
            return prop;
        } else if (Character.isDigit(prop.charAt(0))) {
            return prop;
        } else {
            return Character.toLowerCase(prop.charAt(0)) + prop.substring(1);
        }
    }

    /**
     * Returns a property name equivalent for the given setter name or null if it is not a getter
     *
     * @param setterName The setter name
     *
     * @return The property name equivalent
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static String getPropertyForSetter(@Nullable String setterName) {
        if (GriffonNameUtils.isBlank(setterName)) { return null; }

        if (setterName.startsWith(PROPERTY_SET_PREFIX)) {
            String prop = setterName.substring(3);
            return convertPropertyName(prop);
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isSetter(@Nullable String name, @Nullable Class[] args) {
        if (GriffonNameUtils.isBlank(name) || args == null) { return false; }

        if (name.startsWith(PROPERTY_SET_PREFIX)) {
            if (args.length != 1) { return false; }
            name = name.substring(3);
            return name.length() > 0 && Character.isUpperCase(name.charAt(0));
        }

        return false;
    }

    /**
     * Returns true if the specified clazz parameter is either the same as, or is a superclass or super interface
     * of, the specified type parameter. Converts primitive types to compatible class automatically.
     *
     * @param clazz
     * @param type
     *
     * @return True if the class is a taglib
     *
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
     *
     * @return A boolean value which will be false if the map is null, the map doesn't contain the key or the value is false
     */
    public static boolean getBooleanFromMap(@Nullable String key, @Nullable Map<String, Object> map) {
        if (map == null) { return false; }
        if (map.containsKey(key)) {
            Object o = map.get(key);
            if (o == null) { return false; } else if (o instanceof Boolean) {
                return (Boolean) o;
            } else {
                return Boolean.valueOf(o.toString());
            }
        }
        return false;
    }

    /**
     * Returns whether the specified class is either within one of the specified packages or
     * within a subpackage of one of the packages
     *
     * @param clazz       The class
     * @param packageList The list of packages
     *
     * @return True if it is within the list of specified packages
     */
    public static boolean isClassBelowPackage(@Nonnull Class<?> clazz, @Nonnull List<?> packageList) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonNull(packageList, "Argument 'packageList' must not be null");
        String classPackage = clazz.getPackage().getName();
        for (Object packageName : packageList) {
            if (packageName != null && classPackage.startsWith(packageName.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets or updates an object's properties.
     * <p>
     * This method will attempt setting a property using a matching
     * {@code PropertyDescriptor}; next it will try direct field
     * access if the property was not found.
     *
     * @param bean       the target object on which properties will be set
     * @param properties names and values for properties to be set
     *
     * @throws PropertyException if a property could be found
     * @since 2.1.0
     */
    public static void setPropertiesOrFields(@Nonnull Object bean, @Nonnull Map<String, Object> properties) throws PropertyException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(properties, ERROR_PROPERTIES_NULL);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            setPropertyOrFieldValue(bean, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets or updates an object's properties.
     * <p>
     * This method will attempt setting a property using a matching
     * {@code PropertyDescriptor}; next it will try direct field
     * access if the property was not found.
     *
     * @param bean       the target object on which properties will be set
     * @param properties names and values for properties to be set
     *
     * @since 2.1.0
     */
    public static void setPropertiesOrFieldsNoException(@Nonnull Object bean, @Nonnull Map<String, Object> properties) {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(properties, ERROR_PROPERTIES_NULL);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            try {
                setPropertyOrFieldValue(bean, entry.getKey(), entry.getValue());
            } catch (PropertyException pe) {
                // ignore
            }
        }
    }

    /**
     * Sets or updates an object's property.
     * <p>
     * This method will attempt setting a property using a matching
     * {@code PropertyDescriptor}; next it will try direct field
     * access if the property was not found.
     *
     * @param bean  the target object on which the property will be set
     * @param name  the name of the property to set
     * @param value the value to be set
     *
     * @throws PropertyException if the property could not be found
     * @since 2.1.0
     */
    public static void setPropertyOrFieldValue(@Nonnull Object bean, @Nonnull String name, @Nullable Object value) throws PropertyException {
        try {
            setPropertyValue(bean, name, value);
        } catch (PropertyException pe) {
            try {
                setFieldValue(bean, name, value);
            } catch (FieldException fe) {
                throw pe;
            }
        }
    }

    /**
     * Sets or updates an object's property.
     * <p>
     * This method will attempt setting a property using a matching
     * {@code PropertyDescriptor}; next it will try direct field
     * access if the property was not found.
     *
     * @param bean  the target object on which the property will be set
     * @param name  the name of the property to set
     * @param value the value to be set
     *
     * @throws PropertyException if the property could not be found
     * @since 2.4.0
     */
    public static void setPropertyOrFieldValueNoException(@Nonnull Object bean, @Nonnull String name, @Nullable Object value) {
        try {
            setPropertyOrFieldValue(bean, name, value);
        } catch (PropertyException pe) {
            // ignore
        }
    }

    /**
     * Sets or updates an object's properties.
     * <p>
     * This method will attempt setting a property using direct field
     * access; next it will try a {@code PropertyDescriptor} if a
     * matching field name was not found.
     *
     * @param bean       the target object on which properties will be set
     * @param properties names and values for properties to be set
     *
     * @throws FieldException if the field could not be found
     * @since 2.1.0
     */
    public static void setFieldsOrProperties(@Nonnull Object bean, @Nonnull Map<String, Object> properties) throws FieldException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(properties, ERROR_PROPERTIES_NULL);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            setFieldOrPropertyValue(bean, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets or updates an object's properties.
     * <p>
     * This method will attempt setting a property using direct field
     * access; next it will try a {@code PropertyDescriptor} if a
     * matching field name was not found.
     *
     * @param bean       the target object on which properties will be set
     * @param properties names and values for properties to be set
     *
     * @since 2.1.0
     */
    public static void setFieldsOrPropertiesNoException(@Nonnull Object bean, @Nonnull Map<String, Object> properties) {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(properties, ERROR_PROPERTIES_NULL);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            try {
                setFieldOrPropertyValue(bean, entry.getKey(), entry.getValue());
            } catch (FieldException pe) {
                // ignore
            }
        }
    }

    /**
     * Sets or updates an object's property.
     * <p>
     * This method will attempt setting a property using direct field
     * access; next it will try a {@code PropertyDescriptor} if a
     * matching field name was not found.
     *
     * @param bean  the target object on which the property will be set
     * @param name  the name of the property to set
     * @param value the value to be set
     *
     * @throws FieldException if the property could not be found
     * @since 2.1.0
     */
    public static void setFieldOrPropertyValue(@Nonnull Object bean, @Nonnull String name, @Nullable Object value) throws FieldException {
        try {
            setFieldValue(bean, name, value);
        } catch (FieldException fe) {
            try {
                setPropertyValue(bean, name, value);
            } catch (PropertyException pe) {
                throw fe;
            }
        }
    }

    /**
     * Sets or updates field values on an object.
     *
     * @param bean   the target object on which field values will be set
     * @param fields names and values of fields to be set
     *
     * @throws FieldException if a field could not be found
     * @since 2.1.0
     */
    public static void setFields(@Nonnull Object bean, @Nonnull Map<String, Object> fields) throws FieldException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(fields, ERROR_FIELDS_NULL);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            setFieldValue(bean, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets or updates field values on an object.
     *
     * @param bean   the target object on which field values will be set
     * @param fields names and values of fields to be set
     *
     * @since 2.1.0
     */
    public static void setFieldsNoException(@Nonnull Object bean, @Nonnull Map<String, Object> fields) {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(fields, ERROR_FIELDS_NULL);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            try {
                setFieldValue(bean, entry.getKey(), entry.getValue());
            } catch (FieldException e) {
                // ignore
            }
        }
    }

    /**
     * Sets or updates an object's field.
     *
     * @param bean  the target object on which the field will be set
     * @param name  the name of the field to set
     * @param value the value to be set
     *
     * @throws FieldException if the field could not be found
     * @since 2.1.0
     */
    public static void setFieldValue(@Nonnull Object bean, @Nonnull String name, @Nullable Object value) throws FieldException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);
        try {
            setField(bean, name, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new FieldException(bean, name, value, e);
        }
    }

    /**
     * Sets or updates an object's field.
     *
     * @param bean  the target object on which the field will be set
     * @param name  the name of the field to set
     * @param value the value to be set
     *
     * @throws FieldException if the field could not be found
     * @since 2.4.0
     */
    public static void setFieldValueNoException(@Nonnull Object bean, @Nonnull String name, @Nullable Object value) {
        try {
            setFieldValue(bean, name, value);
        } catch (FieldException e) {
            // ignore
        }
    }

    /**
     * Sets or updates properties on an object.
     *
     * @param bean       the target object on which properties will be set
     * @param properties names and values of properties to be set
     *
     * @throws PropertyException if a property could not be found
     */
    public static void setProperties(@Nonnull Object bean, @Nonnull Map<String, Object> properties) throws PropertyException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(properties, ERROR_PROPERTIES_NULL);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            setPropertyValue(bean, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets or updates properties on an object.
     *
     * @param bean       the target object on which properties will be set
     * @param properties names and values of properties to be set
     */
    public static void setPropertiesNoException(@Nonnull Object bean, @Nonnull Map<String, Object> properties) {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonNull(properties, ERROR_PROPERTIES_NULL);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            try {
                setPropertyValue(bean, entry.getKey(), entry.getValue());
            } catch (PropertyException e) {
                // ignore
            }
        }
    }

    /**
     * /**
     * Sets or updates a property on an object.
     *
     * @param bean  the target object on which the property will be set
     * @param name  the name of the property to set
     * @param value the value to be set
     *
     * @throws PropertyException if the property could not be found
     */
    public static void setPropertyValue(@Nonnull Object bean, @Nonnull String name, @Nullable Object value) throws PropertyException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);
        try {
            setProperty(bean, name, value);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new PropertyException(bean, name, value, e);
        } catch (InvocationTargetException e) {
            throw new PropertyException(bean, name, value, e.getTargetException());
        }
    }

    /**
     * Returns the value of a property.
     *
     * @param bean the owner of the property
     * @param name the name of the property to retrieve
     *
     * @return the value read from the matching property
     *
     * @throws PropertyException if the property could not be found
     */
    @Nullable
    public static Object getPropertyValue(@Nonnull Object bean, @Nonnull String name) throws PropertyException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);
        try {
            return getProperty(bean, name);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new PropertyException(bean, name, e);
        } catch (InvocationTargetException e) {
            throw new PropertyException(bean, name, e.getTargetException());
        }
    }

    // -- The following methods and properties were copied from commons-beanutils

    private static final Map<String, PropertyDescriptor[]> descriptorsCache = new LinkedHashMap<>();

    /**
     * <p>Retrieve the property descriptor for the specified property of the
     * specified bean, or return <code>null</code> if there is no such
     * descriptor.</p>
     * This method does not resolve index, nested nor mapped properties.<p>
     *
     * @param bean Bean for which a property descriptor is requested
     * @param name name of the property for which a property descriptor
     *             is requested
     *
     * @return the property descriptor or null if the bean does not have
     * a property that matches the specified name.
     *
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Nullable
    public static PropertyDescriptor getPropertyDescriptor(@Nonnull Object bean,
                                                           @Nonnull String name)
        throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        return getPropertyDescriptor(bean instanceof Class ? (Class<?>) bean : bean.getClass(), name);
    }

    /**
     * <p>Retrieve the property descriptor for the specified property of the
     * specified class, or return <code>null</code> if there is no such
     * descriptor.</p>
     * This method does not resolve index, nested nor mapped properties.<p>
     *
     * @param clazz class for which a property descriptor is requested
     * @param name  name of the property for which a property descriptor
     *              is requested
     *
     * @return the property descriptor or null if the bean does not have
     * a property that matches the specified name.
     *
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Nullable
    public static PropertyDescriptor getPropertyDescriptor(@Nonnull Class<?> clazz,
                                                           @Nonnull String name) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        PropertyDescriptor[] descriptors = getPropertyDescriptors(clazz);
        for (PropertyDescriptor descriptor : descriptors) {
            if (name.equals(descriptor.getName())) {
                return (descriptor);
            }
        }

        return null;
    }

    /**
     * <p>Retrieve the property descriptors for the specified class,
     * introspecting and caching them the first time a particular bean class
     * is encountered.</p>
     *
     * @param beanClass Bean class for which property descriptors are requested
     *
     * @return the property descriptors
     *
     * @throws IllegalArgumentException if <code>beanClass</code> is null
     */
    @Nonnull
    public static PropertyDescriptor[] getPropertyDescriptors(@Nonnull Class<?> beanClass) {
        requireNonNull(beanClass, ERROR_CLAZZ_NULL);

        // Look up any cached descriptors for this bean class
        PropertyDescriptor[] descriptors;
        descriptors = descriptorsCache.get(beanClass.getName());
        if (descriptors != null) {
            return descriptors;
        }

        // Introspect the bean and cache the generated descriptors
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            return (new PropertyDescriptor[0]);
        }
        descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[0];
        }

        descriptorsCache.put(beanClass.getName(), descriptors);
        return descriptors;
    }

    /**
     * <p>Return an accessible property getter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * @param descriptor Property descriptor to return a getter for
     *
     * @return The read method
     */
    @Nullable
    public static Method getReadMethod(@Nonnull PropertyDescriptor descriptor) {
        requireNonNull(descriptor, ERROR_DESCRIPTOR_NULL);
        return (MethodUtils.getAccessibleMethod(descriptor.getReadMethod()));
    }

    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a readable property on the specified bean; otherwise, return
     * <code>false</code>.
     *
     * @param bean Bean to be examined
     * @param name Property name to be evaluated
     *
     * @return <code>true</code> if the property is readable,
     * otherwise <code>false</code>
     *
     * @throws IllegalArgumentException if <code>bean</code>
     *                                  or <code>name</code> is <code>null</code>
     * @since BeanUtils 1.6
     */
    public static boolean isReadable(@Nonnull Object bean, @Nonnull String name) {
        // Validate method parameters
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        try {
            PropertyDescriptor desc = getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method readMethod = getReadMethod(bean.getClass(), desc);
                if (readMethod != null) {
                    readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
                }
                return (readMethod != null);
            } else {
                return false;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * @param descriptor Property descriptor to return a setter for
     *
     * @return The write method
     */
    @Nullable
    public static Method getWriteMethod(@Nonnull PropertyDescriptor descriptor) {
        requireNonNull(descriptor, ERROR_DESCRIPTOR_NULL);
        return (MethodUtils.getAccessibleMethod(descriptor.getWriteMethod()));
    }

    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a writable property on the specified bean; otherwise, return
     * <code>false</code>.
     *
     * @param bean Bean to be examined
     * @param name Property name to be evaluated
     *
     * @return <code>true</code> if the property is writable,
     * otherwise <code>false</code>
     *
     * @throws IllegalArgumentException if <code>bean</code>
     *                                  or <code>name</code> is <code>null</code>
     */
    public static boolean isWritable(@Nonnull Object bean, @Nonnull String name) {
        // Validate method parameters
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        try {
            PropertyDescriptor desc = getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method writeMethod = getWriteMethod(bean.getClass(), desc);
                if (writeMethod != null) {
                    writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
                }
                return (writeMethod != null);
            } else {
                return false;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Sets the value of the specified field of the specified bean.
     *
     * @param bean  Bean whose field is to be mutated
     * @param name  Name of the field to be mutated
     * @param value The value to be set on the property
     *
     * @throws IllegalAccessException   if the caller does not have
     *                                  access to the field
     * @throws IllegalArgumentException if <code>bean</code> or
     *                                  <code>name</code> is null
     * @throws NoSuchFieldException     if the named field cannot be found
     * @throws FieldException           if the field cannot be set
     * @since 2.1.0
     */
    public static void setField(@Nonnull Object bean, @Nonnull String name, @Nullable Object value)
        throws NoSuchFieldException, IllegalAccessException, FieldException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        Class<?> declaringClass = bean.getClass();
        while (declaringClass != null) {
            try {
                Field field = declaringClass.getDeclaredField(name);

                // type conversion needed?
                Class<?> propertyType = field.getType();
                if (value != null && !propertyType.isAssignableFrom(value.getClass())) {
                    value = TypeUtils.convertValue(propertyType, value);
                }

                field.setAccessible(true);
                try {
                    field.set(bean, value);
                    return;
                } catch (IllegalArgumentException iae) {
                    throw new FieldException(bean, name, value, iae);
                }
            } catch (NoSuchFieldException nsfe) {
                declaringClass = declaringClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    /**
     * Sets the value of the specified property of the specified bean.
     *
     * @param bean  Bean whose property is to be mutated
     * @param name  Name of the property to be mutated
     * @param value The value to be set on the property
     *
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     * @throws PropertyException         if the property cannot be set
     */
    public static void setProperty(@Nonnull Object bean, @Nonnull String name, @Nullable Object value)
        throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException, PropertyException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        // Retrieve the property setter method for the specified property
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                name + "' on class '" + bean.getClass() + "'");
        }
        Method writeMethod = getWriteMethod(bean.getClass(), descriptor);
        if (writeMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                "' has no setter method in class '" + bean.getClass() + "'");
        }

        // type conversion needed?
        Class<?> propertyType = descriptor.getPropertyType();
        if (value != null && !propertyType.isAssignableFrom(value.getClass())) {
            value = TypeUtils.convertValue(propertyType, value);
        }

        // Call the property setter
        try {
            writeMethod.invoke(bean, value);
        } catch (IllegalArgumentException iae) {
            throw new PropertyException(bean, name, value, iae);
        }
    }

    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *             to be extracted
     *
     * @return the property value
     *
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    @Nullable
    public static Object getProperty(@Nonnull Object bean, @Nonnull String name)
        throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        requireNonNull(bean, ERROR_BEAN_NULL);
        requireNonBlank(name, ERROR_NAME_BLANK);

        // Retrieve the property getter method for the specified property
        PropertyDescriptor descriptor = getPropertyDescriptor(bean, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("Unknown property '" +
                name + "' on class '" + bean.getClass() + "'");
        }
        Method readMethod = getReadMethod(bean.getClass(), descriptor);
        if (readMethod == null) {
            throw new NoSuchMethodException("Property '" + name +
                "' has no getter method in class '" + bean.getClass() + "'");
        }

        // Call the property getter and return the value
        return readMethod.invoke(bean, EMPTY_OBJECT_ARRAY);
    }

    /**
     * <p>Return an accessible property getter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * @param clazz      The class of the read method will be invoked on
     * @param descriptor Property descriptor to return a getter for
     *
     * @return The read method
     */
    @Nullable
    public static Method getReadMethod(@Nonnull Class<?> clazz, @Nonnull PropertyDescriptor descriptor) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonNull(descriptor, ERROR_DESCRIPTOR_NULL);
        return (MethodUtils.getAccessibleMethod(clazz, descriptor.getReadMethod()));
    }

    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * @param clazz      The class of the write method will be invoked on
     * @param descriptor Property descriptor to return a setter for
     *
     * @return The write method
     */
    @Nullable
    public static Method getWriteMethod(@Nonnull Class<?> clazz, @Nonnull PropertyDescriptor descriptor) {
        requireNonNull(clazz, ERROR_CLAZZ_NULL);
        requireNonNull(descriptor, ERROR_DESCRIPTOR_NULL);
        return (MethodUtils.getAccessibleMethod(clazz, descriptor.getWriteMethod()));
    }

    // -- The following methods and properties were copied from commons-lang

    /**
     * <p>Validate that the argument condition is <code>true</code>; otherwise
     * throwing an exception with the specified message. This method is useful when
     * validating according to an arbitrary boolean expression, such as validating a
     * primitive number or using your own custom validation expression.</p>
     * <p>
     * <pre>
     * isTrue( (i > 0), "The value must be greater than zero");
     * isTrue( myObject.isOk(), "The object is not OK");
     * </pre>
     *
     * @param expression the boolean expression to check
     * @param message    the exception message if invalid
     *
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    @Nullable
    public static Object invokeInstanceMethod(@Nonnull Object object, @Nonnull String methodName) {
        return invokeInstanceMethod(object, methodName, EMPTY_ARGS);
    }

    @Nullable
    public static Object invokeInstanceMethod(@Nonnull Object object, @Nonnull String methodName, Object arg) {
        return invokeInstanceMethod(object, methodName, new Object[]{arg});
    }

    @Nullable
    public static Object invokeInstanceMethod(@Nonnull Object object, @Nonnull String methodName, Object... args) {
        requireNonNull(object, ERROR_OBJECT_NULL);
        requireNonBlank(methodName, ERROR_METHOD_NAME_BLANK);
        try {
            return invokeMethod(object, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e.getTargetException());
        }
    }

    @Nullable
    public static Object invokeExactInstanceMethod(@Nonnull Object object, @Nonnull String methodName) {
        return invokeExactInstanceMethod(object, methodName, EMPTY_ARGS);
    }

    @Nullable
    public static Object invokeExactInstanceMethod(@Nonnull Object object, @Nonnull String methodName, Object arg) {
        return invokeExactInstanceMethod(object, methodName, new Object[]{arg});
    }

    @Nullable
    public static Object invokeExactInstanceMethod(@Nonnull Object object, @Nonnull String methodName, Object... args) {
        requireNonNull(object, ERROR_OBJECT_NULL);
        requireNonBlank(methodName, ERROR_METHOD_NAME_BLANK);
        try {
            return invokeExactMethod(object, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e.getTargetException());
        }
    }

    @Nullable
    public static Object invokeStaticMethod(@Nonnull Class<?> type, @Nonnull String methodName) {
        return invokeStaticMethod(type, methodName, EMPTY_ARGS);
    }

    @Nullable
    public static Object invokeStaticMethod(@Nonnull Class<?> type, @Nonnull String methodName, Object arg) {
        return invokeStaticMethod(type, methodName, new Object[]{arg});
    }

    @Nullable
    public static Object invokeStaticMethod(@Nonnull Class<?> type, @Nonnull String methodName, Object... args) {
        requireNonNull(type, ERROR_TYPE_NULL);
        requireNonBlank(methodName, ERROR_METHOD_NAME_BLANK);
        try {
            return MethodUtils.invokeStaticMethod(type, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e.getTargetException());
        }
    }

    @Nullable
    public static Object invokeExactStaticMethod(@Nonnull Class<?> type, @Nonnull String methodName) {
        return invokeExactStaticMethod(type, methodName, EMPTY_ARGS);
    }

    @Nullable
    public static Object invokeExactStaticMethod(@Nonnull Class<?> type, @Nonnull String methodName, Object arg) {
        return invokeExactStaticMethod(type, methodName, new Object[]{arg});
    }

    @Nullable
    public static Object invokeExactStaticMethod(@Nonnull Class<?> type, @Nonnull String methodName, Object... args) {
        requireNonNull(type, ERROR_TYPE_NULL);
        requireNonBlank(methodName, ERROR_METHOD_NAME_BLANK);
        try {
            return MethodUtils.invokeExactStaticMethod(type, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e.getTargetException());
        }
    }

    public static boolean hasMethodAnnotatedwith(@Nonnull final Object instance, @Nonnull final Class<? extends Annotation> annotation) {
        Class<?> klass = instance.getClass();
        while (klass != null) {
            for (Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation) &&
                    method.getParameterTypes().length == 0) {
                    return true;
                }
            }

            klass = klass.getSuperclass();
        }

        return false;
    }

    public static void invokeAnnotatedMethod(@Nonnull final Object instance, @Nonnull final Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        Class<?> klass = instance.getClass();
        while (klass != null) {
            boolean found = false;
            for (Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation) &&
                    method.getParameterTypes().length == 0) {
                    if (found) {
                        throw new InstanceMethodInvocationException(instance, method, buildCause(instance.getClass(), method, methods, annotation));
                    }
                    methods.add(method);
                    found = true;
                }
            }

            klass = klass.getSuperclass();
        }

        for (final Method method : methods) {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                boolean wasAccessible = method.isAccessible();
                try {
                    method.setAccessible(true);
                    return method.invoke(instance);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new InstanceMethodInvocationException(instance, method.getName(), null, e);
                } catch (InvocationTargetException e) {
                    throw new InstanceMethodInvocationException(instance, method.getName(), null, e.getTargetException());
                } finally {
                    method.setAccessible(wasAccessible);
                }
            });
        }
    }

    @Nonnull
    private static Throwable buildCause(@Nonnull Class<?> clazz, @Nonnull Method method, @Nonnull List<Method> methods, @Nonnull final Class<? extends Annotation> annotation) {
        StringBuilder b = new StringBuilder("The following methods were found annotated with @" + annotation.getSimpleName() + " on ")
            .append(clazz);
        for (Method m : methods) {
            b.append("\n  ").append(m.toGenericString());
        }
        b.append("\n  ").append(method.toGenericString());
        return new IllegalStateException(b.toString());
    }

    private static final String EMPTY_STRING = "";

    /**
     * <p>The package separator character: <code>'&#x2e;' == {@value}</code>.</p>
     */
    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    /**
     * <p>The package separator String: <code>"&#x2e;"</code>.</p>
     */
    public static final String PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);

    /**
     * <p>The inner class separator character: <code>'$' == {@value}</code>.</p>
     */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    /**
     * <p>The inner class separator String: <code>"$"</code>.</p>
     */
    public static final String INNER_CLASS_SEPARATOR = String.valueOf(INNER_CLASS_SEPARATOR_CHAR);

    /**
     * Maps a primitive class name to its corresponding abbreviation used in array class names.
     */
    private static final Map<String, String> abbreviationMap = new HashMap<>();

    /**
     * Maps an abbreviation used in array class names to corresponding primitive class name.
     */
    private static final Map<String, String> reverseAbbreviationMap = new HashMap<>();

    /**
     * Add primitive type abbreviation to maps of abbreviations.
     *
     * @param primitive    Canonical name of primitive type
     * @param abbreviation Corresponding abbreviation of primitive type
     */
    private static void addAbbreviation(String primitive, String abbreviation) {
        abbreviationMap.put(primitive, abbreviation);
        reverseAbbreviationMap.put(abbreviation, primitive);
    }

    /*
     * Feed abbreviation maps
     */
    static {
        addAbbreviation("int", "I");
        addAbbreviation("boolean", "Z");
        addAbbreviation("float", "F");
        addAbbreviation("long", "J");
        addAbbreviation("short", "S");
        addAbbreviation("byte", "B");
        addAbbreviation("double", "D");
        addAbbreviation("char", "C");
    }

    // ----------------------------------------------------------------------

    /**
     * <p>Gets the class name minus the package name for an <code>Object</code>.</p>
     *
     * @param object      the class to get the short name for, may be null
     * @param valueIfNull the value to return if null
     *
     * @return the class name of the object without the package name, or the null value
     */
    @Nonnull
    public static String getShortClassName(@Nullable Object object, @Nonnull String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass());
    }

    /**
     * <p>Gets the class name minus the package name from a <code>Class</code>.</p>
     *
     * @param cls the class to get the short name for.
     *
     * @return the class name without the package name or an empty string
     */
    @Nonnull
    public static String getShortClassName(@Nullable Class<?> cls) {
        if (cls == null) {
            return EMPTY_STRING;
        }
        return getShortClassName(cls.getName());
    }

    /**
     * <p>Gets the class name minus the package name from a String.</p>
     * <p/>
     * <p>The string passed in is assumed to be a class name - it is not checked.</p>
     *
     * @param className the className to get the short name for
     *
     * @return the class name of the class without the package name or an empty string
     */
    @Nonnull
    public static String getShortClassName(@Nullable String className) {
        if (className == null) {
            return EMPTY_STRING;
        }
        if (className.length() == 0) {
            return EMPTY_STRING;
        }

        StringBuilder arrayPrefix = new StringBuilder();

        // Handle array encoding
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            // Strip Object type encoding
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }
        }

        if (reverseAbbreviationMap.containsKey(className)) {
            className = reverseAbbreviationMap.get(className);
        }

        int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        int innerIdx = className.indexOf(
            INNER_CLASS_SEPARATOR_CHAR, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(INNER_CLASS_SEPARATOR_CHAR, PACKAGE_SEPARATOR_CHAR);
        }
        return out + arrayPrefix;
    }

    // Package name
    // ----------------------------------------------------------------------

    /**
     * <p>Gets the package name of an <code>Object</code>.</p>
     *
     * @param object      the class to get the package name for, may be null
     * @param valueIfNull the value to return if null
     *
     * @return the package name of the object, or the null value
     */
    @Nonnull
    public static String getPackageName(@Nullable Object object, @Nonnull String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageName(object.getClass());
    }

    /**
     * <p>Gets the package name of a <code>Class</code>.</p>
     *
     * @param cls the class to get the package name for, may be <code>null</code>.
     *
     * @return the package name or an empty string
     */
    @Nonnull
    public static String getPackageName(@Nullable Class<?> cls) {
        if (cls == null) {
            return EMPTY_STRING;
        }
        return getPackageName(cls.getName());
    }

    /**
     * <p>Gets the package name from a <code>String</code>.</p>
     * <p/>
     * <p>The string passed in is assumed to be a class name - it is not checked.</p>
     * <p>If the class is unpackaged, return an empty string.</p>
     *
     * @param className the className to get the package name for, may be <code>null</code>
     *
     * @return the package name or an empty string
     */
    @Nonnull
    public static String getPackageName(@Nullable String className) {
        if (className == null || className.length() == 0) {
            return EMPTY_STRING;
        }

        // Strip array encoding
        while (className.charAt(0) == '[') {
            className = className.substring(1);
        }
        // Strip Object type encoding
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
        }

        int i = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (i == -1) {
            return EMPTY_STRING;
        }
        return className.substring(0, i);
    }

    /**
     * param instance array to the type array
     *
     * @param args the arguments
     *
     * @return the types of the arguments
     */
    @Nullable
    public static Class<?>[] convertToTypeArray(@Nullable Object[] args) {
        if (args == null) {
            return null;
        }
        int s = args.length;
        Class<?>[] ans = new Class<?>[s];
        for (int i = 0; i < s; i++) {
            Object o = args[i];
            ans[i] = o != null ? o.getClass() : null;
        }
        return ans;
    }
}
