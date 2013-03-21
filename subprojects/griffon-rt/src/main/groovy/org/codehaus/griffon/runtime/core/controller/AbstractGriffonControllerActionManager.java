/*
 * Copyright 2007-2013 the original author or authors.
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
 */

package org.codehaus.griffon.runtime.core.controller;

import griffon.core.GriffonApplication;
import griffon.core.GriffonController;
import griffon.core.GriffonControllerClass;
import griffon.core.controller.AbortActionExecution;
import griffon.core.controller.GriffonControllerAction;
import griffon.core.controller.GriffonControllerActionInterceptor;
import griffon.core.controller.GriffonControllerActionManager;
import griffon.core.i18n.NoSuchMessageException;
import griffon.transform.Threading;
import griffon.util.GriffonClassUtils;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static griffon.util.GriffonApplicationUtils.isMacOSX;
import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.*;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static org.codehaus.griffon.runtime.core.DefaultGriffonControllerClass.hasVoidOrDefAsReturnType;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.reverse;
import static org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation.castToBoolean;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public abstract class AbstractGriffonControllerActionManager implements GriffonControllerActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGriffonControllerActionManager.class);
    private static final String KEY_THREADING = "controller.threading";
    private final ActionCache actionCache = new ActionCache();
    private final Map<String, Threading.Policy> threadingPolicies = new ConcurrentHashMap<String, Threading.Policy>();
    private final List<GriffonControllerActionInterceptor> interceptors = new CopyOnWriteArrayList<GriffonControllerActionInterceptor>();
    private GriffonApplication app;

    protected AbstractGriffonControllerActionManager(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    public Map<String, GriffonControllerAction> actionsFor(GriffonController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller parameter is null!");
        }
        Map<String, GriffonControllerAction> actions = actionCache.get(controller);
        if (actions.isEmpty()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No actions defined for controller " + controller);
            }
        }
        return actions;
    }

    public GriffonControllerAction actionFor(GriffonController controller, String actionName) {
        if (controller == null) {
            throw new IllegalArgumentException("controller parameter is null!");
        }
        if (isBlank(actionName)) {
            throw new IllegalArgumentException("actionName parameter is null!");
        }

        return actionCache.get(controller).get(normalizeName(actionName));
    }

    public void createActions(GriffonController controller) {
        GriffonControllerClass griffonClass = (GriffonControllerClass) controller.getGriffonClass();
        for (String actionName : griffonClass.getActionNames()) {
            GriffonControllerAction action = createAndConfigureAction(controller, actionName);

            Method method = findActionAsMethod(controller, actionName);
            Field field = method == null ? findActionAsClosureField(controller, actionName) : null;
            for (GriffonControllerActionInterceptor interceptor : interceptors) {
                // try method first
                if (method != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Configuring action " + controller.getClass().getName() + "." + actionName + " with " + interceptor);
                    }
                    interceptor.configure(controller, actionName, method);
                } else if (field != null) {
                    // try closure property next
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Configuring action " + controller.getClass().getName() + "." + actionName + " with " + interceptor);
                    }
                    interceptor.configure(controller, actionName, field);
                }
            }

            Map<String, GriffonControllerAction> actions = actionCache.get(controller);
            if (actions.isEmpty()) {
                actions = new LinkedHashMap<String, GriffonControllerAction>();
                actionCache.set(controller, actions);
            }
            String actionKey = normalizeName(actionName);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Action for " + controller.getClass().getName() + "." + actionName + " stored as " + actionKey);
            }
            actions.put(actionKey, action);
        }
    }

    public void invokeAction(final GriffonController controller, final String actionName, final Object[] args) {
        Runnable runnable = new Runnable() {
            public void run() {
                Object[] updatedArgs = args;
                List<GriffonControllerActionInterceptor> copy = new ArrayList<GriffonControllerActionInterceptor>(interceptors);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Invoking " + copy.size() + " interceptors for " + controller.getClass().getName() + "." + actionName);
                }

                for (GriffonControllerActionInterceptor interceptor : copy) {
                    try {
                        updatedArgs = interceptor.before(controller, actionName, updatedArgs);
                    } catch (AbortActionExecution aae) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Execution of " + controller.getClass().getName() + "." + actionName + " was aborted by " + interceptor);
                        }
                        return;
                    }
                }

                RuntimeException exception = null;
                try {
                    InvokerHelper.invokeMethod(controller, actionName, updatedArgs);
                } catch (RuntimeException e) {
                    exception = (RuntimeException) sanitize(e);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("An exception occurred when executing " + controller.getClass().getName() + "." + actionName, exception);
                    }
                }

                boolean exceptionWasHandled = false;
                for (GriffonControllerActionInterceptor interceptor : reverse(copy)) {
                    if (exception == null) {
                        interceptor.after(controller, actionName, updatedArgs);
                    } else if (!exceptionWasHandled) {
                        exceptionWasHandled = interceptor.exception(exception, controller, actionName, updatedArgs);
                    }
                }

                if (exception != null && !exceptionWasHandled) {
                    // throw it again
                    throw exception;
                }
            }
        };
        invokeAction(controller, actionName, runnable);
    }

    private void invokeAction(GriffonController controller, String actionName, Runnable runnable) {
        String fullQualifiedActionName = controller.getClass().getName() + "." + actionName;
        Threading.Policy policy = threadingPolicies.get(fullQualifiedActionName);
        if (policy == null) {
            if (isThreadingDisabled(fullQualifiedActionName)) {
                policy = Threading.Policy.SKIP;
            } else {
                policy = resolveThreadingPolicy(controller, actionName);
            }
            threadingPolicies.put(fullQualifiedActionName, policy);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing " + controller.getClass().getName() + "." + actionName + " with policy " + policy);
        }

        switch (policy) {
            case OUTSIDE_UITHREAD:
                getApp().execOutsideUI(runnable);
                break;
            case INSIDE_UITHREAD_SYNC:
                getApp().execInsideUISync(runnable);
                break;
            case INSIDE_UITHREAD_ASYNC:
                getApp().execInsideUIAsync(runnable);
                break;
            case SKIP:
            default:
                runnable.run();
        }
    }

    private static Method findActionAsMethod(GriffonController controller, String actionName) {
        for (Method method : controller.getClass().getMethods()) {
            if (actionName.equals(method.getName()) &&
                isPublic(method.getModifiers()) &&
                !isStatic(method.getModifiers()) &&
                hasVoidOrDefAsReturnType(method)) {
                return method;
            }
        }
        return null;
    }

    private static Field findActionAsClosureField(GriffonController controller, String actionName) {
        try {
            Object propertyValue = GriffonClassUtils.getProperty(controller, actionName);
            if (!(propertyValue instanceof Closure)) return null;
            return GriffonClassUtils.getField(controller, actionName);
        } catch (IllegalAccessException e) {
            // ignore
        } catch (InvocationTargetException e) {
            // ignore
        } catch (NoSuchMethodException e) {
            // ignore
        }
        return null;
    }

    private Threading.Policy resolveThreadingPolicy(GriffonController controller, String actionName) {
        // try method first
        Method method = findActionAsMethod(controller, actionName);
        if (method != null) {
            Threading annotation = method.getAnnotation(Threading.class);
            return annotation == null ? Threading.Policy.OUTSIDE_UITHREAD : annotation.value();
        }

        // try closure property next
        Field field = findActionAsClosureField(controller, actionName);
        if (field != null) {
            Threading annotation = field.getAnnotation(Threading.class);
            return annotation == null ? Threading.Policy.OUTSIDE_UITHREAD : annotation.value();
        }

        return Threading.Policy.OUTSIDE_UITHREAD;
    }

    private boolean isThreadingDisabled(String actionName) {
        Map settings = getApp().getConfig().flatten(new LinkedHashMap());

        String keyName = KEY_THREADING + "." + actionName;
        while (!KEY_THREADING.equals(keyName)) {
            Object value = settings.get(keyName);
            keyName = keyName.substring(0, keyName.lastIndexOf("."));
            if (value != null && !toBoolean(value)) return true;
        }

        return false;
    }

    public void addActionInterceptor(GriffonControllerActionInterceptor actionInterceptor) {
        if (actionInterceptor == null || interceptors.contains(actionInterceptor)) {
            return;
        }
        interceptors.add(actionInterceptor);
    }

    protected GriffonControllerAction createAndConfigureAction(GriffonController controller, String actionName) {
        GriffonControllerAction action = createControllerAction(controller, actionName);

        String normalizeNamed = capitalize(normalizeName(actionName));
        String keyPrefix = controller.getClass().getName() + ".action.";

        String rsActionName = msg(keyPrefix, normalizeNamed, "name", getNaturalName(normalizeNamed));
        if (!isBlank(rsActionName)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".name = " + rsActionName);
            }
            action.setName(rsActionName);
        }

        String rsAccelerator = msg(keyPrefix, normalizeNamed, "accelerator", "");
        if (!isBlank(rsAccelerator)) {
            if (!isMacOSX() && rsAccelerator.contains("meta") && !rsAccelerator.contains("ctrl")) {
                rsAccelerator = rsAccelerator.replace("meta", "ctrl");
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".accelerator = " + rsAccelerator);
            }
            action.setAccelerator(rsAccelerator);
        }

        /*
        String rsCommand = msg(keyPrefix, normalizeNamed, "command", "");
        if (!isBlank(rsCommand)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".command = " + rsCommand);
            }
            action.setCommand(rsCommand);
        }
        */

        String rsShortDescription = msg(keyPrefix, normalizeNamed, "short_description", "");
        if (!isBlank(rsShortDescription)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".short_description = " + rsShortDescription);
            }
            action.setShortDescription(rsShortDescription);
        }

        String rsLongDescription = msg(keyPrefix, normalizeNamed, "long_description", "");
        if (!isBlank(rsLongDescription)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".long_description = " + rsLongDescription);
            }
            action.setLongDescription(rsLongDescription);
        }

        String rsMnemonic = msg(keyPrefix, normalizeNamed, "mnemonic", "");
        if (!isBlank(rsMnemonic)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".mnemonic = " + rsMnemonic);
            }
            action.setMnemonic(rsMnemonic);
        }

        String rsSmallIcon = msg(keyPrefix, normalizeNamed, "small_icon", "");
        if (!isBlank(rsSmallIcon)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".small_icon = " + rsSmallIcon);
            }
            action.setSmallIcon(rsSmallIcon);
        }

        String rsLargeIcon = msg(keyPrefix, normalizeNamed, "large_icon", "");
        if (!isBlank(rsLargeIcon)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".large_icon = " + rsLargeIcon);
            }
            action.setLargeIcon(rsLargeIcon);
        }

        String rsEnabled = msg(keyPrefix, normalizeNamed, "enabled", "true");
        if (!isBlank(rsEnabled)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".enabled = " + rsEnabled);
            }
            action.setEnabled(toBoolean(rsEnabled));
        }

        String rsSelected = msg(keyPrefix, normalizeNamed, "selected", "false");
        if (!isBlank(rsSelected)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".selected = " + rsSelected);
            }
            action.setSelected(toBoolean(rsSelected));
        }

        action.initialize();

        return action;
    }

    protected abstract GriffonControllerAction createControllerAction(GriffonController controller, String actionName);

    public String normalizeName(String actionName) {
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }
        return uncapitalize(actionName);
    }

    protected String msg(String key, String actionName, String subkey, String defaultValue) {
        try {
            return app.getMessage(key + actionName + "." + subkey);
        } catch (NoSuchMessageException nsme) {
            return app.getMessage("application.action." + actionName + "." + subkey, defaultValue);
        }
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else if (value instanceof CharSequence) {
            return "true".equalsIgnoreCase(String.valueOf(value));
        }
        return castToBoolean(value);
    }

    private static class ActionCache {
        private final Map<WeakReference<GriffonController>, Map<String, GriffonControllerAction>> cache = Collections.synchronizedMap(new LinkedHashMap<WeakReference<GriffonController>, Map<String, GriffonControllerAction>>());

        public Map<String, GriffonControllerAction> get(GriffonController controller) {
            synchronized (cache) {
                for (Map.Entry<WeakReference<GriffonController>, Map<String, GriffonControllerAction>> entry : cache.entrySet()) {
                    GriffonController test = entry.getKey().get();
                    if (test == controller) {
                        return entry.getValue();
                    }
                }
            }
            return Collections.emptyMap();
        }

        public void set(GriffonController controller, Map<String, GriffonControllerAction> actions) {
            WeakReference<GriffonController> existingController = null;
            synchronized (cache) {
                for (WeakReference<GriffonController> key : cache.keySet()) {
                    if (key.get() == controller) {
                        existingController = key;
                        break;
                    }
                }
            }

            if (null != existingController) {
                cache.remove(existingController);
            }

            cache.put(new WeakReference<GriffonController>(controller), actions);
        }
    }
}
