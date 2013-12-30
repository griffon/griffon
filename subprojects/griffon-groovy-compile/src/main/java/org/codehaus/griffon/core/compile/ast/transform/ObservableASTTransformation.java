/*
 * Copyright 2009-2014 the original author or authors.
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

import griffon.transform.Observable;
import org.codehaus.griffon.core.compile.ObservableConstants;
import org.codehaus.griffon.core.compile.ast.GriffonASTUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

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
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ObservableASTTransformation extends AbstractASTTransformation implements ObservableConstants {
    private static final Logger LOG = LoggerFactory.getLogger(ObservableASTTransformation.class);
    private static final ClassNode OBSERVABLE_CNODE = makeClassSafe(OBSERVABLE_TYPE);
    private static final ClassNode OBSERVABLE_ANNOTATION_CNODE = makeClassSafe(Observable.class);

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
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addObservableIfNeeded(source, (ClassNode) nodes[1]);
    }

    public static void addObservableIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsDelegate(classNode, source, OBSERVABLE_METHODS, "Observable", OBSERVABLE_TYPE)) {
            LOG.debug("Injecting {} into {}", OBSERVABLE_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     *
     * @param classNode the class to which we add the support field and methods
     */
    public static void apply(ClassNode classNode) {
        injectInterface(classNode, OBSERVABLE_CNODE);

        ClassNode pcsClassNode = makeClassSafe(PROPERTY_CHANGE_SUPPORT_TYPE);
        ClassNode pceClassNode = makeClassSafe(PROPERTY_CHANGE_EVENT_TYPE);

        // add field:
        // protected final PropertyChangeSupport this$propertyChangeSupport = new java.beans.PropertyChangeSupport(this)
        FieldNode pcsField = injectField(classNode,
            PROPERTY_CHANGE_SUPPORT_FIELD_NAME,
            Modifier.PUBLIC | PROTECTED,
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