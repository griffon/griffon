/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonModelClass;
import griffon.inject.Typed;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * Handler for 'Model' artifacts.
 *
 * @author Andres Almiray
 */
@Typed(GriffonModel.class)
public class ModelArtifactHandler extends AbstractArtifactHandler<GriffonModel> {
    @Inject
    public ModelArtifactHandler(@Nonnull GriffonApplication application) {
        super(application, GriffonModel.class, GriffonModelClass.TYPE, GriffonModelClass.TRAILING);
    }

    @Nonnull
    public GriffonClass newGriffonClassInstance(@Nonnull Class<GriffonModel> clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        return new DefaultGriffonModelClass(getApplication(), clazz);
    }
}