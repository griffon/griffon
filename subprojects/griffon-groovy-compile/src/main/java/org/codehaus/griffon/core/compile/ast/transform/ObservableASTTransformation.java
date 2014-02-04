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

import griffon.core.Observable;
import org.codehaus.griffon.core.compile.AnnotationHandler;
import org.codehaus.griffon.core.compile.AnnotationHandlerFor;
import org.codehaus.griffon.core.compile.ObservableConstants;
import org.codehaus.griffon.core.compile.ast.GriffonASTUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Modifier;

import static griffon.util.GriffonNameUtils.getGetterName;
import static griffon.util.GriffonNameUtils.getSetterName;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PROTECTED;
import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * Handles generation of code for the {@code @Observable} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(griffon.transform.Observable.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ObservableASTTransformation extends AbstractASTTransformation implements ObservableConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ObservableASTTransformation.class);
    private static final ClassNode OBSERVABLE_CNODE = makeClassSafe(Observable.class);
    private static final ClassNode OBSERVABLE_ANNOTATION_CNODE = makeClassSafe(griffon.transform.Observable.class);

    /**
     * Convenience method to see if an annotated node is {@code @Observable}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasObservableAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (OBSERVABLE_ANNOTATION_CNODE.equals(annotation.getClassNode())) {
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
        // addObservableIfNeeded(source, (ClassNode) nodes[1]);
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: $node.class / $parent.class");
        }
        AnnotationNode node = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];

        /*
        if (VetoableASTTransformation.hasVetoableAnnotation(parent)) {
            // VetoableASTTransformation will handle both @Observable and @Vetoable
            return;
        }
        */

        ClassNode declaringClass = parent.getDeclaringClass();
        if (parent instanceof FieldNode) {
            if ((((FieldNode) parent).getModifiers() & Modifier.FINAL) != 0) {
                source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
                    new SyntaxException("@griffon.transform.Observable cannot annotate a final property.",
                        node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()),
                    source));
            }

            /*
            if (VetoableASTTransformation.hasVetoableAnnotation(parent.getDeclaringClass())) {
                // VetoableASTTransformation will handle both @Observable and @Vetoable
                return;
            }
            */
            addObservableIfNeeded(source, node, declaringClass, (FieldNode) parent);
        } else if (parent instanceof ClassNode) {
            addObservableIfNeeded(source, (ClassNode) parent);
        }
    }

    public static boolean needsObservableSupport(ClassNode classNode, SourceUnit source) {
        return needsDelegate(classNode, source, OBSERVABLE_METHODS, "Observable", OBSERVABLE_TYPE);
    }

    public static void addObservableIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsObservableSupport(classNode, source)) {
            LOG.debug("Injecting {} into {}", OBSERVABLE_TYPE, classNode.getName());
            apply(classNode);
        }

        for (PropertyNode propertyNode : classNode.getProperties()) {
            FieldNode field = propertyNode.getField();
            // look to see if per-field handlers will catch this one...
            if (hasObservableAnnotation(field)
                || ((field.getModifiers() & Modifier.FINAL) != 0)
                || field.isStatic()
                /*|| VetoableASTTransformation.hasVetoableAnnotation(field)*/) {
                // explicitly labeled properties are already handled,
                // don't transform final properties
                // don't transform static properties
                // VetoableASTTransformation will handle both @Observable and @Vetoable
                continue;
            }
            createListenerSetter(classNode, propertyNode);
        }
    }

    public static void addObservableIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode, FieldNode field) {
        String fieldName = field.getName();
        for (PropertyNode propertyNode : classNode.getProperties()) {
            if (propertyNode.getName().equals(fieldName)) {
                if (field.isStatic()) {
                    //noinspection ThrowableInstanceNeverThrown
                    source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
                        new SyntaxException("@griffon.transform.Observable cannot annotate a static property.",
                            annotationNode.getLineNumber(), annotationNode.getColumnNumber(), annotationNode.getLastLineNumber(), annotationNode.getLastColumnNumber()),
                        source));
                } else {
                    if (needsObservableSupport(classNode, source)) {
                        LOG.debug("Injecting {} into {}", OBSERVABLE_TYPE, classNode.getName());
                        apply(classNode);
                    }
                    createListenerSetter(classNode, propertyNode);
                }
                return;
            }
        }
        //noinspection ThrowableInstanceNeverThrown
        source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
            new SyntaxException("@griffon.transform.Observable must be on a property, not a field. Try removing the private, protected, or public modifier.",
                annotationNode.getLineNumber(), annotationNode.getColumnNumber(), annotationNode.getLastLineNumber(), annotationNode.getLastColumnNumber()),
            source));
    }

    private static void createListenerSetter(ClassNode classNode, PropertyNode propertyNode) {
        String setterName = getSetterName(propertyNode.getName());
        if (classNode.getMethods(setterName).isEmpty()) {
            Expression fieldExpression = new FieldExpression(propertyNode.getField());
            Statement setterBlock = createBindableStatement(propertyNode, fieldExpression);

            // create method void <setter>(<type> fieldName)
            createSetterMethod(classNode, propertyNode, setterName, setterBlock);
        } else {
            wrapSetterMethod(classNode, propertyNode.getName());
        }
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
            NO_EXCEPTIONS,
            setterBlock);
        setter.setSynthetic(true);
        // add it to the class
        declaringClass.addMethod(setter);
    }

    private static void wrapSetterMethod(ClassNode classNode, String propertyName) {
        String getterName = getGetterName(propertyName);
        MethodNode setter = classNode.getSetterMethod(getSetterName(propertyName));

        if (setter != null) {
            // Get the existing code block
            Statement code = setter.getCode();

            VariableExpression oldValue = new VariableExpression("$oldValue");
            VariableExpression newValue = new VariableExpression("$newValue");
            BlockStatement block = new BlockStatement();

            // create a local variable to hold the old value from the getter
            block.addStatement(decls(oldValue, call(THIS, getterName, NO_ARGS)));

            // call the existing block, which will presumably set the value properly
            block.addStatement(code);

            // get the new value to emit in the event
            block.addStatement(decls(newValue, call(THIS, getterName, NO_ARGS)));

            // add the firePropertyChange method call
            block.addStatement(stmnt(call(
                THIS,
                METHOD_FIRE_PROPERTY_CHANGE,
                args(constx(propertyName), oldValue, newValue))));

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
        injectInterface(classNode, OBSERVABLE_CNODE);

        ClassNode pcsClassNode = makeClassSafe(PropertyChangeSupport.class);
        ClassNode pceClassNode = makeClassSafe(PropertyChangeEvent.class);

        // add field:
        // protected final PropertyChangeSupport this$propertyChangeSupport = new java.beans.PropertyChangeSupport(this)
        FieldNode pcsField = injectField(classNode,
            PROPERTY_CHANGE_SUPPORT_FIELD_NAME,
            FINAL | PROTECTED,
            pcsClassNode,
            ctor(pcsClassNode, args(GriffonASTUtils.THIS)));

        addDelegateMethods(classNode, OBSERVABLE_CNODE, new FieldExpression(pcsField));

        // add method:
        // void firePropertyChange(String name, Object oldValue, Object newValue) {
        //     this$propertyChangeSupport.firePropertyChange(name, oldValue, newValue)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_PROPERTY_CHANGE,
                PROTECTED,
                VOID_TYPE,
                params(
                    param(STRING_TYPE, NAME),
                    param(makeClassSafe(OBJECT_TYPE), OLD_VALUE),
                    param(OBJECT_TYPE, NEW_VALUE)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(pcsField),
                    METHOD_FIRE_PROPERTY_CHANGE,
                    args(var(NAME), var(OLD_VALUE), var(NEW_VALUE))))));

        // add method:
        // void firePropertyChange(PropertyChangeEvent event) {
        //     this$propertyChangeSupport.firePropertyChange(event)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_PROPERTY_CHANGE,
                PROTECTED,
                VOID_TYPE,
                params(param(pceClassNode, EVENT)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(pcsField),
                    METHOD_FIRE_PROPERTY_CHANGE,
                    args(var(EVENT))))));
    }
}