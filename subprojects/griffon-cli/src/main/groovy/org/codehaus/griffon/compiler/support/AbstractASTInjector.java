/*
 * Copyright 2010-2012 the original author or authors.
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

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;

import static org.codehaus.griffon.ast.GriffonASTUtils.NO_ARGS;
import static org.codehaus.griffon.ast.GriffonASTUtils.call;
import static org.codehaus.groovy.ast.expr.VariableExpression.THIS_EXPRESSION;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public abstract class AbstractASTInjector implements ASTInjector {
    private static final ClassNode THREAD_CLASS = ClassHelper.makeWithoutCaching(Thread.class).getPlainNodeReference();

    public static ClassNode makeClassSafe(ClassNode classNode) {
        return classNode.getPlainNodeReference();
    }

    public static ClassNode makeClassSafe(Class klass) {
        return makeClassSafe(ClassHelper.makeWithoutCaching(klass));
    }

    public static Expression currentThread() {
        return call(THREAD_CLASS, "currentThread", NO_ARGS);
    }

    public static Expression myClass() {
        return call(THIS_EXPRESSION, "getClass", NO_ARGS);
    }

    public static Expression myClassLoader() {
        return call(myClass(), "getClassLoader", NO_ARGS);
    }
}
