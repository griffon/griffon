/*
 * Copyright 2009-2013 the original author or authors.
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

import griffon.core.MVCClosure;
import griffon.core.MVCGroup;
import griffon.core.MVCHandler;
import griffon.transform.MVCAware;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.*;

/**
 * Handles generation of code for the {@code @MVCAware} annotation.
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class MVCAwareASTTransformation extends AbstractASTTransformation {
    private static final Logger LOG = LoggerFactory.getLogger(MVCAwareASTTransformation.class);

    private static ClassNode MY_TYPE = makeClassSafe(MVCAware.class);
    private static ClassNode MVC_HANDLER_TYPE = makeClassSafe(MVCHandler.class);
    private static final ClassNode MVCCLOSURE_TYPE = makeClassSafe(MVCClosure.class);
    private static final ClassNode MVCGROUP_TYPE = makeClassSafe(MVCGroup.class);

    private static final String CREATE_MVC_GROUP = "createMVCGroup";
    private static final String DESTROY_MVC_GROUP = "destroyMVCGroup";
    private static final String BUILD_MVC_GROUP = "buildMVCGroup";
    private static final String WITH_MVC_GROUP = "withMVCGroup";
    private static final String MVC_TYPE = "mvcType";
    private static final String MVC_NAME = "mvcName";
    private static final String HANDLER = "handler";
    private static final String ARGS = "args";

    /**
     * Convenience method to see if an annotated node is {@code @MVCAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasMVCAwareAnnotation(AnnotatedNode node) {
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
        if (!classNode.implementsInterface(MVC_HANDLER_TYPE)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + MVCHandler.class.getName() + " into " + classNode.getName());
            }
            apply(classNode);
        }
    }

    public static void apply(ClassNode classNode) {
        injectInterface(classNode, MVC_HANDLER_TYPE);

        // MVCGroup buildMVCGroup(String mvcType)
        injectMethod(classNode, new MethodNode(
                BUILD_MVC_GROUP,
                ACC_PUBLIC,
                MVCGROUP_TYPE,
                params(param(STRING_TYPE, MVC_TYPE)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        BUILD_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, emptyMap())))
        ));

        // MVCGroup buildMVCGroup(String mvcType, mvcName)
        injectMethod(classNode, new MethodNode(
                BUILD_MVC_GROUP,
                ACC_PUBLIC,
                MVCGROUP_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        BUILD_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), emptyMap())))
        ));

        // MVCGroup buildMVCGroup(Map args, String mvcType)
        injectMethod(classNode, new MethodNode(
                BUILD_MVC_GROUP,
                ACC_PUBLIC,
                MVCGROUP_TYPE,
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        BUILD_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS))))
        ));

        // MVCGroup buildMVCGroup(String mvcType, Map args)
        injectMethod(classNode, new MethodNode(
                BUILD_MVC_GROUP,
                ACC_PUBLIC,
                MVCGROUP_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(MAP_TYPE), ARGS)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        BUILD_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS))))
        ));

        // MVCGroup buildMVCGroup(Map args, String mvcType, String mvcName)
        injectMethod(classNode, new MethodNode(
                BUILD_MVC_GROUP,
                ACC_PUBLIC,
                MVCGROUP_TYPE,
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        BUILD_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS))))
        ));

        // MVCGroup buildMVCGroup(String mvcType, String mvcName, Map args)
        injectMethod(classNode, new MethodNode(
                BUILD_MVC_GROUP,
                ACC_PUBLIC,
                MVCGROUP_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(MAP_TYPE), ARGS)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        BUILD_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS))))
        ));

        // List createMVCGroup(String mvcType)
        injectMethod(classNode, new MethodNode(
                CREATE_MVC_GROUP,
                ACC_PUBLIC,
                makeClassSafe(LIST_TYPE),
                params(param(STRING_TYPE, MVC_TYPE)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        CREATE_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, emptyMap())))
        ));

        // List createMVCGroup(String mvcType, mvcName)
        injectMethod(classNode, new MethodNode(
                CREATE_MVC_GROUP,
                ACC_PUBLIC,
                makeClassSafe(LIST_TYPE),
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        CREATE_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), emptyMap())))
        ));

        // List createMVCGroup(Map args, String mvcType)
        injectMethod(classNode, new MethodNode(
                CREATE_MVC_GROUP,
                ACC_PUBLIC,
                makeClassSafe(LIST_TYPE),
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        CREATE_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, var(ARGS))))
        ));

        // List createMVCGroup(String mvcType, Map args)
        injectMethod(classNode, new MethodNode(
                CREATE_MVC_GROUP,
                ACC_PUBLIC,
                makeClassSafe(LIST_TYPE),
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(MAP_TYPE), ARGS)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        CREATE_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, var(ARGS))))
        ));

        // List createMVCGroup(Map args, String mvcType, String mvcName)
        injectMethod(classNode, new MethodNode(
                CREATE_MVC_GROUP,
                ACC_PUBLIC,
                makeClassSafe(LIST_TYPE),
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        CREATE_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS))))
        ));

        // List createMVCGroup(String mvcType, String mvcName, Map args)
        injectMethod(classNode, new MethodNode(
                CREATE_MVC_GROUP,
                ACC_PUBLIC,
                makeClassSafe(LIST_TYPE),
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(MAP_TYPE), ARGS)),
                NO_EXCEPTIONS,
                returns(call(
                        mvcGroupManagerInstance(),
                        CREATE_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS))))
        ));

        // void destroyMVCGroup(String mvcName)
        injectMethod(classNode, new MethodNode(
                DESTROY_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(param(STRING_TYPE, MVC_NAME)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        DESTROY_MVC_GROUP,
                        args(var(MVC_NAME))))
        ));

        // void withMVCGroup(String mvcType, Closure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(CLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, emptyMap(), var(HANDLER))))
        ));

        // void withMVCGroup(String mvcType, String mvcName, Closure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(CLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), emptyMap(), var(HANDLER))))
        ));

        // void withMVCGroup(String mvcType, Map args, Closure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(makeClassSafe(CLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, var(ARGS), var(HANDLER))))
        ));

        // void withMVCGroup(Map args, String mvcType, Closure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(CLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, var(ARGS), var(HANDLER))))
        ));

        // void withMVCGroup(String mvcType, String mvcName, Map args, Closure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(makeClassSafe(CLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS), var(HANDLER))))

        ));

        // void withMVCGroup(Map args, String mvcType, String mvcName, Closure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(CLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS), var(HANDLER))))

        ));

        // void withMVCGroup(String mvcType, MVCClosure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(MVCCLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, emptyMap(), var(HANDLER))))
        ));

        // void withMVCGroup(String mvcType, String mvcName, MVCClosure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(MVCCLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), emptyMap(), var(HANDLER))))
        ));

        // void withMVCGroup(String mvcType, Map args, MVCClosure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(makeClassSafe(MVCCLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, var(ARGS), var(HANDLER))))
        ));

        // void withMVCGroup(Map args, String mvcType, MVCClosure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE),
                        param(makeClassSafe(MVCCLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), ConstantExpression.NULL, var(ARGS), var(HANDLER))))
        ));

        // void withMVCGroup(String mvcType, String mvcName, Map args, MVCClosure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(makeClassSafe(MVCCLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS), var(HANDLER))))
        ));

        // void withMVCGroup(Map args, String mvcType, String mvcName, MVCClosure handler)
        injectMethod(classNode, new MethodNode(
                WITH_MVC_GROUP,
                ACC_PUBLIC,
                VOID_TYPE,
                params(
                        param(makeClassSafe(MAP_TYPE), ARGS),
                        param(STRING_TYPE, MVC_TYPE),
                        param(STRING_TYPE, MVC_NAME),
                        param(makeClassSafe(MVCCLOSURE_TYPE), HANDLER)),
                NO_EXCEPTIONS,
                stmnt(call(
                        mvcGroupManagerInstance(),
                        WITH_MVC_GROUP,
                        args(var(MVC_TYPE), var(MVC_NAME), var(ARGS), var(HANDLER))))
        ));
    }

    public static Expression mvcGroupManagerInstance() {
        return call(applicationInstance(), "getMvcGroupManager", NO_ARGS);
    }
}
