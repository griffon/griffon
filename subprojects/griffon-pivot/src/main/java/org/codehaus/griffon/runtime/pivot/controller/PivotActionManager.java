/*
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.pivot.controller;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionMetadataFactory;
import griffon.exceptions.InstanceMethodInvocationException;
import org.apache.pivot.wtk.Component;
import org.codehaus.griffon.runtime.core.controller.AbstractActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static griffon.util.GriffonClassUtils.EMPTY_ARGS;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonNameUtils.getNaturalName;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.TypeUtils.castToBoolean;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class PivotActionManager extends AbstractActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(PivotActionManager.class);

    private static final String EMPTY_STRING = "";
    private static final String DOT = ".";
    private static final String EQUALS = " = ";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ENABLED = "enabled";

    @Inject
    public PivotActionManager(@Nonnull GriffonApplication application, @Nonnull ActionFactory actionFactory, @Nonnull ActionMetadataFactory actionMetadataFactory) {
        super(application, actionFactory, actionMetadataFactory);
    }

    @Override
    protected void doConfigureAction(@Nonnull final Action action, @Nonnull final GriffonController controller, @Nonnull final String normalizeNamed, @Nonnull final String keyPrefix) {
        controller.getApplication().addPropertyChangeListener(GriffonApplication.PROPERTY_LOCALE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                configureAction((PivotGriffonControllerAction) action, controller, normalizeNamed, keyPrefix);
            }
        });
        configureAction((PivotGriffonControllerAction) action, controller, normalizeNamed, keyPrefix);
    }

    protected void configureAction(@Nonnull PivotGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        resolveName(action, controller, normalizeNamed, keyPrefix);
        resolveDescription(action, controller, normalizeNamed, keyPrefix);
        resolveEnabled(action, controller, normalizeNamed, keyPrefix);
    }

    protected void resolveName(@Nonnull PivotGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsActionName = msg(keyPrefix, normalizeNamed, KEY_NAME, getNaturalName(normalizeNamed));
        if (!isBlank(rsActionName)) {
            trace(keyPrefix + normalizeNamed, KEY_NAME, rsActionName);
            action.setName(rsActionName);
        }
    }

    protected void resolveDescription(@Nonnull PivotGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsDescription = msg(keyPrefix, normalizeNamed, KEY_DESCRIPTION, EMPTY_STRING);
        if (!isBlank(rsDescription)) {
            trace(keyPrefix + normalizeNamed, KEY_DESCRIPTION, rsDescription);
            action.setDescription(rsDescription);
        }
    }

    protected void resolveEnabled(@Nonnull PivotGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsEnabled = msg(keyPrefix, normalizeNamed, KEY_ENABLED, "true");
        if (!isBlank(rsEnabled)) {
            trace(keyPrefix + normalizeNamed, KEY_ENABLED, rsEnabled);
            action.setEnabled(castToBoolean(rsEnabled));
        }
    }

    @Nullable
    @Override
    protected Object doInvokeAction(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] updatedArgs) {
        try {
            return invokeExactInstanceMethod(controller, actionName, updatedArgs);
        } catch (InstanceMethodInvocationException imie) {
            if (imie.getCause() instanceof NoSuchMethodException) {
                // try again but this time remove the 1st arg if it's
                // descendant of org.apache.pivot.wtk.Component
                if (updatedArgs.length == 1 && updatedArgs[0] != null && Component.class.isAssignableFrom(updatedArgs[0].getClass())) {
                    return invokeExactInstanceMethod(controller, actionName, EMPTY_ARGS);
                } else {
                    throw imie;
                }
            } else {
                throw imie;
            }
        }
    }

    protected void trace(@Nonnull String actionKey, @Nonnull String key, @Nonnull String value) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(actionKey + DOT + key + EQUALS + value);
        }
    }
}
