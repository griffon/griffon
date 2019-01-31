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
package org.codehaus.griffon.runtime.pivot.controller;

import griffon.annotations.core.Nonnull;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadata;
import griffon.core.threading.UIThreadManager;

import javax.inject.Inject;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class PivotActionFactory implements ActionFactory {
    @Inject
    private UIThreadManager uiThreadManager;

    @Inject
    private ActionManager actionManager;

    @Nonnull
    @Override
    public Action create(@Nonnull GriffonController controller, @Nonnull ActionMetadata actionMetadata) {
        return new PivotGriffonControllerAction(uiThreadManager, actionManager, controller, actionMetadata);
    }
}
