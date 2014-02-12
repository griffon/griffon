/*
 * Copyright 2010-2014 the original author or authors.
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
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;
import static org.codehaus.groovy.ast.expr.VariableExpression.THIS_EXPRESSION;

/**
 * @author Andres Almiray
 * @since 0.9.1
 */
public class ObservableASTInjector extends AbstractASTInjector {
    private static final Logger LOG = LoggerFactory.getLogger(ObservableASTInjector.class);
    private static final ClassNode OBSERVABLE_TYPE = makeClassSafe(Observable.class);

    private static final String PROPERTY_CHANGE_SUPPORT_FIELD_NAME = "this$propertyChangeSupport";
    private static final String METHOD_ADD_PROPERTY_CHANGE_LISTENER = "addPropertyChangeListener";
    private static final String METHOD_REMOVE_PROPERTY_CHANGE_LISTENER = "removePropertyChangeListener";
    private static final String METHOD_GET_PROPERTY_CHANGE_LISTENERS = "getPropertyChangeListeners";
    private static final String METHOD_FIRE_PROPERTY_CHANGE = "firePropertyChange";
    protected static final String OLD_VALUE = "oldValue";
    protected static final String NEW_VALUE = "newValue";
    protected static final String LISTENER = "listener";
    protected static final String NAME = "name";
    protected static final String EVENT = "event";

    public void inject(ClassNode classNode, String artifactType) {
        if (isBindableOrVetoable(classNode)) return;
        for (FieldNode fieldNode : classNode.getFields()) {
            if (isBindableOrVetoable(fieldNode)) return;
        }

        if (!classNode.implementsInterface(OBSERVABLE_TYPE)) {
            classNode.addInterface(OBSERVABLE_TYPE);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Injecting " + OBSERVABLE_TYPE.getName() + " behavior to " + classNode.getName());
        }
        injectObservable(classNode);
    }

    protected void injectObservable(ClassNode classNode) {
        ClassNode pcsClassNode = makeClassSafe(PropertyChangeSupport.class);
        ClassNode pclClassNode = makeClassSafe(PropertyChangeListener.class);
        ClassNode pceClassNode = makeClassSafe(PropertyChangeEvent.class);

        // add field:
        // protected final PropertyChangeSupport this$propertyChangeSupport = new java.beans.PropertyChangeSupport(this)
        FieldNode pcsField = injectField(classNode,
            PROPERTY_CHANGE_SUPPORT_FIELD_NAME,
            ACC_FINAL | ACC_PROTECTED,
            pcsClassNode,
            ctor(pcsClassNode, args(THIS_EXPRESSION)));

        // add method:
        // void addPropertyChangeListener(listener) {
        //     pcs.addPropertyChangeListener(listener)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_ADD_PROPERTY_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(param(pclClassNode, LISTENER)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(pcsField),
                    METHOD_ADD_PROPERTY_CHANGE_LISTENER,
                    args(var(LISTENER))))));

        // add method:
        // void addPropertyChangeListener(name, listener) {
        //     pcs.addPropertyChangeListener(name, listener)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_ADD_PROPERTY_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                    param(STRING_TYPE, NAME),
                    param(pclClassNode, LISTENER)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(pcsField),
                    METHOD_ADD_PROPERTY_CHANGE_LISTENER,
                    args(var(NAME), var(LISTENER))))));

        // add method:
        // void removePropertyChangeListener(listener) {
        //    return pcs.removePropertyChangeListener(listener);
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_REMOVE_PROPERTY_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(param(pclClassNode, LISTENER)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(pcsField),
                    METHOD_REMOVE_PROPERTY_CHANGE_LISTENER,
                    args(var(LISTENER))))));

        // add method:
        // void removePropertyChangeListener(name, listener) {
        //    return pcs.removePropertyChangeListener(name, listener);
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_REMOVE_PROPERTY_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                    param(STRING_TYPE, NAME),
                    param(pclClassNode, LISTENER)), NO_EXCEPTIONS,
                stmnt(call(
                    field(pcsField),
                    METHOD_REMOVE_PROPERTY_CHANGE_LISTENER,
                    args(var(NAME), var(LISTENER))))));

        // add method:
        // void firePropertyChange(String name, Object oldValue, Object newValue) {
        //     pcs.firePropertyChange(name, oldValue, newValue)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_PROPERTY_CHANGE,
                ACC_PROTECTED,
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
        //     pcs.firePropertyChange(event)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_PROPERTY_CHANGE,
                ACC_PROTECTED,
                VOID_TYPE,
                params(param(pceClassNode, EVENT)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(pcsField),
                    METHOD_FIRE_PROPERTY_CHANGE,
                    args(var(EVENT))))));

        // add method:
        // PropertyChangeListener[] getPropertyChangeListeners() {
        //   return pcs.getPropertyChangeListeners
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_GET_PROPERTY_CHANGE_LISTENERS,
                ACC_PUBLIC,
                pclClassNode.makeArray(),
                NO_PARAMS,
                NO_EXCEPTIONS,
                returns(call(
                    field(pcsField),
                    METHOD_GET_PROPERTY_CHANGE_LISTENERS,
                    NO_ARGS))));

        // add method:
        // PropertyChangeListener[] getPropertyChangeListeners(String name) {
        //   return pcs.getPropertyChangeListeners(name)
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_GET_PROPERTY_CHANGE_LISTENERS,
                ACC_PUBLIC,
                pclClassNode.makeArray(),
                params(param(STRING_TYPE, NAME)),
                NO_EXCEPTIONS,
                returns(call(
                    field(pcsField),
                    METHOD_GET_PROPERTY_CHANGE_LISTENERS,
                    args(var(NAME))))));
    }

    protected static boolean isBindableOrVetoable(AnnotatedNode node) {
        return VetoableASTTransformation.hasVetoableAnnotation(node) ||
            BindableASTTransformation.hasBindableAnnotation(node);
    }
}