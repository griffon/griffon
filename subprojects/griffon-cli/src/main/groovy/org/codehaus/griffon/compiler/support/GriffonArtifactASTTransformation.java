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

import org.codehaus.griffon.ast.AbstractASTTransformation;
import org.codehaus.griffon.ast.GriffonASTUtils;
import org.codehaus.griffon.cli.CommandLineConstants;
import org.codehaus.griffon.compiler.GriffonCompilerContext;
import org.codehaus.griffon.compiler.SourceUnitCollector;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.SourceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.compiler.GriffonCompilerContext.getConfigOption;
import static org.codehaus.griffon.compiler.GriffonCompilerContext.isGriffonArtifact;

/**
 * Handles generation of code for Griffon artifacts.
 * <p/>
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public abstract class GriffonArtifactASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonArtifactASTTransformation.class);

    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (getConfigOption(CommandLineConstants.KEY_DISABLE_AST_INJECTION) || !isGriffonArtifact(source)) {
            return;
        }
        ModuleNode moduleNode = (ModuleNode) nodes[0];
        ClassNode classNode = moduleNode.getClasses().get(0);
        if (classNode.isDerivedFrom(ClassHelper.SCRIPT_TYPE) && !allowsScriptAsArtifact() ||
            !matches(classNode, source)) {
            return;
        }
        transform(classNode);
    }

    protected boolean allowsScriptAsArtifact() {
        return false;
    }

    protected void transform(ClassNode classNode) {
        ClassNode superClass = classNode.getSuperClass();
        ClassNode superScriptClassNode = getSuperScriptClassNode(classNode);
        ClassNode superClassNode = getSuperClassNode(classNode);
        if (superScriptClassNode != null && allowsScriptAsArtifact() && classNode.isDerivedFrom(ClassHelper.SCRIPT_TYPE)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting " + superScriptClassNode.getName() + " as the superclass of " + classNode.getName());
            }
            classNode.setSuperClass(superScriptClassNode);
        } else if (superClassNode != null && ClassHelper.OBJECT_TYPE.equals(superClass)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting " + superClassNode.getName() + " as the superclass of " + classNode.getName());
            }
            classNode.setSuperClass(superClassNode);
        } else if (!classNode.implementsInterface(getInterfaceNode())) {
            inject(classNode, superClass);
        }
    }

    protected void inject(ClassNode classNode, ClassNode superClass) {
        SourceUnit superSource = SourceUnitCollector.getInstance().getSourceUnit(superClass);
        if (matches(superClass, superSource)) return;

        if (superSource == null) {
            ClassNode interfaceNode = getInterfaceNode();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + interfaceNode.getName() + " behavior to " + classNode.getName());
            }
            // 1. add interface
            GriffonASTUtils.injectInterface(classNode, interfaceNode);
            // 2. add methods
            for (ASTInjector injector : getASTInjectors()) {
                injector.inject(classNode, getArtifactType());
            }
            postInject(classNode);
        } else {
            transform(superClass);
        }
    }

    protected abstract String getArtifactType();

    protected ClassNode getSuperScriptClassNode(ClassNode classNode) {
        return null;
    }

    protected ClassNode getSuperClassNode(ClassNode classNode){
        return null;
    }

    protected abstract ClassNode getInterfaceNode();

    protected abstract boolean matches(ClassNode classNode, SourceUnit source);

    protected abstract ASTInjector[] getASTInjectors();

    protected void postInject(ClassNode classNode) {

    }

    public static boolean isOrImplements(ClassNode fieldType, ClassNode interfaceType) {
        return fieldType.equals(interfaceType) || fieldType.implementsInterface(interfaceType);
    }
}
