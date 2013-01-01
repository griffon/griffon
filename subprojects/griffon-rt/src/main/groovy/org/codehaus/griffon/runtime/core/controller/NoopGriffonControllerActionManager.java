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
import griffon.core.controller.GriffonControllerAction;
import griffon.core.controller.GriffonControllerActionManager;

import java.util.Collections;
import java.util.Map;

import static griffon.util.GriffonNameUtils.uncapitalize;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class NoopGriffonControllerActionManager implements GriffonControllerActionManager {
    private final GriffonApplication app;

    public NoopGriffonControllerActionManager(GriffonApplication app) {
        this.app = app;
    }

    @Override
    public Map<String, GriffonControllerAction> actionsFor(GriffonController controller) {
        return Collections.emptyMap();
    }

    @Override
    public GriffonControllerAction actionFor(GriffonController controller, String actionName) {
        return null;
    }

    @Override
    public void createActions(GriffonController controller) {
        // empty
    }

    @Override
    public String normalizeName(String actionName) {
        if (actionName.endsWith(ACTION)) {
            actionName = actionName.substring(0, actionName.length() - ACTION.length());
        }
        return uncapitalize(actionName);
    }

    @Override
    public GriffonApplication getApp() {
        return app;
    }
}
