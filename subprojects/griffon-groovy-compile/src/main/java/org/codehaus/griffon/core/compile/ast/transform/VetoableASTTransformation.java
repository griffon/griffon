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
package org.codehaus.griffon.core.compile.ast.transform;

import griffon.core.Vetoable;
import org.codehaus.griffon.core.compile.AnnotationHandler;
import org.codehaus.griffon.core.compile.AnnotationHandlerFor;
import org.codehaus.griffon.core.compile.VetoableConstants;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.lang.reflect.Modifier;

import static griffon.util.GriffonNameUtils.getGetterName;
import static griffon.util.GriffonNameUtils.getSetterName;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PROTECTED;
import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.*;
import static org.codehaus.griffon.core.compile.ast.transform.ObservableASTTransformation.hasObservableAnnotation;
import static org.codehaus.griffon.core.compile.ast.transform.ObservableASTTransformation.needsObservableSupport;
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * Handles generation of code for the {@code @Vetoable} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(griffon.transform.Vetoable.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class VetoableASTTransformation extends AbstractASTTransformation implements VetoableConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(VetoableASTTransformation.class);
    private static final ClassNode VETOABLE_CNODE = makeClassSafe(Vetoable.class);
    private static final ClassNode PROPERTY_VETO_EXCEPTION_CNODE = makeClassSafe(PropertyVetoException.class);
    private static final ClassNode VETOABLE_ANNOTATION_CNODE = makeClassSafe(griffon.transform.Vetoable.class);

    /**
     * Convenience method to see if an annotated node is {@code @Vetoable}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasVetoableAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (VETOABLE_ANNOTATION_CNODE.equals(annotation.getClassNode())) {
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
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: $node.class / $parent.class");
        }
        AnnotationNode node = (AnnotationNode) nodes[0];

        if (nodes[1] instanceof ClassNode) {
            addVetoableIfNeeded(source, (ClassNode) nodes[1]);
        } else {
            if ((((FieldNode) nodes[1]).getModifiers() & Modifier.FINAL) != 0) {
                source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
                    new SyntaxException("@griffon.transform.Vetoable cannot annotate a final property.",
                        node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()),
                    source));
            }

            addVetoableIfNeeded(source, node, (AnnotatedNode) nodes[1]);
        }
    }

    public static boolean needsVetoableSupport(ClassNode classNode, SourceUnit source) {
        return needsDelegate(classNode, source, VETOABLE_METHODS, "Vetoable", VETOABLE_TYPE);
    }

    public static void addVetoableIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsVetoableSupport(classNode, source)) {
            LOG.debug("Injecting {} into {}", VETOABLE_TYPE, classNode.getName());
            apply(classNode);
        }

        boolean bindable = hasObservableAnnotation(classNode);
        for (PropertyNode propertyNode : classNode.getProperties()) {
            if (!hasVetoableAnnotation(propertyNode.getField())
                && ((propertyNode.getField().getModifiers() & Modifier.FINAL) == 0)
                && !propertyNode.getField().isStatic()) {
                createListenerSetter(source,
                    bindable || hasObservableAnnotation(propertyNode.getField()),
                    classNode, propertyNode);
            }
        }
    }

    private void addVetoableIfNeeded(SourceUnit source, AnnotationNode node, AnnotatedNode parent) {
        ClassNode declaringClass = parent.getDeclaringClass();
        FieldNode field = ((FieldNode) parent);
        String fieldName = field.getName();
        for (PropertyNode propertyNode : declaringClass.getProperties()) {
            boolean bindable = hasObservableAnnotation(parent)
                || hasObservableAnnotation(parent.getDeclaringClass());

            if (propertyNode.getName().equals(fieldName)) {
                if (field.isStatic()) {
                    source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
                        new SyntaxException("@griffon.transform.Vetoable cannot annotate a static property.",
                            node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()),
                        source));
                } else {
                    createListenerSetter(source, bindable, declaringClass, propertyNode);
                }
                return;
            }
        }
        source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
            new SyntaxException("@griffon.transform.Vetoable must be on a property, not a field.  Try removing the private, protected, or public modifier.",
                node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()),
            source));
    }

    private static void createListenerSetter(SourceUnit source, boolean bindable, ClassNode declaringClass, PropertyNode propertyNode) {
        if (bindable && needsObservableSupport(declaringClass, source)) {
            ObservableASTTransformation.apply(declaringClass);
        }
        if (needsVetoableSupport(declaringClass, source)) {
            apply(declaringClass);
        }
        String setterName = getSetterName(propertyNode.getName());
        if (declaringClass.getMethods(setterName).isEmpty()) {
            Expression fieldExpression = new FieldExpression(propertyNode.getField());
            BlockStatement setterBlock = new BlockStatement();
            setterBlock.addStatement(createConstrainedStatement(propertyNode, fieldExpression));
            // if (bindable) {
            setterBlock.addStatement(createBindableStatement(propertyNode, fieldExpression));
            // } else {
            //    setterBlock.addStatement(assigns(fieldExpression, var(VALUE)));
            // }

            // create method void <setter>(<type> fieldName)
            createSetterMethod(declaringClass, propertyNode, setterName, setterBlock);
        } else {
            wrapSetterMethod(declaringClass, bindable, propertyNode.getName());
        }
    }

    private static Statement createConstrainedStatement(PropertyNode propertyNode, Expression fieldExpression) {
        return stmnt(call(
            THIS,
            METHOD_FIRE_VETOABLE_CHANGE,
            args(constx(propertyNode.getName()), fieldExpression, var(VALUE))));
    }

    private static Statement createBindableStatement(PropertyNode propertyNode, Expression fieldExpression) {
        // create statementBody
        return stmnt(call(
            THIS,
            METHOD_FIRE_PROPERTY_CHANGE,
            args(
                constx(propertyNode.getName()),
                fieldExpression,
                assign(fieldExpression, var(VALUE)))));
    }

    private static void createSetterMethod(ClassNode declaringClass, PropertyNode propertyNode, String setterName, Statement setterBlock) {
        MethodNode setter = new MethodNode(
            setterName,
            propertyNode.getModifiers(),
            VOID_TYPE,
            params(param(propertyNode.getType(), VALUE)),
            new ClassNode[]{PROPERTY_VETO_EXCEPTION_CNODE},
            setterBlock);
        setter.setSynthetic(true);
        // add it to the class
        declaringClass.addMethod(setter);
    }

    private static void wrapSetterMethod(ClassNode classNode, boolean bindable, String propertyName) {
        String getterName = getGetterName(propertyName);
        MethodNode setter = classNode.getSetterMethod(getSetterName(propertyName));

        if (setter != null) {
            // Get the existing code block
            Statement code = setter.getCode();

            VariableExpression oldValue = new VariableExpression("$oldValue");
            VariableExpression newValue = new VariableExpression("$newValue");
            VariableExpression proposedValue = new VariableExpression(setter.getParameters()[0].getName());
            BlockStatement block = new BlockStatement();

            // create a local variable to hold the old value from the getter
            block.addStatement(decls(oldValue, call(THIS, getterName, NO_ARGS)));

            // add the fireVetoableChange method call
            block.addStatement(new ExpressionStatement(new MethodCallExpression(
                THIS,
                METHOD_FIRE_VETOABLE_CHANGE,
                args(constx(propertyName), oldValue, proposedValue))));

            // call the existing block, which will presumably set the value properly
            block.addStatement(code);

            if (bindable) {
                // get the new value to emit in the event
                block.addStatement(decls(newValue, call(THIS, getterName, NO_ARGS)));

                // add the firePropertyChange method call
                block.addStatement(new ExpressionStatement(new MethodCallExpression(
                    THIS,
                    METHOD_FIRE_PROPERTY_CHANGE,
                    args(constx(propertyName), oldValue, newValue))));
            }

            // replace the existing code block with our new one
            setter.setCode(block);
        }
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     *
     * @param classNode the class to which we add the support field and methods
     */
    public static void apply(ClassNode classNode) {
        injectInterface(classNode, VETOABLE_CNODE);

        ClassNode vcsClassNode = makeClassSafe(VetoableChangeSupport.class);
        ClassNode pceClassNode = makeClassSafe(PropertyChangeEvent.class);

        // add field:
        // protected final VetoableChangeSupport this$vetoableChangeSupport = new java.beans.VetoableChangeSupport(this)
        FieldNode vcsField = injectField(classNode,
            VETOABLE_CHANGE_SUPPORT_FIELD_NAME,
            FINAL | PROTECTED,
            vcsClassNode,
            ctor(vcsClassNode, args(THIS)));

        addDelegateMethods(classNode, VETOABLE_CNODE, new FieldExpression(vcsField));

        // add method:
        // void firePropertyChange(String name, Object oldValue, Object newValue) {
        //     this$vetoableChangeSupport.firePropertyChange(name, oldValue, newValue)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_VETOABLE_CHANGE,
                PROTECTED,
                VOID_TYPE,
                params(
                    param(STRING_TYPE, NAME),
                    param(makeClassSafe(OBJECT_TYPE), OLD_VALUE),
                    param(OBJECT_TYPE, NEW_VALUE)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_FIRE_VETOABLE_CHANGE,
                    args(var(NAME), var(OLD_VALUE), var(NEW_VALUE))))));

        // add method:
        // void firePropertyChange(PropertyChangeEvent event) {
        //     this$vetoableChangeSupport.firePropertyChange(event)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_VETOABLE_CHANGE,
                PROTECTED,
                VOID_TYPE,
                params(param(pceClassNode, EVENT)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_FIRE_VETOABLE_CHANGE,
                    args(var(EVENT))))));
    }
}