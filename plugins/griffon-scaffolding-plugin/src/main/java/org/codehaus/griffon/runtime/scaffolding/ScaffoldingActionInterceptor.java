/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.scaffolding;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.MissingControllerActionException;
import griffon.plugins.scaffolding.CommandObject;
import griffon.plugins.scaffolding.CommandObjectDisplayHandler;
import org.codehaus.griffon.runtime.core.controller.AbstractActionInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class ScaffoldingActionInterceptor extends AbstractActionInterceptor {
    private final Logger LOG = LoggerFactory.getLogger(ScaffoldingActionInterceptor.class);
    private final Map<String, Class<? extends CommandObject>> commandObjectMappings = new ConcurrentHashMap<>();
    private CommandObjectDisplayHandler commandObjectDisplayHandler;

    @Inject
    public ScaffoldingActionInterceptor(@Nonnull CommandObjectDisplayHandler commandObjectDisplayHandler) {
        this.commandObjectDisplayHandler = requireNonNull(commandObjectDisplayHandler, "Argument 'commandObjectDisplayHandler' cannot be null");
    }

    @Override
    public void configure(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Method method) {
        configureAction(controller, actionName, method.getParameterTypes());
    }

    @Nonnull
    @Override
    public Object[] before(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        String fqActionName = qualifyActionName(controller, actionName);
        Class<? extends CommandObject> commandObjectClass = commandObjectMappings.get(fqActionName);
        if (commandObjectClass != null) {
            LOG.debug("Instantiating command object of type {} for action {}", commandObjectClass.getName(), fqActionName);

            CommandObject commandObject = controller.getApplication().getArtifactManager().newInstance(commandObjectClass);
            try {
                commandObjectDisplayHandler.display(controller, actionName, commandObject);
            } catch (MissingControllerActionException mcae) {
                throw abortActionExecution();
            }
            if (commandObject.getErrors().hasErrors()) {
                throw abortActionExecution();
            }
            args = new Object[]{commandObject};
        }
        return args;
    }

    @SuppressWarnings("unchecked")
    private void configureAction(GriffonController controller, String actionName, Class<?>[] parameterTypes) {
        String fqActionName = qualifyActionName(controller, actionName);
        if (parameterTypes != null && parameterTypes.length == 1 && CommandObject.class.isAssignableFrom(parameterTypes[0])) {
            LOG.debug("Action {} requires a command object of type {}", fqActionName, parameterTypes[0].getName());
            commandObjectMappings.put(fqActionName, (Class<? extends CommandObject>) parameterTypes[0]);
        }
    }
}
