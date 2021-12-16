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
package org.codehaus.griffon.runtime.swing.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.swing.support.SwingAction;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Swing-friendly implementation of the GriffonView interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractSwingGriffonView extends AbstractGriffonView {
    public AbstractSwingGriffonView() {

    }

    /**
     * Creates a new instance of this class.
     *
     * @param application the GriffonApplication that holds this artifact.
     * @deprecated Griffon prefers field injection over constructor injector for artifacts as of 2.1.0
     */
    @Inject
    @Deprecated
    public AbstractSwingGriffonView(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nullable
    protected SwingAction toolkitActionFor(@Nonnull GriffonController controller, @Nonnull String actionName) {
        Action action = actionFor(controller, actionName);
        return action != null ? (SwingAction) action.getToolkitAction() : null;
    }
}
