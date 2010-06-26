/*
 * Copyright 2010 the original author or authors.
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

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.objectweb.asm.Opcodes;

import java.util.*;
import java.lang.reflect.Modifier;
import griffon.beans.Listener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Handles generation of code for the {@code @Listener} annotation.
 * <p/>
 * Generally, it adds (if needed) a EventRouter field and
 * the needed add/removeEventListener methods to support the
 * listeners.
 * <p/>
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase= CompilePhase.CANONICALIZATION)
public class ListenerASTTransformation implements ASTTransformation, Opcodes {
    protected static ClassNode LISTENER = new ClassNode(Listener.class);
    protected static ClassNode PROPERTY_CHANGE_LISTENER = ClassHelper.makeWithoutCaching(PropertyChangeListener.class);
    protected static ClassNode PROPERTY_CHANGE_EVENT = ClassHelper.makeWithoutCaching(PropertyChangeEvent.class);
    protected static ClassNode DGM = ClassHelper.makeWithoutCaching(DefaultGroovyMethods.class);
    private static final String EMPTY_STRING = "";
    private static final Map<String, Integer> LISTENER_COUNT = new LinkedHashMap<String, Integer>();

    /**
     * Convenience method to see if an annotated node is {@code @Listener}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasListenerAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : (Collection<AnnotationNode>) node.getAnnotations()) {
            if (LISTENER.equals(annotation.getClassNode())) {
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
            throw new RuntimeException("Internal error: wrong types: $node.class / $parent.class");
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

    private void addListenerToProperty(SourceUnit source, AnnotationNode annotation, ClassNode declaringClass, FieldNode field) {
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

    private void addListenerToClass(SourceUnit source, AnnotationNode annotation, ClassNode classNode)  {
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

    private void processExpression(ClassNode classNode, String propertyName, Expression expression) {
        if(expression instanceof ClosureExpression) {
            addPropertyChangeListener(classNode, propertyName, (ClosureExpression) expression);
        } else if(expression instanceof VariableExpression) {
            addPropertyChangeListener(classNode, propertyName, (VariableExpression) expression);
        } else {
            throw new RuntimeException("Internal error: wrong expression type. "+expression);
        }
    } 

    private void addPropertyChangeListener(ClassNode classNode, String propertyName, ClosureExpression closure) {
        InnerClassNode innerClass = makePropertyChangeListenerClass(classNode, closure);
        classNode.getModule().addClass(innerClass);
        
        ArgumentListExpression args = new ArgumentListExpression();
        if(propertyName != null) args.addExpression(new ConstantExpression(propertyName));
        args.addExpression(new ConstructorCallExpression(innerClass, ArgumentListExpression.EMPTY_ARGUMENTS));
        addListenerStatement(classNode, args);
    }
    
    private void addPropertyChangeListener(ClassNode classNode, String propertyName, VariableExpression variable) {
        ArgumentListExpression args = new ArgumentListExpression();
        if(propertyName != null) args.addExpression(new ConstantExpression(propertyName));
        args.addExpression(CastExpression.asExpression(PROPERTY_CHANGE_LISTENER, variable));

        addListenerStatement(classNode, args);
    }

    private InnerClassNode makePropertyChangeListenerClass(ClassNode classNode, ClosureExpression closure) {
        String className = calculateListenerClassName(classNode);

        InnerClassNode listenerClass = new InnerClassNode(
            classNode,
            className,
            Modifier.PRIVATE | Modifier.STATIC,
            ClassHelper.OBJECT_TYPE,
            new ClassNode[]{PROPERTY_CHANGE_LISTENER},
            MixinNode.EMPTY_ARRAY
        );

        String fieldName = "listener_closure";
        listenerClass.addField(new FieldNode(
            fieldName,
            Modifier.PRIVATE | Modifier.FINAL,
            ClassHelper.CLOSURE_TYPE,
            listenerClass,
            closure
        ));

        listenerClass.addMethod(new MethodNode(
            "propertyChange",
            Modifier.PUBLIC,
            ClassHelper.VOID_TYPE,
            new Parameter[]{new Parameter(PROPERTY_CHANGE_EVENT, "event")},
            ClassNode.EMPTY_ARRAY,
            new ExpressionStatement(
                new MethodCallExpression(
                    VariableExpression.THIS_EXPRESSION,
                    fieldName,
                    new ArgumentListExpression(
                        new Expression[]{new VariableExpression("event")})
                )
            )
        ));

        return listenerClass;
    }

    private static String calculateListenerClassName(ClassNode classNode) {
        Integer count = LISTENER_COUNT.get(classNode.getName());
        if(count == null) {
            count = -1;
        }
        count++;
        LISTENER_COUNT.put(classNode.getName(), count);
        return classNode.getName() + "$PropertyChangeListener_" + count; 
    }

    private void addListenerStatement(ClassNode classNode, ArgumentListExpression args) {
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
