/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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

import griffon.core.resources.ResourceResolver;
import griffon.transform.ResourceResolverAware;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.ResourceResolverAwareConstants;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static griffon.util.GriffonNameUtils.isBlank;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for the {@code @ResourceResolverAware} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(ResourceResolverAware.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ResourceResolverAwareASTTransformation extends AbstractASTTransformation implements ResourceResolverAwareConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceResolverAwareASTTransformation.class);
    private static final ClassNode RESOURCE_RESOLVER_CNODE = makeClassSafe(ResourceResolver.class);
    private static final ClassNode RESOURCE_RESOLVER_AWARE_CNODE = makeClassSafe(ResourceResolverAware.class);

    /**
     * Convenience method to see if an annotated node is {@code @ResourceResolverAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasResourceResolverAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (RESOURCE_RESOLVER_AWARE_CNODE.equals(annotation.getClassNode())) {
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
        addResourceResolverIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addResourceResolverIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, "ResourceResolverAware", RESOURCE_RESOLVER_TYPE)) {
            LOG.debug("Injecting {} into {}", RESOURCE_RESOLVER_TYPE, classNode.getName());
            ConstantExpression value = (ConstantExpression) annotationNode.getMember("value");
            String beanName = value != null ? value.getText() : null;
            beanName = isBlank(beanName) ? "applicationResourceResolver" : beanName;
            apply(classNode, beanName);
        }
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(ClassNode declaringClass, @Nonnull String beanName) {
        injectInterface(declaringClass, RESOURCE_RESOLVER_CNODE);
        Expression resourceResolver = injectedField(declaringClass, RESOURCE_RESOLVER_CNODE, "this$" + RESOURCE_RESOLVER_PROPERTY, beanName);
        addDelegateMethods(declaringClass, RESOURCE_RESOLVER_CNODE, resourceResolver);
    }
}