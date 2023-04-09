/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import org.codehaus.griffon.compile.core.MethodDescriptor;
import org.codehaus.griffon.compile.core.ast.GriffonASTUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static griffon.util.StringUtils.getGetterName;
import static griffon.util.StringUtils.isNotBlank;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.isPrivate;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_ARGS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_EXCEPTIONS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_PARAMS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.THIS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.args;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.call;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.field;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectField;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.injectMethod;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.returns;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.stmnt;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.var;

/**
 * Base class for all of Griffon's ASTTransformation implementations.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractASTTransformation implements ASTTransformation {
    public static final ClassNode COLLECTIONS_CLASS = makeClassSafe(Collections.class);
    public static final ClassNode GRIFFON_APPLICATION_TYPE = makeClassSafe(GriffonApplication.class);
    public static final ClassNode INJECT_TYPE = makeClassSafe(Inject.class);
    public static final ClassNode NAMED_TYPE = makeClassSafe(Named.class);
    private static final String PROPERTY_APPLICATION = "application";
    private static final String METHOD_GET_APPLICATION = "getApplication";

    public static Expression emptyMap() {
        return new StaticMethodCallExpression
            (COLLECTIONS_CLASS, "emptyMap", NO_ARGS);
    }

    @Nonnull
    public static Expression applicationExpression(@Nonnull ClassNode classNode) {
        FieldNode field = classNode.getDeclaredField(PROPERTY_APPLICATION);
        if (field != null) {
            return new FieldExpression(field);
        }
        field = classNode.getField(PROPERTY_APPLICATION);
        if (field != null && !isPrivate(field.getModifiers())) {
            return new FieldExpression(field);
        }

        MethodNode method = classNode.getDeclaredMethod(METHOD_GET_APPLICATION, NO_PARAMS);
        if (method != null) {
            return call(THIS, METHOD_GET_APPLICATION, NO_ARGS);
        }
        method = classNode.getMethod(METHOD_GET_APPLICATION, NO_PARAMS);
        if (method != null && !isPrivate(method.getModifiers())) {
            return call(THIS, METHOD_GET_APPLICATION, NO_ARGS);
        }
        throw new IllegalStateException("Cannot resolve application field nor getApplication() method on class " + classNode.getName());
    }

    @Nonnull
    public static Expression applicationProperty(@Nonnull ClassNode classNode, @Nonnull String property) {
        return call(applicationExpression(classNode), getGetterName(property), NO_ARGS);
    }

    public static void injectApplication(@Nonnull ClassNode classNode) {
        try {
            applicationExpression(classNode);
        } catch (IllegalStateException iae) {
            FieldNode fieldNode = injectField(classNode, PROPERTY_APPLICATION, PRIVATE, GRIFFON_APPLICATION_TYPE, null, false);
            List<AnnotationNode> annotations = fieldNode.getAnnotations(INJECT_TYPE);
            if (annotations == null || annotations.isEmpty()) {
                fieldNode.addAnnotation(new AnnotationNode(INJECT_TYPE));
            }
        }

        MethodNode method = classNode.getDeclaredMethod(METHOD_GET_APPLICATION, NO_PARAMS);
        if (method == null) {
            injectMethod(classNode, new MethodNode(
                METHOD_GET_APPLICATION,
                PUBLIC,
                GRIFFON_APPLICATION_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                returns(field(classNode, PROPERTY_APPLICATION))
            ));
        }
        method = classNode.getMethod(METHOD_GET_APPLICATION, NO_PARAMS);
        if (method == null) {
            injectMethod(classNode, new MethodNode(
                METHOD_GET_APPLICATION,
                PUBLIC,
                GRIFFON_APPLICATION_TYPE,
                NO_PARAMS,
                NO_EXCEPTIONS,
                returns(field(classNode, PROPERTY_APPLICATION))
            ));
        }
    }

    public static FieldExpression injectedField(@Nonnull ClassNode owner, @Nonnull ClassNode type, @Nonnull String name) {
        return injectedField(owner, type, name, null);
    }

    public static FieldExpression injectedField(@Nonnull ClassNode owner, @Nonnull ClassNode type, @Nonnull String name, @Nullable String qualifierName) {
        FieldNode fieldNode = GriffonASTUtils.injectField(owner, name, Modifier.PRIVATE, type, null, false);
        List<AnnotationNode> annotations = fieldNode.getAnnotations(INJECT_TYPE);
        if (annotations == null || annotations.isEmpty()) {
            fieldNode.addAnnotation(new AnnotationNode(INJECT_TYPE));
        }
        if (isNotBlank(qualifierName)) {
            AnnotationNode namedAnnotation = new AnnotationNode(NAMED_TYPE);
            namedAnnotation.addMember("value", new ConstantExpression(qualifierName));
            fieldNode.addAnnotation(namedAnnotation);
        }
        return new FieldExpression(fieldNode);
    }

    protected static ClassNode newClass(ClassNode classNode) {
        return classNode.getPlainNodeReference();
    }

    public static ClassNode makeClassSafe(String className) {
        return makeClassSafeWithGenerics(className);
    }

    public static ClassNode makeClassSafe(Class<?> klass) {
        return makeClassSafeWithGenerics(klass);
    }

    public static ClassNode makeClassSafe(ClassNode classNode) {
        return makeClassSafeWithGenerics(classNode);
    }

    public static ClassNode makeClassSafeWithGenerics(String className, String... genericTypes) {
        GenericsType[] gtypes = new GenericsType[0];
        if (genericTypes != null) {
            gtypes = new GenericsType[genericTypes.length];
            for (int i = 0; i < gtypes.length; i++) {
                gtypes[i] = new GenericsType(makeClassSafe(genericTypes[i]));
            }
        }
        return makeClassSafe0(ClassHelper.make(className), gtypes);
    }

    public static ClassNode makeClassSafeWithGenerics(Class<?> klass, Class<?>... genericTypes) {
        GenericsType[] gtypes = new GenericsType[0];
        if (genericTypes != null) {
            gtypes = new GenericsType[genericTypes.length];
            for (int i = 0; i < gtypes.length; i++) {
                gtypes[i] = new GenericsType(makeClassSafe(genericTypes[i]));
            }
        }
        return makeClassSafe0(ClassHelper.make(klass), gtypes);
    }

    public static ClassNode makeClassSafeWithGenerics(ClassNode classNode, ClassNode... genericTypes) {
        GenericsType[] gtypes = new GenericsType[0];
        if (genericTypes != null) {
            gtypes = new GenericsType[genericTypes.length];
            for (int i = 0; i < gtypes.length; i++) {
                gtypes[i] = new GenericsType(newClass(genericTypes[i]));
            }
        }
        return makeClassSafe0(classNode, gtypes);
    }

    public static GenericsType makeGenericsType(String className, String[] upperBounds, String lowerBound, boolean placeHolder) {
        ClassNode[] up = new ClassNode[0];
        if (upperBounds != null) {
            up = new ClassNode[upperBounds.length];
            for (int i = 0; i < up.length; i++) {
                up[i] = makeClassSafe(upperBounds[i]);
            }
        }
        return makeGenericsType(makeClassSafe(className), up, makeClassSafe(lowerBound), placeHolder);
    }

    public static GenericsType makeGenericsType(Class<?> klass, Class<?>[] upperBounds, Class<?> lowerBound, boolean placeHolder) {
        ClassNode[] up = new ClassNode[0];
        if (upperBounds != null) {
            up = new ClassNode[upperBounds.length];
            for (int i = 0; i < up.length; i++) {
                up[i] = makeClassSafe(upperBounds[i]);
            }
        }
        return makeGenericsType(makeClassSafe(klass), up, makeClassSafe(lowerBound), placeHolder);
    }

    public static GenericsType makeGenericsType(ClassNode classNode, ClassNode[] upperBounds, ClassNode lowerBound, boolean placeHolder) {
        classNode = newClass(classNode);
        classNode.setGenericsPlaceHolder(placeHolder);
        return new GenericsType(classNode, upperBounds, lowerBound);
    }

    public static ClassNode makeClassSafe0(ClassNode classNode, GenericsType... genericTypes) {
        ClassNode plainNodeReference = newClass(classNode);
        if (genericTypes != null && genericTypes.length > 0) {
            plainNodeReference.setGenericsTypes(genericTypes);
        }
        return plainNodeReference;
    }

    public static boolean needsDelegate(@Nonnull ClassNode classNode, @Nonnull SourceUnit sourceUnit,
                                        @Nonnull MethodDescriptor[] methods, @Nonnull String annotationType,
                                        @Nonnull String delegateType) {
        boolean implemented = false;
        int implementedCount = 0;
        ClassNode consideredClass = classNode;
        while (consideredClass != null) {
            for (MethodNode method : consideredClass.getMethods()) {
                for (MethodDescriptor md : methods) {
                    if (method.getName().equals(md.methodName) && method.getParameters().length == md.arguments.length) {
                        implemented |= true;
                        implementedCount++;
                    }
                    if (implementedCount == methods.length) {
                        return false;
                    }
                }
            }

            consideredClass = consideredClass.getSuperClass();
        }
        if (implemented) {
            sourceUnit.getErrorCollector().addErrorAndContinue(
                new SimpleMessage("@" + annotationType + " cannot be processed on "
                    + classNode.getName()
                    + " because some but not all methods from "
                    + delegateType
                    + " were declared in the current class or super classes.",
                    sourceUnit)
            );
            return false;
        }
        return true;
    }

    public static void addDelegateMethods(@Nonnull ClassNode classNode, @Nonnull ClassNode delegateType, @Nonnull Expression delegate) {
        for (MethodNode method : delegateType.getMethods()) {
            List<Expression> variables = new ArrayList<>();
            Parameter[] parameters = new Parameter[method.getParameters().length];
            ClassNode[] exceptions = new ClassNode[method.getExceptions().length];
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter p = method.getParameters()[i];
                parameters[i] = new Parameter(makeClassSafe(p.getType()), p.getName());
                parameters[i].getType().setGenericsTypes(p.getType().getGenericsTypes());
                parameters[i].getType().setGenericsPlaceHolder(p.getType().isGenericsPlaceHolder());
                parameters[i].getType().setUsingGenerics(p.getType().isUsingGenerics());
                parameters[i].addAnnotations(p.getAnnotations());
                variables.add(var(p.getName()));
            }
            for (int i = 0; i < method.getExceptions().length; i++) {
                ClassNode ex = method.getExceptions()[i];
                exceptions[i] = makeClassSafe(ex);
            }
            ClassNode returnType = makeClassSafe(method.getReturnType());
            returnType.addAnnotations(method.getReturnType().getAnnotations());
            returnType.setGenericsTypes(method.getReturnType().getGenericsTypes());
            returnType.setGenericsPlaceHolder(method.getReturnType().isGenericsPlaceHolder());
            returnType.setUsingGenerics(method.getReturnType().isUsingGenerics());

            boolean isVoid = ClassHelper.VOID_TYPE.equals(method.getReturnType());
            Expression delegateExpression = call(
                delegate,
                method.getName(),
                args(variables));
            MethodNode newMethod = new MethodNode(
                method.getName(),
                method.getModifiers() - Modifier.ABSTRACT,
                returnType,
                parameters,
                exceptions,
                isVoid ? stmnt(delegateExpression) : returns(delegateExpression)
            );
            newMethod.setGenericsTypes(method.getGenericsTypes());
            injectMethod(classNode, newMethod);
        }
    }

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.getLineNumber();
        int col = expr.getColumnNumber();
        source.getErrorCollector().addErrorAndContinue(
            new SyntaxErrorMessage(new SyntaxException(msg + '\n', line, col), source)
        );
    }

    protected void checkNodesForAnnotationAndType(ASTNode node1, ASTNode node2) {
        if (!(node1 instanceof AnnotationNode) || !(node2 instanceof ClassNode)) {
            throw new IllegalArgumentException("Internal error: wrong types: " + node1.getClass() + " / " + node2.getClass());
        }
    }

    protected boolean memberHasValue(AnnotationNode node, String name, Object value) {
        final Expression member = node.getMember(name);
        return member != null && member instanceof ConstantExpression && ((ConstantExpression) member).getValue().equals(value);
    }
}
