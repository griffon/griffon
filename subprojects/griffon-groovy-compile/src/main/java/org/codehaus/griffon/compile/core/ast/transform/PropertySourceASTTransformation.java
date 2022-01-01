/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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

import griffon.core.properties.PropertySource;
import org.codehaus.griffon.compile.beans.PropertySourceConstants;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.ast.GriffonASTUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
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

import static griffon.util.StringUtils.getGetterName;
import static griffon.util.StringUtils.getSetterName;
import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PROTECTED;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_ARGS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_EXCEPTIONS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.THIS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.args;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.assign;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.call;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.constx;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.ctor;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.decls;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.field;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectField;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectMethod;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.param;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.params;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.stmnt;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.var;
import static org.codehaus.griffon.compile.core.ast.transform.VetoableASTTransformation.hasVetoableAnnotation;
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * Handles generation of code for the {@code @PropertySource} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(griffon.annotations.beans.PropertySource.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class PropertySourceASTTransformation extends AbstractASTTransformation implements PropertySourceConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PropertySourceASTTransformation.class);
    private static final ClassNode PROPERTY_SOURCE_CNODE = makeClassSafe(PropertySource.class);
    private static final ClassNode PROPERTY_SOURCE_ANNOTATION_CNODE = makeClassSafe(griffon.annotations.beans.PropertySource.class);

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new IllegalArgumentException("Internal error: wrong types: "
                + nodes[0].getClass().getName() + " / " + nodes[1].getClass().getName());
        }
        AnnotationNode node = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];

        if (hasVetoableAnnotation(parent)) {
            // VetoableASTTransformation will handle both @PropertySource and @VetoablePropertySource
            return;
        }

        ClassNode declaringClass = parent.getDeclaringClass();
        if (parent instanceof FieldNode) {
            if ((((FieldNode) parent).getModifiers() & Modifier.FINAL) != 0) {
                source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
                    new SyntaxException("@griffon.transform.beans.PropertySource cannot annotate a final property.",
                        node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()),
                    source));
            }

            if (hasVetoableAnnotation(parent.getDeclaringClass())) {
                // VetoableASTTransformation will handle both @PropertySource and @VetoablePropertySource
                return;
            }
            addPropertySourceIfNeeded(source, node, declaringClass, (FieldNode) parent);
        } else if (parent instanceof ClassNode) {
            addPropertySourceIfNeeded(source, (ClassNode) parent);
        }
    }

    /**
     * Convenience method to see if an annotated node is {@code @PropertySource}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasPropertySourceAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (PROPERTY_SOURCE_ANNOTATION_CNODE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    public static boolean needsPropertySourceSupport(ClassNode classNode, SourceUnit source) {
        return needsDelegate(classNode, source, OBSERVABLE_METHODS, "PropertySource", PROPERTY_SOURCE_TYPE);
    }

    public static void addPropertySourceIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsPropertySourceSupport(classNode, source)) {
            LOG.debug("Injecting {} into {}", PROPERTY_SOURCE_TYPE, classNode.getName());
            apply(classNode);
        }

        for (PropertyNode propertyNode : classNode.getProperties()) {
            FieldNode field = propertyNode.getField();
            // look to see if per-field handlers will catch this one...
            if (hasPropertySourceAnnotation(field)
                || ((field.getModifiers() & Modifier.FINAL) != 0)
                || field.isStatic()
                || hasVetoableAnnotation(field)) {
                // explicitly labeled properties are already handled,
                // don't transform final properties
                // don't transform static properties
                // VetoablePropertySourceASTTransformation will handle both @PropertySource and @VetoablePropertySource
                continue;
            }
            createListenerSetter(classNode, propertyNode);
        }
    }

    public static void addPropertySourceIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode, FieldNode field) {
        String fieldName = field.getName();
        for (PropertyNode propertyNode : classNode.getProperties()) {
            if (propertyNode.getName().equals(fieldName)) {
                if (field.isStatic()) {
                    //noinspection ThrowableInstanceNeverThrown
                    source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
                        new SyntaxException("@griffon.transform.PropertySource cannot annotate a static property.",
                            annotationNode.getLineNumber(), annotationNode.getColumnNumber(), annotationNode.getLastLineNumber(), annotationNode.getLastColumnNumber()),
                        source));
                } else {
                    if (needsPropertySourceSupport(classNode, source)) {
                        LOG.debug("Injecting {} into {}", PROPERTY_SOURCE_TYPE, classNode.getName());
                        apply(classNode);
                    }
                    createListenerSetter(classNode, propertyNode);
                }
                return;
            }
        }
        //noinspection ThrowableInstanceNeverThrown
        source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(
            new SyntaxException("@griffon.transform.PropertySource must be on a property, not a field. Try removing the private, protected, or public modifier.",
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
        injectInterface(classNode, PROPERTY_SOURCE_CNODE);

        ClassNode pcsClassNode = makeClassSafe(PropertyChangeSupport.class);
        ClassNode pceClassNode = makeClassSafe(PropertyChangeEvent.class);

        // add field:
        // protected final PropertyChangeSupport this$propertyChangeSupport = new java.beans.PropertyChangeSupport(this)
        FieldNode pcsField = injectField(classNode,
            PROPERTY_CHANGE_SUPPORT_FIELD_NAME,
            FINAL | PROTECTED,
            pcsClassNode,
            ctor(pcsClassNode, args(GriffonASTUtils.THIS)));

        addDelegateMethods(classNode, PROPERTY_SOURCE_CNODE, new FieldExpression(pcsField));

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