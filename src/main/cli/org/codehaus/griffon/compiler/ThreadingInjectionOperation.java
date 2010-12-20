/*
 * Copyright 2010 the original author or authors.
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

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Modifier;

import griffon.core.GriffonControllerClass;
import griffon.util.GriffonClassUtils;
import griffon.util.GriffonClassUtils.MethodDescriptor;
import griffon.util.BuildSettingsHolder;
import static org.codehaus.griffon.ast.GriffonASTUtils.THIS;

/**
 * @author Andres Almiray
 */
public class ThreadingInjectionOperation extends CompilationUnit.PrimaryClassNodeOperation {
    private static final String ARTIFACT_PATH = "controllers";
    private static final String COMPILER_THREADING_KEY = "compiler.threading";

    public void call(final SourceUnit source, final GeneratorContext context, final ClassNode classNode) throws CompilationFailedException {
        if(!GriffonCompilerContext.isGriffonArtifact(source)) return;
        String artifactPath = GriffonCompilerContext.getArtifactPath(source.getName());
        if(!ARTIFACT_PATH.equals(artifactPath) || !classNode.getName().endsWith(GriffonControllerClass.TRAILING)) return;

        for(MethodNode method : classNode.getMethods()) {
            MethodDescriptor md = methodDescriptorFor(method);
            if(GriffonClassUtils.isPlainMethod(md) &&
               !GriffonClassUtils.isEventHandler(md)) {
                wrapStatements(classNode, method);
            }
        }
        
        for(PropertyNode property : classNode.getProperties()) {
            if(property.getModifiers() - Modifier.PUBLIC == 0 &&
               !GriffonClassUtils.isEventHandler(property.getName()) &&
               property.getInitialExpression() instanceof ClosureExpression) {
                wrapStatements(classNode, property);
            }
        }
    }

    private static MethodDescriptor methodDescriptorFor(MethodNode method) {
        if(method == null) return null;
        Parameter[] types = method.getParameters();
        Class[] parameterTypes = new Class[types.length];
        for(int i = 0; i < types.length; i++) {
            parameterTypes[i] = types[i].getType().getTypeClass();
        }
        return new MethodDescriptor(method.getName(), parameterTypes, method.getModifiers());
    }

    private static void wrapStatements(ClassNode declaringClass, MethodNode method) {
        if(skipInjection(declaringClass.getName() +"."+ method.getName())) return;
        method.setCode(wrapStatements((BlockStatement) method.getCode()));
    }

    private static void wrapStatements(ClassNode declaringClass, PropertyNode property) {
        if(skipInjection(declaringClass.getName() +"."+ property.getName())) return;
        ClosureExpression closure = (ClosureExpression) property.getInitialExpression();
        ClosureExpression newClosure = new ClosureExpression(closure.getParameters(), 
                                           wrapStatements((BlockStatement) closure.getCode()));
        newClosure.setVariableScope(closure.getVariableScope());
        property.getField().setInitialValueExpression(newClosure);
    }

    private static boolean skipInjection(String actionName) {
        Map settings = GriffonCompilerContext.getFlattenedBuildSettings();

        String keyName = COMPILER_THREADING_KEY + "." + actionName;
        while(!COMPILER_THREADING_KEY.equals(keyName)) {
            Object value = settings.get(keyName);
            keyName = keyName.substring(0, keyName.lastIndexOf("."));
            if(value != null && !DefaultTypeTransformation.castToBoolean(value)) return true;
        }

        return false;
    }

    private static Statement wrapStatements(BlockStatement code) {
        BlockStatement newCode = new BlockStatement();
        newCode.setVariableScope(code.getVariableScope());
        ClosureExpression closure = new ClosureExpression(Parameter.EMPTY_ARRAY, code);
        closure.setVariableScope(code.getVariableScope());
        newCode.addStatement(
            new ExpressionStatement(
                new MethodCallExpression(THIS, "execOutside", new ArgumentListExpression(closure))
            )
        );
        return newCode;
    }
}
