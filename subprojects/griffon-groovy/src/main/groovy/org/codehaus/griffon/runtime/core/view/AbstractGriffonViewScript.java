/*
 * Copyright 2010-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core.view;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonView;
import griffon.core.artifact.GriffonViewClass;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonMvcArtifactScript;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the GriffonView interface for Script based views
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractGriffonViewScript extends AbstractGriffonMvcArtifactScript implements GriffonView {
    private FactoryBuilderSupport builder;

    public AbstractGriffonViewScript(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    @Override
    protected String getArtifactType() {
        return GriffonViewClass.TYPE;
    }

    @Nonnull
    public FactoryBuilderSupport getBuilder() {
        return builder;
    }

    public void setBuilder(@Nonnull FactoryBuilderSupport builder) {
        this.builder = requireNonNull(builder, "Argument 'builder' cannot be null");
    }
}
