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

package org.codehaus.griffon.compiler.support;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;

import griffon.core.GriffonApplication;
import griffon.core.GriffonClass;
import griffon.core.ArtifactManager;
import griffon.util.UIThreadHelper;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;

/**
 *
 * @author Andres Almiray 
 *
 * @since 0.9.1
 */
public class GriffonArtifactASTInjector implements ASTInjector {
    private static final ClassNode GRIFFON_APPLICATION_CLASS = ClassHelper.makeWithoutCaching(GriffonApplication.class);
    private static final ClassNode ARTIFACT_MANAGER_CLASS = ClassHelper.makeWithoutCaching(ArtifactManager.class);
    private static final ClassNode GRIFFON_CLASS_CLASS = ClassHelper.makeWithoutCaching(GriffonClass.class);
    private static final ClassNode GAH_CLASS = ClassHelper.makeWithoutCaching(GriffonApplicationHelper.class);
    private static final ClassNode CALLABLE_CLASS = ClassHelper.makeWithoutCaching(Callable.class);
    private static final ClassNode FUTURE_CLASS = ClassHelper.makeWithoutCaching(Future.class);
    private static final ClassNode EXECUTOR_SERVICE_CLASS = ClassHelper.makeWithoutCaching(ExecutorService.class);
    private static final ClassNode UITH_CLASS = ClassHelper.makeWithoutCaching(UIThreadHelper.class);
    private static final ClassNode RUNNABLE_CLASS = ClassHelper.makeWithoutCaching(Runnable.class);
    public static final String APP = "app";
    
    public void inject(ClassNode classNode, String artifactType) {
        // GriffonApplication getApp()
        // void setApp(GriffonApplication app)
        classNode.addProperty(APP, ACC_PUBLIC, GRIFFON_APPLICATION_CLASS, null, null, null);

        // MetaClass getMetaClass()
        classNode.addMethod(new MethodNode(
            "getMetaClass",
            ACC_PUBLIC,
            ClassHelper.METACLASS_TYPE,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            returnExpr(new MethodCallExpression(
                new MethodCallExpression(
                    VariableExpression.THIS_EXPRESSION,
                    "getGriffonClass",
                     ArgumentListExpression.EMPTY_ARGUMENTS),
                "getMetaClass",
                ArgumentListExpression.EMPTY_ARGUMENTS))
        ));
        
        // GriffonClass getGriffonClass()
        classNode.addMethod(new MethodNode(
            "getGriffonClass",
            ACC_PUBLIC,
            GRIFFON_CLASS_CLASS,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            returnExpr(new MethodCallExpression(
                new StaticMethodCallExpression(
                    ARTIFACT_MANAGER_CLASS,
                    "getInstance",
                     ArgumentListExpression.EMPTY_ARGUMENTS),
                "findGriffonClass",
                new ArgumentListExpression(new ClassExpression(classNode))))
        ));
    
        // Object newInstance()
        classNode.addMethod(new MethodNode(
            "newInstance",
            ACC_PUBLIC,
            ClassHelper.OBJECT_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.CLASS_Type, "clazz"),
                new Parameter(ClassHelper.STRING_TYPE, "type")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "newInstance",
                vars(APP, "clazz", "type")))
        ));
    
        // boolean isUIThread()
        classNode.addMethod(new MethodNode(
            "isUIThread",
            ACC_PUBLIC,
            ClassHelper.boolean_TYPE,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            returnExpr(new MethodCallExpression(
                uiThreadHelperInstance(),
                "isUIThread",
                ArgumentListExpression.EMPTY_ARGUMENTS))
        ));

        // void execAsync(Runnable)
        classNode.addMethod(new MethodNode(
            "execAsync",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            new Parameter[]{new Parameter(RUNNABLE_CLASS, "runnable")},
            ClassNode.EMPTY_ARRAY,
            new ExpressionStatement(new MethodCallExpression(
                uiThreadHelperInstance(),
                "execAsync",
                vars("runnable")))
        ));
    
        // void execSync(Runnable)
        classNode.addMethod(new MethodNode(
            "execSync",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            new Parameter[]{new Parameter(RUNNABLE_CLASS, "runnable")},
            ClassNode.EMPTY_ARRAY,
            new ExpressionStatement(new MethodCallExpression(
                uiThreadHelperInstance(),
                "execSync",
                vars("runnable")))
        ));
    
        // void execOutside(Runnable)
        classNode.addMethod(new MethodNode(
            "execOutside",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            new Parameter[]{new Parameter(RUNNABLE_CLASS, "runnable")},
            ClassNode.EMPTY_ARRAY,
            new ExpressionStatement(new MethodCallExpression(
                uiThreadHelperInstance(),
                "execOutside",
                vars("runnable")))
        ));
    
        // Future execFuture(Runnable)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            new Parameter[]{new Parameter(ClassHelper.CLOSURE_TYPE, "closure")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new MethodCallExpression(
                uiThreadHelperInstance(),
                "execFuture",
                vars("closure")))
        ));
    
        // Future execFuture(ExecutorService, Closure)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            new Parameter[]{
                new Parameter(EXECUTOR_SERVICE_CLASS, "executorService"),
                new Parameter(ClassHelper.CLOSURE_TYPE, "closure")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new MethodCallExpression(
                uiThreadHelperInstance(),
                "execFuture",
                vars("executorService", "closure")))
        ));
    
        // Future execFuture(Callable)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            new Parameter[]{new Parameter(CALLABLE_CLASS, "callable")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new MethodCallExpression(
                uiThreadHelperInstance(),
                "execFuture",
                vars("callable")))
        ));
    
        // Future execFuture(ExecutorService, Callable)
        classNode.addMethod(new MethodNode(
            "execFuture",
            ACC_PUBLIC,
            FUTURE_CLASS,
            new Parameter[]{
                new Parameter(EXECUTOR_SERVICE_CLASS, "executorService"),
                new Parameter(CALLABLE_CLASS, "callable")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new MethodCallExpression(
                uiThreadHelperInstance(),
                "execFuture",
                vars("executorService", "callable")))
        ));
    }

    private Expression uiThreadHelperInstance() {
        return new StaticMethodCallExpression(
                   UITH_CLASS,
                   "getInstance",
                   ArgumentListExpression.EMPTY_ARGUMENTS);
    }
}