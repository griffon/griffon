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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static griffon.util.GriffonApplicationUtils.isMacOSX;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.TypeUtils.castToBoolean;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class SwingActionManager extends AbstractActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(SwingActionManager.class);

    @Inject
    public SwingActionManager(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Override
    protected void doConfigureAction(@Nonnull Action action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        SwingGriffonControllerAction swingAction = (SwingGriffonControllerAction) action;

        String rsAccelerator = msg(keyPrefix, normalizeNamed, "accelerator", "");
        if (!isBlank(rsAccelerator)) {
            //noinspection ConstantConditions
            if (!isMacOSX() && rsAccelerator.contains("meta") && !rsAccelerator.contains("ctrl")) {
                rsAccelerator = rsAccelerator.replace("meta", "ctrl");
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".accelerator = " + rsAccelerator);
            }
            swingAction.setAccelerator(rsAccelerator);
        }

        String rsCommand = msg(keyPrefix, normalizeNamed, "command", "");
        if (!isBlank(rsCommand)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".command = " + rsCommand);
            }
            swingAction.setCommand(rsCommand);
        }

        String rsShortDescription = msg(keyPrefix, normalizeNamed, "short_description", "");
        if (!isBlank(rsShortDescription)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".short_description = " + rsShortDescription);
            }
            swingAction.setShortDescription(rsShortDescription);
        }

        String rsLongDescription = msg(keyPrefix, normalizeNamed, "long_description", "");
        if (!isBlank(rsLongDescription)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".long_description = " + rsLongDescription);
            }
            swingAction.setLongDescription(rsLongDescription);
        }

        String rsMnemonic = msg(keyPrefix, normalizeNamed, "mnemonic", "");
        if (!isBlank(rsMnemonic)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".mnemonic = " + rsMnemonic);
            }
            swingAction.setMnemonic(rsMnemonic);
        }

        String rsSmallIcon = msg(keyPrefix, normalizeNamed, "small_icon", "");
        if (!isBlank(rsSmallIcon)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".small_icon = " + rsSmallIcon);
            }
            swingAction.setSmallIcon(rsSmallIcon);
        }

        String rsLargeIcon = msg(keyPrefix, normalizeNamed, "large_icon", "");
        if (!isBlank(rsLargeIcon)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".large_icon = " + rsLargeIcon);
            }
            swingAction.setLargeIcon(rsLargeIcon);
        }

        String rsEnabled = msg(keyPrefix, normalizeNamed, "enabled", "true");
        if (!isBlank(rsEnabled)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".enabled = " + rsEnabled);
            }
            swingAction.setEnabled(castToBoolean(rsEnabled));
        }

        String rsSelected = msg(keyPrefix, normalizeNamed, "selected", "false");
        if (!isBlank(rsSelected)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".selected = " + rsSelected);
            }
            swingAction.setSelected(castToBoolean(rsSelected));
        }
    }

    @Nonnull
    @Override
    protected Action createControllerAction(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return new SwingGriffonControllerAction(getUiThreadManager(), this, controller, actionName);
    }
}
