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
package org.codehaus.griffon.runtime.core.controller;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultActionManager extends AbstractActionManager {
    @Inject
    public DefaultActionManager(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Override
    protected void doConfigureAction(@Nonnull Action action, @Nonnull GriffonController controller, @Nonnull String normalizeNamed, @Nonnull String keyPrefix) {

    }

    @Nonnull
    @Override
    protected Action createControllerAction(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return new DefaultAction(getUiThreadManager(), this, controller, actionName);
    }
}
