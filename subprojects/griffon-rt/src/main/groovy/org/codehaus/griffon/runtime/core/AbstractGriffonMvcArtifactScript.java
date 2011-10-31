/*
 * Copyright 2010-2011 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonMvcArtifact;

import java.util.Map;

/**
 * Base implementation of the GriffonMvcArtifact interface for Script based artifacts.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractGriffonMvcArtifactScript extends AbstractGriffonArtifactScript implements GriffonMvcArtifact {
    public AbstractGriffonMvcArtifactScript(String type) {
        super(type);
    }

    public void mvcGroupInit(Map<String, Object> args) {
        // empty
    }

    public void mvcGroupDestroy() {
        // empty
    }
}
