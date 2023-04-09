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
import griffon.annotations.inject.Typed;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonControllerClass;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * Handler for 'Controller' artifacts.
 *
 * @author Andres Almiray
 */
@Typed(GriffonController.class)
public class ControllerArtifactHandler extends AbstractArtifactHandler<GriffonController> {
    @Inject
    public ControllerArtifactHandler(@Nonnull GriffonApplication application) {
        super(application, GriffonController.class, GriffonControllerClass.TYPE, GriffonControllerClass.TRAILING);
    }

    @Nonnull
    public GriffonClass newGriffonClassInstance(@Nonnull Class<GriffonController> clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        return new DefaultGriffonControllerClass(getApplication(), clazz);
    }
}