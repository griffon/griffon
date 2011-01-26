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

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import griffon.core.GriffonView;
import griffon.core.GriffonViewClass;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;
import org.codehaus.griffon.runtime.core.AbstractGriffonViewScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles generation of code for Griffon views.
 * <p/>
 *
 * @author Andres Almiray 
 *
 * @since 0.9.1
 */
@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class GriffonViewASTTransformation extends GriffonArtifactASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonViewASTTransformation.class);
    private static final String ARTIFACT_PATH = "views";
    private static final ClassNode GRIFFON_VIEW_CLASS = ClassHelper.makeWithoutCaching(GriffonView.class);
    private static final ClassNode ABSTRACT_GRIFFON_VIEW_CLASS = ClassHelper.makeWithoutCaching(AbstractGriffonView.class);
    private static final ClassNode ABSTRACT_GRIFFON_VIEW_SCRIPT_CLASS = ClassHelper.makeWithoutCaching(AbstractGriffonViewScript.class);

    protected boolean allowsScriptAsArtifact() {
        return true;
    }
    
    protected void transform(ClassNode classNode, SourceUnit source, String artifactPath) {
        if(!ARTIFACT_PATH.equals(artifactPath) || !classNode.getName().endsWith(GriffonViewClass.TRAILING)) return;

        if(classNode.isDerivedFrom(ClassHelper.SCRIPT_TYPE)) {
            if(LOG.isDebugEnabled()) LOG.debug("Setting "+ABSTRACT_GRIFFON_VIEW_SCRIPT_CLASS.getName()+" as the superclass of "+classNode.getName());
            classNode.setSuperClass(ABSTRACT_GRIFFON_VIEW_SCRIPT_CLASS);
        } else if(ClassHelper.OBJECT_TYPE.equals(classNode.getSuperClass())) {
            if(LOG.isDebugEnabled()) LOG.debug("Setting "+ABSTRACT_GRIFFON_VIEW_CLASS.getName()+" as the superclass of "+classNode.getName());
            classNode.setSuperClass(ABSTRACT_GRIFFON_VIEW_CLASS);
        } else if(!classNode.implementsInterface(GRIFFON_VIEW_CLASS)){
            inject(classNode);
        }
    }

    private void inject(ClassNode classNode) {
        if(LOG.isDebugEnabled()) LOG.debug("Injecting "+GRIFFON_VIEW_CLASS.getName()+" behavior to "+ classNode.getName());
        // 1. add interface
        classNode.addInterface(GRIFFON_VIEW_CLASS);
        // 2. add methods
        ASTInjector injector = new GriffonViewASTInjector();
        injector.inject(classNode, GriffonViewClass.TYPE);
    }
}
