/*
 * Copyright 2007-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.compiler.support;

import griffon.test.ast.injection.ArtifactTestInjector;
import griffon.test.ast.injection.GriffonControllerTestInjector;
import griffon.test.ast.injection.GriffonModelTestInjector;
import griffon.test.ast.injection.GriffonServiceTestInjector;
import groovy.lang.GroovyObjectSupport;
import org.codehaus.griffon.ast.AbstractASTTransformation;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static griffon.test.ast.injection.ArtifactTestInjector.*;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static org.codehaus.griffon.ast.GriffonASTUtils.*;
import static org.codehaus.groovy.ast.ClassHelper.OBJECT_TYPE;
import static org.codehaus.groovy.ast.ClassHelper.VOID_TYPE;

/**
 * Handles generation of code for the {@code @TestFor} annotation.
 *
 * @author Andres Almiray
 * @since 1.5.0
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class TestForASTTransformation extends AbstractASTTransformation {
    private static final ClassNode EXCEPTION_TYPE = new ClassNode(Exception.class);

    private static final String OBJECT_CLASS = "java.lang.Object";
    private static final String SPEC_CLASS = "spock.lang.Specification";
    private static final String JUNIT3_CLASS = "junit.framework.TestCase";

    private static final ClassNode BEFORE_CLASS_NODE = new ClassNode(Before.class);
    private static final AnnotationNode BEFORE_ANNOTATION = new AnnotationNode(BEFORE_CLASS_NODE);
    private static final ClassNode AFTER_CLASS_NODE = new ClassNode(After.class);
    private static final AnnotationNode AFTER_ANNOTATION = new AnnotationNode(AFTER_CLASS_NODE);
    private static final AnnotationNode TEST_ANNOTATION = new AnnotationNode(new ClassNode(Test.class));
    public static final ClassNode GROOVY_OBJECT_CLASS_NODE = new ClassNode(GroovyObjectSupport.class);

    private static final String METHOD_SETUP_JUNIT = "setUp";
    private static final String METHOD_CLEANUP_JUNIT = "tearDown";
    private static final String METHOD_SETUP_SPOCK = "setup";
    private static final String METHOD_CLEANUP_SPOCK = "cleanup";

    private static final List<ArtifactTestInjector> SUPPORTED_INJECTORS = Arrays.<ArtifactTestInjector>asList(
        new GriffonServiceTestInjector(),
        new GriffonControllerTestInjector(),
        new GriffonModelTestInjector()
    );

    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);

        AnnotationNode annotation = (AnnotationNode) nodes[0];
        ClassNode classNode = (ClassNode) nodes[1];

        if (classNode.isInterface() || Modifier.isAbstract(classNode.getModifiers())) {
            return;
        }

        boolean junit3Test = isJunit3Test(classNode);
        boolean spockTest = isSpockTest(classNode);
        boolean isJunit = !junit3Test && !spockTest;

        if (!junit3Test && !spockTest && !isJunit) return;

        Expression value = annotation.getMember("value");
        ClassExpression ce;
        if (value instanceof ClassExpression) {
            ce = (ClassExpression) value;
            testFor(classNode, ce, source);
            return;
        }

        String className = classNode.getName();
        String targetClassName = null;

        if (className.endsWith("Tests")) {
            targetClassName = className.substring(0, className.indexOf("Tests"));
        } else if (className.endsWith("Test")) {
            targetClassName = className.substring(0, className.indexOf("Test"));
        } else if (className.endsWith("Spec")) {
            targetClassName = className.substring(0, className.indexOf("Spec"));
        }

        if (targetClassName == null) {
            return;
        }

        testFor(classNode, classx(new ClassNode(targetClassName, 0, OBJECT_TYPE)), source);
    }

    public void testFor(ClassNode classNode, ClassExpression classExpr, SourceUnit source) {
        boolean isJunit3Test = isJunit3Test(classNode);
        boolean isSpockTest = isSpockTest(classNode);
        boolean isJunit4 = !isSpockTest && !isJunit3Test;

        if (isJunit4) {
            // assume JUnit 4
            Map<String, MethodNode> declaredMethodsMap = classNode.getDeclaredMethodsMap();
            boolean hasTestMethods = false;
            for (String methodName : declaredMethodsMap.keySet()) {
                MethodNode methodNode = declaredMethodsMap.get(methodName);
                ClassNode testAnnotationClassNode = TEST_ANNOTATION.getClassNode();
                List<AnnotationNode> existingTestAnnotations = methodNode.getAnnotations(testAnnotationClassNode);
                if (isCandidateMethod(methodNode) && (methodNode.getName().startsWith("test") || existingTestAnnotations.size() > 0)) {
                    if (existingTestAnnotations.size() == 0) {
                        ClassNode returnType = methodNode.getReturnType();
                        if (returnType.getName().equals(VOID_TYPE)) {
                            methodNode.addAnnotation(TEST_ANNOTATION);
                        }
                    }
                    hasTestMethods = true;
                }
            }
            if (!hasTestMethods) {
                isJunit4 = false;
            }
        }

        if (isJunit4 || isJunit3Test || isSpockTest) {
            for (ArtifactTestInjector injector : SUPPORTED_INJECTORS) {
                if (injector.matches(classExpr)) {
                    injector.inject(classNode, classExpr);
                    adjustSetupAndCleanupMethods(classNode, isJunit3Test, isSpockTest);
                    return;
                }
            }
            addError("@TestFor does not support artifact '" + classExpr.getType().getName() + "'", classNode, source);
        }
    }

    protected static boolean isJunit3Test(ClassNode classNode) {
        return isSubclassOf(classNode, JUNIT3_CLASS);
    }

    protected static boolean isSpockTest(ClassNode classNode) {
        return isSubclassOf(classNode, SPEC_CLASS);
    }

    private static boolean isSubclassOf(ClassNode classNode, String testType) {
        ClassNode currentSuper = classNode.getSuperClass();
        while (currentSuper != null && !currentSuper.getName().equals(OBJECT_CLASS)) {
            if (currentSuper.getName().equals(testType)) return true;
            currentSuper = currentSuper.getSuperClass();
        }
        return false;
    }

    protected boolean isCandidateMethod(MethodNode declaredMethod) {
        return isAddableMethod(declaredMethod);
    }

    public static boolean isAddableMethod(MethodNode declaredMethod) {
        ClassNode groovyMethods = GROOVY_OBJECT_CLASS_NODE;
        String methodName = declaredMethod.getName();
        return !declaredMethod.isSynthetic() &&
            !methodName.contains("$") &&
            Modifier.isPublic(declaredMethod.getModifiers()) &&
            !Modifier.isAbstract(declaredMethod.getModifiers()) &&
            !groovyMethods.hasMethod(declaredMethod.getName(), declaredMethod.getParameters());
    }

    private void adjustSetupAndCleanupMethods(ClassNode classNode, boolean junit3Test, boolean spockTest) {
        if (junit3Test) {
            configureForJunit3(classNode);
        } else if (spockTest) {
            configureForSpock(classNode);
            autoAnnotateSetupCleanup(classNode);
        } else {
            configureForJunit4(classNode);
            autoAnnotateSetupCleanup(classNode);
        }
    }

    private void configureForJunit3(ClassNode classNode) {
        createOrUpdateSetupMethod(classNode, getSetupStatements(), true);
        createOrUpdateCleanupMethod(classNode, getCleanupStatements(), true);
    }

    private void configureForSpock(ClassNode classNode) {
        createOrUpdateSetupMethod(classNode, getSetupStatements(), false);
        createOrUpdateCleanupMethod(classNode, getCleanupStatements(), false);
    }

    private void configureForJunit4(ClassNode classNode) {
        boolean beforeIsDone = false;
        boolean afterIsDone = false;
        Map<String, MethodNode> declaredMethodsMap = classNode.getDeclaredMethodsMap();
        for (MethodNode methodNode : declaredMethodsMap.values()) {
            if (!beforeIsDone && isDeclaredBeforeMethod(methodNode)) {
                setOrAppendCode(methodNode, getSetupStatements());
                beforeIsDone = true;
            } else if (!afterIsDone && isDeclaredAfterMethod(methodNode)) {
                setOrAppendCode(methodNode, getCleanupStatements());
                afterIsDone = true;
            }
        }

        if (!beforeIsDone) {
            createOrUpdateSetupMethod(classNode, getSetupStatements(), false);
        }
        if (!afterIsDone) {
            createOrUpdateCleanupMethod(classNode, getCleanupStatements(), false);
        }
    }

    private List<Statement> getCleanupStatements() {
        return Arrays.<Statement>asList(
            stmnt(call(THIS, METHOD_CLEANUP_CLASS_UNDER_TEST, NO_ARGS)),
            stmnt(call(THIS, METHOD_CLEANUP_MOCK_APPLICATION, NO_ARGS))
        );
    }

    private List<Statement> getSetupStatements() {
        return Arrays.<Statement>asList(
            stmnt(call(THIS, METHOD_SETUP_MOCK_APPLICATION, NO_ARGS)),
            stmnt(call(THIS, METHOD_SETUP_CLASS_UNDER_TEST, NO_ARGS))
        );
    }

    private void createOrUpdateSetupMethod(ClassNode classNode, List<Statement> code, boolean junit) {
        createOrUpdateMethod(classNode, code,
            junit ? METHOD_SETUP_JUNIT : METHOD_SETUP_SPOCK,
            junit ? PROTECTED : PUBLIC,
            junit ? new ClassNode[]{EXCEPTION_TYPE} : NO_EXCEPTIONS);
    }

    private void createOrUpdateCleanupMethod(ClassNode classNode, List<Statement> code, boolean junit) {
        createOrUpdateMethod(classNode, code,
            junit ? METHOD_CLEANUP_JUNIT : METHOD_CLEANUP_SPOCK,
            junit ? PROTECTED : PUBLIC,
            junit ? new ClassNode[]{EXCEPTION_TYPE} : NO_EXCEPTIONS);
    }

    private void createOrUpdateMethod(ClassNode classNode, List<Statement> code, String methodName, int modifiers, ClassNode[] exceptions) {
        MethodNode method = classNode.getDeclaredMethod(methodName, NO_PARAMS);
        if (method != null) {
            setOrAppendCode(method, code);
        } else {
            classNode.addMethod(
                methodName,
                modifiers,
                VOID_TYPE,
                NO_PARAMS,
                exceptions,
                block(code.toArray(new Statement[code.size()]))
            );
        }
    }

    protected boolean hasAnnotation(MethodNode method, Class<?> annotationClass) {
        return !method.getAnnotations(new ClassNode(annotationClass)).isEmpty();
    }

    private boolean isDeclaredBeforeMethod(MethodNode methodNode) {
        return isPublicInstanceMethod(methodNode) && hasAnnotation(methodNode, Before.class);
    }

    private boolean isDeclaredAfterMethod(MethodNode methodNode) {
        return isPublicInstanceMethod(methodNode) && hasAnnotation(methodNode, After.class);
    }

    private boolean isPublicInstanceMethod(MethodNode methodNode) {
        return !methodNode.isSynthetic() && !methodNode.isStatic() && methodNode.isPublic();
    }

    private void setOrAppendCode(MethodNode method, List<Statement> statements) {
        Statement code = method.getCode();
        BlockStatement methodBody = null;
        if (!(code instanceof BlockStatement)) {
            methodBody = new BlockStatement();
            if (code != null && !(code instanceof ReturnStatement)) {
                methodBody.addStatement(code);
            }
        } else {
            methodBody = (BlockStatement) code;
        }
        methodBody.addStatements(statements);
        method.setCode(methodBody);
    }

    private void autoAnnotateSetupCleanup(ClassNode classNode) {
        MethodNode setupMethod = classNode.getDeclaredMethod(METHOD_SETUP_SPOCK, NO_PARAMS);
        if (setupMethod != null && setupMethod.getAnnotations(BEFORE_CLASS_NODE).size() == 0) {
            setupMethod.addAnnotation(BEFORE_ANNOTATION);
        }

        MethodNode cleanup = classNode.getDeclaredMethod(METHOD_CLEANUP_SPOCK, NO_PARAMS);
        if (cleanup != null && cleanup.getAnnotations(AFTER_CLASS_NODE).size() == 0) {
            cleanup.addAnnotation(AFTER_ANNOTATION);
        }
    }
}