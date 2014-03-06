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

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonControllerClass;
import griffon.core.artifact.GriffonModelClass;
import griffon.core.artifact.GriffonViewClass;
import griffon.core.controller.Action;
import griffon.core.controller.MissingControllerActionException;
import griffon.core.mvc.MVCGroup;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.exceptions.MVCGroupConfigurationException;
import griffon.plugins.scaffolding.CommandObject;
import griffon.plugins.scaffolding.CommandObjectDisplayHandler;
import griffon.plugins.scaffolding.ScaffoldingContext;
import griffon.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.plugins.scaffolding.ScaffoldingUtils.mvcMemberCodes;
import static griffon.plugins.scaffolding.ScaffoldingUtils.qualifyActionValidatable;
import static griffon.plugins.scaffolding.ScaffoldingUtils.safeLoadClass;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultCommandObjectDisplayHandler implements CommandObjectDisplayHandler {
    private final Logger LOG = LoggerFactory.getLogger(DefaultCommandObjectDisplayHandler.class);
    private final GriffonApplication application;
    private final Map<String, ScaffoldingContext> contexts = new ConcurrentHashMap<>();

    @Inject
    public DefaultCommandObjectDisplayHandler(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
    }

    @Nonnull
    @Override
    public GriffonApplication getApplication() {
        return application;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void display(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull CommandObject commandObject) {
        requireNonNull(controller, "Argument 'controller' cannot be null");
        requireNonBlank(actionName, "Argument 'actionName' cannot be blank");
        requireNonNull(commandObject, "Argument 'commandObject' cannot be null");

        MVCGroupConfiguration mvcGroupConfiguration = fetchMVCGroupConfiguration(controller, actionName, commandObject);
        ScaffoldingContext scaffoldingContext = fetchScaffoldingContext(controller, actionName, commandObject);
        MVCGroup mvcGroup = mvcGroupConfiguration.create(CollectionUtils.<String, Object>map()
            .e("scaffoldingContext", scaffoldingContext));
        configureContext(scaffoldingContext, mvcGroup);
        Action showAction = application.getActionManager().actionFor(mvcGroup.getController(), "show");
        try {
            if (showAction != null) {
                showAction.execute();
                mvcGroup.destroy();
            } else {
                LOG.error("Missing action 'show' in controller {}", mvcGroupConfiguration.getMembers().get(GriffonControllerClass.TYPE));
                throw new MissingControllerActionException(controller.getClass(), actionName);
            }
        } finally {
            scaffoldingContext.dispose();
        }
    }

    protected void configureContext(@Nonnull ScaffoldingContext scaffoldingContext, @Nonnull MVCGroup mvcGroup) {

    }

    @Nonnull
    protected ScaffoldingContext fetchScaffoldingContext(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull CommandObject commandObject) {
        String fqCommandName = qualifyActionValidatable(controller, actionName, commandObject);

        ScaffoldingContext scaffoldingContext = contexts.get(fqCommandName);
        if (scaffoldingContext == null) {
            scaffoldingContext = newScaffoldingContext();
            scaffoldingContext.setActionName(actionName);
            contexts.put(fqCommandName, scaffoldingContext);
        }
        scaffoldingContext.setController(controller);
        scaffoldingContext.setValidateable(commandObject);

        return scaffoldingContext;
    }

    @Nonnull
    protected ScaffoldingContext newScaffoldingContext() {
        return application.getInjector().getInstance(ScaffoldingContext.class);
    }

    @Nonnull
    protected MVCGroupConfiguration fetchMVCGroupConfiguration(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull CommandObject commandObject) {
        String fqCommandName = qualifyActionValidatable(controller, actionName, commandObject);

        LOG.debug("Searching MVCGroupConfiguration for {}", fqCommandName);

        MVCGroupConfiguration mvcGroupConfiguration = null;
        try {
            mvcGroupConfiguration = application.getMvcGroupManager().findConfiguration(fqCommandName);
        } catch (MVCGroupConfigurationException e) {
            LOG.debug("Resolving MVCGroupConfiguration for {}", fqCommandName);
            mvcGroupConfiguration = resolveMVCGroupConfiguration(controller, actionName, commandObject);
        }
        return mvcGroupConfiguration;
    }

    @Nonnull
    protected MVCGroupConfiguration resolveMVCGroupConfiguration(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull CommandObject commandObject) {
        String mtemplate = resolveMember(controller, actionName, commandObject, GriffonModelClass.TRAILING);
        String vtemplate = resolveMember(controller, actionName, commandObject, GriffonViewClass.TRAILING);
        String ctemplate = resolveMember(controller, actionName, commandObject, GriffonControllerClass.TRAILING);

        String fqCommandName = qualifyActionValidatable(controller, actionName, commandObject);
        MVCGroupConfiguration mvcGroupConfiguration = application.getMvcGroupManager().newMVCGroupConfiguration(fqCommandName,
            CollectionUtils.<String, String>map()
                .e(GriffonModelClass.TYPE, mtemplate)
                .e(GriffonViewClass.TYPE, vtemplate)
                .e(GriffonControllerClass.TYPE, ctemplate),
            Collections.<String, Object>emptyMap());
        application.getMvcGroupManager().addConfiguration(mvcGroupConfiguration);
        return mvcGroupConfiguration;
    }

    @Nonnull
    protected String resolveMember(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull CommandObject commandObject, @Nonnull String suffix) {
        LOG.debug("  Resolving {} member for {}", suffix, qualifyActionValidatable(controller, actionName, commandObject));

        for (String code : mvcMemberCodes(controller, actionName, commandObject, suffix)) {
            LOG.debug("    Resolving template: {}", code);
            Class memberClass = safeLoadClass(code);
            if (memberClass != null) return memberClass.getName();
        }

        LOG.warn("  Could not resolve {} member for {}", suffix, qualifyActionValidatable(controller, actionName, commandObject));

        throw new IllegalArgumentException("Could not resolve " + suffix + " member for " + qualifyActionValidatable(controller, actionName, commandObject));
    }
}
