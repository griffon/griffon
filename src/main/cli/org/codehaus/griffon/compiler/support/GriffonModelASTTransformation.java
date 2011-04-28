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

import griffon.core.GriffonModel;
import griffon.core.GriffonModelClass;
import org.codehaus.griffon.runtime.core.AbstractGriffonModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles generation of code for Griffon models.
 * <p/>
 *
 * @author Andres Almiray 
 *
 * @since 0.9.1
 */
@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class GriffonModelASTTransformation extends GriffonArtifactASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonModelASTTransformation.class);
    private static final String ARTIFACT_PATH = "models";
    private static final ClassNode GRIFFON_MODEL_CLASS = ClassHelper.makeWithoutCaching(GriffonModel.class);
    private static final ClassNode ABSTRACT_GRIFFON_MODEL_CLASS = ClassHelper.makeWithoutCaching(AbstractGriffonModel.class);    
    
    protected void transform(ClassNode classNode, SourceUnit source, String artifactPath) {
        if(!ARTIFACT_PATH.equals(artifactPath) || !classNode.getName().endsWith(GriffonModelClass.TRAILING)) return;

        if(ClassHelper.OBJECT_TYPE.equals(classNode.getSuperClass())) {
            if(LOG.isDebugEnabled()) LOG.debug("Setting "+ABSTRACT_GRIFFON_MODEL_CLASS.getName()+" as the superclass of "+classNode.getName());
            classNode.setSuperClass(ABSTRACT_GRIFFON_MODEL_CLASS);
        } else if(!classNode.implementsInterface(GRIFFON_MODEL_CLASS)){
            if(LOG.isDebugEnabled()) LOG.debug("Injecting "+GRIFFON_MODEL_CLASS.getName()+" behavior to "+ classNode.getName());
            // 1. add interface
            classNode.addInterface(GRIFFON_MODEL_CLASS);
            // 2. add methods
            ASTInjector injector = new GriffonMvcArtifactASTInjector();
            injector.inject(classNode, GriffonModelClass.TYPE);
            injector = new ObservableASTInjector();
            injector.inject(classNode, GriffonModelClass.TYPE);
        }
    }
}