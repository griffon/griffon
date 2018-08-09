/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core.controller;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.Configuration;
import griffon.core.Context;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonControllerClass;
import griffon.core.controller.AbortActionExecution;
import griffon.core.controller.Action;
import griffon.core.controller.ActionExecutionStatus;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionHandler;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadata;
import griffon.core.controller.ActionMetadataFactory;
import griffon.core.controller.ActionParameter;
import griffon.core.controller.ControllerAction;
import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;
import griffon.core.mvc.MVCGroup;
import griffon.core.threading.UIThreadManager;
import griffon.exceptions.GriffonException;
import griffon.exceptions.InstanceMethodInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.application.threading.Threading;
import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.AnnotationUtils.findAnnotation;
import static griffon.util.AnnotationUtils.isAnnotatedWith;
import static griffon.util.CollectionUtils.reverse;
import static griffon.util.GriffonClassUtils.EMPTY_ARGS;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonClassUtils.invokeInstanceMethod;
import static griffon.util.GriffonNameUtils.capitalize;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static griffon.util.GriffonNameUtils.uncapitalize;
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
    private static final String KEY_THREADING_DEFAULT = "controller.threading.default";
    private static final String KEY_DISABLE_THREADING_INJECTION = "griffon.disable.threading.injection";
    private static final String ERROR_CONTROLLER_NULL = "Argument 'controller' must not be null";
    private static final String ERROR_ACTION_NAME_BLANK = "Argument 'actionName' must not be blank";
    private static final String ERROR_ACTION_HANDLER_NULL = "Argument 'actionHandler' must not be null";
    private static final String ERROR_ACTION_NULL = "Argument 'action' must not be null";
    private static final String ERROR_METHOD_NULL = "Argument 'method' must not be null";

    private final ActionCache actionCache = new ActionCache();
    private final Map<String, Threading.Policy> threadingPolicies = new ConcurrentHashMap<>();
    private final List<ActionHandler> handlers = new CopyOnWriteArrayList<>();

    private final GriffonApplication application;
    private final ActionFactory actionFactory;
    private final ActionMetadataFactory actionMetadataFactory;

    @Inject
    public AbstractActionManager(@Nonnull GriffonApplication application, @Nonnull ActionFactory actionFactory, @Nonnull ActionMetadataFactory actionMetadataFactory) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
        this.actionFactory = requireNonNull(actionFactory, "Argument 'actionFactory' must not be null");
        this.actionMetadataFactory = requireNonNull(actionMetadataFactory, "Argument 'actionMetadataFactory' must not be null");
    }

    @Nullable
    private static Method findActionAsMethod(@Nonnull GriffonController controller, @Nonnull String actionName) {
        for (Method method : controller.getTypeClass().getMethods()) {
            if (actionName.equals(method.getName()) &&
                isPublic(method.getModifiers()) &&
                !isStatic(method.getModifiers()) &&
                (isAnnotatedWith(method, ControllerAction.class, true) || method.getReturnType() == Void.TYPE)) {
                return method;
            }
        }
        return null;
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
        return Collections.unmodifiableMap(actions);
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
            Method method = findActionAsMethod(controller, actionName);
            if (method == null) {
                throw new GriffonException(controller.getTypeClass().getCanonicalName() + " does not define an action named " + actionName);
            }

            Action action = createAndConfigureAction(controller, actionName, method);

            final String qualifiedActionName = action.getFullyQualifiedName();
            for (ActionHandler handler : handlers) {
                LOG.debug("Configuring action {} with {}", qualifiedActionName, handler);
                handler.configure(action, method);
            }

            Map<String, Action> actions = actionCache.get(controller);
            if (actions.isEmpty()) {
                actions = new TreeMap<>();
                actionCache.set(controller, actions);
            }
            String actionKey = normalizeName(actionName);
            LOG.trace("Action for {} stored as {}", qualifiedActionName, actionKey);
            actions.put(actionKey, action);
        }
    }

    @Override
    public void updateActions() {
        for (Action action : actionCache.allActions()) {
            updateAction(action);
        }
    }

    @Override
    public void updateActions(@Nonnull GriffonController controller) {
        for (Action action : actionsFor(controller).values()) {
            updateAction(action);
        }
    }

    @Override
    public void updateAction(@Nonnull Action action) {
        requireNonNull(action, ERROR_ACTION_NULL);

        final String qualifiedActionName = action.getFullyQualifiedName();
        for (ActionHandler handler : handlers) {
            LOG.trace("Calling {}.update() on {}", handler, qualifiedActionName);
            handler.update(action);
        }
    }

    @Override
    public void updateAction(@Nonnull GriffonController controller, @Nonnull String actionName) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        updateAction(actionFor(controller, actionName));
    }

    @Override
    public void invokeAction(@Nonnull final Action action, @Nonnull final Object... args) {
        requireNonNull(action, ERROR_ACTION_NULL);
        final GriffonController controller = action.getController();
        final String actionName = action.getActionName();
        Runnable runnable = () -> {
            Object result = null;
            Object[] updatedArgs = args;
            List<ActionHandler> copy = new ArrayList<>(handlers);
            List<ActionHandler> invokedHandlers = new ArrayList<>();

            final String qualifiedActionName = action.getFullyQualifiedName();
            ActionExecutionStatus status = ActionExecutionStatus.OK;

            try {
                LOG.trace("Resolving contextual arguments for " + qualifiedActionName);
                updatedArgs = injectFromContext(action, updatedArgs);
            } catch (IllegalStateException ise) {
                LOG.debug("Execution of " + qualifiedActionName + " was aborted", ise);
                throw ise;
            }

            if (LOG.isDebugEnabled()) {
                int size = copy.size();
                LOG.debug("Executing " + size + " handler" + (size != 1 ? "s" : "") + " for " + qualifiedActionName);
            }

            for (ActionHandler handler : copy) {
                invokedHandlers.add(handler);
                try {
                    LOG.trace("Calling {}.before() on {}", handler, qualifiedActionName);
                    updatedArgs = handler.before(action, updatedArgs);
                } catch (AbortActionExecution aae) {
                    status = ActionExecutionStatus.ABORTED;
                    LOG.debug("Execution of {} was aborted by {}", qualifiedActionName, handler);
                    break;
                }
            }

            LOG.trace("Status before execution of {} is {}", qualifiedActionName, status);
            RuntimeException exception = null;
            boolean exceptionWasHandled = false;
            if (status == ActionExecutionStatus.OK) {
                try {
                    result = doInvokeAction(controller, actionName, updatedArgs);
                } catch (RuntimeException e) {
                    status = ActionExecutionStatus.EXCEPTION;
                    exception = (RuntimeException) sanitize(e);
                    LOG.warn("An exception occurred when executing {}", qualifiedActionName, exception);
                }
                LOG.trace("Status after execution of {} is {}", qualifiedActionName, status);

                if (exception != null) {
                    for (ActionHandler handler : reverse(invokedHandlers)) {
                        LOG.trace("Calling {}.exception() on {}", handler, qualifiedActionName);
                        exceptionWasHandled = handler.exception(exception, action, updatedArgs);
                    }
                }
            }

            for (ActionHandler handler : reverse(invokedHandlers)) {
                LOG.trace("Calling {}.after() on {}", handler, qualifiedActionName);
                result = handler.after(status, action, updatedArgs, result);
            }

            if (exception != null && !exceptionWasHandled) {
                // throw it again
                throw exception;
            }
        };
        invokeAction(controller, actionName, runnable);
    }

    @Nonnull
    private Object[] injectFromContext(@Nonnull Action action, @Nonnull Object[] args) {
        MVCGroup group = action.getController().getMvcGroup();
        if (group == null) {
            // This case only occurs during testing, when an artifact is
            // instantiated without a group
            return args;
        }

        Context context = group.getContext();
        ActionMetadata actionMetadata = action.getActionMetadata();
        if (actionMetadata.hasContextualArgs()) {
            Object[] newArgs = new Object[actionMetadata.getParameters().length];
            for (int i = 0; i < newArgs.length; i++) {
                ActionParameter param = actionMetadata.getParameters()[i];
                newArgs[i] = param.isContextual() ? context.get(param.getName()) : args[i];
                if (param.isContextual() && newArgs[i] != null) { context.put(param.getName(), newArgs[i]); }
                if (param.isContextual() && !param.isNullable() && newArgs[i] == null) {
                    throw new IllegalStateException("Could not find an instance of type " +
                        param.getType().getName() + " under key '" + param.getName() +
                        "' in the context of MVCGroup[" + group.getMvcType() + ":" + group.getMvcId() +
                        "] to be injected as argument " + i +
                        " at " + action.getFullyQualifiedName() + "(). Argument does not accept null values.");
                }
            }
            return newArgs;
        }

        return args;
    }

    public void invokeAction(@Nonnull final GriffonController controller, @Nonnull final String actionName, @Nonnull final Object... args) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        invokeAction(actionFor(controller, actionName), args);
    }

    @Nullable
    protected Object doInvokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] updatedArgs) {
        try {
            return invokeInstanceMethod(controller, actionName, updatedArgs);
        } catch (InstanceMethodInvocationException imie) {
            if (imie.getCause() instanceof NoSuchMethodException) {
                // try again but this time remove the 1st arg if it's
                // descendant of java.util.EventObject
                if (updatedArgs.length == 1 && updatedArgs[0] != null && EventObject.class.isAssignableFrom(updatedArgs[0].getClass())) {
                    return invokeExactInstanceMethod(controller, actionName, EMPTY_ARGS);
                } else {
                    throw imie;
                }
            } else {
                throw imie;
            }
        }
    }

    private void invokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Runnable runnable) {
        String fullQualifiedActionName = controller.getTypeClass().getName() + "." + actionName;
        Threading.Policy policy = threadingPolicies.get(fullQualifiedActionName);
        if (policy == null) {
            if (isThreadingDisabled(fullQualifiedActionName)) {
                policy = Threading.Policy.SKIP;
            } else {
                policy = resolveThreadingPolicy(controller, actionName);
            }
            threadingPolicies.put(fullQualifiedActionName, policy);
        }

        LOG.debug("Executing {} with policy {}", fullQualifiedActionName, policy);

        switch (policy) {
            case OUTSIDE_UITHREAD:
                getUiThreadManager().executeOutsideUI(runnable);
                break;
            case OUTSIDE_UITHREAD_ASYNC:
                getUiThreadManager().executeOutsideUIAsync(runnable);
                break;
            case INSIDE_UITHREAD_SYNC:
                getUiThreadManager().executeInsideUISync(runnable);
                break;
            case INSIDE_UITHREAD_ASYNC:
                getUiThreadManager().executeInsideUIAsync(runnable);
                break;
            case SKIP:
            default:
                runnable.run();
        }
    }

    @Nonnull
    protected Threading.Policy resolveThreadingPolicy(@Nonnull GriffonController controller, @Nonnull String actionName) {
        Method method = findActionAsMethod(controller, actionName);
        if (method != null) {
            Threading annotation = findAnnotation(method, Threading.class, true);
            return annotation == null ? resolveThreadingPolicy(controller) : annotation.value();
        }

        return Threading.Policy.OUTSIDE_UITHREAD;
    }

    @Nonnull
    protected Threading.Policy resolveThreadingPolicy(@Nonnull GriffonController controller) {
        Threading annotation = findAnnotation(controller.getTypeClass(), Threading.class, true);
        return annotation == null ? resolveThreadingPolicy() : annotation.value();
    }

    @Nonnull
    protected Threading.Policy resolveThreadingPolicy() {
        Object value = getConfiguration().get(KEY_THREADING_DEFAULT);
        if (value == null) {
            return Threading.Policy.OUTSIDE_UITHREAD;
        }

        if (value instanceof Threading.Policy) {
            return (Threading.Policy) value;
        }

        String policy = String.valueOf(value).toLowerCase();
        switch (policy) {
            case "sync":
            case "inside sync":
            case "inside uithread sync":
            case "inside_uithread_sync":
                return Threading.Policy.INSIDE_UITHREAD_SYNC;
            case "async":
            case "inside async":
            case "inside uithread async":
            case "inside_uithread_async":
                return Threading.Policy.INSIDE_UITHREAD_ASYNC;
            case "outside":
            case "outside uithread":
            case "outside_uithread":
                return Threading.Policy.OUTSIDE_UITHREAD;
            case "background":
            case "outside async":
            case "outside uithread async":
            case "outside_uithread_async":
                return Threading.Policy.OUTSIDE_UITHREAD_ASYNC;
            case "skip":
                return Threading.Policy.SKIP;
            default:
                throw new IllegalArgumentException("Value '" + policy + "' cannot be translated into " + Threading.Policy.class.getName());
        }
    }

    protected boolean isThreadingDisabled(@Nonnull String actionName) {
        if (getConfiguration().getAsBoolean(KEY_DISABLE_THREADING_INJECTION, false)) {
            return true;
        }

        Map<String, Object> settings = getConfiguration().asFlatMap();

        String keyName = KEY_THREADING + "." + actionName;
        while (!KEY_THREADING.equals(keyName)) {
            Object value = settings.get(keyName);
            keyName = keyName.substring(0, keyName.lastIndexOf('.'));
            if (value != null && !castToBoolean(value)) { return true; }
        }

        return false;
    }

    public void addActionHandler(@Nonnull ActionHandler actionHandler) {
        requireNonNull(actionHandler, ERROR_ACTION_HANDLER_NULL);
        if (handlers.contains(actionHandler)) {
            return;
        }
        handlers.add(actionHandler);
    }

    @Nonnull
    protected Action createAndConfigureAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Method method) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        requireNonNull(method, ERROR_METHOD_NULL);

        Action action = createControllerAction(controller, actionName, method);

        String normalizeNamed = capitalize(normalizeName(actionName));
        String keyPrefix = controller.getTypeClass().getName() + ".action.";

        doConfigureAction(action, controller, normalizeNamed, keyPrefix);

        action.initialize();

        return action;
    }

    protected abstract void doConfigureAction(@Nonnull Action action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix);

    @Nonnull
    protected Action createControllerAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Method method) {
        ActionMetadata actionMetadata = actionMetadataFactory.create(controller, actionName, method);
        return actionFactory.create(controller, actionMetadata);
    }

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
            return getMessageSource().getMessage(key + actionName + "." + subkey, application.getLocale());
        } catch (NoSuchMessageException nsme) {
            return getMessageSource().getMessage("application.action." + actionName + "." + subkey, application.getLocale(), defaultValue);
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

        public Collection<Action> allActions() {
            // create a copy to avoid CME
            List<Action> actions = new ArrayList<>();

            synchronized (cache) {
                for (Map<String, Action> map : cache.values()) {
                    actions.addAll(map.values());
                }
            }

            return actions;
        }
    }
}
