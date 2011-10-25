/*
 * Copyright 2010-2011 the original author or authors.
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
import org.codehaus.griffon.compiler.SourceUnitCollector;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles generation of code for Griffon controllers.
 * <p/>
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class GriffonControllerASTTransformation extends GriffonArtifactASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonControllerASTTransformation.class);
    private static final String ARTIFACT_PATH = "controllers";
    private static final ClassNode GRIFFON_CONTROLLER_CLASS = ClassHelper.makeWithoutCaching(GriffonController.class);
    private static final ClassNode ABSTRACT_GRIFFON_CONTROLLER_CLASS = ClassHelper.makeWithoutCaching(AbstractGriffonController.class);

    public static boolean isControllerArtifact(ClassNode classNode, SourceUnit source) {
        if (classNode == null || source == null) return false;
        return ARTIFACT_PATH.equals(GriffonCompilerContext.getArtifactPath(source)) && classNode.getName().endsWith(GriffonControllerClass.TRAILING);
    }

    protected void transform(ClassNode classNode, SourceUnit source, String artifactPath) {
        if (!isControllerArtifact(classNode, source)) return;

        if (ClassHelper.OBJECT_TYPE.equals(classNode.getSuperClass())) {
            if (LOG.isDebugEnabled())
                LOG.debug("Setting " + ABSTRACT_GRIFFON_CONTROLLER_CLASS.getName() + " as the superclass of " + classNode.getName());
            classNode.setSuperClass(ABSTRACT_GRIFFON_CONTROLLER_CLASS);
        } else if (!classNode.implementsInterface(GRIFFON_CONTROLLER_CLASS)) {
            inject(classNode);
        }
    }

    private void inject(ClassNode classNode) {
        ClassNode superClass = classNode.getSuperClass();
        SourceUnit superSource = SourceUnitCollector.getInstance().getSourceUnit(superClass);
        if (isControllerArtifact(superClass, superSource)) return;

        if (LOG.isDebugEnabled())
            LOG.debug("Injecting " + GRIFFON_CONTROLLER_CLASS.getName() + " behavior to " + classNode.getName());
        // 1. add interface
        classNode.addInterface(GRIFFON_CONTROLLER_CLASS);
        // 2. add methods
        ASTInjector injector = new GriffonMvcArtifactASTInjector();
        injector.inject(classNode, GriffonControllerClass.TYPE);
    }
}