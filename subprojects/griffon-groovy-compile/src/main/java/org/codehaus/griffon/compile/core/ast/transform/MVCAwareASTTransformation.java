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

import griffon.core.mvc.MVCGroupManager;
import griffon.core.mvc.MVCHandler;
import griffon.transform.core.MVCAware;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.MVCAwareConstants;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for the {@code @MVCAware} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(MVCAware.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class MVCAwareASTTransformation extends AbstractASTTransformation implements MVCAwareConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MVCAwareASTTransformation.class);
    private static final ClassNode MVC_GROUP_MANAGER_CNODE = makeClassSafe(MVCGroupManager.class);
    private static final ClassNode MVC_HANDLER_CNODE = makeClassSafe(MVCHandler.class);
    private static final ClassNode MVC_AWARE_CNODE = makeClassSafe(MVCAware.class);

    /**
     * Convenience method to see if an annotated node is {@code @MVCAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasMVCAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (MVC_AWARE_CNODE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addMVCHandlerIfNeeded(source, (ClassNode) nodes[1]);
    }

    public static void addMVCHandlerIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, "MVCAware", MVC_HANDLER_TYPE)) {
            LOG.debug("Injecting {} into {}", MVC_HANDLER_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(ClassNode declaringClass) {
        injectInterface(declaringClass, MVC_HANDLER_CNODE);
        Expression mvcGroupManager = injectedField(declaringClass, MVC_GROUP_MANAGER_CNODE, "this$" + MVC_GROUP_MANAGER_PROPERTY, null);
        addDelegateMethods(declaringClass, MVC_HANDLER_CNODE, mvcGroupManager);
    }
}