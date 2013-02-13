/*
 * Copyright 2010-2013 the original author or authors.
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

import griffon.core.GriffonController;
import griffon.core.GriffonControllerClass;
import org.codehaus.griffon.compiler.GriffonCompilerContext;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;

/**
 * Handles generation of code for Griffon controllers.
 * <p/>
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class GriffonControllerASTTransformation extends GriffonArtifactASTTransformation {
    private static final String ARTIFACT_PATH = "controllers";
    private static final ClassNode GRIFFON_CONTROLLER_CLASS = makeClassSafe(GriffonController.class);
    private static final ClassNode ABSTRACT_GRIFFON_CONTROLLER_CLASS = makeClassSafe(AbstractGriffonController.class);

    public static boolean isControllerArtifact(ClassNode classNode, SourceUnit source) {
        if (classNode == null || source == null) return false;
        return ARTIFACT_PATH.equals(GriffonCompilerContext.getArtifactPath(source)) && classNode.getName().endsWith(GriffonControllerClass.TRAILING);
    }

    protected String getArtifactType() {
        return GriffonControllerClass.TYPE;
    }

    protected ClassNode getSuperClassNode(ClassNode classNode) {
        return ABSTRACT_GRIFFON_CONTROLLER_CLASS;
    }

    protected ClassNode getInterfaceNode() {
        return GRIFFON_CONTROLLER_CLASS;
    }

    protected boolean matches(ClassNode classNode, SourceUnit source) {
        return isControllerArtifact(classNode, source);
    }

    protected ASTInjector[] getASTInjectors() {
        return new ASTInjector[]{
            new GriffonMvcArtifactASTInjector()
        };
    }
}