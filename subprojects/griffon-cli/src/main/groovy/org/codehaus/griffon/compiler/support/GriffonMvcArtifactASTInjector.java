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

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.EmptyStatement;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.MAP_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * @author Andres Almiray
 * @since 0.9.1
 */
public class GriffonMvcArtifactASTInjector extends GriffonArtifactASTInjector {
    public void inject(ClassNode classNode, String artifactType) {
        super.inject(classNode, artifactType);

        // void mvcGroupInit(Map args)
        injectMethod(classNode, new MethodNode(
                "mvcGroupInit",
                ACC_PUBLIC,
                VOID_TYPE,
                params(param(makeClassSafe(MAP_TYPE), "args")),
                NO_EXCEPTIONS,
                new EmptyStatement()
        ));

        // void mvcGroupDestroy()
        injectMethod(classNode, new MethodNode(
                "mvcGroupDestroy",
                ACC_PUBLIC,
                VOID_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                new EmptyStatement()
        ));
    }
}
