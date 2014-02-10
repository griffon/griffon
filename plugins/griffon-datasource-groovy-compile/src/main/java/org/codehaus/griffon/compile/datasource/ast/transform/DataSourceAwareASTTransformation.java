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
package org.codehaus.griffon.compile.datasource.ast.transform;

import griffon.plugins.datasource.DataSourceHandler;
import griffon.transform.DataSourceAware;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.griffon.compile.core.AnnotationHandlerFor;
import org.codehaus.griffon.compile.core.ast.transform.AbstractASTTransformation;
import org.codehaus.griffon.compile.datasource.DataSourceAwareConstants;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectInterface;

/**
 * Handles generation of code for the {@code @DataSourceAware} annotation.
 *
 * @author Andres Almiray
 */
@AnnotationHandlerFor(DataSourceAware.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class DataSourceAwareASTTransformation extends AbstractASTTransformation implements DataSourceAwareConstants, AnnotationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DataSourceAwareASTTransformation.class);
    private static final ClassNode DATA_SOURCE_HANDLER_CNODE = makeClassSafe(DataSourceHandler.class);
    private static final ClassNode DATA_SOURCE_AWARE_CNODE = makeClassSafe(DataSourceAware.class);

    /**
     * Convenience method to see if an annotated node is {@code @DataSourceAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasDataSourceAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (DATA_SOURCE_AWARE_CNODE.equals(annotation.getClassNode())) {
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
        addDataSourceHandlerIfNeeded(source, (AnnotationNode) nodes[0], (ClassNode) nodes[1]);
    }

    public static void addDataSourceHandlerIfNeeded(SourceUnit source, AnnotationNode annotationNode, ClassNode classNode) {
        if (needsDelegate(classNode, source, METHODS, "DataSourceAware", DATA_SOURCE_HANDLER_TYPE)) {
            LOG.debug("Injecting {} into {}", DATA_SOURCE_TYPE, classNode.getName());
            apply(classNode);
        }
    }

    /**
     * Adds the necessary field and methods to support dataSource handling.
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    public static void apply(@Nonnull ClassNode declaringClass) {
        injectInterface(declaringClass, DATA_SOURCE_HANDLER_CNODE);
        Expression dataSourceHandler = injectedField(declaringClass, DATA_SOURCE_HANDLER_CNODE, "this$" + DATA_SOURCE_HANDLER_PROPERTY);
        addDelegateMethods(declaringClass, DATA_SOURCE_HANDLER_CNODE, dataSourceHandler);
    }
}