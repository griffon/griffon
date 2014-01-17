/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.core.compile.ast.transform;

import griffon.core.artifact.GriffonController;
import griffon.transform.Threading;
import griffon.util.GriffonClassUtils;
import griffon.util.MethodDescriptor;
import org.codehaus.griffon.core.compile.AnnotationHandler;
import org.codehaus.griffon.core.compile.AnnotationHandlerFor;
import org.codehaus.griffon.core.compile.ThreadingAwareConstants;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.util.Iterator;
import java.util.List;

import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.THIS;
import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.args;
import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.stmnt;
import static org.codehaus.griffon.core.compile.ast.transform.ThreadingAwareASTTransformation.addThreadingHandlerIfNeeded;

/**
 * Handles generation of code for the {@code @Threading} annotation.
 * <p/>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
@AnnotationHandlerFor(Threading.class)
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ThreadingASTTransformation extends AbstractASTTransformation implements ThreadingAwareConstants, AnnotationHandler {
    private static ClassNode THREADING_CNODE = makeClassSafe(Threading.class);
    private static final ClassNode GRIFFON_CONTROLLER_CNODE = makeClassSafe(GriffonController.class);

    /**
     * Convenience method to see if an annotated node is {@code @Threading}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasThreadingAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (THREADING_CNODE.equals(annotation.getClassNode())) {
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
        AnnotationNode annotation = (AnnotationNode) nodes[0];
        AnnotatedNode node = (AnnotatedNode) nodes[1];

        Threading.Policy threadingPolicy = getThreadingPolicy(annotation);
        if (threadingPolicy == Threading.Policy.SKIP) return;

        String threadingMethod = METHOD_RUN_OUTSIDE_UI;
        switch (threadingPolicy) {
            case INSIDE_UITHREAD_SYNC:
                threadingMethod = METHOD_RUN_INSIDE_UI_SYNC;
                break;
            case INSIDE_UITHREAD_ASYNC:
                threadingMethod = METHOD_RUN_INSIDE_UI_ASYNC;
                break;
            case OUTSIDE_UITHREAD:
            default:
                break;
        }

        ClassNode declaringClass = node.getDeclaringClass();
        if (declaringClass.implementsInterface(GRIFFON_CONTROLLER_CNODE)) {
            return;
        }

        if (node instanceof MethodNode) {
            addThreadingHandlerIfNeeded(source, declaringClass);
            handleMethodForInjection(declaringClass, (MethodNode) node, threadingMethod);
        }
    }

    public static Threading.Policy getThreadingPolicy(AnnotationNode annotation) {
        PropertyExpression value = (PropertyExpression) annotation.getMember("value");
        if (value == null) return Threading.Policy.OUTSIDE_UITHREAD;
        return Threading.Policy.valueOf(value.getPropertyAsString());
    }

    public static void handleMethodForInjection(ClassNode classNode, MethodNode method, String threadingMethod) {
        MethodDescriptor md = methodDescriptorFor(method);
        if (GriffonClassUtils.isPlainMethod(md) &&
            !GriffonClassUtils.isEventHandler(md) &&
            hasVoidOrDefAsReturnType(method)) {
            wrapStatements(classNode, method, threadingMethod);
        }
    }

    private static boolean hasVoidOrDefAsReturnType(MethodNode method) {
        Class<?> returnType = method.getReturnType().getTypeClass();
        return returnType.equals(ClassHelper.DYNAMIC_TYPE.getTypeClass()) ||
            returnType.equals(ClassHelper.VOID_TYPE.getTypeClass());
    }

    private static MethodDescriptor methodDescriptorFor(MethodNode method) {
        if (method == null) return null;
        Parameter[] types = method.getParameters();
        String[] parameterTypes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            parameterTypes[i] = newClass(types[i].getType()).getName();
        }
        return new MethodDescriptor(method.getName(), parameterTypes, method.getModifiers());
    }

    private static void wrapStatements(ClassNode declaringClass, MethodNode method, String threadingMethod) {
        Statement code = method.getCode();
        Statement wrappedCode = wrapStatements(code, threadingMethod);
        if (code != wrappedCode) {
            method.setCode(wrappedCode);
            for (Parameter param : method.getParameters()) {
                param.setClosureSharedVariable(true);
            }
        }
    }

    private static Statement wrapStatements(Statement code, String threadingMethod) {
        // TODO deal with non-block statements
        if (!(code instanceof BlockStatement)) return code;

        BlockStatement codeBlock = (BlockStatement) code;
        List<Statement> statements = codeBlock.getStatements();
        if (statements.isEmpty()) return code;

        VariableScope variableScope = codeBlock.getVariableScope();
        BlockStatement block = new BlockStatement();
        VariableScope blockScope = variableScope.copy();
        makeVariablesShared(blockScope);
        block.setVariableScope(blockScope);
        ClosureExpression closure = new ClosureExpression(Parameter.EMPTY_ARRAY, code);
        VariableScope closureScope = variableScope.copy();
        makeVariablesShared(closureScope);
        closure.setVariableScope(closureScope);
        block.addStatement(stmnt(new MethodCallExpression(THIS, threadingMethod, args(closure))));

        return block;
    }

    private static void makeVariablesShared(VariableScope scope) {
        for (Iterator<Variable> vars = scope.getReferencedLocalVariablesIterator(); vars.hasNext(); ) {
            Variable var = vars.next();
            var.setClosureSharedVariable(true);
        }
    }
}
