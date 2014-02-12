/*
 * Copyright 2010-2014 the original author or authors.
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

import griffon.core.GriffonView;
import griffon.core.GriffonViewClass;
import org.codehaus.griffon.compiler.GriffonCompilerContext;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;
import org.codehaus.griffon.runtime.core.AbstractGriffonViewScript;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;

/**
 * Handles generation of code for Griffon views.
 * <p/>
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class GriffonViewASTTransformation extends GriffonArtifactASTTransformation {
    private static final String ARTIFACT_PATH = "views";
    private static final ClassNode GRIFFON_VIEW_CLASS = makeClassSafe(GriffonView.class);
    private static final ClassNode ABSTRACT_GRIFFON_VIEW_CLASS = makeClassSafe(AbstractGriffonView.class);
    private static final ClassNode ABSTRACT_GRIFFON_VIEW_SCRIPT_CLASS = makeClassSafe(AbstractGriffonViewScript.class);

    public static boolean isViewArtifact(ClassNode classNode, SourceUnit source) {
        if (classNode == null || source == null) return false;
        return ARTIFACT_PATH.equals(GriffonCompilerContext.getArtifactPath(source)) && classNode.getName().endsWith(GriffonViewClass.TRAILING);
    }

    protected boolean allowsScriptAsArtifact() {
        return true;
    }

    protected String getArtifactType() {
        return GriffonViewClass.TYPE;
    }

    protected ClassNode getSuperScriptClassNode(ClassNode classNode) {
        return ABSTRACT_GRIFFON_VIEW_SCRIPT_CLASS;
    }

    protected ClassNode getSuperClassNode(ClassNode classNode) {
        return ABSTRACT_GRIFFON_VIEW_CLASS;
    }

    protected ClassNode getInterfaceNode() {
        return GRIFFON_VIEW_CLASS;
    }

    protected boolean matches(ClassNode classNode, SourceUnit source) {
        return isViewArtifact(classNode, source);
    }

    protected ASTInjector[] getASTInjectors() {
        return new ASTInjector[]{
            new GriffonMvcArtifactASTInjector(),
            new GriffonViewASTInjector()
        };
    }
}
