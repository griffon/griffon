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
package org.codehaus.griffon.compile.scaffolding.ast.transform;

import griffon.plugins.scaffolding.CommandObject;
import org.codehaus.griffon.compile.domain.ast.transform.ValidateableASTTransformation;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public abstract class CommandObjectASTTransformation extends ValidateableASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(CommandObjectASTTransformation.class);
    protected static final ClassNode COMMAND_OBJECT_CNODE = makeClassSafe(CommandObject.class);
    protected static final ClassNode COMMAND_OBJECT_ANNOTATION = makeClassSafe(griffon.transform.CommandObject.class);

    /**
     * Convenience method to see if an annotated node is {@code @CommandObject}.
     *
     * @param node the node to check
     * @return true if the node is annotated with @CommandObject
     */
    public static boolean hasCommandObjectAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (COMMAND_OBJECT_ANNOTATION.equals(annotation.getClassNode())) {
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

        ClassNode classNode = (ClassNode) nodes[1];

        if (needsValidateable(classNode, source)) {
            LOG.debug("Injecting {} into {}", CommandObject.class.getName(), classNode.getName());
            injectInterface(classNode, COMMAND_OBJECT_CNODE);
            addValidatableBehavior(classNode, source);
            addCommandObjectBehavior(classNode, source);
        }
    }

    protected abstract void addCommandObjectBehavior(ClassNode classNode, SourceUnit source);
}
