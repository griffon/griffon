/*
 * Copyright 2009-2012 the original author or authors.
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

package org.codehaus.griffon.ast;

import griffon.core.ThreadingHandler;
import griffon.core.UIThreadManager;
import griffon.transform.ThreadingAware;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;

/**
 * Handles generation of code for the {@code @ThreadingAware} annotation.
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ThreadingAwareASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadingAwareASTTransformation.class);

    private static ClassNode MY_TYPE = new ClassNode(ThreadingAware.class);
    private static ClassNode THREADING_HANDLER_TYPE = makeClassSafe(ThreadingHandler.class);
    private static final ClassNode CALLABLE_CLASS = makeClassSafe(Callable.class);
    private static final ClassNode FUTURE_CLASS = makeClassSafe(Future.class);
    private static final ClassNode EXECUTOR_SERVICE_CLASS = makeClassSafe(ExecutorService.class);
    private static final ClassNode UITHREAD_MANAGER_CLASS = makeClassSafe(UIThreadManager.class);
    private static final ClassNode RUNNABLE_CLASS = makeClassSafe(Runnable.class);

    private static final String RUNNABLE = "runnable";
    private static final String CALLABLE = "callable";
    private static final String CLOSURE = "closure";

    /**
     * Convenience method to see if an annotated node is {@code @ThreadingAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasThreadingAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (MY_TYPE.equals(annotation.getClassNode())) {
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
        if (!classNode.implementsInterface(THREADING_HANDLER_TYPE)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + ThreadingHandler.class.getName() + " into " + classNode.getName());
            }
            apply(classNode);
        }
    }

    public static void apply(ClassNode classNode) {
        injectInterface(classNode, THREADING_HANDLER_TYPE);

        // boolean isUIThread()
        injectMethod(classNode, new MethodNode(
                "isUIThread",
                ACC_PUBLIC,
                ClassHelper.boolean_TYPE,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                returns(call(
                        uiThreadManagerInstance(),
                        "isUIThread",
                        NO_ARGS))
        ));

        // void execInsideUIAsync(Runnable)
        injectMethod(classNode, new MethodNode(
                "execInsideUIAsync",
                ACC_PUBLIC,
                ClassHelper.VOID_TYPE,
                params(param(RUNNABLE_CLASS, RUNNABLE)),
                ClassNode.EMPTY_ARRAY,
                stmnt(call(
                        uiThreadManagerInstance(),
                        "executeAsync",
                        vars(RUNNABLE)))
        ));

        // void execInsideUISync(Runnable)
        injectMethod(classNode, new MethodNode(
                "execInsideUISync",
                ACC_PUBLIC,
                ClassHelper.VOID_TYPE,
                params(param(RUNNABLE_CLASS, RUNNABLE)),
                ClassNode.EMPTY_ARRAY,
                stmnt(call(
                        uiThreadManagerInstance(),
                        "executeSync",
                        vars(RUNNABLE)))
        ));

        // void execOutsideUI(Runnable)
        injectMethod(classNode, new MethodNode(
                "execOutsideUI",
                ACC_PUBLIC,
                ClassHelper.VOID_TYPE,
                params(param(RUNNABLE_CLASS, RUNNABLE)),
                ClassNode.EMPTY_ARRAY,
                stmnt(call(
                        uiThreadManagerInstance(),
                        "executeOutside",
                        vars(RUNNABLE)))
        ));

        // Future execFuture(Runnable)
        injectMethod(classNode, new MethodNode(
                "execFuture",
                ACC_PUBLIC,
                makeClassSafe(FUTURE_CLASS),
                params(param(makeClassSafe(ClassHelper.CLOSURE_TYPE), CLOSURE)),
                ClassNode.EMPTY_ARRAY,
                returns(call(
                        uiThreadManagerInstance(),
                        "executeFuture",
                        vars(CLOSURE)))
        ));

        // Future execFuture(ExecutorService, Closure)
        injectMethod(classNode, new MethodNode(
                "execFuture",
                ACC_PUBLIC,
                makeClassSafe(FUTURE_CLASS),
                params(
                        param(EXECUTOR_SERVICE_CLASS, "executorService"),
                        param(makeClassSafe(ClassHelper.CLOSURE_TYPE), CLOSURE)),
                ClassNode.EMPTY_ARRAY,
                returns(call(
                        uiThreadManagerInstance(),
                        "executeFuture",
                        vars("executorService", CLOSURE)))
        ));

        // Future execFuture(Callable)
        injectMethod(classNode, new MethodNode(
                "execFuture",
                ACC_PUBLIC,
                makeClassSafe(FUTURE_CLASS),
                params(param(makeClassSafe(CALLABLE_CLASS), CALLABLE)),
                ClassNode.EMPTY_ARRAY,
                returns(call(
                        uiThreadManagerInstance(),
                        "executeFuture",
                        vars(CALLABLE)))
        ));

        // Future execFuture(ExecutorService, Callable)
        injectMethod(classNode, new MethodNode(
                "execFuture",
                ACC_PUBLIC,
                makeClassSafe(FUTURE_CLASS),
                params(
                        param(EXECUTOR_SERVICE_CLASS, "executorService"),
                        param(makeClassSafe(CALLABLE_CLASS), CALLABLE)),
                ClassNode.EMPTY_ARRAY,
                returns(call(
                        uiThreadManagerInstance(),
                        "executeFuture",
                        vars("executorService", CALLABLE)))
        ));
    }

    private static Expression uiThreadManagerInstance() {
        return call(UITHREAD_MANAGER_CLASS, "getInstance", NO_ARGS);
    }
}
