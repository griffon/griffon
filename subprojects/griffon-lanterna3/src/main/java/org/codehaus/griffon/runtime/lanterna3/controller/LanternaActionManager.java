/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.lanterna3.controller;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionMetadataFactory;
import org.codehaus.griffon.runtime.core.controller.AbstractActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static griffon.util.GriffonNameUtils.getNaturalName;
import static griffon.util.GriffonNameUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class LanternaActionManager extends AbstractActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(LanternaActionManager.class);

    private static final String DOT = ".";
    private static final String EQUALS = " = ";
    private static final String KEY_NAME = "name";

    @Inject
    public LanternaActionManager(@Nonnull GriffonApplication application, @Nonnull ActionFactory actionFactory, @Nonnull ActionMetadataFactory actionMetadataFactory) {
        super(application, actionFactory, actionMetadataFactory);
    }

    @Override
    protected void doConfigureAction(@Nonnull final Action action, @Nonnull final GriffonController controller, @Nonnull final String normalizeNamed, @Nonnull final String keyPrefix) {
        controller.getApplication().addPropertyChangeListener(GriffonApplication.PROPERTY_LOCALE, evt -> configureAction((LanternaGriffonControllerAction) action, controller, normalizeNamed, keyPrefix));
        configureAction((LanternaGriffonControllerAction) action, controller, normalizeNamed, keyPrefix);
    }

    protected void configureAction(@Nonnull LanternaGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        resolveName(action, controller, normalizeNamed, keyPrefix);
    }

    protected void resolveName(@Nonnull LanternaGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsActionName = msg(keyPrefix, normalizeNamed, KEY_NAME, getNaturalName(normalizeNamed));
        if (isNotBlank(rsActionName)) {
            trace(keyPrefix + normalizeNamed, KEY_NAME, rsActionName);
            action.setName(rsActionName);
        }
    }

    protected void trace(@Nonnull String actionKey, @Nonnull String key, @Nonnull String value) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(actionKey + DOT + key + EQUALS + value);
        }
    }
}
