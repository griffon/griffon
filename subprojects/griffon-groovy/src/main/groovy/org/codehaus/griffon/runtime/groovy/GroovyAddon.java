/*
 * Copyright 2008-2016 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionManager;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("groovy")
public class GroovyAddon extends AbstractGriffonAddon {
    private GriffonApplication application;

    public void init(@Nonnull GriffonApplication application) {
        this.application = application;
    }

    public void onInitializeMVCGroup(@Nonnull MVCGroupConfiguration configuration, @Nonnull MVCGroup group) {
        GriffonController controller = group.getController();
        if (controller == null) return;
        FactoryBuilderSupport builder = (FactoryBuilderSupport) group.getMember("builder");
        if (builder == null) return;
        Map<String, Action> actions = application.getActionManager().actionsFor(controller);
        for (Map.Entry<String, Action> action : actions.entrySet()) {
            String actionKey = application.getActionManager().normalizeName(action.getKey()) + ActionManager.ACTION;
            getLog().trace("Adding action {} to {}:{}:builder", actionKey, configuration.getMvcType(), group.getMvcId());
            builder.setVariable(actionKey, action.getValue().getToolkitAction());
        }
    }
}
