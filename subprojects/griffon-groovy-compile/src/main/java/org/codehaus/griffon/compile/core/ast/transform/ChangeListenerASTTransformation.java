/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.annotations.javafx.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_ARGS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.THIS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.call;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.ctor;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.field;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.stmnt;

/**
 * Handles generation of code for the {@code @ChangeListener} annotation.
 * <p>
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement ChangeListener (when the value
 * is a closure defined in place) or be casted as a proxy of ChangeListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(ChangeListener.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ChangeListenerASTTransformation extends AbstractASTTransformation implements AnnotationHandler {
    private static final ClassNode CHANGE_LISTENER_CLASS = makeClassSafe(ChangeListener.class);
    private static final ClassNode JAVAFX_CHANGE_LISTENER_CLASS = makeClassSafe(javafx.beans.value.ChangeListener.class);
    private static final ClassNode JAVAFX_WEAK_CHANGE_LISTENER_CLASS = makeClassSafe(WeakChangeListener.class);
    private static final String EMPTY_STRING = "";
    private static final String VALUE = "value";

    /**
     * Convenience method to see if an annotated node is {@code @ChangeListener}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasListenerAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (CHANGE_LISTENER_CLASS.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    public static void addListenerToProperty(SourceUnit source, AnnotationNode annotation, ClassNode declaringClass, FieldNode field) {
        Expression value = annotation.getMember(VALUE);
        Expression weak = annotation.getMember("weak");
        boolean useWeakListener = false;
        if (weak != null && weak instanceof ConstantExpression && ((ConstantExpression) weak).isTrueExpression()) {
            useWeakListener = true;
        }

        if ((value instanceof ListExpression)) {
            for (Expression expr : ((ListExpression) value).getExpressions()) {
                processExpression(declaringClass, field.getName(), expr, useWeakListener);
            }
            annotation.setMember(VALUE, new ConstantExpression(EMPTY_STRING));
        } else {
            processExpression(declaringClass, field.getName(), value, useWeakListener);
            annotation.setMember(VALUE, new ConstantExpression(EMPTY_STRING));
        }
    }

    private static void processExpression(ClassNode classNode, String propertyName, Expression expression, boolean useWeakListener) {
        if (expression instanceof ClosureExpression) {
            addChangeListener(classNode, propertyName, (ClosureExpression) expression, useWeakListener);
        } else if (expression instanceof VariableExpression) {
            addChangeListener(classNode, propertyName, (VariableExpression) expression, useWeakListener);
        } else if (expression instanceof ConstantExpression) {
            addChangeListener(classNode, propertyName, (ConstantExpression) expression, useWeakListener);
        } else {
            throw new RuntimeException("Internal error: wrong expression type. " + expression);
        }
    }

    private static void addChangeListener(ClassNode classNode, String propertyName, ClosureExpression closure, boolean useWeakListener) {
        ArgumentListExpression args = createListenerExpression(classNode, propertyName, CastExpression.asExpression(JAVAFX_CHANGE_LISTENER_CLASS, closure), useWeakListener);
        addListenerStatement(classNode, propertyName, args);
    }

    private static void addChangeListener(ClassNode classNode, String propertyName, VariableExpression variable, boolean useWeakListener) {
        ArgumentListExpression args = createListenerExpression(classNode, propertyName, CastExpression.asExpression(JAVAFX_CHANGE_LISTENER_CLASS, variable), useWeakListener);
        addListenerStatement(classNode, propertyName, args);
    }

    private static ArgumentListExpression createListenerExpression(ClassNode classNode, String propertyName, Expression expression, boolean useWeakListener) {
        ArgumentListExpression args = new ArgumentListExpression();
        if (useWeakListener) {
            String fieldName = "$" + propertyName + "ChangeListener__" + System.nanoTime();
            FieldNode listenerField = new FieldNode(
                fieldName,
                PRIVATE | FINAL,
                JAVAFX_CHANGE_LISTENER_CLASS,
                classNode,
                expression);
            classNode.addField(listenerField);
            ArgumentListExpression params = new ArgumentListExpression();
            params.addExpression(field(listenerField));
            args.addExpression(
                ctor(JAVAFX_WEAK_CHANGE_LISTENER_CLASS, params)
            );
        } else {
            args.addExpression(CastExpression.asExpression(JAVAFX_CHANGE_LISTENER_CLASS, expression));
        }
        return args;
    }

    private static void addChangeListener(ClassNode classNode, String propertyName, ConstantExpression reference, boolean useWeakListener) {
        addChangeListener(classNode, propertyName, new VariableExpression(reference.getText()), useWeakListener);
    }

    private static void addListenerStatement(ClassNode classNode, String propertyName, ArgumentListExpression args) {
        BlockStatement body = new BlockStatement();
        body.addStatement(stmnt(
            call(
                call(THIS, propertyName + "Property", NO_ARGS),
                "addListener",
                args
            )
        ));

        classNode.addObjectInitializerStatements(body);
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: " + nodes[0].getClass() + " / " + nodes[1].getClass());
        }

        AnnotationNode annotation = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];

        ClassNode declaringClass = parent.getDeclaringClass();
        if (parent instanceof FieldNode) {
            addListenerToProperty(source, annotation, declaringClass, (FieldNode) parent);
        }
    }
}
