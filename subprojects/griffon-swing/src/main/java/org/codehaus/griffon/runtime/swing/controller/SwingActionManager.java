/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.swing.controller;

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

import static griffon.core.util.GriffonApplicationUtils.isMacOSX;
import static griffon.core.util.TypeUtils.castToBoolean;
import static griffon.util.StringUtils.getNaturalName;
import static griffon.util.StringUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SwingActionManager extends AbstractActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(SwingActionManager.class);
    private static final String KEY_SELECTED = "selected";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_LARGE_ICON = "large_icon";
    private static final String KEY_SMALL_ICON = "small_icon";
    private static final String KEY_LONG_DESCRIPTION = "long_description";
    private static final String KEY_SHORT_DESCRIPTION = "short_description";
    private static final String KEY_COMMAND = "command";
    private static final String KEY_ACCELERATOR = "accelerator";
    private static final String KEY_NAME = "name";
    private static final String KEY_MNEMONIC = "mnemonic";
    private static final String KEY_CTRL = "ctrl";
    private static final String KEY_META = "meta";
    private static final String EMPTY_STRING = "";
    private static final String DOT = ".";
    private static final String EQUALS = " = ";

    @Inject
    public SwingActionManager(@Nonnull GriffonApplication application, @Nonnull ActionFactory actionFactory, @Nonnull ActionMetadataFactory actionMetadataFactory) {
        super(application, actionFactory, actionMetadataFactory);
    }

    @Override
    protected void doConfigureAction(@Nonnull final Action action, @Nonnull final GriffonController controller, @Nonnull final String normalizeNamed, @Nonnull final String keyPrefix) {
        controller.getApplication().addPropertyChangeListener(GriffonApplication.PROPERTY_LOCALE, evt -> configureAction((SwingGriffonControllerAction) action, controller, normalizeNamed, keyPrefix));
        configureAction((SwingGriffonControllerAction) action, controller, normalizeNamed, keyPrefix);
    }

    protected void configureAction(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        resolveName(action, controller, normalizeNamed, keyPrefix);
        resolveAccelerator(action, controller, normalizeNamed, keyPrefix);
        resolveCommand(action, controller, normalizeNamed, keyPrefix);
        resolveShortDescription(action, controller, normalizeNamed, keyPrefix);
        resolveLongDescription(action, controller, normalizeNamed, keyPrefix);
        resolveMnemonic(action, controller, normalizeNamed, keyPrefix);
        resolveSmallIcon(action, controller, normalizeNamed, keyPrefix);
        resolveLargeIcon(action, controller, normalizeNamed, keyPrefix);
        resolveEnabled(action, controller, normalizeNamed, keyPrefix);
        resolveSelected(action, controller, normalizeNamed, keyPrefix);
    }

    protected void resolveName(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsActionName = msg(keyPrefix, normalizeNamed, KEY_NAME, getNaturalName(normalizeNamed));
        if (isNotBlank(rsActionName)) {
            trace(keyPrefix + normalizeNamed, KEY_NAME, rsActionName);
            action.setName(rsActionName);
        }
    }

    protected void resolveAccelerator(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsAccelerator = msg(keyPrefix, normalizeNamed, KEY_ACCELERATOR, EMPTY_STRING);
        if (isNotBlank(rsAccelerator)) {
            //noinspection ConstantConditions
            if (!isMacOSX() && rsAccelerator.contains(KEY_META) && !rsAccelerator.contains(KEY_CTRL)) {
                rsAccelerator = rsAccelerator.replace(KEY_META, KEY_CTRL);
            }
            trace(keyPrefix + normalizeNamed, KEY_ACCELERATOR, rsAccelerator);
            action.setAccelerator(rsAccelerator);
        }
    }

    protected void resolveCommand(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsCommand = msg(keyPrefix, normalizeNamed, KEY_COMMAND, EMPTY_STRING);
        if (isNotBlank(rsCommand)) {
            trace(keyPrefix + normalizeNamed, KEY_COMMAND, rsCommand);
            action.setCommand(rsCommand);
        }
    }

    protected void resolveShortDescription(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsShortDescription = msg(keyPrefix, normalizeNamed, KEY_SHORT_DESCRIPTION, EMPTY_STRING);
        if (isNotBlank(rsShortDescription)) {
            trace(keyPrefix + normalizeNamed, KEY_SHORT_DESCRIPTION, rsShortDescription);
            action.setShortDescription(rsShortDescription);
        }
    }

    protected void resolveLongDescription(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsLongDescription = msg(keyPrefix, normalizeNamed, KEY_LONG_DESCRIPTION, EMPTY_STRING);
        if (isNotBlank(rsLongDescription)) {
            trace(keyPrefix + normalizeNamed, KEY_LONG_DESCRIPTION, rsLongDescription);
            action.setLongDescription(rsLongDescription);
        }
    }

    protected void resolveMnemonic(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsMnemonic = msg(keyPrefix, normalizeNamed, KEY_MNEMONIC, EMPTY_STRING);
        if (isNotBlank(rsMnemonic)) {
            trace(keyPrefix + normalizeNamed, KEY_MNEMONIC, rsMnemonic);
            action.setMnemonic(rsMnemonic);
        }
    }

    protected void resolveSmallIcon(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsSmallIcon = msg(keyPrefix, normalizeNamed, KEY_SMALL_ICON, EMPTY_STRING);
        if (isNotBlank(rsSmallIcon)) {
            trace(keyPrefix + normalizeNamed, KEY_SMALL_ICON, rsSmallIcon);
            action.setSmallIcon(rsSmallIcon);
        }
    }

    protected void resolveLargeIcon(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsLargeIcon = msg(keyPrefix, normalizeNamed, KEY_LARGE_ICON, EMPTY_STRING);
        if (isNotBlank(rsLargeIcon)) {
            trace(keyPrefix + normalizeNamed, KEY_LARGE_ICON, rsLargeIcon);
            action.setLargeIcon(rsLargeIcon);
        }
    }

    protected void resolveEnabled(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsEnabled = msg(keyPrefix, normalizeNamed, KEY_ENABLED, "true");
        if (isNotBlank(rsEnabled)) {
            trace(keyPrefix + normalizeNamed, KEY_ENABLED, rsEnabled);
            action.setEnabled(castToBoolean(rsEnabled));
        }
    }

    protected void resolveSelected(@Nonnull SwingGriffonControllerAction action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        String rsSelected = msg(keyPrefix, normalizeNamed, KEY_SELECTED, "false");
        if (isNotBlank(rsSelected)) {
            trace(keyPrefix + normalizeNamed, KEY_SELECTED, rsSelected);
            action.setSelected(castToBoolean(rsSelected));
        }
    }

    protected void trace(@Nonnull String actionKey, @Nonnull String key, @Nonnull String value) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(actionKey + DOT + key + EQUALS + value);
        }
    }
}
