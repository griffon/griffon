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

package org.codehaus.griffon.ast;

import griffon.transform.PropertyListener;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Handles generation of code for the {@code @PropertyListener} annotation.
 * <p/>
 * Any closures found as the annotation's value will be either transformed
 * into inner classes that implement PropertyChangeListener (when the value
 * is a closue defined in place) or be casted as a proxy of PropertyChangeListener
 * (when the value is a property reference found in the same class).<p>
 * List of closures are also supported.
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase= CompilePhase.CANONICALIZATION)
public class PropertyListenerASTTransformation extends AbstractASTTransformation {
    private static final ClassNode PROPERTY_LISTENER_CLASS = ClassHelper.makeWithoutCaching(PropertyListener.class);
    private static final ClassNode PROPERTY_CHANGE_LISTENER_CLASS = ClassHelper.makeWithoutCaching(PropertyChangeListener.class);
    private static final String EMPTY_STRING = "";

    /**
     * Convenience method to see if an annotated node is {@code @PropertyListener}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasListenerAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (PROPERTY_LISTENER_CLASS.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes   the ast nodes
     * @param source  the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: "+ nodes[0].getClass() +" / "+ nodes[1].getClass());
        }

        AnnotationNode annotation = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];

        ClassNode declaringClass = parent.getDeclaringClass();
        if (parent instanceof FieldNode) {
            addListenerToProperty(source, annotation, declaringClass, (FieldNode) parent);
        } else if (parent instanceof ClassNode) {
            addListenerToClass(source, annotation, (ClassNode) parent);
        }
    }

    public static void addListenerToProperty(SourceUnit source, AnnotationNode annotation, ClassNode declaringClass, FieldNode field) {
        for(Map.Entry<String, Expression> member : annotation.getMembers().entrySet()) {
            Expression value = member.getValue();
            if((value instanceof ListExpression)) {
                for(Expression expr : ((ListExpression) value).getExpressions()) {
                    processExpression(declaringClass, field.getName(), expr);
                }
                member.setValue(new ConstantExpression(EMPTY_STRING));
            } else {
                processExpression(declaringClass, field.getName(), value);
                member.setValue(new ConstantExpression(EMPTY_STRING));
            }
        }
    }

    public static void addListenerToClass(SourceUnit source, AnnotationNode annotation, ClassNode classNode)  {
        for(Map.Entry<String, Expression> member : annotation.getMembers().entrySet()) {
            Expression value = member.getValue();
            if((value instanceof ListExpression)) {
                for(Expression expr : ((ListExpression) value).getExpressions()) {
                    processExpression(classNode, null, expr);
                }
                member.setValue(new ConstantExpression(EMPTY_STRING));
            } else {
                processExpression(classNode, null, value);
                member.setValue(new ConstantExpression(EMPTY_STRING));
            }
        }
    }

    private static void processExpression(ClassNode classNode, String propertyName, Expression expression) {
        if(expression instanceof ClosureExpression) {
            addPropertyChangeListener(classNode, propertyName, (ClosureExpression) expression);
        } else if(expression instanceof VariableExpression) {
            addPropertyChangeListener(classNode, propertyName, (VariableExpression) expression);
        } else {
            throw new RuntimeException("Internal error: wrong expression type. "+expression);
        }
    } 

    private static void addPropertyChangeListener(ClassNode classNode, String propertyName, ClosureExpression closure) {
        ArgumentListExpression args = new ArgumentListExpression();
        if(propertyName != null) args.addExpression(new ConstantExpression(propertyName));
        args.addExpression(CastExpression.asExpression(PROPERTY_CHANGE_LISTENER_CLASS, closure));

        addListenerStatement(classNode, args);
    }
    
    private static void addPropertyChangeListener(ClassNode classNode, String propertyName, VariableExpression variable) {
        ArgumentListExpression args = new ArgumentListExpression();
        if(propertyName != null) args.addExpression(new ConstantExpression(propertyName));
        args.addExpression(CastExpression.asExpression(PROPERTY_CHANGE_LISTENER_CLASS, variable));

        addListenerStatement(classNode, args);
    }

    private static void addListenerStatement(ClassNode classNode, ArgumentListExpression args) {
        BlockStatement body = new BlockStatement();
        body.addStatement(new ExpressionStatement(
            new MethodCallExpression(
                VariableExpression.THIS_EXPRESSION,
                "addPropertyChangeListener",
                args
            )
        ));

        classNode.addObjectInitializerStatements(body);
    }
}
