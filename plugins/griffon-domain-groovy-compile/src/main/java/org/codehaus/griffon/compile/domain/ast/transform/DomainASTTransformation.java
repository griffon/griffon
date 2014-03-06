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
package org.codehaus.griffon.compile.domain.ast.transform;

import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.orm.Criterion;
import griffon.transform.Domain;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.MethodDescriptor;
import org.codehaus.griffon.compile.core.ast.transform.AbstractASTTransformation;
import org.codehaus.griffon.compile.domain.DomainConstants;
import org.codehaus.griffon.runtime.domain.GriffonDomainHandlerRegistry;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isStatic;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;

/**
 * Handles generation of code for the {@code @Domain} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(Domain.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class DomainASTTransformation extends AbstractASTTransformation implements DomainConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DomainASTTransformation.class);
    private static final ClassNode GRIFFON_DOMAIN_CNODE = makeClassSafe(GriffonDomain.class);
    private static final ClassNode GRIFFON_DOMAIN_HANDLER_REGISTRY_CNODE = makeClassSafe(GriffonDomainHandlerRegistry.class);
    private static final ClassNode DOMAIN_CNODE = makeClassSafe(Domain.class);
    private static final ClassNode CRITERION_CNODE = makeClassSafe(Criterion.class);
    private static final ClassNode COLLECTION_CNODE = makeClassSafe(Collection.class);
    private static final ClassNode NULLABLE_CNODE = makeClassSafe(Nullable.class);
    private static final ClassNode NONNULL_CNODE = makeClassSafe(Nonnull.class);

    /**
     * Convenience method to see if an annotated node is {@code @Domain}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasDomainAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (DOMAIN_CNODE.equals(annotation.getClassNode())) {
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
        addDomainHandlerIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addDomainHandlerIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, "Domain", GRIFFON_DOMAIN_TYPE)) {
            LOG.debug("Injecting {} into {}", GRIFFON_DOMAIN_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    private static Expression domainHandler(ClassNode classNode) {
        return call(GRIFFON_DOMAIN_HANDLER_REGISTRY_CNODE, METHOD_RESOLVE_FOR, args(classx(classNode)));
    }

    /**
     * Adds the necessary field and methods to support dataSource handling.
     *
     * @param classNode the class to which we add the support field and methods
     */
    public static void apply(@Nonnull ClassNode classNode) {
        for (MethodDescriptor method : METHODS) {
            if (!isStatic(method.modifiers) && isAbstract(classNode.getModifiers())) {
                continue;
            }

            List<Expression> variables = new ArrayList<>();
            Parameter[] parameters = new Parameter[method.arguments.length];

            if (isStatic(method.modifiers)) {
                variables.add(classx(classNode));
            } else {
                variables.add(THIS);
            }

            for (int i = 0; i < method.arguments.length; i++) {
                MethodDescriptor.Type t = method.arguments[i];
                parameters[i] = new Parameter(typeOf(t, classNode), "arg" + i);
                List<AnnotationNode> annotations = annotationsOf(t);
                if (!annotations.isEmpty()) {
                    parameters[i].addAnnotations(annotations);
                }
                variables.add(var("arg" + i));
            }

            ClassNode returnType = typeOf(method.returnType, classNode);
            List<AnnotationNode> annotations = annotationsOf(method.returnType);
            if (!annotations.isEmpty()) {
                returnType.addAnnotations(annotations);
            }

            MethodNode newMethod = new MethodNode(
                method.methodName,
                method.modifiers,
                returnType,
                parameters,
                NO_EXCEPTIONS,
                returns(call(
                    domainHandler(classNode),
                    method.methodName,
                    args(variables)
                ))
            );
            injectMethod(classNode, newMethod);
        }
    }

    @Nonnull
    private static List<AnnotationNode> annotationsOf(@Nonnull MethodDescriptor.Type type) {
        List<AnnotationNode> annotations = new ArrayList<>();
        for (MethodDescriptor.Type annotation : type.annotations) {
            switch (annotation.type) {
                case JAVAX_ANNOTATION_NONNULL:
                    annotations.add(new AnnotationNode(NONNULL_CNODE));
                    break;
                case JAVAX_ANNOTATION_NULLABLE:
                    annotations.add(new AnnotationNode(NULLABLE_CNODE));
                    break;
                default:
                    // not supported!!
            }
        }
        return annotations;
    }

    @Nonnull
    private static ClassNode typeOf(@Nonnull MethodDescriptor.Type type, @Nonnull ClassNode classNode) {
        switch (type.type) {
            case BOOLEAN:
                return boolean_TYPE;
            case INT:
                return int_TYPE;
            case JAVA_LANG_STRING:
                return makeClassSafe(STRING_TYPE);
            case JAVA_LANG_OBJECT:
                return type.dimensions == 0 ? makeClassSafe(OBJECT_TYPE) : makeClassSafe(OBJECT_TYPE).makeArray();
            case CRITERION_TYPE:
                return makeClassSafe(CRITERION_CNODE);
            case JAVA_UTIL_LIST:
                return makeClassSafeWithGenerics(LIST_TYPE, OBJECT_TYPE);
            case JAVA_UTIL_MAP:
                return makeClassSafeWithGenerics(MAP_TYPE, STRING_TYPE, OBJECT_TYPE);
            case JAVA_UTIL_COLLECTION:
                if (type.parameters.length == 1 && type.parameters[0].type.equals(T)) {
                    return makeClassSafeWithGenerics(COLLECTION_CNODE, classNode);
                }
                return makeClassSafe(COLLECTION_CNODE);
            case T:
                return makeClassSafe(classNode);
        }
        return makeClassSafe(OBJECT_TYPE);
    }
}