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
import griffon.core.i18n.MessageSource;
import griffon.transform.core.MessageSourceAware;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.MessageSourceAwareConstants;
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

import static griffon.util.GriffonNameUtils.isBlank;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for the {@code @MessageSourceAware} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(MessageSourceAware.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class MessageSourceAwareASTTransformation extends AbstractASTTransformation implements MessageSourceAwareConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MessageSourceAwareASTTransformation.class);
    private static final ClassNode MESSAGE_SOURCE_CNODE = makeClassSafe(MessageSource.class);
    private static final ClassNode MESSAGE_SOURCE_AWARE_CNODE = makeClassSafe(MessageSourceAware.class);

    /**
     * Convenience method to see if an annotated node is {@code @MessageSourceAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasMessageSourceAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (MESSAGE_SOURCE_AWARE_CNODE.equals(annotation.getClassNode())) {
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
        addMessageSourceIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addMessageSourceIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, "MessageSourceAware", MESSAGE_SOURCE_TYPE)) {
            LOG.debug("Injecting {} into {}", MESSAGE_SOURCE_TYPE, classNode.getName());
            ConstantExpression value = (ConstantExpression) annotationNode.getMember("value");
            String beanName = value != null ? value.getText() : null;
            beanName = isBlank(beanName) ? "applicationMessageSource" : beanName;
            apply(classNode, beanName);
        }
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(@Nonnull ClassNode declaringClass, @Nonnull String beanName) {
        injectInterface(declaringClass, MESSAGE_SOURCE_CNODE);
        Expression messageSource = injectedField(declaringClass, MESSAGE_SOURCE_CNODE, "this$" + MESSAGE_SOURCE_PROPERTY, beanName);
        addDelegateMethods(declaringClass, MESSAGE_SOURCE_CNODE, messageSource);
    }
}