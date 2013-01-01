/*
 * Copyright 2010-2013 the original author or authors.
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

import griffon.core.Vetoable;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;
import static org.codehaus.groovy.ast.expr.VariableExpression.THIS_EXPRESSION;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class VetoableASTInjector extends ObservableASTInjector {
    private static final Logger LOG = LoggerFactory.getLogger(VetoableASTInjector.class);
    private static final ClassNode VETOABLE_TYPE = makeClassSafe(Vetoable.class);

    private static final String VETOABLE_CHANGE_SUPPORT_FIELD_NAME = "this$vetoableChangeSupport";
    private static final String METHOD_ADD_VETOABLE_CHANGE_LISTENER = "addVetoableChangeListener";
    private static final String METHOD_REMOVE_VETOABLE_CHANGE_LISTENER = "removeVetoableChangeListener";
    private static final String METHOD_GET_VETOABLE_CHANGE_LISTENERS = "getVetoableChangeListeners";
    private static final String METHOD_FIRE_VETOABLE_CHANGE = "fireVetoableChange";

    public void inject(ClassNode classNode, String artifactType) {
        if (isBindableOrVetoable(classNode)) return;
        for (FieldNode fieldNode : classNode.getFields()) {
            if (isBindableOrVetoable(fieldNode)) return;
        }

        if (!classNode.implementsInterface(VETOABLE_TYPE)) {
            classNode.addInterface(VETOABLE_TYPE);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Injecting " + VETOABLE_TYPE.getName() + " behavior to " + classNode.getName());
        }

        injectVetoable(classNode);
        injectObservable(classNode);
    }

    protected void injectVetoable(ClassNode classNode) {
        ClassNode vcsClassNode = makeClassSafe(VetoableChangeSupport.class);
        ClassNode vclClassNode = makeClassSafe(VetoableChangeListener.class);
        ClassNode pceClassNode = makeClassSafe(PropertyChangeEvent.class);

        // add field:
        // protected final VetoableChangeSupport this$vetoableChangeSupport = new java.beans.VetoableChangeSupport(this)
        FieldNode vcsField = injectField(classNode,
            VETOABLE_CHANGE_SUPPORT_FIELD_NAME,
            ACC_FINAL | ACC_PROTECTED,
            vcsClassNode,
            ctor(vcsClassNode, args(THIS_EXPRESSION)));

        // add method:
        // void addVetoableChangeListener(listener) {
        //     vcs.addVetoableChangeListener(listener)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_ADD_VETOABLE_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(param(vclClassNode, LISTENER)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_ADD_VETOABLE_CHANGE_LISTENER,
                    args(var(LISTENER))))));

        // add method:
        // void addVetoableChangeListener(name, listener) {
        //     vcs.addVetoableChangeListener(name, listener)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_ADD_VETOABLE_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                    param(STRING_TYPE, NAME),
                    param(vclClassNode, LISTENER)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_ADD_VETOABLE_CHANGE_LISTENER,
                    args(var(NAME), var(LISTENER))))));

        // add method:
        // void removeVetoableChangeListener(listener) {
        //    return vcs.removeVetoableChangeListener(listener);
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_REMOVE_VETOABLE_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(param(vclClassNode, LISTENER)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_REMOVE_VETOABLE_CHANGE_LISTENER,
                    args(var(LISTENER))))));

        // add method:
        // void removeVetoableChangeListener(name, listener) {
        //    return vcs.removeVetoableChangeListener(name, listener);
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_REMOVE_VETOABLE_CHANGE_LISTENER,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                    param(STRING_TYPE, NAME),
                    param(vclClassNode, LISTENER)), NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_REMOVE_VETOABLE_CHANGE_LISTENER,
                    args(var(NAME), var(LISTENER))))));

        // add method:
        // void firePropertyChange(String name, Object oldValue, Object newValue) {
        //     vcs.firePropertyChange(name, oldValue, newValue)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_VETOABLE_CHANGE,
                ACC_PROTECTED,
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
        //     vcs.firePropertyChange(event)
        //  }
        injectMethod(classNode,
            new MethodNode(
                METHOD_FIRE_VETOABLE_CHANGE,
                ACC_PROTECTED,
                VOID_TYPE,
                params(param(pceClassNode, EVENT)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_FIRE_VETOABLE_CHANGE,
                    args(var(EVENT))))));

        // add method:
        // VetoableChangeListener[] getVetoableChangeListeners() {
        //   return vcs.getVetoableChangeListeners
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_GET_VETOABLE_CHANGE_LISTENERS,
                ACC_PUBLIC,
                vclClassNode.makeArray(),
                NO_PARAMS,
                NO_EXCEPTIONS,
                returns(call(
                    field(vcsField),
                    METHOD_GET_VETOABLE_CHANGE_LISTENERS,
                    NO_ARGS))));

        // add method:
        // VetoableChangeListener[] getVetoableChangeListeners(String name) {
        //   return vcs.getVetoableChangeListeners(name)
        // }
        injectMethod(classNode,
            new MethodNode(
                METHOD_GET_VETOABLE_CHANGE_LISTENERS,
                ACC_PUBLIC,
                vclClassNode.makeArray(),
                params(param(STRING_TYPE, NAME)),
                NO_EXCEPTIONS,
                returns(call(
                    field(vcsField),
                    METHOD_GET_VETOABLE_CHANGE_LISTENERS,
                    args(var(NAME))))));
    }
}