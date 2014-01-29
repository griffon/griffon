/*
 * Copyright 2007-2014 the original author or authors.
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

import griffon.core.Configuration;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonControllerClass;
import griffon.core.controller.*;
import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;
import griffon.core.threading.UIThreadManager;
import griffon.exceptions.InstanceMethodInvocationException;
import griffon.transform.Threading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.CollectionUtils.reverse;
import static griffon.util.GriffonClassUtils.EMPTY_ARGS;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonNameUtils.*;
import static griffon.util.TypeUtils.castToBoolean;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractActionManager implements ActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractActionManager.class);

    private static final String KEY_THREADING = "controller.threading";
    private static final String KEY_DISABLE_THREADING_INJECTION = "griffon.disable.threading.injection";
    private static final String ERROR_CONTROLLER_NULL = "Argument 'controller' cannot be null";
    private static final String ERROR_ACTION_NAME_BLANK = "Argument 'actionName' cannot be blank";
    private static final String ERROR_ACTION_INTERCEPTOR_NULL = "Argument 'actionInterceptor' cannot be null";
    private final ActionCache actionCache = new ActionCache();
    private final Map<String, Threading.Policy> threadingPolicies = new ConcurrentHashMap<>();
    private final List<ActionInterceptor> interceptors = new CopyOnWriteArrayList<>();

    private final GriffonApplication application;

    @Inject
    public AbstractActionManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
    }

    @Nonnull
    protected Configuration getConfiguration() {
        return application.getConfiguration();
    }

    @Nonnull
    protected MessageSource getMessageSource() {
        return application.getMessageSource();
    }

    @Nonnull
    protected UIThreadManager getUiThreadManager() {
        return application.getUIThreadManager();
    }

    @Nonnull
    protected Map<String, Threading.Policy> getThreadingPolicies() {
        return threadingPolicies;
    }

    @Nonnull
    public Map<String, Action> actionsFor(@Nonnull GriffonController controller) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        Map<String, Action> actions = actionCache.get(controller);
        if (actions.isEmpty()) {
            LOG.trace("No actions defined for controller {}", controller);
        }
        return actions;
    }

    @Nullable
    public Action actionFor(@Nonnull GriffonController controller, @Nonnull String actionName) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        return actionCache.get(controller).get(normalizeName(actionName));
    }

    public void createActions(@Nonnull GriffonController controller) {
        GriffonControllerClass griffonClass = (GriffonControllerClass) controller.getGriffonClass();
        for (String actionName : griffonClass.getActionNames()) {
            Action action = createAndConfigureAction(controller, actionName);

            Method method = findActionAsMethod(controller, actionName);
            final String qualifiedActionName = controller.getClass().getName() + "." + actionName;
            for (ActionInterceptor interceptor : interceptors) {
                if (method != null) {
                    LOG.debug("Configuring action {} with {}", qualifiedActionName, interceptor);
                    interceptor.configure(controller, actionName, method);
                }
            }

            Map<String, Action> actions = actionCache.get(controller);
            if (actions.isEmpty()) {
                actions = new LinkedHashMap<>();
                actionCache.set(controller, actions);
            }
            String actionKey = normalizeName(actionName);
            LOG.trace("Action for {} stored as {}", qualifiedActionName, actionKey);
            actions.put(actionKey, action);
        }
    }

    public void invokeAction(@Nonnull final GriffonController controller, @Nonnull final String actionName, @Nonnull final Object... args) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        Runnable runnable = new Runnable() {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            public void run() {
                Object[] updatedArgs = args;
                List<ActionInterceptor> copy = new ArrayList<>(interceptors);
                List<ActionInterceptor> invokedInterceptors = new ArrayList<>();

                final String qualifiedActionName = controller.getClass().getName() + "." + actionName;
                ActionExecutionStatus status = ActionExecutionStatus.OK;

                if (LOG.isDebugEnabled()) {
                    int size = copy.size();
                    LOG.debug("Executing " + size + " interceptor" + (size != 1 ? "s" : "") + " for " + qualifiedActionName);
                }

                for (ActionInterceptor interceptor : copy) {
                    invokedInterceptors.add(interceptor);
                    try {
                        LOG.trace("Calling {}.before() on {}", interceptor, qualifiedActionName);
                        updatedArgs = interceptor.before(controller, actionName, updatedArgs);
                    } catch (AbortActionExecution aae) {
                        status = ActionExecutionStatus.ABORTED;
                        LOG.debug("Execution of {} was aborted by {}", qualifiedActionName, interceptor);
                        break;
                    }
                }

                LOG.trace("Status before execution of {} is {}", qualifiedActionName, status);
                RuntimeException exception = null;
                boolean exceptionWasHandled = false;
                if (status == ActionExecutionStatus.OK) {
                    try {
                        doInvokeAction(controller, actionName, updatedArgs);
                    } catch (RuntimeException e) {
                        status = ActionExecutionStatus.EXCEPTION;
                        exception = (RuntimeException) sanitize(e);
                        LOG.warn("An exception occurred when executing {}", qualifiedActionName, exception);
                    }
                    LOG.trace("Status after execution of {} is {}", qualifiedActionName, status);

                    if (exception != null) {
                        for (ActionInterceptor interceptor : reverse(invokedInterceptors)) {
                            LOG.trace("Calling {}.exception() on {}", interceptor, qualifiedActionName);
                            exceptionWasHandled = interceptor.exception(exception, controller, actionName, updatedArgs);
                        }
                    }
                }

                for (ActionInterceptor interceptor : reverse(invokedInterceptors)) {
                    LOG.trace("Calling {}.after() on {}", interceptor, qualifiedActionName);
                    interceptor.after(status, controller, actionName, updatedArgs);
                }

                if (exception != null && !exceptionWasHandled) {
                    // throw it again
                    throw exception;
                }
            }
        };
        invokeAction(controller, actionName, runnable);
    }

    protected void doInvokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] updatedArgs) {
        try {
            invokeExactInstanceMethod(controller, actionName, updatedArgs);
        } catch (InstanceMethodInvocationException imie) {
            if (imie.getCause() instanceof NoSuchMethodException) {
                // try again but this time remove the 1st arg if it's
                // descendant of java.util.EventObject
                if (updatedArgs.length == 1 && updatedArgs[0] != null && EventObject.class.isAssignableFrom(updatedArgs[0].getClass())) {
                    invokeExactInstanceMethod(controller, actionName, EMPTY_ARGS);
                } else {
                    throw imie;
                }
            } else {
                throw imie;
            }
        }
    }

    private void invokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Runnable runnable) {
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

        LOG.debug("Executing {}.{} with policy {}", controller.getClass().getName(), actionName, policy);

        switch (policy) {
            case OUTSIDE_UITHREAD:
                getUiThreadManager().runOutsideUI(runnable);
                break;
            case INSIDE_UITHREAD_SYNC:
                getUiThreadManager().runInsideUISync(runnable);
                break;
            case INSIDE_UITHREAD_ASYNC:
                getUiThreadManager().runInsideUIAsync(runnable);
                break;
            case SKIP:
            default:
                runnable.run();
        }
    }

    @Nullable
    private static Method findActionAsMethod(@Nonnull GriffonController controller, @Nonnull String actionName) {
        for (Method method : controller.getClass().getMethods()) {
            if (actionName.equals(method.getName()) &&
                isPublic(method.getModifiers()) &&
                !isStatic(method.getModifiers()) &&
                method.getReturnType() == Void.TYPE) {
                return method;
            }
        }
        return null;
    }

    @Nonnull
    private Threading.Policy resolveThreadingPolicy(@Nonnull GriffonController controller, @Nonnull String actionName) {
        Method method = findActionAsMethod(controller, actionName);
        if (method != null) {
            Threading annotation = method.getAnnotation(Threading.class);
            return annotation == null ? Threading.Policy.OUTSIDE_UITHREAD : annotation.value();
        }

        return Threading.Policy.OUTSIDE_UITHREAD;
    }

    private boolean isThreadingDisabled(@Nonnull String actionName) {
        if (getConfiguration().getAsBoolean(KEY_DISABLE_THREADING_INJECTION, false)) {
            return true;
        }

        Map<String, Object> settings = getConfiguration().asFlatMap();

        String keyName = KEY_THREADING + "." + actionName;
        while (!KEY_THREADING.equals(keyName)) {
            Object value = settings.get(keyName);
            keyName = keyName.substring(0, keyName.lastIndexOf("."));
            if (value != null && !castToBoolean(value)) return true;
        }

        return false;
    }

    public void addActionInterceptor(@Nonnull ActionInterceptor actionInterceptor) {
        requireNonNull(actionInterceptor, ERROR_ACTION_INTERCEPTOR_NULL);
        if (interceptors.contains(actionInterceptor)) {
            return;
        }
        interceptors.add(actionInterceptor);
    }

    @Nonnull
    protected Action createAndConfigureAction(@Nonnull GriffonController controller, @Nonnull String actionName) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        Action action = createControllerAction(controller, actionName);

        String normalizeNamed = capitalize(normalizeName(actionName));
        String keyPrefix = controller.getClass().getName() + ".action.";

        String rsActionName = msg(keyPrefix, normalizeNamed, "name", getNaturalName(normalizeNamed));
        if (!isBlank(rsActionName)) {
            LOG.trace("{}{}.name = {}", keyPrefix, normalizeNamed, rsActionName);
            action.setName(rsActionName);
        }

        doConfigureAction(action, controller, normalizeNamed, keyPrefix);

        action.initialize();

        return action;
    }

    protected abstract void doConfigureAction(@Nonnull Action action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix);

    @Nonnull
    protected abstract Action createControllerAction(@Nonnull GriffonController controller, @Nonnull String actionName);

    @Nonnull
    public String normalizeName(@Nonnull String actionName) {
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }
        return uncapitalize(actionName);
    }

    @Nullable
    protected String msg(@Nonnull String key, @Nonnull String actionName, @Nonnull String subkey, @Nullable String defaultValue) {
        try {
            return getMessageSource().getMessage(key + actionName + "." + subkey);
        } catch (NoSuchMessageException nsme) {
            return getMessageSource().getMessage("application.action." + actionName + "." + subkey, defaultValue);
        }
    }

    private static class ActionCache {
        private final Map<WeakReference<GriffonController>, Map<String, Action>> cache = new ConcurrentHashMap<>();

        @Nonnull
        public Map<String, Action> get(@Nonnull GriffonController controller) {
            synchronized (cache) {
                for (Map.Entry<WeakReference<GriffonController>, Map<String, Action>> entry : cache.entrySet()) {
                    GriffonController test = entry.getKey().get();
                    if (test == controller) {
                        return entry.getValue();
                    }
                }
            }
            return Collections.emptyMap();
        }

        public void set(@Nonnull GriffonController controller, @Nonnull Map<String, Action> actions) {
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

            cache.put(new WeakReference<>(controller), actions);
        }
    }
}
