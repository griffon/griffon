/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonView;
import griffon.core.artifact.GriffonViewClass;
import griffon.core.controller.Action;

/**
 * Base implementation of the GriffonView interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractGriffonView extends AbstractGriffonMvcArtifact implements GriffonView {
    public AbstractGriffonView() {
    }

    @Nonnull
    @Override
    protected String getArtifactType() {
        return GriffonViewClass.TYPE;
    }

    @Nullable
    protected Action actionFor(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return getApplication().getActionManager().actionFor(controller, actionName);
    }
}