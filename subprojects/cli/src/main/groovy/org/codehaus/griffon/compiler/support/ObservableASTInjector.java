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

package org.codehaus.griffon.compiler.support;

import griffon.core.Observable;
import groovy.beans.BindableASTTransformation;
import groovy.beans.VetoableASTTransformation;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andres Almiray 
 *
 * @since 0.9.1
 */
public class ObservableASTInjector implements ASTInjector {
    private static final Logger LOG = LoggerFactory.getLogger(ObservableASTInjector.class);
    private static final ClassNode OBSERVABLE_CLASS = ClassHelper.makeWithoutCaching(Observable.class);

    public void inject(ClassNode classNode, String artifactType) {
        if(!classNode.implementsInterface(OBSERVABLE_CLASS)){
            classNode.addInterface(OBSERVABLE_CLASS);
        }

        if(isBindableOrVetoable(classNode)) return;

        for(FieldNode fieldNode : classNode.getFields()) {
            if(isBindableOrVetoable(fieldNode)) return;
        }

        if(LOG.isDebugEnabled()) LOG.debug("Injecting "+OBSERVABLE_CLASS.getName()+" behavior to "+ classNode.getName());
    
        ClassNode pcsClassNode = ClassHelper.makeWithoutCaching(PropertyChangeSupport.class);
        ClassNode pclClassNode = ClassHelper.makeWithoutCaching(PropertyChangeListener.class);
        ClassNode pceClassNode = ClassHelper.makeWithoutCaching(PropertyChangeEvent.class);

        //String pcsFieldName = "pcs";

        // add field:
        // protected final PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this)
        FieldNode pcsField = classNode.addField(
                "pcs",
                ACC_FINAL | ACC_PROTECTED,
                pcsClassNode,
                new ConstructorCallExpression(pcsClassNode,
                        new ArgumentListExpression(new Expression[]{new VariableExpression("this")})));

        // add method:
        // void addPropertyChangeListener(listener) {
        //     pcs.addPropertyChangeListener(listener)
        //  }
        classNode.addMethod(
                new MethodNode(
                        "addPropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(pclClassNode, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(pcsField),
                                        "addPropertyChangeListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("listener")})))));

        // add method:
        // void addPropertyChangeListener(name, listener) {
        //     pcs.addPropertyChangeListener(name, listener)
        //  }
        classNode.addMethod(
                new MethodNode(
                        "addPropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(pclClassNode, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(pcsField),
                                        "addPropertyChangeListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("name"), new VariableExpression("listener")})))));

        // add method:
        // boolean removePropertyChangeListener(listener) {
        //    return pcs.removePropertyChangeListener(listener);
        // }
        classNode.addMethod(
                new MethodNode(
                        "removePropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(pclClassNode, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(pcsField),
                                        "removePropertyChangeListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("listener")})))));

        // add method: void removePropertyChangeListener(name, listener)
        classNode.addMethod(
                new MethodNode(
                        "removePropertyChangeListener",
                        ACC_PUBLIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(pclClassNode, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(pcsField),
                                        "removePropertyChangeListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("name"), new VariableExpression("listener")})))));

        // add method:
        // void firePropertyChange(String name, Object oldValue, Object newValue) {
        //     pcs.firePropertyChange(name, oldValue, newValue)
        //  }
        classNode.addMethod(
                new MethodNode(
                        "firePropertyChange",
                        ACC_PROTECTED,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.OBJECT_TYPE, "oldValue"), new Parameter(ClassHelper.OBJECT_TYPE, "newValue")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(pcsField),
                                        "firePropertyChange",
                                        new ArgumentListExpression(
                                                new Expression[]{
                                                        new VariableExpression("name"),
                                                        new VariableExpression("oldValue"),
                                                        new VariableExpression("newValue")})))));

        // add method:
        // void firePropertyChange(PropertyChangeEvent event) {
        //     pcs.firePropertyChange(event)
        //  }
        classNode.addMethod(
                new MethodNode(
                        "firePropertyChange",
                        ACC_PROTECTED,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(pceClassNode, "name")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(pcsField),
                                        "firePropertyChange",
                                        new ArgumentListExpression(
                                                new Expression[]{
                                                        new VariableExpression("event")})))));

        // add method:
        // PropertyChangeListener[] getPropertyChangeListeners() {
        //   return pcs.getPropertyChangeListeners
        // }
        classNode.addMethod(
                new MethodNode(
                        "getPropertyChangeListeners",
                        ACC_PUBLIC,
                        pclClassNode.makeArray(),
                        Parameter.EMPTY_ARRAY,
                        ClassNode.EMPTY_ARRAY,
                        new ReturnStatement(
                                new ExpressionStatement(
                                        new MethodCallExpression(
                                                new FieldExpression(pcsField),
                                                "getPropertyChangeListeners",
                                                ArgumentListExpression.EMPTY_ARGUMENTS)))));

        // add method:
        // PropertyChangeListener[] getPropertyChangeListeners(String name) {
        //   return pcs.getPropertyChangeListeners(name)
        // }
        classNode.addMethod(
                new MethodNode(
                        "getPropertyChangeListeners",
                        ACC_PUBLIC,
                        pclClassNode.makeArray(),
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name")},
                        ClassNode.EMPTY_ARRAY,
                        new ReturnStatement(
                                new ExpressionStatement(
                                        new MethodCallExpression(
                                                new FieldExpression(pcsField),
                                                "getPropertyChangeListeners",
                                                new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("name")}))))));
    }

    private static boolean isBindableOrVetoable(AnnotatedNode node) {
        if(VetoableASTTransformation.hasVetoableAnnotation(node) ||
           BindableASTTransformation.hasBindableAnnotation(node)) {
            return true;
        }
        return false;
    } 
}