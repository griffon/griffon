/*
 * Copyright 2008-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.core.controller;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.exceptions.InstanceMethodInvocationException;
import org.apache.pivot.wtk.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static griffon.util.GriffonClassUtils.EMPTY_ARGS;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.TypeUtils.castToBoolean;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class PivotActionManager extends AbstractActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(PivotActionManager.class);

    @Inject
    public PivotActionManager(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    @Override
    protected Action createControllerAction(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return new PivotGriffonControllerAction(getUiThreadManager(), this, controller, actionName);
    }

    @Override
    protected void doConfigureAction(@Nonnull Action action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        PivotGriffonControllerAction swingAction = (PivotGriffonControllerAction) action;

        String rsDescription = msg(keyPrefix, normalizeNamed, "description", "");
        if (!isBlank(rsDescription)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".description = " + rsDescription);
            }
            swingAction.setDescription(rsDescription);
        }

        String rsEnabled = msg(keyPrefix, normalizeNamed, "enabled", "true");
        if (!isBlank(rsEnabled)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".enabled = " + rsEnabled);
            }
            swingAction.setEnabled(castToBoolean(rsEnabled));
        }
    }

    @Override
    protected void doInvokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] updatedArgs) {
        try {
            invokeExactInstanceMethod(controller, actionName, updatedArgs);
        } catch (InstanceMethodInvocationException imie) {
            if (imie.getCause() instanceof NoSuchMethodException) {
                // try again but this time remove the 1st arg if it's
                // descendant of org.apache.pivot.wtk.Component
                if (updatedArgs.length == 1 && updatedArgs[0] != null && Component.class.isAssignableFrom(updatedArgs[0].getClass())) {
                    invokeExactInstanceMethod(controller, actionName, EMPTY_ARGS);
                } else {
                    throw imie;
                }
            } else {
                throw imie;
            }
        }
    }
}
