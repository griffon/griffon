/*
 * Copyright 2010-2012 the original author or authors.
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

package org.codehaus.griffon.compiler.support;

import groovy.util.FactoryBuilderSupport;
import org.codehaus.groovy.ast.ClassNode;

import static org.codehaus.griffon.ast.GriffonASTUtils.injectProperty;

/**
 * @author Andres Almiray
 * @since 0.9.1
 */
public class GriffonViewASTInjector extends GriffonMvcArtifactASTInjector {
    private static final ClassNode FBS_CLASS = makeClassSafe(FactoryBuilderSupport.class);

    public void inject(ClassNode classNode, String artifactType) {
        super.inject(classNode, artifactType);

        // FactoryBuilderSupport getBuilder()
        // void setBuilder(FactoryBuilderSupport builder)
        injectProperty(classNode, "builder", FBS_CLASS);
    }
}