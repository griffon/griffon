/*
 * Copyright 2010 the original author or authors.
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
package griffon.core;

import groovy.util.FactoryBuilderSupport;

/**
 * Identifies a View artifact.
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public interface GriffonView extends GriffonMvcArtifact {
    /**
     * Sets the corresponding builder instance on this view.<p>
     * Views will normally use a builder in order to create their UI elements
     * however they can opt out and build the UI by other means.
     *
     * @param builder the builder instance that belongs to the same MVC group
     * as this View.
     */
    void setBuilder(FactoryBuilderSupport builder);
}