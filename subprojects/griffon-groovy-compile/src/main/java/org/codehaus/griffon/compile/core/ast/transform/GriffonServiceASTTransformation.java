/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.compile.core.ast.transform;

import griffon.annotations.core.Nonnull;
import griffon.core.artifact.GriffonService;
import griffon.core.artifact.GriffonServiceClass;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.kordamp.jipsy.ServiceProviderFor;

/**
 * Handles generation of code for Griffon controllers.
 * <p/>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
@ServiceProviderFor(ASTTransformation.class)
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class GriffonServiceASTTransformation extends GriffonArtifactASTTransformation {
    private static final ClassNode GRIFFON_SERVICE_CLASS = makeClassSafe(GriffonService.class);
    private static final ClassNode ABSTRACT_GRIFFON_SERVICE_CLASS = makeClassSafe(AbstractGriffonService.class);

    public static boolean isServiceArtifact(@Nonnull ClassNode classNode, @Nonnull SourceUnit source) {
        return isArtifact(classNode, source, GRIFFON_SERVICE_CLASS);
    }

    protected String getArtifactType() {
        return GriffonServiceClass.TYPE;
    }

    @Override
    protected ClassNode getSuperClassNode(ClassNode classNode) {
        return ABSTRACT_GRIFFON_SERVICE_CLASS;
    }

    protected ClassNode getInterfaceNode() {
        return GRIFFON_SERVICE_CLASS;
    }

    protected boolean matches(ClassNode classNode, SourceUnit source) {
        return isServiceArtifact(classNode, source);
    }

    protected ASTInjector[] getASTInjectors() {
        return new ASTInjector[]{
            new GriffonArtifactASTInjector(),
            new AbstractASTInjector() {
                @Override
                public void inject(@Nonnull ClassNode classNode, @Nonnull String artifactType) {
                    AbstractASTTransformation.injectApplication(classNode);
                }
            }
        };
    }
}