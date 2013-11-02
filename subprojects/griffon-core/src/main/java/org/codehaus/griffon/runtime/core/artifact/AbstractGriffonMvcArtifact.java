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

package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonMvcArtifact;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Base implementation of the GriffonMvcArtifact interface.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public abstract class AbstractGriffonMvcArtifact extends AbstractGriffonArtifact implements GriffonMvcArtifact {
    public AbstractGriffonMvcArtifact(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        // empty
    }

    public void mvcGroupDestroy() {
        // empty
    }
}
