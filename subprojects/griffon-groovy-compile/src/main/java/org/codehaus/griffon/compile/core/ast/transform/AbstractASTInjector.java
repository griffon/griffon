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
import org.codehaus.griffon.compile.core.ast.GriffonASTUtils;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Modifier;

import static griffon.util.StringUtils.getGetterName;
import static griffon.util.StringUtils.isNotBlank;
import static java.lang.reflect.Modifier.isPrivate;
import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_ARGS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.NO_PARAMS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.THIS;
import static org.codehaus.griffon.compile.core.ast.GriffonASTUtils.call;
import static org.codehaus.groovy.ast.expr.VariableExpression.THIS_EXPRESSION;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractASTInjector implements ASTInjector {
    public static final ClassNode NAMED_TYPE = makeClassSafe(Named.class);
    private static final ClassNode THREAD_TYPE = ClassHelper.make(Thread.class).getPlainNodeReference();
    private static final ClassNode INJECT_TYPE = makeClassSafe(Inject.class);
    private static final String PROPERTY_APPLICATION = "application";
    private static final String METHOD_GET_APPLICATION = "getApplication";

    @Nonnull
    public static ClassNode makeClassSafe(@Nonnull ClassNode classNode) {
        requireNonNull(classNode, "Argument 'classNode' must not be null");
        return classNode.getPlainNodeReference();
    }

    @Nonnull
    public static ClassNode makeClassSafe(@Nonnull Class<?> klass) {
        requireNonNull(klass, "Argument 'klass' must not be null");
        return makeClassSafe(ClassHelper.make(klass));
    }

    @Nonnull
    public static Expression currentThread() {
        return call(THREAD_TYPE, "currentThread", NO_ARGS);
    }

    @Nonnull
    public static Expression myClass() {
        return call(THIS_EXPRESSION, "getClass", NO_ARGS);
    }

    @Nonnull
    public static Expression myClassLoader() {
        return call(myClass(), "getClassLoader", NO_ARGS);
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

    @Nonnull
    public static FieldExpression injectedField(@Nonnull ClassNode owner, @Nonnull ClassNode type, @Nonnull String name) {
        return injectedField(owner, type, name, null);
    }

    @Nonnull
    public static FieldExpression injectedField(@Nonnull ClassNode owner, @Nonnull ClassNode type, @Nonnull String name, @Nullable String qualifierName) {
        FieldNode fieldNode = GriffonASTUtils.injectField(owner, name, Modifier.PRIVATE, type, null, false);
        if (fieldNode.getAnnotations(INJECT_TYPE) == null) {
            fieldNode.addAnnotation(new AnnotationNode(INJECT_TYPE));
        }
        if (isNotBlank(qualifierName) && fieldNode.getAnnotations(NAMED_TYPE) == null) {
            AnnotationNode namedAnnotation = new AnnotationNode(NAMED_TYPE);
            namedAnnotation.addMember("value", new ConstantExpression(qualifierName));
            fieldNode.addAnnotation(namedAnnotation);
        }
        return new FieldExpression(fieldNode);
    }
}
