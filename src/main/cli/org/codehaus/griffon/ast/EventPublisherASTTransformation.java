/*
 * Copyright 2009-2011 the original author or authors.
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

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.objectweb.asm.Opcodes;

import java.util.Collection;
import griffon.util.EventRouter;
import griffon.util.EventPublisher;
import griffon.util.RunnableWithArgs;

/**
 * Handles generation of code for the {@code @EventPublisher} annotation.
 * <p/>
 * Generally, it adds (if needed) a EventRouter field and
 * the needed add/removeEventListener methods to support the
 * listeners.
 * <p/>
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase= CompilePhase.CANONICALIZATION)
public class EventPublisherASTTransformation implements ASTTransformation, Opcodes {
    private static final ClassNode RUNNABLE_WITH_ARGS_CLASS = ClassHelper.makeWithoutCaching(RunnableWithArgs.class);

    protected static ClassNode epClassNode = new ClassNode(EventPublisher.class);
    protected ClassNode erClassNode = new ClassNode(EventRouter.class);

    /**
     * Convenience method to see if an annotated node is {@code @EventPublisher}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasEventPublisherAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : (Collection<AnnotationNode>) node.getAnnotations()) {
            if (epClassNode.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes   the ast nodes
     * @param source  the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: $node.class / $parent.class");
        }
        AnnotationNode node = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];

        if (parent instanceof ClassNode) {
            addEventRouterToClass(source, node, (ClassNode) parent);
        }
    }

    private void addEventRouterToClass(SourceUnit source, AnnotationNode node, ClassNode classNode) {
        if (needsEventRouter(classNode, source)) {
            addEventRouter(classNode);
        }
    }

    /**
     * Snoops through the declaring class and all parents looking for methods<ul>
     * <li>void addEventListener(Object)</li>
     * <li>void addEventListener(String,C losure)</li>
     * <li>void addEventListener(String, RunnableWithArgs)</li>
     * <li>void removeEventListener(Object)</li>
     * <li>void removeEventListener(String, Closure)</li>
     * <li>void removeEventListener(String, RunnableWithArgs)</li>
     * <li>void publishEvent(String, List = [])</li>
     * <li>void publishEventOutside(String, List = [])</li>
     * <li>void publishEventAsync(String, List = [])</li>
     * </ul>If any are defined all
     * must be defined or a compilation error results.
     *
     * @param declaringClass the class to search
     * @param sourceUnit the source unit, for error reporting. {@code @NotNull}.
     * @return true if property change support should be added
     */
    protected boolean needsEventRouter(ClassNode declaringClass, SourceUnit sourceUnit) {
        boolean foundAdd = false, foundRemove = false, foundPublish = false;
        ClassNode consideredClass = declaringClass;
        while (consideredClass!= null) {
            for (MethodNode method : consideredClass.getMethods()) {
                // just check length, MOP will match it up
                foundAdd = foundAdd || method.getName().equals("addEventListener") && method.getParameters().length == 1;
                foundAdd = foundAdd || method.getName().equals("addEventListener") && method.getParameters().length == 2;
                foundRemove = foundRemove || method.getName().equals("removeEventListener") && method.getParameters().length == 1;
                foundRemove = foundRemove || method.getName().equals("removeEventListener") && method.getParameters().length == 2;
                foundPublish = foundPublish || method.getName().equals("publishEvent") && method.getParameters().length == 1;
                foundPublish = foundPublish || method.getName().equals("publishEvent") && method.getParameters().length == 2;
                foundPublish = foundPublish || method.getName().equals("publishEventOutside") && method.getParameters().length == 1;
                foundPublish = foundPublish || method.getName().equals("publishEventOutside") && method.getParameters().length == 2;
                foundPublish = foundPublish || method.getName().equals("publishEventAsync") && method.getParameters().length == 1;
                foundPublish = foundPublish || method.getName().equals("publishEventAsync") && method.getParameters().length == 2;
                if (foundAdd && foundRemove && foundPublish) {
                    return false;
                }
            }
            consideredClass = consideredClass.getSuperClass();
        }
        if (foundAdd || foundRemove || foundPublish) {
            sourceUnit.getErrorCollector().addErrorAndContinue(
                new SimpleMessage("@EventPublisher cannot be processed on "
                    + declaringClass.getName()
                    + " because some but not all of addEventListener, removeEventListener, publishEvent and publishEventAsync were declared in the current or super classes.",
                sourceUnit)
            );
            return false;
        }
        return true;
    }

    /**
     * Adds the necessary field and methods to support event firing.
     * <p/>
     * Adds a new field:
     * <code>protected final griffon.util.EventRouter this$eventRouter = new griffon.util.EventRouter()</code>
     * <p/>
     * Also adds support methods:
     * <code>public void addEventListener(Object)</code><br/>
     * <code>public void addEventListener(String, Closure)</code><br/>
     * <code>public void addEventListener(String, RunnableWithArgs)</code><br/>
     * <code>public void removeEventListener(Object)</code><br/>
     * <code>public void removeEventListener(String, Closure)</code><br/>
     * <code>public void removeEventListener(String, RunnableWithArgs)</code><br/>
     * <code>public void publishEvent(String,List = [])</code><br/>
     * <code>public void publishEventOutside(String,List = [])</code><br/>
     * <code>public void publishEventAsync(String,List = [])</code><br/>
     *
     * @param declaringClass the class to which we add the support field and methods
     */
    protected void addEventRouter(ClassNode declaringClass) {
        ClassNode erClassNode = ClassHelper.make(EventRouter.class);

        // add field:
        // protected final EventRouter this$eventRouter = new griffon.util.EventRouter()
        FieldNode erField = declaringClass.addField(
                "this$eventRouter",
                ACC_FINAL | ACC_PRIVATE | ACC_SYNTHETIC,
                erClassNode,
                new ConstructorCallExpression(erClassNode,
                        ArgumentListExpression.EMPTY_ARGUMENTS));

        // add method:
        // void addEventListener(listener) {
        //     this$eventRouter.addEventListener(listener)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "addEventListener",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.DYNAMIC_TYPE, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "addEventListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("listener")})))));

        // add method:
        // void addEventListener(String name, Closure listener) {
        //     this$eventRouter.addEventListener(name, listener)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "addEventListener",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.CLOSURE_TYPE, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "addEventListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("name"), new VariableExpression("listener")})))));

        // add method:
        // void addEventListener(String name, RunnableWithArgs listener) {
        //     this$eventRouter.addEventListener(name, listener)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "addEventListener",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(RUNNABLE_WITH_ARGS_CLASS, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "addEventListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("name"), new VariableExpression("listener")})))));

        // add method:
        // void removeEventListener(listener) {
        //    return this$eventRouter.removeEventListener(listener);
        // }
        declaringClass.addMethod(
                new MethodNode(
                        "removeEventListener",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.DYNAMIC_TYPE, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "removeEventListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("listener")})))));

        // add method:
        // void removeEventListener(String name, Closure listener) {
        //    return this$eventRouter.removeEventListener(name, listener);
        // }
        declaringClass.addMethod(
                new MethodNode(
                        "removeEventListener",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(ClassHelper.CLOSURE_TYPE, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "removeEventListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("name"), new VariableExpression("listener")})))));

        // add method:
        // void removeEventListener(String name, RunnableWithArgs listener) {
        //    return this$eventRouter.removeEventListener(name, listener);
        // }
        declaringClass.addMethod(
                new MethodNode(
                        "removeEventListener",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), new Parameter(RUNNABLE_WITH_ARGS_CLASS, "listener")},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "removeEventListener",
                                        new ArgumentListExpression(
                                                new Expression[]{new VariableExpression("name"), new VariableExpression("listener")})))));

        // add method:
        // void publishEvent(String name, List args = []) {
        //     this$eventRouter.publishEvent(name, args)
        //  }
        Parameter args = new Parameter(ClassHelper.LIST_TYPE, "args");
        args.setInitialExpression(new ListExpression());
        declaringClass.addMethod(
                new MethodNode(
                        "publishEvent",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), args},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "publish",
                                        new ArgumentListExpression(
                                                new Expression[]{
                                                        new VariableExpression("name"),
                                                        new VariableExpression("args")})))));

        // add method:
        // void publishEventOutside(String name, List args = []) {
        //     this$eventRouter.publishEventOutside(name, args)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "publishEventOutside",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), args},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "publishOutside",
                                        new ArgumentListExpression(
                                                new Expression[]{
                                                        new VariableExpression("name"),
                                                        new VariableExpression("args")})))));

        // add method:
        // void publishEventAsync(String name, List args = []) {
        //     this$eventRouter.publishEventAsync(name, args)
        //  }
        declaringClass.addMethod(
                new MethodNode(
                        "publishEventAsync",
                        ACC_PUBLIC | ACC_SYNTHETIC,
                        ClassHelper.VOID_TYPE,
                        new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "name"), args},
                        ClassNode.EMPTY_ARRAY,
                        new ExpressionStatement(
                                new MethodCallExpression(
                                        new FieldExpression(erField),
                                        "publishAsync",
                                        new ArgumentListExpression(
                                                new Expression[]{
                                                        new VariableExpression("name"),
                                                        new VariableExpression("args")})))));
    }
}
