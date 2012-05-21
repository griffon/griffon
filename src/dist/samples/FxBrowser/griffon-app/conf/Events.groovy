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
 * limitations under the License.
 */

import griffon.core.GriffonController
import griffon.core.GriffonControllerClass
import griffon.core.MVCGroup
import griffon.core.MVCGroupConfiguration
import griffon.plugins.actions.ActionManager
import groovyx.javafx.appsupport.Action

onNewInstance = { Class klass, String type, instance ->
    if (GriffonControllerClass.TYPE == type) {
        ActionManager.instance.createActions(instance)
    }
}

onInitializeMVCGroup = { MVCGroupConfiguration config, MVCGroup group ->
    GriffonController controller = group.controller
    if (controller == null) return
    FactoryBuilderSupport builder = group.builder
    Map<String, Action> actions = ActionManager.instance.actionsFor(controller)
    for (Map.Entry<String, Action> action : actions.entrySet()) {
        String actionKey = ActionManager.getInstance().normalizeName(action.getKey()) + ActionManager.ACTION;
        builder.setVariable(actionKey, action.getValue());
    }
}