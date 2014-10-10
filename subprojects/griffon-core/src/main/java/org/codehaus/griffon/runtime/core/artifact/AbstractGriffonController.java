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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonControllerClass;
import griffon.core.controller.ActionManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static griffon.util.GriffonNameUtils.requireNonBlank;

/**
 * Base implementation of the GriffonController interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractGriffonController extends AbstractGriffonMvcArtifact implements GriffonController {
    public AbstractGriffonController() {

    }

    /**
     * Creates a new instance of this class.
     *
     * @param application the GriffonApplication that holds this artifact.
     * @deprecated Griffon prefers field injection over constructor injector for artifacts as of 2.1.0
     */
    @Inject
    @Deprecated
    public AbstractGriffonController(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    @Override
    protected String getArtifactType() {
        return GriffonControllerClass.TYPE;
    }

    @Nonnull
    protected ActionManager getActionManager() {
        return getApplication().getActionManager();
    }

    public void invokeAction(@Nonnull String name, Object... args) {
        getActionManager().invokeAction(this, requireNonBlank(name, "Argument 'name' must not be blank"), args);
    }
}
