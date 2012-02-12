/*
 * Copyright 2010-2012 the original author or authors.
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
package org.codehaus.griffon.compiler;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

import static org.codehaus.griffon.ast.ThreadingASTTransformation.*;
import static org.codehaus.griffon.compiler.support.GriffonControllerASTTransformation.isControllerArtifact;

/**
 * @author Andres Almiray
 * @since 0.9.2
 */
public class ThreadingInjectionOperation extends CompilationUnit.PrimaryClassNodeOperation {
    public void call(final SourceUnit source, final GeneratorContext context, final ClassNode classNode) throws CompilationFailedException {
        if (!GriffonCompilerContext.isGriffonArtifact(source)) return;
        if (!isControllerArtifact(classNode, source)) return;

        for (MethodNode method : classNode.getMethods()) {
            if (hasThreadingAnnotation(method)) continue;
            handleMethodForInjection(classNode, method);
        }

        for (PropertyNode property : classNode.getProperties()) {
            FieldNode field = property.getField();
            if (!hasThreadingAnnotation(field)) {
                handlePropertyForInjection(classNode, property);
            } else {
                String threadingMethod = getThreadingMethod(field);
                if (threadingMethod == null) continue;
                handlePropertyForInjection(classNode, property, threadingMethod);
            }
        }
    }
}