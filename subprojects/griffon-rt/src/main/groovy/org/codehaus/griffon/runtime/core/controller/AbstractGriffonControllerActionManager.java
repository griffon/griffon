/*
 * Copyright 2007-2012 the original author or authors.
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
import griffon.core.controller.GriffonControllerAction;
import griffon.core.controller.GriffonControllerActionManager;
import griffon.core.i18n.NoSuchMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonApplicationUtils.isMacOSX;
import static griffon.util.GriffonNameUtils.*;
import static org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation.castToBoolean;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public abstract class AbstractGriffonControllerActionManager implements GriffonControllerActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractGriffonControllerActionManager.class);
    private final ActionCache actionCache = new ActionCache();
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
        if (actions == null) {
            actions = Collections.emptyMap();
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
            Map<String, GriffonControllerAction> actions = actionCache.get(controller);
            if (actions == null) {
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
            action.setEnabled(castToBoolean(rsEnabled));
        }

        String rsSelected = msg(keyPrefix, normalizeNamed, "selected", "false");
        if (!isBlank(rsSelected)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".selected = " + rsSelected);
            }
            action.setSelected(castToBoolean(rsSelected));
        }

        return action;
    }

    protected abstract GriffonControllerAction createControllerAction(GriffonController controller, String actionName);

    public String normalizeName(String actionName) {
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }
        return uncapitalize(actionName);
    }

    private String msg(String key, String actionName, String subkey, String defaultValue) {
        try {
            return app.getMessage(key + actionName + "." + subkey);
        } catch (NoSuchMessageException nsme) {
            return app.getMessage("application.action." + actionName + "." + subkey, defaultValue);
        }
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
            return null;
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
