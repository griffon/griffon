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

import java.util.Collections;

import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import static org.codehaus.griffon.ast.GriffonASTUtils.*;

/**
 *
 * @author Andres Almiray 
 *
 * @since 0.9.1
 */
public class GriffonMvcArtifactASTInjector extends GriffonArtifactASTInjector {
    private static final ClassNode GAH_CLASS = ClassHelper.makeWithoutCaching(GriffonApplicationHelper.class);
    
    public void inject(ClassNode classNode, String artifactType) {
        super.inject(classNode, artifactType);
    
        // void mvcGroupInit(Map args)
        addMethod(classNode, new MethodNode(
            "mvcGroupInit",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            new Parameter[]{new Parameter(ClassHelper.MAP_TYPE, "args")},
            ClassNode.EMPTY_ARRAY,
            new EmptyStatement()
        ));
    
        // void mvcGroupDestroy()
        addMethod(classNode, new MethodNode(
            "mvcGroupDestroy",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            Parameter.EMPTY_ARRAY,
            ClassNode.EMPTY_ARRAY,
            new EmptyStatement()
        ));
    
        // Map buildMVCGroup(String mvcType)
        classNode.addMethod(new MethodNode(
            "buildMVCGroup",
            ACC_PUBLIC,
            ClassHelper.MAP_TYPE,
            new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "mvcType")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "buildMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), emptyMap(), var("mvcType"), var("mvcType")
                })))
        ));
    
        // Map buildMVCGroup(String mvcType, mvcName)
        classNode.addMethod(new MethodNode(
            "buildMVCGroup",
            ACC_PUBLIC,
            ClassHelper.MAP_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.STRING_TYPE, "mvcType"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcName")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "buildMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), emptyMap(), var("mvcType"), var("mvcName")
                })))
        ));
    
        // Map buildMVCGroup(Map args, String mvcType)
        classNode.addMethod(new MethodNode(
            "buildMVCGroup",
            ACC_PUBLIC,
            ClassHelper.MAP_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.MAP_TYPE, "args"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcType")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "buildMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), vars("args"), var("mvcType"), var("mvcType")
                })))
        ));

        // Map buildMVCGroup(Map args, String mvcType, String mvcName)
        classNode.addMethod(new MethodNode(
            "buildMVCGroup",
            ACC_PUBLIC,
            ClassHelper.MAP_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.MAP_TYPE, "args"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcType"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcName")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "buildMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), vars("args"), var("mvcType"), var("mvcName")
                })))
        ));

        // List createMVCGroup(String mvcType)
        classNode.addMethod(new MethodNode(
            "createMVCGroup",
            ACC_PUBLIC,
            ClassHelper.LIST_TYPE,
            new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "mvcType")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "createMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), emptyMap(), var("mvcType"), var("mvcType")
                })))
        ));
    
        // List createMVCGroup(String mvcType, mvcName)
        classNode.addMethod(new MethodNode(
            "createMVCGroup",
            ACC_PUBLIC,
            ClassHelper.LIST_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.STRING_TYPE, "mvcType"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcName")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "createMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), emptyMap(), var("mvcType"), var("mvcName")
                })))
        ));
    
        // List createMVCGroup(Map args, String mvcType)
        classNode.addMethod(new MethodNode(
            "createMVCGroup",
            ACC_PUBLIC,
            ClassHelper.LIST_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.MAP_TYPE, "args"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcType")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "createMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), vars("args"), var("mvcType"), var("mvcType")
                })))
        ));

        // List createMVCGroup(String mvcType, Map args)
        classNode.addMethod(new MethodNode(
            "createMVCGroup",
            ACC_PUBLIC,
            ClassHelper.LIST_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.STRING_TYPE, "mvcType"),
                new Parameter(ClassHelper.MAP_TYPE, "args")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "createMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), vars("args"), var("mvcType"), var("mvcType")
                })))
        ));

        // List createMVCGroup(Map args, String mvcType, String mvcName)
        classNode.addMethod(new MethodNode(
            "createMVCGroup",
            ACC_PUBLIC,
            ClassHelper.LIST_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.MAP_TYPE, "args"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcType"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcName")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "createMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), vars("args"), var("mvcType"), var("mvcName")
                })))
        ));

        // List createMVCGroup(String mvcType, String mvcName, Map args)
        classNode.addMethod(new MethodNode(
            "createMVCGroup",
            ACC_PUBLIC,
            ClassHelper.LIST_TYPE,
            new Parameter[]{
                new Parameter(ClassHelper.STRING_TYPE, "mvcType"),
                new Parameter(ClassHelper.STRING_TYPE, "mvcName"),
                new Parameter(ClassHelper.MAP_TYPE, "args")},
            ClassNode.EMPTY_ARRAY,
            returnExpr(new StaticMethodCallExpression(
                GAH_CLASS,
                "createMVCGroup",
                new ArgumentListExpression(new Expression[]{
                    var(APP), vars("args"), var("mvcType"), var("mvcName")
                })))
        ));

        // void destroyMVCGroup(String mvcName)
        addMethod(classNode, new MethodNode(
            "destroyMVCGroup",
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "mvcName")},
            ClassNode.EMPTY_ARRAY,
            new EmptyStatement()
        ));
    }

    private static Expression emptyMap() {
        return new StaticMethodCallExpression(
             ClassHelper.makeWithoutCaching(Collections.class),
             "emptyMap",
             ArgumentListExpression.EMPTY_ARGUMENTS);
    }
}
