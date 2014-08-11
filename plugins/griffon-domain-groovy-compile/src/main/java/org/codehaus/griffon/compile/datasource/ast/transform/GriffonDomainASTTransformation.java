/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.compile.datasource.ast.transform;

import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import org.codehaus.griffon.compile.core.ast.transform.ASTInjector;
import org.codehaus.griffon.compile.core.ast.transform.GriffonArtifactASTTransformation;
import org.codehaus.griffon.compile.core.ast.transform.GriffonMvcArtifactASTInjector;
import org.codehaus.griffon.runtime.domain.AbstractGriffonDomain;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.annotation.Nonnull;

/**
 * Handles generation of code for Griffon domain classes.
 * <p/>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
@ServiceProviderFor(ASTTransformation.class)
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class GriffonDomainASTTransformation extends GriffonArtifactASTTransformation {
    private static final ClassNode GRIFFON_DOMAIN_CLASS = makeClassSafe(GriffonDomain.class);
    private static final ClassNode ABSTRACT_GRIFFON_DOMAIN_CLASS = makeClassSafe(AbstractGriffonDomain.class);

    public static boolean isDomainArtifact(@Nonnull ClassNode classNode, @Nonnull SourceUnit source) {
        return isArtifact(classNode, source, GRIFFON_DOMAIN_CLASS);
    }

    protected String getArtifactType() {
        return GriffonDomainClass.TYPE;
    }

    protected ClassNode getSuperClassNode(ClassNode classNode) {
        return ABSTRACT_GRIFFON_DOMAIN_CLASS;
    }

    protected ClassNode getInterfaceNode() {
        return GRIFFON_DOMAIN_CLASS;
    }

    protected boolean matches(ClassNode classNode, SourceUnit source) {
        return isDomainArtifact(classNode, source);
    }

    protected ASTInjector[] getASTInjectors() {
        return new ASTInjector[]{
            new GriffonMvcArtifactASTInjector(),
            new GriffonDomainASTInjector()
        };
    }
}