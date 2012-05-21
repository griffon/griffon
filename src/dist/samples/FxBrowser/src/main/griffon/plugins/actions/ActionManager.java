/*
 * Copyright 2012 the original author or authors.
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

package griffon.plugins.actions;

import griffon.core.GriffonController;
import griffon.core.GriffonControllerClass;
import griffon.plugins.i18n.ConstrainedMessageSource;
import griffon.plugins.i18n.MessageSource;
import griffon.plugins.i18n.MessageSourceHolder;
import griffon.plugins.i18n.NoSuchMessageException;
import groovyx.javafx.event.GroovyEventHandler;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import groovyx.javafx.appsupport.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.*;

/**
 * @author Andres Almiray
 */
public final class ActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ActionManager.class);
    public static final String ACTION = "Action";
    private static ActionManager INSTANCE;
    private Map<GriffonController, Map<String, Action>> actionCache = new LinkedHashMap<GriffonController, Map<String, Action>>();

    static {
        INSTANCE = new ActionManager();
    }

    public static ActionManager getInstance() {
        return INSTANCE;
    }

    private ActionManager() {
    }

    public Map<String, Action> actionsFor(GriffonController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller parameter is null!");
        }
        Map<String, Action> actions = actionCache.get(controller);
        if (actions == null) {
            actions = Collections.emptyMap();
            if (LOG.isTraceEnabled()) {
                LOG.trace("No actions defined for controller " + controller);
            }
        }
        return actions;
    }

    public Action actionFor(GriffonController controller, String actionName) {
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
            Action action = createAction(controller, actionName);
            Map<String, Action> actions = actionCache.get(controller);
            if (actions == null) {
                actions = new LinkedHashMap<String, Action>();
                actionCache.put(controller, actions);
            }
            String actionKey = normalizeName(actionName);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Action for " + controller.getClass().getName() + "." + actionName + " stored as " + actionKey);
            }
            actions.put(actionKey, action);
        }
    }

    public String normalizeName(String actionName) {
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }
        return uncapitalize(actionName);
    }

    private Action createAction(GriffonController controller, String actionName) {
        Action action = new Action();

        String normalizeNamed = capitalize(normalizeName(actionName));
        String prefix = controller.getClass().getName() + ".action.";

        String rsActionName = msg(controller, prefix, normalizeNamed, "name", getNaturalName(normalizeNamed));
        if (!isBlank(rsActionName)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(prefix + normalizeNamed + ".name = " + rsActionName);
            }
            action.setName(rsActionName);
        }

        String rsAccelerator = msg(controller, prefix, normalizeNamed, "accelerator", "");
        if (!isBlank(rsAccelerator)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(prefix + normalizeNamed + ".accelerator = " + rsAccelerator);
            }
            action.setAccelerator(rsAccelerator);
        }

        String rsDescription = msg(controller, prefix, normalizeNamed, "description", "");
        if (!isBlank(rsDescription)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(prefix + normalizeNamed + ".description = " + rsDescription);
            }
            action.setDescription(rsDescription);
        }

        String rsEnabled = msg(controller, prefix, normalizeNamed, "enabled", "true");
        if (!isBlank(rsEnabled)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(prefix + normalizeNamed + ".enabled = " + rsEnabled);
            }
            action.setEnabled(DefaultTypeTransformation.castToBoolean(rsEnabled));
        }

        String rsSelected = msg(controller, prefix, normalizeNamed, "selected", "");
        if (!isBlank(rsSelected)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(prefix + normalizeNamed + ".selected = " + rsSelected);
            }
            action.setSelected(DefaultTypeTransformation.castToBoolean(rsSelected));
        }

        String rsIcon = msg(controller, prefix, normalizeNamed, "icon", "");
        if (!isBlank(rsIcon)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(prefix + normalizeNamed + ".icon = " + rsIcon);
            }
            action.setIcon(rsIcon);
        }

        GroovyEventHandler eventHandler = new GroovyEventHandler("onAction");
        eventHandler.setClosure(new MethodClosure(controller, actionName));
        action.setOnAction(eventHandler);
        return action;
    }

    private static String msg(GriffonController controller, String key, String actionName, String subkey, String defaultValue) {
        MessageSource messageSource = MessageSourceHolder.getInstance().getMessageSource();
        if (messageSource instanceof ConstrainedMessageSource) {
            messageSource = MessageSourceHolder.getInstance().getMessageSource(controller.getClass());
        }

        try {
            return messageSource.getMessage(key + actionName + "." + subkey);
        } catch (NoSuchMessageException nsme) {
            return messageSource.getMessage("application.action." + actionName + "." + subkey, defaultValue);
        }
    }
}
