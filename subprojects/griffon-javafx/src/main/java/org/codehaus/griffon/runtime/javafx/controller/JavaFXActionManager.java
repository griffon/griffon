/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.javafx.controller;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import org.codehaus.griffon.runtime.core.controller.AbstractActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static griffon.util.GriffonApplicationUtils.isMacOSX;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.TypeUtils.castToBoolean;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class JavaFXActionManager extends AbstractActionManager {
    private static final Logger LOG = LoggerFactory.getLogger(JavaFXActionManager.class);

    @Inject
    public JavaFXActionManager(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    @Override
    protected Action createControllerAction(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return new JavaFXGriffonControllerAction(getUiThreadManager(), this, controller, actionName);
    }

    @Override
    protected void doConfigureAction(@Nonnull Action action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {
        JavaFXGriffonControllerAction javafxAction = (JavaFXGriffonControllerAction) action;

        String rsAccelerator = msg(keyPrefix, normalizeNamed, "accelerator", "");
        if (!isBlank(rsAccelerator)) {
            //noinspection ConstantConditions
            if (!isMacOSX() && rsAccelerator.contains("meta") && !rsAccelerator.contains("ctrl")) {
                rsAccelerator = rsAccelerator.replace("meta", "ctrl");
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".accelerator = " + rsAccelerator);
            }
            javafxAction.setAccelerator(rsAccelerator);
        }

        String rsDescription = msg(keyPrefix, normalizeNamed, "description", "");
        if (!isBlank(rsDescription)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".description = " + rsDescription);
            }
            javafxAction.setDescription(rsDescription);
        }

        String rsIcon = msg(keyPrefix, normalizeNamed, "icon", "");
        if (!isBlank(rsIcon)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".icon = " + rsIcon);
            }
            javafxAction.setIcon(rsIcon);
        }

        String rsImage = msg(keyPrefix, normalizeNamed, "image", "");
        if (!isBlank(rsImage)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".image = " + rsImage);
            }
            javafxAction.setImage(rsImage);
        }

        String rsEnabled = msg(keyPrefix, normalizeNamed, "enabled", "true");
        if (!isBlank(rsEnabled)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".enabled = " + rsEnabled);
            }
            javafxAction.setEnabled(castToBoolean(rsEnabled));
        }

        String rsSelected = msg(keyPrefix, normalizeNamed, "selected", "false");
        if (!isBlank(rsSelected)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".selected = " + rsSelected);
            }
            javafxAction.setSelected(castToBoolean(rsSelected));
        }
        
        String rsVisible = msg(keyPrefix, normalizeNamed, "visible", "true");
        if (!isBlank(rsVisible)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".visible = " + rsVisible);
            }
            javafxAction.setVisible(castToBoolean(rsVisible));
        }

        String rsStyleClass = msg(keyPrefix, normalizeNamed, "styleclass", "");
        if (!isBlank(rsStyleClass)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(keyPrefix + normalizeNamed + ".styleclass = " + rsStyleClass);
            }
            javafxAction.setStyleClass(rsStyleClass);
        }
    }
}
