/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.util.ServiceLoaderUtils;
import org.codehaus.griffon.compile.core.AnnotationHandler;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(ASTTransformation.class)
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class AnnotationHandlerASTTransformation extends AbstractASTTransformation {
    private final Object lock = new Object();
    // @GuardedBy("lock")
    private boolean initialized;
    // @GuardedBy("lock")
    private final Map<String, Class<? extends ASTTransformation>> transformations = new LinkedHashMap<>();

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        synchronized (lock) {
            if (!initialized) {
                initialize(source);
                initialized = true;
            }
        }

        if (nodes.length > 0 && nodes[0] instanceof ModuleNode) {
            ModuleNode moduleNode = (ModuleNode) nodes[0];
            for (ClassNode classNode : moduleNode.getClasses()) {
                visitAnnotationsOnNode(classNode, classNode);
                for (MethodNode method : classNode.getMethods()) {
                    visitAnnotationsOnNode(method, classNode);
                }
                for (PropertyNode propertyNode : classNode.getProperties()) {
                    visitAnnotationsOnNode(propertyNode, classNode);
                }
                for (FieldNode fieldNode : classNode.getFields()) {
                    visitAnnotationsOnNode(fieldNode, classNode);
                }
            }
        }
    }

    private void visitAnnotationsOnNode(AnnotatedNode node, ClassNode owner) {
        for (AnnotationNode annotationNode : node.getAnnotations()) {
            Class<? extends ASTTransformation> transformationClass = transformations.get(annotationNode.getClassNode().getName());
            if (transformationClass == null) { continue; }
            GroovyASTTransformation annotation = transformationClass.getAnnotation(GroovyASTTransformation.class);
            if (annotation == null) { continue; }
            // Set<ASTNode> nodes = owner.getTransforms(annotation.phase()).get(transformationClass);
            // if (nodes != null && !nodes.isEmpty()) continue;
            owner.addTransform(transformationClass, annotationNode);
        }
    }

    private void initialize(final SourceUnit source) {
        ServiceLoaderUtils.load(getClass().getClassLoader(), "META-INF/annotations/", AnnotationHandler.class, new ServiceLoaderUtils.LineProcessor() {
            @Override
            @SuppressWarnings("unchecked")
            public void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                String[] parts = line.trim().split("=");
                String annotationClassName = parts[0].trim();
                String transformationClassName = parts[1].trim();
                try {
                    Class<?> transformationClass = classLoader.loadClass(transformationClassName);
                    transformations.put(annotationClassName, (Class<? extends ASTTransformation>) transformationClass);
                } catch (Exception e) {
                    source.addException(e);
                }
            }
        });
    }
}
