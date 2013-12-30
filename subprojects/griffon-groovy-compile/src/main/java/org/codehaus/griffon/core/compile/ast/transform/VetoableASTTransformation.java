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

import griffon.transform.Vetoable;
import org.codehaus.griffon.core.compile.VetoableConstants;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.STRING_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * Handles generation of code for the {@code @Vetoable} annotation.
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class VetoableASTTransformation extends AbstractASTTransformation implements VetoableConstants {
    private static final Logger LOG = LoggerFactory.getLogger(VetoableASTTransformation.class);
    private static final ClassNode VETOABLE_CNODE = makeClassSafe(VETOABLE_TYPE);
    private static final ClassNode VETOABLE_ANNOTATION_CNODE = makeClassSafe(Vetoable.class);

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
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        ObservableASTTransformation.addObservableIfNeeded(source, (ClassNode) nodes[1]);
        addVetoableIfNeeded(source, (ClassNode) nodes[1]);
    }

    public static void addVetoableIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsDelegate(classNode, source, VETOABLE_METHODS, "Vetoable", VETOABLE_TYPE)) {
            LOG.debug("Injecting {} into {}", VETOABLE_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    /**
     * Adds the necessary field and methods to support resource locating.
     *
     * @param classNode the class to which we add the support field and methods
     */
    public static void apply(ClassNode classNode) {
        injectInterface(classNode, VETOABLE_CNODE);

        ClassNode vcsClassNode = makeClassSafe(VETOABLE_CHANGE_SUPPORT_TYPE);
        ClassNode pceClassNode = makeClassSafe(PROPERTY_CHANGE_EVENT_TYPE);

        // add field:
        // protected final VetoableChangeSupport this$vetoableChangeSupport = new java.beans.VetoableChangeSupport(this)
        FieldNode vcsField = injectField(classNode,
            VETOABLE_CHANGE_SUPPORT_FIELD_NAME,
            Modifier.FINAL | Modifier.PROTECTED,
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
                Modifier.PROTECTED,
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
                Modifier.PROTECTED,
                VOID_TYPE,
                params(param(pceClassNode, EVENT)),
                NO_EXCEPTIONS,
                stmnt(call(
                    field(vcsField),
                    METHOD_FIRE_VETOABLE_CHANGE,
                    args(var(EVENT))))));
    }
}