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

import griffon.core.event.EventPublisher;
import griffon.core.event.EventRouter;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.EventPublisherConstants;
import org.codehaus.griffon.runtime.core.event.DefaultEventPublisher;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.isNotBlank;
import static java.lang.reflect.Modifier.PRIVATE;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_EXCEPTIONS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.args;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.call;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.ctor;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.field;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectField;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectMethod;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.param;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.params;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.stmnt;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.var;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * Handles generation of code for the {@code @EventPublisher} annotation.
 * <p/>
 * Generally, it adds (if needed) an EventRouter field and
 * the needed add/removeEventListener methods to support the
 * listeners.
 * <p/>
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(griffon.transform.EventPublisher.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class EventPublisherASTTransformation extends AbstractASTTransformation implements EventPublisherConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EventPublisherASTTransformation.class);
    private static final ClassNode EVENT_ROUTER_CNODE = makeClassSafe(EventRouter.class);
    private static final ClassNode EVENT_PUBLISHER_CNODE = makeClassSafe(EventPublisher.class);
    private static final ClassNode EVENT_PUBLISHER_FIELD_CNODE = makeClassSafe(DefaultEventPublisher.class);
    private static final ClassNode EVENT_PUBLISHER_ANODE = makeClassSafe(griffon.transform.EventPublisher.class);

    /**
     * Convenience method to see if an annotated node is {@code @EventPublisher}.
     *
     * @param node the node to check
     *
     * @return true if the node is an event publisher
     */
    public static boolean hasEventPublisherAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (EVENT_PUBLISHER_ANODE.equals(annotation.getClassNode())) {
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
        addEventPublisherIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addEventPublisherIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, "EventPublisher", EVENT_PUBLISHER_TYPE)) {
            LOG.debug("Injecting {} into {}", EVENT_PUBLISHER_TYPE, classNode.getName());
            ConstantExpression value = (ConstantExpression) annotationNode.getMember("value");
            String beanName = value != null ? value.getText() : null;
            beanName = isBlank(beanName) ? null : beanName;
            apply(classNode, beanName);
        }
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(@Nonnull ClassNode declaringClass, @Nullable String beanName) {
        injectInterface(declaringClass, EVENT_PUBLISHER_CNODE);

        FieldNode epField = injectField(declaringClass,
            EVENT_PUBLISHER_FIELD_NAME,
            PRIVATE,
            EVENT_PUBLISHER_FIELD_CNODE,
            ctor(EVENT_PUBLISHER_FIELD_CNODE));

        Parameter erParam = param(EVENT_ROUTER_CNODE, EVENT_ROUTER_PROPERTY);
        if (isNotBlank(beanName)) {
            AnnotationNode namedAnnotation = new AnnotationNode(NAMED_TYPE);
            namedAnnotation.addMember("value", new ConstantExpression(beanName));
            erParam.addAnnotation(namedAnnotation);
        }

        MethodNode setter = new MethodNode(
            METHOD_SET_EVENT_ROUTER,
            PRIVATE,
            VOID_TYPE,
            params(erParam),
            NO_EXCEPTIONS,
            stmnt(call(
                field(epField),
                METHOD_SET_EVENT_ROUTER,
                args(var(EVENT_ROUTER_PROPERTY)))));
        setter.addAnnotation(new AnnotationNode(INJECT_TYPE));
        injectMethod(declaringClass, setter);

        addDelegateMethods(declaringClass, EVENT_PUBLISHER_CNODE, field(epField));
    }
}