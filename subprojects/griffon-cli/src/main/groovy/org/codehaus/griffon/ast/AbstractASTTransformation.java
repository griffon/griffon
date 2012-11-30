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

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.objectweb.asm.Opcodes;

import java.util.Collections;

/**
 * Base class for all of Griffon's ASTTransformation implementations.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public abstract class AbstractASTTransformation implements ASTTransformation, Opcodes {
    private static final ClassNode APPLICATION_HOLDER_TYPE = makeClassSafe("griffon.util.ApplicationHolder");
    private static final ClassNode COLLECTIONS_CLASS = makeClassSafe(Collections.class);

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.getLineNumber();
        int col = expr.getColumnNumber();
        source.getErrorCollector().addErrorAndContinue(
            new SyntaxErrorMessage(new SyntaxException(msg + '\n', line, col), source)
        );
    }

    protected void checkNodesForAnnotationAndType(ASTNode node1, ASTNode node2) {
        if (!(node1 instanceof AnnotationNode) || !(node2 instanceof ClassNode)) {
            throw new RuntimeException("Internal error: wrong types: " + node1.getClass() + " / " + node2.getClass());
        }
    }

    public static Expression emptyMap() {
        return new StaticMethodCallExpression
            (COLLECTIONS_CLASS, "emptyMap", ArgumentListExpression.EMPTY_ARGUMENTS);
    }

    public static Expression applicationInstance() {
        return new StaticMethodCallExpression(APPLICATION_HOLDER_TYPE, "getApplication", ArgumentListExpression.EMPTY_ARGUMENTS);
    }

    protected static ClassNode newClass(ClassNode classNode) {
        return classNode.getPlainNodeReference();
    }

    public static ClassNode makeClassSafe(String className) {
        return makeClassSafe(ClassHelper.makeWithoutCaching(className));
    }

    public static ClassNode makeClassSafe(Class klass) {
        return makeClassSafe(ClassHelper.makeWithoutCaching(klass));
    }

    public static ClassNode makeClassSafe(ClassNode classNode) {
        return classNode.getPlainNodeReference();
    }
}
