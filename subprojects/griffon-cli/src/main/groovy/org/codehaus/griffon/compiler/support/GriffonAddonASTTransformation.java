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

import griffon.core.GriffonAddon;
import org.codehaus.griffon.ast.AbstractASTTransformation;
import org.codehaus.griffon.cli.CommandLineConstants;
import org.codehaus.griffon.compiler.GriffonCompilerContext;
import org.codehaus.griffon.runtime.core.AbstractGriffonAddon;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles generation of code for Griffon addons.
 * <p/>
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class GriffonAddonASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonAddonASTTransformation.class);
    private static final ClassNode GRIFFON_ADDON_CLASS = makeClassSafe(GriffonAddon.class);
    private static final ClassNode ABSTRACT_GRIFFON_ADDON_CLASS = makeClassSafe(AbstractGriffonAddon.class);

    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (GriffonCompilerContext.getConfigOption(CommandLineConstants.KEY_DISABLE_AST_INJECTION) || !GriffonCompilerContext.isGriffonAddon(source))
            return;
        ModuleNode moduleNode = (ModuleNode) nodes[0];
        ClassNode classNode = moduleNode.getClasses().get(0);

        if (ClassHelper.OBJECT_TYPE.equals(classNode.getSuperClass())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting " + ABSTRACT_GRIFFON_ADDON_CLASS.getName() + " as the superclass of " + classNode.getName());
            }
            classNode.setSuperClass(ABSTRACT_GRIFFON_ADDON_CLASS);
            GriffonAddonASTInjector.addDefaultConstructor(classNode);
        } else if (!classNode.implementsInterface(GRIFFON_ADDON_CLASS)) {
            inject(classNode);
        }
    }

    private void inject(ClassNode classNode) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Injecting " + GRIFFON_ADDON_CLASS.getName() + " behavior to " + classNode.getName());
        }
        // 1. add interface
        classNode.addInterface(GRIFFON_ADDON_CLASS);
        // 2. add methods
        ASTInjector injector = new GriffonAddonASTInjector();
        injector.inject(classNode, null);
    }
}