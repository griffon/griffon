/* 
 * Copyright 2004-2014 the original author or authors.
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

import griffon.core.CallableWithArgs;
import griffon.core.event.Event;
import griffon.core.mvc.MVCCallable;
import griffon.core.mvc.MVCGroup;
import griffon.exceptions.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import static griffon.util.MethodUtils.invokeExactMethod;
import static griffon.util.MethodUtils.invokeMethod;

/**
 * Class containing utility methods for dealing with Griffon class artifacts.<p>
 * Contains utility methods copied from commons-lang and commons-beanutils in order
 * to reduce dependencies on external libraries.<p>
 * <p/>
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
    // private static final Set<MethodDescriptor> SERVICE_METHODS = new TreeSet<MethodDescriptor>();
    private static final Set<MethodDescriptor> THREADING_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> EVENT_PUBLISHER_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> OBSERVABLE_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> RESOURCE_HANDLER_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> MESSAGE_SOURCE_METHODS = new TreeSet<>();
    private static final Set<MethodDescriptor> RESOURCE_RESOLVER_METHODS = new TreeSet<>();

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

        /*
        for (Method method : GroovyObject.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method);
            if (!BASIC_METHODS.contains(md)) {
                BASIC_METHODS.add(md);
            }
        }
        for (Method method : GroovyObjectSupport.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method);
            if (!BASIC_METHODS.contains(md)) {
                BASIC_METHODS.add(md);
            }
        }
        */
        for (Method method : Object.class.getMethods()) {
            MethodDescriptor md = MethodDescriptor.forMethod(method);
            if (!BASIC_METHODS.contains(md)) {
                BASIC_METHODS.add(md);
            }
        }

        ARTIFACT_METHODS.add(new MethodDescriptor("newInstance", new Class<?>[]{Class.class, String.class}));
        ARTIFACT_METHODS.add(new MethodDescriptor("newInstance", new Class<?>[]{Object[].class}));
        ARTIFACT_METHODS.add(new MethodDescriptor("getApp"));
        ARTIFACT_METHODS.add(new MethodDescriptor("getLog"));
        ARTIFACT_METHODS.add(new MethodDescriptor("getGriffonClass"));
        // ARTIFACT_METHODS.add(new MethodDescriptor("griffonDestroy"));

        MVC_METHODS.add(new MethodDescriptor("getMvcGroup"));
        MVC_METHODS.add(new MethodDescriptor("setMvcGroup", new Class<?>[]{MVCGroup.class}));
        MVC_METHODS.add(new MethodDescriptor("mvcGroupInit", new Class<?>[]{Map.class}));
        MVC_METHODS.add(new MethodDescriptor("mvcGroupDestroy"));
        MVC_METHODS.add(new MethodDescriptor("buildMVCGroup", new Class<?>[]{String.class}));
        MVC_METHODS.add(new MethodDescriptor("buildMVCGroup", new Class<?>[]{String.class, Map.class}));
        MVC_METHODS.add(new MethodDescriptor("buildMVCGroup", new Class<?>[]{Map.class, String.class}));
        MVC_METHODS.add(new MethodDescriptor("buildMVCGroup", new Class<?>[]{String.class, String.class}));
        MVC_METHODS.add(new MethodDescriptor("buildMVCGroup", new Class<?>[]{String.class, String.class, Map.class}));
        MVC_METHODS.add(new MethodDescriptor("buildMVCGroup", new Class<?>[]{Map.class, String.class, String.class}));
        MVC_METHODS.add(new MethodDescriptor("createMVCGroup", new Class<?>[]{String.class}));
        MVC_METHODS.add(new MethodDescriptor("createMVCGroup", new Class<?>[]{String.class, Map.class}));
        MVC_METHODS.add(new MethodDescriptor("createMVCGroup", new Class<?>[]{Map.class, String.class}));
        MVC_METHODS.add(new MethodDescriptor("createMVCGroup", new Class<?>[]{String.class, String.class}));
        MVC_METHODS.add(new MethodDescriptor("createMVCGroup", new Class<?>[]{String.class, String.class, Map.class}));
        MVC_METHODS.add(new MethodDescriptor("createMVCGroup", new Class<?>[]{Map.class, String.class, String.class}));
        MVC_METHODS.add(new MethodDescriptor("destroyMVCGroup", new Class<?>[]{String.class}));
        MVC_METHODS.add(new MethodDescriptor("withMVCGroup", new Class<?>[]{String.class, MVCCallable.class}));
        MVC_METHODS.add(new MethodDescriptor("withMVCGroup", new Class<?>[]{String.class, Map.class, MVCCallable.class}));
        MVC_METHODS.add(new MethodDescriptor("withMVCGroup", new Class<?>[]{Map.class, String.class, MVCCallable.class}));
        MVC_METHODS.add(new MethodDescriptor("withMVCGroup", new Class<?>[]{String.class, String.class, MVCCallable.class}));
        MVC_METHODS.add(new MethodDescriptor("withMVCGroup", new Class<?>[]{String.class, String.class, Map.class, MVCCallable.class}));
        MVC_METHODS.add(new MethodDescriptor("withMVCGroup", new Class<?>[]{Map.class, String.class, String.class, MVCCallable.class}));

        // Special cases due to the usage of varargs
        MVC_METHODS.add(new MethodDescriptor("buildMVCGroup", new Class<?>[]{Object[].class}));
        MVC_METHODS.add(new MethodDescriptor("createMVCGroup", new Class<?>[]{Object[].class}));
        MVC_METHODS.add(new MethodDescriptor("withMVCGroup", new Class<?>[]{Object[].class}));

        MVC_METHODS.add(new MethodDescriptor("initUI"));
        // MVC_METHODS.add(new MethodDescriptor("getArtifactManager"));
        // MVC_METHODS.add(new MethodDescriptor("getAddonManager"));
        // MVC_METHODS.add(new MethodDescriptor("getMvcGroupManager"));
        // MVC_METHODS.add(new MethodDescriptor("setBuilder", new Class<?>[]{FactoryBuilderSupport.class}));
        MVC_METHODS.add(new MethodDescriptor("invokeAction", new Class<?>[]{String.class, Object[].class}));
        MVC_METHODS.add(new MethodDescriptor("invokeAction", new Class<?>[]{String.class, Object[].class}, Modifier.PUBLIC | Modifier.TRANSIENT));

        // SERVICE_METHODS.add(new MethodDescriptor("serviceInit"));
        // SERVICE_METHODS.add(new MethodDescriptor("serviceDestroy"));

        THREADING_METHODS.add(new MethodDescriptor("isUIThread"));
        THREADING_METHODS.add(new MethodDescriptor("runInsideUIAsync", new Class<?>[]{Runnable.class}));
        THREADING_METHODS.add(new MethodDescriptor("runInsideUISync", new Class<?>[]{Runnable.class}));
        THREADING_METHODS.add(new MethodDescriptor("runOutsideUI", new Class<?>[]{Runnable.class}));
        THREADING_METHODS.add(new MethodDescriptor("runFuture", new Class<?>[]{Callable.class}));
        THREADING_METHODS.add(new MethodDescriptor("runFuture", new Class<?>[]{ExecutorService.class, Callable.class}));
        THREADING_METHODS.add(new MethodDescriptor("edt", new Class<?>[]{Runnable.class}));
        THREADING_METHODS.add(new MethodDescriptor("doLater", new Class<?>[]{Runnable.class}));
        THREADING_METHODS.add(new MethodDescriptor("doOutside", new Class<?>[]{Runnable.class}));
        // Special case due to the usage of varargs
        THREADING_METHODS.add(new MethodDescriptor("runFuture", new Class<?>[]{Object[].class}));

        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("addEventListener", new Class<?>[]{Object.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("addEventListener", new Class<?>[]{String.class, CallableWithArgs.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("addEventListener", new Class<?>[]{Event.class, CallableWithArgs.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("removeEventListener", new Class<?>[]{Object.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("removeEventListener", new Class<?>[]{String.class, CallableWithArgs.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("removeEventListener", new Class<?>[]{Event.class, CallableWithArgs.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEvent", new Class<?>[]{String.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEvent", new Class<?>[]{String.class, List.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEvent", new Class<?>[]{Event.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEventAsync", new Class<?>[]{String.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEventAsync", new Class<?>[]{String.class, List.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEventAsync", new Class<?>[]{Event.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEventOutsideUI", new Class<?>[]{String.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEventOutsideUI", new Class<?>[]{String.class, List.class}));
        EVENT_PUBLISHER_METHODS.add(new MethodDescriptor("publishEventOutsideUI", new Class<?>[]{Event.class}));

        OBSERVABLE_METHODS.add(new MethodDescriptor("addPropertyChangeListener", new Class<?>[]{PropertyChangeListener.class}));
        OBSERVABLE_METHODS.add(new MethodDescriptor("addPropertyChangeListener", new Class<?>[]{String.class, PropertyChangeListener.class}));
        OBSERVABLE_METHODS.add(new MethodDescriptor("removePropertyChangeListener", new Class<?>[]{PropertyChangeListener.class}));
        OBSERVABLE_METHODS.add(new MethodDescriptor("removePropertyChangeListener", new Class<?>[]{String.class, PropertyChangeListener.class}));
        OBSERVABLE_METHODS.add(new MethodDescriptor("getPropertyChangeListeners", new Class<?>[0]));
        OBSERVABLE_METHODS.add(new MethodDescriptor("getPropertyChangeListeners", new Class<?>[]{String.class}));

        RESOURCE_HANDLER_METHODS.add(new MethodDescriptor("getResourceAsURL", new Class<?>[]{String.class}));
        RESOURCE_HANDLER_METHODS.add(new MethodDescriptor("getResourceAsStream", new Class<?>[]{String.class}));
        RESOURCE_HANDLER_METHODS.add(new MethodDescriptor("getResources", new Class<?>[]{String.class}));

        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("resolveMessageValue", new Class<?>[]{String.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Object[].class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Object[].class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, String.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Object[].class, String.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, String.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Object[].class, String.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, List.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, List.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, List.class, String.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, List.class, String.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Map.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Map.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Map.class, String.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("getMessage", new Class<?>[]{String.class, Map.class, String.class, Locale.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("formatMessage", new Class<?>[]{String.class, Object[].class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("formatMessage", new Class<?>[]{String.class, List.class}));
        MESSAGE_SOURCE_METHODS.add(new MethodDescriptor("formatMessage", new Class<?>[]{String.class, Map.class}));

        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResourceValue", new Class<?>[]{String.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Object[].class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Object[].class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Object.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Object[].class, Object.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Object.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Object[].class, Object.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, List.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, List.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, List.class, Object.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, List.class, Object.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Map.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Map.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Map.class, Object.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("resolveResource", new Class<?>[]{String.class, Map.class, Object.class, Locale.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("formatResource", new Class<?>[]{String.class, Object[].class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("formatResource", new Class<?>[]{String.class, List.class}));
        RESOURCE_RESOLVER_METHODS.add(new MethodDescriptor("formatResource", new Class<?>[]{String.class, Map.class}));
    }

    /**
     * Finds out if the given string represents the name of an
     * event handler by matching against the following pattern:
     * "^on[A-Z][\\w]*$"<p>
     * <p/>
     * <pre>
     * isEventHandler("onBootstrapEnd") = true
     * isEventHandler("mvcGroupInit")   = false
     * isEventHandler("online")         = false
     * </pre>
     *
     * @param name the name of a possible event handler
     * @return true if the name matches the given event handler
     *         pattern, false otherwise.
     */
    public static boolean isEventHandler(String name) {
        return !GriffonNameUtils.isBlank(name) && EVENT_HANDLER_PATTERN.matcher(name).matches();
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
     * @return true if the method name matches the given event handler
     *         pattern, false otherwise.
     */
    public static boolean isEventHandler(Method method) {
        return isEventHandler(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given Method represents an event handler
     * by matching its name against the following pattern:
     * "^on[A-Z][\\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isEventHandler(getMethod("onBootstrapEnd")) = true
     * isEventHandler(getMethod("mvcGroupInit"))   = false
     * isEventHandler(getMethod("online"))         = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method name matches the given event handler
     *         pattern, false otherwise.
     */
    /* public static boolean isEventHandler(MetaMethod method) {
        return isEventHandler(MethodDescriptor.forMethod(method));
    }*/

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
     * @return true if the method name matches the given event handler
     *         pattern, false otherwise.
     */
    public static boolean isEventHandler(MethodDescriptor method) {
        return !(method == null || method.getModifiers() - Modifier.PUBLIC != 0) &&
            EVENT_HANDLER_PATTERN.matcher(method.getName()).matches();
    }

    /**
     * Finds out if the given {@code Method} belongs either to the
     * {@code Object} class or the {@code GroovyObject} class.<p>
     *
     * @param method a Method reference
     * @return true if the method belongs to {@code Object} or
     *         {@code GroovyObject}, false otherwise.
     */
    public static boolean isBasicMethod(Method method) {
        return isBasicMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given string represents the name of a
     * contribution method by matching against the following pattern:
     * "^with[A-Z][a-z0-9_]*[\w]*$"<p>
     * <p/>
     * <pre>
     * isContributionMethod("withRest")     = true
     * isContributionMethod("withMVCGroup") = false
     * isContributionMethod("without")      = false
     * </pre>
     *
     * @param name the name of a possible contribution method
     * @return true if the name matches the given contribution method
     *         pattern, false otherwise.
     */
    public static boolean isContributionMethod(String name) {
        return !GriffonNameUtils.isBlank(name) && CONTRIBUTION_PATTERN.matcher(name).matches();
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
     * @return true if the method name matches the given contribution method
     *         pattern, false otherwise.
     */
    public static boolean isContributionMethod(Method method) {
        return isContributionMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given Method represents a contribution method
     * by matching its name against the following pattern:
     * "^with[A-Z][a-z0-9_]*[\w]*$"<p>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isContributionMethod(getMethod("withRest"))     = true
     * isContributionMethod(getMethod("withMVCGroup")) = false
     * isContributionMethod(getMethod("without"))      = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method name matches the given contribution method
     *         pattern, false otherwise.
     */
    /*public static boolean isContributionMethod(MetaMethod method) {
        return isContributionMethod(MethodDescriptor.forMethod(method));
    }*/

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
     * @return true if the method name matches the given contribution method
     *         pattern, false otherwise.
     */
    public static boolean isContributionMethod(MethodDescriptor method) {
        return !(method == null || method.getModifiers() - Modifier.PUBLIC != 0) &&
            CONTRIBUTION_PATTERN.matcher(method.getName()).matches();
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs either to the
     * {@code Object} class or the {@code GroovyObject} class.<p>
     *
     * @param method a MetaMethod reference
     * @return true if the method belongs to {@code Object} or
     *         {@code GroovyObject}, false otherwise.
     */
    /*public static boolean isBasicMethod(MetaMethod method) {
        return isBasicMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs either to the
     * {@code Object} class or the {@code GroovyObject} class.<p>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method belongs to {@code Object} or
     *         {@code GroovyObject}, false otherwise.
     */
    public static boolean isBasicMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) && BASIC_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} was injected by the Groovy
     * compiler.<p>
     * Performs a basic checks against the method's name, returning true
     * if the name starts with either "super$" or "this$".
     *
     * @param method a Method reference
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isGroovyInjectedMethod(Method method) {
        return isGroovyInjectedMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} was injected by the Groovy
     * compiler.<p>
     * Performs a basic checks against the method's name, returning true
     * if the name starts with either "super$" or "this$".
     *
     * @param method a MetaMethod reference
     * @return true if the method matches the given criteria, false otherwise.
     */
    /*public static boolean isGroovyInjectedMethod(MetaMethod method) {
        return isGroovyInjectedMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} was injected by the Groovy
     * compiler.<p>
     * Performs a basic checks against the method's name, returning true
     * if the name starts with either "super$" or "this$".
     *
     * @param method a MethodDescriptor reference
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isGroovyInjectedMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            (method.getName().startsWith("super$") || method.getName().startsWith("this$"));
    }

    /**
     * Finds out if the given {@code Method} is a getter method.
     * <p/>
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
    public static boolean isGetterMethod(Method method) {
        return isGetterMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} is a getter method.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
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
    /*public static boolean isGetterMethod(MetaMethod method) {
        return isGetterMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MetaMethod} is a getter method.
     * <p/>
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
    public static boolean isGetterMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            (GETTER_PATTERN_1.matcher(method.getName()).matches() || GETTER_PATTERN_2.matcher(method.getName()).matches());
    }

    /**
     * Finds out if the given {@code Method} is a setter method.
     * <p/>
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
    public static boolean isSetterMethod(Method method) {
        return isSetterMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} is a setter method.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isGetterMethod(getMethod("setFoo"))       = true
     * isGetterMethod(getMethod("setfoo"))       = false
     * isGetterMethod(getMethod("mvcGroupInit")) = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is a setter, false otherwise.
     */
    /*public static boolean isSetterMethod(MetaMethod method) {
        return isSetterMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} is a setter method.
     * <p/>
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
    public static boolean isSetterMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) && SETTER_PATTERN.matcher(method.getName()).matches();
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined Artifact methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isArtifactMethod(getMethod("newInstance"))    = true
     * isArtifactMethod(getMethod("griffonDestroy")) = false
     * isArtifactMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an Artifact method, false otherwise.
     */
    public static boolean isArtifactMethod(Method method) {
        return isArtifactMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined Artifact methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isArtifactMethod(getMethod("newInstance"))    = true
     * isArtifactMethod(getMethod("griffonDestroy")) = false
     * isArtifactMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an Artifact method, false otherwise.
     */
    /*public static boolean isArtifactMethod(MetaMethod method) {
        return isArtifactMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined Artifact methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isArtifactMethod(getMethod("newInstance"))    = true
     * isArtifactMethod(getMethod("griffonDestroy")) = false
     * isArtifactMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an Artifact method, false otherwise.
     */
    public static boolean isArtifactMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            ARTIFACT_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined MVC methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isMvcMethod(getMethod("mvcGroupInit"))    = true
     * isMvcMethod(getMethod("mvcGroupDestroy")) = true
     * isMvcMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an MVC method, false otherwise.
     */
    public static boolean isMvcMethod(Method method) {
        return isMvcMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined MVC methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isMvcMethod(getMethod("mvcGroupInit"))    = true
     * isMvcMethod(getMethod("mvcGroupDestroy")) = true
     * isMvcMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an MVC method, false otherwise.
     */
    /*public static boolean isMvcMethod(MetaMethod method) {
        return isMvcMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined MVC methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isMvcMethod(getMethod("mvcGroupInit"))    = true
     * isMvcMethod(getMethod("mvcGroupDestroy")) = true
     * isMvcMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an MVC method, false otherwise.
     */
    public static boolean isMvcMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            MVC_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined {@code GriffonService} methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isServiceMethod(getMethod("serviceInit"))    = true
     * isServiceMethod(getMethod("serviceDestroy")) = true
     * isServiceMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an {@code GriffonService} method, false otherwise.
     */
    /*
    public static boolean isServiceMethod(Method method) {
        return isServiceMethod(MethodDescriptor.forMethod(method));
    }
    */

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined {@code GriffonService} methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isServiceMethod(getMethod("serviceInit"))    = true
     * isServiceMethod(getMethod("serviceDestroy")) = true
     * isServiceMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an {@code GriffonService} method, false otherwise.
     */
    /*public static boolean isServiceMethod(MetaMethod method) {
        return isServiceMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined {@code GriffonService} methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isServiceMethod(getMethod("serviceInit"))    = true
     * isServiceMethod(getMethod("serviceDestroy")) = true
     * isServiceMethod(getMethod("foo"))            = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an {@code GriffonService} method, false otherwise.
     */
    /*
    public static boolean isServiceMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            SERVICE_METHODS.contains(method);
    }
    */

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined threading methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isThreadingMethod(getMethod("execOutsideUI"))    = true
     * isThreadingMethod(getMethod("doLater"))          = true
     * isThreadingMethod(getMethod("foo"))              = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is a threading method, false otherwise.
     */
    public static boolean isThreadingMethod(Method method) {
        return isThreadingMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined threading methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isThreadingMethod(getMethod("execOutsideUI"))    = true
     * isThreadingMethod(getMethod("doLater"))          = true
     * isThreadingMethod(getMethod("foo"))              = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is a threading method, false otherwise.
     */
    /*public static boolean isThreadingMethod(MetaMethod method) {
        return isThreadingMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined threading methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isThreadingMethod(getMethod("execOutsideUI"))    = true
     * isThreadingMethod(getMethod("doLater"))          = true
     * isThreadingMethod(getMethod("foo"))              = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is a threading method, false otherwise.
     */
    public static boolean isThreadingMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            THREADING_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined event publisher methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isEventPublisherMethod(getMethod("addEventPublisher"))  = true
     * isEventPublisherMethod(getMethod("publishEvent"))       = true
     * isEventPublisherMethod(getMethod("foo"))                = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an @EventPublisher method, false otherwise.
     */
    public static boolean isEventPublisherMethod(Method method) {
        return isEventPublisherMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined event publisher methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isEventPublisherMethod(getMethod("addEventPublisher"))  = true
     * isEventPublisherMethod(getMethod("publishEvent"))       = true
     * isEventPublisherMethod(getMethod("foo"))                = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an @EventPublisher method, false otherwise.
     */
    /*public static boolean isEventPublisherMethod(MetaMethod method) {
        return isEventPublisherMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined event publisher methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isEventPublisherMethod(getMethod("addEventPublisher"))  = true
     * isEventPublisherMethod(getMethod("publishEvent"))       = true
     * isEventPublisherMethod(getMethod("foo"))                = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an @EventPublisher method, false otherwise.
     */
    public static boolean isEventPublisherMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            EVENT_PUBLISHER_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined observable methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isObservableMethod(getMethod("addPropertyChangeListener"))  = true
     * isObservableMethod(getMethod("getPropertyChangeListeners")) = true
     * isObservableMethod(getMethod("foo"))                        = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isObservableMethod(Method method) {
        return isObservableMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined observable methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isObservableMethod(getMethod("addPropertyChangeListener"))  = true
     * isObservableMethod(getMethod("getPropertyChangeListeners")) = true
     * isObservableMethod(getMethod("foo"))                        = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an Observable method, false otherwise.
     */
    /*public static boolean isObservableMethod(MetaMethod method) {
        return isObservableMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined observable methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isObservableMethod(getMethod("addPropertyChangeListener"))  = true
     * isObservableMethod(getMethod("getPropertyChangeListeners")) = true
     * isObservableMethod(getMethod("foo"))                        = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isObservableMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            OBSERVABLE_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined resources methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isResourceHandlerMethod(getMethod("getResourceAsURL"))    = true
     * isResourceHandlerMethod(getMethod("getResourceAsStream")) = true
     * isResourceHandlerMethod(getMethod("foo"))                 = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isResourceHandlerMethod(Method method) {
        return isResourceHandlerMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined resources methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isResourceHandlerMethod(getMethod("getResourceAsURL"))    = true
     * isResourceHandlerMethod(getMethod("getResourceAsStream")) = true
     * isResourceHandlerMethod(getMethod("foo"))                 = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an Observable method, false otherwise.
     */
    /*public static boolean isResourceHandlerMethod(MetaMethod method) {
        return isResourceHandlerMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined resources methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isResourceHandlerMethod(getMethod("getResourceAsURL"))    = true
     * isResourceHandlerMethod(getMethod("getResourceAsStream")) = true
     * isResourceHandlerMethod(getMethod("foo"))                 = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isResourceHandlerMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            RESOURCE_HANDLER_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined message source methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isMessageSourceMethod(getMethod("getMessage"))    = true
     * isMessageSourceMethod(getMethod("foo"))           = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isMessageSourceMethod(Method method) {
        return isMessageSourceMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined message source methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isMessageSourceMethod(getMethod("getMessage"))    = true
     * isMessageSourceMethod(getMethod("foo"))           = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an Observable method, false otherwise.
     */
    /*public static boolean isMessageSourceMethod(MetaMethod method) {
        return isMessageSourceMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined message source methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isMessageSourceMethod(getMethod("getMessage"))    = true
     * isMessageSourceMethod(getMethod("foo"))           = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isMessageSourceMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            MESSAGE_SOURCE_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} belongs to the set of
     * predefined resource resolver methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate Method reference
     * isResourceResolverMethod(getMethod("resolveResource")) = true
     * isResourceResolverMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a Method reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isResourceResolverMethod(Method method) {
        return isResourceResolverMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} belongs to the set of
     * predefined resource resolver methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MetaMethod reference
     * isResourceResolverMethod(getMethod("resolveResource")) = true
     * isResourceResolverMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a MetaMethod reference
     * @return true if the method is an Observable method, false otherwise.
     */
    /*public static boolean isResourceResolverMethod(MetaMethod method) {
        return isResourceResolverMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} belongs to the set of
     * predefined resource resolver methods by convention.
     * <p/>
     * <pre>
     * // assuming getMethod() returns an appropriate MethodDescriptor reference
     * isResourceResolverMethod(getMethod("resolveResource")) = true
     * isResourceResolverMethod(getMethod("foo"))             = false
     * </pre>
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an Observable method, false otherwise.
     */
    public static boolean isResourceResolverMethod(MethodDescriptor method) {
        return !(method == null || !isInstanceMethod(method)) &&
            RESOURCE_RESOLVER_METHODS.contains(method);
    }

    /**
     * Finds out if the given {@code Method} is an instance method, i.e,
     * it is public and non-static.
     *
     * @param method a Method reference
     * @return true if the method is an instance method, false otherwise.
     */
    public static boolean isInstanceMethod(Method method) {
        return isInstanceMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} is an instance method, i.e,
     * it is public and non-static.
     *
     * @param method a MetaMethod reference
     * @return true if the method is an instance method, false otherwise.
     */
    /*public static boolean isInstanceMethod(MetaMethod method) {
        return isInstanceMethod(MethodDescriptor.forMethod(method));
    }*/

    /**
     * Finds out if the given {@code MethodDescriptor} is an instance method, i.e,
     * it is public and non-static.
     *
     * @param method a MethodDescriptor reference
     * @return true if the method is an instance method, false otherwise.
     */
    public static boolean isInstanceMethod(MethodDescriptor method) {
        if (method == null) return false;
        int modifiers = method.getModifiers();
        return Modifier.isPublic(modifiers) &&
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
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isPlainMethod(Method method) {
        return isPlainMethod(MethodDescriptor.forMethod(method));
    }

    /**
     * Finds out if the given {@code MetaMethod} matches the following criteria:<ul>
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
     * @param method a MetaMethod reference
     * @return true if the method matches the given criteria, false otherwise.
     */
    /*public static boolean isPlainMethod(MetaMethod method) {
        return isPlainMethod(MethodDescriptor.forMethod(method));
    }*/

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
     * @return true if the method matches the given criteria, false otherwise.
     */
    public static boolean isPlainMethod(MethodDescriptor method) {
        return isInstanceMethod(method) &&
            !isBasicMethod(method) &&
            !isGroovyInjectedMethod(method) &&
            !isThreadingMethod(method) &&
            !isArtifactMethod(method) &&
            !isMvcMethod(method) &&
            //!isServiceMethod(method) &&
            !isEventPublisherMethod(method) &&
            !isObservableMethod(method) &&
            !isResourceHandlerMethod(method) &&
            !isGetterMethod(method) &&
            !isSetterMethod(method) &&
            !isContributionMethod(method);
    }

    /*
    public static boolean isGetter(MetaProperty property) {
        return isGetter(property, false);
    }

    public static boolean isGetter(MetaProperty property, boolean strict) {
        if (property == null) return false;
        return GETTER_PATTERN_1.matcher(property.getName()).matches() ||
            (strict && GETTER_PATTERN_2.matcher(property.getName()).matches());
    }

    public static boolean isSetter(MetaProperty property) {
        if (property == null) return false;
        return SETTER_PATTERN.matcher(property.getName()).matches();
    }
    */

    /**
     * Returns true if the specified property in the specified class is of the specified type
     *
     * @param clazz        The class which contains the property
     * @param propertyName The property name
     * @param type         The type to check
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
     * @return the newly instantiated object.
     * @throws BeanInstantiationException if an error occurs when creating the object
     */
    public static Object instantiateClass(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException("Could not create an instance of " + clazz, e);
        }
    }

/*
    public static Object instantiate(Class<?> clazz, Object arg) {
        return instantiate(clazz, new Object[]{arg});
    }
*/

    public static Object instantiate(Class<?> clazz, Object[] args) {
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
     * @return The value of the property or null if none exists
     */
    public static Object getPropertyValueOfNewInstance(Class<?> clazz, String propertyName, Class<?> propertyType) {
        // validate
        if (clazz == null || GriffonNameUtils.isBlank(propertyName))
            return null;

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
     * @return The value of the property or null if none exists
     */
    public static Object getPropertyValueOfNewInstance(Class<?> clazz, String propertyName) {
        // validate
        if (clazz == null || GriffonNameUtils.isBlank(propertyName))
            return null;

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
     * @return The PropertyDescriptor
     */
    public static PropertyDescriptor getPropertyDescriptorForValue(Object instance, Object propertyValue) {
        if (instance == null || propertyValue == null)
            return null;

        PropertyDescriptor[] descriptors = getPropertyDescriptors(instance.getClass());

        for (PropertyDescriptor pd : descriptors) {
            if (isAssignableOrConvertibleFrom(pd.getPropertyType(), propertyValue.getClass())) {
                Object value;
                try {
                    value = getReadMethod(pd).invoke(instance, (Object[]) null);
                } catch (Exception e) {
                    throw new RuntimeException("Problem calling readMethod of " + pd, e);
                }
                if (propertyValue.equals(value))
                    return pd;
            }
        }
        return null;
    }

    /**
     * Returns the type of the given property contained within the specified class
     *
     * @param clazz        The class which contains the property
     * @param propertyName The name of the property
     * @return The property type or null if none exists
     */
    public static Class<?> getPropertyType(Class<?> clazz, String propertyName) {
        if (clazz == null || GriffonNameUtils.isBlank(propertyName))
            return null;

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
     * @return An array of PropertyDescriptor instances
     */
    public static PropertyDescriptor[] getPropertiesOfType(Class<?> clazz, Class<?> propertyType) {
        if (clazz == null || propertyType == null)
            return new PropertyDescriptor[0];

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
     * @return An array of PropertyDescriptor instances
     */
    public static PropertyDescriptor[] getPropertiesAssignableToType(Class<?> clazz, Class<?> propertySuperType) {
        if (clazz == null || propertySuperType == null)
            return new PropertyDescriptor[0];

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
     * @return A PropertyDescriptor instance or null if none exists
     */
    public static PropertyDescriptor getProperty(Class<?> clazz, String propertyName, Class<?> propertyType) {
        if (clazz == null || propertyName == null || propertyType == null)
            return null;

        try {
            PropertyDescriptor pd = getPropertyDescriptor(clazz, propertyName);
            if (pd.getPropertyType().equals(propertyType)) {
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
     * @return An object array
     */
    public static Object[] collectionToObjectArray(Collection<?> c) {
        if (c == null) return EMPTY_OBJECT_ARRAY;
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
     *         of the same native type
     */
    public static boolean isMatchBetweenPrimitiveAndWrapperTypes(Class<?> leftType, Class<?> rightType) {
        if (leftType == null) {
            throw new NullPointerException("Left type is null!");
        } else if (rightType == null) {
            throw new NullPointerException("Right type is null!");
        } else {
            return isMatchBetweenPrimitiveAndWrapperTypes(leftType.getName(), rightType.getName());
        }
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
     *         of the same native type
     */
    public static boolean isMatchBetweenPrimitiveAndWrapperTypes(String leftType, String rightType) {
        if (leftType == null) {
            throw new NullPointerException("Left type is null!");
        } else if (rightType == null) {
            throw new NullPointerException("Right type is null!");
        } else {
            String r = PRIMITIVE_TYPE_COMPATIBLE_TYPES.get(leftType);
            return r != null && r.equals(rightType);
        }
    }

    private static Method findDeclaredMethod(Class<?> clazz, String methodName, Class[] parameterTypes) {
        while (clazz != null) {
            try {
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                if (method != null) return method;
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
     * @return true if the property with name propertyName has a static getter method
     */
    public static boolean isStaticProperty(Class<?> clazz, String propertyName) {
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
     * @param m
     * @return True if the method is declared public static
     */
    public static boolean isPublicStatic(Method m) {
        final int modifiers = m.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
    }

    /**
     * Determine whether the field is declared public static
     *
     * @param f
     * @return True if the field is declared public static
     */
    public static boolean isPublicStatic(Field f) {
        final int modifiers = f.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
    }

    /**
     * Calculate the name for a getter method to retrieve the specified property
     *
     * @param propertyName
     * @return The name for the getter method for this property, if it were to exist, i.e. getConstraints
     */
    public static String getGetterName(String propertyName) {
        return PROPERTY_GET_PREFIX + Character.toUpperCase(propertyName.charAt(0))
            + propertyName.substring(1);
    }

    /**
     * <p>Get a static property value, which has a public static getter or is just a public static field.</p>
     *
     * @param clazz The class to check for static property
     * @param name  The property name
     * @return The value if there is one, or null if unset OR there is no such property
     */
    public static Object getStaticPropertyValue(Class<?> clazz, String name) {
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
    public static Object getPropertyOrStaticPropertyOrFieldValue(Object obj, String name) {
        if (isReadable(obj, name)) {
            try {
                return getProperty(obj, name);
            } catch (Exception e) {
                throw new BeanException("Error while reading value of property/field " + name, e);
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
     * @param obj
     * @param name
     * @return The object value or null if there is no such field or access problems
     */
    public static Object getFieldValue(Object obj, String name) {
        Class<?> clazz = obj.getClass();
        Field f;
        try {
            f = clazz.getDeclaredField(name);
            return f.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the a declared field on an object
     *
     * @param obj
     * @param name
     * @return The field or null if there is no such field or access problems
     */
    public static Field getField(Object obj, String name) {
        return getField(obj.getClass(), name);
    }

    /**
     * Get the a declared field on a class
     *
     * @param clazz
     * @param name
     * @return The field or null if there is no such field or access problems
     */
    public static Field getField(Class<?> clazz, String name) {
        Field f;
        try {
            f = clazz.getDeclaredField(name);
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Work out if the specified object has a public field with the name supplied.
     *
     * @param obj
     * @param name
     * @return True if a public field with the name exists
     */
    public static boolean isPublicField(Object obj, String name) {
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
     * @return True if the property is inherited
     */
    public static boolean isPropertyInherited(Class<?> clz, String propertyName) {
        if (clz == null) return false;
        if (GriffonNameUtils.isBlank(propertyName))
            throw new IllegalArgumentException("Argument [propertyName] cannot be null or blank");

        Class<?> superClass = clz.getSuperclass();

        PropertyDescriptor pd;
        try {
            pd = getPropertyDescriptor(superClass, propertyName);
        } catch (Exception e) {
            throw new BeanException("Could not read property descritptor for " + propertyName + " in " + superClass, e);
        }
        return pd != null && pd.getReadMethod() != null;
    }

    /**
     * Creates a concrete collection for the suppied interface
     *
     * @param interfaceType The interface
     * @return ArrayList for List, TreeSet for SortedSet, HashSet for Set etc.
     */
    public static Collection<?> createConcreteCollection(Class<?> interfaceType) {
        Collection<?> elements;
        if (interfaceType.equals(List.class)) {
            elements = new ArrayList<>();
        } else if (interfaceType.equals(SortedSet.class)) {
            elements = new TreeSet<>();
        } else {
            elements = new HashSet<>();
        }
        return elements;
    }

    /**
     * Retrieves the name of a setter for the specified property name
     *
     * @param propertyName The property name
     * @return The setter equivalent
     */
    public static String getSetterName(String propertyName) {
        return PROPERTY_SET_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    /**
     * Returns true if the name of the method specified and the number of arguments make it a javabean property
     *
     * @param name True if its a Javabean property
     * @param args The arguments
     * @return True if it is a javabean property method
     */
    public static boolean isGetter(String name, Class[] args) {
        if (GriffonNameUtils.isBlank(name) || args == null) return false;
        if (args.length != 0) return false;

        if (name.startsWith(PROPERTY_GET_PREFIX)) {
            name = name.substring(3);
            if (name.length() > 0 && Character.isUpperCase(name.charAt(0)))
                return true;
        } else if (name.startsWith(PROPERTY_IS_PREFIX)) {
            name = name.substring(2);
            if (name.length() > 0 && Character.isUpperCase(name.charAt(0)))
                return true;
        }
        return false;
    }

    /**
     * Returns a property name equivalent for the given getter name or null if it is not a getter
     *
     * @param getterName The getter name
     * @return The property name equivalent
     */
    public static String getPropertyForGetter(String getterName) {
        if (GriffonNameUtils.isBlank(getterName)) return null;

        if (getterName.startsWith(PROPERTY_GET_PREFIX)) {
            String prop = getterName.substring(3);
            return convertPropertyName(prop);
        } else if (getterName.startsWith(PROPERTY_IS_PREFIX)) {
            String prop = getterName.substring(2);
            return convertPropertyName(prop);
        }
        return null;
    }

    private static String convertPropertyName(String prop) {
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
     * @return The property name equivalent
     */
    public static String getPropertyForSetter(String setterName) {
        if (GriffonNameUtils.isBlank(setterName)) return null;

        if (setterName.startsWith(PROPERTY_SET_PREFIX)) {
            String prop = setterName.substring(3);
            return convertPropertyName(prop);
        }
        return null;
    }

    public static boolean isSetter(String name, Class[] args) {
        if (GriffonNameUtils.isBlank(name) || args == null) return false;

        if (name.startsWith(PROPERTY_SET_PREFIX)) {
            if (args.length != 1) return false;
            name = name.substring(3);
            if (name.length() > 0 && Character.isUpperCase(name.charAt(0)))
                return true;
        }

        return false;
    }

    /*
    public static MetaClass getExpandoMetaClass(Class<?> clazz) {
        MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
        isTrue(registry.getMetaClassCreationHandler() instanceof ExpandoMetaClassCreationHandle, "Griffon requires an instance of [ExpandoMetaClassCreationHandle] to be set in Groovy's MetaClassRegistry!");
        MetaClass mc = registry.getMetaClass(clazz);
        AdaptingMetaClass adapter = null;
        if (mc instanceof AdaptingMetaClass) {
            adapter = (AdaptingMetaClass) mc;
            mc = ((AdaptingMetaClass) mc).getAdaptee();
        }

        if (!(mc instanceof ExpandoMetaClass)) {
            // removes cached version
            registry.removeMetaClass(clazz);
            mc = registry.getMetaClass(clazz);
            if (adapter != null) {
                adapter.setAdaptee(mc);
            }
        }
        isTrue(mc instanceof ExpandoMetaClass, "BUG! Method must return an instance of [ExpandoMetaClass]!");
        return mc;
    }
    */

    /**
     * Returns true if the specified clazz parameter is either the same as, or is a superclass or superinterface
     * of, the specified type parameter. Converts primitive types to compatible class automatically.
     *
     * @param clazz
     * @param type
     * @return True if the class is a taglib
     * @see java.lang.Class#isAssignableFrom(Class)
     */
    public static boolean isAssignableOrConvertibleFrom(Class<?> clazz, Class<?> type) {
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
    public static boolean getBooleanFromMap(String key, Map<String, Object> map) {
        if (map == null) return false;
        if (map.containsKey(key)) {
            Object o = map.get(key);
            if (o == null) return false;
            else if (o instanceof Boolean) {
                return (Boolean) o;
            } else {
                return Boolean.valueOf(o.toString());
            }
        }
        return false;
    }

    /**
     * Locates the name of a property for the given value on the target object using Groovy's meta APIs.
     * Note that this method uses the reference so the incorrect result could be returned for two properties
     * that refer to the same reference. Use with caution.
     *
     * @param target The target
     * @param obj    The property value
     * @return The property name or null
     */
    /*public static String findPropertyNameForValue(Object target, Object obj) {
        MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(target.getClass());
        List<MetaProperty> metaProperties = mc.getProperties();
        for (MetaProperty metaProperty : metaProperties) {
            if (isAssignableOrConvertibleFrom(metaProperty.getType(), obj.getClass())) {
                Object val = metaProperty.getProperty(target);
                if (val != null && val.equals(obj))
                    return metaProperty.getName();
            }
        }
        return null;
    }*/

    /**
     * Returns whether the specified class is either within one of the specified packages or
     * within a subpackage of one of the packages
     *
     * @param theClass    The class
     * @param packageList The list of packages
     * @return True if it is within the list of specified packages
     */
    public static boolean isClassBelowPackage(Class<?> theClass, List<?> packageList) {
        String classPackage = theClass.getPackage().getName();
        for (Object packageName : packageList) {
            if (packageName != null) {
                if (classPackage.startsWith(packageName.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setProperties(@Nonnull Object bean, @Nonnull Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            setPropertyValue(bean, entry.getKey(), entry.getValue());
        }
    }

    public static void setPropertiesNoException(@Nonnull Object bean, @Nonnull Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            try {
                setPropertyValue(bean, entry.getKey(), entry.getValue());
            } catch (PropertyException e) {
                // ignore
            }
        }
    }

    public static void setPropertyValue(@Nonnull Object bean, @Nonnull String name, @Nullable Object value) {
        try {
            setProperty(bean, name, value);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new PropertyException(bean, name, value, e);
        } catch (InvocationTargetException e) {
            throw new PropertyException(bean, name, value, e.getTargetException());
        }
    }

    @Nullable
    public static Object getPropertyValue(@Nonnull Object bean, @Nonnull String name) {
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
     * @return the property descriptor or null if the bean does not have
     *         a property that matches the specified name.
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public static PropertyDescriptor getPropertyDescriptor(Object bean,
                                                           String name)
        throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                bean.getClass() + "'");
        }

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
     * @return the property descriptor or null if the bean does not have
     *         a property that matches the specified name.
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz,
                                                           String name)
        throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (clazz == null) {
            throw new IllegalArgumentException("No class specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for class '" + clazz + "'");
        }

        PropertyDescriptor[] descriptors = getPropertyDescriptors(clazz);
        if (descriptors != null) {
            for (PropertyDescriptor descriptor : descriptors) {
                if (name.equals(descriptor.getName())) {
                    return (descriptor);
                }
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
     * @return the property descriptors
     * @throws IllegalArgumentException if <code>beanClass</code> is null
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        if (beanClass == null) {
            throw new IllegalArgumentException("No bean class specified");
        }

        // Look up any cached descriptors for this bean class
        PropertyDescriptor[] descriptors;
        descriptors = descriptorsCache.get(beanClass.getName());
        if (descriptors != null) {
            return (descriptors);
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
        return (descriptors);
    }

    /**
     * <p>Return an accessible property getter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * @param descriptor Property descriptor to return a getter for
     * @return The read method
     */
    public static Method getReadMethod(PropertyDescriptor descriptor) {
        return (MethodUtils.getAccessibleMethod(descriptor.getReadMethod()));
    }

    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a readable property on the specified bean; otherwise, return
     * <code>false</code>.
     *
     * @param bean Bean to be examined
     * @param name Property name to be evaluated
     * @return <code>true</code> if the property is readable,
     *         otherwise <code>false</code>
     * @throws IllegalArgumentException if <code>bean</code>
     *                                  or <code>name</code> is <code>null</code>
     * @since BeanUtils 1.6
     */
    public static boolean isReadable(Object bean, String name) {
        // Validate method parameters
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                bean.getClass() + "'");
        }

        try {
            PropertyDescriptor desc = getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method readMethod = getReadMethod(bean.getClass(), desc);
                if (readMethod == null) {
                    readMethod = MethodUtils.getAccessibleMethod(bean.getClass(), readMethod);
                }
                return (readMethod != null);
            } else {
                return false;
            }
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * @param descriptor Property descriptor to return a setter for
     * @return The write method
     */
    public static Method getWriteMethod(PropertyDescriptor descriptor) {
        return (MethodUtils.getAccessibleMethod(descriptor.getWriteMethod()));
    }

    /**
     * <p>Return <code>true</code> if the specified property name identifies
     * a writable property on the specified bean; otherwise, return
     * <code>false</code>.
     *
     * @param bean Bean to be examined
     * @param name Property name to be evaluated
     * @return <code>true</code> if the property is writable,
     *         otherwise <code>false</code>
     * @throws IllegalArgumentException if <code>bean</code>
     *                                  or <code>name</code> is <code>null</code>
     */
    public static boolean isWritable(Object bean, String name) {
        // Validate method parameters
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                bean.getClass() + "'");
        }

        try {
            PropertyDescriptor desc = getPropertyDescriptor(bean, name);
            if (desc != null) {
                Method writeMethod = getWriteMethod(bean.getClass(), desc);
                if (writeMethod == null) {
                    writeMethod = MethodUtils.getAccessibleMethod(bean.getClass(), writeMethod);
                }
                return (writeMethod != null);
            } else {
                return false;
            }
        } catch (IllegalAccessException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Sets the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean  Bean whose property is to be mutated
     * @param name  Possibly indexed and/or nested name of the property
     *              to be mutated
     * @param value The value to be set on the property
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public static void setProperty(Object bean, String name, Object value)
        throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                bean.getClass() + "'");
        }

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

        // Call the property setter
        writeMethod.invoke(bean, value);
    }

    /**
     * Return the value of the specified property of the specified bean,
     * no matter which property reference format is used, with no
     * type conversions.
     *
     * @param bean Bean whose property is to be extracted
     * @param name Possibly indexed and/or nested name of the property
     *             to be extracted
     * @return the property value
     * @throws IllegalAccessException    if the caller does not have
     *                                   access to the property accessor method
     * @throws IllegalArgumentException  if <code>bean</code> or
     *                                   <code>name</code> is null
     * @throws InvocationTargetException if the property accessor method
     *                                   throws an exception
     * @throws NoSuchMethodException     if an accessor method for this
     *                                   property cannot be found
     */
    public static Object getProperty(Object bean, String name)
        throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        if (bean == null) {
            throw new IllegalArgumentException("No bean specified");
        }
        if (name == null) {
            throw new IllegalArgumentException("No name specified for bean class '" +
                bean.getClass() + "'");
        }

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
     * @return The read method
     */
    public static Method getReadMethod(Class<?> clazz, PropertyDescriptor descriptor) {
        return (MethodUtils.getAccessibleMethod(clazz, descriptor.getReadMethod()));
    }

    /**
     * <p>Return an accessible property setter method for this property,
     * if there is one; otherwise return <code>null</code>.</p>
     *
     * @param clazz      The class of the write method will be invoked on
     * @param descriptor Property descriptor to return a setter for
     * @return The write method
     */
    public static Method getWriteMethod(Class<?> clazz, PropertyDescriptor descriptor) {
        return (MethodUtils.getAccessibleMethod(clazz, descriptor.getWriteMethod()));
    }

    // -- The following methods and properties were copied from commons-lang

    /**
     * <p>Validate that the argument condition is <code>true</code>; otherwise
     * throwing an exception with the specified message. This method is useful when
     * validating according to an arbitrary boolean expression, such as validating a
     * primitive number or using your own custom validation expression.</p>
     * <p/>
     * <pre>
     * isTrue( (i > 0), "The value must be greater than zero");
     * isTrue( myObject.isOk(), "The object is not OK");
     * </pre>
     *
     * @param expression the boolean expression to check
     * @param message    the exception message if invalid
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static Object invokeInstanceMethod(Object object, String methodName) {
        return invokeInstanceMethod(object, methodName, EMPTY_ARGS);
    }

    public static Object invokeInstanceMethod(Object object, String methodName, Object arg) {
        return invokeInstanceMethod(object, methodName, new Object[]{arg});
    }

    public static Object invokeInstanceMethod(Object object, String methodName, Object... args) {
        try {
            return invokeMethod(object, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e.getTargetException());
        }
    }

    public static Object invokeExactInstanceMethod(Object object, String methodName) {
        return invokeExactInstanceMethod(object, methodName, EMPTY_ARGS);
    }

    public static Object invokeExactInstanceMethod(Object object, String methodName, Object arg) {
        return invokeExactInstanceMethod(object, methodName, new Object[]{arg});
    }

    public static Object invokeExactInstanceMethod(Object object, String methodName, Object... args) {
        try {
            return invokeExactMethod(object, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new InstanceMethodInvocationException(object, methodName, args, e.getTargetException());
        }
    }

    public static Object invokeStaticMethod(Class<?> type, String methodName) {
        return invokeStaticMethod(type, methodName, EMPTY_ARGS);
    }

    public static Object invokeStaticMethod(Class<?> type, String methodName, Object arg) {
        return invokeStaticMethod(type, methodName, new Object[]{arg});
    }

    public static Object invokeStaticMethod(Class<?> type, String methodName, Object... args) {
        try {
            return MethodUtils.invokeStaticMethod(type, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e.getTargetException());
        }
    }

    public static Object invokeExactStaticMethod(Class<?> type, String methodName) {
        return invokeExactStaticMethod(type, methodName, EMPTY_ARGS);
    }

    public static Object invokeExactStaticMethod(Class<?> type, String methodName, Object arg) {
        return invokeExactStaticMethod(type, methodName, new Object[]{arg});
    }

    public static Object invokeExactStaticMethod(Class<?> type, String methodName, Object... args) {
        try {
            return MethodUtils.invokeExactStaticMethod(type, methodName, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e);
        } catch (InvocationTargetException e) {
            throw new StaticMethodInvocationException(type, methodName, args, e.getTargetException());
        }
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

    /**
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
     * @return the class name of the object without the package name, or the null value
     */
    public static String getShortClassName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass());
    }

    /**
     * <p>Gets the class name minus the package name from a <code>Class</code>.</p>
     *
     * @param cls the class to get the short name for.
     * @return the class name without the package name or an empty string
     */
    public static String getShortClassName(Class<?> cls) {
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
     * @return the class name of the class without the package name or an empty string
     */
    public static String getShortClassName(String className) {
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
     * @return the package name of the object, or the null value
     */
    public static String getPackageName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageName(object.getClass());
    }

    /**
     * <p>Gets the package name of a <code>Class</code>.</p>
     *
     * @param cls the class to get the package name for, may be <code>null</code>.
     * @return the package name or an empty string
     */
    public static String getPackageName(Class<?> cls) {
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
     * @return the package name or an empty string
     */
    public static String getPackageName(String className) {
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
     * @return the types of the arguments
     */
    public static Class<?>[] convertToTypeArray(Object[] args) {
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
